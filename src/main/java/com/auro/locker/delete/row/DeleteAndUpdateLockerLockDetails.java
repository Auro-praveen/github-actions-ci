package com.auro.locker.delete.row;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.Session;
import org.json.JSONObject;

import com.auro.beans.LockCategory;
import com.auro.hibernateUtilities.HibernateUtils;

/**
 * @author Praveen
 */

@WebServlet("/DeleteAndUpdateLockerLockDetails")
public class DeleteAndUpdateLockerLockDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteAndUpdateLockerLockDetails() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
		// System.out.println("inside get");
		
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");
		
		int slno = Integer.parseInt(request.getParameter("slno"));
		// System.out.println(slno);
		
		Session session = HibernateUtils.getSession();
		session.beginTransaction();
		LockCategory lockCategory = new LockCategory();
		PrintWriter writer = response.getWriter();
		JSONObject respObj = new JSONObject();
		
		try {
			int n = session.createQuery("DELETE FROM LockCategory WHERE slno="+slno).executeUpdate();
			// System.out.println(n);
			if(n==1) {
				respObj.put("status", "success");
			} else {
				respObj.put("status", "failed");
			}
			Thread t = new Thread();
			t.wait(2000);

			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		writer.println(respObj.toString());
		writer.flush();
		session.close();
		writer.close();
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
