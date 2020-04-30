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
public class ResponseStatus{

    private Date timestamp;
    private int error_code;
    private String error_message;
    private double elapsed;
    private double credit_count;
    private String notice;
}
