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

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Servlet implementation class PasscodeHandler
 */
@WebServlet("/PasscodeHandler")
public class PasscodeHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PasscodeHandler() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("inside get");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		System.out.println("inside post");
		
		JSONObject respOTPObject =new JSONObject();
		
		String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(Collectors.joining("/"));
		JSONObject reqObj = new JSONObject(jsonBody);
		
		System.out.println(reqObj);
		JSONArray jsonArr = new JSONArray("[L1,S2]");
		
		// for invalid mobile number
//		respOTPObject.put("responseCode", "INMNO-201");
		
		// for invalid passcode
//		respOTPObject.put("responseCode", "INPAC-201");
		
		//good to go
		respOTPObject.put("responseCode", "RESUC-200");
	    respOTPObject.put("LOCKNO", jsonArr);
		respOTPObject.put("terminalID", "98921833");
		
		writer.println(respOTPObject.toString());
		writer.flush();
		writer.close();
		
	}

}
