package com.crypto.controllers;

import com.crypto.model.CryptoRequest;
import com.crypto.services.CryptoPriceService;
import com.crypto.services.CryptoPriceServiceImpl;

public class CryptoPriceControllerImpl implements CryptoPriceController{

    private final CryptoPriceService service;

    public CryptoPriceControllerImpl(CryptoPriceServiceImpl service){
        this.service = service;
    }

    //Este método queda para las llamadas automáticas a la API
    public String getCryptoPrices(CryptoRequest requestData){
        return service.processRequest(requestData.getStart(),requestData.getLimit(),requestData.getConvert());
    }

    //Este método gestiona las llamadas del front
    public String getLastCoins(CryptoRequest requestData){
        return service.getCoins(requestData.getConvert());
    }

}
