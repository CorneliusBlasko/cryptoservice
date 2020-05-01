package com.crypto.jobs;

import com.crypto.services.CryptoPriceService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.logging.Logger;


public class CryptoPriceJob implements Job {

    private final static Logger LOGGER = Logger.getLogger(CryptoPriceJob.class.getName());
    private final CryptoPriceService service = new CryptoPriceService();
    private String start = "1";
    private String limit = "1";
    private String convert = "EUR";

    public void execute(JobExecutionContext jobExecutionContext){

        String response = service.doConnect(start, limit, convert);
//        System.out.println("Request done");

    }
}
