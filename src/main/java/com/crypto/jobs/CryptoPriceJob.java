package com.crypto.jobs;

import com.crypto.services.CryptoPriceServiceImpl;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.Date;
import java.util.logging.Logger;


public class CryptoPriceJob implements Job{

    private final static Logger LOGGER = Logger.getLogger(CryptoPriceJob.class.getName());
    private final CryptoPriceServiceImpl service = new CryptoPriceServiceImpl();
    private String start = "1";
    private String limit = "10";
    private String convert = "USD";

    public void execute(JobExecutionContext jobExecutionContext){

        LOGGER.info("Executing scheduled job at " + new Date().toString());
        try{
            String response = service.processRequest(start,limit,convert);

        }
        catch(Exception e){
            LOGGER.severe("Error executing scheduled job at " + new Date().toString());
            LOGGER.severe("Error: " + e);
        }
                System.out.println("Request done");

    }
}
