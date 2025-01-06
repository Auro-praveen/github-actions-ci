package com.lockretrice.passcodeHandler;

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
 * @author Praveen
 */

@WebServlet("/OpenLockHandler")
public class OpenLockHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OpenLockHandler() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		// System.out.println("inside post");
		
		JSONObject respOTPObject =new JSONObject();
		
		String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(Collectors.joining("/"));
		JSONObject reqObj = new JSONObject(jsonBody);
		
		// System.out.println(reqObj);

		// for excess hour and amount
//		respOTPObject.put("responseCode", "LOCEX-200");
//		respOTPObject.put("eamount", "232");
//		respOTPObject.put("EXHour", "30");
		
		//for user agreed to pay post payment {responseCode:"LOCPO-200",amount:232,Hour:30}  
//		respOTPObject.put("responseCode", "LOCPO-200");
//		respOTPObject.put("amount", "232");
//		respOTPObject.put("Hour", "30");
		
		//for user agreed to pay post payment and usage is excess {responseCode:"LOCPAEX-200",amount:232,Hour:2,eamount:32,EXHour:30}
		respOTPObject.put("responseCode", "LOCPAEX-200");
		respOTPObject.put("eamount", "2000");
		respOTPObject.put("EXHour", "135");
		respOTPObject.put("amount", "2400");
		respOTPObject.put("Hour", "150");
		
		//if everything is fine   {responseCode:"LOCKRS-200"}
//		respOTPObject.put("responseCode", "LOCKRS-200");
		
		
		writer.println(respOTPObject.toString());
		writer.flush();
		writer.close();
		
	}

}
