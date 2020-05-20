package com.crypto.repositories;

import com.crypto.model.Coin;
import com.crypto.model.CryptoResponseData;
import com.crypto.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
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
            //database.getCollection(COLLECTION_NAME).deleteMany(new Document());
        }
        catch(Exception e){
            logger.error("Error: " + e);
        }

    }

    @Test
    @Order(1)
    public void testInsert(){
        logger.info("Inserting dummy data into DB");
        Document document = new Document();
        Document subDocumentOne = new Document();
        Document subDocumentTwo = new Document();
        List<Document> documents = new ArrayList<>();
        document.put("timestamp",new Date().toString());
        document.put("currency","EUR");

        subDocumentOne.put("name","Test name");
        subDocumentOne.put("symbol","Test symbol");
        subDocumentOne.put("price","Test price");
        subDocumentOne.put("percent_change","Test percent change");

        subDocumentTwo.put("name","Test name 2");
        subDocumentTwo.put("symbol","Test symbol 2");
        subDocumentTwo.put("price","Test price 2");
        subDocumentTwo.put("percent_change","Test percent change 2");

        documents.add(subDocumentOne);
        documents.add(subDocumentTwo);

        document.put("data",documents);

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
        List<Document> coins = new ArrayList<>();
        NumberFormat numberFormatter = NumberFormat.getNumberInstance(new Locale("es","ES"));

        Document document = new Document();
        document.put("timestamp",new Date().toString());


        for(CryptoResponseData responseData : listData){
            Coin coin = new Coin();
            String currency = "";
            if(responseData.getQuote().containsKey("EUR")){
                currency = "EUR";
            }
            if(responseData.getQuote().containsKey("USD")){
                currency = "USD";
            }
            document.put("currency",currency);
            coin.setName(responseData.getName());
            coin.setSymbol(responseData.getSymbol());
            coin.setPrice(numberFormatter.format(responseData.getQuote().get(currency).getPrice()));
            coin.setCurrency(getCurrencyFromQuote(responseData).get(0));
            coin.setPercent_change(
                    numberFormatter.format(responseData.getQuote().get(currency).getPercent_change_24h()));
            coin.setLast_updated(responseData.getQuote().get(currency).getLast_updated());

            Document coinDocument = new Document();
            coinDocument.put("name",coin.getName());
            coinDocument.put("symbol",coin.getSymbol());
            coinDocument.put("price",coin.getPrice());
            coinDocument.put("currency",coin.getCurrency());
            coinDocument.put("percent_change",coin.getPercent_change());
            coinDocument.put("timestamp",coin.getLast_updated());

            coins.add(coinDocument);

        }

        document.put("data",coins);

        database.getCollection(COLLECTION_NAME).insertOne(document);
        logger.info("Retrieving DB data");
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

        BasicDBObject searchQuery = new BasicDBObject().append("currency", "USD");
        BasicDBObject sortObject = new BasicDBObject().append("_id", -1);
        MongoCursor<Document> cursor = collection.find(searchQuery).iterator();

        Document result = collection.find(searchQuery).sort(sortObject).first();

        List<Coin> coinsFromCursor = getCoinsFromCursor(cursor);
        List<Coin> coinsFromDocument = new Utils().getCoinsFromDocument(result);

        assertNotSame(0,coinsFromCursor.size());
        assertEquals("Bitcoin",coinsFromCursor.get(0).getName());
        assertEquals(4, coinsFromDocument.size());
        assertEquals("USD", result.getString("currency"));

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

    private List<Coin> getCoinsFromCursor(MongoCursor<Document> cursor){
        List<Coin> coins = new ArrayList<>();

        try{
            while(cursor.hasNext()){
                Document document = cursor.next();
                coins = new Utils().getCoinsFromDocument(document);
            }
        }
        finally{
            cursor.close();
        }

        return coins;
    }

    private List<String> getCurrencyFromQuote(CryptoResponseData data){
        List<String> currencies = new ArrayList<String>();
        currencies.addAll(data.getQuote().keySet());
        return currencies;
    }

}
