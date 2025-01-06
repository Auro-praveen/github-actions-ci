package com.auro.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
import com.auro.beans.TerminalHealthPacket;
import com.auro.hibernateUtilities.HibernateUtils;

/**
 * Servlet implementation class SaveUserRegistration
 */
@WebServlet("/SaveSiteRegistration")
public class SaveSiteRegistration extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	SimpleDateFormat stf = new SimpleDateFormat("hh:MM:ss");
    public SaveSiteRegistration() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");
		
		System.out.println("got request here ");
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		doGet(request, response);
		
		// System.out.println("inside post");
		 	Session ses=HibernateUtils.getSession();
//			response.setContentType("ap/html");  
		    PrintWriter out = response.getWriter(); 
		    SiteRegistration siteReg = new SiteRegistration();
		    
		    JSONObject respObj = new JSONObject();
			   
			response.addHeader("Access-Control-Allow-Origin", "*");
			response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
			response.setCharacterEncoding("UTF-8");
			

		try {
			ses.beginTransaction();
			
			String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(Collectors.joining("\n"));
			// System.out.println(jsonBody);
			JSONObject jsonUserObj = new JSONObject(jsonBody);

			siteReg.setSiteID(jsonUserObj.getString("siteId"));
			siteReg.setSiteName(jsonUserObj.getString("siteName"));
			siteReg.setNo_of_locks(jsonUserObj.getString("noOfLockers"));
			siteReg.setTerminalid(jsonUserObj.getString("terminalId"));
			siteReg.setAreaCode(jsonUserObj.getString("areaCode"));
			siteReg.setArea(jsonUserObj.getString("areaName"));
			siteReg.setCity(jsonUserObj.getString("cityName"));
			siteReg.setState(jsonUserObj.getString("state"));
			siteReg.setImeiNo(jsonUserObj.getString("imeiNumber"));
			siteReg.setMobileNo(jsonUserObj.getString("mobileNumber"));
			siteReg.setUserName(jsonUserObj.getString("userName"));
			siteReg.setLattitude(Double.parseDouble(jsonUserObj.getString("lattitude")));
			siteReg.setLongitude(Double.parseDouble(jsonUserObj.getString("longitude")));

			int respId = (int) ses.save(siteReg);
			
			SiteRegistration respSiteReg = (SiteRegistration) ses.get(SiteRegistration.class, respId);
			
			 ses.getTransaction().commit();
			 			   
			   if(respSiteReg.getUserName().equalsIgnoreCase(siteReg.getUserName())) {
				   // System.out.println("========"+respSiteReg.getUserName());	   
				   respObj.put("status", "success");
			   } else {
				   respObj.put("status", "failed");
			   }
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		out.println(respObj.toString());
		out.flush();
		out.close();
		ses.close();
	}
	
	public String getCurrentDate() {
		Date currentDate = new Date();
		
		
		String date = sdf.format(currentDate);
		String time = stf.format(currentDate);
		// System.out.println(currentDate);
		
		String dateAndTime = date+"#"+time;
		return dateAndTime;
	}
	
}