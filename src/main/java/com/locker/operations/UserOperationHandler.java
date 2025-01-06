package com.locker.operations;

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

import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import com.auro.beans.SiteRegistration;
import com.auro.beans.User;
import com.auro.hibernateUtilities.HibernateUtils;

/**
 * Servlet implementation class UserOperationHandler
 */

@WebServlet("/UserOperationHandler")
public class UserOperationHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserOperationHandler() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		PrintWriter writer = response.getWriter();
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-control-Allow-Method", "POST, GET, DELETE, UPDATE, OPTION");
		response.setCharacterEncoding("UTF-8");
		
		Session session = HibernateUtils.getSession();
		
		
		String line = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(Collectors.joining("/n"));
		JSONObject requestObj = new JSONObject(line);
		
		JSONArray slno = new JSONArray();
		JSONArray types = new JSONArray();
		JSONArray status = new JSONArray();
		JSONArray userNames = new JSONArray();
		JSONArray userPermissions = new JSONArray();
		ArrayList<String> siteNames = new ArrayList<>();
		ArrayList<String> siteLocation = new ArrayList<>();
		
		String packetType = requestObj.getString("PacketType");
		
		JSONObject responseObj = new JSONObject();
		session.beginTransaction();
		
		if (packetType.equalsIgnoreCase("getallusers")) {
			String hql = "from User";
			
			List<User> allUsetDetails = session.createQuery(hql).getResultList();
			
//			System.out.println(userAllDetails);
			
			if (!allUsetDetails.isEmpty()) {
				
				for (User user : allUsetDetails) {
					
					slno.put(user.getSlno());
					userNames.put(user.getUserName());
					types.put(user.getType());
					status.put(user.getStatus());
					userPermissions.put(user.getUserpermissions());
					
				}

				responseObj.put("responseCode", "udetail-200");
				responseObj.put("userName", userNames );
				responseObj.put("status", status);
				responseObj.put("types", types);
				responseObj.put("slno", slno);
				responseObj.put("userPermissions", userPermissions);
				
			} else {
				responseObj.put("responseCode", "udetail-201");
			}
		} else if(packetType.equalsIgnoreCase("getuserdetails")) {
			String uName = requestObj.getString("userName");
			
			String hql = "from User where userName=:username";
			User user = (User) session.createQuery(hql).setParameter("username", uName).getSingleResult();
			
			if (user.getSlno() > 0) {
				
				if (user.getType().equalsIgnoreCase("Mall-Authority")) {
					responseObj.put("responseCode", "user-202");
				} else {
					responseObj.put("responseCode", "user-200");
					responseObj.put("userName", user.getUserName() );
					responseObj.put("status", user.getStatus());
					responseObj.put("types", user.getType());
					responseObj.put("slno", user.getSlno());
					responseObj.put("userPermissions", user.getUserpermissions());
				}


				
			} else {
				responseObj.put("responseCode", "user-201");
			}
		} else if(packetType.equalsIgnoreCase("getloc")) {
			
			String hql = "from SiteRegistration";
			
			List<SiteRegistration> siteRegObj = session.createQuery(hql).getResultList();
			
			if (!siteRegObj.isEmpty()) {
				
				for (SiteRegistration siteRegistration : siteRegObj) {
					
					if (!siteNames.contains(siteRegistration.getSiteName())) {
						siteNames.add(siteRegistration.getSiteName());
					}
					
					if (!siteLocation.contains(siteRegistration.getCity())) {
						siteLocation.add(siteRegistration.getCity());
					}
					
				}
				
				
				responseObj.put("responseCode", "siteloc-200");
				responseObj.put("siteName", siteNames);
				responseObj.put("siteLocation", siteLocation);
			} else {
				responseObj.put("responseCode", "siteloc-404");
			}
			
		}
		
		writer.append(responseObj.toString());
		writer.close();
		session.close();
		
	}

}
