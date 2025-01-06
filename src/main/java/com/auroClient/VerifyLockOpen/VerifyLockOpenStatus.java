package com.auroClient.VerifyLockOpen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

@WebServlet("/VerifyLockOpenStatus")
public class VerifyLockOpenStatus extends HttpServlet  {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		// System.out.println("inside verify lock open status");
		
		resp.addHeader("Access-Control-Allow-Origin", "*");
		resp.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		resp.setCharacterEncoding("UTF-8");
		
		PrintWriter writer = resp.getWriter();
		String jsonBody = new BufferedReader(new InputStreamReader(req.getInputStream())).lines().collect(Collectors.joining("/"));
		JSONObject reqObj = new JSONObject(jsonBody);
		
		// System.out.println(reqObj);
		
		JSONObject verifyLockObj = new JSONObject();
		int i = 0;
		while (true) {
			if(i == 15) {
				verifyLockObj.put("status", "success");
				break;
			} else {
				try {
					verifyLockObj.put("status", "fail");
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			i++;
			// System.out.println(i);
		}
		
		// System.out.println(verifyLockObj);
		
		writer.append(verifyLockObj.toString());
		writer.close();
		
	}
}
