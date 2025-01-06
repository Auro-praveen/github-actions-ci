package com.locks.payment.status;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import com.auro.beans.CustomerDetails;
import com.auro.beans.RazorpayAmountRefund;
import com.auro.beans.TransactionDetails;
import com.auro.beans.TransactionHistory;
import com.auro.beans.paygatorderid_details;
import com.auro.hibernateUtilities.HibernateUtils;
import com.locks.gloablVariable.GlobalVariable;
import com.mysql.cj.x.protobuf.MysqlxCrud.Order;
import com.razorpay.Payment;
import com.razorpay.QrCode;
import com.razorpay.RazorpayClient;
import com.razorpay.Refund;

import okhttp3.Request;

/**
 * Servlet implementation class TransactionRefundHandler
 */

@WebServlet("/TransactionRefundHandler")
public class TransactionRefundHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public TransactionRefundHandler() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		super.doGet(req, resp);

//		order_NM6Aep89VYPY4p

//		System.out.println("here inside the do get method!! ");
		String orderId = req.getParameter("id");

		System.out.println(verifyPayment(orderId, "other"));
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// // System.out.println("inside transaction refund handler");

		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");

		PrintWriter writer = response.getWriter();

		Session session = HibernateUtils.getSession();

		ArrayList<TransactionDetails> transactionDetails = new ArrayList<>();
		ArrayList<TransactionHistory> tdHistory = new ArrayList<>();

		RazorpayAmountRefund razorpayAmountRefund = new RazorpayAmountRefund();

		String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines()
				.collect(Collectors.joining("/"));
		JSONObject reqObj = new JSONObject(jsonBody);

		JSONArray custName = new JSONArray();
		JSONArray mobileNumber = new JSONArray();
		JSONArray slno = new JSONArray();
		JSONArray terminalId = new JSONArray();
		JSONArray amount = new JSONArray();
		JSONArray dateOFOpen = new JSONArray();
		JSONArray status = new JSONArray();
		JSONArray noOfHours = new JSONArray();
		JSONArray lockNo = new JSONArray();
		JSONArray paymentId = new JSONArray();
		JSONArray excessPayId = new JSONArray();
		JSONArray excessAmount = new JSONArray();
		JSONArray activeTransaction = new JSONArray();

		JSONObject tdDetialsObj = new JSONObject();
		JSONObject refundResp = new JSONObject();

		String packetType = reqObj.getString("packetType");

		// // System.out.println(reqObj);

		if (packetType.equalsIgnoreCase("gettditems")) {

			String type = reqObj.getString("type");
			String mobNumber = reqObj.getString("mobileNumber");

			Date dateOfOpen = Date.valueOf(reqObj.getString("dateOfTransaction"));

			// // System.out.println(reqObj);
			// // System.out.println("sql date : "+dateOfOpen);

			if (type.equalsIgnoreCase("Store")) {
				try {
					// to check all the generated orderIds of the selected Mobile number

					String payORderIDHql = "from paygatorderid_details where mobileNo=:MobileNumber and traDate=:transactionDate";
					List<paygatorderid_details> genOrderIdDetails = session.createQuery(payORderIDHql)
							.setParameter("MobileNumber", mobNumber).setParameter("transactionDate", dateOfOpen)
							.getResultList();

					if (!genOrderIdDetails.isEmpty()) {

						String orderDetails = null;

						for (paygatorderid_details paygatorderid_details : genOrderIdDetails) {

							if (paygatorderid_details.getOrderID().startsWith("qr_")) {
								orderDetails = verifyPayment(paygatorderid_details.getOrderID(), "terminal");
//								System.out.println("inisde qr_ as order id");
							} else {
								orderDetails = verifyPayment(paygatorderid_details.getOrderID(), "other");
//								System.out.println("inisde order_ as order id");
							}

//							System.out.println("pay order elements : -- "+orderDetails);

							if (orderDetails != null) {

								transactionDetails = (ArrayList<TransactionDetails>) session
										.createQuery("from TransactionDetails " + "where paygatewayPaymenstoreTRID='"
												+ orderDetails.substring(0, orderDetails.indexOf(",")) + "'")
										.getResultList();

//								System.out.println("is empty :  "+transactionDetails.isEmpty());

								if (!transactionDetails.isEmpty()) {

									for (TransactionDetails tdDetails : transactionDetails) {
										// // (tdDetails.getDate_of_open());

										DecimalFormat decimalFormat = new DecimalFormat("#.00");

										double CGST = GlobalVariable
												.calulcateGstMethod((int) (tdDetails.getAmount() / 100), "CGST", tdDetails.getTerminalid());
										double SGST = GlobalVariable
												.calulcateGstMethod((int) (tdDetails.getAmount() / 100), "SGST", tdDetails.getTerminalid());

										double totAmount_WithGST = Double.parseDouble(
												decimalFormat.format(((tdDetails.getAmount() / 100) + CGST + SGST)));

										amount.put(totAmount_WithGST);

										if (tdDetails.getExcess_amount() > 0) {

											double Excess_CGST = GlobalVariable.calulcateGstMethod(
													(int) (tdDetails.getExcess_amount() / 100), "CGST", tdDetails.getTerminalid());
											double Excess_SGST = GlobalVariable.calulcateGstMethod(
													(int) (tdDetails.getExcess_amount() / 100), "SGST", tdDetails.getTerminalid());

											double Excess_totAmount_withGst = Double.parseDouble(decimalFormat
													.format(((tdDetails.getExcess_amount() / 100) + CGST + SGST)));
											excessAmount.put(Excess_totAmount_withGst);
										} else {
											excessAmount.put(tdDetails.getExcess_amount());
										}

										slno.put(tdDetails.getSlno());
										custName.put(tdDetails.getCustomerName());
										mobileNumber.put(tdDetails.getMobileNo());
										terminalId.put(tdDetails.getTerminalid());
//										amount.put(tdDetails.getAmount());
										dateOFOpen.put(tdDetails.getDate_of_open());
										status.put(tdDetails.getStatus());
										paymentId.put(tdDetails.getPaygatewayPaymenstoreTRID());
										noOfHours.put(tdDetails.getNo_of_hours());
										lockNo.put(tdDetails.getLockNo());
										excessPayId.put(tdDetails.getPaygatewayexcpayTRID());
//										excessAmount.put(tdDetails.getExcess_amount());
										activeTransaction.put("Active");
									}
								} else {

									String custDetialsHql = "select distinct customerName from CustomerDetails where mobileNo=:MobileNumber";
									String customerName = (String) session.createQuery(custDetialsHql)
											.setParameter("MobileNumber", mobNumber).getSingleResult();

									DecimalFormat decimalFormat = new DecimalFormat("#.00");

									double CGST = GlobalVariable.calulcateGstMethod(
											(int) (paygatorderid_details.getAmount() / 100), "CGST", paygatorderid_details.getTerminaLID());
									double SGST = GlobalVariable.calulcateGstMethod(
											(int) (paygatorderid_details.getAmount() / 100), "SGST", paygatorderid_details.getTerminaLID());

									double totAmount_WithGST = Double.parseDouble(decimalFormat
											.format(((paygatorderid_details.getAmount() / 100) + CGST + SGST)));

									amount.put(totAmount_WithGST);

									slno.put(paygatorderid_details.getSlno());
									custName.put(customerName);
									mobileNumber.put(paygatorderid_details.getMobileNo());
									terminalId.put(paygatorderid_details.getTerminaLID());
//									amount.put(paygatorderid_details.getAmount());
									dateOFOpen.put(paygatorderid_details.getTraDate());
									status.put("PaymentSuccess");
									paymentId.put(orderDetails.substring(0, orderDetails.indexOf(",")));
									noOfHours.put(0);
									lockNo.put(paygatorderid_details.getLockNo());
									excessPayId.put("");
									excessAmount.put(0);
									activeTransaction.put("InActive");
								}

//								System.out.println(tdDetialsObj);
//								System.out.println("--------------------");
							}
						}
					}

//					("trnsaction detials items : "+transactionDetails);

					tdDetialsObj.put("responseCode", "tdfetchsuccess");
					tdDetialsObj.put("slno", slno);
					tdDetialsObj.put("userName", custName);
					tdDetialsObj.put("mobileNumber", mobileNumber);
					tdDetialsObj.put("terminalID", terminalId);
					tdDetialsObj.put("amount", amount);
					tdDetialsObj.put("dateOfOpen", dateOFOpen);
					tdDetialsObj.put("status", status);
					tdDetialsObj.put("paymentId", paymentId);
					tdDetialsObj.put("noOfHours", noOfHours);
					tdDetialsObj.put("lockNo", lockNo);
					tdDetialsObj.put("excessPayId", excessPayId);
					tdDetialsObj.put("excessAmount", excessAmount);
					tdDetialsObj.put("activeTransaction", activeTransaction);

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					tdDetialsObj.put("responseCode", "tdfetchfail");
				}

			} else if (type.equalsIgnoreCase("Retrieve")) {
				// // ("td history");
				try {
					tdHistory = (ArrayList<TransactionHistory>) session
							.createQuery("from TransactionHistory where mobileNo='" + mobNumber + "'"
									+ " and date_of_open='" + dateOfOpen + "'")
							.getResultList();

					for (TransactionHistory tdDetails : tdHistory) {
						// // ("data : "+ tdDetails.getSlno());
						slno.put(tdDetails.getSlno());
						custName.put(tdDetails.getCustomerName());
						mobileNumber.put(tdDetails.getMobileNo());
						terminalId.put(tdDetails.getTerminalid());

						DecimalFormat decimalFormat = new DecimalFormat("#.00");

//						double CGST = GlobalVariable
//								.calulcateGstMethod((int) (tdDetails.getAmount() / 100 + tdDetails.getExcess_amount() / 100), "CGST");
//						double SGST = GlobalVariable
//								.calulcateGstMethod((int) (tdDetails.getAmount() / 100 + tdDetails.getExcess_amount() / 100), "SGST");
//						
//						double totAmount_WithoutGST = Double.parseDouble(decimalFormat
//								.format(((tdDetails.getAmount() / 100 + tdDetails.getExcess_amount() / 100) + CGST + SGST)));

						double CGST = GlobalVariable.calulcateGstMethod((int) (tdDetails.getAmount() / 100), "CGST", tdDetails.getTerminalid());
						double SGST = GlobalVariable.calulcateGstMethod((int) (tdDetails.getAmount() / 100), "SGST", tdDetails.getTerminalid());

						double totAmount_WithGST = Double
								.parseDouble(decimalFormat.format(((tdDetails.getAmount() / 100) + CGST + SGST)));

						amount.put(totAmount_WithGST);

						if (tdDetails.getExcess_amount() > 0) {

							double Excess_CGST = GlobalVariable
									.calulcateGstMethod((int) (tdDetails.getExcess_amount() / 100), "CGST", tdDetails.getTerminalid());
							double Excess_SGST = GlobalVariable
									.calulcateGstMethod((int) (tdDetails.getExcess_amount() / 100), "SGST", tdDetails.getTerminalid());

							double Excess_totAmount_withGst = Double.parseDouble(
									decimalFormat.format(((tdDetails.getExcess_amount() / 100) + CGST + SGST)));
							excessAmount.put(Excess_totAmount_withGst);
						} else {
							excessAmount.put(tdDetails.getExcess_amount());
						}

						dateOFOpen.put(tdDetails.getDate_of_open());
						status.put(tdDetails.getStatus());
						paymentId.put(tdDetails.getPaygatewayPaymenstoreTRID());
						noOfHours.put(tdDetails.getNo_of_hours());
						lockNo.put(tdDetails.getLockNo());
						excessPayId.put(tdDetails.getPaygatewayexcpayTRID());

						activeTransaction.put("Retreived");
					}

					tdDetialsObj.put("responseCode", "tdfetchsuccess");
					tdDetialsObj.put("slno", slno);
					tdDetialsObj.put("userName", custName);
					tdDetialsObj.put("mobileNumber", mobileNumber);
					tdDetialsObj.put("terminalID", terminalId);
					tdDetialsObj.put("amount", amount);
					tdDetialsObj.put("dateOfOpen", dateOFOpen);
					tdDetialsObj.put("status", status);
					tdDetialsObj.put("paymentId", paymentId);
					tdDetialsObj.put("noOfHours", noOfHours);
					tdDetialsObj.put("lockNo", lockNo);
					tdDetialsObj.put("excessAmount", excessAmount);
					tdDetialsObj.put("excessPayId", excessPayId);
					tdDetialsObj.put("activeTransaction", activeTransaction);

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					tdDetialsObj.put("responseCode", "tdfetchfail");
				}

			}
			writer.append(tdDetialsObj.toString());
//			System.out.println(tdDetialsObj);
			writer.flush();
		} else if (packetType.equalsIgnoreCase("refundreq")) {

//			int refAmount = reqObj.getInt("enteredAmount");
			double refAmount = reqObj.getDouble("enteredAmount");

			String dateOfPayment = reqObj.getString("dateOfPayment");
			Date dateOfPaymentDone = Date.valueOf(dateOfPayment);
			String payId = reqObj.getString("paymentId");
			String mobileNo = reqObj.getString("mobileNo");
			String termId = reqObj.getString("terminalID");
			String lockerNo = reqObj.getString("lockerNo");
			String userName = reqObj.getString("userName");

			String refundId = refundMethondHandler(payId, refAmount);

			java.util.Date uDate = new java.util.Date();
			Date sqlDate = new Date(uDate.getTime());

//			System.out.println(sqlDate);

			// // ("before try catch : "+ refundId);
			try {
				if (!refundId.isEmpty()) {
					// // (" after try catch : "+refundId);

					razorpayAmountRefund.setAmount(refAmount * 100);
					razorpayAmountRefund.setUserName(userName);
					razorpayAmountRefund.setDateOfPayment(dateOfPaymentDone);
					razorpayAmountRefund.setPaymentId(payId);
					razorpayAmountRefund.setMobileNumber(mobileNo);
					razorpayAmountRefund.setDateOfRefund(sqlDate);
					razorpayAmountRefund.setTerminalId(termId);
					razorpayAmountRefund.setLockerNo(lockerNo);
					razorpayAmountRefund.setRefundPayId(refundId);

					int resp = (int) session.save(razorpayAmountRefund);

					if (resp > 0) {
						refundResp.put("responseCode", "refundsucc");
					} else {
						refundResp.put("responseCode", "refundfail");
					}
					
				} else {
					refundResp.put("responseCode", "refundfail");
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				refundResp.put("responseCode", "refundfail");
			}
			writer.append(refundResp.toString());
			writer.flush();
		}

		session.close();
		writer.close();
	}

	public String verifyPayment(String OrderID, String type) {

		String payOrderDetails = null;
		try {

			// for payment done using order id

//			System.out.println(OrderID + " --------- "+ type);

			if (type.equalsIgnoreCase("other")) {
//				System.out.println("prderID :" + OrderID);
				RazorpayClient razorpayClient = new RazorpayClient(GlobalVariable.razorpayKeyId,
						GlobalVariable.razorpaySecretKey);

				List<Payment> payOrders = razorpayClient.orders.fetchPayments(OrderID);

//				System.out.println(payOrders);
//				System.out.println("inside the payment confirmation !! ");

				if (payOrders.size() > 0) {
					JSONObject razorpayObject = new JSONObject(payOrders.get(0).toString());

//					System.out.println(payOrders);

					if (razorpayObject.getString("status").equalsIgnoreCase("captured")
							&& razorpayObject.getBoolean("captured") && razorpayObject.getInt("amount_refunded") == 0) {
						payOrderDetails = razorpayObject.getString("id") + "," + razorpayObject.getInt("amount");
					}
				}
			} else if (type.equalsIgnoreCase("terminal")) {
//				System.out.println("prderID :"+OrderID);
				RazorpayClient razorpayClient = new RazorpayClient(GlobalVariable.razorpayKeyId,
						GlobalVariable.razorpaySecretKey);

				JSONObject params = new JSONObject();
				params.put("count", "1");

				List<QrCode> payOrders = razorpayClient.qrCode.fetchAllPayments(OrderID, params);
				if (payOrders.size() > 0) {
					JSONObject razorpayObject = new JSONObject(payOrders.get(0).toString());

//					System.out.println(payOrders);

					if (razorpayObject.getString("status").equalsIgnoreCase("captured")
							&& razorpayObject.getBoolean("captured") && razorpayObject.getInt("amount_refunded") == 0) {
						payOrderDetails = razorpayObject.getString("id") + "," + razorpayObject.getInt("amount");
					}
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

//		System.out.println(payOrderDetails);
		return payOrderDetails;
	}

	public String refundMethondHandler(String paymentId, double amount) {

		String refundId = "";

		double amountInPaise = amount * 100;

		try {

			// live mode Satish dashboard for live money transfer
//			RazorpayClient razorpay = new RazorpayClient("rzp_live_pBblKyD6MMaGpB", "jZLbV1EiTh02gSGzKfeQipPB");

			// test key PRAVEEN razor pay dashobard for testing
//			RazorpayClient razorpay = new RazorpayClient("rzp_test_kF1NdHUm47R7R4", "qy48Nhzq72txUptAWkQrMEqy");

//			live key tuckit originall
//			RazorpayClient razorpay = new RazorpayClient("rzp_live_sjKUSO8AovpnnS", "KgmWtCSt9TOG4KTxNGkSoD5C");

			RazorpayClient razorpay = new RazorpayClient(GlobalVariable.razorpayKeyId,
					GlobalVariable.razorpaySecretKey);
			JSONObject refundRequest = new JSONObject();
			refundRequest.put("amount", amountInPaise);
			refundRequest.put("speed", "normal");

			Refund refund = razorpay.payments.refund(paymentId, refundRequest);

			// // System.out.println("refund respiose : " + refund);
			String refId = refund.get("id");

			if (!refId.isEmpty()) {
				// // System.out.println("refund IDId: "+ refund.get("id"));
				refundId = refund.get("id");
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return refundId;
	}
}
