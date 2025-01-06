package com.locks.gloablVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.Session;

import com.auro.beans.SiteRegistration;
import com.auro.beans.gstDetails;
import com.auro.hibernateUtilities.HibernateUtils;

import java.text.DecimalFormat;

public class GlobalVariable {

	public static Map<String, String> userDetails = new HashMap<>();
	public static Map<String, ArrayList<String>> openTerminalLockers = new HashMap<>();
	public static String fromGamil = "praveen@aurodisplay.com";
	public static String toGmail = "Raja@tuckpod.com";
	public static String razorpayKeyId = "rzp_live_sjKUSO8AovpnnS"; // live key_id rzp_live_sjKUSO8AovpnnS test_key_id
																	// rzp_test_kF1NdHUm47R7R4
	public static String razorpaySecretKey = "KgmWtCSt9TOG4KTxNGkSoD5C"; // live secret_key KgmWtCSt9TOG4KTxNGkSoD5C
																			// test_secret_key qy48Nhzq72txUptAWkQrMEqy

	// live key_id rzp_live_sjKUSO8AovpnnS test_key_id rzp_test_kF1NdHUm47R7R4
	// live secret_key KgmWtCSt9TOG4KTxNGkSoD5C test_secret_key
	// qy48Nhzq72txUptAWkQrMEqy

	// for the calculation of the gst

	private static final double CGSTpercent = 9.0;
	private static final double SGSTpercent = 9.0;

	public static HashMap<String, gstDetails> stateWise_gstMap = new HashMap<>();

	public static HashMap<String, String> statewise_terminalMap = new HashMap<>();

	public static double calulcateGstMethod(int amount, String gstType, String terminalId) {
		
//		System.out.println(terminalId);

		if (stateWise_gstMap.isEmpty()) {
			loadGstPercentaege();
		}

		if (statewise_terminalMap.isEmpty()) {
			LoadStatewiseTerminalidsMap();
		}

		String state = statewise_terminalMap.get(terminalId);

		double CGST = stateWise_gstMap.get(state).getCgstPercentage();
		double SGST = stateWise_gstMap.get(state).getIgstPercentage();

//		System.out.println(state);
//		System.out.println(SGST);
//		System.out.println(CGST);

		double gstAmount = 0;

		if (gstType == "CGST") {
			gstAmount = ((amount * CGST) / 100);
		} else {
			gstAmount = ((amount * SGST) / 100);
		}

		DecimalFormat decimalFormat = new DecimalFormat("#.00");
		gstAmount = Double.parseDouble(decimalFormat.format(gstAmount));
		return gstAmount;

	}

	public static void loadGstPercentaege() {

		String hql = "from gstDetails";

		Session session = HibernateUtils.getSession();

		try {

			List<gstDetails> gstDetailsList = (ArrayList<gstDetails>) session.createQuery(hql).getResultList();

			if (!gstDetailsList.isEmpty()) {
				for (gstDetails gstDetails : gstDetailsList) {
					stateWise_gstMap.put(gstDetails.getStateName(), gstDetails);
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			session.close();
		}

	}

	public static void LoadStatewiseTerminalidsMap() {
		String hql = "from SiteRegistration";

		Session session = HibernateUtils.getSession();

		try {

			List<SiteRegistration> siteRegistration = (ArrayList<SiteRegistration>) session.createQuery(hql)
					.getResultList();

			if (!siteRegistration.isEmpty()) {

				for (SiteRegistration siteRegister : siteRegistration) {

					statewise_terminalMap.put(siteRegister.getTerminalid(), siteRegister.getState());

				}
				
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			session.close();
		}

	}

}
