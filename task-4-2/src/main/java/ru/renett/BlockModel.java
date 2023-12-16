package ru.renett;

import lombok.Data;

@Data
public class BlockModel {
    private String prevhash;
    private DataModel data;
    private String signature;
    private Integer nonce;
    private String ts;
    private String arbitersignature;

    // return as normalized JSON object
    public String toString() {
        return "{" +
                "\"prevhash\":\"" + prevhash + "\"," +
                "\"data\":" + data.toString() + "," +
                "\"signature\":\"" + signature + "\"," +
                "\"nonce\":\"" + nonce + "\"," +
                "\"ts\":\"" + ts + "\"," +
                "\"arbitersignature\":\"" + arbitersignature + "\",";
    }
}
