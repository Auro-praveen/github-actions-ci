package com.auroLocker.fetch.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputFilter.Config;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.hibernate.Session;
import org.json.JSONObject;

import com.auro.beans.User;
import com.auro.hibernateUtilities.HibernateUtils;
import com.locks.gloablVariable.GlobalVariable;

/**
 * Servlet implementation class FetchUserLoginDetails
 * @author Praveen 
 */

@WebServlet("/FetchUserLoginDetails")
public class FetchUserLoginDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger = Logger.getLogger(FetchUserLoginDetails.class.getName());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FetchUserLoginDetails() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// System.out.println("inide get");
		
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");
		
//		String path = "D:\\locker_serssion_version\\locker_backend\\logj-config.properties";
//		String path = getServletContext().getRealPath("")+File.separator+"logj-config.properties";
		
//		System.out.println(path);
//		PropertyConfigurator.configure(path);
//		
//		logger.info("Auro Auto Locker");

		JSONObject respObj = new JSONObject();
		String uname = request.getParameter("userName");
		if (GlobalVariable.userDetails.containsKey(uname)) {
			GlobalVariable.userDetails.remove(uname);
			// System.out.println("removed user : "+uname);
			respObj.put("status", "logout");
		} else {
			// System.out.println("no such user present");
			respObj.put("status", "nouser");
		}	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// System.out.println("inide fetch user login details");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");
		
		String ipAddress = request.getHeader("application/json");  
		if (ipAddress == null) {  
		    ipAddress = request.getRemoteAddr();  
		}
		PrintWriter writer = response.getWriter();
		
		String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(Collectors.joining("\n"));
		
		JSONObject jsonReq = new JSONObject(jsonBody);
//		 System.out.println(jsonBody);
		Session session = HibernateUtils.getSession();
		JSONObject jsonResp = new JSONObject();
		session.beginTransaction();
//		GlobalVariable.userDetails.remove(jsonReq.getString("userName"));
		
		if (GlobalVariable.userDetails.containsKey(jsonReq.getString("userName"))) {
			jsonResp.put("status", "userExist");
//			 System.out.println("user Already Active");
		} else {
			try {
				
//				System.out.println(jsonReq);
				String hql = "FROM User WHERE userName=:uName and password=:uPassword";
				User permissions = (User) session.createQuery(hql)
						.setParameter("uName", jsonReq.getString("userName"))
						.setParameter("uPassword", jsonReq.getString("userPassword"))
						.getSingleResult();
				
				
				if (permissions.getType().equalsIgnoreCase("Mall-Authority")) {
					jsonResp.put("responseCode", "mall-auth");
//					jsonResp.put("permissions", "");
//					jsonResp.put("terminalId", permissions.getStatus());
					jsonResp.put("siteName", permissions.getSite_name());
					jsonResp.put("siteLocation", permissions.getSite_location());
					jsonResp.put("userpresent", true);
				} else {
					jsonResp.put("responseCode", "usr");
					jsonResp.put("permissions", permissions.getUserpermissions());
					if (permissions.getApp_access_allowed() != null && permissions.getApp_access_allowed() != "") {
						jsonResp.put("appAccessPerminassion", permissions.getApp_access_allowed());
					} else {
						jsonResp.put("appAccessPerminassion", "NONE");
					}
					
					jsonResp.put("userpresent", true);
				}
				
					logger.info("user name : "+jsonReq.getString("userName")+" logged in");
				
//					jsonResp.put("allPermissions", permissions.getAllpermissions());
//					jsonResp.put("permissions", permissions.getUserpermissions());
//					jsonResp.put("userpresent", true);
				
					GlobalVariable.userDetails.put(jsonReq.getString("userName"), permissions.getUserpermissions());
			} catch (NoResultException ex) {
				// TODO: handle exception
				jsonResp.put("userpresent", false);
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
//				session.close();
//				writer.close();
			} finally {
				writer.println(jsonResp.toString());
				writer.flush();
				
				writer.close();
				session.close();
			}
		}
		

		
	}
}
