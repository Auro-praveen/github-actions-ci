package com.locker.operations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.JSONObject;

import com.auro.beans.SiteRegistration;
import com.auro.hibernateUtilities.HibernateUtils;
import com.locks.update.UpdateMobileNumber;

import okhttp3.Request;

/**
 * Servlet implementation class LockerOperationViaSms
 */

@WebServlet("/LockerOperationViaSms")
public class LockerOperationViaSms extends HttpServlet {

	private static final long serialVersionUID = 1L;
//	public static final String MOB_NUM_FILE_PATH = "mobNumberPath.txt";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LockerOperationViaSms() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
//		super.doGet(request, response);
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// doGet(request, response);

		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");

		Session session = HibernateUtils.getSession();

		PrintWriter writer = response.getWriter();
		response.getWriter();

		UpdateMobileNumber updateMobileNum = new UpdateMobileNumber();
		String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines()
				.collect(Collectors.joining("\n"));

		SiteRegistration siteReg = new SiteRegistration();

		JSONObject obj = new JSONObject(jsonBody);
//		System.out.println(obj);
		if (obj.get("PacketType").equals("uncondsms")) {

			String lockerNo = obj.getString("LockerNo");
			String terminalId = obj.getString("terminalID");
			String MobileNo = obj.getString("MobileNo");
			int lockerNumber = 0;

			Date date = new Date();
//			System.out.println(date);
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			String currentDate = sdf.format(date);

			try {

				String hql = "SELECT mobileNo FROM SiteRegistration WHERE terminalid='"+terminalId+"'";
				String mobileNumber = (String) session.createQuery(hql).getSingleResult();
				System.out.println(currentDate);
				MobileNo = mobileNumber; 

				String lockerMappingHql = "select lockerNumber from TerminalLockMapping where terminalID=:terminalId and lockerName=:lockerName";
				lockerNumber = (int) session.createQuery(lockerMappingHql).setParameter("terminalId", terminalId)
						.setParameter("lockerName", lockerNo).getSingleResult();

//				System.out.println("the locker number is : -- "+lockerNumber);

				session.close();

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				session.close();
			}

//			System.out.println(currentDate);

			if (MobileNo != null) {

//				String downloadingPath = request.getServletContext().getRealPath("") + File.separator
//						+ MOB_NUM_FILE_PATH;
//
//				File file = new File(downloadingPath);
//				String mobileNumber = null;
//
//				try {
//					BufferedReader reader = new BufferedReader(new FileReader(file));
//					String line;
//
//					while ((line = reader.readLine()) != null) {
//						mobileNumber = line;
//
//					}
//				} catch (Exception e) {
//					// TODO: handle exception
//					e.printStackTrace();
//				}

				int i = 0;

//				System.out.println("Mobile Number is : " + mobileNumber + "\n TerminalID is : " + terminalId
//						+ "\n Locker Number is : " + lockerNumber);

//				if (mobileNumber != null) {
//					String sms = "https://otp2.aclgateway.com/OTP_ACL_Web/OtpRequestListener?enterpriseid=tuckpotp&subEnterpriseid=tuckpotp&"
//							+ "pusheid=tuckpotp&pushepwd=tuckpotp13&msisdn=91" + mobileNumber
//							+ "&sender=TUCKPD&msgtext=Locker%20" + lockerNumber + "%20in%20terminal%20" + terminalId
//							+ "," + currentDate
//							+ "%20is%20open.%20Please%20close,%20thank%20you%20t%26c%20apply.%20Tuckpod";
//
//					i = updateMobileNum.sendData(sms);
//				}
				

					String sms = "https://otp2.aclgateway.com/OTP_ACL_Web/OtpRequestListener?enterpriseid=tuckpotp&subEnterpriseid=tuckpotp&"
							+ "pusheid=tuckpotp&pushepwd=tuckpotp13&msisdn=91" + MobileNo
							+ "&sender=TUCKPD&msgtext=Locker%20" + lockerNumber + "%20in%20terminal%20" + terminalId
							+ "," + currentDate
							+ "%20is%20open.%20Please%20close,%20thank%20you%20t%26c%20apply.%20Tuckpod";

					i = updateMobileNum.sendData(sms);
				

				if (i == 200) {
					writer.print("{\"responseCode\":\"success\"}");
				} else {
					writer.print("{\"responseCode\":\"failed\"}");
				}
			} else {
				writer.print("{\"responseCode\":\"failed\"}");
			}

			writer.flush();
			writer.close();
		}
	}
}
