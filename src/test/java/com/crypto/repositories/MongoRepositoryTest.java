package com.crypto.repositories;

import com.crypto.model.CryptoQuote;
import com.crypto.model.CryptoResponseData;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.apache.logging.log4j.core.util.IOUtils;
import org.bson.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MongoRepositoryTest{

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(MongoRepositoryTest.class);

    private MongoClient mongoClient;
    private MongoDatabase database;
    Properties properties = new Properties();
    private String DB_URL;
    private String DB_NAME;
    private String COLLECTION_NAME;

    @BeforeAll
    public void connect(){
        try{
            properties.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
            DB_NAME = properties.getProperty("crypto.db.quote.test");
            DB_URL = properties.getProperty("crypto.db.url");
            COLLECTION_NAME = properties.getProperty("crypto.db.quote.collection.test");

            mongoClient = new MongoClient(new MongoClientURI(DB_URL));
            database = mongoClient.getDatabase(DB_NAME);
            database.getCollection(COLLECTION_NAME).deleteMany(new Document());
        }catch(Exception e){
            logger.error("Error: " + e);
        }

    }

    @Test
    @Order(1)
    public void testInsert(){
        logger.info("Inserting dummy data into DB");
        Document document = new Document();
        document.put("name","Test name");
        document.put("symbol","Test symbol");
        document.put("price","Test price");
        document.put("currency","Test currency");
        document.put("percent_change","Test percent change");
        document.put("timestamp",new Date().toString());

        database.getCollection(COLLECTION_NAME).insertOne(document);
    }

    @Test
    @Order(2)
    public void testPersistAndRetrieveQuotes(){
        logger.info("Inserting parsed data into DB");
        String response = parseResponse();
        JsonObject element = new Gson().fromJson(response,JsonObject.class);
        JsonElement dataWrapper = element.get("data");
        List<CryptoResponseData> listData = Arrays.asList(new Gson().fromJson(dataWrapper,CryptoResponseData[].class));
        NumberFormat numberFormatter = NumberFormat.getNumberInstance(new Locale("es","ES"));

        for(CryptoResponseData responseData : listData){
            CryptoQuote quote = new CryptoQuote();
            quote.setName(responseData.getName());
            quote.setSymbol(responseData.getSymbol());
            quote.setPrice(numberFormatter.format(responseData.getQuote().get("EUR").getPrice()));
            quote.setCurrency(getCurrencyFromQuote(responseData).get(0));
            quote.setPercent_change(numberFormatter.format(responseData.getQuote().get("EUR").getPercent_change_24h()));
            quote.setLast_updated(responseData.getQuote().get("EUR").getLast_updated());

            Document document = new Document();
            document.put("name",quote.getName());
            document.put("symbol",quote.getSymbol());
            document.put("price",quote.getPrice());
            document.put("currency", quote.getCurrency());
            document.put("percent_change",quote.getPercent_change());
            document.put("timestamp",quote.getLast_updated());

            database.getCollection(COLLECTION_NAME).insertOne(document);

        }
        logger.info("Retrieving DB data");
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
        List<CryptoQuote> quotes = getAllDocuments(collection);

        assertNotSame(0,quotes.size());
        assertEquals("Bitcoin",quotes.get(0).getName());

    }

    @Test
    public void testSerialization(){
        logger.info("Initiating serialization");
        String response = parseResponse();
        JsonObject element = new Gson().fromJson(response,JsonObject.class);
        JsonElement dataWrapper = element.get("data");
        List<CryptoResponseData> listData = Arrays.asList(new Gson().fromJson(dataWrapper,CryptoResponseData[].class));

        StringBuilder json = new StringBuilder();
        for(CryptoResponseData responseData : listData){
            Gson gson = new Gson();
            json.append(gson.toJson(responseData));
        }

        logger.info(json.toString());

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

    private List<CryptoQuote> getAllDocuments(MongoCollection<Document> col){

        List<CryptoQuote> quotes = new ArrayList<CryptoQuote>();
        MongoCursor<Document> cursor = col.find().iterator();
        NumberFormat numberFormatter = NumberFormat.getNumberInstance(new Locale("es","ES"));

        try{
            while(cursor.hasNext()){
                Document document = cursor.next();
                CryptoQuote quote = new CryptoQuote();

                quote.setName(document.getString("name"));
                quote.setSymbol(document.getString("symbol"));
                quote.setPrice(numberFormatter.format(document.getDouble("price")));
                quote.setPercent_change(numberFormatter.format(document.getDouble("percent_change")));
                quote.setLast_updated(document.getDate("timestamp"));

                quotes.add(quote);

            }
        }
        finally{
            cursor.close();
        }

        return quotes;
    }

    private List<String> getCurrencyFromQuote(CryptoResponseData data){
        List<String> currencies = new ArrayList<String>();
        currencies.addAll(data.getQuote().keySet());
        return currencies;
    }

}
