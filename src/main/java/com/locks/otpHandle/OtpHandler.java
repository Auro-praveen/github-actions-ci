package com.locks.otpHandle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class OtpHandler
 */

@WebServlet("/OtpHandler")
public class OtpHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	
    public OtpHandler() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		// for client side handling this is for testing purpose
		// System.out.println("inside post");
		
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");
		
//		String phonenumber = request.getParameter("mobileNum");
//		// System.out.println(phonenumber);
		
		JSONObject respOTPObject =new JSONObject();
		
//		String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(Collectors.joining("/"));
//		JSONObject reqObj = new JSONObject(jsonBody);
//		
//		// System.out.println(reqObj);
		
		PrintWriter writer = response.getWriter();
//		if (phonenumber != null) {
//			respOTPObject.put("status", "success");
//		} else {
			respOTPObject.put("PacketType", "stverotp");
			respOTPObject.put("otp","2256");
			respOTPObject.put("MobileNo","9980071121");
			respOTPObject.put("terminalID","G21");
			respOTPObject.put("TransactionId","6664091222180443");
			respOTPObject.put("responseCode", "VEROTPE-200");
			respOTPObject.put("dob", "1997:03:18");
//		}
		
		writer.println(respOTPObject.toString());
		writer.flush();
		writer.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
		// for client side handling this is for testing purpose
		// System.out.println("inside post");
		
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");
		
		JSONObject respOTPObject =new JSONObject();
		
		String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(Collectors.joining("/"));
		JSONObject reqObj = new JSONObject(jsonBody);
		
		// System.out.println(reqObj);
		
		PrintWriter writer = response.getWriter();
		respOTPObject.put("responseCode", "GENOTP-200");

//			respOTPObject.put("PacketType", "stverotp");
//			respOTPObject.put("otp","2256");
//			respOTPObject.put("MobileNo","9980071121");
//			respOTPObject.put("terminalID","G21");
//			respOTPObject.put("TransactionId","6664091222180443");
//			respOTPObject.put("responseCode", "VEROTPE-200");

		
		writer.println(respOTPObject.toString());
		writer.flush();
		writer.close();
		
	}

}
