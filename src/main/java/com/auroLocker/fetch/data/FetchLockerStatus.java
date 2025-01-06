package com.auroLocker.fetch.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;

import com.auro.beans.TerminalLockMapping;
import com.auro.beans.TerminalLockStatusDetail;
import com.auro.hibernateUtilities.HibernateUtils;
import com.locks.gloablVariable.GlobalVariable;

import net.bytebuddy.agent.builder.AgentBuilder.CircularityLock.Global;
import okhttp3.Request;

/**
 * @author Praveen
 */

@WebServlet("/FetchLockerStatus")
public class FetchLockerStatus extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FetchLockerStatus() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// System.out.println("inside get");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me");
		response.setCharacterEncoding("UTF-8");

		PrintWriter writer = response.getWriter();

		String termID = request.getParameter("terminalId");
		JSONObject lockerStatusObj = new JSONObject();

		JSONArray slno = new JSONArray();
		JSONArray lockerNumber = new JSONArray();
		JSONArray lockerName = new JSONArray();
		JSONArray terminalId = new JSONArray();
		JSONArray deviceDate = new JSONArray();
		JSONArray deviceTime = new JSONArray();
		JSONArray packetType = new JSONArray();
		JSONArray rdate = new JSONArray();
		JSONArray rtime = new JSONArray();
		JSONArray remarks = new JSONArray();
		JSONArray status = new JSONArray();
		JSONArray userId = new JSONArray();

		Session session = HibernateUtils.getSession();
		Transaction transaction = session.beginTransaction();
		int count = 1;

		try {
			String hqlQurey = "from TerminalLockStatusDetail where terminalID=:TerminalId";
			ArrayList<TerminalLockStatusDetail> lockStatus = (ArrayList<TerminalLockStatusDetail>) session
					.createQuery(hqlQurey).setParameter("TerminalId", termID).getResultList();

			for (TerminalLockStatusDetail terminalLockStatusDetail : lockStatus) {

				slno.put(count);
				lockerNumber.put(terminalLockStatusDetail.getLockerNo());
				terminalId.put(terminalLockStatusDetail.getTerminalID());
				deviceDate.put(terminalLockStatusDetail.getDevicedate());
				deviceTime.put(terminalLockStatusDetail.getDevicetime());
				packetType.put(terminalLockStatusDetail.getPackettype());
				rdate.put(terminalLockStatusDetail.getRdate());
				rtime.put(terminalLockStatusDetail.getRtime());
				remarks.put(terminalLockStatusDetail.getRemarks());
				status.put(terminalLockStatusDetail.getStatus());
				userId.put(terminalLockStatusDetail.getUserid());
				// System.out.println(terminalLockStatusDetail.getLockerNo());
				// System.out.println(terminalLockStatusDetail.getTerminalID());

				ArrayList<TerminalLockMapping> lockersMapping = (ArrayList<TerminalLockMapping>) session
						.createQuery(
								"from TerminalLockMapping where lockerNumber=" + terminalLockStatusDetail.getLockerNo()
										+ "" + " and terminalID='" + terminalLockStatusDetail.getTerminalID() + "'")
						.getResultList();

				for (TerminalLockMapping terminalLockMapping : lockersMapping) {
					lockerName.put(terminalLockMapping.getLockerName());

					// System.out.println(terminalLockMapping.getLockerName());
				}

				count++;
			}

			transaction.commit();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		if (lockerName.length() > 0) {
			lockerStatusObj.put("responseCode", "lockstatus");
			lockerStatusObj.put("slno", slno);
			lockerStatusObj.put("lockerNumber", lockerNumber);
			lockerStatusObj.put("lockerName", lockerName);
			lockerStatusObj.put("deviceDate", deviceDate);
			lockerStatusObj.put("deviceTime", deviceTime);
			lockerStatusObj.put("packetType", packetType);
			lockerStatusObj.put("rdate", rdate);
			lockerStatusObj.put("rtime", rtime);
			lockerStatusObj.put("remarks", remarks);
			lockerStatusObj.put("status", status);
			lockerStatusObj.put("terminalId", terminalId);
			lockerStatusObj.put("userId", userId);
		} else {
			lockerStatusObj.put("responseCode", "null");
		}

		writer.append(lockerStatusObj.toString());
		writer.close();
		session.close();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub

		PrintWriter writer = resp.getWriter();
		Session session = HibernateUtils.getSession();

		resp.addHeader("Access-Control-Allow-Origin", "*");
		resp.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me");
		
//		resp.setHeader("Access-Control-Allow-Credentials", "true");
		resp.setCharacterEncoding("UTF-8");

		String respData = new BufferedReader(new InputStreamReader(req.getInputStream())).lines()
				.collect(Collectors.joining("/"));
		JSONObject respObj = new JSONObject(respData);
//		System.out.println(respObj);
		JSONObject openLockObj = new JSONObject();

		Map<String, ArrayList<String>> lockMap = new HashMap<>();
		JSONArray terminalID = new JSONArray();

		if (respObj.getString("PacketType").equalsIgnoreCase("lockstatus")) {
			try {
				session.beginTransaction();

				ArrayList<TerminalLockStatusDetail> terminalLockerStatus = (ArrayList<TerminalLockStatusDetail>) session
						.createQuery("from TerminalLockStatusDetail where status='Open'").getResultList();

				if (terminalLockerStatus != null) {

					for (TerminalLockStatusDetail termLockStatus : terminalLockerStatus) {
						boolean isLockerOpenForLongTime = compareLockerOpenTime(termLockStatus.getRdate(),
								termLockStatus.getRtime());

						if (isLockerOpenForLongTime) {
							ArrayList<String> singleData = (ArrayList<String>) lockMap
									.get(termLockStatus.getTerminalID());
							if (singleData == null) {
								terminalID.put(termLockStatus.getTerminalID());
								singleData = new ArrayList<>();

							}

							ArrayList<TerminalLockMapping> terminalLockerMapping = (ArrayList<TerminalLockMapping>) session
									.createQuery("from TerminalLockMapping where" + " lockerNumber="
											+ termLockStatus.getLockerNo() + " and terminalID='"
											+ termLockStatus.getTerminalID() + "'")
									.getResultList();

							for (TerminalLockMapping terminalLocker : terminalLockerMapping) {
								singleData.add(terminalLocker.getLockerName());
							}
							lockMap.put(termLockStatus.getTerminalID(), singleData);
						}
					}
				}

				if (lockMap.isEmpty()) {
					// System.out.println("inside if part ----- ----- --------");
					openLockObj.put("newOpenLocks", false);
					GlobalVariable.openTerminalLockers = lockMap;
				} else if (GlobalVariable.openTerminalLockers.equals(lockMap)) {
					// System.out.println("inside else if else part ----- ----- --------");
					for (Map.Entry<String, ArrayList<String>> entry : lockMap.entrySet()) {
						openLockObj.put(entry.getKey(), entry.getValue());

					}
					openLockObj.put("terminalId", terminalID);
					openLockObj.put("newOpenLocks", false);
				} else {
					// System.out.println("inside else part ----- ----- --------");
					for (Map.Entry<String, ArrayList<String>> entry : lockMap.entrySet()) {
						openLockObj.put(entry.getKey(), entry.getValue());

					}
					openLockObj.put("terminalId", terminalID);
					ArrayList<String> globalVarLocks = new ArrayList(GlobalVariable.openTerminalLockers.values());
					ArrayList<String> newLocksFromdb = new ArrayList(lockMap.values());

					// System.out.println("Global var locks ---- "+globalVarLocks.size());
					// System.out.println("Local Var locks ----- "+newLocksFromdb.size());

					// System.out.println("Global var locks ---- "+globalVarLocks);
					// System.out.println("Local Var locks ----- "+newLocksFromdb);

					if (globalVarLocks.containsAll(newLocksFromdb)) {
						openLockObj.put("newOpenLocks", false);
					} else {
						openLockObj.put("newOpenLocks", true);
					}
					GlobalVariable.openTerminalLockers = lockMap;
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				session.close();
			}
		} else if (respObj.getString("PacketType").equalsIgnoreCase("lockstatusbytid")) {

		}

		boolean verify = (boolean) openLockObj.get("newOpenLocks");
		// System.out.println("errorror : "+verify);

		if (verify || openLockObj.length() > 1) {
			openLockObj.put("responseCode", "openlocker");
		} else {
			openLockObj.put("responseCode", "closelocker");
		}
		writer.append(openLockObj.toString());
		writer.flush();
		writer.close();
		session.close();
	}

	public Boolean compareLockerOpenTime(Date date, Time time) {
		int totMinutes = 0;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			String rDate = (date + " " + time).toString();
			java.util.Date recDate = sdf.parse(rDate);

			long rDateInMillisec = recDate.getTime();
			long cDateInMilisec = new java.util.Date().getTime();
			long timeDiff = cDateInMilisec - rDateInMillisec;
			totMinutes = (int) TimeUnit.MILLISECONDS.toMinutes(timeDiff);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		if (totMinutes > 5) {
			return true;
		} else {
			return false;
		}
	}
}
