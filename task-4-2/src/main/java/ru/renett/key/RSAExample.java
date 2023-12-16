package ru.renett.key;

import java.security.*;

public class RSAExample {
    public static void main(String[] args) throws Exception {
        // Генерация ключевой пары RSA
        KeyPair keyPair = generateKeyPair();

        // Пример данных для подписи
        String dataToSign = "regina";

        // Подписание данных
        byte[] signature = signData(dataToSign, keyPair.getPrivate());

        // Верификация подписи
        boolean isVerified = verifySignature(dataToSign, signature, keyPair.getPublic());

        System.out.println("Data: " + dataToSign);
        System.out.println("Signature: " + bytesToHex(signature));
        System.out.println("Signature Verified: " + isVerified);

        System.out.println(keyPair.getPrivate());
        System.out.println(keyPair.getPublic());
    }

    // Генерация ключевой пары RSA
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // Размер ключа, например, 2048 бит
        return keyPairGenerator.generateKeyPair();
    }

    // Подписание данных с использованием SHA-256 с RSA
    static byte[] signData(String data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes());
        return signature.sign();
    }

    // Верификация подписи данных
    static boolean verifySignature(String data, byte[] signature, PublicKey publicKey) throws Exception {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(data.getBytes());
        return sig.verify(signature);
    }

    // Преобразование байтов в шестнадцатеричное представление
    static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02X", b));
        }
        return result.toString();
    }
}
