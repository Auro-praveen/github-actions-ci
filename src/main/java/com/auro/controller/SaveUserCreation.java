package com.auro.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONObject;

import com.auro.beans.User;
import com.auro.hibernateUtilities.HibernateUtils;


/**
 * Servlet implementation class SaveUserCreation
 */

@WebServlet("/SaveUserCreation")
public class SaveUserCreation extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveUserCreation() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// System.out.println("inside post");
		
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");
		
		Session session = HibernateUtils.getSession();
		PrintWriter writer = response.getWriter();
		
		Transaction tr =  session.beginTransaction();
		
		JSONObject respObj = new JSONObject();
		String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(Collectors.joining("\n"));
		// System.out.println(jsonBody);
		JSONObject jsonObj  = new JSONObject(jsonBody);
//		 System.out.println(jsonObj);
		User  user = new User();
		String packetType =jsonObj.getString("PacketType");
		
		if(packetType.equalsIgnoreCase("getallperm")) {
			try {
				// System.out.println("inside getAll Permissions");
//				List<user> userPermList = (ArrayList<user>) session.createQuery("from User")
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
		} else if (packetType.equalsIgnoreCase("saveuser")) {
			try {
//				JSONObject permissions = new JSONObject();
				// System.out.println("inside save user Permissions");
				String userPerm = String.valueOf(jsonObj.get("userPermissions"));
//				String allPerm = jsonObj.getString("allPermissions");
				// System.out.println(userPerm);
				
				String hql = "from User where userName=:uName";
				List<User> isUserExist = (List<User>) session.createQuery(hql).setParameter("uName", jsonObj.get("userName").toString()).getResultList();

				if (!isUserExist.isEmpty() ) {
					// System.out.println("userAlreadExist");
					respObj.put("userexist", true);
				} else {
					user.setUserName(jsonObj.get("userName").toString());
					user.setPassword(jsonObj.get("userPassword").toString());
					user.setStatus(jsonObj.getString("userStatus"));
					user.setType(jsonObj.getString("userType"));
					user.setSite_location(jsonObj.getString("siteLocation"));
					user.setSite_name(jsonObj.get("siteName").toString());
					user.setUserpermissions(userPerm);
					
					int resp = (int) session.save(user);
				
					tr.commit();
					if (resp>0) {
						respObj.put("userexist", false);
						respObj.put("status", "success");
					} else {
						respObj.put("userexist", false);
						respObj.put("status", "failed");
					}
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		} else if(packetType.equalsIgnoreCase("edituser")) {
			
			// for authentocation of the user who is making changes
			String uName = jsonObj.getString("uName");
			String uPassword = jsonObj.getString("uPwd");
			
			// if authentication is success then applying the changes
			String userName = jsonObj.getString("userName");
			String status = jsonObj.getString("status");
			String type = jsonObj.getString("type");
			String userPermissions = jsonObj.getString("userPermissions");
//			String siteName = jsonObj.getString("siteName");
//			String siteLocation = jsonObj.getString("siteLocation");
			
			String verifyUserHql = "FROM User WHERE userName=:uName AND password=:uPwd";
			String updateUserHql = "UPDATE User SET status=:status, type=:type, userpermissions=:userPermissions "
					+ "WHERE userName=:userName";
			
			List<User> userDetails = session.createQuery(verifyUserHql).setParameter("uName", uName)
					.setParameter("uPwd", uPassword).getResultList();
			if (!userDetails.isEmpty()) {
				int updateStatus = session.createQuery(updateUserHql).setParameter("status", status).setParameter("type", type)
						.setParameter("userPermissions", userPermissions).setParameter("userName", userName)
						.executeUpdate();
				
				System.out.println(updateStatus);
				
				if(updateStatus > 0) {
					respObj.put("responseCode", "upduser-200");
				} else {
					respObj.put("responseCode", "upduser-202");
				}
			} else {
				respObj.put("responseCode", "pwd-404");
			}
		}
		
		writer.println(respObj.toString());
		writer.flush();
		writer.close();
		session.close();
		
	}
}
