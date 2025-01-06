package com.auroLocker.fetch.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.auro.beans.SiteRegistration;
import com.auro.beans.TerminalHealthPacket;
import com.auro.hibernateUtilities.HibernateUtils;

/**
 * @author Praveen
 */

@WebServlet("/FetchDeviceHealth")
public class FetchDeviceHealth extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FetchDeviceHealth() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");

		// System.out.println("inside fethcDeviceHealth");

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Session session = HibernateUtils.getSession();
		Transaction transaction = session.beginTransaction();
		PrintWriter writer = response.getWriter();

		String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines()
				.collect(Collectors.joining("\n"));

		String packetType;
		String stateName = null;
		JSONObject obj = null;

		String type = null;
		try {

			obj = new JSONObject(jsonBody);

//			System.out.println(obj);
			packetType = obj.getString("packetType");

		} catch (JSONException e) {
			packetType = (String) request.getAttribute("PacketType");
//			stateName = obj.getString("stateName");
		}
		// System.out.println(jsonBody);
		java.util.Date devDate = null;
		java.util.Date recDate = null;

		JSONArray terminalID = new JSONArray();
		JSONArray deviceStatus = new JSONArray();
		JSONArray rdate = new JSONArray();
		JSONArray rtime = new JSONArray();
		JSONArray ddate = new JSONArray();
		JSONArray dtime = new JSONArray();
		JSONArray versionArr = new JSONArray();
		JSONArray i_typeArr = new JSONArray();
		JSONArray mob_number = new JSONArray();

		JSONObject respObj = new JSONObject();

		Long currentTimeMil = System.currentTimeMillis();
		String[] curDate = simpleDateFormat.format(new java.util.Date()).split(" ");
		// System.out.println(curDate[0]+ " : : : "+curDate[1]);

		JSONArray openStatTerminals = new JSONArray();

		if (packetType.equalsIgnoreCase("healthPacket")) {

			stateName = obj.getString("stateName");

			try {
				type = obj.getString("type");
			} catch (Exception e) {
				// TODO: handle exception
			}

//			System.out.println(stateName);

			ArrayList<TerminalHealthPacket> terminalHealthPackets = new ArrayList<>();
			List<String> statwiseTerminals = new ArrayList<>();
			ArrayList<SiteRegistration> termMob_numbers = new ArrayList<>();

			try {

				if (stateName != null) {

					statwiseTerminals = session
							.createQuery("select terminalid from SiteRegistration where state=:state")
							.setParameter("state", stateName).getResultList();

//					System.out.println(statwiseTerminals);

					terminalHealthPackets = (ArrayList<TerminalHealthPacket>) session
							.createQuery("from TerminalHealthPacket where terminalID in (:termidList)")
							.setParameterList("termidList", statwiseTerminals).getResultList();
//					System.out.println(terminalHealthPackets);
					termMob_numbers = (ArrayList<SiteRegistration>) session
							.createQuery("from SiteRegistration where state='" + stateName + "'").getResultList();

//					System.err.println(termMob_numbers);

				} else {

					terminalHealthPackets = (ArrayList<TerminalHealthPacket>) session
							.createQuery("from TerminalHealthPacket").getResultList();
					termMob_numbers = (ArrayList<SiteRegistration>) session.createQuery("from SiteRegistration")
							.getResultList();

				}

//				terminalHealthPackets = (ArrayList<TerminalHealthPacket>) session.createQuery("from TerminalHealthPacket where terminalID=:termidList").setParameterList("termidList", statwiseTerminals).getResultList();

//				termMob_numbers = (ArrayList<SiteRegistration>) session.createQuery("from SiteRegistration").getResultList();

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			for (TerminalHealthPacket terminalHealthPacket : terminalHealthPackets) {

				String mobileNo = "no-number";

				Date deviceDate = terminalHealthPacket.getDevicedate();
				String terminalId = terminalHealthPacket.getTerminalID();
				Time deviceTime = terminalHealthPacket.getDevicetime();
				Date recievedDate = terminalHealthPacket.getRdate();
				Time recievedTime = terminalHealthPacket.getRtime();

				for (SiteRegistration site_reg : termMob_numbers) {
					if (terminalHealthPacket.getTerminalID().equalsIgnoreCase(site_reg.getTerminalid())) {
						if (site_reg.getMobileNo() != null) {
							mobileNo = site_reg.getMobileNo();
						}
						break;
					}
				}

				// System.out.println("terminal health packet : "+ terminalId);

				try {

					// status verify using system time

//					String devTotalTime = deviceDate+" "+deviceTime;
//					String devTotalTime = currentTimeMil;
					String recTotalTime = (recievedDate + " " + recievedTime).toString();
//					devDate = simpleDateFormat.parse(devTotalTime);
					recDate = simpleDateFormat.parse(recTotalTime);
					// System.out.println("date : : "+ devDate);
//					long devHealthTime = devDate.getTime();
					long recHealthTime = recDate.getTime();
					// System.out.println(currentTimeMil + " ; ; ; "+recHealthTime);
					long diffMill = currentTimeMil - recHealthTime;

					// System.out.println("total diff : "+diffMill);
					// System.out.println("total time : "+ (diffMill / (60*1000)));

					int timeDiff = (int) TimeUnit.MILLISECONDS.toMinutes(diffMill); // (diffMill / (60*1000))

					// System.out.println("diff time "+timeDiff);

					if (type != null && type.equals("INACTIVE")) {
						if (timeDiff > 1) {
							deviceStatus.put("inactive");

							mob_number.put(mobileNo);

							String[] packetTypeArr = terminalHealthPacket.getPackettype().split(",");
//							System.out.println(packetTypeArr.length);

							switch (packetTypeArr.length) {
							case 2:
								if (packetTypeArr[1].equals("-")) {
									versionArr.put("no_version");
								} else {
									versionArr.put(packetTypeArr[1]);
								}
								i_typeArr.put("no_mode");
								break;
							case 3:
								if (packetTypeArr[2].equals("-")) {
									i_typeArr.put("no_mode");
								} else {
									i_typeArr.put(packetTypeArr[2]);
								}
								if (packetTypeArr[1].equals("-")) {
									versionArr.put("no_version");
								} else {
									versionArr.put(packetTypeArr[1]);
								}
								break;

							default:
								versionArr.put("no_version");
								i_typeArr.put("no_mode");
								break;
							}

//							ddate.put(terminalHealthPacket.getDevicedate());
//							dtime.put(terminalHealthPacket.getDevicetime());

							rdate.put(terminalHealthPacket.getRdate());
							rtime.put(terminalHealthPacket.getRtime());

							// current device time
							ddate.put(curDate[0]);
							dtime.put(curDate[1]);

							terminalID.put(terminalHealthPacket.getTerminalID());
						}

					} else {
						if (timeDiff < 1) {
							deviceStatus.put("active");
						} else {
							deviceStatus.put("inactive");
						}

						mob_number.put(mobileNo);

						String[] packetTypeArr = terminalHealthPacket.getPackettype().split(",");
//						System.out.println(packetTypeArr.length);

						switch (packetTypeArr.length) {
						case 2:
							if (packetTypeArr[1].equals("-")) {
								versionArr.put("no_version");
							} else {
								versionArr.put(packetTypeArr[1]);
							}
							i_typeArr.put("no_mode");
							break;
						case 3:
							if (packetTypeArr[2].equals("-")) {
								i_typeArr.put("no_mode");
							} else {
								i_typeArr.put(packetTypeArr[2]);
							}
							if (packetTypeArr[1].equals("-")) {
								versionArr.put("no_version");
							} else {
								versionArr.put(packetTypeArr[1]);
							}
							break;

						default:
							versionArr.put("no_version");
							i_typeArr.put("no_mode");
							break;
						}

//						ddate.put(terminalHealthPacket.getDevicedate());
//						dtime.put(terminalHealthPacket.getDevicetime());

						rdate.put(terminalHealthPacket.getRdate());
						rtime.put(terminalHealthPacket.getRtime());

						// current device time
						ddate.put(curDate[0]);
						dtime.put(curDate[1]);

						terminalID.put(terminalHealthPacket.getTerminalID());
					}

					// mappded to site registration uncomment in case need more details about the
					// lock of site

//					ArrayList<SiteRegistration> deviceHealthPacket = (ArrayList<SiteRegistration>) session.createQuery("from SiteRegistration where terminalid ='"+terminalId+"'").getResultList();
//						
//					for (SiteRegistration siteReg : deviceHealthPacket) {
//						terminalID.put(siteReg.getTerminalid());
//						siteId.put(siteReg.getSiteID());
//						siteName.put(siteReg.getSiteName());
//						noOfLocks.put(siteReg.getNo_of_locks());
//						area.put(siteReg.getArea());
//						areaCode.put(siteReg.getAreaCode());
//						cityName.put(siteReg.getSiteName());
//						state.put(siteReg.getState());
//						mobileNo.put(siteReg.getMobileNo());
//						userName.put(siteReg.getUserName());
//						imeiNo.put(siteReg.getImeiNo());
//						
//						if (timeDiff < 3) {
//							deviceStatus.put("active");
//						} else {
//							deviceStatus.put("inactive");
//						}
//					}

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			transaction.commit();

		} else if (packetType.equalsIgnoreCase("termhealthstat")) {
			// request is from fetch locker status servlet and sending it back to it

//			System.out.println(packetType);

			ArrayList<TerminalHealthPacket> terminalHealthPackets = (ArrayList<TerminalHealthPacket>) session
					.createQuery("from TerminalHealthPacket where terminalID != 'G21'").getResultList();

			for (TerminalHealthPacket terminalHealthPacket : terminalHealthPackets) {

				Date recievedDate = terminalHealthPacket.getRdate();
				Time recievedTime = terminalHealthPacket.getRtime();

				try {

					String recTotalTime = (recievedDate + " " + recievedTime).toString();

					recDate = simpleDateFormat.parse(recTotalTime);

					long recHealthTime = recDate.getTime();
					long diffMill = currentTimeMil - recHealthTime;

					int timeDiff = (int) TimeUnit.MILLISECONDS.toMinutes(diffMill);

					if (timeDiff > 1) {
						openStatTerminals.put(terminalHealthPacket.getTerminalID());
//						System.out.println("inactive terminal ID's : "+terminalHealthPacket.getTerminalID());
					}

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			transaction.commit();
		}

		if (packetType.equalsIgnoreCase("termhealthstat")) {

			if (openStatTerminals.length() > 0) {
				respObj.put("responseCode", "inactive");
				respObj.put("terminals", openStatTerminals);
			} else {
				respObj.put("responseCode", "active");
			}

		} else if (terminalID.length() > 0) {
			respObj.put("packetType", "devhlth");
			respObj.put("terminalId", terminalID);
			respObj.put("ddate", ddate);
			respObj.put("dtime", dtime);
			respObj.put("rdate", rdate);
			respObj.put("rtime", rtime);
			respObj.put("deviceStatus", deviceStatus);
			respObj.put("inetMode", i_typeArr);
			respObj.put("version", versionArr);
			respObj.put("mob_number", mob_number);
		} else {
			respObj.put("packetType", "nodevhlth");
		}

		// uncomment below in case using more details and uncommented the above query

//			respObj.put("siteId", siteId);
//			respObj.put("siteName", siteName);
//			respObj.put("noOfLocks", noOfLocks);
//			respObj.put("area", area);
//			respObj.put("areaCode", areaCode);
//			respObj.put("cityName", cityName);
//			respObj.put("state", state);
//			respObj.put("mobileNo", mobileNo);
//			respObj.put("userName", userName);
//			respObj.put("imeiNo", imeiNo);
//			respObj.put("deviceStatus", deviceStatus);

		// System.out.println(respObj);
		writer.println(respObj.toString());
		writer.flush();
		writer.close();
		session.close();
	}
}
