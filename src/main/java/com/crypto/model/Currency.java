package com.crypto.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Currency{

    private double price;
    private double volume_24h;
    private double percent_change_1h;
    private double percent_change_24h;
    private double percent_change_7d;
    private double market_cap;
    private Date last_updated;

}
