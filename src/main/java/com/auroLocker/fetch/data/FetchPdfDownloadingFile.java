package com.auroLocker.fetch.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.JSONObject;

import com.auro.hibernateUtilities.HibernateUtils;

import net.bytebuddy.implementation.bytecode.Throw;

/**
 * Servlet implementation class FetchPdfDownloadingFile
 */

@WebServlet("/FetchPdfDownloadingFile")
public class FetchPdfDownloadingFile extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String INVOICE_PATH = "invoices";

	//

	/**
	 * @see HttpServlet#HttpServlet()
	 */

	public FetchPdfDownloadingFile() {
		super();
		// TODO Auto-generated constructor stub
	}

	// for transferring the pdf file to react
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Method", "POST, GET, UPDATE, OPTIONS, DELETE");
		response.setCharacterEncoding("UTF-8");

//		String fileName = request.getParameter("filename");
		String fileName = request.getParameter("inv");
//		System.out.println(fileName);

		String pathDirect = fileName.substring(10, 14);

//		System.out.println(pathDirect);

//		File file = new File("E:\\"+fileName);

		String pathname = getServletContext().getRealPath("/")+".." + File.separator +INVOICE_PATH + File.separator + pathDirect
				+ File.separator + fileName;

//		System.out.println("inside here here here");

//		System.out.println("pathname here :-----------------=========" + pathname);

		File file = new File(pathname);

		if (file.exists()) {
			InputStream inputStream = new FileInputStream(file);
			OutputStream outStream = response.getOutputStream();
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=" + file);

			byte[] buffer = new byte[4096];
			int bytesRead;

			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}

			inputStream.close();
			outStream.flush();
		} else {
			try {
				throw new Exception(new FileNotFoundException());
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

	}

}
