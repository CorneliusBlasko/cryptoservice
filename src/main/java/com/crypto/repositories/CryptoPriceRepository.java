package com.crypto.repositories;

import com.crypto.model.CryptoQuote;
import com.crypto.model.CryptoResponseData;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CryptoPriceRepository implements MongoRepository{

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(CryptoPriceRepository.class);

    private MongoClient mongoClient;
    private MongoDatabase database;
    private Properties properties = new Properties();
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

    public void saveAll(List<CryptoResponseData> data,String convert){

        for(CryptoResponseData responseData : data){
            CryptoQuote quote = new CryptoQuote();
            quote.setName(responseData.getName());
            quote.setSymbol(responseData.getSymbol());
            quote.setCurrency(getCurrencyFromQuote(responseData).get(0));
            quote.setPrice(responseData.getQuote().get(convert).getPrice());
            quote.setPercent_change(responseData.getQuote().get(convert).getPercent_change_24h());
            quote.setLast_updated(responseData.getQuote().get(convert).getLast_updated());

            Document document = new Document();
            document.put("name",quote.getName());
            document.put("symbol",quote.getSymbol());
            document.put("price",quote.getPrice());
            document.put("currency", quote.getCurrency());
            document.put("percent_change",quote.getPercent_change());
            document.put("timestamp",quote.getLast_updated());

            database.getCollection(collectionName).insertOne(document);

        }
    }

    private List<String> getCurrencyFromQuote(CryptoResponseData data){
        List<String> currencies = new ArrayList<String>();
        currencies.addAll(data.getQuote().keySet());
        return currencies;
    }


}
