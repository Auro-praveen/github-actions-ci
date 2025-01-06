package com.locker.operations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import com.auro.beans.SiteRegistration;
import com.auro.beans.TransactionDetails;
import com.auro.hibernateUtilities.HibernateUtils;

/**
 * Servlet implementation class FetchActiveTransactionMallAuth
 */
@WebServlet("/FetchActiveTransactionMallAuth")
public class FetchActiveTransactionMallAuth extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FetchActiveTransactionMallAuth() {
        super();
        // TODO Auto-generated constructor stub
    }


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");
		
		String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(Collectors.joining("/"));
		JSONObject reqObj = new JSONObject(jsonBody);
		
		PrintWriter writer = response.getWriter();
		
		Session session = HibernateUtils.getSession();
		
		String packetType = reqObj.getString("PacketType");
		

		
		JSONArray terminalIdArr = new JSONArray();
		JSONObject responseJSNObject = new JSONObject();
		JSONArray allActiveLocks = new JSONArray();
		
		if (packetType.equalsIgnoreCase("get-td")) {
			
			String siteLocation = reqObj.get("siteLocation").toString();

			
			// for converting json String to list 
			
			String selectedSiteNames = reqObj.get("siteName").toString().replace("[", "")
					.replace("]", "").replace("\"", "");
			
			Collection<String> siteNameList = new ArrayList<String>(
					Arrays.asList(selectedSiteNames.split(","))
					);
			
			
			String siteRegHql = "from SiteRegistration where siteName IN (:sName) and city=:siteLoc";
			
			List<SiteRegistration> siteRegObj = session.createQuery(siteRegHql).setParameterList("sName", siteNameList)
					.setParameter("siteLoc", siteLocation).getResultList();
			
			if (!siteRegObj.isEmpty()) {
				
				String termId;
				for (SiteRegistration siteRegistration : siteRegObj) {
					terminalIdArr.put(siteRegistration.getTerminalid());
				}
				
				if (terminalIdArr.length() > 1) {
					
					responseJSNObject.put("responseCode", "tid-202");	// for more thatn 1 terminal ID response code
					responseJSNObject.put("termIdArr", terminalIdArr);
					
					
				} else {
					termId = terminalIdArr.getString(0);
					
					String hqlActiveTransaction = "from TransactionDetails where terminalid=:termid";
					
					List<TransactionDetails> activeTransaction = session.createQuery(hqlActiveTransaction).setParameter("termid", termId)
							.getResultList();
					
					if (!activeTransaction.isEmpty()) {
						
						for (TransactionDetails tdActive : activeTransaction) {
							allActiveLocks.put(tdActive.getLockNo());
						}
						
						responseJSNObject.put("allLocks", allActiveLocks);
						responseJSNObject.put("actLocks", "status-200");	// active bookings from the terminal id
						
					} else {
						
						responseJSNObject.put("actLocks", "status-404");	// no active bookings from the terminal id
						
					}
					
					responseJSNObject.put("responseCode", "tid-200");	// for only one terminal id
					responseJSNObject.put("terminalId", termId);

				}
				
			} else {
				responseJSNObject.put("responseCode", "tid-404");	// no terminal id found in the site registration
			}
			
		} else if(packetType.equalsIgnoreCase("getactlocks")) {
			String termId = reqObj.getString("terminalId");
			
			String actiTransactionHQL = "from TransactionDetails where terminalid=:termid";
			
			List<TransactionDetails> activeTd = session.createQuery(actiTransactionHQL).setParameter("termid", termId).getResultList();
			
			if (!activeTd.isEmpty()) {
				for (TransactionDetails transactionDetails : activeTd) {
					allActiveLocks.put(transactionDetails.getLockNo());
				}
				responseJSNObject.put("responseCode", "acttid-200");
				responseJSNObject.put("actlocks", allActiveLocks);
			} else {
				responseJSNObject.put("responseCode", "acttid-404");
			}
			
		}
		
		writer.append(responseJSNObject.toString());
		writer.flush();
		session.close();
		writer.close();
		
	}

}
