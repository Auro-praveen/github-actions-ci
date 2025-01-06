package com.auroLocker.fetch.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
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

import com.auro.beans.ManualOverride;
import com.auro.beans.PartialRetrieveData;
import com.auro.beans.SiteRegistration;
import com.auro.beans.TransactionDetails;
import com.auro.hibernateUtilities.HibernateUtils;
import com.locks.gloablVariable.GlobalVariable;

/**
 * Servlet implementation class FetchTransactionDetails
 * 
 * @author Praveen
 * 
 *         current live transactions and some operations are given here fetch
 *         occording to terminal
 * 
 *         oct 05 - 2023 gst calulation and total amount calculation
 * 
 */

@WebServlet("/FetchTransactionDetails")
public class FetchTransactionDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FetchTransactionDetails() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
//		 System.out.println("inside get");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");

		PrintWriter writer = response.getWriter();
		TransactionDetails transactionDetails = new TransactionDetails();

		Session session = HibernateUtils.getSession();
		session.beginTransaction();
		ArrayList<TransactionDetails> tdList = new ArrayList<TransactionDetails>();
		JSONObject tdJsonObj = new JSONObject();

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
		JSONArray isPartialOpen = new JSONArray();

		try {
			tdList = (ArrayList<TransactionDetails>) session.createQuery("FROM TransactionDetails").getResultList();

			// System.out.println("some error occured");
			for (TransactionDetails td : tdList) {

				if (!td.getStatus().equalsIgnoreCase("transint")) {
					// System.out.println("transit locker : "+td.getStatus());
					slno.put(td.getSlno());
					terminalId.put(td.getTerminalid());
					custName.put(td.getCustomerName());
					amount.put(td.getAmount());
					mobileNumber.put(td.getMobileNo());
					dateOFOpen.put(td.getDate_of_open());
					timeOfOpen.put(td.getTime_of_open());
					transactionId.put(td.getTransactionID());
					status.put(td.getStatus());
					noOfHours.put(td.getNo_of_hours());
					lockNo.put(td.getLockNo());

				} else {
					// System.out.println("transit locker : "+td.getStatus());
				}

//				tdJsonObj.put(String.valueOf(td.getSlno()), jsonArr);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		tdJsonObj.put("slno", slno);
		tdJsonObj.put("custName", custName);
		tdJsonObj.put("mobileNum", mobileNumber);
		tdJsonObj.put("amount", amount);
		tdJsonObj.put("terminalId", terminalId);
		tdJsonObj.put("transactionId", transactionId);
		tdJsonObj.put("dateOfOpen", dateOFOpen);
		tdJsonObj.put("timeOfOpen", timeOfOpen);
		tdJsonObj.put("status", status);
		tdJsonObj.put("noOfHours", noOfHours);
		tdJsonObj.put("lockNo", lockNo);

		if (tdJsonObj.length() > 0) {
			writer.println(tdJsonObj.toString());
		} else {
			writer.println("{status:failed}");
		}
		// System.out.println("json object : "+tdJsonObj);
		writer.flush();
		writer.close();
		session.close();

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// transaction history including manual override
//		 System.out.println("inside fetch transaction details");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		TransactionDetails transactionDetails = new TransactionDetails();

		Session session = HibernateUtils.getSession();
		session.beginTransaction();
		ArrayList<TransactionDetails> tdList = new ArrayList<TransactionDetails>();

		// for fetching the live transaction details

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
		JSONArray isPartialOpen = new JSONArray();

		JSONArray CGSTArr = new JSONArray();
		JSONArray SGSTArr = new JSONArray();
		JSONArray amount_withoutGST = new JSONArray();
		JSONArray amount_witGST = new JSONArray();

		String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines()
				.collect(Collectors.joining("/"));
		JSONObject reqObj = new JSONObject(jsonBody);
		// System.out.println(reqObj);

		JSONObject lockerNoObj = new JSONObject();
		String type = reqObj.getString("PacketType");

		if (type.equalsIgnoreCase("getLockerNumber")) {
			String mobileNo = reqObj.getString("mobileNo");
			// System.out.println(mobileNo);

			try {

				tdList = (ArrayList<TransactionDetails>) session
						.createQuery("select lockNo from TransactionDetails where mobileNo=" + mobileNo)
						.getResultList();
				// System.out.println(tdList);

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			if (tdList.isEmpty()) {
				lockerNoObj.put("status", "fail");
			} else {
				lockerNoObj.put("status", "success");
				lockerNoObj.put("lockNo", tdList);
			}
		} else if (type.equalsIgnoreCase("getprogresslock")) {
			String terminalID = reqObj.getString("terminalID");
			JSONArray lockers = new JSONArray();

			try {

				tdList = (ArrayList<TransactionDetails>) session
						.createQuery("from TransactionDetails where terminalid='" + terminalID + "'").getResultList();

				for (TransactionDetails tdLockers : tdList) {
					lockers.put(tdLockers.getLockNo());
					// System.out.println("terminal ID : "+tdLockers.getTerminalid());
				}

				if (lockers.isNull(0)) {
					lockerNoObj.put("responseCode", "NOLOCK-201");
				} else {
					lockerNoObj.put("responseCode", "TDLOCK-200");
					lockerNoObj.put("LOCKERS", lockers);
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				lockerNoObj.put("responseCode", "NOLOCK-201");
			}

		} else if (type.equalsIgnoreCase("gettermid")) {

			try {

//				tdList = (ArrayList<TransactionDetails>) session.createQuery("from TransactionDetails").getResultList();
				List<SiteRegistration> termDetails = (ArrayList<SiteRegistration>) session
						.createQuery("select distinct terminalid from SiteRegistration").getResultList();
				// System.out.println("got : "+tdList);

				if (termDetails.isEmpty()) {
					lockerNoObj.put("responseCode", "notd-201");
				} else {
					lockerNoObj.put("responseCode", "avltd-200");
					lockerNoObj.put("terminalID", termDetails);
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				lockerNoObj.put("responseCode", "no-201");
			}

		} else if (type.equalsIgnoreCase("livetd")) {

//			ArrayList<TransactionDetails> tdList = new ArrayList<TransactionDetails>();

			// System.out.println("inside livTd terminal Id");

			String termId = reqObj.getString("terminalID");

			try {

				String partialHQL = "from PartialRetrieveData where terminalID=:terminalID and dateOfopen=:openDate and mobileNo=:mobileNo and locNo=:lockerNo";
				tdList = (ArrayList<TransactionDetails>) session
						.createQuery("FROM TransactionDetails where terminalid='" + termId + "'").getResultList();

				if (!tdList.isEmpty()) {

					for (TransactionDetails td : tdList) {
						int transitTime = 0;

						if (td.getStatus().equalsIgnoreCase("transint")) {
							transitTime = compareTransitMinute(td.getTime_of_open(), td.getDate_of_open());
						}

						List<PartialRetrieveData> partialData = session.createQuery(partialHQL)
								.setParameter("terminalID", td.getTerminalid())
								.setParameter("openDate", td.getDate_of_open())
								.setParameter("mobileNo", td.getMobileNo()).setParameter("lockerNo", td.getLockNo())
								.getResultList();

						if (partialData.isEmpty()) {
							isPartialOpen.put("false");
						} else {
							String isUserPartRetrieved = "false";
							
							for (PartialRetrieveData partData : partialData) {
								
								if (partData.getOpenStatus().equalsIgnoreCase("partialopen")) {
//									isPartialOpen.put("true");
									isUserPartRetrieved = "true";
									break;
									
								} else {
									isUserPartRetrieved = "false";
								}
									
//								partData.getOpenStatus();
								
							}
//							isPartialOpen.put("true");
							isPartialOpen.put(isUserPartRetrieved);
						}

						if (transitTime >= 8) {

							slno.put(td.getSlno());
							terminalId.put(td.getTerminalid());
							custName.put(td.getCustomerName());
							amount.put(td.getAmount() / 100);
							mobileNumber.put(td.getMobileNo());
							dateOFOpen.put(td.getDate_of_open());
							timeOfOpen.put(td.getTime_of_open());
							transactionId.put(td.getTransactionID());
							status.put("transit");
							noOfHours.put(td.getNo_of_hours());
							lockNo.put(td.getLockNo());
							itemStored.put(td.getItemsStored());
							passcode.put(td.getPasscode());
							balance.put(Math.round(td.getBalance() / 100));
							excessAmount.put(Math.round(td.getExcess_amount() / 100));
							excessHour.put(td.getExcess_hours());

						} else {

							slno.put(td.getSlno());
							terminalId.put(td.getTerminalid());
							custName.put(td.getCustomerName());
							amount.put(td.getAmount() / 100);
							mobileNumber.put(td.getMobileNo());
							dateOFOpen.put(td.getDate_of_open());
							timeOfOpen.put(td.getTime_of_open());
							transactionId.put(td.getTransactionID());
							status.put(td.getStatus());
							noOfHours.put(td.getNo_of_hours());
							lockNo.put(td.getLockNo());
							itemStored.put(td.getItemsStored());
							passcode.put(td.getPasscode());
							balance.put(Math.round(td.getBalance() / 100));
							excessAmount.put(Math.round(td.getExcess_amount() / 100));
							excessHour.put(td.getExcess_hours());

						}

						DecimalFormat decimalFormat = new DecimalFormat("#.00");

						double CGST = GlobalVariable
								.calulcateGstMethod((int) (td.getAmount() / 100 + td.getExcess_amount() / 100 + td.getBalance()/100), "CGST", td.getTerminalid());
						
						double SGST = GlobalVariable
								.calulcateGstMethod((int) (td.getAmount() / 100 + td.getExcess_amount() / 100 + td.getBalance()/100), "SGST", td.getTerminalid());
						
						double totAmount_WithoutGST = Double.parseDouble(decimalFormat
								.format(((td.getAmount() / 100 + td.getExcess_amount() / 100 + td.getBalance()/100) + CGST + SGST)));
						
						double totAmount_WithGST = Math.round(td.getAmount() / 100 + td.getExcess_amount() / 100 + td.getBalance()/100);

						CGSTArr.put(CGST);
						SGSTArr.put(SGST);
						amount_withoutGST.put(totAmount_WithoutGST);
						amount_witGST.put(totAmount_WithGST);

						// uncomment if trnasit is not required in live transaction details

//						if(!td.getStatus().equalsIgnoreCase("transint")) {
//							slno.put(td.getSlno());
//							terminalId.put(td.getTerminalid());
//							custName.put(td.getCustomerName());
//							amount.put(td.getAmount());
//							mobileNumber.put(td.getMobileNo());
//							dateOFOpen.put(td.getDate_of_open());
//							timeOfOpen.put(td.getTime_of_open());
//							transactionId.put(td.getTransactionID());
//							status.put(td.getStatus());
//							noOfHours.put(td.getNo_of_hours());
//							lockNo.put(td.getLockNo());
//							itemStored.put(td.getItemsStored());
//							passcode.put(td.getPasscode());
//							balance.put(td.getBalance());
//							excessAmount.put(td.getExcess_amount());
//							excessHour.put(td.getExcess_hours());
//						} 
//						tdJsonObj.put(String.valueOf(td.getSlno()), jsonArr);

					}

					if (slno.length() > 0) {
//						GlobalVariable.calulcateGstMethod();
					}

				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			lockerNoObj.put("responseCode", "liveitems");
			lockerNoObj.put("slno", slno);
			lockerNoObj.put("custName", custName);
			lockerNoObj.put("mobileNum", mobileNumber);
			lockerNoObj.put("amount", amount);
			lockerNoObj.put("terminalId", terminalId);
			lockerNoObj.put("transactionId", transactionId);
			lockerNoObj.put("dateOfOpen", dateOFOpen);
			lockerNoObj.put("timeOfOpen", timeOfOpen);
			lockerNoObj.put("status", status);
			lockerNoObj.put("noOfHours", noOfHours);
			lockerNoObj.put("lockNo", lockNo);
			lockerNoObj.put("passcode", passcode);
			lockerNoObj.put("itemStored", itemStored);
			lockerNoObj.put("excess_amount", excessAmount);
			lockerNoObj.put("excess_hour", excessHour);
			lockerNoObj.put("balance", balance);
			lockerNoObj.put("partialOpen", isPartialOpen);
			lockerNoObj.put("CGST", CGSTArr);
			lockerNoObj.put("SGST", SGSTArr);
			lockerNoObj.put("amount_withGST", amount_witGST);
			lockerNoObj.put("amount_withoutGST", amount_withoutGST);

			if (lockerNoObj.length() > 0) {
				lockerNoObj.put("responseCode", "liveitems");
			} else {
				lockerNoObj.put("responseCode", "failed");
			}

//			System.out.println("json object : "+lockerNoObj);
//			writer.flush();
//			writer.close();
//			session.close();
		} else if (type.equalsIgnoreCase("transitlocks")) {

//			JSONArray terminalIdJsonArr = new JSONArray();

			HashSet<String> terminalIdHash = new HashSet<>();

			// get all the lockers which are in transaction details from more that 8 mins
			String hql = "from TransactionDetails where status='transint'";
			List<TransactionDetails> transactionTransitDetails = session.createQuery(hql).getResultList();

			for (TransactionDetails transactionTransit : transactionTransitDetails) {
				int diffTime = compareTransitMinute(transactionTransit.getTime_of_open(),
						transactionTransit.getDate_of_open());
//				System.out.println("----diffTime -- "+diffTime);
				if (diffTime >= 8) {
					terminalIdHash.add(transactionTransit.getTerminalid());
				}

//				System.out.println("terminalID : "+transactionTransit.getTerminalid());
			}

			if (terminalIdHash.size() > 0) {
				lockerNoObj.put("responseCode", "transit-200");
				lockerNoObj.put("terminalId", terminalIdHash);
			} else {
				lockerNoObj.put("responseCode", "transit-500");
			}
		} else if (type.equalsIgnoreCase("strpaysuc")) {

			String termId = reqObj.getString("terminalID");
			String mobNo = reqObj.getString("MobileNo");
			String lockerNo = reqObj.getString("LockerNo");
			String responseCode = null;

			String checkPayStatusHQL = "FROM TransactionDetails WHERE terminalid=:terminalID "
					+ "AND mobileNo=:mobileno AND lockNo=:lockerNo";

			String hql = "UPDATE TransactionDetails SET status=:status WHERE slno=:slno";

			List<TransactionDetails> td = session.createQuery(checkPayStatusHQL).setParameter("terminalID", termId)
					.setParameter("mobileno", mobNo).setParameter("lockerNo", lockerNo).getResultList();

			if (!td.isEmpty()) {
				for (TransactionDetails tDetails : td) {
					int slIdNo = tDetails.getSlno();

					if (tDetails.getStatus().equalsIgnoreCase("paymentSuccess")) {
						responseCode = "paysucexist-500";
					} else {
						try {

							int update = session.createQuery(hql).setParameter("status", "paymentSuccess")
									.setParameter("slno", slIdNo).executeUpdate();

							if (update > 0) {
								responseCode = "updtsucc-200";
							} else {
								responseCode = "updtpdayfail-202";
							}

						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();

						}

					}
				}
			} else {
				responseCode = "notd-404";
			}

			lockerNoObj.put("responseCode", responseCode);

		} else if(type.equalsIgnoreCase("getTDbyMobileNo")) {
			
			String mobileNo = reqObj.getString("mobileNo");

			try {

				String partialHQL = "from PartialRetrieveData where terminalID=:terminalID and dateOfopen=:openDate and mobileNo=:mobileNo and locNo=:lockerNo";
				
				tdList = (ArrayList<TransactionDetails>) session
						.createQuery("FROM TransactionDetails where mobileNo='" + mobileNo + "'").getResultList();

				if (!tdList.isEmpty()) {

					for (TransactionDetails td : tdList) {
						int transitTime = 0;

						if (td.getStatus().equalsIgnoreCase("transint")) {
							transitTime = compareTransitMinute(td.getTime_of_open(), td.getDate_of_open());
						}

						List<PartialRetrieveData> partialData = session.createQuery(partialHQL)
								.setParameter("terminalID", td.getTerminalid())
								.setParameter("openDate", td.getDate_of_open())
								.setParameter("mobileNo", td.getMobileNo()).setParameter("lockerNo", td.getLockNo())
								.getResultList();

						if (partialData.isEmpty()) {
							isPartialOpen.put("false");
						} else {
							String isUserPartRetrieved = "false";
							
							for (PartialRetrieveData partData : partialData) {
								
								if (partData.getOpenStatus().equalsIgnoreCase("partialopen")) {
//									isPartialOpen.put("true");
									isUserPartRetrieved = "true";
									break;
									
								} else {
									isUserPartRetrieved = "false";
								}
									
//								partData.getOpenStatus();
								
							}
//							isPartialOpen.put("true");
							isPartialOpen.put(isUserPartRetrieved);
						}

						if (transitTime >= 8) {

							slno.put(td.getSlno());
							terminalId.put(td.getTerminalid());
							custName.put(td.getCustomerName());
							amount.put(td.getAmount() / 100);
							mobileNumber.put(td.getMobileNo());
							dateOFOpen.put(td.getDate_of_open());
							timeOfOpen.put(td.getTime_of_open());
							transactionId.put(td.getTransactionID());
							status.put("transit");
							noOfHours.put(td.getNo_of_hours());
							lockNo.put(td.getLockNo());
							itemStored.put(td.getItemsStored());
							passcode.put(td.getPasscode());
							balance.put(Math.round(td.getBalance() / 100));
							excessAmount.put(Math.round(td.getExcess_amount() / 100));
							excessHour.put(td.getExcess_hours());

						} else {

							slno.put(td.getSlno());
							terminalId.put(td.getTerminalid());
							custName.put(td.getCustomerName());
							amount.put(td.getAmount() / 100);
							mobileNumber.put(td.getMobileNo());
							dateOFOpen.put(td.getDate_of_open());
							timeOfOpen.put(td.getTime_of_open());
							transactionId.put(td.getTransactionID());
							status.put(td.getStatus());
							noOfHours.put(td.getNo_of_hours());
							lockNo.put(td.getLockNo());
							itemStored.put(td.getItemsStored());
							passcode.put(td.getPasscode());
							balance.put(Math.round(td.getBalance() / 100));
							excessAmount.put(Math.round(td.getExcess_amount() / 100));
							excessHour.put(td.getExcess_hours());

						}

						DecimalFormat decimalFormat = new DecimalFormat("#.00");

						double CGST = GlobalVariable
								.calulcateGstMethod((int) (td.getAmount() / 100 + td.getExcess_amount() / 100 + td.getBalance()/100), "CGST", td.getTerminalid());
						
						double SGST = GlobalVariable
								.calulcateGstMethod((int) (td.getAmount() / 100 + td.getExcess_amount() / 100 + td.getBalance()/100), "SGST", td.getTerminalid());
						
						double totAmount_WithoutGST = Double.parseDouble(decimalFormat
								.format(((td.getAmount() / 100 + td.getExcess_amount() / 100 + td.getBalance()/100) + CGST + SGST)));
						
						double totAmount_WithGST = Math.round(td.getAmount() / 100 + td.getExcess_amount() / 100 + td.getBalance()/100);

						CGSTArr.put(CGST);
						SGSTArr.put(SGST);
						amount_withoutGST.put(totAmount_WithoutGST);
						amount_witGST.put(totAmount_WithGST);

						// uncomment if trnasit is not required in live transaction details

//						if(!td.getStatus().equalsIgnoreCase("transint")) {
//							slno.put(td.getSlno());
//							terminalId.put(td.getTerminalid());
//							custName.put(td.getCustomerName());
//							amount.put(td.getAmount());
//							mobileNumber.put(td.getMobileNo());
//							dateOFOpen.put(td.getDate_of_open());
//							timeOfOpen.put(td.getTime_of_open());
//							transactionId.put(td.getTransactionID());
//							status.put(td.getStatus());
//							noOfHours.put(td.getNo_of_hours());
//							lockNo.put(td.getLockNo());
//							itemStored.put(td.getItemsStored());
//							passcode.put(td.getPasscode());
//							balance.put(td.getBalance());
//							excessAmount.put(td.getExcess_amount());
//							excessHour.put(td.getExcess_hours());
//						} 
//						tdJsonObj.put(String.valueOf(td.getSlno()), jsonArr);

					}

					if (slno.length() > 0) {
//						GlobalVariable.calulcateGstMethod();
					}

				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			lockerNoObj.put("responseCode", "TD-200");
			lockerNoObj.put("slno", slno);
			lockerNoObj.put("custName", custName);
			lockerNoObj.put("mobileNum", mobileNumber);
			lockerNoObj.put("amount", amount);
			lockerNoObj.put("terminalId", terminalId);
			lockerNoObj.put("transactionId", transactionId);
			lockerNoObj.put("dateOfOpen", dateOFOpen);
			lockerNoObj.put("timeOfOpen", timeOfOpen);
			lockerNoObj.put("status", status);
			lockerNoObj.put("noOfHours", noOfHours);
			lockerNoObj.put("lockNo", lockNo);
			lockerNoObj.put("passcode", passcode);
			lockerNoObj.put("itemStored", itemStored);
			lockerNoObj.put("excess_amount", excessAmount);
			lockerNoObj.put("excess_hour", excessHour);
			lockerNoObj.put("balance", balance);
			lockerNoObj.put("partialOpen", isPartialOpen);
			lockerNoObj.put("CGST", CGSTArr);
			lockerNoObj.put("SGST", SGSTArr);
			lockerNoObj.put("amount_withGST", amount_witGST);
			lockerNoObj.put("amount_withoutGST", amount_withoutGST);

			if (lockerNoObj.length() > 0) {
				lockerNoObj.put("responseCode", "TD-200");
			} else {
				lockerNoObj.put("responseCode", "TD-404");
			}
		}

		else {
			// System.out.println("inside else part");
			try {

				ManualOverride manualOverride = new ManualOverride();

				// uncomment it if lockernumber coming from front end is a json array
//				JSONArray str = reqObj.getJSONArray("LOCKNO");
				String str = reqObj.getString("LOCKNO");
//				String val = str.toString();
//				val = val.replace("[", "");
//				val = val.replace("]", "");
//				val = val.replace("\"", "");
//				// System.out.println(val);

				manualOverride.setLockNo(str);
				manualOverride.setMobileNo(reqObj.getString("MobileNo"));
				manualOverride.setReason(reqObj.getString("reason"));
				manualOverride.setRemarks(reqObj.getString("remarks"));
				manualOverride.setRequestType(reqObj.getString("ReqType"));
				manualOverride.setTerminalId(reqObj.getString("terminalID"));
				manualOverride.setTransType(reqObj.getString("TransType"));

				int resp = (int) session.save(manualOverride);
				session.getTransaction().commit();

				if (resp > 0) {
					// System.out.println("stored successfully : "+resp);
					lockerNoObj.put("status", "success");
				} else {
					lockerNoObj.put("status", "fail");
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		// System.out.println(lockerNoObj);
		writer.append(lockerNoObj.toString());
		writer.flush();
		writer.close();
		session.close();
	}

	public int compareTransitMinute(Time sqlTime, Date sqlDate) {

		int diffTime = 0;
//		System.out.println(sqlTime);
//		System.out.println(sqlDate);

		Timestamp timeStamp = Timestamp.valueOf(sqlDate + " " + sqlTime);

		long currentSysTime = System.currentTimeMillis();
		long transitTimeInMilli = timeStamp.getTime();
//		System.out.println("sql time --- : "+currentSysTime);
//		System.out.println("sql time --- : "+transitTimeInMilli);
		long diffMilliSec = currentSysTime - transitTimeInMilli;

		diffTime = (int) TimeUnit.MILLISECONDS.toMinutes(diffMilliSec);
		return diffTime;

	}

}
