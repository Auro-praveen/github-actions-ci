package com.locker.operations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

import com.auro.beans.TransactionHistory;
import com.auro.beans.invoiceDetails;
import com.auro.hibernateUtilities.HibernateUtils;
import com.locks.gloablVariable.GlobalVariable;

/**
 * Servlet implementation class FetchToXlFromIvnoice
 */

@WebServlet("/FetchToXlFromIvnoice")
public class FetchToXlFromIvnoice extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static DecimalFormat decimalFormat = new DecimalFormat("#.00");

	private double totlockerAmount = 0.0;
	private double totexcessAmount = 0.0;
	private double totbalance = 0.0;
	private double totAmountWithGST = 0.0;
	private double totAmountWithoutGST = 0.0;
	private double totcgst = 0.0;
	private double totsgst = 0.0;
	private double totPartialAmount = 0.0;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("got request");

		Session session = HibernateUtils.getSession();

		JSONObject totalXLSheetAmount = new JSONObject();
		JSONArray totalListInArr = new JSONArray();

//		String query = "SELECT invoiceDetails.mobileNo, invoiceDetails.lockerRentamount, "
//				+ "invoiceDetails.lockNo, invoiceDetails.customerName, invoiceDetails.lockerRenthour, invoiceDetails.terminalid, "
//				+ " invoiceDetails.excess_amount, invoiceDetails.excess_hours, invoiceDetails.balanceAmount, invoiceDetails.partretamount, "
//				+ " invoiceDetails.invoice_date, invoiceDetails.invoice_time, invoiceDetails.totalAmount FROM invoiceDetails";
//				+ " WHERE date_of_open BETWEEN :from_date AND :to_date";

		//

		String query = "from invoiceDetails where terminalid in ('ORN', 'ELPROCST', 'DSLHYD1', 'DSLHYD2', 'NXSWUG', 'NXSWLG')";
		session.beginTransaction();
		try {

			List<invoiceDetails> invoiceDetailsObjects = session.createQuery(query)

//					.setParameter("from_date", "2023-01-01")
//					.setParameter("to_date", "2024-05-05")

					.getResultList();

			int count = 1;

			for (invoiceDetails invoiceDetailObject : invoiceDetailsObjects) {

				JSONObject totalCalculationAmount = new JSONObject();

				totalCalculationAmount.put("slno", count);
				totalCalculationAmount.put("MobileNo", invoiceDetailObject.getMobileNo());
				totalCalculationAmount.put("amount", invoiceDetailObject.getLockerRentamount());
//				totalCalculationAmount.put("paymentStatus", (String) invoiceDetailObject[2]);
//				totalCalculationAmount.put("dateOfOpen", (Date) invoiceDetailObject[3]);
//				totalCalculationAmount.put("timeOfOpen", (Date) invoiceDetailObject[4]);
				totalCalculationAmount.put("lockers", invoiceDetailObject.getLockNo());
				totalCalculationAmount.put("custName", invoiceDetailObject.getCustomerName());
				totalCalculationAmount.put("noOfHours", invoiceDetailObject.getLockerRenthour());
				totalCalculationAmount.put("terminalID", invoiceDetailObject.getTerminalid());
//				totalCalculationAmount.put("itemStored", (String) invoiceDetailObject[9]);
				totalCalculationAmount.put("excess_amount", invoiceDetailObject.getExcess_amount());
				totalCalculationAmount.put("excess_hour_inMins", invoiceDetailObject.getExcess_hours());
				totalCalculationAmount.put("balance", invoiceDetailObject.getBalanceAmount());
				totalCalculationAmount.put("partionRet_Amount", invoiceDetailObject.getPartretamount());
				totalCalculationAmount.put("invioce_date", invoiceDetailObject.getInvoice_date());
				totalCalculationAmount.put("invoice_time", invoiceDetailObject.getInvoice_time());

				totalCalculationAmount.put("TotalAmountWithout_GST",
						((double) invoiceDetailObject.getLockerRentamount()
								+ (double) invoiceDetailObject.getExcess_amount()
								+ invoiceDetailObject.getBalanceAmount() + invoiceDetailObject.getPartretamount()));

				totalCalculationAmount.put("CGST", GlobalVariable.calulcateGstMethod(
						(int) (invoiceDetailObject.getLockerRentamount() + invoiceDetailObject.getExcess_amount()
								+ invoiceDetailObject.getBalanceAmount() + invoiceDetailObject.getPartretamount()),
						"CGST", invoiceDetailObject.getTerminalid()));
				totalCalculationAmount.put("SGST", GlobalVariable.calulcateGstMethod(
						(int) (invoiceDetailObject.getLockerRentamount() + invoiceDetailObject.getExcess_amount()
								+ invoiceDetailObject.getBalanceAmount() + invoiceDetailObject.getPartretamount()),
						"SGST", (String) invoiceDetailObject.getTerminalid()));
				totalCalculationAmount.put("TotalAmountWith_GST", (double) invoiceDetailObject.getTotalAmount());

				count++;

				totalListInArr.put(totalCalculationAmount);

			}

			totalXLSheetAmount.put("invoiceData", totalListInArr);

			System.out.println(totalXLSheetAmount);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			session.close();
		}

		resp.getWriter().append(totalXLSheetAmount.toString()).flush();

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

		// initializing the variables to zero so when next request comes the value will
		// be changed
		totlockerAmount = 0.0;
		totexcessAmount = 0.0;
		totbalance = 0.0;
		totAmountWithGST = 0.0;
		totAmountWithoutGST = 0.0;
		totcgst = 0.0;
		totsgst = 0.0;
		totPartialAmount = 0.0;

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

			if (totlockerAmount > 0) {
				JSONObject totalLockPrizeSheet = getAllLockersDetail();

				if (!totalLockPrizeSheet.isNull("sheetName")) {
					arrOfTransctionHistObj.put(totalLockPrizeSheet);
					countSheets++;
				}
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

				i++;
			}

			if (totlockerAmount > 0) {
				JSONObject totalLockPrizeSheet = getAllLockersDetail();

				if (!totalLockPrizeSheet.isNull("sheetName")) {
					arrOfTransctionHistObj.put(totalLockPrizeSheet);
					countSheets++;
				}
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

//		String hql = "select terminalid from SiteRegistration";	

		String hql = "select terminalid from SiteRegistration where terminalid not in ('G21')";

		tIdList = session.createQuery(hql).getResultList();

		return tIdList;
	}

	// for getting the result terminal wise

	private JSONObject getListWiseTermIdDetails(String termID, Date fromDate, Date toDate, Session session, int count) {

		JSONObject termWiseJSONObject = new JSONObject();
		JSONArray specificTermWiseJSONDetailArr = new JSONArray();

		String getTHHql = "FROM invoiceDetails WHERE invoice_date BETWEEN :fromDate AND :toDate AND terminalid=:termId";
		ArrayList<invoiceDetails> transHistList = (ArrayList<invoiceDetails>) session
				.createQuery(getTHHql, invoiceDetails.class).setParameter("fromDate", fromDate)
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
			double totPartRetAmount = 0;
			double balanceAmount = 0;

			int countSlno = 1;

			for (invoiceDetails invoiceDetailBean : transHistList) {

				JSONObject singleTransactionjsn = new JSONObject();

//				System.out.println("inside for each condition");

				if (isFirstExecute) {

					String sheetName = "sheet" + count;
					isFirstExecute = false;
//					termWiseJSONObject.put("sheetName", sheetName);
					termWiseJSONObject.put("sheetName", invoiceDetailBean.getTerminalid());

				}

				singleTransactionjsn.put("slno", invoiceDetailBean.getSlno());
				singleTransactionjsn.put("MobileNo", invoiceDetailBean.getMobileNo());
				singleTransactionjsn.put("amount", invoiceDetailBean.getLockerRentamount());
				singleTransactionjsn.put("lockers", invoiceDetailBean.getLockNo());
				singleTransactionjsn.put("custName", invoiceDetailBean.getCustomerName());
				singleTransactionjsn.put("noOfHours", invoiceDetailBean.getLockerRenthour());
				singleTransactionjsn.put("terminalID", invoiceDetailBean.getTerminalid());
				singleTransactionjsn.put("excess_amount", invoiceDetailBean.getExcess_amount());
				singleTransactionjsn.put("excess_hour", invoiceDetailBean.getExcess_hours());
				singleTransactionjsn.put("balance", invoiceDetailBean.getBalanceAmount());
				singleTransactionjsn.put("partialRetAmount", invoiceDetailBean.getPartretamount());
				singleTransactionjsn.put("invoice_date", invoiceDetailBean.getInvoice_date());
				singleTransactionjsn.put("invoice_time", invoiceDetailBean.getInvoice_time());

				// praveen october 06-2024
				double cgstCalculatedAmount = GlobalVariable.calulcateGstMethod(
						(int) (invoiceDetailBean.getLockerRentamount() + invoiceDetailBean.getExcess_amount()
								+ invoiceDetailBean.getBalanceAmount() + invoiceDetailBean.getPartretamount()),
						"CGST", invoiceDetailBean.getTerminalid());

				double sgstCalculatedAmounnt = GlobalVariable.calulcateGstMethod(
						(int) (invoiceDetailBean.getLockerRentamount() + invoiceDetailBean.getExcess_amount()
								+ invoiceDetailBean.getPartretamount() + invoiceDetailBean.getBalanceAmount()),
						"SGST", invoiceDetailBean.getTerminalid());

				double totDoubleAmount_withGST = invoiceDetailBean.getTotalAmount();

				singleTransactionjsn.put("TotalAmountWith_GST", totDoubleAmount_withGST);
				singleTransactionjsn.put("CGST", cgstCalculatedAmount);
				singleTransactionjsn.put("SGST", sgstCalculatedAmounnt);
				singleTransactionjsn.put("TotalAmountWithout_GST",
						Math.round(invoiceDetailBean.getLockerRentamount() + invoiceDetailBean.getExcess_amount()
								+ invoiceDetailBean.getBalanceAmount() + invoiceDetailBean.getPartretamount()));

//				arrOfTransctionHistObj.put(singleTransactionjsn);

				specificTermWiseJSONDetailArr.put(singleTransactionjsn);

				{
					// calculation part is here

					totCGST_Amount += cgstCalculatedAmount;
					totSGST_Amount += sgstCalculatedAmounnt;
					totAmonutWith_GST += totDoubleAmount_withGST;

					totAmountWithout_GST += Double.parseDouble(decimalFormat
							.format(invoiceDetailBean.getLockerRentamount() + invoiceDetailBean.getExcess_amount()
									+ invoiceDetailBean.getBalanceAmount() + invoiceDetailBean.getPartretamount()));
					totAmount += (invoiceDetailBean.getLockerRentamount());
					totExcessAmount += (invoiceDetailBean.getExcess_amount());
					totPartRetAmount += invoiceDetailBean.getPartretamount();
					balanceAmount += invoiceDetailBean.getBalanceAmount();

				}

				countSlno++;
			}

			if (specificTermWiseJSONDetailArr.length() > 0) {

				for (int i = 0; i < 2; i++) {
					JSONObject totalCalculationAmount = new JSONObject();
					if (i == 0) {
						totalCalculationAmount.put("slno", "");
						totalCalculationAmount.put("MobileNo", "");

						totalCalculationAmount.put("amount", "");

						totalCalculationAmount.put("lockers", "");
						totalCalculationAmount.put("custName", "");
						totalCalculationAmount.put("noOfHours", "");
						totalCalculationAmount.put("terminalID", "");
						totalCalculationAmount.put("excess_amount", "");
						totalCalculationAmount.put("excess_hour", "");
						totalCalculationAmount.put("balance", "");
						totalCalculationAmount.put("partialRetAmount", "");

						totalCalculationAmount.put("invoice_date", "");
						totalCalculationAmount.put("invoice_time", "");
						totalCalculationAmount.put("TotalAmountWithout_GST", "");
						totalCalculationAmount.put("CGST", "");
						totalCalculationAmount.put("SGST", "");
						totalCalculationAmount.put("TotalAmountWith_GST", "");
					} else {
						totalCalculationAmount.put("slno", "-");
						totalCalculationAmount.put("MobileNo", "-");
						totalCalculationAmount.put("amount", totAmount);

						totalCalculationAmount.put("lockers", "-");
						totalCalculationAmount.put("custName", "-");
						totalCalculationAmount.put("noOfHours", "-");
						totalCalculationAmount.put("terminalID", "-");

						totalCalculationAmount.put("excess_amount", totExcessAmount);
						totalCalculationAmount.put("excess_hour", "-");
						totalCalculationAmount.put("balance", balanceAmount);
						totalCalculationAmount.put("partialRetAmount", totPartRetAmount);

						totalCalculationAmount.put("invoice_date", "-");
						totalCalculationAmount.put("invoice_time", "-");

						totalCalculationAmount.put("TotalAmountWithout_GST", totAmountWithout_GST);
						totalCalculationAmount.put("CGST", totCGST_Amount);
						totalCalculationAmount.put("SGST", totSGST_Amount);
						totalCalculationAmount.put("TotalAmountWith_GST", totAmonutWith_GST);

					}

					specificTermWiseJSONDetailArr.put(totalCalculationAmount);
				}

			}

			{
				totlockerAmount += totAmount;
				totexcessAmount += totExcessAmount;
				totbalance += balanceAmount;
				totAmountWithGST += totAmonutWith_GST;
				totAmountWithoutGST += totAmountWithout_GST;
				totcgst += totCGST_Amount;
				totsgst += totSGST_Amount;
				totPartialAmount += totPartRetAmount;
			}

			termWiseJSONObject.put("details", specificTermWiseJSONDetailArr);
		}

//		System.out.println("outside if condition");

		return termWiseJSONObject;
	}

	// prvaeen added 26-07-2024 for total gst amount

	private JSONObject getAllLockersDetail() {

		JSONObject termWiseJSONObject = new JSONObject();
		termWiseJSONObject.put("sheetName", "Total Calculations");

		try {
			JSONArray specificTermWiseJSONDetailArr = new JSONArray();

			JSONObject totalCalculationAmount = new JSONObject();

			totalCalculationAmount.put("amount", totlockerAmount);
			totalCalculationAmount.put("excess_amount", totexcessAmount);
			totalCalculationAmount.put("balance", totbalance);
			totalCalculationAmount.put("partialRetAmount", totPartialAmount);

			totalCalculationAmount.put("TotalAmountWithout_GST", totAmountWithoutGST);
			totalCalculationAmount.put("CGST", totcgst);
			totalCalculationAmount.put("SGST", totsgst);
			totalCalculationAmount.put("TotalAmountWith_GST", totAmountWithGST);

			specificTermWiseJSONDetailArr.put(totalCalculationAmount);

			termWiseJSONObject.put("details", specificTermWiseJSONDetailArr);
		} catch (Exception e) {
			// TODO: handle exception
		}

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
