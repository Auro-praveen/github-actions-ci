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

import com.auro.beans.paygatorderid_details;
import com.auro.hibernateUtilities.HibernateUtils;


/**
 * Servlet implementation class FetchPayOrderIdDetails
 */

@WebServlet("/FetchPayOrderIdDetails")
public class FetchPayOrderIdDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FetchPayOrderIdDetails() {
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
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");
		
		PrintWriter writer = response.getWriter();
		Session session = HibernateUtils.getSession();
		String dataResp = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(Collectors.joining("/n"));
		
//		System.out.println(dataResp);
		JSONObject obj = new JSONObject(dataResp);
		String typeOp = obj.getString("PacketType");
		
		JSONArray slNo = new JSONArray();
		JSONArray orderId = new JSONArray();
		JSONArray tranDate = new JSONArray();
		JSONArray tranTime = new JSONArray();
		JSONArray balance = new JSONArray();
		JSONArray lockerNo = new JSONArray();
		JSONArray mobileNo = new JSONArray();
		JSONArray paygatwVarStatus = new JSONArray();
		JSONArray terminalId = new JSONArray();
		JSONArray transactionType = new JSONArray();
		
//		System.out.println("data : -- "+ obj);
		Date requestedDate = Date.valueOf(obj.getString("date"));
		
		JSONObject responseToServer = new JSONObject();
		
		if (typeOp.equalsIgnoreCase("ftrans")) {
			String hql = "from paygatorderid_details where traDate=:tdate";
			
			try {
				List<paygatorderid_details> failedTrans = session.createQuery(hql).setParameter("tdate", requestedDate).getResultList();
				for (paygatorderid_details paygatorderid_details : failedTrans) {
					slNo.put(paygatorderid_details.getSlno());
					orderId.put(paygatorderid_details.getOrderID());
					tranDate.put(paygatorderid_details.getTraDate());
					tranTime.put(paygatorderid_details.getTraTime());
					balance.put(paygatorderid_details.getBalance());
					lockerNo.put(paygatorderid_details.getLockNo());
					mobileNo.put(paygatorderid_details.getMobileNo());
					paygatwVarStatus.put(paygatorderid_details.getPaygatwVerStatus());
					terminalId.put(paygatorderid_details.getTerminaLID());		
					transactionType.put(paygatorderid_details.getTransactionType());
				}
				
				if (slNo.length() > 0) {
					
					responseToServer.put("slno", slNo);
					responseToServer.put("orderId", orderId);
					responseToServer.put("tranDate", tranDate);
					responseToServer.put("tranTime", tranTime);
					responseToServer.put("balance", balance);
					responseToServer.put("lockerNo", lockerNo);
					responseToServer.put("mobileNo", mobileNo);
					responseToServer.put("paygatvarstatus", paygatwVarStatus);
					responseToServer.put("terminalId", terminalId );
					responseToServer.put("tranType", transactionType );
				} else {
					responseToServer.put("responseCode", "ftd-202");
				}
			} catch (Exception e) {
				// TODO: handle exception
//				responseToServer.put("responseCode", "ftd-202");
				e.printStackTrace();
				session.close();
				writer.close();
			}
			
			writer.append(responseToServer.toString());
			writer.flush();
			writer.close();
			session.close();
		}
		
		
	}
}
