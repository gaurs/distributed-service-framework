package com.optimumalgorithms.client;

import com.optimumalgorithms.framework.core.ServiceFactory;
import com.optimumalgorithms.framework.util.ServiceConstants;
import com.optimumalgorithms.service.FirstService;

/**
 * The main class to call a remote Service
 * 
 * @author sgaur
 * 
 */
public class ServiceCaller {

	/**
	 * Main method that will mark the entry point to the application
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		FirstService service = (FirstService) ServiceFactory.getInstance(
				FirstService.class, ServiceConstants.Client);

		String val = service.tempMethod("Gaurs");
		System.out.println(val);

	}

}
