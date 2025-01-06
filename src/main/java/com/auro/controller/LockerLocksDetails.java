package com.auro.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;

import com.auro.beans.LockCategory;
import com.auro.beans.SiteRegistration;
import com.auro.hibernateUtilities.HibernateUtils;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * @author Praveen
 */

@WebServlet("/LockerLocksDetails")
public class LockerLocksDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LockerLocksDetails() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
		
		// System.out.println("insde get");
		
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");
		
		ArrayList<String> list = new ArrayList<String>();
		
		Session session = HibernateUtils.getSession();
		
		try {
			session.beginTransaction();
//			list = (ArrayList<String>) session.createQuery("FROM site_registration").getResultList();
			//always give bean class name in hibernate to fetch data
			list = (ArrayList<String>) session.createQuery("SELECT terminalid FROM SiteRegistration").getResultList();
			
		
			
			// // System.out.println("list : " + list);
			// // System.out.println("after");
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		JSONObject allTerminalIds = new JSONObject();
		
		allTerminalIds.put("terminalId",list );
		PrintWriter writer = response.getWriter();
		writer.println(allTerminalIds.toString());
		
		
		writer.flush();
		session.close();
		writer.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// TODO Auto-generated method stub
//		doGet(request, response);
		
		// System.out.println("inside post");
		
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");
		
		String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(Collectors.joining());
		JSONObject requestedObj = new JSONObject(jsonBody);
		
		// // System.out.println(requestedObj);
		
		String terminalId = requestedObj.getString("terminalID");
		String packetType = requestedObj.getString("PacketType");
		String dayType = requestedObj.getString("dayType");
		String lockercategory = requestedObj.getString("lockerCategory");
		int minuteSlot = requestedObj.getInt("minuteSlot");
		int minAmountSlot = Integer.parseInt(requestedObj.getString("minuteAmountSlot"));
		String timeSlot = requestedObj.getString("slotTime");
		
		LockCategory lockCategory = new LockCategory();
		
		
		JSONObject responseObject = new JSONObject();
		
		Session session = HibernateUtils.getSession();
		
		PrintWriter writer = response.getWriter();
		Transaction tr = session.beginTransaction();
		
		if (packetType.equalsIgnoreCase("updatelockscat")) {
			try {
				
				int id = 0;
				
				String hql = "FROM LockCategory WHERE terminalid=:terminalid AND "
						+ "category=:category AND typeofday=:daytype";
				
				lockCategory = (LockCategory) session.createQuery(hql)

						.setParameter("terminalid", terminalId)
						.setParameter("category", lockercategory)
						.setParameter("daytype", dayType).getSingleResult();
				

				
				// System.out.println(lockCategory.getSlno());
				id = lockCategory.getSlno();
				
				lockCategory.setSlno(id);
				lockCategory.setSlot(timeSlot);
				lockCategory.setMinslot(minuteSlot);
				lockCategory.setMinslotamt(minAmountSlot);
			
				session.saveOrUpdate(lockCategory);
				
				
				tr.commit();
//				List<LockCategory> list= (ArrayList<LockCategory>) lockCategory;
				
//				// System.out.println("fot the  slot : "+list.size());
				
				
				responseObject.put("responseCode", "success");
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				responseObject.put("responseCode", "fail");
			}
		}
		
		writer.append(responseObject.toString());
		writer.flush();
		writer.close();
		session.close();
	}
}
