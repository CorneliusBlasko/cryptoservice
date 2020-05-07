package com.crypto.services;

import com.crypto.model.CryptoResponseData;
import com.crypto.repositories.CryptoPriceRepository;
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
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CryptoPriceServiceImpl implements CryptoPriceService{

    private String apiKey = "";
    private String uri = "";
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(CryptoPriceServiceImpl.class);
    private final CryptoPriceRepository mongoRepository = new CryptoPriceRepository();

    public String processRequest(String start,String limit,String convert){
        Properties properties = new Properties();
        Properties keyProperties = new Properties();
        String processResult;
        String result = "";
        List<CryptoResponseData> cryptoResponseData = new ArrayList<CryptoResponseData>();

        try{
            properties.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
            keyProperties.load(getClass().getClassLoader().getResourceAsStream("secure.properties"));
            apiKey = keyProperties.getProperty("api.key");
            uri = properties.getProperty("crypto.prices.uri");
        }
        catch(IOException e){
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,e.getMessage(),e);
        }

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("start",start));
        params.add(new BasicNameValuePair("limit",limit));
        params.add(new BasicNameValuePair("convert",convert));

        try{
            result = makeAPICall(uri,params);
            cryptoResponseData = Arrays.asList(this.parseResponse(result));
        }
        catch(IOException e){
            processResult = "Error: " + e;
            logger.error(processResult);
        }
        catch(URISyntaxException e){
            processResult = "Error: " + e;
            logger.error(processResult);
        }

        mongoRepository.saveAll(cryptoResponseData,convert);

        return result;
    }

    private String makeAPICall(String uri,List<NameValuePair> parameters) throws URISyntaxException, IOException{
        String response_content;
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


            return response_content;
        }
        catch(Exception e){
            logger.error("Error: " + e);
            return null;
        }
        finally{
            response.close();
        }

    }

    private CryptoResponseData[] parseResponse(String content){
        JsonObject element = new Gson().fromJson(content,JsonObject.class);
        JsonElement data = element.get("data");
        Gson gson = new Gson();
        CryptoResponseData[] response = gson.fromJson(data,CryptoResponseData[].class);

        return response;
    }

}
