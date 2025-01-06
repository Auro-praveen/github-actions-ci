package com.auto.user.logdetails;

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

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONObject;

import com.auro.beans.LogDetails;
import com.auro.hibernateUtilities.HibernateUtils;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

/**
 * @author Praveen
 */

@WebServlet("/UserLogDetails")
public class UserLogDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserLogDetails() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// System.out.println("inside post");
		
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE");
		response.setCharacterEncoding("UTF-8");
		
		String ip = request.getRemoteAddr();
		// System.out.println("ip addr : "+ip);
		
		PrintWriter writer = response.getWriter();
		
		JSONObject respObj = new JSONObject();
		Session session = HibernateUtils.getSession();
		LogDetails logDetails = new LogDetails();
		
		try {
			String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(Collectors.joining("/"));
			JSONObject reqObj = new JSONObject(jsonBody);
			logDetails.setEventDate(reqObj.getString("date"));
			logDetails.setEventTime(reqObj.getString("time"));
			logDetails.setEventType(reqObj.getString("eventType"));
			logDetails.setRemarks(reqObj.getString("remarks"));
			logDetails.setUserName(reqObj.getString("username"));
			logDetails.setIpAddress(ip);
			
			Transaction transaction = session.beginTransaction();
			int n = (int) session.save(logDetails);
			transaction.commit();
		
			if (n>1) {
				respObj.put("status", "success");
			} else {
				respObj.put("status", "failed");
			}
//			respObj.put("status", "failed");
			
			// System.out.println(reqObj);
						
			writer.println(respObj.toString());
			writer.flush();
			writer.close();
			session.clear();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	

}
