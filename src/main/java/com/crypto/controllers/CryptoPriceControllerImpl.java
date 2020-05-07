package com.crypto.controllers;

import com.crypto.model.CryptoRequest;
import com.crypto.services.CryptoPriceService;
import com.crypto.services.CryptoPriceServiceImpl;

public class CryptoPriceControllerImpl implements CryptoPriceController{

    private final CryptoPriceService service;

    public CryptoPriceControllerImpl(CryptoPriceServiceImpl service){
        this.service = service;
    }

    public String getCryptoPrices(CryptoRequest requestData){
        return service.processRequest(requestData.getStart(),requestData.getLimit(),requestData.getConvert());
    }

}
