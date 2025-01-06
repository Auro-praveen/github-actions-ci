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

import com.auro.beans.TransactionFailedDetails;
import com.auro.beans.paygatorderid_details;
import com.auro.hibernateUtilities.HibernateUtils;

/**
 * Servlet implementation class FetchFailedTransaction
 * @author Praveen
 */

@WebServlet("/FetchFailedTransaction")
public class FetchFailedTransaction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FetchFailedTransaction() {
        super();
        // TODO Auto-generated constructor stub
    }
  

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
		
		JSONArray custName = new JSONArray();
		JSONArray mobileNumber = new JSONArray();
		JSONArray slno = new JSONArray();
		JSONArray terminalId = new JSONArray();
		JSONArray amount = new JSONArray();
		JSONArray dateOFOpen = new JSONArray();
		JSONArray timeOfOpen = new JSONArray();
		JSONArray transactionId = new JSONArray();
		JSONArray status = new JSONArray();
		JSONArray noOfHours = new JSONArray();
		JSONArray lockNo = new JSONArray();
		JSONArray itemStored = new JSONArray();
		JSONArray passcode = new JSONArray();
		JSONArray excessHour = new JSONArray();
		JSONArray excessAmount = new JSONArray();
		JSONArray balance = new JSONArray();
		
//		System.out.println("data : -- "+ obj);
		Date requestedDate = Date.valueOf(obj.getString("date"));
		
		JSONObject responseToServer = new JSONObject();
		
		if (typeOp.equalsIgnoreCase("ftrans")) {
			String hql = "from TransactionFailedDetails where date_of_open=:tdate";
//			String hql = "from TransactionFailedDetails";
			
			try {
				List<TransactionFailedDetails> failedTrans = session.createQuery(hql).setParameter("tdate", requestedDate).getResultList();
//				List<TransactionFailedDetails> failedTrans = session.createQuery(hql).getResultList();
				for (TransactionFailedDetails FailedTransactions : failedTrans) {
					custName.put(FailedTransactions.getCustomerName());
					mobileNumber.put(FailedTransactions.getMobileNo());
					slno.put(FailedTransactions.getSlno());
					terminalId.put(FailedTransactions.getTerminalid());
					balance.put(FailedTransactions.getBalance());
					amount.put(FailedTransactions.getAmount());
					dateOFOpen.put(FailedTransactions.getDate_of_open());
					timeOfOpen.put(FailedTransactions.getTime_of_open());
					transactionId.put(FailedTransactions.getTransactionID());		
					status.put(FailedTransactions.getStatus());
					noOfHours.put(FailedTransactions.getNo_of_hours());
					lockNo.put(FailedTransactions.getLockNo());
					itemStored.put(FailedTransactions.getItemsStored());
					passcode.put(FailedTransactions.getPasscode());
					excessAmount.put(FailedTransactions.getExcess_amount());
					excessHour.put(FailedTransactions.getExcess_hours());		

				}
				
				if (slno.length() > 0) {
					
					responseToServer.put("slno", slno);
					responseToServer.put("customerName", custName);
					responseToServer.put("mobileNumber", mobileNumber);
					responseToServer.put("terminalID", terminalId);
					responseToServer.put("balance", balance);
					responseToServer.put("amount", amount);
					responseToServer.put("dateOfOpen", dateOFOpen);
					responseToServer.put("timeOfOpen", timeOfOpen);
					responseToServer.put("transactionID", transactionId );
					responseToServer.put("noOfHours", noOfHours);
					responseToServer.put("lockerNo", lockNo);
					responseToServer.put("itemStored", itemStored );
					responseToServer.put("passcode", passcode);
					responseToServer.put("excessAmount", excessAmount );
					responseToServer.put("excessHour", excessHour);
					responseToServer.put("status", status);
					
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
