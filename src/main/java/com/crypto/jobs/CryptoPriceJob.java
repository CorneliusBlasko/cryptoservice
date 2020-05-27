package com.crypto.jobs;

import com.crypto.controllers.CryptoPriceControllerImpl;
import com.crypto.services.CryptoPriceServiceImpl;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.Date;
import java.util.logging.Logger;


public class CryptoPriceJob implements Job{

    private final static Logger LOGGER = Logger.getLogger(CryptoPriceJob.class.getName());
    private final CryptoPriceServiceImpl cryptoPriceService = new CryptoPriceServiceImpl();
    private final CryptoPriceControllerImpl cryptoPriceController = new CryptoPriceControllerImpl(cryptoPriceService);
    private String start = "1";
    private String limit = "10";
    private String USD = "USD";
    private String EUR = "EUR";

    public void execute(JobExecutionContext jobExecutionContext){

        LOGGER.info("Executing scheduled job at " + new Date().toString());
        try{
            cryptoPriceController.getCryptoPrices(start,limit,USD);
            cryptoPriceController.getCryptoPrices(start,limit,EUR);

        }
        catch(Exception e){
            LOGGER.severe("Error executing scheduled job at " + new Date().toString());
            LOGGER.severe("Error: " + e);
        }
                System.out.println("Request done");

    }
}
