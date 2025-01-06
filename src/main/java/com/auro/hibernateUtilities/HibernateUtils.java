package com.auro.hibernateUtilities;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.auro.beans.*;
import com.locker.operations.UserOperationHandler;

import net.bytebuddy.implementation.bind.annotation.Super;

/**
 * @author Praveen
 */

public class HibernateUtils {
   
	private static SessionFactory SessionFactory = null;
//	static SessionFactory factory=null;
	
//	 public static void loadSessionFactoryAnnotedClasses() {
//		Configuration cfg=new Configuration();
//		
//		    cfg.configure("hibernate.cfg.xml");
//		    cfg.addAnnotatedClass(User.class);
//		    cfg.addAnnotatedClass(CustomerDetails.class);
//		    cfg.addAnnotatedClass(SiteRegistration.class);
//		    cfg.addAnnotatedClass(LockCategory.class);
//		    cfg.addAnnotatedClass(TransactionDetails.class);
//		    cfg.addAnnotatedClass(LogDetails.class);
//		    cfg.addAnnotatedClass(TransactionHistory.class);
//		    cfg.addAnnotatedClass(ManualOverride.class);
//		    cfg.addAnnotatedClass(TerminalHealthPacket.class);
//		    cfg.addAnnotatedClass(TerminalLockMapping.class);
//		    cfg.addAnnotatedClass(TerminalLockStatusDetail.class);
//		    cfg.addAnnotatedClass(ReleaseLocker.class);
//		    cfg.addAnnotatedClass(RazorpayAmountRefund.class);
//		    cfg.addAnnotatedClass(TermwiseLockermalfunction.class);
//		    cfg.addAnnotatedClass(paygatorderid_details.class);
//		    cfg.addAnnotatedClass(TransactionFailedDetails.class);
//		    cfg.addAnnotatedClass(PartialRetrieveData.class);
//		    cfg.addAnnotatedClass(UserOperationHandler.class);
//		    cfg.addAnnotatedClass(UpdateToDevice.class);
//		    cfg.addAnnotatedClass(gstDetails.class);
//		    factory=cfg.buildSessionFactory();
////		    return SessionFactory;
//		
//	}
	
	static {
		Configuration cfg=new Configuration();
		
		    cfg.configure("hibernate.cfg.xml");
		    cfg.addAnnotatedClass(User.class);
		    cfg.addAnnotatedClass(CustomerDetails.class);
		    cfg.addAnnotatedClass(SiteRegistration.class);
		    cfg.addAnnotatedClass(LockCategory.class);
		    cfg.addAnnotatedClass(TransactionDetails.class);
		    cfg.addAnnotatedClass(LogDetails.class);
		    cfg.addAnnotatedClass(TransactionHistory.class);
		    cfg.addAnnotatedClass(ManualOverride.class);
		    cfg.addAnnotatedClass(TerminalHealthPacket.class);
		    cfg.addAnnotatedClass(TerminalLockMapping.class);
		    cfg.addAnnotatedClass(TerminalLockStatusDetail.class);
		    cfg.addAnnotatedClass(ReleaseLocker.class);
		    cfg.addAnnotatedClass(RazorpayAmountRefund.class);
		    cfg.addAnnotatedClass(TermwiseLockermalfunction.class);
		    cfg.addAnnotatedClass(paygatorderid_details.class);
		    cfg.addAnnotatedClass(TransactionFailedDetails.class);
		    cfg.addAnnotatedClass(PartialRetrieveData.class);
		    cfg.addAnnotatedClass(UserOperationHandler.class);
		    cfg.addAnnotatedClass(UpdateToDevice.class);
		    cfg.addAnnotatedClass(gstDetails.class);
		    cfg.addAnnotatedClass(invoiceDetails.class);
		    cfg.addAnnotatedClass(amountreceiveddetails.class);
//		    factory=cfg.buildSessionFactory();
		    SessionFactory = cfg.buildSessionFactory();
//		    return SessionFactory;
		
	}
	 
	public static SessionFactory getSessionFactory() {
		
//		loadSessionFactoryAnnotedClasses();
		return SessionFactory;
	}
	
	  
	public static Session getSession() {
		
//		if (factory == null) {
////			super();
//			
//			return getSessionFactory().openSession();
//		} else {
//			return factory.openSession();
//		}
		
		return SessionFactory.openSession();
		
		
	}
	
}
