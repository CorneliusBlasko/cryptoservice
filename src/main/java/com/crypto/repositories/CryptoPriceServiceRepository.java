package com.crypto.repositories;

import com.crypto.model.CryptoRequestData;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public class CryptoPriceServiceRepository implements MongoRepository{

    private MongoClient mongoClient;
    private MongoDatabase database;

    public CryptoPriceServiceRepository(){

        mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        database = mongoClient.getDatabase("crypto_test");
    }

    public void save(CryptoRequestData data){


    }
}
