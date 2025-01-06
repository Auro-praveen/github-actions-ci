package com.auroLocker.fetch.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.jboss.jandex.TypeTarget;
import org.json.JSONArray;
import org.json.JSONObject;

import com.auro.beans.LockCategory;
import com.auro.beans.LockerOccupacyCountBean;
import com.auro.beans.SiteRegistration;
import com.auro.beans.TermwiseLockermalfunction;
import com.auro.beans.TransactionDetails;
import com.auro.beans.TransactionHistory;
import com.auro.hibernateUtilities.HibernateUtils;
import com.google.gson.Gson;

import net.bytebuddy.description.type.TypeVariableToken;

/**
 * Servlet implementation class FetchLockersForChart Praveenkumar here the data
 * is sent for the graphical representation one is for active transaction from
 * all the terminals and other one is for transaction history in graphical
 * representation
 * 
 * history for graphical representation is bit different as we just need the
 * total bookings from the transition history for selected date of range for the
 * selected pair of terminal ids, if no bookings on the date need to put 0, for
 * graph represnetation
 * 
 * the data need to be send in sorted date format from the range of selected
 * date, be cautios while editing this page especially
 */

@WebServlet("/FetchLockersForChart")
public class FetchLockersForChart extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FetchLockersForChart() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE");
		response.setCharacterEncoding("UTF-8");

		Session session = HibernateUtils.getSession();
		session.beginTransaction();

		String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines()
				.collect(Collectors.joining("/"));
		JSONObject requestedObject = new JSONObject(jsonBody);

		String packetType = requestedObject.getString("PacketType");
		ArrayList<String> terminalIds;
		JSONArray activeTransaction = new JSONArray();

		PrintWriter writer = response.getWriter();

		JSONObject responseObject = new JSONObject();

		if (packetType.equalsIgnoreCase("activetransaction")) {
			String hql = "SELECT terminalid FROM SiteRegistration";
			terminalIds = (ArrayList<String>) session.createQuery(hql).getResultList();

			for (String termId : terminalIds) {
				String transactionDetailsHql = "select count(*) from TransactionDetails where terminalid=:terminalID";
				long totalActive = (long) session.createQuery(transactionDetailsHql).setParameter("terminalID", termId)
						.uniqueResult();

//				System.out.println(totalActive);
				activeTransaction.put(totalActive);
			}

			responseObject.put("terminalId", terminalIds);
			responseObject.put("activeTd", activeTransaction);
			responseObject.put("responseCode", "activetd");

		}
		// here its all about getting the transaction history data date wise occording o
		// terminal id and count the transaction on
		// that particular date
		else if (packetType.equalsIgnoreCase("getthchart")) {
			Date fromDate = Date.valueOf(requestedObject.getString("fromDate"));
			Date toDate = Date.valueOf(requestedObject.getString("toDate"));

			String selectedTerminalId = requestedObject.get("terminalID").toString().replace("[", "").replace("]", "")
					.replace("\"", "");

			List<TransactionHistory> transHistList = new ArrayList<>();

			List<String> allHistoryDates = new ArrayList<>();

			if (selectedTerminalId.equalsIgnoreCase("all")) {

				String getTHHql = "FROM TransactionHistory WHERE date_of_open BETWEEN :fromDate AND :toDate";
				transHistList = (ArrayList<TransactionHistory>) session.createQuery(getTHHql, TransactionHistory.class)
						.setParameter("fromDate", fromDate).setParameter("toDate", toDate).getResultList();
			} else {
				Collection<String> termIdList = new ArrayList<String>(Arrays.asList(selectedTerminalId.split(",")));
				String getTHHql = "FROM TransactionHistory WHERE date_of_open BETWEEN :fromDate AND :toDate AND terminalid IN (:termIdList)";
				transHistList = (ArrayList<TransactionHistory>) session.createQuery(getTHHql, TransactionHistory.class)
						.setParameter("fromDate", fromDate).setParameter("toDate", toDate)
						.setParameterList("termIdList", termIdList).getResultList();
			}

			HashMap<String, Map<String, Integer>> historyMap = new HashMap<>();

			if (!transHistList.isEmpty()) {

				for (TransactionHistory transactionHistory : transHistList) {

					if (!allHistoryDates.contains(transactionHistory.getDate_of_open().toString())) {
						allHistoryDates.add(transactionHistory.getDate_of_open().toString());
					}

					if (historyMap.containsKey(transactionHistory.getTerminalid())) {
						Map<String, Integer> map = historyMap.get(transactionHistory.getTerminalid());

						if (map.containsKey(transactionHistory.getDate_of_open().toString())) {

							int count = map.get(transactionHistory.getDate_of_open().toString()) + 1;
//							System.out.println(count);
							map.put(transactionHistory.getDate_of_open().toString(), count);

						} else {
							map.put(transactionHistory.getDate_of_open().toString(), 1);
						}

					} else {

						Map<String, Integer> map = new HashMap<>();
						map.put(transactionHistory.getDate_of_open().toString(), 1);
						historyMap.put(transactionHistory.getTerminalid(), map);

					}

//					if (!TerminalIdlst.contains(transactionHistory.getTerminalid()) || TerminalIdlst.isEmpty()) {
//						TerminalIdlst.add(transactionHistory.getTerminalid());
//					}
//					
//					if (dateOfTransactionlst.isEmpty() || !dateOfTransactionlst.contains(transactionHistory.getDate_of_open())) {
//						
//					}
				}

				if (!historyMap.isEmpty()) {
					for (Map.Entry<String, Map<String, Integer>> entry : historyMap.entrySet()) {
						JSONObject uniqTermIdjsn = new JSONObject();

						int i = allHistoryDates.size() - 1;
						List<String> nonPresentDatesInTerminalId = new ArrayList<>();
						Map<String, Integer> compareDatesMap = entry.getValue();
						while (i != -1) {

							if (!compareDatesMap.containsKey(allHistoryDates.get(i))) {
								nonPresentDatesInTerminalId.add(allHistoryDates.get(i));
							}
							i = i - 1;
						}

//						if (!nonPresentDatesInTerminalId.isEmpty()) {
//							Map<String, Integer> nonPresentDateCount = new HashMap<>();
//							for (String nonPresetDate : nonPresentDatesInTerminalId) {
//								nonPresentDateCount.put(nonPresetDate, 0);
//							}
//						}

						for (Map.Entry<String, Integer> subEntry : entry.getValue().entrySet()) {

							uniqTermIdjsn.put(subEntry.getKey(), subEntry.getValue());
						}

						if (!nonPresentDatesInTerminalId.isEmpty()) {
							for (String nonPresentDate : nonPresentDatesInTerminalId) {
								uniqTermIdjsn.put(nonPresentDate, 0);
							}
						}

						Gson gson = new Gson();

						Type type = new TypeToken<TreeMap<String, Integer>>() {
						}.getType();

						TreeMap<String, Integer> submap = gson.fromJson(uniqTermIdjsn.toString(), type);
//						System.out.println(uniqTermIdjsn);
//						System.out.println(submap);

						JSONObject sortedJsnObj = new JSONObject();
						String sortOrderStr = gson.toJson(submap);
						sortedJsnObj.put("availtermidsort", sortOrderStr);
//						System.out.println("sorted json object");
//						System.out.println(sortedJsnObj);

						responseObject.put(entry.getKey(), sortedJsnObj);
					}
					responseObject.put("responseCode", "tdhist-200");
				}

//				System.out.println(historyMap);
//				System.out.println(responseObject);

			} else {
				responseObject.put("responseCode", "tdhist-404");
			}

		} else if (packetType.equalsIgnoreCase("getthcltn")) {
			Date fromDate = Date.valueOf(requestedObject.getString("fromDate"));
			Date toDate = Date.valueOf(requestedObject.getString("toDate"));
			String selectedTerminalId = requestedObject.get("terminalID").toString().replace("[", "").replace("]", "")
					.replace("\"", "");

			List<TransactionHistory> transHistList = new ArrayList<>();

			List<String> allHistoryDates = new ArrayList<>();

			if (selectedTerminalId.equalsIgnoreCase("all")) {

				String getTHHql = "FROM TransactionHistory WHERE date_of_open BETWEEN :fromDate AND :toDate AND status NOT IN('payFailCancel', 'payFailPaylater')";
				transHistList = (ArrayList<TransactionHistory>) session.createQuery(getTHHql, TransactionHistory.class)
						.setParameter("fromDate", fromDate).setParameter("toDate", toDate).getResultList();
			} else {
				Collection<String> termIdList = new ArrayList<String>(Arrays.asList(selectedTerminalId.split(",")));
				String getTHHql = "FROM TransactionHistory WHERE date_of_open BETWEEN :fromDate AND :toDate AND status NOT IN('payFailCancel', 'payFailPaylater') AND terminalid IN (:termIdList)";
				transHistList = (ArrayList<TransactionHistory>) session.createQuery(getTHHql, TransactionHistory.class)
						.setParameter("fromDate", fromDate).setParameter("toDate", toDate)
						.setParameterList("termIdList", termIdList).getResultList();
			}

			HashMap<String, Map<String, Integer>> historyMap = new HashMap<>();
			HashMap<String, Integer> termViceCollection = new HashMap<>();
			int totAmountFromAllTermId = 0;

			if (!transHistList.isEmpty()) {
				for (TransactionHistory transactionHistory : transHistList) {

//					System.out.println(transactionHistory.getTerminalid());

					if (!allHistoryDates.contains(transactionHistory.getDate_of_open().toString())) {
						allHistoryDates.add(transactionHistory.getDate_of_open().toString());
					}

					// total amount transaction between the provided date
					totAmountFromAllTermId += (int) ((int) (transactionHistory.getAmount() / 100)
							+ (int) (transactionHistory.getExcess_amount() / 100));

					// for the total amount
					if (termViceCollection.containsKey(transactionHistory.getTerminalid())) {

						int amount = (int) ((int) termViceCollection.get(transactionHistory.getTerminalid())
								+ (int) (transactionHistory.getAmount() / 100)
								+ (int) (transactionHistory.getExcess_amount() / 100));
						termViceCollection.put(transactionHistory.getTerminalid(), amount);

					} else {
						termViceCollection.put(transactionHistory.getTerminalid(),
								(int) (transactionHistory.getAmount() / 100)
										+ (int) (transactionHistory.getExcess_amount() / 100));
					}

					// for creating map for every terminal id and every sub map for every
					// transaction date

					if (historyMap.containsKey(transactionHistory.getTerminalid())) {
						Map<String, Integer> map = historyMap.get(transactionHistory.getTerminalid());

						if (map.containsKey(transactionHistory.getDate_of_open().toString())) {

							int count = map.get(transactionHistory.getDate_of_open().toString())
									+ (int) (transactionHistory.getAmount() / 100)
									+ (int) (transactionHistory.getExcess_amount() / 100);
//							System.out.println(count);
							map.put(transactionHistory.getDate_of_open().toString(), count);

						} else {
							map.put(transactionHistory.getDate_of_open().toString(),
									(int) (transactionHistory.getAmount() / 100)
											+ (int) (transactionHistory.getExcess_amount() / 100));
						}

					} else {

						Map<String, Integer> map = new HashMap<>();
						map.put(transactionHistory.getDate_of_open().toString(),
								(int) (transactionHistory.getAmount() / 100)
										+ (int) (transactionHistory.getExcess_amount() / 100));
						historyMap.put(transactionHistory.getTerminalid(), map);

					}

				}
				if (!historyMap.isEmpty()) {
					Gson gson = new Gson();
					for (Map.Entry<String, Map<String, Integer>> entry : historyMap.entrySet()) {
						JSONObject uniqTermIdjsn = new JSONObject();

						int i = allHistoryDates.size() - 1;
						List<String> nonPresentDatesInTerminalId = new ArrayList<>();
						Map<String, Integer> compareDatesMap = entry.getValue();
						while (i != -1) {

							if (!compareDatesMap.containsKey(allHistoryDates.get(i))) {
								nonPresentDatesInTerminalId.add(allHistoryDates.get(i));
							}
							i = i - 1;
						}

//						if (!nonPresentDatesInTerminalId.isEmpty()) {
//							Map<String, Integer> nonPresentDateCount = new HashMap<>();
//							for (String nonPresetDate : nonPresentDatesInTerminalId) {
//								nonPresentDateCount.put(nonPresetDate, 0);
//							}
//						}

						for (Map.Entry<String, Integer> subEntry : entry.getValue().entrySet()) {

							uniqTermIdjsn.put(subEntry.getKey(), subEntry.getValue());
						}

						if (!nonPresentDatesInTerminalId.isEmpty()) {
							for (String nonPresentDate : nonPresentDatesInTerminalId) {
								uniqTermIdjsn.put(nonPresentDate, 0);
							}
						}

						// we use gson to convert map into JSONObject from json to map

						Type type = new TypeToken<TreeMap<String, Integer>>() {
						}.getType();

						TreeMap<String, Integer> submap = gson.fromJson(uniqTermIdjsn.toString(), type);

						// converting from map to json object
						JSONObject sortedJsnObj = new JSONObject();
						String sortOrderStr = gson.toJson(submap);

						sortedJsnObj.put("availtermidsort", sortOrderStr);

						responseObject.put(entry.getKey(), sortedJsnObj);
					}
					String totCollectionTerminalVice = gson.toJson(termViceCollection);
					responseObject.put("responseCode", "thcltn-200");
					responseObject.put("termtotcltn", totCollectionTerminalVice);
					responseObject.put("totcltn", totAmountFromAllTermId);
				} else {

					responseObject.put("responseCode", "tdhist-404");
				}

			}

//			System.out.println(responseObject);

		} else if (packetType.equalsIgnoreCase("lockersdet")) {
			String hql = "select terminalid, no_of_locks from SiteRegistration";
			List<Object[]> termIdList = session.createQuery(hql, Object[].class).list();

			JSONArray arrayForAllLocksTerminal = new JSONArray();

			int totLockersInAllTerminalId = 0;
			int allLockersInMalfunction = 0;
			int allActiveLockersFromAllTermId = 0;

			JSONObject terminalwiseLockerDetailsObject = new JSONObject();
			JSONArray totNumLocksArr = new JSONArray();

//			System.out.println("siteRegistrationData = "+termIdList);

			for (Object[] object : termIdList) {

//				System.out.println("load from here ");

				String terminalId = (String) object[0];
				int totLockCount = Integer.valueOf((String) object[1]);

				JSONArray arrayOfLockDetails = new JSONArray();

//				System.out.println("total lockers - "+ totLockCount);

				String malfunctionHql = "from TermwiseLockermalfunction where terminalid=:termid";
				String activeTdHql = "select count(*) from TransactionDetails where terminalid=:termId";

				List<TermwiseLockermalfunction> malfunctionLocks = session.createQuery(malfunctionHql)
						.setParameter("termid", terminalId).getResultList();

				int malLocks = 0;
				int activeLocks = 0;

				if (!malfunctionLocks.isEmpty()) {
					for (TermwiseLockermalfunction malfunctionObject : malfunctionLocks) {
						if (!malfunctionObject.getLocks().isEmpty()) {
//							System.out.println("length of malfunction lockers : "+malfunctionObject.getLocks().split(",").length);

							malLocks = malfunctionObject.getLocks().split(",").length;
						}

					}
				}

				long activeTransactionCount = (long) session.createQuery(activeTdHql).setParameter("termId", terminalId)
						.uniqueResult();

				if (activeTransactionCount != 0) {
					activeLocks = (int) activeTransactionCount;
				}

				int availableLocks = totLockCount - malLocks - activeLocks;

				totNumLocksArr.put(totLockCount);
				// json object should be in the order of available lockers, active lockers,
				// malfunction lockers, total lockers object

				arrayOfLockDetails.put(availableLocks);
				arrayOfLockDetails.put(activeLocks);
				arrayOfLockDetails.put(malLocks);

//				arrayOfLockDetails.put(totalNumberOfLocks);

				terminalwiseLockerDetailsObject.put(terminalId, arrayOfLockDetails);

				// for all the lockers in terminal id and its details

				totLockersInAllTerminalId += totLockCount;
				allActiveLockersFromAllTermId += activeLocks;
				allLockersInMalfunction += malLocks;

			}

			int totAllAvailableLocksFromAllTermId = totLockersInAllTerminalId - allActiveLockersFromAllTermId
					- allLockersInMalfunction;

			arrayForAllLocksTerminal.put(totAllAvailableLocksFromAllTermId); // available
			arrayForAllLocksTerminal.put(allActiveLockersFromAllTermId); // active locks
			arrayForAllLocksTerminal.put(allLockersInMalfunction); // malfunction locks

			JSONArray lockerMappingAs = new JSONArray();
			lockerMappingAs.put("available-locks");
			lockerMappingAs.put("active-locks");
			lockerMappingAs.put("malfunction-locks");

			responseObject.put("alllocksDetails", arrayForAllLocksTerminal);
			responseObject.put("terminalwiseLocks", terminalwiseLockerDetailsObject);
			responseObject.put("totallLocks", totLockersInAllTerminalId);
			responseObject.put("lockersMappedAs", lockerMappingAs);

			responseObject.put("responseCode", "lock-details");

		}

		// locker occupacy for lockers locker usage count
		else if (packetType.equalsIgnoreCase("getlocksavg-occupacy")) {

			Date fromDate = Date.valueOf(requestedObject.getString("fromDate"));
			Date toDate = Date.valueOf(requestedObject.getString("toDate"));
			String selectedTerminalId = requestedObject.get("terminalID").toString().replace("[", "").replace("]", "")
					.replace("\"", "");

			List<TransactionHistory> transHistList = new ArrayList<>();
			List<LockCategory> locksCategory = new ArrayList<>();

			List<String> allHistoryDates = new ArrayList<>();

			HashMap<String, Map<String, Integer>> lockerTypeOccupacyMap = new HashMap<>();

			if (selectedTerminalId.equalsIgnoreCase("all")) {

				String hqlTotLocks = "from LockCategory";
				String termIdHistory = "FROM TransactionHistory WHERE date_of_open BETWEEN :fDate AND :tDate AND status NOT IN('payFailCancel', 'payFailPaylater')";

				// for getting the total number of lockers present in the selected terminalid's
				locksCategory = (ArrayList<LockCategory>) session.createQuery(hqlTotLocks, LockCategory.class)
						.getResultList();

				// for collecting the transaction history between the dates and terminal id
				// provided by the user where status is retrievecnf
				transHistList = (ArrayList<TransactionHistory>) session
						.createQuery(termIdHistory, TransactionHistory.class).setParameter("fDate", fromDate)
						.setParameter("tDate", toDate).getResultList();

			} else {
				Collection<String> termIdList = new ArrayList<String>(Arrays.asList(selectedTerminalId.split(",")));
				String hqlTotLocks = "from LockCategory where terminalid in (:terminalID)";
				String termIdHistory = "FROM TransactionHistory WHERE terminalid IN :(TerminalID) AND date_of_open BETWEEN :fDate AND :tDate AND status NOT IN('payFailCancel', 'payFailPaylater')";

				// for getting the total number of lockers present in the selected terminalid's
				locksCategory = (ArrayList<LockCategory>) session.createQuery(hqlTotLocks, LockCategory.class)
						.setParameter("terminalID", termIdList).getResultList();

				// for collecting the transaction history between the dates and terminal id
				// provided by the user where status is retrievecnf
				transHistList = (ArrayList<TransactionHistory>) session
						.createQuery(termIdHistory, TransactionHistory.class).setParameter("fDate", fromDate)
						.setParameter("tDate", toDate).setParameterList("terminalID", termIdList).getResultList();
			}

			if (!locksCategory.isEmpty()) {
				for (LockCategory lockCat : locksCategory) {
					if (lockerTypeOccupacyMap.containsKey(lockCat.getTerminalid())) {
						Map<String, Integer> map = lockerTypeOccupacyMap.get(lockCat.getTerminalid());

						if (!map.containsKey(lockCat.getCategory())) {
							map.put(lockCat.getCategory(), 0);
						}

					} else {
						Map<String, Integer> map = new HashMap<>();
						map.put(lockCat.getCategory(), 0);
						lockerTypeOccupacyMap.put(lockCat.getTerminalid(), map);
					}
				}
			}

//			System.out.println(lockerTypeOccupacyMap);

			if (!transHistList.isEmpty()) {

				int totSmallCollection = 0;
				int totMediumCollection = 0;
				int totLargeCollection = 0;
				int totElargeCollection = 0;

				for (TransactionHistory transactionHistory : transHistList) {

					if (lockerTypeOccupacyMap.containsKey(transactionHistory.getTerminalid())) {
						Map<String, Integer> map = lockerTypeOccupacyMap.get(transactionHistory.getTerminalid());

						if (transactionHistory.getLockNo().contains("S")) {
							int val = map.get("Small") + (int) (transactionHistory.getAmount() / 100)
									+ (int) (transactionHistory.getExcess_amount() / 100);
							map.put("Small", val);
							totSmallCollection += (int) (transactionHistory.getAmount() / 100
									+ (int) (transactionHistory.getExcess_amount() / 100));
						} else if (transactionHistory.getLockNo().contains("M")) {
							int val = map.get("Medium") + (int) (transactionHistory.getAmount() / 100
									+ (int) (transactionHistory.getExcess_amount() / 100));
							map.put("Medium", val);
							totMediumCollection += (int) (transactionHistory.getAmount() / 100
									+ (int) (transactionHistory.getExcess_amount() / 100));
						} else if (transactionHistory.getLockNo().contains("XL")) {
							int val = map.get("eLarge") + (int) (transactionHistory.getAmount() / 100
									+ (int) (transactionHistory.getExcess_amount() / 100));
							map.put("eLarge", val);
							totLargeCollection += (int) (transactionHistory.getAmount() / 100
									+ (int) (transactionHistory.getExcess_amount() / 100));
						} else if (transactionHistory.getLockNo().contains("L")) {
							int val = map.get("Large") + (int) (transactionHistory.getAmount() / 100
									+ (int) (transactionHistory.getExcess_amount() / 100));
							map.put("Large", val);
							totElargeCollection += (int) (transactionHistory.getAmount() / 100
									+ (int) (transactionHistory.getExcess_amount() / 100));
						}

					}

				}

				Gson gson = new Gson();

				String lockersOccupacyByTypeStr = gson.toJson(lockerTypeOccupacyMap);

//				(lockersOccupacyByTypeStr);

				JSONArray totLockTypeNamesArr = new JSONArray();
				JSONArray totLockTypeCollectionArr = new JSONArray();

				totLockTypeNamesArr.put("Small");
				totLockTypeNamesArr.put("Medium");
				totLockTypeNamesArr.put("Large");
				totLockTypeNamesArr.put("eLarge");

				totLockTypeCollectionArr.put(totSmallCollection);
				totLockTypeCollectionArr.put(totMediumCollection);
				totLockTypeCollectionArr.put(totLargeCollection);
				totLockTypeCollectionArr.put(totElargeCollection);

				responseObject.put("responseCode", "loc-type-200");
				responseObject.put("lockTypeDetails", lockersOccupacyByTypeStr);
				responseObject.put("totLockTypeNames", totLockTypeNamesArr);
				responseObject.put("totLockTypecltn", totLockTypeCollectionArr);

			} else {
				responseObject.put("responseCode", "loc-type-404");
			}

		}

		// this one is for locker occupacy percentage for locker by its collection
		else if (packetType.equalsIgnoreCase("lock-occupacy-count")) {

			Date fromDate = Date.valueOf(requestedObject.getString("fromDate"));
			Date toDate = Date.valueOf(requestedObject.getString("toDate"));
			String selectedTerminalId = requestedObject.get("terminalID").toString().replace("[", "").replace("]", "")
					.replace("\"", "");

			List<TransactionHistory> transHistList = new ArrayList<>();
//			List<LockCategory> locksCategory = new ArrayList<>();

			List<String> allHistoryDates = new ArrayList<>();

			List<Object> historyLockerUsageData = new ArrayList<>();
			HashMap<String, Map<String, Integer>> lockerTypeOccupacyMap = new HashMap<>();

			List<LockerOccupacyCountBean> lockhist;

			if (selectedTerminalId.equalsIgnoreCase("all")) {

				String termIdHistory = "SELECT NEW com.auro.beans.LockerOccupacyCountBean(terminalid, lockNo, COUNT(lockNo)) FROM TransactionHistory WHERE date_of_open BETWEEN :fDate "
						+ "AND :tDate AND status NOT IN ('payFailCancel') GROUP BY terminalid, lockNo";

				// for collecting the transaction history between the dates and terminal id
				// provided by the user where status is retrievecnf
//				transHistList = (ArrayList<TransactionHistory>) session
//						.createQuery(termIdHistory, TransactionHistory.class).setParameter("fDate", fromDate)
//						.setParameter("tDate", toDate).getResultList();

				Query query = session.createQuery(termIdHistory).setParameter("fDate", fromDate).setParameter("tDate",
						toDate);

				lockhist = query.list();

//				historyLockerUsageData = query.getSession();

//				System.out.println(lockhist);

			} else {

				Collection<String> termIdList = new ArrayList<String>(Arrays.asList(selectedTerminalId.split(",")));

				String termIdHistory = "SELECT NEW com.auro.beans.LockerOccupacyCountBean(terminalid, lockNo, COUNT(lockNo)) FROM TransactionHistory WHERE terminalid IN :(TerminalID) AND date_of_open BETWEEN :fDate AND :tDate AND "
						+ "status NOT IN ('payFailCancel') GROUP BY terminalid, lockNo";

				// for collecting the transaction history between the dates and terminal id
				// provided by the user where status is retrievecnf

				Query query = session.createQuery(termIdHistory).setParameterList("TerminalID", termIdList).setParameter("fDate", fromDate)
						.setParameter("tDate", toDate);

				lockhist = query.list();
//				System.out.println(lockhist);

			}

			int totSmallCount = 0;
			int totMediumCount = 0;
			int totLargeCount = 0;
			int totXlargeCount = 0;

//			System.out.println(historyLockerUsageData);

			if (!lockhist.isEmpty()) {

//				for (TransactionHistory transactionHistory : transHistList) {

//					if (lockerTypeOccupacyMap.containsKey(transactionHistory.getTerminalid())) {
//						
//						HashMap<String, Integer> termwiseLockOccupacy = (HashMap<String, Integer>) 
//								lockerTypeOccupacyMap.get(transactionHistory.getTerminalid());
//						
//						System.out.println(transactionHistory.getLockNo());
//						System.out.println(lockerTypeOccupacyMap);
//						
//						if (transactionHistory.getLockNo().contains("S")) {
//							
//							if (termwiseLockOccupacy.containsKey("small")) {
//								termwiseLockOccupacy.put("small", Integer.valueOf(termwiseLockOccupacy.get("small")) + 1);
//							} else {
//								termwiseLockOccupacy.put("small", 1);
//							}
//							
//							totSmallCount += 1;
//							
//							
//						} else if (transactionHistory.getLockNo().contains("M")) {
//							
//							
//							if (termwiseLockOccupacy.containsKey("medium")) {
//								termwiseLockOccupacy.put("medium", Integer.valueOf(termwiseLockOccupacy.get("medium")) + 1);
//							} else {
//								termwiseLockOccupacy.put("medium", 1);
//							}
//							
//							
//							totMediumCount += 1;
//
//						} else if (transactionHistory.getLockNo().contains("L")) {
//							
//							if (termwiseLockOccupacy.containsKey("large")) {
//								termwiseLockOccupacy.put("large", Integer.valueOf(termwiseLockOccupacy.get("large")) + 1);
//							} else {
//								termwiseLockOccupacy.put("large", 1);
//							}
//							
//							totLargeCount += 1;
//							
//							
//						} else if (transactionHistory.getLockNo().contains("XL")) {
//							
//							if (termwiseLockOccupacy.containsKey("xLarge")) {
//								termwiseLockOccupacy.put("xLarge", Integer.valueOf(termwiseLockOccupacy.get("xLarge")) + 1);
//							} else {
//								termwiseLockOccupacy.put("xLarge", 1);
//							}
//							
//							
//							totXlargeCount += 1;
//							
//						}
//						
//						
//					} else {
//						
//						HashMap<String, Integer> termwiseLockOccupacy = new HashMap<>();
//						
//						
//						
//						if (transactionHistory.getLockNo().contains("S")) {
//							termwiseLockOccupacy.put("small", 1);
//							
//							totSmallCount += 1;
//							
//						} else if (transactionHistory.getLockNo().contains("M")) {
//							termwiseLockOccupacy.put("medium", 1);
//							totMediumCount += 1;
//							
//						} else if (transactionHistory.getLockNo().contains("L")) {
//							termwiseLockOccupacy.put("large", 1);
//							totLargeCount += 1;
//							
//						} else if (transactionHistory.getLockNo().contains("XL")) {
//							termwiseLockOccupacy.put("xLarge", 1);
//							totXlargeCount += 1;
//							
//						}
//						
//						lockerTypeOccupacyMap.put(transactionHistory.getTerminalid(), termwiseLockOccupacy);
//						
//						
//						System.out.println("inside else of termwise locker occupacy : "+transactionHistory.getLockNo());
//						System.out.println(lockerTypeOccupacyMap);
//					}
//				}

				for (LockerOccupacyCountBean lockerCount : lockhist) {
//					System.out.println(lockerCountBean.getTerminalid()+" --- "+ lockerCountBean.getLockNo() + " --- "+ lockerCountBean.getLockCount());
//					JSONObject countbean = (JSONObject) object;
//					LockerOccupacyCountBean
//					System.out.print(lockerCount.getLockNo() + " - ");
//					System.out.print(lockerCount.getTerminalid() + " - ");
//					System.out.print(lockerCount.getLockCount() + " - ");
//					System.out.println();

					if (lockerTypeOccupacyMap.containsKey(lockerCount.getTerminalid())) {

						HashMap<String, Integer> termwiseLockOccupacy = (HashMap<String, Integer>) lockerTypeOccupacyMap
								.get(lockerCount.getTerminalid());

//						System.out.println(lockerCount.getLockNo());
//						System.out.println(lockerTypeOccupacyMap);

						if (lockerCount.getLockNo().contains("S")) {

							if (termwiseLockOccupacy.containsKey("small")) {
								termwiseLockOccupacy.put("small",
										(Integer.valueOf(termwiseLockOccupacy.get("small")) + (int) lockerCount.getLockCount() ));
							} else {
								termwiseLockOccupacy.put("small", (int) lockerCount.getLockCount());
							}

							totSmallCount += (int) lockerCount.getLockCount();

						} else if (lockerCount.getLockNo().contains("M")) {

							if (termwiseLockOccupacy.containsKey("medium")) {
								termwiseLockOccupacy.put("medium",
										(Integer.valueOf(termwiseLockOccupacy.get("medium")) + (int) lockerCount.getLockCount() ));
							} else {
								termwiseLockOccupacy.put("medium", (int) lockerCount.getLockCount());
							}

							totMediumCount += (int) lockerCount.getLockCount();

						} else if (lockerCount.getLockNo().contains("XL")) {

							if (termwiseLockOccupacy.containsKey("xLarge")) {
								termwiseLockOccupacy.put("xLarge",
										(Integer.valueOf(termwiseLockOccupacy.get("xLarge")) + (int) lockerCount.getLockCount()));
							} else {
								termwiseLockOccupacy.put("xLarge", (int) lockerCount.getLockCount());
							}

							totXlargeCount += (int) lockerCount.getLockCount();

						} else if (lockerCount.getLockNo().contains("L")) {

							if (termwiseLockOccupacy.containsKey("large")) {
								termwiseLockOccupacy.put("large",
										(Integer.valueOf(termwiseLockOccupacy.get("large")) + (int) lockerCount.getLockCount()));
							} else {
								termwiseLockOccupacy.put("large", (int) lockerCount.getLockCount());
							}

							totLargeCount += (int) lockerCount.getLockCount();

						} 

					} else {

						HashMap<String, Integer> termwiseLockOccupacy = new HashMap<>();

						if (lockerCount.getLockNo().contains("S")) {
							termwiseLockOccupacy.put("small", (int) lockerCount.getLockCount());

							totSmallCount += (int) lockerCount.getLockCount();

						} else if (lockerCount.getLockNo().contains("M")) {
							termwiseLockOccupacy.put("medium", (int) lockerCount.getLockCount());
							totMediumCount += (int) lockerCount.getLockCount();

						}  else if (lockerCount.getLockNo().contains("XL")) {
							termwiseLockOccupacy.put("xLarge", (int) lockerCount.getLockCount());
							totXlargeCount += (int) lockerCount.getLockCount();

						} else if (lockerCount.getLockNo().contains("L")) {
							termwiseLockOccupacy.put("large", (int) lockerCount.getLockCount());
							totLargeCount += (int) lockerCount.getLockCount();

						}

						lockerTypeOccupacyMap.put(lockerCount.getTerminalid(), termwiseLockOccupacy);

//						System.out.println("inside else of termwise locker occupacy : " + lockerCount.getLockNo());
//						System.out.println(lockerTypeOccupacyMap);
					}
				}

				Gson gson = new Gson();
				String lockerTypeOccupacyCount = gson.toJson(lockerTypeOccupacyMap);

				JSONObject totLockercount = new JSONObject();

				if (totSmallCount > 0) {
					totLockercount.put("small", totSmallCount);
				}

				if (totMediumCount > 0) {
					totLockercount.put("medium", totMediumCount);
				}

				if (totLargeCount > 0) {
					totLockercount.put("large", totLargeCount);
				}

				if (totXlargeCount > 0) {
					totLockercount.put("xLarge", totXlargeCount);
				}

				responseObject.put("responseCode", "locker-occurancy-200");
				responseObject.put("termwise_lCount", lockerTypeOccupacyCount);
				responseObject.put("total_lCount", totLockercount);
				
//				System.out.println(responseObject);

			} else {
				responseObject.put("responseCode", "locker-occurancy-202");
			}

		}

		writer.append(responseObject.toString());

		writer.close();
		session.close();

	}

}
