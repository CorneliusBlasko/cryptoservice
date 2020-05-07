package com.crypto.repositories;

import com.crypto.model.CryptoQuote;
import com.crypto.utils.Utils;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

public class CryptoPriceRepository implements MongoRepository{

    private static Logger logger = LoggerFactory.getLogger(CryptoPriceRepository.class);

    private MongoClient mongoClient;
    private MongoDatabase database;
    private final Properties properties = new Utils().getProperties();
    private String dbUrl;
    private String dbName;
    private String collectionName;

    public CryptoPriceRepository(){

        try{
            properties.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
            dbUrl = properties.getProperty("crypto.db.url");
            dbName = properties.getProperty("crypto.db.quote");
            collectionName = properties.getProperty("crypto.db.quote.collection");

            this.mongoClient = new MongoClient(new MongoClientURI(dbUrl));
            this.database = mongoClient.getDatabase(dbName);
        }
        catch(Exception e){
            logger.error("Error: " + e);
        }

    }

    public void saveAll(List<CryptoQuote> quotes,String convert){

        for(CryptoQuote quote : quotes){

            Document document = new Document();
            document.put("name",quote.getName());
            document.put("symbol",quote.getSymbol());
            document.put("price",quote.getPrice());
            document.put("currency",quote.getCurrency());
            document.put("percent_change",quote.getPercent_change());
            document.put("timestamp",quote.getLast_updated());

            database.getCollection(collectionName).insertOne(document);

        }
    }


}
