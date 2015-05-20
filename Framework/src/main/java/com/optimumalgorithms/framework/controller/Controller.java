package com.optimumalgorithms.framework.controller;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.optimumalgorithms.framework.rmi.ObjectData;
import com.optimumalgorithms.framework.util.ClassBindings;
import com.optimumalgorithms.framework.util.ConfigReader;
import com.optimumalgorithms.framework.util.ServiceConstants;

/**
 * The main entry point of the Service Calls as defined in web.xml of every
 * deployable project. This project will go as a jar to every deployable project
 * so this class will act as a single point of entry for all the projects which
 * needs to be deployed on different servers. Make sure the corresponding entry is
 * present in the web.xml of all such projects
 * 
 * @author sgaur
 * 
 */
public class Controller extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {

			// Retrieve the Interface name
			String interfaceName = req.getParameter("interfaceName");

			// Retrieve the Method Name
			String methodName = req.getParameter("methodName");

			// Retrieve the call type
			String callType = req.getParameter("callType");

			// Set the path to context folder
			if (!ConfigReader.getInstance().isPathSet()
					|| !ClassBindings.getInstance().isPathSet()) {

				// Get the path to config folder
				String pathToConfigFolder = getServletContext()
						.getInitParameter("contextPath");
				ConfigReader.getInstance().setPathToPropertiesFile(
						pathToConfigFolder);
				ClassBindings.getInstance().setPathToBindingsFile(
						pathToConfigFolder);
			}

			// Create a local instance as it will land here only in case client
			// call was made from some other tier
			String className = ClassBindings
					.getInstance()
					.getImplementingClass(interfaceName, ServiceConstants.Local);

			// get the class
			Class implementorClass = Class.forName(className);

			// Opens up the connection on the URL
			ObjectInputStream inputStream = new ObjectInputStream(
					req.getInputStream());

			//Read t6he input data from the stream
			ObjectData objectData = (ObjectData) inputStream.readObject();

			Map<String, Object> dataMap = objectData.getDataMap();

			//Get the remote method to be invoked
			Method method = implementorClass.getMethod(methodName,
					(Class[]) dataMap.get(ServiceConstants.ParamTypes));

			//Invoke the method passing all the parameter values
			Object returnValue = method.invoke(implementorClass.newInstance(),
					(Object[]) dataMap.get(ServiceConstants.ParamValues));

			//Open the output stream on the server
			OutputStream outPutStream = new BufferedOutputStream(
					resp.getOutputStream());

			//write the resultant data on it to be read by the client
			outPutStream.write(populateArgsData(new Object[] { returnValue }));
			outPutStream.flush();

			//Close the streams
			inputStream.close();
			outPutStream.close();

		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	/**
	 * Will populates the argument Data so as to make it possible to be written
	 * on the url. This method use serialization to make the data capable of
	 * being written on the server ports
	 * 
	 * @param args
	 * @return
	 * @throws Exception
	 */
	private byte[] populateArgsData(Object[] args) throws Exception {

		Map<String, Object> objectData = new HashMap<String, Object>();
		Class[] classTypes = new Class[args.length];
		int index = 0;

		for (Object currentParameter : args) {
			Class<? extends Object> parameterType = currentParameter.getClass();
			classTypes[index++] = parameterType;
		}

		objectData.put(ServiceConstants.ParamTypes, classTypes);
		objectData.put(ServiceConstants.ParamValues, args);

		ObjectData data = new ObjectData(objectData);

		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(byteOut);
		out.writeObject(data);

		return byteOut.toByteArray();

	}
}
