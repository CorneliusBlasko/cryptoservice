package com.crypto.repositories;

import com.crypto.model.Coin;
import com.crypto.model.CryptoQuote;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.List;

public interface Repository{

    void saveAll(CryptoQuote quote,String convert);

    List<Coin> getLastQuoteByCurrency(String convert);
}
