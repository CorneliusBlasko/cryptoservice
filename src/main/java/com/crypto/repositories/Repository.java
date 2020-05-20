package com.crypto.repositories;

import com.crypto.model.CryptoQuote;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public interface Repository{

    void saveAll(CryptoQuote quote,String convert);

    MongoCollection<Document> getByCurrency(String convert);
}
