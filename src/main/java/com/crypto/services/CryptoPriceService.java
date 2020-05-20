package com.crypto.services;

public interface CryptoPriceService{

    String processRequest(String start,String limit,String convert);
    String getCoins(String convert);
}
