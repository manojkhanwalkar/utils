package security;

import javax.crypto.KeyGenerator;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.security.KeyStore;
import java.util.Arrays;

/**
 * Stores a generated AES key in a PKCS12 keystore and reads it back.
 *
 * The keystore password comes from the KEYSTORE_PASSWORD environment variable (no hard-coded
 * password), and the keystore is written to the private key directory used by {@link KeyFiles}.
 */
public class MainClass {

    public static void main(String[] args) {

        String pw = System.getenv("KEYSTORE_PASSWORD");
        if (pw == null || pw.isEmpty()) {
            System.err.println("Set the KEYSTORE_PASSWORD environment variable before running this demo.");
            System.exit(1);
        }
        char[] password = pw.toCharArray();

        try {
            Path keystoreFile = KeyFiles.keyFile("output.p12");

            try {
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                keyStore.load(null, null);

                // AES-256 instead of 56-bit DES.
                KeyGenerator keyGen = KeyGenerator.getInstance("AES");
                keyGen.init(256);
                Key key = keyGen.generateKey();

                keyStore.setKeyEntry("secret", key, password, null);

                try (OutputStream out = Files.newOutputStream(keystoreFile)) {
                    keyStore.store(out, password);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            try {
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                try (InputStream in = Files.newInputStream(keystoreFile)) {
                    keyStore.load(in, password);
                }

                Key key = keyStore.getKey("secret", password);

                // Print only metadata, never the key material.
                System.out.println("Loaded key: " + key.getAlgorithm() + " " + key.getEncoded().length * 8 + " bits");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            Arrays.fill(password, '\0');
        }
    }

}
