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
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;

import com.auro.beans.SiteRegistration;
import com.auro.beans.TransactionHistory;
import com.auro.beans.invoiceDetails;
import com.auro.hibernateUtilities.HibernateUtils;
import com.locks.gloablVariable.GlobalVariable;

/**
 * Servlet implementation class FetchHistoryForMallAuthFromInvoice
 */

@WebServlet("/FetchHistoryForMallAuthFromInvoice")
public class FetchHistoryForMallAuthFromInvoice extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FetchHistoryForMallAuthFromInvoice() {
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
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");

		String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines()
				.collect(Collectors.joining("/"));
		JSONObject reqObj = new JSONObject(jsonBody);
		// System.out.println(reqObj);

		PrintWriter writer = response.getWriter();

		System.out.println("Got requesttt");

		Session session = HibernateUtils.getSession();
		Transaction transaction = session.beginTransaction();

//		String siteName = reqObj.get("siteName").toString();
		String siteLocation = reqObj.get("siteLocation").toString();
		String packetType = reqObj.getString("PacketType");

		// for converting json String to list

		String selectedSiteNames = reqObj.get("siteName").toString().replace("[", "").replace("]", "").replace("\"",
				"");

		Collection<String> siteNameList = new ArrayList<String>(Arrays.asList(selectedSiteNames.split(",")));

		JSONObject responseObj = new JSONObject();

		JSONArray slno = new JSONArray();
		JSONArray MobileNo = new JSONArray();
		JSONArray dateOfTransaction = new JSONArray();
		JSONArray timeOfTransaction = new JSONArray();
		JSONArray amount = new JSONArray();
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
		JSONArray CGSTArr = new JSONArray();
		JSONArray SGSTArr = new JSONArray();
//		JSONArray amtWithoutTax = new JSONArray();
		JSONArray totAmntExcludingTax = new JSONArray();
		JSONArray totAmountIncludingTax = new JSONArray();
		JSONArray partRetAmountArr = new JSONArray();

		JSONObject totalCalculations = new JSONObject();

		if (packetType.equalsIgnoreCase("cr-hist")) {

			ArrayList<String> terminalIdList = new ArrayList<>();

			String siteRegHql = "from SiteRegistration where siteName IN (:sName) and city=:siteLoc";

			List<SiteRegistration> siteRegObj = session.createQuery(siteRegHql).setParameterList("sName", siteNameList)
					.setParameter("siteLoc", siteLocation).getResultList();

			if (!siteRegObj.isEmpty()) {
				for (SiteRegistration siteRegistration : siteRegObj) {
					terminalIdList.add(siteRegistration.getTerminalid());
				}
			}

			if (!terminalIdList.isEmpty()) {
				Date currentDate = Date.valueOf(reqObj.getString("currentDate"));

				String hql = "from invoiceDetails where terminalid IN (:termId) and invoice_date=:selectedDate order by invoice_time DESC";

				List<invoiceDetails> mallAuthHistList = session.createQuery(hql)
						.setParameterList("termId", terminalIdList).setParameter("selectedDate", currentDate)
						.getResultList();

				if (!mallAuthHistList.isEmpty()) {

					int countSlno = 1;

					for (invoiceDetails invoiceDetailsBean : mallAuthHistList) {
						slno.put(countSlno);
//						MobileNo.put(transactionHistory.getMobileNo());
						dateOfTransaction.put(invoiceDetailsBean.getInvoice_date());
						amount.put(invoiceDetailsBean.getLockerRentamount());
//						transactionType.put(transactionHistory.getStatus());
						timeOfTransaction.put(invoiceDetailsBean.getInvoice_time());
						lockerNumbers.put(invoiceDetailsBean.getLockNo());
						custName.put(invoiceDetailsBean.getCustomerName());
						noOfHours.put(invoiceDetailsBean.getLockerRenthour());
						terminalID.put(invoiceDetailsBean.getTerminalid());
//						passcode.put(transactionHistory.getPasscode());
						excessAmount.put(Math.round(invoiceDetailsBean.getExcess_amount()));
						excessHour.put(invoiceDetailsBean.getExcess_hours());
//						itemStored.put(invoiceDetailsBean.getItemsStored());
						balance.put(Math.round(invoiceDetailsBean.getBalanceAmount()));
						closingDate.put(invoiceDetailsBean.getInvoice_date());
						closingTime.put(invoiceDetailsBean.getInvoice_time());
						partRetAmountArr.put(invoiceDetailsBean.getPartretamount());
						CGSTArr.put(GlobalVariable.calulcateGstMethod(
								(((int) (invoiceDetailsBean.getLockerRentamount()))
										+ ((int) invoiceDetailsBean.getExcess_amount())
										+ (invoiceDetailsBean.getPartretamount())
										+ ((int) invoiceDetailsBean.getBalanceAmount())),
								"CGST", invoiceDetailsBean.getTerminalid()));

						SGSTArr.put(GlobalVariable.calulcateGstMethod(
								(((int) (invoiceDetailsBean.getLockerRentamount()))
										+ ((int) invoiceDetailsBean.getExcess_amount())
										+ (invoiceDetailsBean.getPartretamount())
										+ ((int) invoiceDetailsBean.getBalanceAmount())),
								"SGST", invoiceDetailsBean.getTerminalid()));

//						totAmountIncludingTax.put((calulcateGstMethod((((int) (transactionHistory.getAmount() / 100))
//								+ ((int) transactionHistory.getExcess_amount() / 100)
//								+ ((int) transactionHistory.getBalance() / 100)), SGST_PERCENTAGE))
//								- (transactionHistory.getAmount() / 100));

						
//						totAmntExcludingTax
//								.put(((int) (invoiceDetailsBean.getLockerRentamount())
//										+ ((int) invoiceDetailsBean.getExcess_amount())
//										+ (invoiceDetailsBean.getPartretamount())
//										+ ((int) invoiceDetailsBean.getBalanceAmount()))
//										- ((GlobalVariable.calulcateGstMethod(
//												(((int) (invoiceDetailsBean.getLockerRentamount()))
//														+ ((int) invoiceDetailsBean.getExcess_amount())
//														+ (invoiceDetailsBean.getPartretamount())
//														+ ((int) invoiceDetailsBean.getBalanceAmount())),
//												"CGST", invoiceDetailsBean.getTerminalid()))
//												+ (GlobalVariable
//														.calulcateGstMethod(
//																(((int) (invoiceDetailsBean.getLockerRentamount()))
//																		+ ((int) invoiceDetailsBean.getExcess_amount())
//																		+ (invoiceDetailsBean.getPartretamount())
//																		+ ((int) invoiceDetailsBean
//																				.getBalanceAmount())),
//																"SGST", invoiceDetailsBean.getTerminalid()))));

						totAmntExcludingTax
						.put(((int) (invoiceDetailsBean.getLockerRentamount()))
								+ ((int) invoiceDetailsBean.getExcess_amount())
								+ (invoiceDetailsBean.getPartretamount())
								+ ((int) invoiceDetailsBean.getBalanceAmount()));
						
						

						totAmountIncludingTax.put(Math.round(invoiceDetailsBean.getTotalAmount()));
						countSlno++;
					}
				}
			}

		} else if (packetType.equalsIgnoreCase("date-wise-hist")) {

			ArrayList<String> terminalIdList = new ArrayList<>();

//			System.out.println("site name list : "+siteNameList);
//			System.out.println("site location : "+siteLocation);
//			System.out.println("collected site names from front end : "+ selectedSiteNames);

			String siteRegHql = "FROM SiteRegistration WHERE siteName IN (:sName) AND city=:siteLoc";

			ArrayList<SiteRegistration> siteRegObj = (ArrayList<SiteRegistration>) session.createQuery(siteRegHql)
					.setParameterList("sName", siteNameList).setParameter("siteLoc", siteLocation).getResultList();

//			System.out.println("is site registration obj is empty ? :: "+siteRegObj.isEmpty());

			if (!siteRegObj.isEmpty()) {
				for (SiteRegistration siteRegistration : siteRegObj) {
					terminalIdList.add(siteRegistration.getTerminalid());
				}
			}

//			System.out.println("terminal id list : "+terminalIdList);

			if (!terminalIdList.isEmpty()) {
				Date fromDate = Date.valueOf(reqObj.getString("fromDate"));
				Date toDate = Date.valueOf(reqObj.getString("toDate"));

				String hql = "from invoiceDetails where terminalid IN (:termId) and invoice_date between :fromDate and : toDate";

				List<invoiceDetails> mallAuthHistList = session.createQuery(hql)
						.setParameterList("termId", terminalIdList).setParameter("fromDate", fromDate)
						.setParameter("toDate", toDate).getResultList();

//				System.out.println("mall Authority history list is empty  ? :: "+mallAuthHistList.isEmpty());

				if (!mallAuthHistList.isEmpty()) {

					int countSlno = 1;

					for (invoiceDetails invoiceDetailsBean : mallAuthHistList) {
						slno.put(countSlno);
//						MobileNo.put(transactionHistory.getMobileNo());
						dateOfTransaction.put(invoiceDetailsBean.getInvoice_date());
						amount.put(invoiceDetailsBean.getLockerRentamount());
//						transactionType.put(transactionHistory.getStatus());
						timeOfTransaction.put(invoiceDetailsBean.getInvoice_time());
						lockerNumbers.put(invoiceDetailsBean.getLockNo());
						custName.put(invoiceDetailsBean.getCustomerName());
						noOfHours.put(invoiceDetailsBean.getLockerRenthour());
						terminalID.put(invoiceDetailsBean.getTerminalid());
//						passcode.put(transactionHistory.getPasscode());
						excessAmount.put(Math.round(invoiceDetailsBean.getExcess_amount()));
						excessHour.put(invoiceDetailsBean.getExcess_hours());
//						itemStored.put(invoiceDetailsBean.getItemsStored());
						balance.put(Math.round(invoiceDetailsBean.getBalanceAmount()));
						closingDate.put(invoiceDetailsBean.getInvoice_date());
						closingTime.put(invoiceDetailsBean.getInvoice_time());
						partRetAmountArr.put(invoiceDetailsBean.getPartretamount());

						CGSTArr.put(GlobalVariable.calulcateGstMethod(
								(((int) (invoiceDetailsBean.getLockerRentamount()))
										+ ((int) invoiceDetailsBean.getExcess_amount())
										+ (invoiceDetailsBean.getPartretamount())
										+ ((int) invoiceDetailsBean.getBalanceAmount())),
								"CGST", invoiceDetailsBean.getTerminalid()));

						SGSTArr.put(GlobalVariable.calulcateGstMethod(
								(((int) (invoiceDetailsBean.getLockerRentamount()))
										+ ((int) invoiceDetailsBean.getExcess_amount())
										+ (invoiceDetailsBean.getPartretamount())
										+ ((int) invoiceDetailsBean.getBalanceAmount())),
								"SGST", invoiceDetailsBean.getTerminalid()));

//						totAmountIncludingTax.put((calulcateGstMethod((((int) (transactionHistory.getAmount() / 100))
//								+ ((int) transactionHistory.getExcess_amount() / 100)
//								+ ((int) transactionHistory.getBalance() / 100)), SGST_PERCENTAGE))
//								- (transactionHistory.getAmount() / 100));

						
//						totAmntExcludingTax
//								.put(((int) (invoiceDetailsBean.getLockerRentamount())
//										+ ((int) invoiceDetailsBean.getExcess_amount())
//										+ (invoiceDetailsBean.getPartretamount())
//										+ ((int) invoiceDetailsBean.getBalanceAmount()))
//										- ((GlobalVariable.calulcateGstMethod(
//												(((int) (invoiceDetailsBean.getLockerRentamount()))
//														+ ((int) invoiceDetailsBean.getExcess_amount())
//														+ (invoiceDetailsBean.getPartretamount())
//														+ ((int) invoiceDetailsBean.getBalanceAmount())),
//												"CGST", invoiceDetailsBean.getTerminalid()))
//												+ (GlobalVariable
//														.calulcateGstMethod(
//																(((int) (invoiceDetailsBean.getLockerRentamount()))
//																		+ ((int) invoiceDetailsBean.getExcess_amount())
//																		+ (invoiceDetailsBean.getPartretamount())
//																		+ ((int) invoiceDetailsBean
//																				.getBalanceAmount())),
//																"SGST", invoiceDetailsBean.getTerminalid()))));
						
						totAmntExcludingTax
						.put(((int) (invoiceDetailsBean.getLockerRentamount()))
								+ ((int) invoiceDetailsBean.getExcess_amount())
								+ (invoiceDetailsBean.getPartretamount())
								+ ((int) invoiceDetailsBean.getBalanceAmount()));
						
						

						totAmountIncludingTax.put(Math.round(invoiceDetailsBean.getTotalAmount()));
						countSlno++;
					}
				}
			}

		}

//		System.out.println("sdfs-0sdf-0sdf");
//		System.out.println(responseObj);

		if (slno.length() > 0) {
			responseObj.put("responseCode", "tdhist-200");
			responseObj.put("slno", slno);
//			responseObj.put("MobileNo", MobileNo);
			responseObj.put("amount", amount);
//			responseObj.put("transactionType", transactionType);
			responseObj.put("dateOfTransaction", dateOfTransaction);
			responseObj.put("timeOfTransaction", timeOfTransaction);
			responseObj.put("lockers", lockerNumbers);
			responseObj.put("custName", custName);
			responseObj.put("noOfHours", noOfHours);
			responseObj.put("terminalID", terminalID);
//			responseObj.put("itemStored", itemStored);
			responseObj.put("excess_amount", excessAmount);
			responseObj.put("excess_hour", excessHour);
			responseObj.put("balance", balance);
			responseObj.put("partRetAmount", partRetAmountArr);
//			responseObj.put("passcode", passcode);
			responseObj.put("closing_date", closingDate);
			responseObj.put("closingTime", closingTime);
			responseObj.put("CGST", CGSTArr);
			responseObj.put("SGST", SGSTArr);
			responseObj.put("totAmntExcludingGST", totAmntExcludingTax);
			responseObj.put("totAmntIncludingGST", totAmountIncludingTax);

		} else {
			responseObj.put("responseCode", "tdhist-404");
		}

		writer.append(responseObj.toString());
		writer.flush();
		writer.close();
		session.close();

	}

	public double calulcateGstMethod(int amount, double gstPercentage) {

		double gstAmount = 0;
		gstAmount = ((amount * gstPercentage) / 100);
		DecimalFormat decimalFormat = new DecimalFormat("#.00");
		gstAmount = Double.parseDouble(decimalFormat.format(gstAmount));
		return gstAmount;

	}

}
