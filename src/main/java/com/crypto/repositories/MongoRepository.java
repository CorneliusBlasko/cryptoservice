package com.crypto.repositories;

import com.crypto.model.CryptoRequestData;

public interface MongoRepository{

    void save(CryptoRequestData data);
}
