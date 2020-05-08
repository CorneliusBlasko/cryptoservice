package com.crypto.repositories;

import com.crypto.model.CryptoQuote;

import java.util.List;

public interface Repository{

    void saveAll(List<CryptoQuote> quotes,String convert);
}
