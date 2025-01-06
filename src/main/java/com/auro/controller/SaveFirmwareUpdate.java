package com.auro.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import com.auro.beans.UpdateToDevice;
import com.auro.hibernateUtilities.HibernateUtils;

/**
 * Servlet implementation class SaveFirmareUpdate
 * Auther Praveenkumar
 * Firmware update is saved and updated to databases in the database
 * firmware save , update , delete  may 04 - 2023
 */

@WebServlet("/SaveFirmwareUpdate")
@MultipartConfig
public class SaveFirmwareUpdate extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String uploading_directory_path = "Downloads";
//	private static final String uploading_directory_path = "C:\\Program Files\\apache-server\\Downloads";
//	private static final String downPath = "D:\\FilesToDownload\\";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveFirmwareUpdate() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	// TODO Auto-generated method stub
    	super.doGet(req, resp);
    	
    	verifyFileSize(new File("D:\\Users\\AuroLed\\Downloads"), "praveen_basu_ticket.pdf",20000);
    }


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE");
		response.setCharacterEncoding("UTF-8");
		
		//for downloading the files
		response.setContentType("application/octet-stream");
		
		Session session = HibernateUtils.getSession();
		
		PrintWriter writer = response.getWriter();
		JSONObject responseObject = new JSONObject();
		
		// collecting
		JSONObject fileDetailObj = new JSONObject(request.getParameter("fileDetails"));
//		System.out.println(fileDetailObj);
		
		String packetType = fileDetailObj.getString("PacketType");
		
		String downloadingPath = request.getServletContext().getRealPath("");
		
		JSONArray slno = new JSONArray();
		JSONArray destinationPath = new JSONArray();
		JSONArray fName = new JSONArray();
		JSONArray genDate = new JSONArray();
		JSONArray genTime = new JSONArray();
		JSONArray updateDate = new JSONArray();
		JSONArray updateTime = new JSONArray();
		JSONArray updateType = new JSONArray();
		JSONArray status = new JSONArray();
		JSONArray terminalID = new JSONArray();
		
		
		session.beginTransaction();
		
		
		// for storing the file and file details in database
		
		try {
			if (packetType.equalsIgnoreCase("savefirmupdate")) {
				
				// collecting the file data as input stream
				
				Part filePart = request.getPart("file");
				String fileName = extractFileName(filePart);
				
				downloadingPath = downloadingPath +File.separator + uploading_directory_path +File.separator;
				
				
//				downloadingPath = uploading_directory_path +File.separator;
				
//				System.out.println("absolute path : "+downloadingPath);
				
//				System.out.println("get submittes file name : "+ filePart.getSubmittedFileName()); get FileName Directly
				// downloading files from the server so need to set the content type
				
				response.setHeader("Content-Disposition", "attachment; filename=\""+fileName+"\"");
				
				
				
				if (fileName != null && !fileName.equals("")) {
					
					File file = new File(downloadingPath);
					if (!file.exists()) {
						file.mkdir();
					}
					
//					File newFile = new File(file + "\\");
//					System.out.println("downloading path : "+downloadingPath);
//					System.out.println("file : "+file);
//					System.out.println("filename : "+newFile);
					
					boolean resp = writeFileToGivenPath(file, fileName, filePart);
					
					if (resp) {
						resp = false;
						resp = verifyFileSize(file, fileName, fileDetailObj.getLong("fileSize"));
					}

					if (resp) {
						
						UpdateToDevice updateDevice = new UpdateToDevice();
						
						updateDevice.setDestPath(fileDetailObj.getString("destPath"));
						updateDevice.setFileName(fileName);
						updateDevice.setGeneratedDate(new Date(System.currentTimeMillis()));
						updateDevice.setGeneratedTime(new Time(System.currentTimeMillis()));
//						updateDevice.setUpdatedDate(new Date(System.currentTimeMillis()));
//						updateDevice.setUpdatedTime(new Time(System.currentTimeMillis()));
						updateDevice.setStatus(fileDetailObj.getString("status"));
						updateDevice.setUpdateType(fileDetailObj.getString("firmwareType"));
						updateDevice.setTerminalID(fileDetailObj.getString("terminalId"));
						
						int storeInDbResp = (int) session.save(updateDevice);
//						System.out.println(storeInDbResp);
						if (storeInDbResp > 0) {
							responseObject.put("responseCode", "strsucc-200");
						} else {
							responseObject.put("responseCode", "strfail-400");
						}
						
					} else {
						responseObject.put("responeCode", "fwrite-404");
					}
				}
				
			} else if(packetType.equalsIgnoreCase("getfirmupdate")) {
				
				String hql = "from UpdateToDevice";
				
				List<UpdateToDevice> firmwareTables = session.createQuery(hql).getResultList();
				
				if (!firmwareTables.isEmpty()) {
					for (UpdateToDevice updateToDevice : firmwareTables) {
						slno.put(updateToDevice.getSlno());
						destinationPath.put(updateToDevice.getDestPath());
						fName.put(updateToDevice.getFileName());
						genDate.put(updateToDevice.getGeneratedDate());
						genTime.put(updateToDevice.getGeneratedTime());
						status.put(updateToDevice.getStatus());
						updateType.put(updateToDevice.getUpdateType());
						updateTime.put(updateToDevice.getUpdatedTime());
						updateDate.put(updateToDevice.getUpdatedDate());
						terminalID.put(updateToDevice.getTerminalID());
					}
					
					responseObject.put("responseCode", "firmwaretb-200");
					responseObject.put("destPath", destinationPath);
					responseObject.put("fileName", fName);
					responseObject.put("genDate", genDate);
					responseObject.put("genTime", genTime);
					responseObject.put("slno", slno);
					responseObject.put("status", status);
					responseObject.put("updateType", updateType);
					responseObject.put("updatedDate", updateDate);
					responseObject.put("updatedTime", updateTime);
					responseObject.put("terminalID", terminalID);
					
				} else {
					responseObject.put("responseCode", "nofirmupdate-300");
				}
				
				
			} 
			// to delete the firmware update record ask if only need to delete in database or delete the file as well 
			// because the file is over writing itself right now if the files from the same name are forces to download through servlet
			
			else if(packetType.equalsIgnoreCase("deletefirmware")) {
				
//				System.out.println("insde detetefirmare update");
				
				String delFileName = fileDetailObj.getString("fileName");
				String delDestPath = fileDetailObj.getString("destPath");
				int delSlno = Integer.valueOf(fileDetailObj.getInt("slno"));
				
				downloadingPath = downloadingPath +File.separator + uploading_directory_path + File.separator;
				
				File file = new File(downloadingPath+delFileName);
				
//				System.out.println(file);
				
				if (file.exists()) {
					boolean isdeleted  = file.delete();
					if (isdeleted) {
						try {
							
							UpdateToDevice upToDevice = session.get(UpdateToDevice.class, delSlno);
							

							if (upToDevice != null) {
//								System.out.println("object found inside if condition");
								session.delete(upToDevice);
								session.getTransaction().commit();
								responseObject.put("responseCode", "firmdelsuc-200");
							}else {
								responseObject.put("responseCode", "filenotfound-404");
							}
							
						} catch (Exception e) {
							// TODO: handle exception
							
							e.printStackTrace();
						}
					}
					
//					System.out.println("is file deleted = " +isdeleted);
					
				} else {
					
					UpdateToDevice upToDevice = session.get(UpdateToDevice.class, delSlno);
					if (upToDevice != null) {
//						System.out.println(upToDevice.getSlno());
//						System.out.println("object found inside else condition");
						session.delete(upToDevice);
						session.getTransaction().commit();
						responseObject.put("responseCode", "filenotfound-200");
					}else {
						responseObject.put("responseCode", "filenotfound-404");
					}
				}
				
			} else if(packetType.equalsIgnoreCase("SaveFirmwareUrl")) {

				UpdateToDevice updateDevice = new UpdateToDevice();
				
				updateDevice.setDestPath(fileDetailObj.getString("destPath"));
				updateDevice.setFileName(fileDetailObj.getString("downloadUrl"));
				updateDevice.setGeneratedDate(new Date(System.currentTimeMillis()));
				updateDevice.setGeneratedTime(new Time(System.currentTimeMillis()));
//				updateDevice.setUpdatedDate(new Date(System.currentTimeMillis()));
//				updateDevice.setUpdatedTime(new Time(System.currentTimeMillis()));
				updateDevice.setStatus(fileDetailObj.getString("status"));
				updateDevice.setUpdateType(fileDetailObj.getString("firmwareType"));
				updateDevice.setTerminalID(fileDetailObj.getString("terminalId"));
				
				int storeInDbResp = (int) session.save(updateDevice);
//				System.out.println(storeInDbResp);
				if (storeInDbResp > 0) {
					responseObject.put("responseCode", "strsucc-200");
				} else {
					responseObject.put("responseCode", "strfail-400");
				}
				
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			writer.append(responseObject.toString());
			writer.flush();
			writer.close();
			session.close();
		}
		

		
	}
	
	private boolean writeFileToGivenPath(File file, String fileName, Part part) {
		
		boolean isSuccess = false;
		
		Path downloadingPath = Paths.get(file +File.separator+fileName);
		try (InputStream streamingContent = part.getInputStream()) {
			
			// replaces a file already present
			
			long val = Files.copy(streamingContent, downloadingPath, StandardCopyOption.REPLACE_EXISTING);
//			System.out.println(val);
			
			if (val > 0) {
				isSuccess = true;
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return isSuccess;
		
	}
	
	private boolean verifyFileSize(File file, String fileName, long fileSize) {
		boolean isFileSizeSame = false;
		
		try {
//			Path path = Paths.get(file+File.separator+fileName);
			File downloadedFile = new File(file+ File.separator + fileName);
			
//			System.out.println(downloadedFile);
			
//			System.out.println(downloadedFile.exists());
			
			long copiedFileSize = downloadedFile.getTotalSpace();
			
//			System.out.println(copiedFileSize);
			
//			System.out.println(downloadedFile.length() + "   ::::   "+fileSize);
			
			if (downloadedFile.length() == fileSize) {
				isFileSizeSame = true;
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return isFileSizeSame;
	}
	
	
	
	// get username
		
	// for extracting filename from the input stream type file
	
	private String extractFileName(Part part)  throws IOException {
		String contentDisp = part.getHeader("content-disposition");
		String[] items = contentDisp.split(";");
		for (String fileNameString : items) {
			if(fileNameString.trim().startsWith("filename")) {
				return fileNameString.substring(fileNameString.indexOf("=")+2, fileNameString.length() - 1);
			}
		}
		return null;
	}

}
