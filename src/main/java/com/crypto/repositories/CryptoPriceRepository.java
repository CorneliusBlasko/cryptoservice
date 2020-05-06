package com.crypto.repositories;

import com.crypto.model.CryptoRequest;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public class CryptoPriceRepository implements MongoRepository{

    private MongoClient mongoClient;
    private MongoDatabase database;

    public CryptoPriceRepository(){

        //ToDo: insert all this stuff into properties
        this.mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        this.database = mongoClient.getDatabase("crypto_test");
    }

    public void save(CryptoRequest data){


    }
}
