package com.crypto.controllers;

import com.crypto.model.CryptoRequest;

public interface CryptoPriceController{

    String getCryptoPrices(String start, String limit, String currency);
    String getLastCoins(CryptoRequest requestData);

}
