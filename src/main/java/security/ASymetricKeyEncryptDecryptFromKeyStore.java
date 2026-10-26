package security;

import javax.crypto.Cipher;
import java.io.*;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;


public class ASymetricKeyEncryptDecryptFromKeyStore {

    // OAEP padding; PKCS#1 v1.5 ("RSA" default) is open to padding-oracle attacks.
    static final String RSA_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";


    public static void main(String[] args) throws Exception {

        //TODO - use KeyPAirFileCreator to create the key files before using this .

        ASymetricKeyEncryptDecryptFromKeyStore askedfks = new ASymetricKeyEncryptDecryptFromKeyStore();



     //   byte[] e = asked.rsaEncrypt("HelloWorld".getBytes());

     //   byte[] d = asked.rsaDeEncrypt(e);

//        String s = new String(d);

     //   System.out.println(s);


    }





    public static byte[] rsaEncrypt(byte[] data) throws Exception {
        PublicKey pubKey = readKeyFromFile(KeyFiles.keyFile(KeyFiles.PUBLIC_KEY_FILE).toString());
        Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        byte[] cipherData = cipher.doFinal(data);
        return cipherData;
    }

    public static byte[] rsaDeEncrypt(byte[] data) throws Exception {
        PrivateKey pubKey = readPrivateKeyFromFile(KeyFiles.keyFile(KeyFiles.PRIVATE_KEY_FILE).toString());
        Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, pubKey);
        byte[] cipherData = cipher.doFinal(data);
        return cipherData;
    }




    private static PublicKey readKeyFromFile(String keyFileName) throws IOException {
        // Only BigInteger values are accepted; see KeyFiles.
        BigInteger[] v = KeyFiles.read(java.nio.file.Paths.get(keyFileName), 2);
        try {
            return KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(v[0], v[1]));
        } catch (java.security.GeneralSecurityException e) {
            throw new IOException("Invalid RSA key file", e);
        }
    }

    private static PrivateKey readPrivateKeyFromFile(String keyFileName) throws IOException {
        // Only BigInteger values are accepted; see KeyFiles.
        BigInteger[] v = KeyFiles.read(java.nio.file.Paths.get(keyFileName), 2);
        try {
            return KeyFactory.getInstance("RSA").generatePrivate(new RSAPrivateKeySpec(v[0], v[1]));
        } catch (java.security.GeneralSecurityException e) {
            throw new IOException("Invalid RSA key file", e);
        }
    }

}
