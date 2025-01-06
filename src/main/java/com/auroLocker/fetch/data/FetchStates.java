package com.auroLocker.fetch.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.JSONObject;

import com.auro.beans.SiteRegistration;
import com.auro.hibernateUtilities.HibernateUtils;

/**
 * Servlet implementation class FetchStates
 */

@WebServlet("/FetchStates")
public class FetchStates extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FetchStates() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());

		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");

		String pathVar = request.getParameter("value");

		Session session = null;
		List<String> allStates = new ArrayList<>();
		JSONObject object = new JSONObject();

		if (pathVar.equals("states")) {

			session = HibernateUtils.getSession();
			session.beginTransaction();

			try {

				String query = "SELECT DISTINCT state FROM SiteRegistration";
				allStates = session.createQuery(query).getResultList();

//				System.out.println(allStates);

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			} finally {
				if (session != null) {
					session.close();
				}
				object.put("state", allStates);
			}

		} else {

			session = HibernateUtils.getSession();
			session.beginTransaction();

			try {

				String query = "SELECT area, terminalid FROM SiteRegistration WHERE state = :State";
//				allStates = session.createQuery(query).setParameter("State", pathVar).getResultList();

				List<Object[]> stateResult = session.createQuery(query).setParameter("State", pathVar).getResultList();

				if (stateResult.size() > 0) {
					for (Object[] areaAndTermIDarr : stateResult) {
						allStates.add((String) areaAndTermIDarr[0] + ", " + areaAndTermIDarr[1]);
					}
				}

//				System.out.println(allStates);

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			} finally {
				if (session != null) {
					session.close();
				}
				object.put("terminals", allStates);
			}
		}

		response.getWriter().append(object.toString()).flush();

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");

		String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines()
				.collect(Collectors.joining("/"));
		
		
		JSONObject reqObj = new JSONObject(jsonBody);
		Session session = null;
		String type = reqObj.getString("PacketType");

		List<String> allStates = new ArrayList<>();
		JSONObject object = new JSONObject();

		if (type.equalsIgnoreCase("gettermid")) {

			session = HibernateUtils.getSession();
			session.beginTransaction();

			try {

				String query = "SELECT area, terminalid FROM SiteRegistration";
//				allStates = session.createQuery(query).setParameter("State", pathVar).getResultList();

				List<Object[]> stateResult = session.createQuery(query).getResultList();

				if (stateResult.size() > 0) {
					for (Object[] areaAndTermIDarr : stateResult) {
						allStates.add((String) areaAndTermIDarr[0] + ", " + (String) areaAndTermIDarr[1]);
					}
				}

//				System.out.println(allStates);

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			} finally {
				if (session != null) {
					session.close();
				}

				if (allStates.size() > 0) {
					object.put("responseCode", "avltd-200");
					object.put("terminalID", allStates);
				} else {
					object.put("responseCode", "no-201");
				}

			}

		}

		response.getWriter().append(object.toString()).flush();

	}

}
