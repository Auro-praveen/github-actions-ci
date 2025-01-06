package com.auro.otpHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * @author Praveen
 */

public class SmsSender {
//	String otp=GlobalVariables.storeHandler.insert(mobileno,terminalID);
	
	 //String sms="https://otp2.aclgateway.com/OTP_ACL_Web/OtpRequestListener?enterpriseid=tuckpotp&subEnterpriseid=tuckpotp&pusheid=tuckpotp&pushepwd=tuckpotp13&msisdn=91"+&sender=TUCKPD&msgtext=Your";
//	if(GlobalVariables.sendSMS) {
//	controller.smsSender sensms=new controller.smsSender(sms);
//	}

    static int count = 0;
 
    
    public  URL url = null;
    public  HttpURLConnection httpConn = null;
    String packettype;
    
    public static HashMap instnogprs=new HashMap();
    public static int key=1,lastkey=1;
    
    
    
    public SmsSender( String packettype ) 
    {
       this.packettype=packettype;
//        start();
    }
    public void run()
    {
        int rcode=sendData( packettype) ;  
    }
    
    public  int sendData(String smsURL) {
        String necresponse = "", nresponse = "";
        int responseCode = 0;
        try {
           // System.out.println("Entertd to network=" + smsURL);
            url = new URL(smsURL );
            httpConn = (HttpURLConnection) url.openConnection();
            //httpConn.setUseCaches(false);
            httpConn.setDoOutput(true);
            httpConn.setConnectTimeout(20000); //satish Jun 4 2019 earlier 4000
            httpConn.setRequestMethod("GET");
            System.setProperty("java.net.useSystemProxies", "true");
           
            responseCode = httpConn.getResponseCode();
            // System.out.println("responseCode" + responseCode);
                           
            BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            String inputLine;
            StringBuffer response=new StringBuffer("");
           
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            } 
            // System.out.println(response);
              
        } 
        catch (Exception e) {
      
            e.printStackTrace();
        
        }
        return responseCode;
    }

    
}
