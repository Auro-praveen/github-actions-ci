package com.auro.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.JSONObject;

import com.auro.beans.ReleaseLocker;
import com.auro.hibernateUtilities.HibernateUtils;

/**
 * Servlet implementation class SaveReleaseLocker
 */

@WebServlet("/SaveReleaseLocker")
public class SaveReleaseLocker extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveReleaseLocker() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// System.out.println("inside");
		PrintWriter writer = response.getWriter();
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");
		ReleaseLocker releaseLocker = new ReleaseLocker();
		Session session = HibernateUtils.getSession();
		
		JSONObject sendResp = new JSONObject();
		
		try {
			session.beginTransaction();
			String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(Collectors.joining("\n"));
			// System.out.println(jsonBody);
			JSONObject releaseLockObj = new JSONObject(jsonBody);
			
			String date = getCurrentDate();
			String time = getCurrentTime();
			
			releaseLocker.setLockNumber(releaseLockObj.getString("LockerNo"));
			releaseLocker.setTerminalId(releaseLockObj.getString("terminalID"));
			releaseLocker.setUserId(releaseLockObj.getString("userId"));
			releaseLocker.setLockReleaseDate(date);
			releaseLocker.setLockReleaseTime(time);
			
			int value = (int) session.save(releaseLocker);
			
			if (value > 0) {
				sendResp.put("status", "success");
			} else {
				sendResp.put("status", "failed");
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		writer.append(sendResp.toString());
		session.close();
		writer.close();

	}
	
	public String getCurrentDate( ) {
		
		Date currentDate = new Date();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		
		
		String date = sdf.format(currentDate);

		// System.out.println(currentDate);
		
		return date;
	}
	
	public String getCurrentTime() {
		Date currentDate = new Date();
		
		SimpleDateFormat stf = new SimpleDateFormat("hh-MM-ss");
		
		
		String date = stf.format(currentDate);

		// System.out.println(currentDate);
		
		return date;
	}
	

}
