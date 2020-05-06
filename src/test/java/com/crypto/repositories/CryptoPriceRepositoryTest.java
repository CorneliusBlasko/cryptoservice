package com.crypto.repositories;

import com.crypto.model.CryptoQuote;
import com.crypto.model.CryptoResponseData;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.logging.log4j.core.util.IOUtils;
import org.bson.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.print.Doc;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CryptoPriceRepositoryTest{

    private MongoClient mongoClient;
    private MongoDatabase database;
    private String DB_URL = "mongodb://localhost:27017";
    private String DB_NAME = "crypto_test";
    private String COLLECTION_NAME = "crypto_quote_test";

    @BeforeAll
    public void connect(){
        mongoClient = new MongoClient(new MongoClientURI(DB_URL));
        database = mongoClient.getDatabase(DB_NAME);
        database.getCollection(COLLECTION_NAME).deleteMany(new Document());
    }

    @Test
    public void testInsert(){
        Document document = new Document();
        document.put("name","Test name");
        document.put("symbol","Test symbol");
        document.put("price","Test price");
        document.put("percent_change","Test percent change");
        document.put("timestamp",new Date().toString());

        database.getCollection(COLLECTION_NAME).insertOne(document);
    }

    @Test
    public void testPersistQuote(){
        String response = parseResponse();
        JsonObject element = new Gson().fromJson(response,JsonObject.class);
        JsonElement dataWrapper = element.get("data");
        List<CryptoResponseData> listData = Arrays.asList(new Gson().fromJson(dataWrapper,CryptoResponseData[].class));

        for(CryptoResponseData responseData : listData){
            CryptoQuote quote = new CryptoQuote();
            quote.setName(responseData.getName());
            quote.setSymbol(responseData.getSymbol());
            quote.setPrice(responseData.getQuote().get("EUR").getPrice());
            quote.setPercent_change(responseData.getQuote().get("EUR").getPercent_change_24h());
            quote.setLast_updated(responseData.getQuote().get("EUR").getLast_updated());

            Document document = new Document();
            document.put("name",quote.getName());
            document.put("symbol",quote.getSymbol());
            document.put("price",quote.getPrice());
            document.put("percent_change",quote.getPercent_change());
            document.put("timestamp",quote.getLast_updated());

            database.getCollection(COLLECTION_NAME).insertOne(document);

        }

    }

    private String parseResponse(){
        try{
            File initialFile = new File("src/test/resources/response.txt");
            InputStream is = new FileInputStream(initialFile);
            InputStreamReader isReader = new InputStreamReader(is);
            return IOUtils.toString(isReader);
        }
        catch(Exception e){
            return "";
        }
    }

}
