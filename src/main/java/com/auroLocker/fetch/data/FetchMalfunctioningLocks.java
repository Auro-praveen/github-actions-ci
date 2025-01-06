package com.auroLocker.fetch.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.Iterator;
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
import com.auro.beans.TermwiseLockermalfunction;
import com.auro.hibernateUtilities.HibernateUtils;

@WebServlet("/FetchMalfunctioningLocks")
public class FetchMalfunctioningLocks extends HttpServlet {
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// System.out.println("Inside fetchMalfunctionLocks");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");
		
		PrintWriter writer = response.getWriter();
		response.getWriter();
		
		String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(Collectors.joining("\n"));
		
		JSONObject obj = new JSONObject(jsonBody);
		// System.out.println(jsonBody);
		
		TermwiseLockermalfunction termwiseLockermalfunction = new TermwiseLockermalfunction();
		
		Session session = HibernateUtils.getSession();
		Transaction tr = null;
		
		String packetType = obj.getString("PacketType");
		String terminalID = obj.getString("terminalID");
		
		String malfunLocks = null;
		JSONObject malRespObj = new JSONObject();
		if (packetType.equals("getmalflocks")) {
			JSONArray malFunctionLocksArr = new JSONArray();
			try {
				tr = session.beginTransaction();
				termwiseLockermalfunction =  session.get(TermwiseLockermalfunction.class, terminalID);
				
				if (termwiseLockermalfunction != null) {
					// System.out.println("locks : : "+termwiseLockermalfunction.getLocks());
					
					if (termwiseLockermalfunction.getLocks() != "") {
						malfunLocks = termwiseLockermalfunction.getLocks();
						String[] malfunArray = malfunLocks.split(",");
						for (int i = 0; i < malfunArray.length; i++) {
							malFunctionLocksArr.put(malfunArray[i]);
						}
						malRespObj.put("responseCode", "mallocks");
						malRespObj.put("mlocks", malFunctionLocksArr);

					} else {
						malRespObj.put("responseCode", "nolocks");
					}
				}else {
					malRespObj.put("responseCode", "nolocks");
				}
				
				tr.commit();

				
			} catch (Exception e) {
				// TODO: handle exception
				malRespObj.put("responseCode", "nolocks");
				e.printStackTrace();
			}
			

				
		} else if (packetType.equals("blocklocker")) {
			String addMalFunLocks = obj.getString("LockerNo");
			String openUserName = obj.getString("userName");
			String malLocks = "";
//			System.out.println("inside add malfunction locks ");
			tr = session.beginTransaction();
			try {
				termwiseLockermalfunction = session.get(TermwiseLockermalfunction.class, terminalID);

//				System.out.println();
				if (termwiseLockermalfunction != null) {
					if (termwiseLockermalfunction.getLocks() != "") {
						malLocks = termwiseLockermalfunction.getLocks()+addMalFunLocks+",";
						// System.out.println(malLocks);
					} else {
						malLocks = termwiseLockermalfunction.getLocks();
					}
					// System.out.println("before Stored Successfully : " +malLocks);
					termwiseLockermalfunction.setTerminalid(terminalID);
					termwiseLockermalfunction.setLocks(malLocks);
					termwiseLockermalfunction.setOpenuserid(openUserName);
					termwiseLockermalfunction.setMdate(getCurrentDate());
					termwiseLockermalfunction.setNo_of_locks(termwiseLockermalfunction.getNo_of_locks()+1);
//					session.save(termwiseLockermalfunction);
					session.saveOrUpdate(termwiseLockermalfunction);
					tr.commit();
					// System.out.println("Stored Successfully ");
					malRespObj.put("responseCode", "strmalfun");
				} else {
					TermwiseLockermalfunction termwiselockmalf = new TermwiseLockermalfunction();
					malLocks = addMalFunLocks+",";
					// System.out.println("before Stored Successfully : " +malLocks);
					termwiselockmalf.setTerminalid(terminalID);
					termwiselockmalf.setLocks(malLocks);	
					termwiselockmalf.setOpenuserid(openUserName);
					termwiselockmalf.setMdate(getCurrentDate());
					termwiselockmalf.setNo_of_locks(1);
					termwiselockmalf.setStatus("malfunction");
					termwiselockmalf.setCloseremarks("setto maldunction");
//					session.save(termwiseLockermalfunction);
					session.saveOrUpdate(termwiselockmalf);
					tr.commit();
					// System.out.println("Stored Successfully ");
					malRespObj.put("responseCode", "strmalfun");
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				malRespObj.put("responseCode", "failed");
			}
			

			
			
		} else if (packetType.equals("unblocklocker")) {
			String addMalFunLocks = obj.getString("LockerNo");
			String closeUserName = obj.getString("userName");
			String malLocks = "";

			tr = session.beginTransaction();
			try {
				termwiseLockermalfunction = session.get(TermwiseLockermalfunction.class, terminalID);
		
				if (termwiseLockermalfunction != null) {
					if (termwiseLockermalfunction.getLocks().contains(addMalFunLocks)) {
						String locksfromdb = termwiseLockermalfunction.getLocks();
						// System.out.println("malLocks from db: "+locksfromdb);

						String[] allMalfunLocks = locksfromdb.split(",");
						// System.out.println("malLocks from db: "+allMalfunLocks.length);
						for (int i = 0; i < allMalfunLocks.length; i++) {
							if (!allMalfunLocks[i].equals(addMalFunLocks)) {
								malLocks = malLocks + allMalfunLocks[i] + ",";
							}
						}
						
						termwiseLockermalfunction.setTerminalid(terminalID);
						termwiseLockermalfunction.setLocks(malLocks);
						termwiseLockermalfunction.setCloseuserid(closeUserName);
						termwiseLockermalfunction.setRdate(getCurrentDate());
						termwiseLockermalfunction.setNo_of_locks(termwiseLockermalfunction.getNo_of_locks()-1);
						session.saveOrUpdate(termwiseLockermalfunction);
						tr.commit();
						malRespObj.put("responseCode", "remmalfun");
					}
				}


				
			} catch (Exception e) {
				// TODO: handle exception
				malRespObj.put("responseCode", "failed");
				e.printStackTrace();
				// System.out.println("Some Propblem Occured");
			}
		}
		writer.print(malRespObj);
		writer.close();
		session.close();
	
	}
	
	public Date getCurrentDate() {
		java.util.Date utilCurrentDate = new java.util.Date();
		Date currentDate = new Date(utilCurrentDate.getTime());
		return currentDate;
	}
}
