package com.crypto.controllers;

import com.crypto.model.CryptoRequestData;

public interface CryptoPriceController{

    String getCryptoPrices(CryptoRequestData requestData);

}
