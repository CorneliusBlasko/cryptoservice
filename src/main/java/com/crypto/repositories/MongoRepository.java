package com.crypto.repositories;

import com.crypto.model.CryptoResponseData;

import java.util.List;

public interface MongoRepository{

    void saveAll(List<CryptoResponseData> data, String convert);
}
