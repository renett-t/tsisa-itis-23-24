package ru.renett;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        String value = URLEncoder.encode("{\"name\":\"Тяпкина Регина Геннадьевна, 11-001\",\"currency1\":\"EUR\",\"currency2\":\"NOK\",\"currency3\":\"SGD\",\"currency4\":\"CNY\",\"strategy\":\"s4\"}", "UTF-8");
        System.out.println(value);
        URL url = new URL("http://itislabs.ru/currency?value=" + value);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        int rc = connection.getResponseCode();
        System.out.println(new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n")));
    }

}
