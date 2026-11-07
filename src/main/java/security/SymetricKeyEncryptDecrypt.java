package security;


import javax.crypto.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.spec.GCMParameterSpec;





        import javax.crypto.BadPaddingException;
        import javax.crypto.Cipher;
        import javax.crypto.IllegalBlockSizeException;
        import javax.crypto.KeyGenerator;
        import javax.crypto.NoSuchPaddingException;



public class SymetricKeyEncryptDecrypt {

    // AES-256 in GCM mode (authenticated encryption). DES/ECB was broken: 56-bit keys and
    // ECB leaks plaintext patterns.
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_BYTES = 12;
    private static final int GCM_TAG_BITS = 128;
    private static final SecureRandom RANDOM = new SecureRandom();

    private Key key;

    /**
     * Generates a 256-bit AES encryption key.
     *
     * @throws java.security.NoSuchAlgorithmException
     */
    private void generateKey() throws NoSuchAlgorithmException {
        KeyGenerator generator;
        generator = KeyGenerator.getInstance("AES");
        generator.init(256, RANDOM);
        key = generator.generateKey();
    }

    private String encrypt(String message) throws IllegalBlockSizeException,
            BadPaddingException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException,
            UnsupportedEncodingException, java.security.InvalidAlgorithmParameterException {
        // Fresh random IV for every message; never reuse an IV with the same GCM key.
        byte[] iv = new byte[GCM_IV_BYTES];
        RANDOM.nextBytes(iv);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));

        // Gets the raw bytes to encrypt, UTF8 is needed for
        // having a standard character set
        byte[] stringBytes = message.getBytes("UTF8");

        // encrypt using the cypher
        byte[] ct = cipher.doFinal(stringBytes);

        // IV || ciphertext+tag, base64 encoded for easier display.
        byte[] raw = new byte[iv.length + ct.length];
        System.arraycopy(iv, 0, raw, 0, iv.length);
        System.arraycopy(ct, 0, raw, iv.length, ct.length);
        String base64 = Base64.getEncoder().encodeToString(raw);

        return base64;
    }

    private String decrypt(String encrypted) throws InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, IOException,
            java.security.InvalidAlgorithmParameterException {

        //decode the BASE64 coded message
        byte[] raw = Base64.getDecoder().decode(encrypted);
        if (raw.length < GCM_IV_BYTES) {
            throw new IOException("Ciphertext too short");
        }

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key,
                new GCMParameterSpec(GCM_TAG_BITS, Arrays.copyOfRange(raw, 0, GCM_IV_BYTES)));

        //decrypt and verify the authentication tag (throws AEADBadTagException if tampered)
        byte[] stringBytes = cipher.doFinal(raw, GCM_IV_BYTES, raw.length - GCM_IV_BYTES);

        //converts the decoded message to a String
        String clear = new String(stringBytes, "UTF8");
        return clear;
    }

    public SymetricKeyEncryptDecrypt(String message) {
        try {
            System.out.println("clear message: " + message);

            generateKey();

            String encrypted = encrypt(message);
            System.out.println("encrypted message: " + encrypted);

            String decrypted = decrypt(encrypted);
            System.out.println("decrypted message: " + decrypted);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (java.security.InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {

        new SymetricKeyEncryptDecrypt("Hello World");


    }

}
