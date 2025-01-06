package com.locker.operations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import com.auro.beans.TransactionHistory;
import com.auro.hibernateUtilities.HibernateUtils;
import com.locks.gloablVariable.GlobalVariable;

/**
 * Servlet implementation class FetchPartialRetrieveDetails
 */
@WebServlet("/FetchPartialRetrieveDetails")
public class FetchPartialRetrieveDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public FetchPartialRetrieveDetails() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String respOrigin = request.getHeader("Origin");
		response.setHeader("Access-Control-Allow-Origin", respOrigin);
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, UPDATE, OPTIONS, DELETE");
//		response.setHeader("Access-Control-Allow-Headers",
//				"Content-Type, X-Content-Type-Options, X-Frame-Options, Referrer-Policy");
		response.setHeader("Access-Control-Allow-Headers", "*");

		response.setCharacterEncoding("UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);

		PrintWriter writer = response.getWriter();

		String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines()
				.collect(Collectors.joining("/"));

		JSONObject requestedObject = new JSONObject(jsonBody);
		Session session = HibernateUtils.getSession();

		String packetType = requestedObject.getString("PacketType");

		JSONObject respObject = new JSONObject();
		JSONArray slno = new JSONArray();
		JSONArray customerNAme = new JSONArray();
		JSONArray mobileNo = new JSONArray();
		JSONArray amount = new JSONArray();
		JSONArray partRetAmount = new JSONArray();
		JSONArray terminalID = new JSONArray();
		JSONArray dateOfOpen = new JSONArray();
		JSONArray dateOfClose = new JSONArray();
		JSONArray timeOfOpen = new JSONArray();
		JSONArray timeOfClose = new JSONArray();
		JSONArray lockerNo = new JSONArray();

		// to fetch all the part retrieve details for all terminalID
		if (packetType.equals("GET-PARTDETAILS")) {

			Date fromDate = Date.valueOf(requestedObject.getString("fromDate"));
			Date toDate = Date.valueOf(requestedObject.getString("toDate"));

			try {

//				System.out.println("inside get part details for all terminal id's ");

//				System.out.println(fromDate);
//				System.out.println(toDate);

				session.beginTransaction();

				String getTHHql = "FROM TransactionHistory WHERE date_of_open BETWEEN :fromDate AND :toDate AND partretamount > :partAmount";

				ArrayList<TransactionHistory> transHistList = (ArrayList<TransactionHistory>) session
						.createQuery(getTHHql, TransactionHistory.class).setParameter("fromDate", fromDate)
						.setParameter("toDate", toDate).setParameter("partAmount", 0).getResultList();

				if (!transHistList.isEmpty()) {

					for (TransactionHistory transactionHistory : transHistList) {

						slno.put(transactionHistory.getSlno());
						customerNAme.put(transactionHistory.getCustomerName());
						mobileNo.put(transactionHistory.getMobileNo());
						amount.put(transactionHistory.getAmount());
						partRetAmount.put(transactionHistory.getPartretamount());
						terminalID.put(transactionHistory.getTerminalid());
						dateOfOpen.put(transactionHistory.getDate_of_open());
						dateOfClose.put(transactionHistory.getClosing_date());
						timeOfOpen.put(transactionHistory.getTime_of_open());
						timeOfClose.put(transactionHistory.getClosing_time());
						lockerNo.put(transactionHistory.getLockNo());

					}

					respObject.put("responseCode", "partData-200");
					respObject.put("slno", slno);
					respObject.put("amount", amount);
					respObject.put("customerName", customerNAme);
					respObject.put("mobileNo", mobileNo);
					respObject.put("partRetAmount", partRetAmount);
					respObject.put("terminalID", terminalID);
					respObject.put("dateOfOpen", dateOfOpen);
					respObject.put("dateOfClose", dateOfClose);
					respObject.put("timeOfOpen", timeOfOpen);
					respObject.put("timeOfClose", timeOfClose);
					respObject.put("lockerNo", lockerNo);

				} else {
					respObject.put("responseCode", "partData-204");
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				respObject.put("responseCode", "partData-500");

			} finally {
				session.close();
			}

		} else if (packetType.equals("GETTERMWISE-PARTDETAILS")) {
			// to fetch all the part retrieve details for the terminalIds selected from the
			// user end

			Date fromDate = Date.valueOf(requestedObject.getString("fromDate"));
			Date toDate = Date.valueOf(requestedObject.getString("toDate"));
			String selectedTerminalId = requestedObject.get("terminalID").toString().replace("[", "").replace("]", "")
					.replace("\"", "");

			Collection<String> termIdList = new ArrayList<String>(Arrays.asList(selectedTerminalId.split(",")));

			try {

//				System.out.println("inside get part details for selected terminal id's ");

				session.beginTransaction();

				String getTHHql = "FROM TransactionHistory WHERE date_of_open BETWEEN :fromDate AND :toDate AND terminalid IN (:termIdList) AND partretamount > :partAmount";

				ArrayList<TransactionHistory> transHistList = (ArrayList<TransactionHistory>) session
						.createQuery(getTHHql, TransactionHistory.class).setParameter("fromDate", fromDate)
						.setParameter("toDate", toDate).setParameterList("termIdList", termIdList)
						.setParameter("partAmount", 0).getResultList();

				if (!transHistList.isEmpty()) {

					for (TransactionHistory transactionHistory : transHistList) {

						slno.put(transactionHistory.getSlno());
						customerNAme.put(transactionHistory.getCustomerName());
						mobileNo.put(transactionHistory.getMobileNo());
						amount.put(transactionHistory.getAmount());
						partRetAmount.put(transactionHistory.getPartretamount());
						terminalID.put(transactionHistory.getTerminalid());
						dateOfOpen.put(transactionHistory.getDate_of_open());
						dateOfClose.put(transactionHistory.getClosing_date());
						timeOfOpen.put(transactionHistory.getTime_of_open());
						timeOfClose.put(transactionHistory.getClosing_time());

					}

					respObject.put("responseCode", "partData-200");
					respObject.put("slno", slno);
					respObject.put("amount", amount);
					respObject.put("customerName", customerNAme);
					respObject.put("mobileNo", mobileNo);
					respObject.put("partRetAmount", partRetAmount);
					respObject.put("terminalID", terminalID);
					respObject.put("dateOfOpen", dateOfOpen);
					respObject.put("dateOfClose", dateOfClose);
					respObject.put("timeOfOpen", timeOfOpen);
					respObject.put("timeOfClose", timeOfClose);
					respObject.put("lockerNo", lockerNo);

				} else {
					respObject.put("responseCode", "partData-204");
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				respObject.put("responseCode", "partData-500");

			} finally {
				session.close();
			}

		}

		writer.append(respObject.toString());
		writer.flush();
		writer.close();

	}

}
