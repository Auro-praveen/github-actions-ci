package com.locker.operations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import com.auro.beans.TerminalHealthPacket;
import com.auro.hibernateUtilities.HibernateUtils;

/**
 * Servlet implementation class MallAuthorityOperations
 */

@WebServlet("/MallAuthorityOperations")
public class MallAuthorityOperations extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MallAuthorityOperations() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");

		String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines()
				.collect(Collectors.joining("/"));

		JSONObject reqObj = new JSONObject(jsonBody);

		Session session = HibernateUtils.getSession();
		session.beginTransaction();

		java.util.Date recDate = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Long currentTimeMil = System.currentTimeMillis();

		JSONObject responseObject = new JSONObject();
		JSONArray responseArrOfObject = new JSONArray();

		PrintWriter writer = response.getWriter();

		if (reqObj.getString("PacketType").equals("TERMINAL-STATUS")) {

			String getTerminalsHQL = "SELECT terminalid FROM SiteRegistration WHERE siteName IN (:siteName)";
			String devicestatusHQL = "FROM TerminalHealthPacket WHERE terminalID IN (:terminalIdList)";

//			String siteName = reqObj.getString("siteName").replace("[", "").replace("]", "").replace("\"", "").strip();
			String siteName = reqObj.getString("siteName");
//			System.out.println(reqObj.getString("siteName") + " result is is is ::" + siteName);

			try {

				ArrayList<String> terminalIdList = (ArrayList<String>) session.createQuery(getTerminalsHQL)
						.setParameter("siteName", siteName).getResultList();

//				System.out.println(terminalIdList);
				if (!terminalIdList.isEmpty()) {

					ArrayList<TerminalHealthPacket> terminalHealthPacketObjectArr = (ArrayList<TerminalHealthPacket>) session
							.createQuery(devicestatusHQL).setParameter("terminalIdList", terminalIdList)
							.getResultList();

					for (TerminalHealthPacket terminalHealthPacket : terminalHealthPacketObjectArr) {

						JSONObject terminalHealthObj = new JSONObject();

						Date recievedDate = terminalHealthPacket.getRdate();
						Time recievedTime = terminalHealthPacket.getRtime();

						terminalHealthObj.put("terminalId", terminalHealthPacket.getTerminalID());

						String[] packetTypeArr = terminalHealthPacket.getPackettype().split(",");
//						System.out.println(packetTypeArr.length);

						switch (packetTypeArr.length) {
						case 2:

							terminalHealthObj.put("inet_mode", "Not-Detected");
							break;
						case 3:
							if (packetTypeArr[2].equals("-")) {
								terminalHealthObj.put("inet_mode", "Not-Detected");
							} else {
								terminalHealthObj.put("inet_mode", packetTypeArr[2]);
							}

							break;

						default:
							terminalHealthObj.put("inet_mode", "Not-Detected");
							break;
						}

						try {

							// status verify using system time

							String recTotalTime = (recievedDate + " " + recievedTime).toString();

							recDate = simpleDateFormat.parse(recTotalTime);

							long recHealthTime = recDate.getTime();

							long diffMill = currentTimeMil - recHealthTime;

							int timeDiff = (int) TimeUnit.MILLISECONDS.toMinutes(diffMill);

							if (timeDiff < 1) {
								terminalHealthObj.put("status", "active");
							} else {
								terminalHealthObj.put("status", "inactive");
							}

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						responseArrOfObject.put(terminalHealthObj);

					}

				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			} finally {
				session.close();
			}

		}

		if (responseArrOfObject.length() > 0) {
			
			responseObject.put("responseCode", 200);
			responseObject.put("terminalHealth", responseArrOfObject);
			
		} else {
			
			responseObject.put("responseCode", 400);
			
		}

		writer.append(responseObject.toString()).flush();

	}

}
