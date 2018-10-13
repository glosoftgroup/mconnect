/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.glosoftgroup.mpesa.api;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import com.glosoftgroup.mpesa.models.Transaction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.glosoftgroup.mpesa.utils.Logging;
import com.glosoftgroup.mpesa.models.User;
import com.glosoftgroup.mpesa.utils.MyPreferences;

/**
 *
 * @author alexkiburu
 */
public class Api {
    
    public static Api instance = null;
    private final String USER_AGENT = "Mozilla/5.0";
    private String BaseUrl;
    
    private static transient Logging log;
    
    public static Api getInstance(Logging logging) {
        // define log if null
//        log = log == null ? new Logging() : log;
        log = logging;
        return instance == null ? new Api(log) : instance;
    }
    
    public Api(Logging logging){
        log = logging;
        testConnection();        
        instance = this;
    }
    
    private boolean testConnection(){
        try {
            log.info("Testing connection...");
            BaseUrl = Endpoint.getInstance().getBaseUrl();
            URL url = new URL(BaseUrl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setReadTimeout(4000);
            connection.connect();
            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                return true;
            }else{
                log.info("Error Connecting to the Server ...");
            }
            log.info("Connected to : "+url.toString()+" connection.getResponseCode()...");
        } catch (Exception ex) {
            log.info("Connection Error! "+ex.getMessage(), ex);
            log.error("Connection Error!", ex);
        }
        return false;
    }
       
     public boolean login(String username, String pass) {
        JSONObject user = new JSONObject();
        user.put("email", username);
        user.put("password", pass);
        String PostData = "{}";
        try {
            PostData = PostData(user.toJSONString(), "login");
        } catch (Exception ex) { 
            Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
            log.info(ex.getMessage(), ex);
            return false;
        }

        System.err.println("post data"+PostData);
        //return true;
        JSONParser parser = new JSONParser();
        JSONObject res = new JSONObject();
        try {
            res = (JSONObject) parser.parse(PostData);
        } catch (ParseException ex) {
            log.info(ex.getMessage(), ex);
            return false;
        }

        if (res.get("token") != null) {
            //go home and initialize the user and terminal
            JSONObject jsonuser = (JSONObject) res.get("user");
            User me = new User(
                    jsonuser.get("id") != null ? jsonuser.get("id").toString() : "",
                    jsonuser.get("name") != null ? jsonuser.get("name").toString() : "N/A",
                    jsonuser.get("code") != null ? jsonuser.get("code").toString() : "00000",
                    jsonuser.get("email") != null ? jsonuser.get("email").toString() : username,
                    jsonuser.get("password") != null ? jsonuser.get("password").toString() : pass,
                    jsonuser.get("position") != null ? jsonuser.get("position").toString() : "Teller",
                    res.get("token").toString()
            );
            MyPreferences.getInstance().setUser(me);
            return true;
        } else {
            String Error = res.get("error") != null ? res.get("error").toString() : "Some Error occured";
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(Error);
                alert.show();
            });
            log.info("Error getting the token");
        }
        return false;
    }

     /* end of postdata with label inside */
    // HTTP POST request
    public String PostData(String json_data,String type) throws Exception {
        boolean exit=false;
        String request="POST";
        String url = "";
        if(type.equalsIgnoreCase("login")){ 
            url = Endpoint.getInstance().getLOGIN();
        }
        
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        
        try{con.setDoInput(true);}catch(Exception e){
            log.info(e.getMessage(),e);
        }
        
        //add reuqest header
        con.setRequestMethod(request);
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setConnectTimeout(3000);
        JWTAuthorize(con);
        String urlParameters = json_data;

        // Send post request
        try{con.setDoOutput(true);}catch(Exception e){
            e.printStackTrace();
            log.info(e.getMessage(), e);
        }
        
        
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        if(responseCode==401){
            log.info("Error " + responseCode);
        }else if(responseCode ==200 || responseCode ==201 || responseCode==202 || responseCode==204){/*looks good*/
            log.info("Success " + responseCode);
        }
        else{
            //read error
            String error = getError(con); 
            log.error("Error Code: "+responseCode+"\n"+error);
        }

        log.info("Sending 'POST' request to URL : " + url+"\t->Post parameters :" + urlParameters + "\t->Response Code :"  + responseCode);
        
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } catch (IOException ioe) {
            StringBuffer response = new StringBuffer();
            return response.toString();
        }
    }
    
    
    public String sendGet(URL url, boolean authorize) throws IOException{
        
        log.info("REQUEST { \"Method\": GET, \"url\": "+url+" }");
        
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        if(authorize)JWTAuthorize(con);
        /* optional default Method is GET */
        con.setRequestMethod("GET");
        /* add request header */
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        con.setUseCaches(false);
        con.setConnectTimeout(3000);
        
        int responseCode = con.getResponseCode();
        
        if(responseCode!=200){//not ok
            if(responseCode==401){//Unauthorized, so login again                
                //new login().Relaunch(Main.home);
                log.info("Error "+responseCode+"\nTransaction is not authorized!\nYou must login again to continue");
            }else{  
                String error = getError(con);
                log.error("Error: " + error + " status is: " + responseCode); 
            }            
        }
        
        log.info("Sending 'GET' request to URL : " + url.toString()+"\nResponse Code : " + responseCode);
        
        StringBuilder response;
        try (BufferedReader in = new BufferedReader(
            new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        }
        
        System.err.println(response);
        log.info("RESPONSE { \"Method\": GET, \"body\": "+response+" }");
        return response.toString();
    }
    
     private String sendGet(URL obj) throws IOException{
        return sendGet(obj, true);
    }

    private String getError(HttpURLConnection con){
        
        try {
            
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            StringBuffer err = new StringBuffer();
            String inputLine;
            
            while ((inputLine = in.readLine()) != null) {
                err.append(inputLine);
            }
            
            in.close();
            
            log.info(err.toString());
            
            JSONObject object=(JSONObject)new JSONParser().parse(err.toString());
            StringBuffer tostring = new StringBuffer();
            
            object.forEach((t, u) -> {
                if(t.toString().contains("status_code")){}else{
                    try{((JSONArray)new JSONParser().parse(u.toString())).forEach(O->{
                        tostring.append("Error Message: ").append(O.toString());
                    });
                    }catch( Exception ex){
                        tostring.append("Error: ").append(t.toString()).append("\nMessage: ").append(u.toString()).append("\n\n");
                    }
                }
            });
            
            return tostring.toString();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        
        return "Sorry, we got an error!";
    }  
    
    public JSONArray getNewTransactions(){
        
        JSONArray parseData = new JSONArray();
        try {
            URL url = new URL(Endpoint.getInstance().getNewMPESATransactionsURL());
            String response = sendGet(url);
            JSONParser parser= new JSONParser();
            JSONObject objectData=(JSONObject) parser.parse(response);
            parseData = (JSONArray) objectData.get("results");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        
        return parseData;
    }
    
    
    /** not really needed for the daemon
     *  but can be used for the restaurant
     */
    public void searchTransaction(String needle, ObservableList<Transaction> items){

        try {
            items.clear();
            String url = BaseUrl + "/api/transactions/?q="+URLEncoder.encode(needle,"UTF-8")+"&format=json";
            URL obj = new URL(url);
            String response=sendGet(obj);
            JSONParser parser = new JSONParser();
            Object objt = parser.parse(response);
            JSONArray dataArray = (JSONArray) objt;
           
            dataArray.stream().forEach(data->{
                items.add(Transaction.fromJSON((JSONObject) data));
            });

        } catch (Exception e) { 
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
        
    }
    
    private void JWTAuthorize(HttpURLConnection con){
        con.setRequestProperty("Authorization","JWT "+ MyPreferences.getInstance().getUser().getToken());
        System.out.println("token:-"+MyPreferences.getInstance().getUser().getToken());
    }
}
