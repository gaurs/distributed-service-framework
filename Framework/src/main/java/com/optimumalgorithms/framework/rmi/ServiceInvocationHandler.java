package com.optimumalgorithms.framework.rmi;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.optimumalgorithms.framework.util.ClassBindings;
import com.optimumalgorithms.framework.util.ConfigReader;
import com.optimumalgorithms.framework.util.ServiceConstants;

/**
 * Service Invocation Handler that is called when any method is invoked on the
 * proxies
 * 
 * @author gaurs
 * 
 */
public class ServiceInvocationHandler implements InvocationHandler {

	private static final String standardImpl = "Impl";
	public String callType = null;

	/**
	 * The ServiceInvocationhandler class is responsible for handling the
	 * service requests and invoking the methopds on it. Whenever a method is
	 * invoked on a proxy object the invoke method of the corresponding
	 * ServiceInvocationhandler is called. The callType is used to identify the
	 * type of call (client or local). <br/>
	 * <br/>
	 * If the call is a client call it will map the call to a URL using the
	 * config.properties and bindings.xml files. Make sure the corresponding
	 * entries are present in these two files
	 * 
	 * @param callType
	 */
	public ServiceInvocationHandler(String callType) {
		this.callType = callType;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		// ge5t the parent class for which the proxy was created
		Class<? extends Object>[] interfaceNames = ((Proxy) proxy).getClass()
				.getInterfaces();

		Class<? extends Object> tempClass = null;
		Object returnValue = null;

		// Get the corrsponding interfrace name
		String interfaceName = interfaceNames[0].getCanonicalName();

		// Call to get the corresponding implementor class of the interface
		String implementingClass = getImplementingClassName(interfaceName);

		// if the entry is missing try to initiate the class locally
		if (null == implementingClass) {
			implementingClass = implementingClass + standardImpl;
			implementingClass = (tempClass = Class.forName(implementingClass))
					.getCanonicalName();
		}

		// If the call was a local call. Invoke the method locally
		if (ServiceConstants.Local.equalsIgnoreCase(callType)) {
			if (null == tempClass) {
				tempClass = Class.forName(implementingClass);
			}

			returnValue = method.invoke(tempClass.newInstance(), args);
		}

		else {

			// If the call was client call

			// get the tier on which the service is hosted. Make sure the entry
			// exist in the corresponding config.properties file of that project
			String tier = ConfigReader.getInstance().getStringValue(
					implementingClass);

			// Prepare the full URL string having all the input parameters
			tier = appendClassNameAndMethodName(tier, interfaceName,
					method.getName());

			// Instantiates the URL
			URL url = new URL(tier);

			// get the connection
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.setAllowUserInteraction(true);
			urlConnection.setRequestMethod("POST");
			urlConnection.setRequestProperty("Content-Type",
					"multipart/form-data");

			// get the outputStream
			OutputStream outPutStream = new BufferedOutputStream(
					urlConnection.getOutputStream());

			// Write the input parameters for the method call to the stream
			// (Obviously data should be Serializable)
			outPutStream.write(populateArgsData(args));
			outPutStream.flush();

			// Wait for the Remote machine to write the data back to the server
			// URL
			ObjectInputStream inputStream = new ObjectInputStream(
					urlConnection.getInputStream());

			// fetch the resultant data from the inputStream
			ObjectData objectData = (ObjectData) inputStream.readObject();
			Map<String, Object> dataMap = objectData.getDataMap();

			// Close the streams
			outPutStream.close();
			inputStream.close();

			// fetch the required values from the map
			returnValue = dataMap.get(ServiceConstants.ParamValues);
			returnValue = ((Object[]) returnValue)[0];

		}

		return returnValue;
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

	/**
	 * Appends the classname and the method name in the URL
	 * 
	 * @param tier
	 * @param interfaceName
	 * @param methodName
	 * @return
	 */
	private String appendClassNameAndMethodName(String tier,
			String interfaceName, String methodName) {

		return tier + "?interfaceName=" + interfaceName + "&methodName="
				+ methodName + "&callType=" + callType;
	}

	/**
	 * This method is used to get the implementing class of the interface name
	 * passed as input parameter. This method will use the ClassBindings class
	 * to fetch the corresponding binding class. Make sure the entry exist in
	 * the bindings.xml file (not bindings.xsd)
	 * 
	 * @param interfaceName
	 * @param
	 * @return
	 */
	private String getImplementingClassName(String interfaceName)
			throws Exception {
		return ClassBindings.getInstance().getImplementingClass(interfaceName,
				callType);
	}

}
