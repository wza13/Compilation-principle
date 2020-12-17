package 课程设计;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

public class httpServer {
    private static treeBuilder tb = new treeBuilder();
    public static void main(String [] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8086), 0);
        server.createContext("/", new index());
        server.createContext("/postApi", new postApi());
        server.start();
    }
    static  class index implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            File root = new File("reactUI");
            String response = "File not exist";
            URI requestUri =  exchange.getRequestURI();
            if(requestUri.toString().equals("/")){
                try {
                    requestUri = new URI("/index.html");
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            File requestFile = new File(root+requestUri.toString());
            System.out.println(requestFile);
            byte[] bt;
            if (requestFile.isFile() && requestFile.exists()){
                InputStream ins = new FileInputStream(requestFile);
                bt = ins.readAllBytes();
            }
            else{
                bt = response.getBytes();
            }
            exchange.sendResponseHeaders(200, 0);
            OutputStream os = exchange.getResponseBody();
            os.write(bt);
            os.close();
        }
    }
    static class postApi implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) {
            try{
                InputStream in  = exchange.getRequestBody();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String temp;
                StringBuffer postSb = new StringBuffer();
                while((temp = reader.readLine()) != null) {
                    postSb.append(temp);
                }
                String postString = postSb.toString();
                String result = tb.getResult(postString);

                System.out.println(postString);

                System.out.println(result);

                exchange.sendResponseHeaders(200,0);
                OutputStream os = exchange.getResponseBody();
                os.write(result.getBytes());
                os.close();
            }catch (IOException ie) {

            } catch (Exception e) {

            }
        }
    }
}

