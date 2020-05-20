package com.crypto.utils;

import com.crypto.model.Coin;
import com.crypto.model.CryptoQuote;
import com.crypto.model.CryptoResponseData;

import java.text.NumberFormat;
import java.util.*;

public class Utils{

    /*public static List<String> getCurrencyFromQuote(CryptoResponseData data){
        List<String> currencies = new ArrayList<String>();
        currencies.addAll(data.getQuote().keySet());
        return currencies;
    }*/

    public static CryptoQuote CryptoResponseDataToQuote(List<CryptoResponseData> data,String convert){
        List<Coin> coins = new ArrayList<>();

        NumberFormat numberFormatter = NumberFormat.getNumberInstance(new Locale("es","ES"));
        CryptoQuote quote = new CryptoQuote();
        quote.setCurrency(convert);
        quote.setTimestamp(new Date());


        for(CryptoResponseData responseData : data){
            Coin coin = new Coin();

            coin.setName(responseData.getName());
            coin.setSymbol(responseData.getSymbol());
            coin.setPrice(numberFormatter.format(responseData.getQuote().get(convert).getPrice()));
            coin.setPercent_change(numberFormatter.format(responseData.getQuote().get(convert).getPercent_change_24h()));
            coin.setLast_updated(responseData.getQuote().get(convert).getLast_updated());

            coins.add(coin);
        }

        quote.setData(coins);

        return quote;
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
