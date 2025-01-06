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

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Praveen
 */

@WebServlet("/ConfirmOtp")
public class ConfirmOtp extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConfirmOtp() {
        super();

    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// System.out.println("inside get");
		
	}


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
		
		JSONArray availLocks = new JSONArray();
		availLocks.put("S2");
		availLocks.put("S3");
		availLocks.put("L1");
		availLocks.put("L24");
		availLocks.put("L20");
		availLocks.put("S15");
		availLocks.put("L22");
		availLocks.put("L11");
		availLocks.put("L17");
		availLocks.put("S14");
		availLocks.put("S8");
		availLocks.put("S10");
		availLocks.put("M8");

		
		// System.out.println(availLocks);

		PrintWriter writer = response.getWriter();
		JSONArray hoursslot = new JSONArray("[1, 3, 5, 7, 10]");
		JSONArray smallAmount = new JSONArray("[20, 30, 50, 70, 100, 130]");
		JSONArray mediumAmount = new JSONArray("[30, 40, 60, 80, 110, 140]");
		JSONArray largeAmount = new JSONArray("[40, 50, 70, 90, 120, 150]");

		respOTPObject.put("PacketType", "stverotp");
		respOTPObject.put("otp","2256");
		respOTPObject.put("MobileNo","9980071121");
		respOTPObject.put("terminalID","G21");
		respOTPObject.put("TransactionId","6664091222180443");
		respOTPObject.put("responseCode", "VEROTPN-200");
		respOTPObject.put("userName", "Anamika");
		respOTPObject.put("AvailableLocker", availLocks);
		respOTPObject.put("Small", smallAmount);
		respOTPObject.put("Large", largeAmount);
		respOTPObject.put("Medium", mediumAmount);
		respOTPObject.put("hourslot", hoursslot);
		
		writer.println(respOTPObject.toString());
		writer.flush();
		writer.close();
		
	}
}
