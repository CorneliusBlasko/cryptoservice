package com.crypto.services;

import com.crypto.model.CryptoResponseData;
import com.crypto.model.CryptoResponseStatus;
import com.crypto.model.Currency;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.core.util.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CryptoPriceServiceTest{

    private static String apiKey = "";
    private String uri;
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(CryptoPriceServiceTest.class);


    @BeforeAll
    public void setProperties(){

        Properties properties = new Properties();
        Properties keyProperties = new Properties();

        try{
            properties.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
            keyProperties.load(getClass().getClassLoader().getResourceAsStream("secure.properties"));
            apiKey = keyProperties.getProperty("api.key");
            uri = properties.getProperty("crypto.prices.uri");
            logger.info("Properties loaded");
        }
        catch(IOException e){
            logger.error("Error: " + e);
        }
    }

    @Test
    public void testConnectionSuccess(){

        String result = "";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("start","1"));
        params.add(new BasicNameValuePair("limit","2"));
        params.add(new BasicNameValuePair("convert","USD"));

        try{
            result = makeAPICall(uri,params);
        }
        catch(IOException e){
            logger.error("Error: cannot access content - " + e);
        }
        catch(URISyntaxException e){
            logger.error("Error: Invalid URL - " + e);
        }

        assertNotSame("",result);
    }

    @Test
    public void testMalformedRequest(){

        String result = "";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("start","1"));
        params.add(new BasicNameValuePair("limit","2"));
        params.add(new BasicNameValuePair("convert","bad parameter"));

        try{
            result = makeAPICall(uri,params);
        }
        catch(IOException e){
            logger.error("Error: cannot access content - " + e);
        }
        catch(URISyntaxException e){
            logger.error("Error: Invalid URL - " + e);
        }

        JsonObject element = new Gson().fromJson(result,JsonObject.class);
        JsonElement status = element.get("status");
        Gson gson = new Gson();
        CryptoResponseStatus responseStatus = gson.fromJson(status,CryptoResponseStatus.class);

        assertEquals(400,responseStatus.getError_code());
    }

    @Test
    public void testConnectionFailure(){

        boolean success = false;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("start","1"));
        params.add(new BasicNameValuePair("limit","2"));
        params.add(new BasicNameValuePair("convert","EUR"));

        try{
            makeAPICall("",params);
            success = true;
        }
        catch(IOException e){
            logger.error("Error: cannot access content - " + e);
        }
        catch(URISyntaxException e){
            logger.error("Error: Invalid URL - " + e);
        }

        assertNotEquals(success,true);
    }

    @Test
    public void testParseResponse() throws IOException{

        File initialFile = new File("src/test/resources/response.txt");
        InputStream is = new FileInputStream(initialFile);
        InputStreamReader isReader = new InputStreamReader(is);
        String str = IOUtils.toString(isReader);

        JsonObject element = new Gson().fromJson(str,JsonObject.class);

        JsonElement dataWrapper = element.get("data");
        JsonElement statusWrapper = element.get("status");

        CryptoResponseData[] response = new Gson().fromJson(dataWrapper,CryptoResponseData[].class);
        CryptoResponseStatus status = new Gson().fromJson(statusWrapper,CryptoResponseStatus.class);


        List<CryptoResponseData> data = Arrays.asList(response);
        Map<String,Currency> quote = data.get(0).getQuote();

        assertEquals(4,response.length);
        assertNotNull(status.getTimestamp());
        assertNotNull(quote.get("EUR"));
    }

    @Test
    public void testErrorResponse() throws IOException{
        File initialFile = new File("src/test/resources/error.txt");
        InputStream is = new FileInputStream(initialFile);
        InputStreamReader isReader = new InputStreamReader(is);
        String str = IOUtils.toString(isReader);

        JsonObject element = new Gson().fromJson(str,JsonObject.class);
        JsonElement statusWrapper = element.get("status");
        CryptoResponseStatus status = new Gson().fromJson(statusWrapper,CryptoResponseStatus.class);

        assertEquals(1001, status.getError_code());

    }

    private String makeAPICall(String uri,List<NameValuePair> parameters) throws URISyntaxException, IOException{
        String response_content = "";

        URIBuilder query = new URIBuilder(uri);
        query.addParameters(parameters);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(query.build());

        request.setHeader(HttpHeaders.ACCEPT,"application/json");
        request.addHeader("X-CMC_PRO_API_KEY",apiKey);

        CloseableHttpResponse response = client.execute(request);

        try{
            HttpEntity entity = response.getEntity();
            response_content = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        }
        finally{
            response.close();
        }

        return response_content;
    }

}
