package com.auroLocker.fetch.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.Time;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;

import com.auro.beans.TransactionHistory;
import com.auro.hibernateUtilities.HibernateUtils;
import com.locks.gloablVariable.GlobalVariable;

/**
 * Servlet implementation class FetchTransactionHistory Auther Praveen
 */

@WebServlet("/FetchTransactionHistory")
public class FetchTransactionHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static DecimalFormat decimalFormat = new DecimalFormat("#.00");

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FetchTransactionHistory() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// System.out.println("inside transction history");

		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");

		PrintWriter writer = response.getWriter();

		Session session = HibernateUtils.getSession();
		Transaction transaction = session.beginTransaction();

		JSONObject responseObj = new JSONObject();

		JSONArray slno = new JSONArray();
		JSONArray MobileNo = new JSONArray();
		JSONArray transactionId = new JSONArray();
		JSONArray dateOfTransaction = new JSONArray();
		JSONArray timeOfTransaction = new JSONArray();
		JSONArray amount = new JSONArray();
		JSONArray remarks = new JSONArray();
		JSONArray transactionType = new JSONArray();
		JSONArray lockerNumbers = new JSONArray();
		JSONArray custName = new JSONArray();
//		JSONArray closingDate = new JSONArray();
//		JSONArray closingTime = new JSONArray();
//		JSONArray balance = new JSONArray();
//		JSONArray excessAmount = new JSONArray();
//		JSONArray excessHour = new JSONArray();

		int count = 0;
		try {

			ArrayList<TransactionHistory> tdHistory = (ArrayList<TransactionHistory>) session
					.createQuery("from TransactionHistory").getResultList();

			for (TransactionHistory transactionHistory : tdHistory) {
				slno.put(transactionHistory.getSlno());
				MobileNo.put(transactionHistory.getMobileNo());
				transactionId.put(transactionHistory.getTransactionID());
				dateOfTransaction.put(transactionHistory.getDate_of_open());
				amount.put((transactionHistory.getAmount()) / 100);
				remarks.put(transactionHistory.getRemark());
				transactionType.put(transactionHistory.getStatus());
				timeOfTransaction.put(transactionHistory.getTime_of_open());
				lockerNumbers.put(transactionHistory.getLockNo());
				custName.put(transactionHistory.getCustomerName());
//				balance.put(transactionHistory.getBalance());
//				closingDate.put(transactionHistory.getClosing_date());
//				closingTime.put(transactionHistory.getClosing_time());
//				excessAmount.put(transactionHistory.getExcess_amount());
//				excessHour.put(transactionHistory.getExcess_hours());

				count += (transactionHistory.getAmount() / 100);
			}

			// System.out.println(count);

			transaction.commit();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		if (slno.length() > 0) {
			responseObj.put("responseCode", "tdhistory");
			responseObj.put("slno", slno);
			responseObj.put("MobileNo", MobileNo);
			responseObj.put("transactionId", transactionId);
			responseObj.put("amount", amount);
			responseObj.put("remarks", remarks);
			responseObj.put("transactionType", transactionType);
			responseObj.put("dateOfTransaction", dateOfTransaction);
			responseObj.put("timeOfTransaction", timeOfTransaction);
			responseObj.put("lockers", lockerNumbers);
			responseObj.put("custName", custName);
//			responseObj.put("balance", balance);
//			responseObj.put("excessamount", excessAmount);
//			responseObj.put("excesshour", excessHour);
//			responseObj.put("closingdate", closingDate);
//			responseObj.put("closingtime", closingTime);

		} else {
			responseObj.put("responseType", "null");
		}

		writer.append(responseObj.toString());
		session.close();
		writer.close();

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// System.out.println("inside transction history post");

		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");

		String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines()
				.collect(Collectors.joining("/"));
		JSONObject reqObj = new JSONObject(jsonBody);
		// System.out.println(reqObj);

		PrintWriter writer = response.getWriter();

		Session session = HibernateUtils.getSession();
		Transaction transaction = session.beginTransaction();

		String terminalId = reqObj.getString("terminalID");
		String packetType = reqObj.getString("PacketType");

		JSONObject responseObj = new JSONObject();

		JSONArray slno = new JSONArray();
		JSONArray MobileNo = new JSONArray();
		JSONArray transactionId = new JSONArray();
		JSONArray dateOfTransaction = new JSONArray();
		JSONArray timeOfTransaction = new JSONArray();
		JSONArray amount = new JSONArray();
		JSONArray remarks = new JSONArray();
		JSONArray transactionType = new JSONArray();
		JSONArray lockerNumbers = new JSONArray();
		JSONArray custName = new JSONArray();
		JSONArray noOfHours = new JSONArray();
		JSONArray terminalID = new JSONArray();
		JSONArray itemStored = new JSONArray();
		JSONArray passcode = new JSONArray();
		JSONArray excessHour = new JSONArray();
		JSONArray excessAmount = new JSONArray();
		JSONArray balance = new JSONArray();
		JSONArray closingTime = new JSONArray();
		JSONArray closingDate = new JSONArray();
		String tdResponseCode = "";

		JSONArray CGSTArr = new JSONArray();
		JSONArray SGSTArr = new JSONArray();
		JSONArray amount_withoutGST = new JSONArray();
		JSONArray amount_witGST = new JSONArray();

		if (packetType.equalsIgnoreCase("gettdhistorybytid")) {
			try {

				ArrayList<TransactionHistory> tdHistory = (ArrayList<TransactionHistory>) session
						.createQuery("from TransactionHistory " + "where terminalid='" + terminalId + "'")
						.getResultList();

				for (TransactionHistory transactionHistory : tdHistory) {

					slno.put(transactionHistory.getSlno());
					MobileNo.put(transactionHistory.getMobileNo());
					transactionId.put(transactionHistory.getTransactionID());
					dateOfTransaction.put(transactionHistory.getDate_of_open());
					amount.put(transactionHistory.getAmount() / 100);
					remarks.put(transactionHistory.getRemark());
					transactionType.put(transactionHistory.getStatus());
					timeOfTransaction.put(transactionHistory.getTime_of_open());
					lockerNumbers.put(transactionHistory.getLockNo());
					custName.put(transactionHistory.getCustomerName());
					noOfHours.put(transactionHistory.getNo_of_hours());
					terminalID.put(transactionHistory.getTerminalid());
					passcode.put(transactionHistory.getPasscode());
					excessAmount.put(Math.round(transactionHistory.getExcess_amount() / 100));
					excessHour.put(transactionHistory.getExcess_hours());
					itemStored.put(transactionHistory.getItemsStored());
					balance.put(Math.round(transactionHistory.getBalance() / 100));
					closingDate.put(transactionHistory.getClosing_date());
					closingTime.put(transactionHistory.getClosing_time());

					double CGST = GlobalVariable.calulcateGstMethod((int) (transactionHistory.getAmount() / 100
							+ transactionHistory.getExcess_amount() / 100 + transactionHistory.getBalance() / 100),
							"CGST", transactionHistory.getTerminalid());
					double SGST = GlobalVariable.calulcateGstMethod((int) (transactionHistory.getAmount() / 100
							+ transactionHistory.getExcess_amount() / 100 + transactionHistory.getBalance() / 100),
							"SGST", transactionHistory.getTerminalid());
					double totAmount_WithoutGST = Double.parseDouble(decimalFormat
							.format((transactionHistory.getAmount() / 100 + transactionHistory.getExcess_amount() / 100
									+ transactionHistory.getBalance() / 100) + CGST + SGST));
					double totAmount_WithGST = Math.round((transactionHistory.getAmount() / 100
							+ transactionHistory.getExcess_amount() / 100 + transactionHistory.getBalance() / 100));

					CGSTArr.put(CGST);
					SGSTArr.put(SGST);
					amount_withoutGST.put(totAmount_WithGST);
					amount_witGST.put(totAmount_WithoutGST);
//					balance.put(0);
				}

				if (slno.length() > 0) {
					tdResponseCode = "tdhistory";
				} else {
					tdResponseCode = "notidhistory";
				}

				transaction.commit();

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		} else if (packetType.equalsIgnoreCase("gettdhistorybyddate")) {

			Date selectedDate = Date.valueOf(reqObj.getString("selectedDate"));

			try {

				ArrayList<TransactionHistory> tdHistory = (ArrayList<TransactionHistory>) session
						.createQuery("from TransactionHistory " + "where terminalid='" + terminalId
								+ "' and date_of_open='" + selectedDate + "' order by closing_time DESC")
						.getResultList();

				for (TransactionHistory transactionHistory : tdHistory) {
					slno.put(transactionHistory.getSlno());
					MobileNo.put(transactionHistory.getMobileNo());
					transactionId.put(transactionHistory.getTransactionID());
					dateOfTransaction.put(transactionHistory.getDate_of_open());
					amount.put(transactionHistory.getAmount() / 100);
					remarks.put(transactionHistory.getRemark());
					transactionType.put(transactionHistory.getStatus());
					timeOfTransaction.put(transactionHistory.getTime_of_open());
					lockerNumbers.put(transactionHistory.getLockNo());
					custName.put(transactionHistory.getCustomerName());
					noOfHours.put(transactionHistory.getNo_of_hours());
					terminalID.put(transactionHistory.getTerminalid());
					passcode.put(transactionHistory.getPasscode());
					excessAmount.put(Math.round(transactionHistory.getExcess_amount() / 100));
					excessHour.put(transactionHistory.getExcess_hours());
					itemStored.put(transactionHistory.getItemsStored());
					balance.put(Math.round(transactionHistory.getBalance() / 100));
					closingDate.put(transactionHistory.getClosing_date());
					closingTime.put(transactionHistory.getClosing_time());
//					balance.put(0);

					double CGST = GlobalVariable.calulcateGstMethod((int) (transactionHistory.getAmount() / 100
							+ transactionHistory.getExcess_amount() / 100 + transactionHistory.getBalance() / 100),
							"CGST", transactionHistory.getTerminalid());
					double SGST = GlobalVariable.calulcateGstMethod((int) (transactionHistory.getAmount() / 100
							+ transactionHistory.getExcess_amount() / 100 + transactionHistory.getBalance() / 100),
							"SGST", transactionHistory.getTerminalid());
					double totAmount_WithoutGST = Double.parseDouble(decimalFormat
							.format((transactionHistory.getAmount() / 100 + transactionHistory.getExcess_amount() / 100
									+ transactionHistory.getBalance() / 100) + CGST + SGST));
					double totAmount_WithGST = Math.round((transactionHistory.getAmount() / 100
							+ transactionHistory.getExcess_amount() / 100 + transactionHistory.getBalance() / 100));

					CGSTArr.put(CGST);
					SGSTArr.put(SGST);
					amount_withoutGST.put(totAmount_WithGST);
					amount_witGST.put(totAmount_WithoutGST);
				}

				if (slno.length() > 0) {
					tdResponseCode = "tdhistory";
				} else {
					tdResponseCode = "nodatehistory";
				}

				transaction.commit();

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		}
		// TODO : praveen transaction history by mobile number without date and mobile
		// number with date

		else if (packetType.equalsIgnoreCase("gethistnodate")) {

			String mobileNo = reqObj.getString("MobileNo");
			List<TransactionHistory> tdHistByMobileNo = null;

			if (terminalId.equalsIgnoreCase("notId")) {
				String hql = "from TransactionHistory where mobileNo=:MobileNo";

				tdHistByMobileNo = session.createQuery(hql).setParameter("MobileNo", mobileNo).getResultList();

			} else {
				String hql = "from TransactionHistory where mobileNo=:MobileNo and terminalid=:terminalID";

				tdHistByMobileNo = session.createQuery(hql).setParameter("MobileNo", mobileNo)
						.setParameter("terminalID", terminalId).getResultList();

			}

			if (!tdHistByMobileNo.isEmpty()) {
				for (TransactionHistory transactionHistory : tdHistByMobileNo) {
					slno.put(transactionHistory.getSlno());
					MobileNo.put(transactionHistory.getMobileNo());
					transactionId.put(transactionHistory.getTransactionID());
					dateOfTransaction.put(transactionHistory.getDate_of_open());
					amount.put(transactionHistory.getAmount() / 100);
					remarks.put(transactionHistory.getRemark());
					transactionType.put(transactionHistory.getStatus());
					timeOfTransaction.put(transactionHistory.getTime_of_open());
					lockerNumbers.put(transactionHistory.getLockNo());
					custName.put(transactionHistory.getCustomerName());
					noOfHours.put(transactionHistory.getNo_of_hours());
					terminalID.put(transactionHistory.getTerminalid());
					passcode.put(transactionHistory.getPasscode());
					excessAmount.put(Math.round(transactionHistory.getExcess_amount() / 100));
					excessHour.put(transactionHistory.getExcess_hours());
					itemStored.put(transactionHistory.getItemsStored());
					balance.put(Math.round(transactionHistory.getBalance() / 100));
					closingDate.put(transactionHistory.getClosing_date());
					closingTime.put(transactionHistory.getClosing_time());

					double CGST = GlobalVariable.calulcateGstMethod((int) (transactionHistory.getAmount() / 100
							+ transactionHistory.getExcess_amount() / 100 + transactionHistory.getBalance() / 100),
							"CGST", transactionHistory.getTerminalid());
					double SGST = GlobalVariable.calulcateGstMethod((int) (transactionHistory.getAmount() / 100
							+ transactionHistory.getExcess_amount() / 100 + transactionHistory.getBalance() / 100),
							"SGST", transactionHistory.getTerminalid());
					double totAmount_WithoutGST = Double.parseDouble(decimalFormat
							.format((transactionHistory.getAmount() / 100 + transactionHistory.getExcess_amount() / 100
									+ transactionHistory.getBalance() / 100) + CGST + SGST));
					double totAmount_WithGST = Math.round((transactionHistory.getAmount() / 100
							+ transactionHistory.getExcess_amount() / 100 + transactionHistory.getBalance() / 100));

					CGSTArr.put(CGST);
					SGSTArr.put(SGST);
					amount_withoutGST.put(totAmount_WithGST);
					amount_witGST.put(totAmount_WithoutGST);
				}
			} else {
				tdResponseCode = "notdhistbyalldate";
			}

		}

		// TODO praveen fetch mobile number for provided date

		else if (packetType.equalsIgnoreCase("gethistbydate")) {
			String mobileNo = reqObj.getString("MobileNo");
			Date tdOpenDate = Date.valueOf(reqObj.getString("selectedDate"));

			List<TransactionHistory> tdHistByMobileNo = null;

			if (terminalId.equalsIgnoreCase("notId")) {

				String hql = "from TransactionHistory where mobileNo=:MobileNo and date_of_open=:dateOfOpen";

				tdHistByMobileNo = session.createQuery(hql).setParameter("MobileNo", mobileNo)
						.setParameter("dateOfOpen", tdOpenDate).getResultList();

			} else {

				String hql = "from TransactionHistory where mobileNo=:MobileNo and date_of_open=:dateOfOpen"
						+ " and terminalid=:terminalID";

				tdHistByMobileNo = session.createQuery(hql).setParameter("MobileNo", mobileNo)
						.setParameter("dateOfOpen", tdOpenDate).setParameter("terminalID", terminalId).getResultList();

			}

			if (!tdHistByMobileNo.isEmpty()) {
				for (TransactionHistory transactionHistory : tdHistByMobileNo) {
					slno.put(transactionHistory.getSlno());
					MobileNo.put(transactionHistory.getMobileNo());
					transactionId.put(transactionHistory.getTransactionID());
					dateOfTransaction.put(transactionHistory.getDate_of_open());
					amount.put(transactionHistory.getAmount() / 100);
					remarks.put(transactionHistory.getRemark());
					transactionType.put(transactionHistory.getStatus());
					timeOfTransaction.put(transactionHistory.getTime_of_open());
					lockerNumbers.put(transactionHistory.getLockNo());
					custName.put(transactionHistory.getCustomerName());
					noOfHours.put(transactionHistory.getNo_of_hours());
					terminalID.put(transactionHistory.getTerminalid());
					passcode.put(transactionHistory.getPasscode());
					excessAmount.put(Math.round(transactionHistory.getExcess_amount() / 100));
					excessHour.put(transactionHistory.getExcess_hours());
					itemStored.put(transactionHistory.getItemsStored());
					balance.put(Math.round(transactionHistory.getBalance() / 100));
					closingDate.put(transactionHistory.getClosing_date());
					closingTime.put(transactionHistory.getClosing_time());

					double CGST = GlobalVariable.calulcateGstMethod((int) (transactionHistory.getAmount() / 100
							+ transactionHistory.getExcess_amount() / 100 + transactionHistory.getBalance() / 100),
							"CGST", transactionHistory.getTerminalid());
					double SGST = GlobalVariable.calulcateGstMethod((int) (transactionHistory.getAmount() / 100
							+ transactionHistory.getExcess_amount() / 100 + transactionHistory.getBalance() / 100),
							"SGST", transactionHistory.getTerminalid());
					double totAmount_WithoutGST = Double.parseDouble(decimalFormat
							.format((transactionHistory.getAmount() / 100 + transactionHistory.getExcess_amount() / 100
									+ transactionHistory.getBalance() / 100) + CGST + SGST));
					double totAmount_WithGST = Math.round((transactionHistory.getAmount() / 100
							+ transactionHistory.getExcess_amount() / 100 + transactionHistory.getBalance() / 100));

					CGSTArr.put(CGST);
					SGSTArr.put(SGST);
					amount_withoutGST.put(totAmount_WithGST);
					amount_witGST.put(totAmount_WithoutGST);
				}
			} else {
				tdResponseCode = "notdhistbymobbydate";
			}
		} else if (packetType.equalsIgnoreCase("retrpaysuc")) {
			
			String lockerNo = reqObj.getString("LockerNo");
			String mobileNo = reqObj.getString("MobileNo");
			Date openDate = Date.valueOf(reqObj.getString("dateOfOpen"));
			Time openTime = Time.valueOf(reqObj.getString("timeOfOpen"));

			String checkPayStatusHQL = "FROM TransactionHistory WHERE terminalid=:terminalID "
					+ "AND mobileNo=:mobileno AND lockNo=:lockerNo AND date_of_open=:dateOfOpen AND time_of_open=:timeOfOpen";

			String hql = "UPDATE TransactionHistory SET status=:status WHERE slno=:slno";

			List<TransactionHistory> tdhistory = session.createQuery(checkPayStatusHQL)
					.setParameter("terminalID", terminalId).setParameter("mobileno", mobileNo)
					.setParameter("lockerNo", lockerNo).setParameter("dateOfOpen", openDate)
					.setParameter("timeOfOpen", openTime).getResultList();

			if (!tdhistory.isEmpty()) {

				for (TransactionHistory transactionHistory : tdhistory) {
					int slIdNo = transactionHistory.getSlno();

					if (transactionHistory.getStatus().equalsIgnoreCase("retrievecnf")) {
						tdResponseCode = "paysucexist-500";
					} else {

						int update = session.createQuery(hql).setParameter("status", "retrievecnf")
								.setParameter("slno", slIdNo).executeUpdate();

						if (update > 0) {
							tdResponseCode = "updtsucc-200";
						} else {
							tdResponseCode = "updtpdayfail-202";
						}
					}
				}

			} else {
				tdResponseCode = "notd-404";
			}

		}

		if (slno.length() > 0) {

			responseObj.put("responseCode", "tdhistory");
			responseObj.put("slno", slno);
			responseObj.put("MobileNo", MobileNo);
			responseObj.put("transactionId", transactionId);
			responseObj.put("amount", amount);
			responseObj.put("remarks", remarks);
			responseObj.put("transactionType", transactionType);
			responseObj.put("dateOfTransaction", dateOfTransaction);
			responseObj.put("timeOfTransaction", timeOfTransaction);
			responseObj.put("lockers", lockerNumbers);
			responseObj.put("custName", custName);
			responseObj.put("noOfHours", noOfHours);
			responseObj.put("terminalID", terminalID);
			responseObj.put("itemStored", itemStored);
			responseObj.put("excess_amount", excessAmount);
			responseObj.put("excess_hour", excessHour);
			responseObj.put("balance", balance);
			responseObj.put("passcode", passcode);
			responseObj.put("closing_date", closingDate);
			responseObj.put("closingTime", closingTime);
			responseObj.put("CGST", CGSTArr);
			responseObj.put("SGST", SGSTArr);
			responseObj.put("amount_withGST", amount_witGST);
			responseObj.put("amount_withoutGST", amount_withoutGST);

		} else {
			responseObj.put("responseCode", tdResponseCode);
		}

		writer.append(responseObj.toString());
		session.close();
		writer.close();

	}

}
