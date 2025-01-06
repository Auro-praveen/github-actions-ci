package com.auroLocker.fetch.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 * Servlet implementation class FetchToXlSheet
 * 
 * @author Praveen
 * 
 *         Here is the all calculation part to the excel-sheet file xl-sheet
 *         downloading data in the format of array of object is returned to the
 *         viewing part
 * 
 */

@WebServlet("/FetchToXlSheet")
public class FetchToXlSheet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final double CGSTpercent = 9.0;
	private static final double SGSTpercent = 9.0;
	private static DecimalFormat decimalFormat = new DecimalFormat("#.00");

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FetchToXlSheet() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE");
		response.setCharacterEncoding("UTF-8");

		PrintWriter writer = response.getWriter();
		Session session = HibernateUtils.getSession();
		session.beginTransaction();

		String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines()
				.collect(Collectors.joining("/"));

//		System.out.println(jsonBody);
		JSONObject requestedObject = new JSONObject(jsonBody);

		String packetType = requestedObject.getString("PacketType");

		JSONObject responseObject = new JSONObject();

		List<String> terminalIDList = getAllTerminalIds(session);

		if (packetType.equalsIgnoreCase("getforxlsxtermwise")) {

			Date fromDate = Date.valueOf(requestedObject.getString("fromDate"));
			Date toDate = Date.valueOf(requestedObject.getString("toDate"));
			String selectedTerminalId = requestedObject.get("terminalID").toString().replace("[", "").replace("]", "")
					.replace("\"", "");

			Collection<String> termIdList = new ArrayList<String>(Arrays.asList(selectedTerminalId.split(",")));

//			System.out.println(termIdList);
//			System.out.println(selectedTerminalId);

			JSONArray arrOfTransctionHistObj = new JSONArray();

			String getTHHql = "FROM TransactionHistory WHERE date_of_open BETWEEN :fromDate AND :toDate AND terminalid IN (:termIdList)";

			ArrayList<TransactionHistory> transHistList = (ArrayList<TransactionHistory>) session
					.createQuery(getTHHql, TransactionHistory.class).setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).setParameterList("termIdList", termIdList).getResultList();

			if (!transHistList.isEmpty()) {

				double totCGST_Amount = 0;
				double totSGST_Amount = 0;
				double totAmountWithout_GST = 0;
				double totAmonutWith_GST = 0;
				double totAmount = 0;
				double totExcessAmount = 0;

				for (TransactionHistory transactionHistory : transHistList) {

					JSONObject singleTransactionjsn = new JSONObject();

					singleTransactionjsn.put("slno", transactionHistory.getSlno());
					singleTransactionjsn.put("MobileNo", transactionHistory.getMobileNo());
					singleTransactionjsn.put("transactionId", transactionHistory.getTransactionID());
					singleTransactionjsn.put("amount", transactionHistory.getAmount());
					singleTransactionjsn.put("excess_amount", transactionHistory.getExcess_amount());
					singleTransactionjsn.put("excess_hour", transactionHistory.getExcess_hours());
					singleTransactionjsn.put("remarks", transactionHistory.getRemark());
					singleTransactionjsn.put("transactionType", transactionHistory.getStatus());
					singleTransactionjsn.put("dateOfTransaction", transactionHistory.getDate_of_open());
					singleTransactionjsn.put("timeOfTransaction", transactionHistory.getTime_of_open());
					singleTransactionjsn.put("lockers", transactionHistory.getLockNo());
					singleTransactionjsn.put("custName", transactionHistory.getCustomerName());
					singleTransactionjsn.put("noOfHours", transactionHistory.getNo_of_hours());
					singleTransactionjsn.put("terminalID", transactionHistory.getTerminalid());
					singleTransactionjsn.put("itemStored", transactionHistory.getItemsStored());
					singleTransactionjsn.put("balance", transactionHistory.getBalance());
					singleTransactionjsn.put("passcode", transactionHistory.getPasscode());
					singleTransactionjsn.put("closing_date", transactionHistory.getClosing_date());
					singleTransactionjsn.put("closingTime", transactionHistory.getClosing_time());
					singleTransactionjsn.put("storeOrderId", transactionHistory.getPaygatewaystoreOrderID());
					singleTransactionjsn.put("excessOrderId", transactionHistory.getPaygatewayexcpayorderTRID());
					singleTransactionjsn.put("storTransactionId", transactionHistory.getPaygatewayPaymenstoreTRID());
					singleTransactionjsn.put("excessTransactionId", transactionHistory.getPaygatewayexcpayTRID());

					// praveen october 05-2023

//					double cgstCalculatedAmount = GlobalVariable.calulcateGstMethod(
//							(int) (transactionHistory.getAmount() / 100 + transactionHistory.getExcess_amount() / 100 + transactionHistory.getBalance() / 100),
//							"CGST", transactionHistory.getTerminalid());
//					double sgstCalculatedAmounnt = GlobalVariable.calulcateGstMethod(
//							(int) (transactionHistory.getAmount() / 100 + transactionHistory.getExcess_amount() / 100 + transactionHistory.getBalance() / 100),
//							"SGST", transactionHistory.getTerminalid());
//					double totDoubleAmount_withoutGST = Double.parseDouble(decimalFormat
//							.format((transactionHistory.getAmount() / 100 + transactionHistory.getExcess_amount() / 100 + transactionHistory.getBalance() / 100)
//									- cgstCalculatedAmount - sgstCalculatedAmounnt));

					// praveen changed here june 03-2024

					double cgstCalculatedAmount = GlobalVariable.calulcateGstMethod(
							(int) (transactionHistory.getAmount() / 100
									+ (double) transactionHistory.getExcess_amount() / 100
									+ (double) transactionHistory.getBalance() / 100
									+ (int) transactionHistory.getPartretamount() / 100),
							"CGST", transactionHistory.getTerminalid());
					double sgstCalculatedAmounnt = GlobalVariable.calulcateGstMethod(
							(int) (transactionHistory.getAmount() / 100
									+ (double) transactionHistory.getExcess_amount() / 100
									+ (double) transactionHistory.getBalance() / 100
									+ (int) transactionHistory.getPartretamount() / 100),
							"SGST", transactionHistory.getTerminalid());
					double totDoubleAmount_withoutGST = Double
							.parseDouble(decimalFormat.format(((int) transactionHistory.getAmount() / 100
									+ (double) transactionHistory.getExcess_amount() / 100
									+ (double) transactionHistory.getBalance() / 100
									+ (int) transactionHistory.getPartretamount() / 100 + (int) cgstCalculatedAmount
									+ (int) sgstCalculatedAmounnt)));

					singleTransactionjsn.put("TotalAmountWithout_GST", totDoubleAmount_withoutGST);
					singleTransactionjsn.put("CGST", cgstCalculatedAmount);
					singleTransactionjsn.put("SGST", sgstCalculatedAmounnt);
					singleTransactionjsn.put("TotalAmountWith_GST",
							Math.round(((int) transactionHistory.getAmount() / 100
									+ (double) transactionHistory.getExcess_amount() / 100
									+ (double) transactionHistory.getBalance() / 100
									+ (int) transactionHistory.getPartretamount() / 100)));

					arrOfTransctionHistObj.put(singleTransactionjsn);

					{
						totCGST_Amount += cgstCalculatedAmount;
						totSGST_Amount += sgstCalculatedAmounnt;
						totAmountWithout_GST += totDoubleAmount_withoutGST;
						totAmonutWith_GST += Double
								.parseDouble(decimalFormat.format(((int) transactionHistory.getAmount() / 100
										+ (double) transactionHistory.getExcess_amount() / 100
										+ (double) transactionHistory.getBalance() / 100
										+ (int) transactionHistory.getPartretamount() / 100)));
						;
						totAmount += transactionHistory.getAmount() / 100;
						totExcessAmount += transactionHistory.getExcess_amount() / 100;
					}

				}

				if (arrOfTransctionHistObj.length() > 0) {

					for (int i = 0; i < 2; i++) {
						JSONObject totalCalculationAmount = new JSONObject();
						if (i == 0) {
							totalCalculationAmount.put("slno", "");
							totalCalculationAmount.put("MobileNo", "");
							totalCalculationAmount.put("transactionId", "");
							totalCalculationAmount.put("amount", "");
							totalCalculationAmount.put("remarks", "");
							totalCalculationAmount.put("transactionType", "");
							totalCalculationAmount.put("dateOfTransaction", "");
							totalCalculationAmount.put("timeOfTransaction", "");
							totalCalculationAmount.put("lockers", "");
							totalCalculationAmount.put("custName", "");
							totalCalculationAmount.put("noOfHours", "");
							totalCalculationAmount.put("terminalID", "");
							totalCalculationAmount.put("itemStored", "");
							totalCalculationAmount.put("excess_amount", "");
							totalCalculationAmount.put("excess_hour", "");
							totalCalculationAmount.put("balance", "");
							totalCalculationAmount.put("passcode", "");
							totalCalculationAmount.put("closing_date", "");
							totalCalculationAmount.put("closingTime", "");
							totalCalculationAmount.put("storeOrderId", "");
							totalCalculationAmount.put("excessOrderId", "");
							totalCalculationAmount.put("storTransactionId", "");
							totalCalculationAmount.put("excessTransactionId", "");
							totalCalculationAmount.put("TotalAmountWithout_GST", "");
							totalCalculationAmount.put("CGST", "");
							totalCalculationAmount.put("SGST", "");
							totalCalculationAmount.put("TotalAmountWith_GST", "");
						} else {
							totalCalculationAmount.put("slno", "-");
							totalCalculationAmount.put("MobileNo", "-");
							totalCalculationAmount.put("transactionId", "-");
							totalCalculationAmount.put("amount", totAmount);
							totalCalculationAmount.put("remarks", "-");
							totalCalculationAmount.put("transactionType", "-");
							totalCalculationAmount.put("dateOfTransaction", "-");
							totalCalculationAmount.put("timeOfTransaction", "-");
							totalCalculationAmount.put("lockers", "-");
							totalCalculationAmount.put("custName", "-");
							totalCalculationAmount.put("noOfHours", "-");
							totalCalculationAmount.put("terminalID", "-");
							totalCalculationAmount.put("itemStored", "-");
							totalCalculationAmount.put("excess_amount", totExcessAmount);
							totalCalculationAmount.put("excess_hour", "-");
							totalCalculationAmount.put("balance", "-");
							totalCalculationAmount.put("passcode", "-");
							totalCalculationAmount.put("closing_date", "-");
							totalCalculationAmount.put("closingTime", "-");
							totalCalculationAmount.put("storeOrderId", "-");
							totalCalculationAmount.put("excessOrderId", "-");
							totalCalculationAmount.put("storTransactionId", "-");
							totalCalculationAmount.put("excessTransactionId", "-");

							totalCalculationAmount.put("TotalAmountWithout_GST", totAmountWithout_GST);
							totalCalculationAmount.put("CGST", totCGST_Amount);
							totalCalculationAmount.put("SGST", totSGST_Amount);
							totalCalculationAmount.put("TotalAmountWith_GST", totAmonutWith_GST);
						}

						arrOfTransctionHistObj.put(totalCalculationAmount);

					}

				}

				responseObject.put("responseCode", "tddata-200");
				responseObject.put("jsontoxldata", arrOfTransctionHistObj);

			} else {
				responseObject.put("responseCode", "tddata-400");
			}

		} else if (packetType.equalsIgnoreCase("getforxlsxall")) {

//			System.out.println("got request inside");

			Date fromDate = Date.valueOf(requestedObject.getString("fromDate"));
			Date toDate = Date.valueOf(requestedObject.getString("toDate"));

//			String selectedTerminalId = requestedObject.get("terminalID").toString().replace("[", "")
//					.replace("]", "").replace("\"", "");
//			
//			Collection<String> termIdList = new ArrayList<String>(
//					Arrays.asList(selectedTerminalId.split(","))
//					);

			JSONArray arrOfTransctionHistObj = new JSONArray();

			String getTHHql = "FROM TransactionHistory WHERE date_of_open BETWEEN :fromDate AND :toDate";
			ArrayList<TransactionHistory> transHistList = (ArrayList<TransactionHistory>) session
					.createQuery(getTHHql, TransactionHistory.class).setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).getResultList();

			if (!transHistList.isEmpty()) {

				for (TransactionHistory transactionHistory : transHistList) {

					JSONObject singleTransactionjsn = new JSONObject();

					singleTransactionjsn.put("slno", transactionHistory.getSlno());
					singleTransactionjsn.put("MobileNo", transactionHistory.getMobileNo());
					singleTransactionjsn.put("transactionId", transactionHistory.getTransactionID());
					singleTransactionjsn.put("amount", transactionHistory.getAmount());
					singleTransactionjsn.put("remarks", transactionHistory.getRemark());
					singleTransactionjsn.put("transactionType", transactionHistory.getStatus());
					singleTransactionjsn.put("dateOfTransaction", transactionHistory.getDate_of_open());
					singleTransactionjsn.put("timeOfTransaction", transactionHistory.getTime_of_open());
					singleTransactionjsn.put("lockers", transactionHistory.getLockNo());
					singleTransactionjsn.put("custName", transactionHistory.getCustomerName());
					singleTransactionjsn.put("noOfHours", transactionHistory.getNo_of_hours());
					singleTransactionjsn.put("terminalID", transactionHistory.getTerminalid());
					singleTransactionjsn.put("itemStored", transactionHistory.getItemsStored());
					singleTransactionjsn.put("excess_amount", transactionHistory.getExcess_amount());
					singleTransactionjsn.put("excess_hour", transactionHistory.getExcess_hours());
					singleTransactionjsn.put("balance", transactionHistory.getBalance());
					singleTransactionjsn.put("passcode", transactionHistory.getPasscode());
					singleTransactionjsn.put("closing_date", transactionHistory.getClosing_date());
					singleTransactionjsn.put("closingTime", transactionHistory.getClosing_time());
					singleTransactionjsn.put("storeOrderId", transactionHistory.getPaygatewaystoreOrderID());
					singleTransactionjsn.put("excessOrderId", transactionHistory.getPaygatewayexcpayorderTRID());
					singleTransactionjsn.put("storTransactionId", transactionHistory.getPaygatewayPaymenstoreTRID());
					singleTransactionjsn.put("excessTransactionId", transactionHistory.getPaygatewayexcpayTRID());

					// praveen october 05-2023
//					double cgstCalculatedAmount = GlobalVariable.calulcateGstMethod(
//							(int) (transactionHistory.getAmount() / 100 + transactionHistory.getExcess_amount() / 100 + transactionHistory.getBalance() / 100),
//							"CGST", transactionHistory.getTerminalid());
//					double sgstCalculatedAmounnt = GlobalVariable.calulcateGstMethod(
//							(int) (transactionHistory.getAmount() / 100 + transactionHistory.getExcess_amount() / 100),
//							"SGST", transactionHistory.getTerminalid());
//					double totDoubleAmount_withoutGST = Double.parseDouble(decimalFormat
//							.format((transactionHistory.getAmount() / 100 + transactionHistory.getExcess_amount() / 100 + transactionHistory.getBalance() / 100)
//									- cgstCalculatedAmount - sgstCalculatedAmounnt));

					// praveen changed here june 03-2024

					double cgstCalculatedAmount = GlobalVariable.calulcateGstMethod(
							(int) (transactionHistory.getAmount() / 100
									+ (double) transactionHistory.getExcess_amount() / 100
									+ (double) transactionHistory.getBalance() / 100
									+ (int) transactionHistory.getPartretamount() / 100),
							"CGST", transactionHistory.getTerminalid());
					double sgstCalculatedAmounnt = GlobalVariable.calulcateGstMethod(
							(int) (transactionHistory.getAmount() / 100
									+ (double) transactionHistory.getExcess_amount() / 100
									+ (double) transactionHistory.getBalance() / 100
									+ (int) transactionHistory.getPartretamount() / 100),
							"SGST", transactionHistory.getTerminalid());
					double totDoubleAmount_withoutGST = Double
							.parseDouble(decimalFormat.format(((int) transactionHistory.getAmount() / 100
									+ (double) transactionHistory.getExcess_amount() / 100
									+ (double) transactionHistory.getBalance() / 100
									+ (int) transactionHistory.getPartretamount() / 100 + (double) cgstCalculatedAmount
									+ (double) sgstCalculatedAmounnt)));

					singleTransactionjsn.put("TotalAmountWithout_GST", totDoubleAmount_withoutGST);
					singleTransactionjsn.put("CGST", cgstCalculatedAmount);
					singleTransactionjsn.put("SGST", sgstCalculatedAmounnt);
					singleTransactionjsn.put("TotalAmountWith_GST",
							Math.round(((int) transactionHistory.getAmount() / 100
									+ (double) transactionHistory.getExcess_amount() / 100
									+ (double) transactionHistory.getBalance() / 100
									+ (int) transactionHistory.getPartretamount() / 100)));

					singleTransactionjsn.put("TotalAmountWithout_GST", totDoubleAmount_withoutGST);
					singleTransactionjsn.put("CGST", cgstCalculatedAmount);
					singleTransactionjsn.put("SGST", sgstCalculatedAmounnt);
					singleTransactionjsn.put("TotalAmountWith_GST",
							Math.round((transactionHistory.getAmount() / 100
									+ (double) transactionHistory.getExcess_amount() / 100
									+ (double) transactionHistory.getBalance() / 100
									+ transactionHistory.getPartretamount() / 100)));
//					arrOfTransctionHistObj.put(singleTransactionjsn);

					arrOfTransctionHistObj.put(singleTransactionjsn);

				}
				responseObject.put("responseCode", "tddata-200");
				responseObject.put("jsontoxldata", arrOfTransctionHistObj);
			} else {
				responseObject.put("responseCode", "tddata-400");
			}
		} else if (packetType.equalsIgnoreCase("getxl")) {
			Date fromDate = Date.valueOf(requestedObject.getString("fromDate"));
			Date toDate = Date.valueOf(requestedObject.getString("toDate"));

			JSONArray arrOfTransctionHistObj = new JSONArray();

//			terminalIDList;

			int i = 0;
			int countSheets = 1;
			while (i < terminalIDList.size()) {
				JSONObject termWiseJSONObject = getListWiseTermIdDetails(terminalIDList.get(i), fromDate, toDate,
						session, countSheets);

				if (!termWiseJSONObject.isNull("sheetName")) {
					arrOfTransctionHistObj.put(termWiseJSONObject);
					countSheets++;
				}

				i++;
			}

//			System.out.println(countSheets);?

			if (arrOfTransctionHistObj.length() > 0) {
				responseObject.put("responseCode", "tddata-200");
				responseObject.put("jsontoxldata", arrOfTransctionHistObj);
			} else {
				responseObject.put("responseCode", "tddata-400");
			}

//			System.out.println(responseObject);

		} else if (packetType.equalsIgnoreCase("gettermwise-xl")) {

			Date fromDate = Date.valueOf(requestedObject.getString("fromDate"));
			Date toDate = Date.valueOf(requestedObject.getString("toDate"));

			JSONArray arrOfTransctionHistObj = new JSONArray();

			String selectedTerminalId = requestedObject.get("terminalID").toString().replace("[", "").replace("]", "")
					.replace("\"", "");

			Collection<String> termIdList = new ArrayList<String>(Arrays.asList(selectedTerminalId.split(",")));

//			terminalIDList;

			int i = 0;
			int countSheets = 1;
			for (String termID : termIdList) {

				JSONObject termWiseJSONObject = getListWiseTermIdDetails(termID, fromDate, toDate, session,
						countSheets);

				if (!termWiseJSONObject.isNull("sheetName")) {
					arrOfTransctionHistObj.put(termWiseJSONObject);
					countSheets++;
				}
				;

				i++;
			}

//			System.out.println(countSheets);?

			if (arrOfTransctionHistObj.length() > 0) {
				responseObject.put("responseCode", "tddata-200");
				responseObject.put("jsontoxldata", arrOfTransctionHistObj);
			} else {
				responseObject.put("responseCode", "tddata-400");
			}
		}

		writer.append(responseObject.toString());
		writer.close();
		session.close();

	}

	public List<String> getAllTerminalIds(Session session) {
		List<String> tIdList = new ArrayList<>();

		String hql = "select terminalid from SiteRegistration";

		tIdList = session.createQuery(hql).getResultList();

		return tIdList;
	}

	// for getting the result terminal wise

	private JSONObject getListWiseTermIdDetails(String termID, Date fromDate, Date toDate, Session session, int count) {

		JSONObject termWiseJSONObject = new JSONObject();
		JSONArray specificTermWiseJSONDetailArr = new JSONArray();

		String getTHHql = "FROM TransactionHistory WHERE date_of_open BETWEEN :fromDate AND :toDate AND terminalid=:termId";
		ArrayList<TransactionHistory> transHistList = (ArrayList<TransactionHistory>) session
				.createQuery(getTHHql, TransactionHistory.class).setParameter("fromDate", fromDate)
				.setParameter("toDate", toDate).setParameter("termId", termID).getResultList();

		if (!transHistList.isEmpty()) {

//			System.out.println("inside if condition");

			boolean isFirstExecute = true;

			double totCGST_Amount = 0;
			double totSGST_Amount = 0;
			double totAmountWithout_GST = 0;
			double totAmonutWith_GST = 0;
			double totAmount = 0;
			double totExcessAmount = 0;

			for (TransactionHistory transactionHistory : transHistList) {

				JSONObject singleTransactionjsn = new JSONObject();

//				System.out.println("inside for each condition");

				if (isFirstExecute) {

					String sheetName = "sheet" + count;
					isFirstExecute = false;
//					termWiseJSONObject.put("sheetName", sheetName);
					termWiseJSONObject.put("sheetName", transactionHistory.getTerminalid());
					
				}

				singleTransactionjsn.put("slno", transactionHistory.getSlno());
				singleTransactionjsn.put("MobileNo", transactionHistory.getMobileNo());
				singleTransactionjsn.put("transactionId", transactionHistory.getTransactionID());
				singleTransactionjsn.put("amount", transactionHistory.getAmount() / 100);
				singleTransactionjsn.put("remarks", transactionHistory.getRemark());
				singleTransactionjsn.put("transactionType", transactionHistory.getStatus());
				singleTransactionjsn.put("dateOfTransaction", transactionHistory.getDate_of_open());
				singleTransactionjsn.put("timeOfTransaction", transactionHistory.getTime_of_open());
				singleTransactionjsn.put("lockers", transactionHistory.getLockNo());
				singleTransactionjsn.put("custName", transactionHistory.getCustomerName());
				singleTransactionjsn.put("noOfHours", transactionHistory.getNo_of_hours());
				singleTransactionjsn.put("terminalID", transactionHistory.getTerminalid());
				singleTransactionjsn.put("itemStored", transactionHistory.getItemsStored());
				singleTransactionjsn.put("excess_amount", transactionHistory.getExcess_amount() / 100);
				singleTransactionjsn.put("excess_hour", transactionHistory.getExcess_hours());
				singleTransactionjsn.put("balance", transactionHistory.getBalance() / 100);
				singleTransactionjsn.put("passcode", transactionHistory.getPasscode());
				singleTransactionjsn.put("closing_date", transactionHistory.getClosing_date());
				singleTransactionjsn.put("closingTime", transactionHistory.getClosing_time());
				singleTransactionjsn.put("storeOrderId", transactionHistory.getPaygatewaystoreOrderID());
				singleTransactionjsn.put("excessOrderId", transactionHistory.getPaygatewayexcpayorderTRID());
				singleTransactionjsn.put("storTransactionId", transactionHistory.getPaygatewayPaymenstoreTRID());
				singleTransactionjsn.put("excessTransactionId", transactionHistory.getPaygatewayexcpayTRID());

				// praveen october 05-2023
				double cgstCalculatedAmount = GlobalVariable.calulcateGstMethod(
						(int) (transactionHistory.getAmount() / 100 + transactionHistory.getExcess_amount() / 100
								+ transactionHistory.getBalance() / 100 + transactionHistory.getPartretamount() / 100),
						"CGST", transactionHistory.getTerminalid());
				double sgstCalculatedAmounnt = GlobalVariable.calulcateGstMethod(
						(int) (transactionHistory.getAmount() / 100 + transactionHistory.getExcess_amount() / 100
								+ transactionHistory.getPartretamount() / 100),
						"SGST", transactionHistory.getTerminalid());
				double totDoubleAmount_withGST = Double.parseDouble(decimalFormat.format(
						(transactionHistory.getAmount() / 100 + (double) transactionHistory.getExcess_amount() / 100
								+ (double) transactionHistory.getBalance() / 100
								+ transactionHistory.getPartretamount() / 100) + cgstCalculatedAmount
								+ sgstCalculatedAmounnt));

				singleTransactionjsn.put("TotalAmountWith_GST", totDoubleAmount_withGST);
				singleTransactionjsn.put("CGST", cgstCalculatedAmount);
				singleTransactionjsn.put("SGST", sgstCalculatedAmounnt);
				singleTransactionjsn.put("TotalAmountWithout_GST",
						Math.round((transactionHistory.getAmount() / 100
								+ (double) transactionHistory.getExcess_amount() / 100
								+ (double) transactionHistory.getBalance() / 100
								+ transactionHistory.getPartretamount() / 100)));

//				arrOfTransctionHistObj.put(singleTransactionjsn);

				specificTermWiseJSONDetailArr.put(singleTransactionjsn);

				{
					// calculation part is here
					totCGST_Amount += cgstCalculatedAmount;
					totSGST_Amount += sgstCalculatedAmounnt;
					totAmonutWith_GST += totDoubleAmount_withGST; 
					totAmountWithout_GST += Double.parseDouble(decimalFormat.format(
							(transactionHistory.getAmount() / 100 + (double) transactionHistory.getExcess_amount() / 100
									+ (double) transactionHistory.getBalance() / 100
									+ transactionHistory.getPartretamount() / 100)));
					totAmount += (transactionHistory.getAmount() / 100);
					totExcessAmount += (transactionHistory.getExcess_amount() / 100);

				}

			}

			if (specificTermWiseJSONDetailArr.length() > 0) {

				for (int i = 0; i < 2; i++) {
					JSONObject totalCalculationAmount = new JSONObject();
					if (i == 0) {
						totalCalculationAmount.put("slno", "");
						totalCalculationAmount.put("MobileNo", "");
						totalCalculationAmount.put("transactionId", "");
						totalCalculationAmount.put("amount", "");
						totalCalculationAmount.put("remarks", "");
						totalCalculationAmount.put("transactionType", "");
						totalCalculationAmount.put("dateOfTransaction", "");
						totalCalculationAmount.put("timeOfTransaction", "");
						totalCalculationAmount.put("lockers", "");
						totalCalculationAmount.put("custName", "");
						totalCalculationAmount.put("noOfHours", "");
						totalCalculationAmount.put("terminalID", "");
						totalCalculationAmount.put("itemStored", "");
						totalCalculationAmount.put("excess_amount", "");
						totalCalculationAmount.put("excess_hour", "");
						totalCalculationAmount.put("balance", "");
						totalCalculationAmount.put("passcode", "");
						totalCalculationAmount.put("closing_date", "");
						totalCalculationAmount.put("closingTime", "");
						totalCalculationAmount.put("storeOrderId", "");
						totalCalculationAmount.put("excessOrderId", "");
						totalCalculationAmount.put("storTransactionId", "");
						totalCalculationAmount.put("excessTransactionId", "");
						totalCalculationAmount.put("TotalAmountWithout_GST", "");
						totalCalculationAmount.put("CGST", "");
						totalCalculationAmount.put("SGST", "");
						totalCalculationAmount.put("TotalAmountWith_GST", "");
					} else {
						totalCalculationAmount.put("slno", "-");
						totalCalculationAmount.put("MobileNo", "-");
						totalCalculationAmount.put("transactionId", "-");
						totalCalculationAmount.put("amount", totAmount);
						totalCalculationAmount.put("remarks", "-");
						totalCalculationAmount.put("transactionType", "-");
						totalCalculationAmount.put("dateOfTransaction", "-");
						totalCalculationAmount.put("timeOfTransaction", "-");
						totalCalculationAmount.put("lockers", "-");
						totalCalculationAmount.put("custName", "-");
						totalCalculationAmount.put("noOfHours", "-");
						totalCalculationAmount.put("terminalID", "-");
						totalCalculationAmount.put("itemStored", "-");
						totalCalculationAmount.put("excess_amount", totExcessAmount);
						totalCalculationAmount.put("excess_hour", "-");
						totalCalculationAmount.put("balance", "-");
						totalCalculationAmount.put("passcode", "-");
						totalCalculationAmount.put("closing_date", "-");
						totalCalculationAmount.put("closingTime", "-");
						totalCalculationAmount.put("storeOrderId", "-");
						totalCalculationAmount.put("excessOrderId", "-");
						totalCalculationAmount.put("storTransactionId", "-");
						totalCalculationAmount.put("excessTransactionId", "-");

						totalCalculationAmount.put("TotalAmountWithout_GST", totAmountWithout_GST);
						totalCalculationAmount.put("CGST", totCGST_Amount);
						totalCalculationAmount.put("SGST", totSGST_Amount);
						totalCalculationAmount.put("TotalAmountWith_GST", totAmonutWith_GST);
					}

					specificTermWiseJSONDetailArr.put(totalCalculationAmount);
				}

			}

			termWiseJSONObject.put("details", specificTermWiseJSONDetailArr);
		}

//		System.out.println("outside if condition");

		return termWiseJSONObject;
	}

	public double calulcateGstMethod(int amount, double gstPercentage) {

		double gstAmount = 0;
		gstAmount = ((amount * gstPercentage) / 100);
		DecimalFormat decimalFormat = new DecimalFormat("#.00");
		gstAmount = Double.parseDouble(decimalFormat.format(gstAmount));
		return gstAmount;

	}

}
