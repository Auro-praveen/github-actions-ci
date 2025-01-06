package com.auroLocker.fetch.data;

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
import org.hibernate.mapping.Array;
import org.json.JSONArray;
import org.json.JSONObject;

import com.auro.beans.LockCategory;
import com.auro.hibernateUtilities.HibernateUtils;



@WebServlet("/FetchLocksDetail")
public class FetchLocksDetail extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FetchLocksDetail() {
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
		
		PrintWriter writer = response.getWriter();
		
		LockCategory lockCategory = new LockCategory();
		
		Session session = HibernateUtils.getSession();
		
		
		JSONArray slno = new JSONArray();
		JSONArray catagory = new JSONArray();
		JSONArray noOfLocks = new JSONArray();
		JSONArray termId = new JSONArray();
		JSONArray locks = new JSONArray();
		JSONArray siteName = new JSONArray();
		JSONArray slotTime = new JSONArray();
		JSONArray amount = new JSONArray();
		JSONArray typeOfDay = new JSONArray();
		JSONObject jsonObj  =  new JSONObject();
		JSONArray minutsSlot = new JSONArray();
		JSONArray minutsSlotAmount = new JSONArray();
		try {
			session.beginTransaction();
			ArrayList<LockCategory> locksList =(ArrayList<LockCategory>) session.createQuery("FROM LockCategory").getResultList();
			for (LockCategory lockCat : locksList) {
				
				catagory.put(lockCat.getCategory());
				locks.put(lockCat.getLocks());
				siteName.put(lockCat.getSiteName());
				noOfLocks.put(lockCat.getNo_of_locks());
				termId.put(lockCat.getTerminalid());
				slno.put(lockCat.getSlno());
				slotTime.put(lockCat.getSlot());
				amount.put(lockCat.getAmount());
				typeOfDay.put(lockCat.getTypeofday());
				minutsSlotAmount.put(lockCat.getMinslotamt());
				minutsSlot.put(lockCat.getMinslot());
			}
			session.close();
			
			jsonObj.put("catagory", catagory);
			jsonObj.put("slno", slno);
			jsonObj.put("noOfLocks", noOfLocks);
			jsonObj.put("termId", termId);
			jsonObj.put("locks", locks);
			jsonObj.put("siteName", siteName);
			jsonObj.put("slotTime", slotTime);
			jsonObj.put("amount", amount);
			jsonObj.put("typeOfDay", typeOfDay);
			jsonObj.put("minuteslot", minutsSlot);
			jsonObj.put("minuteslotamount", minutsSlotAmount);
			
			if (jsonObj.length() == 0) {
				jsonObj.put("status", "nodata");
			} 
			writer.println(jsonObj.toString());
			writer.flush();
			
				
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// System.out.println("Inside fetch locker details");
		
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");
		
		String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(Collectors.joining("/"));
		JSONObject reqObj = new JSONObject(jsonBody);
		
		PrintWriter writer = response.getWriter();
		JSONObject jsonObj  =  new JSONObject();


		// System.out.println(reqObj);
		
		String packetType = reqObj.getString("PacketType");
		String terminalId = reqObj.getString("terminalID");
		
		Session session = HibernateUtils.getSession();
		Transaction tr = session.beginTransaction();
		
		ArrayList<String> catagories = new ArrayList<>();
		ArrayList<String> dayTypes = new ArrayList<>();
		String location = "";

		if (packetType.equalsIgnoreCase("getlockcatdet")) {
			
			try {
				
				ArrayList<LockCategory> lockCat =(ArrayList<LockCategory>) session.createQuery("FROM LockCategory WHERE terminalid='"+terminalId+"'").getResultList();
				// System.out.println(lockCat);
				for (LockCategory lockCategory : lockCat) {
					
					if (!location.equalsIgnoreCase(lockCategory.getSiteName())) {
						location = lockCategory.getSiteName();
					}
					
					if (!catagories.contains(lockCategory.getCategory())) {
						catagories.add(lockCategory.getCategory());
					}
					
					if(!dayTypes.contains(lockCategory.getTypeofday())) {
						dayTypes.add(lockCategory.getTypeofday());
					}
					
				}	
				tr.commit();
				
				if (catagories.size() > 0) {
					jsonObj.put("responseCode", "lcatpresent");
					jsonObj.put("location", location);
					jsonObj.put("categories", catagories);
					jsonObj.put("typeofday", dayTypes);
				} else {
					jsonObj.put("responseCode", "nolockcat");
				}
				
				
				
				
			} catch (Exception e) {
				// TODO: handle exception
				jsonObj.put("responseCode", "nolockcat");
			}
		}
		
		else if (packetType.equalsIgnoreCase("lockcatdet")) {
			String lockCategory = reqObj.getString("lockCat");
			String typeOfDay = reqObj.getString("dayType");
			
			JSONArray hourSlot = new JSONArray();
			JSONArray amountSlot = new JSONArray();
			int minSlot = 0;
			int minSlotAmount = 0;
			String totalNumberOfLocks = null;
			String timeSlot = null;
			String allLocks = null;
			try {
				
				List<LockCategory> lockCatDetials = (ArrayList<LockCategory>) session.createQuery("from LockCategory where terminalid"
						+ "='"+terminalId+"' and category='"+lockCategory+"' and typeofday='"+typeOfDay+"'").getResultList();
						
				for (LockCategory lockCat : lockCatDetials) {
					totalNumberOfLocks = lockCat.getNo_of_locks();
					minSlot = lockCat.getMinslot();
					minSlotAmount = lockCat.getMinslotamt();
					// System.out.println(minSlot + "   :   "+ minSlotAmount);
					timeSlot = lockCat.getSlot();
					allLocks = lockCat.getLocks();
				}
				String[] arr = timeSlot.split("#");
				
				// System.out.println(totalNumberOfLocks);
				
				for(int i = 0; i < arr.length; i++) {
					String[] splitHour = arr[i].split("HOUR");
					hourSlot.put(splitHour[0]);
					amountSlot.put(splitHour[1]);
				}
				
				tr.commit();
				
				jsonObj.put("responseCode", "lockcatdetavail");
				jsonObj.put("hourslot", hourSlot);
				jsonObj.put("minuteslot", minSlot);
				jsonObj.put("hoursamount", amountSlot);
				jsonObj.put("minuteslotamount", minSlotAmount);
				jsonObj.put("numberoflocks", totalNumberOfLocks);
				jsonObj.put("alllocks", allLocks);
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				jsonObj.put("responseCode", "nolockdet");
			}
		} 
		writer.print(jsonObj);
		session.close();
		writer.close();
		
	}

}
