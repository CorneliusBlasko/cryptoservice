package com.crypto.controllers;

import com.crypto.model.CryptoRequestData;
import com.crypto.services.CryptoPriceServiceImpl;

public class CryptoPriceControllerImpl implements CryptoPriceController{

    private final CryptoPriceServiceImpl service;

    public CryptoPriceControllerImpl(CryptoPriceServiceImpl service){
        this.service = service;
    }

    public String getCryptoPrices(CryptoRequestData requestData){
        String response;
        response = service.doConnect(requestData.getStart(), requestData.getLimit(), requestData.getConvert());
        return response;
    }

}