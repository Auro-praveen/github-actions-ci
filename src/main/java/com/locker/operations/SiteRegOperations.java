package com.locker.operations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONObject;

import com.auro.beans.SiteRegistration;
import com.auro.hibernateUtilities.HibernateUtils;

/**
 * Servlet implementation class SiteRegOperations
 */

@WebServlet("/SiteRegOperations")
public class SiteRegOperations extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");

		String terminalid = request.getParameter("terminalid");
		Session session = HibernateUtils.getSession();
		JSONObject siteRegisterObject = new JSONObject();
		Writer writer = response.getWriter();

		try {
			
			session.beginTransaction();
			String hql = "from SiteRegistration where terminalid=:terminalID";

			SiteRegistration siteRegistration = (SiteRegistration) session.createQuery(hql)
					.setParameter("terminalID", terminalid).getSingleResult();

			if (siteRegistration != null) {

//				System.out.println(siteRegistration.getArea());
//				System.out.println(siteRegistration.getAreaCode());

				siteRegisterObject.put("slno", siteRegistration.getSlno());
				siteRegisterObject.put("terminalId", siteRegistration.getTerminalid());
				siteRegisterObject.put("siteId", siteRegistration.getSiteID());
				siteRegisterObject.put("siteName", siteRegistration.getSiteName());
				siteRegisterObject.put("noOfLockers", siteRegistration.getNo_of_locks());
				siteRegisterObject.put("areaCode", siteRegistration.getAreaCode());
				siteRegisterObject.put("areaName", siteRegistration.getArea());
				siteRegisterObject.put("cityName", siteRegistration.getCity());
				siteRegisterObject.put("state", siteRegistration.getState());
				siteRegisterObject.put("imeiNumber", siteRegistration.getImeiNo());
				siteRegisterObject.put("mobileNumber", siteRegistration.getMobileNo());
				siteRegisterObject.put("userName", siteRegistration.getUserName());
				siteRegisterObject.put("lattitude", siteRegistration.getLattitude());
				siteRegisterObject.put("longitude", siteRegistration.getLongitude());
				siteRegisterObject.put("siteStatus", siteRegistration.getStatus());

			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			session.close();
		}

//		System.out.println(siteRegisterObject);

		writer.append(siteRegisterObject.toString());
		writer.flush();
		writer.close();

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "*");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
		response.setStatus(HttpServletResponse.SC_OK);

		String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines()
				.collect(Collectors.joining("\n"));
		// System.out.println(jsonBody);
		JSONObject requestedJsonObject = new JSONObject(jsonBody);

		PrintWriter writer = response.getWriter();
		Session session = HibernateUtils.getSession();

		SiteRegistration siteRegistration = new SiteRegistration();
		siteRegistration.setSlno(requestedJsonObject.getInt("slno"));
		siteRegistration.setLattitude(requestedJsonObject.getDouble("lattitude"));
		siteRegistration.setLongitude(requestedJsonObject.getDouble("longitude"));
		siteRegistration.setSiteID(requestedJsonObject.getString("siteId"));
		siteRegistration.setSiteName(requestedJsonObject.getString("siteName"));
		siteRegistration.setArea(requestedJsonObject.getString("areaName"));
		siteRegistration.setAreaCode(requestedJsonObject.getString("areaCode"));
		siteRegistration.setCity(requestedJsonObject.getString("cityName"));
		siteRegistration.setState(requestedJsonObject.getString("state"));
		siteRegistration.setUserName(requestedJsonObject.getString("userName"));
		siteRegistration.setImeiNo(requestedJsonObject.getString("imeiNumber"));
		siteRegistration.setMobileNo(requestedJsonObject.getString("mobileNumber"));
		siteRegistration.setTerminalid(requestedJsonObject.getString("terminalId"));
		siteRegistration.setNo_of_locks(requestedJsonObject.getString("noOfLockers"));
		siteRegistration.setNo_of_locks(requestedJsonObject.getString("status"));

		Transaction transaction = session.beginTransaction();

		try {

			session.update(siteRegistration);
			transaction.commit();
			writer.append("{\"status\":\"success\"}");

//			String hql = "UPDATE SiteRegistration SET siteID=:siteid, siteName=:sitename, no_of_locks=:noOfLocks,"
//					+ "area=:areaname, areaCode=:areacode,city=:cityname, state=:state, imeiNo=:imeino, mobileNo=:mobileno, userName=:username,"
//					+ "lattitude=:lattitute, longitude=longitude WHERE terminalid=:terminalid";

		} catch (Exception e) {
			// TODO: handle exception
			writer.append("{\"status\":\"fail\"}");
			e.printStackTrace();
		} finally {

			session.close();

		}

		writer.flush();
		writer.close();

	}

}
