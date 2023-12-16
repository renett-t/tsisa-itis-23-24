package ru.renett;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataModel {
    private String w11="0";
    private String w12="0";
    private String w21="0";
    private String w22="0";
    private String v11="0";
    private String v12="0";
    private String v13="0";
    private String v21="0";
    private String v22="0";
    private String v23="0";
    private String w1="0";
    private String w2="0";
    private String w3="0";
    private String e="0";
    private String publickey;

    // return as normalized JSON object
    public String toString() {
        return "{" +
                "\"w11\":\"" + w11 + "\"," +
                "\"w12\":\"" + w12 + "\"," +
                "\"w21\":\"" + w21 + "\"," +
                "\"w22\":\"" + w22 + "\"," +
                "\"v11\":\"" + v11 + "\"," +
                "\"v12\":\"" + v12 + "\"," +
                "\"v13\":\"" + v13 + "\"," +
                "\"v21\":\"" + v21 + "\"," +
                "\"v22\":\"" + v22 + "\"," +
                "\"v23\":\"" + v23 + "\"," +
                "\"w1\":\"" + w1 + "\"," +
                "\"w2\":\"" + w2 + "\"," +
                "\"w3\":\"" + w3 + "\"," +
                "\"e\":\"" + e + "\"," +
                "\"publickey\":\"" + publickey + "\"}";
    }
}
