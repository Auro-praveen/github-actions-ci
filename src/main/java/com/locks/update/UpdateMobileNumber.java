package com.locks.update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.JSONObject;

import com.auro.hibernateUtilities.HibernateUtils;

/**
 * @author Praveen
 */

@WebServlet("/UpdateMobileNumber")
public class UpdateMobileNumber extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    static int count = 0;
    static String otp="";
    
    public  URL url = null;
    public  HttpURLConnection httpConn = null;
    String packettype;
    
    public static HashMap instnogprs=new HashMap();
    public static int key=1,lastkey=1;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	PrintWriter writer = resp.getWriter();
    	String randomDigit = String.format("%04d", (int)Math.floor(Math.random()*(9999)));
    	
    	writer.write(String.valueOf(randomDigit));
    	writer.flush();
    	writer.close();
    	
    }
    

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter writer = response.getWriter();
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");
		
		Session session = HibernateUtils.getSession();
		
		session.beginTransaction();
//		Connection con =null;
//		Statement st = null;
//		ResultSet res = null;
		System.out.println("inside");
		
		// Hibernate query has been written instead jdbc  TODO:- 07-04-2023  PRAVEEN
		
		// fo the original db
//		String dbUrl = "jdbc:mysql://localhost:3306/auroautolocker";
//		String userName = "root";
//		String password = "aurotecGM@21";
		
		// for the commonly shared database
		
//		String dbUrl = "jdbc:mysql://192.168.0.133:3306/auroautolocker";
//		String userName = "praveen";
//		String password = "root";
		
		String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(Collectors.joining("/"));
		System.out.println(jsonBody);
		JSONObject jsonObj = new JSONObject(jsonBody);
		String packetType = jsonObj.getString("PacketType");
		JSONObject resp = new JSONObject();
		if (packetType.equalsIgnoreCase("genotp")) {
			
			String mobileNumber = jsonObj.getString("MobileNo");
//			int randomOtp = (int)Math.floor(Math.random()*(9999)+1);
			String randomDigit = String.format("%04d", (int)Math.floor(Math.random()*(9999)));
			otp = randomDigit;
			System.out.println(randomDigit);
			
			String sms="https://otp2.aclgateway.com/OTP_ACL_Web/OtpRequestListener?enterpriseid=tuckpotp&"
					+ "subEnterpriseid=tuckpotp&pusheid=tuckpotp&pushepwd=tuckpotp13&msisdn=91"+ mobileNumber+"&sender=TUCKPD"
				+ "&msgtext=Your%20OTP%20is%20"+randomDigit+".%20Ensure%20locker%20door%20is%20closed,%20t%26c%20apply,%20TUCKPOD.";
			
			int i = sendData(sms);
			System.out.println(i);
			
			resp.put("otp", randomDigit);
			resp.put("responseCode", "otpgen");
		} else if (packetType.equalsIgnoreCase("verotp")) {
			String enteredOtp = jsonObj.getString("otp");
			
			System.out.println("otp java : "+otp +" \n entered otp"+enteredOtp);
			if(enteredOtp.equals(otp)) {
				System.out.println("otp match");
				resp.put("responseCode", "verotpsuc");
			} else {
				resp.put("responseCode", "verotpfail");
				System.out.println("otp didnt match");
			}
		} else {
		String newMobileNumber = jsonObj.getString("newMobileNo");
		String curretMobileNumber = jsonObj.getString("MobileNo");
		String terminalID = jsonObj.getString("terminalID");
		
		String hqlQuery = "UPDATE TransactionDetails SET mobileNo=:newMobileNo WHERE mobileNo=:currentMobileNo AND terminalid=:terminalID";
//		String query = "UPDATE auro_auto_lock.transaction_details SET `mobileNo` = '"+newMobileNumber+"' WHERE (`mobileNo` = '"+curreMobileNumber+"') and (terminalid = '"+terminalID+"')";
//		String query = "UPDATE auroautolocker.transaction_details SET `mobileNo` = '"+newMobileNumber+"' WHERE (`mobileNo` = '"+curretMobileNumber+"') and (terminalid = '"+terminalID+"')";
		try {
			
			
			// if using jdbc database connection then uncomment the following and comment the hibernate session connections
			
//			Class.forName("com.mysql.cj.jdbc.Driver");
//			con = DriverManager.getConnection(dbUrl, userName, password);
//			st = con.createStatement();
//			int number = st.executeUpdate(query);
			
			int updateStatus = session.createQuery(hqlQuery).setParameter("newMobileNo", newMobileNumber)
					.setParameter("currentMobileNo", curretMobileNumber).setParameter("terminalID", terminalID).executeUpdate();
			
			
			if (updateStatus > 0) {
				resp.put("status", "success");
			} else {
				resp.put("status", "fail");
			}
			System.out.println(updateStatus);
			session.close();
//			con.close();
//			st.close();
		} catch (Exception e) {
			// TODO: handle exception
			session.close();
			e.printStackTrace();
		}
		}
		writer.append(resp.toString());
		writer.close();
		
	}
	
    public  int sendData(String smsURL) {
        String necresponse = "", nresponse = "";
        int responseCode = 0;
        try {
//           System.out.println("Entertd to network=" + smsURL);
            url = new URL(smsURL );
            httpConn = (HttpURLConnection) url.openConnection();
            //httpConn.setUseCaches(false);
            httpConn.setDoOutput(true);
            httpConn.setConnectTimeout(20000);
            httpConn.setRequestMethod("GET");
            System.setProperty("java.net.useSystemProxies", "true");
           
            responseCode = httpConn.getResponseCode();
            System.out.println("responseCode" + responseCode);
                           
            BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            String inputLine;
            StringBuffer response=new StringBuffer("");
           
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            } 
//            System.out.println(response);
        } 
        
        catch (Exception e) {
            e.printStackTrace(); 
        }
        return responseCode;
    }
}
