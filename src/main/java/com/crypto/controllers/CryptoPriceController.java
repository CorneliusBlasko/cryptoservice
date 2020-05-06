package com.crypto.controllers;

import com.crypto.model.CryptoRequest;

public interface CryptoPriceController{

    String getCryptoPrices(CryptoRequest requestData);

}
