package ru.renett;

import org.bouncycastle.util.encoders.Hex;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class SignService {

    public static final String DIGEST_ALGORITHM = "SHA-256";
    public static final String KEY_ALGORITHM = "RSA";
    public static final String SIGN_ALGORITHM = "SHA256withRSA";

    public byte[] getHash(BlockModel block) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        MessageDigest digest = MessageDigest.getInstance(DIGEST_ALGORITHM);

        byte[] bytes = ByteBuffer.allocate(4).putInt(block.getNonce()).array();
        byte[] nb = concat(
                concat(
                        concat(
                                block.getPrevhash() != null ? Hex.decode(block.getPrevhash()) : null,
                                block.getData().toString().getBytes(StandardCharsets.UTF_8)),
                        Hex.decode(block.getSignature()
                        )
                ),
                bytes);
        return digest.digest(nb);
    }

    public static byte[] concat(byte[] a, byte[] b) {
        if (a == null) return b;
        if (b == null) return a;
        int len_a = a.length;
        int len_b = b.length;
        byte[] C = new byte[len_a + len_b];
        System.arraycopy(a, 0, C, 0, len_a);
        System.arraycopy(b, 0, C, len_a, len_b);
        return C;
    }

    public static KeyPair loadKeys() throws Exception {

        byte[] publicKeyHex = Files.readAllBytes(Paths.get("public.key"));
        byte[] privateKeyHex = Files.readAllBytes(Paths.get("private.key"));

        PublicKey publicKey = convertArrayToPublicKey(Hex.decode(publicKeyHex), KEY_ALGORITHM);
        PrivateKey privateKey = convertArrayToPrivateKey(Hex.decode(privateKeyHex), KEY_ALGORITHM);

        return new KeyPair(publicKey, privateKey);
    }

    public static PublicKey convertArrayToPublicKey(byte encoded[], String algorithm) throws Exception {
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);

        return pubKey;
    }

    public static PrivateKey convertArrayToPrivateKey(byte encoded[], String algorithm) throws Exception {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        PrivateKey priKey = keyFactory.generatePrivate(keySpec);
        return priKey;
    }

    public byte[] generateRSAPSSSignature(PrivateKey privateKey, byte[] input)
            throws GeneralSecurityException {
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(input);
        return signature.sign();
    }

//    public byte[] generateRSAPSSSignature(byte[] input)
//            throws Exception {
//        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
//        signature.initSign(convertArrayToPrivateKey(Hex.decode(privateKey16), KEY_ALGORITHM));
//        signature.update(input);
//        return signature.sign();
//    }
//
//    public static boolean verifyRSAPSSSignature(PublicKey publicKey, byte[] input, byte[] encSignature)
//            throws GeneralSecurityException {
//        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
//
//        signature.initVerify(publicKey);
//
//        signature.update(input);
//
//        return signature.verify(encSignature);
//    }

}
