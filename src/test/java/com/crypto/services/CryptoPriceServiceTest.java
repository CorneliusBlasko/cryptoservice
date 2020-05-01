package com.crypto.services;

import com.crypto.model.CryptoResponseData;
import com.crypto.model.CryptoResponseStatus;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CryptoPriceServiceTest {

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
        }catch(IOException e){
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

        try {
            result = makeAPICall(uri, params);
        } catch (IOException e) {
            System.out.println("Error: cannont access content - " + e.toString());
        } catch (URISyntaxException e) {
            System.out.println("Error: Invalid URL " + e.toString());
        }

        assertNotSame("", result);
    }

    @Test
    public void testMalformedRequest(){

        String result = "";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("start","1"));
        params.add(new BasicNameValuePair("limit","2"));
        params.add(new BasicNameValuePair("convert","bad parameter"));

        try {
            result = makeAPICall(uri, params);
        } catch (IOException e) {
            System.out.println("Error: cannont access content - " + e.toString());
        } catch (URISyntaxException e) {
            System.out.println("Error: Invalid URL " + e.toString());
        }

        JsonObject element = new Gson().fromJson(result, JsonObject.class);
        JsonElement status = element.get("status");
        Gson gson = new Gson();
        CryptoResponseStatus responseStatus = gson.fromJson(status,CryptoResponseStatus.class);

        assertEquals(400, responseStatus.getError_code());
    }

    @Test
    public void testConnectionFailure(){

        boolean success = false;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("start","1"));
        params.add(new BasicNameValuePair("limit","2"));
        params.add(new BasicNameValuePair("convert","EUR"));

        try {
            makeAPICall("", params);
            success = true;
        } catch (IOException e) {
            System.out.println("Error: cannont access content - " + e.toString());
        } catch (URISyntaxException e) {
            System.out.println("Error: Invalid URL " + e.toString());
        }

        assertNotEquals(success, true);
    }

    @Test
    public void testParseResponse(){
        String str = "{\"status\":{\"timestamp\":\"2020-04-30T14:20:04.757Z\",\"error_code\":0,\"error_message\":null,\"elapsed\":11,\"credit_count\":1,\"notice\":null},\"data\":[{\"id\":1,\"name\":\"Bitcoin\",\"symbol\":\"BTC\",\"slug\":\"bitcoin\",\"num_market_pairs\":7997,\"date_added\":\"2013-04-28T00:00:00.000Z\",\"tags\":[\"mineable\"],\"max_supply\":21000000,\"circulating_supply\":18353512,\"total_supply\":18353512,\"platform\":null,\"cmc_rank\":1,\"last_updated\":\"2020-04-30T14:18:32.000Z\",\"quote\":{\"USD\":{\"price\":8849.96060035,\"volume_24h\":72540472164.7273,\"percent_change_1h\":0.26688,\"percent_change_24h\":6.46298,\"percent_change_7d\":21.501,\"market_cap\":162427858078.0509,\"last_updated\":\"2020-04-30T14:18:32.000Z\"}}},{\"id\":1027,\"name\":\"Ethereum\",\"symbol\":\"ETH\",\"slug\":\"ethereum\",\"num_market_pairs\":5163,\"date_added\":\"2015-08-07T00:00:00.000Z\",\"tags\":[\"mineable\"],\"max_supply\":null,\"circulating_supply\":110733523.999,\"total_supply\":110733523.999,\"platform\":null,\"cmc_rank\":2,\"last_updated\":\"2020-04-30T14:18:26.000Z\",\"quote\":{\"USD\":{\"price\":212.019867947,\"volume_24h\":29292026272.6805,\"percent_change_1h\":-0.0370262,\"percent_change_24h\":1.03547,\"percent_change_7d\":14.2834,\"market_cap\":23477707135.573933,\"last_updated\":\"2020-04-30T14:18:26.000Z\"}}},{\"id\":52,\"name\":\"XRP\",\"symbol\":\"XRP\",\"slug\":\"xrp\",\"num_market_pairs\":537,\"date_added\":\"2013-08-04T00:00:00.000Z\",\"tags\":[],\"max_supply\":100000000000,\"circulating_supply\":44112853111,\"total_supply\":99990976125,\"platform\":null,\"cmc_rank\":3,\"last_updated\":\"2020-04-30T14:18:04.000Z\",\"quote\":{\"USD\":{\"price\":0.217865583548,\"volume_24h\":3386493902.03635,\"percent_change_1h\":0.0529221,\"percent_change_24h\":-1.49211,\"percent_change_7d\":13.9503,\"market_cap\":9610672484.995222,\"last_updated\":\"2020-04-30T14:18:04.000Z\"}}}]}";

        JsonObject element = new Gson().fromJson(str, JsonObject.class);

        JsonElement dataWrapper = element.get("data");
        JsonElement statusWrapper = element.get("status");

        Gson gson = new Gson();
        CryptoResponseData[] response = gson.fromJson(dataWrapper,CryptoResponseData[].class);
        CryptoResponseStatus status = gson.fromJson(statusWrapper, CryptoResponseStatus.class);

        assertEquals(3, response.length);
        assertNotNull(status.getTimestamp());
    }

    private String makeAPICall(String uri, List<NameValuePair> parameters)
            throws URISyntaxException, IOException {
        String response_content = "";

        URIBuilder query = new URIBuilder(uri);
        query.addParameters(parameters);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(query.build());

        request.setHeader(HttpHeaders.ACCEPT,"application/json");
        request.addHeader("X-CMC_PRO_API_KEY", apiKey);

        CloseableHttpResponse response = client.execute(request);

        try {
            //            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            response_content = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }

        return response_content;
    }

}
