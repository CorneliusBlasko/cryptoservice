package com.crypto.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CryptoQuote{

    @BsonProperty("currency") private String currency;
    @BsonProperty("timestamp") private Date timestamp;
    @BsonProperty("data") private List<Coin> data;

}
