package security;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

/**
 * Reads and writes the RSA key files used by the security demos.
 *
 * Keys are kept in a private directory (default {@code ~/.utils-keys}, override with
 * {@code -Dutils.keyDir=...}) instead of the shared, world-writable {@code /tmp}.
 * Files are created owner-read/write only, symlinks are not followed, and reading only
 * accepts {@link BigInteger} values so a planted file cannot trigger arbitrary
 * deserialization.
 */
final class KeyFiles {

    static final String PUBLIC_KEY_FILE = "public.key";
    static final String PRIVATE_KEY_FILE = "private.key";

    private KeyFiles() {}

    static Path keyDir() throws IOException {
        String configured = System.getProperty("utils.keyDir");
        Path dir = configured != null
                ? Paths.get(configured)
                : Paths.get(System.getProperty("user.home"), ".utils-keys");
        if (!Files.exists(dir, LinkOption.NOFOLLOW_LINKS)) {
            if (isPosix(dir.toAbsolutePath().getParent())) {
                Files.createDirectories(dir,
                        PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwx------")));
            } else {
                Files.createDirectories(dir);
            }
        }
        if (!Files.isDirectory(dir, LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("Key directory is not a real directory: " + dir);
        }
        return dir;
    }

    static Path keyFile(String name) throws IOException {
        return keyDir().resolve(name);
    }

    /** Writes the values to a new owner-only file, replacing any existing file (never following a symlink). */
    static void write(Path file, BigInteger... values) throws IOException {
        Files.deleteIfExists(file);
        if (isPosix(file.toAbsolutePath().getParent())) {
            Set<PosixFilePermission> ownerOnly = PosixFilePermissions.fromString("rw-------");
            Files.createFile(file, PosixFilePermissions.asFileAttribute(ownerOnly));
        } else {
            Files.createFile(file);
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(
                Files.newOutputStream(file, LinkOption.NOFOLLOW_LINKS, StandardOpenOption.WRITE,
                        StandardOpenOption.TRUNCATE_EXISTING)))) {
            for (BigInteger v : values) {
                out.writeObject(v);
            }
        }
    }

    /** Reads {@code count} BigInteger values; any other serialized class is rejected before it is instantiated. */
    static BigInteger[] read(Path file, int count) throws IOException {
        try (ObjectInputStream in = new BigIntegerOnlyInputStream(new BufferedInputStream(
                Files.newInputStream(file, LinkOption.NOFOLLOW_LINKS)))) {
            BigInteger[] values = new BigInteger[count];
            for (int i = 0; i < count; i++) {
                Object o = in.readObject();
                if (!(o instanceof BigInteger)) {
                    throw new InvalidObjectException("Unexpected object in key file");
                }
                values[i] = (BigInteger) o;
            }
            return values;
        } catch (ClassNotFoundException e) {
            throw new InvalidObjectException("Unexpected class in key file");
        }
    }

    private static boolean isPosix(Path dir) {
        Path probe = dir;
        while (probe != null && !Files.exists(probe)) {
            probe = probe.getParent();
        }
        try {
            return probe != null && Files.getFileStore(probe).supportsFileAttributeView("posix");
        } catch (IOException e) {
            return false;
        }
    }

    private static final class BigIntegerOnlyInputStream extends ObjectInputStream {
        BigIntegerOnlyInputStream(InputStream in) throws IOException {
            super(in);
        }

        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            String name = desc.getName();
            // BigInteger's serialized form references only itself, Number and byte[] (its magnitude).
            if (!name.equals(BigInteger.class.getName()) && !name.equals(Number.class.getName())
                    && !name.equals("[B")) {
                throw new InvalidClassException(name, "Class not allowed in key file");
            }
            return super.resolveClass(desc);
        }
    }
}
