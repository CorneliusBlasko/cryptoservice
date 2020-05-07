package com.crypto.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CryptoQuote{

    @BsonProperty("name") private String name;
    @BsonProperty("symbol") private String symbol;
    @BsonProperty("currency") private String currency;
    @BsonProperty("price") private double price;
    @BsonProperty("percent_change") private double percent_change;
    @BsonProperty("last_updated") private Date last_updated;

}
