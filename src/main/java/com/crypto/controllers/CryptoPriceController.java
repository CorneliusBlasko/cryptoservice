package com.crypto.controllers;

import com.crypto.model.CryptoRequestData;

import java.io.IOException;

public interface CryptoPriceController{

    String getCryptoPrices(CryptoRequestData requestData);
    String getCryptoPricesTest() throws IOException;

}
