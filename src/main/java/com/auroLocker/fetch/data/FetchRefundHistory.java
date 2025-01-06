package com.auroLocker.fetch.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import com.auro.beans.RazorpayAmountRefund;
import com.auro.hibernateUtilities.HibernateUtils;

/**
 * Servlet implementation class FetchRefundHistory
 */

@WebServlet("/FetchRefundHistory")
public class FetchRefundHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FetchRefundHistory() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
//		System.out.println("here inside FetchRefundHistory");
		
		PrintWriter writer = response.getWriter();
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, DELETE, OPTIONS");
		response.setCharacterEncoding("UTF-8");
		
		Session session = HibernateUtils.getSession();
		
		String requLine = new BufferedReader(new InputStreamReader(request.getInputStream())).lines()
				.collect(Collectors.joining("/"));
		
		JSONObject requestObj = new JSONObject(requLine);
		
		JSONArray slno = new JSONArray();
		JSONArray amount = new JSONArray();
		JSONArray mobileNumber = new JSONArray();
		JSONArray userName = new JSONArray();
		JSONArray lockerNumber = new JSONArray();
		JSONArray terminalId = new JSONArray();
		JSONArray dateOfPayment = new JSONArray();
		JSONArray dateOfRefund = new JSONArray();
		
		JSONObject responseObj = new JSONObject();
		
		String packetType = requestObj.getString("PacketType");
		
		session.beginTransaction();
		
		if (packetType.equalsIgnoreCase("allrefhist")) {
			
			String hql = "from RazorpayAmountRefund";
			
			List<RazorpayAmountRefund> allRefundList = session.createQuery(hql).getResultList();
			
			if (!allRefundList.isEmpty()) {
				
				for (RazorpayAmountRefund razorpayRefund : allRefundList) {
					slno.put(razorpayRefund.getSlno());
					amount.put(razorpayRefund.getAmount()/100);
					userName.put(razorpayRefund.getUserName());
					mobileNumber.put(razorpayRefund.getMobileNumber());
					lockerNumber.put(razorpayRefund.getLockerNo());
					terminalId.put(razorpayRefund.getTerminalId());
					dateOfPayment.put(razorpayRefund.getDateOfPayment());
					dateOfRefund.put(razorpayRefund.getDateOfRefund());
				}
			}
			
			
		} else if(packetType.equalsIgnoreCase("refhistbydate")) {
			
			Date requestedDate = Date.valueOf(requestObj.getString("reqdate"));
			
			String hql = "from RazorpayAmountRefund where dateOfRefund=:dateofrefund";
			List<RazorpayAmountRefund> razorpayRefundByDate = session.createQuery(hql).setParameter("dateofrefund", requestedDate)
					.getResultList();
			if (!razorpayRefundByDate.isEmpty()) {
				
				for (RazorpayAmountRefund refundByDate : razorpayRefundByDate) {
					slno.put(refundByDate.getSlno());
					amount.put(refundByDate.getAmount()/100);
					userName.put(refundByDate.getUserName());
					mobileNumber.put(refundByDate.getMobileNumber());
					lockerNumber.put(refundByDate.getLockerNo());
					terminalId.put(refundByDate.getTerminalId());
					dateOfPayment.put(refundByDate.getDateOfPayment());
					dateOfRefund.put(refundByDate.getDateOfRefund());
				}
				
			}	
		}
		
		if (slno.length() > 0) {
			responseObj.put("slno", slno);
			responseObj.put("amount", amount);
			responseObj.put("userName", userName);
			responseObj.put("mobileNumber", mobileNumber);
			responseObj.put("lockerNamber", lockerNumber);
			responseObj.put("terminalId", terminalId);
			responseObj.put("dateOfPayment", dateOfPayment);
			responseObj.put("dateOfRefund", dateOfRefund);
		} else {
			responseObj.put("responseCode", "none");
		}
		
		writer.append(responseObj.toString());
		writer.flush();
		writer.close();
		session.close();
		
	}
	
	

}
