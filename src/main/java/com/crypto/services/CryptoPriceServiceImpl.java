package com.crypto.services;

import com.crypto.model.Coin;
import com.crypto.model.CryptoQuote;
import com.crypto.model.CryptoResponseData;
import com.crypto.model.CryptoResponseStatus;
import com.crypto.repositories.MongoRepository;
import com.crypto.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoCollection;
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
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class CryptoPriceServiceImpl implements CryptoPriceService{

    private String apiKey = "";
    private String uri = "";
    private static Logger logger = LoggerFactory.getLogger(CryptoPriceServiceImpl.class);
    private final MongoRepository mongoRepository = new MongoRepository();
    Properties properties = new Utils().getProperties();
    Properties keyProperties = new Utils().getKeyProperties();
    private static final String GENERIC_ERROR = "An unexpected error occured, please see server log";

    public String processRequest(String start,String limit,String convert){
        String processResult;
        String result;
        CryptoResponseStatus status;
        List<CryptoResponseData> cryptoResponseData;
        CryptoQuote quote;

        apiKey = keyProperties.getProperty("api.key");
        uri = properties.getProperty("crypto.prices.uri");

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("start",start));
        params.add(new BasicNameValuePair("limit",limit));
        params.add(new BasicNameValuePair("convert",convert));

        try{
            result = makeAPICall(uri,params);
            status = getCryptoResponseStatus(result);
            if(status.getError_code() == 0){
                cryptoResponseData = Arrays.asList(this.getCryptoResponseData(result));
                quote = Utils.CryptoResponseDataToQuote(cryptoResponseData,convert);
                mongoRepository.saveAll(quote,convert);
                return coinsToResponse(quote.getData());
            }
            else{
                return status.getError_message();
            }
        }
        catch(Exception e){
            processResult = "Error: " + e;
            logger.error(processResult);
        }

        return GENERIC_ERROR;

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

    public String getCoins(String convert){

        mongoRepository.saveLog(convert);

        List<Coin> coins = mongoRepository.getLastQuoteByCurrency(convert);

        return coinsToResponse(coins);

    }

    private CryptoResponseStatus getCryptoResponseStatus(String content){
        JsonObject element = new Gson().fromJson(content,JsonObject.class);
        JsonElement data = element.get("status");
        Gson gson = new Gson();
        return gson.fromJson(data,CryptoResponseStatus.class);
    }

    private CryptoResponseData[] getCryptoResponseData(String content){
        JsonObject element = new Gson().fromJson(content,JsonObject.class);
        JsonElement data = element.get("data");
        Gson gson = new Gson();
        return gson.fromJson(data,CryptoResponseData[].class);
    }

    private String coinsToResponse(List<Coin> coins){
        StringBuilder builder = new StringBuilder();
        builder.append("{\"data\":[");

        for(Coin coin : coins){
            Gson gson = new Gson();
            builder.append(gson.toJson(coin));
            if(coins.indexOf(coin) != coins.size() - 1){
                builder.append(',');
            }
            else{
                builder.append("]}");

            }
        }

        return builder.toString();
    }

}
