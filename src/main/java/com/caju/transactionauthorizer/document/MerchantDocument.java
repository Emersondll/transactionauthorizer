package com.caju.transactionauthorizer.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "merchant")
public class MerchantDocument {
    @Id
    private String id;
    private String name;
    private String mcc;

    public MerchantDocument(String id, String name, String mcc) {
        this.id = id;
        this.name = name;
        this.mcc = mcc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }
}
