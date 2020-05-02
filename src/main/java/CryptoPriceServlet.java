import com.crypto.model.CryptoRequestData;
import com.crypto.services.CryptoPriceService;
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
    private final CryptoPriceService service = new CryptoPriceService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{

        String response;
        BufferedReader reader = req.getReader();
        Gson gson = new Gson();

        try {
            CryptoRequestData requestData = gson.fromJson(reader, CryptoRequestData.class);
            logger.info("Initiating crypto prices query");
            response = service.doConnect(requestData.getStart(), requestData.getLimit(), requestData.getConvert());
        }catch (Exception e){
            logger.error("Error retrieving params from cryptoprices request. Error: " + e);
            response = "Error retrieving params from cryptoprices. Error: " + e;
        }

        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.addHeader("Access-Control-Allow-Origin", "*");
        out.print(response);
        out.flush();
    }

}
