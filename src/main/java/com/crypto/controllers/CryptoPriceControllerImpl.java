package com.crypto.controllers;

import com.crypto.model.CryptoRequestData;
import com.crypto.services.CryptoPriceServiceImpl;
import org.apache.logging.log4j.core.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

    public String getCryptoPricesTest() throws IOException{
        //        File initialFile = new File("response.txt");
        //        InputStream is = new FileInputStream(initialFile);
        InputStream is = getClass().getClassLoader().getResourceAsStream("response.txt");
        InputStreamReader isReader = new InputStreamReader(is);
        return IOUtils.toString(isReader);
    }

}
