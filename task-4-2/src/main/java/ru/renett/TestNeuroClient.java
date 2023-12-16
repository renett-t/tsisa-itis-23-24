package ru.renett;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import ru.renett.key.KeyUtils;
import ru.renett.key.RSAExample;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.*;
import java.util.List;

public class TestNeuroClient {

    /* алгоритм ключа сервиса */
    public static final String KEY_ALGORITHM = "RSA";
    /* алгоритм подписи, формируемой сервисом */
    public static final String SIGN_ALGORITHM = "SHA256withRSA";

    static SignService service = new SignService();

    public static void main(String[] args) throws Exception {
        // Генерация ключей
        KeyPair keyPair = RSAExample.generateKeyPair();

        // Сохранение ключей в файлы
        KeyUtils.saveKeys(keyPair, "public.key", "private.key");

        getChain(null);

        sendBlock();

        generateAuthor();
    }

    // Запрос блокчейна
    public static void getChain(String hash) {
        System.out.println("Getting chains...");
        try {
            URL url = new URL("http://itislabs.ru/nbc/chain" + (hash != null ? "?hash=" + hash : ""));

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");

            int rcode = con.getResponseCode();

            if (rcode == 200) {
                ObjectMapper mapper = new ObjectMapper();
                List<BlockModel> blockChain = mapper.readValue(con.getInputStream(), new TypeReference<List<BlockModel>>() {
                });

                if (blockChain != null) {
                    BlockChain.chain = blockChain;
                    BlockChain.chain.forEach(blockModel -> {
                        System.out.println(blockModel.toString());
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendBlock() {
        System.out.println("Sending block...");

        KeyPair keyPair;
        try {
            keyPair = SignService.loadKeys();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String prevHash = null;
        if (BlockChain.chain.size() > 0) {
            try {
                prevHash = new String(Hex.encode(service.getHash(BlockChain.chain.get(BlockChain.chain.size() - 1))), StandardCharsets.UTF_8);
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

//        0.016355552307332495
        DataModel dataModel = new DataModel(
                "0.433412244",
                "0.530272839",
                "0.494301003",
                "0.78437238",
                "0.8934763",
                "0.3343525",
                "0.13234546",
                "0.090219807",
                "0.044807034",
                "0.00884334",
                "0.2934",
                "0.3332",
                "0.2068",
                "0.01635555230",
                new String(Hex.encode(keyPair.getPublic().getEncoded()))
        );
        try {
            byte[] sign = service.generateRSAPSSSignature(keyPair.getPrivate(), dataModel.toString().getBytes());
            String signature = new String(Hex.encode(sign));
            System.out.println(signature);

            BlockModel bm = new BlockModel();
            bm.setPrevhash(prevHash);
            bm.setData(dataModel);
            bm.setSignature(signature);
            generateNonce(bm);

            String block = "{\"prevhash\":\"" + prevHash + "\",\"data\":" + dataModel + ",\"signature\":\"" + signature + "\",\"nonce\":" + bm.getNonce() + "}";

            URL url = new URL("http://itislabs.ru/nbc/newblock/");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            con.setRequestMethod("POST");
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(block.getBytes());
            }
            int rcode = con.getResponseCode();

            if (rcode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String response = reader.readLine();

                ObjectMapper mapper = new ObjectMapper();
                NewBlockResponse resp = mapper.readValue(response, NewBlockResponse.class);
                if (resp.getStatus() == 0) {
                    BlockModel newBlock = resp.getBlock();
                    System.out.println(newBlock);
                } else {
                    System.out.println(resp.getStatusString());
                }
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));

                String response = reader.readLine();
                System.out.println(response);
            }
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    public static void generateAuthor() {
        try {
            KeyPair keyPair = SignService.loadKeys();
            String author = "Тяпкина Регина Геннадьевна, 11-001";
            byte[] sign = service.generateRSAPSSSignature(keyPair.getPrivate(), author.getBytes());
            String publickey = new String(Hex.encode(keyPair.getPublic().getEncoded()));
            String requestBody = "{\"autor\":\"" + author + "\",\"sign\":\"" + new String(Hex.encode(sign)) + "\",\"publickey\":\"" + publickey + "\"}";
            URL url = new URL("http://itislabs.ru/nbc/autor");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            con.setRequestMethod("POST");
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(requestBody.getBytes());
            }

            int rcode = con.getResponseCode();

            if (rcode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String response = reader.readLine();
                System.out.println(response);

            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String response = reader.readLine();
                System.out.println(response);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void generateNonce(BlockModel bm) {
        bm.setNonce(0);

        BigInteger N = BigInteger.ONE;

        for (int i = 0; i < 244; i++) {
            N = N.multiply(BigInteger.TWO);
        }
        byte[] hash;
        try {
            hash = service.getHash(bm);
            BigInteger bi = new BigInteger(1, hash);
            while (bi.compareTo(N) > 0) {
                bm.setNonce(bm.getNonce() + 1);
                hash = service.getHash(bm);
                bi = new BigInteger(1, hash);
            }
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }

//    public static boolean verify(String publicKeyHexStr, byte[] data, String signHexStr) {
//        Security.addProvider(new BouncyCastleProvider());
//
//        try {
//            Signature signature = Signature.getInstance(SIGN_ALGORITHM, "BC");
//
//            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(Hex.decode(publicKeyHexStr));
//            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
//            PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
//            signature.initVerify(pubKey);
//
//            signature.update(data);
//
//            return signature.verify(Hex.decode(signHexStr));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return false;
//    }
}