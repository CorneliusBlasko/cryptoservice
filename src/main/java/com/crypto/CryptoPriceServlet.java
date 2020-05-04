package com.crypto;

import com.crypto.controllers.CryptoPriceControllerImpl;
import com.crypto.model.CryptoRequestData;
import com.crypto.services.CryptoPriceServiceImpl;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(
        name = "cryptoservlet",
        urlPatterns = "/cryptoprices"
)
public class CryptoPriceServlet extends HttpServlet{

    private static Logger logger = LoggerFactory.getLogger(CryptoPriceServlet.class);
    private final CryptoPriceServiceImpl cryptoPriceService = new CryptoPriceServiceImpl();
    private final CryptoPriceControllerImpl cryptoPriceController = new CryptoPriceControllerImpl(cryptoPriceService);

    private final String CRYPTOPRICESERVICE = "cryptopriceservice";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        String response = "";
        String error = "";
        BufferedReader reader = req.getReader();
        Gson gson = new Gson();
        CryptoRequestData requestData = gson.fromJson(reader, CryptoRequestData.class);

        try {
            if(null != requestData.getService()){
                if(requestData.getService().equals(CRYPTOPRICESERVICE)){
                    response = cryptoPriceController.getCryptoPrices(requestData);
                }
                logger.info("Initiating crypto prices query");
            }else{
                response = "Error. A service must be specified in the request.";
            }
        }catch (Exception e){
            error = "Error retrieving params from cryptoprices request. Error: " + e;
            logger.error(error);
            response = error;
        }

        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.addHeader("Access-Control-Allow-Origin", "*");
        out.print(response);
        out.flush();
    }
}