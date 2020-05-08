package com.crypto.services;

import com.crypto.model.CryptoQuote;
import com.crypto.model.CryptoResponseData;
import com.crypto.repositories.MongoRepository;
import com.crypto.utils.Utils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class CryptoPriceServiceImpl implements CryptoPriceService{

    private String apiKey = "";
    private String uri = "";
    private static Logger logger = LoggerFactory.getLogger(CryptoPriceServiceImpl.class);
    private final MongoRepository mongoRepository = new MongoRepository();
    Properties properties = new Utils().getProperties();
    Properties keyProperties = new Utils().getKeyProperties();

    public String processRequest(String start,String limit,String convert){
        String processResult;
        String result;
        List<CryptoResponseData> cryptoResponseData = new ArrayList<CryptoResponseData>();
        List<CryptoQuote> quotes;

        apiKey = keyProperties.getProperty("api.key");
        uri = properties.getProperty("crypto.prices.uri");

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

        quotes = Utils.CryptoResponseDataToQuote(cryptoResponseData,convert);

        mongoRepository.saveAll(quotes,convert);

        return quotesToServiceResponse(quotes);
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
        return gson.fromJson(data,CryptoResponseData[].class);
    }

    private String quotesToServiceResponse(List<CryptoQuote> quotes){
        StringBuilder builder = new StringBuilder();

        for(CryptoQuote quote : quotes){
            Gson gson = new Gson();
            builder.append(gson.toJson(quote));
        }

        return builder.toString();
    }

}
