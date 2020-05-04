package com.crypto.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CryptoResponseData{

    private String id;
    private String name;
    private String symbol;
    private String slug;
    private String num_market_pairs;
    private Date date_added;
    private List<String> tags;
    private long circulating_supply;
    private long total_supply;
    private Platform platform;
    private String cmc_rank;
    private Date last_updated;
    private Map<String, Currency> quote;


}
