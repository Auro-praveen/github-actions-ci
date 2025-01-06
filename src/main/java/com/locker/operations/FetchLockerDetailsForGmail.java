package com.locker.operations;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

//import jakarta.mail.Authenticator;
//import jakarta.mail.Message;
//import jakarta.mail.PasswordAuthentication;
//import jakarta.mail.Session;
//import jakarta.mail.Transport;
//import jakarta.mail.internet.InternetAddress;
//import jakarta.mail.internet.MimeMessage;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
//
//
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.auro.hibernateUtilities.HibernateUtils;
import com.locks.gloablVariable.GlobalVariable;

/**
 * Servlet implementation class FetchLockerDetailsForGmail
 */

@WebServlet("/FetchLockerDetailsForGmail")
public class FetchLockerDetailsForGmail extends HttpServlet {
	private static final long serialVersionUID = 1L;

	final String fromEmail = "praveen@aurodisplay.com"; // requires valid gmail id
	final String password = "Praveen@au6"; // correct password for gmail id

//	final String fromEmail = "info@tuckpod.com"; // requires valid gmail id 
//	final String password = "infotp2023"; // correct password for gmail id 

//	final String toEmail = "Raja@tuckpod.com"; // can be any email id 
//	final String toEmail = "praveen@aurodisplay.com"; // can be any email id

	public boolean isNoBookingTerminalsPresent = false;

	/**
	 * @see HttpServlet#HttpServlet()
	 */

	public FetchLockerDetailsForGmail() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// TODO Auto-generated method stub
//		response.addHeader("Access-Control-Allow-Origin", "*");
//		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
//		response.setCharacterEncoding("UTF-8");
		
		String respOrigin = request.getHeader("Origin");
		response.setHeader("Access-Control-Allow-Origin", respOrigin);
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, UPDATE, OPTIONS, DELETE");
//		response.setHeader("Access-Control-Allow-Headers", "Content-Type, X-Content-Type-Options, X-Frame-Options, Referrer-Policy");
		response.setHeader("Access-Control-Allow-Headers", "*");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);

		PrintWriter writer = response.getWriter();
		org.hibernate.Session session = HibernateUtils.getSession();

		JSONObject responseObj = new JSONObject();
		JSONArray allTermDetailsArr = new JSONArray();

		String zeroBookingTerminals = "";

		java.sql.Date sqlDate = new java.sql.Date(new Date().getTime() - 86400000); // need to send the details about
																					// the yesterdays details
		String siteRegQuery = "SELECT terminalid FROM SiteRegistration WHERE terminalid NOT IN (:termID)";

		List<String> allTerminalIdsList = session.createQuery(siteRegQuery).setParameter("termID", "G21")
				.getResultList();

//		System.out.println(sqlDate);

//		System.out.println("inside locker details for gmail sdfgsdfg");
		String query = "select sum(amount), count(slno), terminalid, sum(excess_amount), lockNo from TransactionHistory where date_of_open='"
				+ sqlDate
				+ "' and status not in('payFailCancel', 'payFailPaylater') and terminalid not in ('G21') group by terminalid, lockNo";

		List<Object[]> lockerDetailsList = session.createQuery(query, Object[].class).getResultList();

//		System.out.println(lockerDetailsList.isEmpty());

		Map<String, ArrayList<Object>> dailyLockDetailMap = new HashMap<>();

		for (Object[] lockDetail : lockerDetailsList) {

			JSONArray termIdDetailsArr = new JSONArray();

			if (dailyLockDetailMap.containsKey((String) lockDetail[2])) {

				ArrayList<Object> dataList = dailyLockDetailMap.get((String) lockDetail[2]);

				int totCollection = (int) dataList.get(0);
				int noOfBookings = (int) dataList.get(1);
				int excessAmount = (int) dataList.get(2);
				JSONObject lockerTypeObj = (JSONObject) dataList.get(3);

				totCollection = ((int) ((double) lockDetail[0]) / 100) + totCollection;
				noOfBookings = (new Long((long) lockDetail[1]).intValue()) + noOfBookings;
				excessAmount = ((int) ((double) lockDetail[3]) / 100) + excessAmount;

				String lockerName = (String) lockDetail[4];

				if (lockerName.contains("XL")) {

					int occurrance = (int) lockerTypeObj.get("eLarge");
					lockerTypeObj.put("eLarge", occurrance + new Long((long) lockDetail[1]).intValue());

				} else if (lockerName.contains("L")) {

					int occurrance = (int) lockerTypeObj.get("large");
					lockerTypeObj.put("large", occurrance + new Long((long) lockDetail[1]).intValue());

				} else if (lockerName.contains("M")) {

					int occurrance = (int) lockerTypeObj.get("medium");
					lockerTypeObj.put("medium", occurrance + new Long((long) lockDetail[1]).intValue());

				} else if (lockerName.contains("S")) {

					int occurrance = (int) lockerTypeObj.get("small");
					lockerTypeObj.put("small", occurrance + new Long((long) lockDetail[1]).intValue());

				}

				dataList.set(0, totCollection);
				dataList.set(1, noOfBookings);
				dataList.set(2, excessAmount);
				dataList.set(3, lockerTypeObj);

//				dailyLockDetailMap.put((String) lockDetail[2], dataList);

			} else {

				ArrayList<Object> dataList = new ArrayList<>();
				dataList.add(0, (int) ((double) lockDetail[0]) / 100); // total collection
				dataList.add(1, new Long((long) lockDetail[1]).intValue()); // no of bookings
//				dataList.add((String) lockDetail[2]);		// terminal id
				dataList.add(2, (int) ((double) lockDetail[3]) / 100); // excess amount

				// for details about locker No small, medium, large, extraLarge

				JSONObject lockerTypeObj = new JSONObject();

				String lockerName = (String) lockDetail[4];

				if (lockerName.contains("XL")) {
					lockerTypeObj.put("small", 0);
					lockerTypeObj.put("medium", 0);
					lockerTypeObj.put("large", 0);
					lockerTypeObj.put("eLarge", new Long((long) lockDetail[1]).intValue());
				} else if (lockerName.contains("L")) {
					lockerTypeObj.put("small", 0);
					lockerTypeObj.put("medium", 0);
					lockerTypeObj.put("large", new Long((long) lockDetail[1]).intValue());
					lockerTypeObj.put("eLarge", 0);
				} else if (lockerName.contains("M")) {
					lockerTypeObj.put("small", 0);
					lockerTypeObj.put("medium", new Long((long) lockDetail[1]).intValue());
					lockerTypeObj.put("large", 0);
					lockerTypeObj.put("eLarge", 0);
				} else if (lockerName.contains("S")) {
					lockerTypeObj.put("small", new Long((long) lockDetail[1]).intValue());
					lockerTypeObj.put("medium", 0);
					lockerTypeObj.put("large", 0);
					lockerTypeObj.put("eLarge", 0);
				}

				dataList.add(3, lockerTypeObj);
				dailyLockDetailMap.put((String) lockDetail[2], dataList);

			}

			int totCollection = (int) ((double) lockDetail[0]) / 100;
			int totLocksCount = new Long((long) lockDetail[1]).intValue();
			String terminalID = (String) lockDetail[2];

			termIdDetailsArr.put(terminalID);
			termIdDetailsArr.put(totLocksCount);
			termIdDetailsArr.put(totCollection);

			allTermDetailsArr.put(termIdDetailsArr);

		}

		for (String terminalId : allTerminalIdsList) {

			boolean isTerminalHasBooking = false;
			if (!dailyLockDetailMap.isEmpty()) {
				for (String transactionHistTermid : dailyLockDetailMap.keySet()) {

					if (terminalId.equals(transactionHistTermid)) {
						isTerminalHasBooking = true;
						break;
					}

				}
			} else {
				isTerminalHasBooking = false;
			}

			if (!isTerminalHasBooking) {
				zeroBookingTerminals += " TerminalId :- " + terminalId + ",  Bookings :- 0 \n";

				if (!isTerminalHasBooking) {
					isNoBookingTerminalsPresent = true;
				}
			}

		}

		System.out.println(dailyLockDetailMap);

		System.out.println("=-=-");
		System.out.println(zeroBookingTerminals);
		System.out.println("=-=-");

		if (allTermDetailsArr.length() > 1000) {
			responseObj.put("responseCode", "tdtogmail-200");
			responseObj.put("data", allTermDetailsArr);
		} else {
			responseObj.put("responseCode", "tdtogmail-404");
		}

		try {

			String hql = "SELECT terminalid, lockNo, mobileNo, customerName, date_of_open, time_of_open  from TransactionDetails";
			List<Object[]> activeTransactionDetails = session.createQuery(hql, Object[].class).getResultList();

			if (!activeTransactionDetails.isEmpty()) {

				String activeTransactions = "There Are Active Transactions From The Previus Date \n\n";

				for (Object[] objects : activeTransactionDetails) {

					String terminalId = (String) objects[0];
					String lockNo = (String) objects[1];
					String mobileNo = (String) objects[2];
					String custName = (String) objects[3];
					java.sql.Date dateOfOpen = (java.sql.Date) objects[4];
					Time timeOfOpen = (Time) objects[5];

//					System.out.println(terminalId + " :: " + lockNo + " :: " + mobileNo + "  ::  " + custName + "  ::  "
//							+ dateOfOpen + "  ::  " + timeOfOpen);

					activeTransactions += "terminalId :- " + terminalId + ", mobileNo :- " + mobileNo + ", custName :- "
							+ custName + ", Date of Open :- " + dateOfOpen + ", Time Of Open :- " + timeOfOpen + "\n";
				}

//				System.out.println(activeTransactions);
				boolean res = forwardMailHere(activeTransactions);

//				boolean res = false;

				if (res) {
					try {
						Thread.sleep(5000);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}

			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		try {

			Thread.sleep(2000);

			boolean isPaymentUsageMailSent = getAllPaymentTypesUsedMsg(session, sqlDate);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

//		boolean isMailSent = true;
		boolean isMailSent = aligningMailDetails(dailyLockDetailMap, sqlDate, zeroBookingTerminals);

		if (isMailSent) {
			writer.print("mailsent-200");
		} else {
			writer.print("mailsent-404");
		}

		writer.close();
		session.close();

	}


	
	public boolean aligningMailDetails(Map<String, ArrayList<Object>> lockDetailsMap, java.sql.Date sqlDate,
			String zeroBookingTerminals) {

		boolean isMessageSent = false;
//		System.out.println("SimpleEmail Start");

// 	    Date date = new Date();
		String lockDetailFormatStr = "Locker Occupacy Details terminal_wise for date : " + sqlDate + "\n\n";
		String lockerDetailsFormatForLessBookings = null;
		boolean isLessLockerFirstTime = true;

		for (Map.Entry<String, ArrayList<Object>> entryMap : lockDetailsMap.entrySet()) {

			ArrayList<Object> valList = entryMap.getValue();
			JSONObject lockerDetailsObj = (JSONObject) valList.get(3);

			int totAmount = ((int) valList.get(0)) + ((int) valList.get(2));

			if ((int) valList.get(1) > 5) {
				lockDetailFormatStr += "TerminalID : " + entryMap.getKey() + "\n";
				lockDetailFormatStr += "No of Bookings : " + (int) valList.get(1) + ". \n";
				lockDetailFormatStr += "Base Amount : " + (int) valList.get(0) + ".00 Rs , Excess Amount : "
						+ (int) valList.get(2) + ".00 Rs , Total Amount : " + totAmount + ".00 Rs \n";

				lockDetailFormatStr += "Types Of Lockers Used \n";
				lockDetailFormatStr += "Small : " + lockerDetailsObj.getInt("small") + ", ";
				lockDetailFormatStr += "Medium : " + lockerDetailsObj.getInt("medium") + ", ";
				lockDetailFormatStr += "Large : " + lockerDetailsObj.getInt("large") + ", ";
				lockDetailFormatStr += "eLarge : " + lockerDetailsObj.getInt("eLarge") + " \n\n";
			} else {

				if (isLessLockerFirstTime) {
					lockerDetailsFormatForLessBookings = "Locker Booking Details For Five Or Less Bookings : " + sqlDate
							+ "\n\n";
					isLessLockerFirstTime = false;
				}
				lockerDetailsFormatForLessBookings += "TerminalID : " + entryMap.getKey() + "\n";
				lockerDetailsFormatForLessBookings += "No of Bookings : " + (int) valList.get(1) + ". \n";
				lockerDetailsFormatForLessBookings += "Base Amount : " + (int) valList.get(0)
						+ ".00 Rs , Excess Amount : " + (int) valList.get(2) + ".00 Rs , Total Amount : " + totAmount
						+ ".00 Rs \n";

				lockerDetailsFormatForLessBookings += "Types Of Lockers Used \n";
				lockerDetailsFormatForLessBookings += "Small : " + lockerDetailsObj.getInt("small") + ", ";
				lockerDetailsFormatForLessBookings += "Medium : " + lockerDetailsObj.getInt("medium") + ", ";
				lockerDetailsFormatForLessBookings += "Large : " + lockerDetailsObj.getInt("large") + ", ";
				lockerDetailsFormatForLessBookings += "eLarge : " + lockerDetailsObj.getInt("eLarge") + " \n \n";
			}

		}

		isMessageSent = forwardMailHere(lockDetailFormatStr);

		System.out.println("-------------=============-----------");

		if (isMessageSent && !isLessLockerFirstTime) {
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			System.out.println(zeroBookingTerminals);
			forwardMailHere(lockerDetailsFormatForLessBookings + zeroBookingTerminals + "\n\n");
		} else if (isNoBookingTerminalsPresent) {
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			System.out.println(zeroBookingTerminals);
			forwardMailHere("Locker Booking Details For Five Or Less Bookings : " + sqlDate + "\n \n"
					+ zeroBookingTerminals + "\n");
		}

		return isMessageSent;
	}

	public boolean forwardMailHere(String lockDetailFormatStr) {

//		System.out.println("================ inisde forward mail here method");
//		System.out.println(lockDetailFormatStr);

		boolean isMessageSent = false;

//		System.out.println("TLSEmail Start");
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com"); // SMTP Host
		props.put("mail.smtp.port", "587"); // TLS Port
		props.put("mail.smtp.auth", "true"); // enable authentication
		props.put("mail.smtp.starttls.enable", "true"); // enable STARTTLS

		// create Authenticator object to pass in Session.getInstance argument
		Authenticator auth = new Authenticator() {
			// override the getPasswordAuthentication method
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail, password);
			}
		};

		Session session = Session.getInstance(props, auth);

		session.setDebug(true);

		try {

			//

			MimeMessage message = new MimeMessage(session);
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(GlobalVariable.toGmail));	//GlobalVariable.toGmail
//			message.addRecipient(Message.RecipientType.TO, new InternetAddress("praveen@aurodisplay.com"));
			message.setSubject("Locker Details ");
			message.setText(lockDetailFormatStr);
//			System.out.println("message sendingsdsd  !!");

			// send message
			Transport.send(message);
			isMessageSent = true;
			System.out.println("mail sent successfully");

		} catch (Exception e) {

			// TODO: handle exception

			e.printStackTrace();

		}

		return isMessageSent;

	}

	private boolean getAllPaymentTypesUsedMsg(org.hibernate.Session session, java.sql.Date currentDate) {

		boolean isMailSent = false;

//		String invoiceHql = "SELECT SUM(table.orderID LIKE 'QR%') AS QR_pays, "
//				+ "SUM(table.orderID LIKE 'order%') AS order_pays FROM amountreceiveddetails as table";
//				+ " WHERE received_date=:RECDATE";

//		String invoiceHql = "SELECT orderID "
//				+ "FROM amountreceiveddetails WHERE orderID LIKE 'QR%'";

		System.out.println(currentDate);

		String invoiceHql = "SELECT " + "COUNT(CASE WHEN orderID LIKE 'QR%' THEN 1 END) AS qr_data, "
				+ "COUNT(CASE WHEN orderID LIKE 'order%' THEN 1 END) AS order_data "
				+ "FROM amountreceiveddetails WHERE received_date=:RECDATE";

		try {

//			List<Integer> getCount = (ArrayList<Integer>) session.createQuery(invoiceHql)
////					.setParameter("RECDATE", currentDate)
//					.getResultList();

			Object[] result = (Object[]) session.createNativeQuery(invoiceHql).setParameter("RECDATE", currentDate)
					.getSingleResult();

			if (result.length > 0) {

				String firstRes = result[0].toString();
				String secondRes = result[1].toString();

				System.out.println(firstRes);
				System.out.println(secondRes);

				String mailLine = "Payment Methods Type for :" + currentDate + "\n"
						+ "payments using QR_CODE (touchscreen) : " + firstRes + " \n"
						+ "Payments using regular payment Methods (mobile) : " + secondRes;

				isMailSent = forwardMailHere(mailLine);
				if (isMailSent) {

					try {
						Thread.sleep(5000);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}

				}
			} else {
				System.out.println("something went wrong!!! here");
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return isMailSent;

	}

}
