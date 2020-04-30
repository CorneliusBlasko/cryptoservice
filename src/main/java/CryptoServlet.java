import com.crypto.model.CryptoRequestData;
import com.crypto.services.CryptoService;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(
        name = "cryptoservlet",
        urlPatterns = "/CryptoPrices"
)
public class CryptoServlet extends HttpServlet{


    @Override
    protected void doGet(HttpServletRequest req,HttpServletResponse resp) throws IOException{

        BufferedReader reader = req.getReader();
        Gson gson = new Gson();

        CryptoRequestData requestData = gson.fromJson(reader, CryptoRequestData.class);

        CryptoService service = new CryptoService();
        String response = service.doConnect(requestData.getStart(), requestData.getLimit(), requestData.getConvert());

        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        out.print(response);
        out.flush();

    }

}
