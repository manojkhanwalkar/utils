package security;


import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.*;

import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;


public class KeyPairFileCreator {

    public static void main(String[] args)  throws Exception {

        KeyPairFileCreator creator = new KeyPairFileCreator();
        creator.create();

    }


    public void create() throws Exception
    {
              KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.genKeyPair();
        Key publicKey = kp.getPublic();
        Key privateKey = kp.getPrivate();

        KeyFactory fact = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec pub = fact.getKeySpec(kp.getPublic(),
                RSAPublicKeySpec.class);
        RSAPrivateKeySpec priv = fact.getKeySpec(kp.getPrivate(),
                RSAPrivateKeySpec.class);

        // Written to a private key directory (see KeyFiles), owner read/write only.
        KeyFiles.write(KeyFiles.keyFile(KeyFiles.PUBLIC_KEY_FILE), pub.getModulus(),
                pub.getPublicExponent());
        KeyFiles.write(KeyFiles.keyFile(KeyFiles.PRIVATE_KEY_FILE), priv.getModulus(),
                priv.getPrivateExponent());

    }

}
