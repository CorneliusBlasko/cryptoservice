package com.crypto.utils;

import com.crypto.model.CryptoQuote;
import com.crypto.model.CryptoResponseData;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Utils{

    public static List<String> getCurrencyFromQuote(CryptoResponseData data){
        List<String> currencies = new ArrayList<String>();
        currencies.addAll(data.getQuote().keySet());
        return currencies;
    }

    public static List<CryptoQuote> CryptoResponseDataToQuote(List<CryptoResponseData> data,String convert){
        List<CryptoQuote> quotes = new ArrayList<CryptoQuote>();

        for(CryptoResponseData responseData : data){
            CryptoQuote quote = new CryptoQuote();
            quote.setName(responseData.getName());
            quote.setSymbol(responseData.getSymbol());
            quote.setCurrency(Utils.getCurrencyFromQuote(responseData).get(0));
            quote.setPrice(responseData.getQuote().get(convert).getPrice());
            quote.setPercent_change(responseData.getQuote().get(convert).getPercent_change_24h());
            quote.setLast_updated(responseData.getQuote().get(convert).getLast_updated());

            quotes.add(quote);
        }

        return quotes;
    }

    public Properties getProperties(){
        Properties properties = new Properties();
        try{
            properties.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
        }
        catch(Exception e){

        }
        return properties;
    }

    public Properties getKeyProperties(){
        Properties properties = new Properties();
        try{
            properties.load(getClass().getClassLoader().getResourceAsStream("secure.properties"));
        }
        catch(Exception e){

        }
        return properties;
    }
}
