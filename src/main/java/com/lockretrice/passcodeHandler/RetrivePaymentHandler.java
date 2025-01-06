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

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

/**
 * Servlet implementation class RetrivePaymentHandler
 */

@WebServlet("/RetrivePaymentHandler")
public class RetrivePaymentHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RetrivePaymentHandler() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// System.out.println("inside retrive post");
		
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		// System.out.println("inside post");
		
		String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(Collectors.joining("/"));
		JSONObject reqObj = new JSONObject(jsonBody);
		
		
		String amount = reqObj.get("amount").toString();
		// System.out.println(reqObj);
		
		JSONObject respObj =new JSONObject();
		String orderId = generateOrderId(amount);
		String orderArr[] = orderId.split(",");
		if (orderArr.length > 0) {
			respObj.put("responseCode", "SUCC-200");
			respObj.put("orderId", orderArr[0]);
			respObj.put("amount", orderArr[1]);
		} else {
			respObj.put("responseCode", "FAIL-200");
		}
		
		writer.println(respObj.toString());
		writer.flush();
		writer.close();
		
	}
	
	// function to generate orderId razor pay
	public String generateOrderId(String amount) {
		String orderId= null;
		
		int amountInt = Integer.parseInt(amount)*100;
		// System.out.println("tot amount payable  "+amountInt);
		try {
			
		RazorpayClient razorpay = new RazorpayClient("rzp_test_kF1NdHUm47R7R4", "qy48Nhzq72txUptAWkQrMEqy");

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
