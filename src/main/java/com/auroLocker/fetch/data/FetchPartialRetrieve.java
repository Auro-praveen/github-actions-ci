package com.auroLocker.fetch.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Date;
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

import com.auro.beans.PartialRetrieveData;
import com.auro.hibernateUtilities.HibernateUtils;

/**
 * Servlet implementation class FetchPartialRetrieve
 */

@WebServlet("/FetchPartialRetrieve")
public class FetchPartialRetrieve extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FetchPartialRetrieve() {

    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");
		
		Session session = HibernateUtils.getSession();
		
		PrintWriter writer = response.getWriter();
		
		String line = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(Collectors.joining("\n"));
		JSONObject requestObj = new JSONObject(line);
		
//		System.out.println(requestObj);
		
		String PacketType = requestObj.getString("PacketType");
		
		Date requestedDate = Date.valueOf(requestObj.getString("date"));
		
		JSONObject responseObj = new JSONObject();

		JSONArray slno = new JSONArray();
		JSONArray mobileNo = new JSONArray();
		JSONArray lockNo = new JSONArray();
		JSONArray dateOfOpen = new JSONArray();
		JSONArray timeOfOpen = new JSONArray();
		JSONArray openStatus = new JSONArray();
		JSONArray dateOfFullClose = new JSONArray();
		JSONArray timeOfFullClose = new JSONArray();
		JSONArray terminalID = new JSONArray();
		
		session.beginTransaction();
		if (PacketType.equalsIgnoreCase("partialdata")) {
	
			try {
				String hql = "from PartialRetrieveData where dateOfopen=:givenDate";
				List<PartialRetrieveData> partialRetrData = session.createQuery(hql).setParameter("givenDate", requestedDate).getResultList();
				
//				List<PartialRetrieveData> partialRetrData =  session.createQuery("from PartialRetrieveData").getResultList();
				
//				System.out.println(partialRetrData);
				if (!partialRetrData.isEmpty()) {
					for (PartialRetrieveData partialRetrieveData : partialRetrData) {
						
//						System.out.println(partialRetrieveData.getMobileNo());
						slno.put(partialRetrieveData.getSlno());
						mobileNo.put(partialRetrieveData.getMobileNo());
						lockNo.put(partialRetrieveData.getLocNo());
						dateOfOpen.put(partialRetrieveData.getDateOfopen());
						timeOfOpen.put(partialRetrieveData.getTime_of_open());
						openStatus.put(partialRetrieveData.getOpenStatus());
						dateOfFullClose.put(partialRetrieveData.getDateOffullclose());
						terminalID.put(partialRetrieveData.getTerminalID());
						timeOfFullClose.put(partialRetrieveData.getTime_of_fullclose());
					}
				}
				
				responseObj.put("slno", slno);
				responseObj.put("mobileNo", mobileNo);
				responseObj.put("lockerNo", lockNo);
				responseObj.put("dateOfOpen", dateOfOpen);
				responseObj.put("timeOfOpen", timeOfOpen);
				responseObj.put("openStatus", openStatus);
				responseObj.put("dateOfFullClose", dateOfFullClose);
				responseObj.put("terminalId", terminalID);
				responseObj.put("timeOfFullClose", timeOfFullClose);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			} 
		}
		
		writer.append(responseObj.toString());
		writer.flush();
		session.close();
		writer.close();
	}
}
