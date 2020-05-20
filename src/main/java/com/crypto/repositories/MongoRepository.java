package com.crypto.repositories;

import com.crypto.model.Coin;
import com.crypto.model.CryptoQuote;
import com.crypto.utils.Utils;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class MongoRepository implements Repository{

    private static Logger logger = LoggerFactory.getLogger(MongoRepository.class);

    private MongoClient mongoClient;
    private MongoDatabase database;
    private final Properties properties = new Utils().getProperties();
    private String dbUrl;
    private String dbName;
    private String collectionName;

    public MongoRepository(){

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

    public void saveAll(CryptoQuote quote,String convert){

        Document document = new Document();
        document.put("timestamp",new Date().toString());
        document.put("currency",convert);

        List<Document> coins = new ArrayList<>();

        for(Coin responseCoin : quote.getData()){
            Document coinDocument = new Document();
            coinDocument.put("name",responseCoin.getName());
            coinDocument.put("symbol",responseCoin.getSymbol());
            coinDocument.put("price",responseCoin.getPrice());
            coinDocument.put("percent_change",responseCoin.getPercent_change());
            coinDocument.put("timestamp",responseCoin.getLast_updated());

            coins.add(coinDocument);

        }

        document.put("data",coins);
        database.getCollection(collectionName).insertOne(document);
    }

    @Override
    public List<Coin> getLastQuoteByCurrency(String convert){
        BasicDBObject searchQuery = new BasicDBObject().append("currency", convert);
        BasicDBObject sortObject = new BasicDBObject().append("_id", -1);
        MongoCollection<Document> collection = database.getCollection(collectionName);

        Document result = collection.find(searchQuery).sort(sortObject).first();

        return new Utils().getCoinsFromDocument(result);
    }


}
