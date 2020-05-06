package com.crypto.repositories;

import com.crypto.model.CryptoRequest;

public interface MongoRepository{

    void save(CryptoRequest data);
}
