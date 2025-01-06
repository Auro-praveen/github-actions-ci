package com.auroLocker.fetch.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.Time;
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

import com.auro.beans.TransactionDetails;
import com.auro.beans.TransactionFailedDetails;
import com.auro.beans.TransactionHistory;
import com.auro.hibernateUtilities.HibernateUtils;

/**
 * Servlet implementation class FetchLossedCustomers
 */


@WebServlet("/FetchLossedCustomers")
public class FetchLossedCustomers extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FetchLossedCustomers() {
        super();
        // TODO Auto-generated constructor stub
    }
    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");
		
		String reqLine = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(Collectors.joining("/n"));
		JSONObject reqObj = new JSONObject(reqLine);
		Session session = HibernateUtils.getSession();
		PrintWriter writer = response.getWriter();
		
//		System.out.println("inside Lossed customers details :: "+ reqLine);
		
		String packetType = reqObj.getString("PacketType");
		Date requestedDate = Date.valueOf(reqObj.getString("date"));

		
		
		JSONArray slno = new JSONArray();
		JSONArray customerName = new JSONArray();
		JSONArray dateOfOpen = new JSONArray();
		JSONArray timeOfOpen = new JSONArray();
		JSONArray terminalID = new JSONArray();
		JSONArray noOfHours = new JSONArray();
		JSONArray amount = new JSONArray();
		JSONArray status = new JSONArray();
		JSONArray closingDate = new JSONArray();
		JSONArray closingTime = new JSONArray();
		JSONArray lockerNo = new JSONArray();
		JSONArray Passcode = new JSONArray();
		JSONArray mobileNo = new JSONArray();
		
		JSONObject responseObject = new JSONObject();
		
		try {
			
			if (packetType.equalsIgnoreCase("lossedcust")) {
				String hqlFailedTransaction = "from TransactionFailedDetails where date_of_open=:givenDate";
				String hqlTransactionDetails = "from TransactionDetails where mobileNo=:mobileNumber and date_of_open=:givenDate";
				String hqlTransactionHistory = "from TransactionHistory where mobileNo=:mobileNumber and date_of_open=:givenDate";
				List<TransactionFailedDetails> failedTransaction = session.createQuery(hqlFailedTransaction).
						setParameter("givenDate", requestedDate).getResultList();
				
				if (!failedTransaction.isEmpty()) {
					for (TransactionFailedDetails transactionFailedDetails : failedTransaction) {
						String mobileNumber = transactionFailedDetails.getMobileNo();
						
						List<TransactionDetails> transactionDet = session.createQuery(hqlTransactionDetails)
								.setParameter("mobileNumber", mobileNumber).setParameter("givenDate", requestedDate).getResultList();
						
//						if (!transactionDet.isEmpty()) {
//							for (TransactionDetails transactionDetails : transactionDet) {
//								slno.put(transactionDetails.getSlno());
//								customerName.put(transactionDetails.getCustomerName());
//								dateOfOpen.put(transactionDetails.getDate_of_open());
//								timeOfOpen.put(transactionDetails.getTime_of_open());
//								terminalID.put(transactionDetails.getTerminalid());
//								noOfHours.put(transactionDetails.getNo_of_hours());
//								amount.put(transactionDetails.getAmount());
//								status.put(transactionDetails.getStatus());
//								closingDate.put(transactionDetails.getClosing_date());
//								closingTime.put(transactionDetails.getClosing_time());
//								lockerNo.put(transactionDetails.getLockNo());
//								Passcode.put(transactionDetails.getPasscode());
//								mobileNo.put(transactionDetails.getMobileNo());
//							}
//						}else {
//							List<TransactionHistory> transactionHist = session.createQuery(hqlTransactionDetails)
//									.setParameter("mobileNumber", mobileNumber).setParameter("givenDate", requestedDate).getResultList();
//							
//							if (!transactionHist.isEmpty()) {
//								for (TransactionHistory transactionHistory : transactionHist) {
//									slno.put(transactionHistory.getSlno());
//									customerName.put(transactionHistory.getCustomerName());
//									dateOfOpen.put(transactionHistory.getDate_of_open());
//									timeOfOpen.put(transactionHistory.getTime_of_open());
//									terminalID.put(transactionHistory.getTerminalid());
//									noOfHours.put(transactionHistory.getNo_of_hours());
//									amount.put(transactionHistory.getAmount());
//									status.put(transactionHistory.getStatus());
//									closingDate.put(transactionHistory.getClosing_date());
//									closingTime.put(transactionHistory.getClosing_time());
//									lockerNo.put(transactionHistory.getLockNo());
//									Passcode.put(transactionHistory.getPasscode());
//									mobileNo.put(transactionHistory.getMobileNo());
//								}
//							} else {
//								slno.put(transactionFailedDetails.getSlno());
//								customerName.put(transactionFailedDetails.getCustomerName());
//								dateOfOpen.put(transactionFailedDetails.getDate_of_open());
//								timeOfOpen.put(transactionFailedDetails.getTime_of_open());
//								terminalID.put(transactionFailedDetails.getTerminalid());
//								noOfHours.put(transactionFailedDetails.getNo_of_hours());
//								amount.put(transactionFailedDetails.getAmount());
//								status.put(transactionFailedDetails.getStatus());
//								closingDate.put(transactionFailedDetails.getClosing_date());
//								closingTime.put(transactionFailedDetails.getClosing_time());
//								lockerNo.put(transactionFailedDetails.getLockNo());
//								Passcode.put(transactionFailedDetails.getPasscode());
//								mobileNo.put(transactionFailedDetails.getMobileNo());
//							}
//						}
						
						if (transactionDet.isEmpty()) {
							List<TransactionHistory> transactionHist = session.createQuery(hqlTransactionHistory)
									.setParameter("mobileNumber", mobileNumber).setParameter("givenDate", requestedDate).getResultList();
							
							if (transactionHist.isEmpty()) {
								slno.put(transactionFailedDetails.getSlno());
								customerName.put(transactionFailedDetails.getCustomerName());
								dateOfOpen.put(transactionFailedDetails.getDate_of_open());
								timeOfOpen.put(transactionFailedDetails.getTime_of_open());
								terminalID.put(transactionFailedDetails.getTerminalid());
								noOfHours.put(transactionFailedDetails.getNo_of_hours());
								amount.put(transactionFailedDetails.getAmount());
								status.put(transactionFailedDetails.getStatus());
								closingDate.put(transactionFailedDetails.getClosing_date());
								closingTime.put(transactionFailedDetails.getClosing_time());
								lockerNo.put(transactionFailedDetails.getLockNo());
								Passcode.put(transactionFailedDetails.getPasscode());
								mobileNo.put(transactionFailedDetails.getMobileNo());
							}
						}
					}
				}
				
			}	
			
			responseObject.put("slno", slno);
			responseObject.put("customerName", customerName);
			responseObject.put("dateOfOpen", dateOfOpen);
			responseObject.put("timeOfOpen", timeOfOpen);
			responseObject.put("terminalId", terminalID);
			responseObject.put("noOfHours", noOfHours);
			responseObject.put("amount", amount);
			responseObject.put("status", status);
			responseObject.put("closingDate", closingDate);
			responseObject.put("closingTime", closingTime);
			responseObject.put("lockerNo", lockerNo);
			responseObject.put("Passcode", Passcode);
			responseObject.put("MobileNo", mobileNo);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		writer.append(responseObject.toString());
		writer.flush();
		
		session.close();
		writer.close();

	}
}
