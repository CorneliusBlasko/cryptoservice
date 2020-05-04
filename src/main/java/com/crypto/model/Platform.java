package com.crypto.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Platform{

    private String id;
    private String name;
    private String symbol;
    private String slug;
    private String token_address;

}
