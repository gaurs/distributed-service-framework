package com.optimumalgorithms.framework.core;

import java.lang.reflect.Proxy;

import com.optimumalgorithms.framework.rmi.ServiceInvocationHandler;

/**
 * The service factory class that provides instances of Service Classes. It will
 * create the proxy object of the service classes and will invoke the methods on
 * it.
 * 
 * @author gaurs
 * 
 */
public class ServiceFactory {

	/**
	 * The public method to get the instance of the service class. If the class
	 * is to be invoked at a remote machine then the callType should be made as
	 * Client else if the class is to be invoked at a local machine then the
	 * callType should be Local. Take reference from ServiceConstants. It will
	 * receieve the interface name whose method we need to call as input
	 * parameter and the corresponding callType.
	 * 
	 * @param interfaceObject
	 * @param callType
	 * @return
	 * @throws Exception
	 */
	public static Object getInstance(Class<? extends Object> interfaceObject,
			String callType) throws Exception {

		Proxy proxyObject = (Proxy) Proxy.newProxyInstance(interfaceObject
				.getClassLoader(), new Class[] { interfaceObject },
				new ServiceInvocationHandler(callType));

		return proxyObject;
	}
}