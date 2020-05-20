package com.crypto.controllers;

import com.crypto.model.CryptoRequest;
import com.crypto.services.CryptoPriceService;
import com.crypto.services.CryptoPriceServiceImpl;

public class CryptoPriceControllerImpl implements CryptoPriceController{

    private final CryptoPriceService service;

    public CryptoPriceControllerImpl(CryptoPriceServiceImpl service){
        this.service = service;
    }

    //This method makes the scheduled calls to the API
    @Override
    public String getCryptoPrices(String start, String limit, String currency){
        return service.processRequest(start, limit, currency);
    }

    //This method manages the frontend requests
    @Override
    public String getLastCoins(CryptoRequest requestData){
        return service.getCoins(requestData.getConvert());
    }

}
