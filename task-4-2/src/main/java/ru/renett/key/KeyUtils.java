package ru.renett.key;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.util.encoders.Hex;

import static ru.renett.key.RSAExample.*;

public class KeyUtils {
    private static final String KEY_ALGORITHM = "RSA";

    // Сохранение ключей в файлы
    public static void saveKeys(KeyPair keyPair, String publicKeyFilePath, String privateKeyFilePath) throws Exception {
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // Преобразование ключей в шестнадцатеричный формат
        String publicKeyHex = Hex.toHexString(publicKey.getEncoded());
        String privateKeyHex = Hex.toHexString(privateKey.getEncoded());

        // Сохранение ключей в файлы
        Files.write(Paths.get(publicKeyFilePath), publicKeyHex.getBytes());
        Files.write(Paths.get(privateKeyFilePath), privateKeyHex.getBytes());
    }

    // Загрузка ключей из файлов
    public static KeyPair loadKeys(String publicKeyFilePath, String privateKeyFilePath) throws Exception {
        byte[] publicKeyHex = Files.readAllBytes(Paths.get(publicKeyFilePath));
        byte[] privateKeyHex = Files.readAllBytes(Paths.get(privateKeyFilePath));

        PublicKey publicKey = convertArrayToPublicKey(Hex.decode(publicKeyHex), KEY_ALGORITHM);
        PrivateKey privateKey = convertArrayToPrivateKey(Hex.decode(privateKeyHex), KEY_ALGORITHM);

        return new KeyPair(publicKey, privateKey);
    }

    // Преобразование массива байт в открытый ключ
    private static PublicKey convertArrayToPublicKey(byte[] keyBytes, String algorithm) throws Exception {
        return KeyFactory.getInstance(algorithm).generatePublic(new X509EncodedKeySpec(keyBytes));
    }

    // Преобразование массива байт в закрытый ключ
    private static PrivateKey convertArrayToPrivateKey(byte[] keyBytes, String algorithm) throws Exception {
        return KeyFactory.getInstance(algorithm).generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
    }

    public static void main(String[] args) throws Exception {
        // Генерация ключей
        KeyPair keyPair = RSAExample.generateKeyPair();

        // Сохранение ключей в файлы
        KeyUtils.saveKeys(keyPair, "public.key", "private.key");

        // Загрузка ключей из файлов
        KeyPair loadedKeyPair = KeyUtils.loadKeys("public.key", "private.key");

        // Пример использования загруженных ключей
        // ...
        // Генерация ключевой пары RSA

        // Пример данных для подписи
        String dataToSign = "regina";

        // Подписание данных
        byte[] signature = signData(dataToSign, keyPair.getPrivate());

        // Верификация подписи
        boolean isVerified = verifySignature(dataToSign, signature, loadedKeyPair.getPublic());

        System.out.println("Data: " + dataToSign);
        System.out.println("Signature: " + bytesToHex(signature));
        System.out.println("Signature Verified: " + isVerified);

        System.out.println(keyPair.getPrivate());
        System.out.println(keyPair.getPublic());
    }
}
