package com.locks.payment.status;

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

import com.locks.gloablVariable.GlobalVariable;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

/**
 * @author Praveen
 */

@WebServlet("/PaymentStatusBefor")
public class PaymentStatusBefor extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PaymentStatusBefor() {
        super();
        // TODO Auto-generated constructor stub
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

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
		
		// System.out.println("resp obj : "+reqObj);
		
		String amount = reqObj.get("amount").toString();
		// System.out.println(amount);
		
		PrintWriter writer = response.getWriter();
		respOTPObject.put("status", "success");	
		String packetType = reqObj.get("PacketType").toString();
		// System.out.println(packetType);

		if( packetType.equalsIgnoreCase("stlockcnf")) {
			// System.out.println("inside first if");
			String orderId =generateOrderId(amount);
			// System.out.println("after processing data : ");
			if (orderId != null) {
				String orderDetails[] = orderId.split(",");
				
				respOTPObject.put("orderId", orderDetails[0]);
				respOTPObject.put("totAmount", orderDetails[1]);
				respOTPObject.put("responseCode", "LOCKAVS-200");
			} else {
				respOTPObject.put("responseCode", "LOCKAVS-200");
			}
		} else {
			respOTPObject.put("responseCode", "LOCKAVF-201");
		}

		writer.println(respOTPObject.toString());
		writer.flush();
		writer.close();
	}
	
	// function to generate orderId razor pay
	public String generateOrderId(String amount) {
		String orderId= null;
		
		int amountInt = Integer.parseInt(amount)*100;
		// System.out.println("tot amount payable  "+amountInt);
		try {
			
		RazorpayClient razorpay = new RazorpayClient(GlobalVariable.razorpayKeyId , GlobalVariable.razorpaySecretKey);

		JSONObject orderRequest = new JSONObject();
		orderRequest.put("amount",amountInt);
		orderRequest.put("currency","INR");
		orderRequest.put("receipt", "receipt_101");
		JSONObject notes = new JSONObject();
		notes.put("notes_key_1","Tea, Earl Grey, Hot");
		notes.put("notes_key_1","Tea, Earl Grey, Hot");
		orderRequest.put("notes",notes);

		
		Order order = razorpay.orders.create(orderRequest);
		
//		// System.out.println(order.get("id"));
//		// System.out.println(order);
		
		orderId = order.get("id");

		} catch (RazorpayException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (orderId != null) {
			// System.out.println("output");
			return orderId+","+amountInt;
		} else {
			// System.out.println("none");
			return null;
		}
	}
}
