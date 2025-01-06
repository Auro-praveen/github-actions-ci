package com.auro.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.json.JSONArray;
import org.json.JSONObject;

import com.auro.beans.LockCategory;
import com.auro.hibernateUtilities.HibernateUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import net.bytebuddy.implementation.bytecode.Throw;

/**
 * Servlet implementation class SaveLockerCategory
 */
@WebServlet("/SaveLockerCategory")
public class SaveLockerCategory extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(SaveLockerCategory.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveLockerCategory() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");
		
		String query = "SELECT terminalid FROM SiteRegistration WHERE terminalid NOT IN (SELECT terminalid FROM LockCategory)";
		
		Session session = HibernateUtils.getSession();
		ArrayList<String> terminalIds = null;
		Gson gson = new Gson();
		JSONArray termIds = null;
		
		try {
			
			session.beginTransaction();
			terminalIds = (ArrayList<String>) session.createQuery(query).getResultList();
			
			if (terminalIds.size() > 0) {
				String termData = gson.toJson(terminalIds);
				termIds = new JSONArray(termData);
			}
			
			System.out.println(termIds);
			
			logger.info("Locker Category has been added");
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("Some error occured while adding a new locker");
		} finally {
			session.close();
			
			if (termIds != null) {
				response.getWriter().append(termIds.toString()).flush();
				response.getWriter().close();
			} else {
				response.getWriter().append(null).flush();
				response.getWriter().close();
			}
			
		}
		
//		if (terminalIds.size() > 0 || terminalIds != null) {
//			response.getWriter().append(terminalIds.toString()).flush();
//		} else {
//			response.getWriter().append(terminalIds.toString()).flush();
//		}
		

		

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");
		
		String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(Collectors.joining("/"));

		String responseCode = "{\"responseCode\":200}";
		System.out.println(jsonBody);
		
		JSONObject reqObj = new JSONObject(jsonBody);
		
		ObjectMapper mapper = new ObjectMapper();

		//		Map<String, Object> map = mapper.readValue(jsonBody, Map.class);
		
		JsonNode jsonNode = mapper.readTree(jsonBody);
		
		Session session = HibernateUtils.getSession();
		Transaction transaction = session.beginTransaction();

		List<LockCategory> lockerCategoriesList = alterJsonNode(jsonNode);
		
		
		try {
			
			for (LockCategory lockCategory : lockerCategoriesList) {
				
				System.out.println();
				System.out.println();
				
				System.out.println(lockCategory.getTerminalid());
				System.out.println(lockCategory.getSiteName());
				System.out.println(lockCategory.getAmount());
				System.out.println(lockCategory.getCategory());
				System.out.println(lockCategory.getMinslot());
				System.out.println(lockCategory.getSlot());
				System.out.println(lockCategory.getTypeofday());
				System.out.println(lockCategory.getMinslotamt());
				System.out.println(lockCategory.getLocks());
				System.out.println(lockCategory.getNo_of_locks());
				
				
				int i = (int) session.save(lockCategory);
				
				
				System.err.println("here is the i response :: "+i);
				
			}
			
			session.flush();
			session.clear();
			
			transaction.commit();
			
		} catch (Exception e) {
			// TODO: handle exception
			responseCode = "{\"responseCode\":204}";
			e.printStackTrace();
		} finally {
			session.close();
			response.getWriter().append(responseCode).flush();
		}
		

		
//		session.flush();
//		session.clear();
//		
//		transaction.commit();
//		session.close();
		
//		response.getWriter().append("{\"responseCode\":200}").flush();
		
		
	}
	
	public List<LockCategory> alterJsonNode(JsonNode jsonNode) {
		
		List<String> locksType  = null;
		List<LockCategory> lockCategoryObjects = new ArrayList<>();
		String terminalId = null;
		String siteLocation = null;
		
		if (jsonNode.isObject()) {
			
			Iterator<Map.Entry<String, JsonNode>> requestField = jsonNode.fields();
			
			while (requestField.hasNext()) {
				
				Map.Entry<String, JsonNode> entry = requestField.next();
				
				switch (entry.getKey()) {
				
				case "Category":
					
					String locks = entry.getValue().toString();
					
					locksType = Arrays.asList(locks.replaceAll("[\\[\\]\"]", "").split(","));
					break;
				case "terminalId":
					terminalId = entry.getValue().toString().replaceAll("\"", "");
					break;
				case "siteLoc" :
					siteLocation = entry.getValue().toString().replaceAll("\"", "");
					break;
					
				case "slot":
					
					JsonNode slotsNode = entry.getValue();
					
					if (slotsNode.isObject()) {
						Iterator<Map.Entry<String, JsonNode>> slotMap = slotsNode.fields();
						
						while (slotMap.hasNext()) {
							
							Map.Entry<String, JsonNode> dayTypeSlots = slotMap.next();

							switch (dayTypeSlots.getKey()) {
							case "WeekDay":
								lockCategoryObjects.addAll(handleWeeklySlot(dayTypeSlots.getValue(), dayTypeSlots.getKey()));
								
								
								break;
							case "WeekEnd":
								lockCategoryObjects.addAll(handleWeeklySlot(dayTypeSlots.getValue(), dayTypeSlots.getKey()));
								
								break;
							}
							
						}
						
					}
					
					break;

				}
				
//				System.out.println(entry.getKey());
//				System.out.println(entry.getValue());
					
			}
			
			System.out.println("list is "+ locksType.size());
			
			System.out.println("terminal id is is is :   "+terminalId);
			
			
			if (lockCategoryObjects.size() > 0) {
				
				for (LockCategory lockCategory : lockCategoryObjects) {
					lockCategory.setTerminalid(terminalId);
					lockCategory.setSiteName(siteLocation);
				}
				
			}

		} else {
			System.out.println("Not a object");
		}
		
		return lockCategoryObjects;
	}
	
	public List<LockCategory> handleWeeklySlot(JsonNode weeklySotNode, String dayType) {
		
		List<LockCategory> lockCategoryObjects = new ArrayList<>();
		
		if (weeklySotNode.isObject()) {
			Iterator<Map.Entry<String, JsonNode>> weekSlotIter = weeklySotNode.fields();
			
			while (weekSlotIter.hasNext()) {
				Map.Entry<String, JsonNode> weekSlotMap = weekSlotIter.next();
				
				
				
				switch (weekSlotMap.getKey()) {
				
				case "amount":
					
					Iterator<Map.Entry<String, JsonNode>> amountSlot = weekSlotMap.getValue().fields();
					
					while (amountSlot.hasNext()) {
						Map.Entry<String, JsonNode> amoutMap = amountSlot.next();
						List<Integer> amountList = null;
						
						int NoOfLocks = 0;
						String locks = null;
						
						LockCategory lockCategory;
						
						String lockerCategory = amoutMap.getKey();
						
						switch (lockerCategory) {
						case "Small":
							amountList =  handleAmountSlots(amoutMap.getValue().toString());
							
							NoOfLocks = Integer.valueOf(weeklySotNode.get("NoOfLocksSmall").toString().replaceAll("[\\[\\]\"]", ""));
							locks = weeklySotNode.get("LocksSmall").toString().replaceAll("\"", "");
							
							break;

						case "Medium":
							amountList = handleAmountSlots(amoutMap.getValue().toString());
							NoOfLocks =  Integer.valueOf(weeklySotNode.get("NoOfLocksMedium").toString().replaceAll("[\\[\\]\"]", ""));
							locks = weeklySotNode.get("LocksMedium").toString().replaceAll("\"", "");
							break;
							
						case "Large":
							amountList = handleAmountSlots(amoutMap.getValue().toString());
							
							NoOfLocks = Integer.valueOf(weeklySotNode.get("NoOfLocksLarge").toString().replaceAll("[\\[\\]\"]", ""));
							locks = weeklySotNode.get("LocksLarge").toString().replaceAll("\"", "");
							
							break;
							
						case "eLarge":
							amountList = handleAmountSlots(amoutMap.getValue().toString());
							
							NoOfLocks = Integer.valueOf(weeklySotNode.get("NoOfLockseLarge").toString().replaceAll("[\\[\\]\"]", ""));
							locks = weeklySotNode.get("LockseLarge").toString().replaceAll("\"", "");
							
							break;
						}
						String amounts ="1HOUR"+amountList.get(1)+"#3HOUR"+amountList.get(2)+"#5HOUR"+amountList.get(3)+"#8HOUR"+amountList.get(4)+"#12HOUR"+amountList.get(5); 
						
						
						int minAmount = amountList.get(0); 
						
						lockCategory = new LockCategory("", "", String.valueOf(NoOfLocks), 
								locks, lockerCategory, amounts, 0, minAmount, dayType, 1);
						
						
						lockCategoryObjects.add(lockCategory);
								
					}

					break;

				}
			}
		}
		
		return lockCategoryObjects;
		
		
		
	}
	
	public List<Integer> handleAmountSlots(String amounts) {
		
		String[] amountArr = amounts.replaceAll("[\\[\\]\"]", "").split(","); 
		
//		List<S> list = Arrays.asList(amountArr);
//		
//		System.err.println("==============");
//		System.err.println();
//		System.out.println(list);
		
		List<Integer> amountList = new ArrayList<>();
		
		for (String string : amountArr) {
			amountList.add(Integer.valueOf(string));
		}
		
//		List<Integer> amountList = Arrays.stream(amountArr).map(Integer::parseInt).collect(Collectors.toList());
//		List<Integer> amountList = new ArrayList<>();
		
//		amountList.add(10);
//		amountList.add(10);
//		amountList.add(10);
//		amountList.add(10);
//		amountList.add(10);
//		amountList.add(10);
		
		
		
		return amountList;
		
	}

}



