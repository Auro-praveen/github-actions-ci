package com.locks.update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONObject;

import com.auro.beans.User;
import com.auro.hibernateUtilities.HibernateUtils;

/**
 * Servlet implementation class UpdateUserPassword
 */
@WebServlet("/UpdateUserPassword")
public class UpdateUserPassword extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateUserPassword() {
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
		// System.out.println("insisde post ");
		PrintWriter writer = response.getWriter();
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");
		
		Session session = HibernateUtils.getSession();
		Transaction tr = session.beginTransaction();
		JSONObject respObj = new JSONObject();
		
		try {
			String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(Collectors.joining("/"));
			// System.out.println(jsonBody);
			JSONObject jsonObj = new JSONObject(jsonBody);
			
			String hql = "UPDATE User SET password=:newPwd WHERE userName=:userName and password=:currentPwd";
			int update = session.createQuery(hql).setParameter("newPwd", jsonObj.getString("newPwd")).setParameter("userName", jsonObj.getString("userName"))
					.setParameter("currentPwd", jsonObj.getString("currentPwd")).executeUpdate();
			// System.out.println(update);
//			session.update(user);
			tr.commit();
			if (update > 0) {
				respObj.put("status", "success");
			} else {
				respObj.put("status", "failed");
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			session.close();
			writer.close();
			e.printStackTrace();
			respObj.put("status", "failed");
		}
		
		writer.println(respObj.toString());
		writer.flush();
		writer.close();
		session.close();
		
	}
}
