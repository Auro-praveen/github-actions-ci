package com.locker.operations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.ArrayList;
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

import com.auro.beans.blockedCustomers;
import com.auro.hibernateUtilities.HibernateUtils;

/**
 * Servlet implementation class FetchBlockedCustomers
 */
@WebServlet("/fetchBlockedCustomers")
public class FetchBlockedCustomers extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FetchBlockedCustomers() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE");
		response.setCharacterEncoding("UTF-8");

		String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines()
				.collect(Collectors.joining("/"));

		JSONObject requestedObject = new JSONObject(jsonBody);
		
		System.out.println("===================");
		System.out.println("===================");

		PrintWriter writer = response.getWriter();
		Session session = HibernateUtils.getSession();
		session.beginTransaction();
		
		JSONObject respJSON = new JSONObject();
		JSONArray slno = new JSONArray();
		JSONArray city = new JSONArray();
		JSONArray name = new JSONArray();
		JSONArray mobileNo = new JSONArray();
		JSONArray reason = new JSONArray();
		JSONArray dateOfBlocking = new JSONArray();
		JSONArray dateOfUnblocking = new JSONArray();
		JSONArray status = new JSONArray();
		

		try {
			String packetType = requestedObject.getString("packetType");

			// for handling the blocked customers
			if (packetType.equals("BLOCKCUST")) {

				blockedCustomers blockCustomers = new blockedCustomers();
				blockCustomers.setCity(requestedObject.getString("city"));
				blockCustomers.setMobilenumber(requestedObject.getString("mobileNo"));
				blockCustomers.setName(requestedObject.getString("name"));
				blockCustomers.setReason(requestedObject.getString("reason"));
				blockCustomers.setStatus("BLOCKED");
				blockCustomers.setDatofunblock(new Date(new java.util.Date().getTime()));
				
				System.out.println(new Date(new java.util.Date().getTime()));

				try {
					int resp = (int) session.save(blockCustomers);

					System.out.println(resp);

					if (resp > 0) {
						writer.write("{\"status\":\"SAVE-200\"}");
					} else {
						writer.write("{\"status\":\"FAIL-200\"}");
					}
				} catch (Exception e) {
					// TODO: handle exception
					writer.write("{\"status\":\"FAIL-200\"}");
					e.printStackTrace();
				} finally {
					writer.flush();
				}

			} else if(packetType.equals("GETBLOCKEDCUST")) {
				

				try {
					ArrayList<blockedCustomers> blockedCustomers = (ArrayList<com.auro.beans.blockedCustomers>) session.createQuery("FROM blockedCustomers").getResultList();
					
					if (!blockedCustomers.isEmpty() && blockedCustomers != null) {
						for (blockedCustomers bCust : blockedCustomers) {
							slno.put(bCust.getSlno());
							city.put(bCust.getCity());
							status.put(bCust.getStatus());
							name.put(bCust.getName());
							dateOfBlocking.put(bCust.getDateofblock());
							if (bCust.getDatofunblock() != null) {
								dateOfUnblocking.put(bCust.getDatofunblock());
							} else {
								dateOfUnblocking.put("-");
							}
							mobileNo.put(bCust.getMobilenumber());
							reason.put(bCust.getReason());
						}
					}
					
					if (slno.length() > 0) {
						respJSON.put("status", "ACTIVE-200");
						respJSON.put("city", city);
						respJSON.put("custStatus",status);
						respJSON.put("name", name);
						respJSON.put("reason", reason);
						respJSON.put("dateofblock", dateOfBlocking);
						respJSON.put("dateofunblock", dateOfUnblocking);
						respJSON.put("slno", slno);
						respJSON.put("mobileNo", mobileNo);
					} else {
						respJSON.put("status", "INACTIVE-200");
					}
				} catch (Exception e) {
					// TODO: handle exception
					respJSON.put("status", "INACTIVE-200");
					e.printStackTrace();
				} finally {
					writer.append(respJSON.toString()).flush();
				}
				
				
			} else if (packetType.equals("BLOCKEXISTINGUSER")) {		
				
				try {
					
					String hql = "FROM blockedCustomers where slno=:slno AND mobilenumber=:mobileNo";
					blockedCustomers bCust = (blockedCustomers) session.createQuery(hql)
							.setParameter("slno", requestedObject.getInt("slno"))
							.setParameter("mobileNo", requestedObject.getString("mobileNo")).getSingleResult();
					
					if (bCust != null) {
						bCust.setDateofblock(new Date(new java.util.Date().getTime()));
						bCust.setDatofunblock(null);
						bCust.setStatus("BLOCKED");
						// for updating
						session.getTransaction().commit();
						writer.append("{\"status\": \"BLOCKUPDATE-200\"}").flush();
					} else {
						writer.append("{\"status\": \"BLOCKUPDATE-404\"}").flush();
					}
					
					
				} catch (Exception e) {
					// TODO: handle exception
					writer.append("{\"status\": \"BLOCKUPDATE-404\"}").flush();
					e.printStackTrace();
				}
				
			} else if (packetType.equals("UNBLOCKCUST")) {
				
				
			try {
					
					String hql = "FROM blockedCustomers where slno=:slno AND mobilenumber=:mobileNo";
					blockedCustomers bCust = (blockedCustomers) session.createQuery(hql)
							.setParameter("slno", requestedObject.getInt("slno"))
							.setParameter("mobileNo", requestedObject.getString("mobileNo")).getSingleResult();
					
					if (bCust != null) {
						bCust.setDatofunblock(new Date(new java.util.Date().getTime()));
						bCust.setDateofblock(null);
						bCust.setStatus("UNBLOCKED");
						// for updating
						session.getTransaction().commit();
						writer.append("{\"status\": \"UNBLOCK-200\"}").flush();
					} else {
						writer.append("{\"status\": \"UNBLOCK-404\"}").flush();
					}
					
				} catch (Exception e) {
					// TODO: handle exception
					writer.append("{\"status\": \"UNBLOCK-404\"}").flush();
					e.printStackTrace();
				}
				
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			respJSON.put("status", "INACTIVE-200");
		} finally {
			session.close();
			writer.close();
		}

	}

}
