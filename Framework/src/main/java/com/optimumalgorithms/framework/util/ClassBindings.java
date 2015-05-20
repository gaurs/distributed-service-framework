package com.optimumalgorithms.framework.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Returns the class Bindings of t6he interfaces with the corresponding class.
 * It implements the Singleton Design Pattern as it is tier specific.
 * 
 * @author gaurs
 */
public class ClassBindings {

	private static final ClassBindings INSTANCE = new ClassBindings();
	private String pathToMappingFile = null;
	private String defaultPath = "config/";
	private boolean isPathSet = false;

	private ClassBindings() {
		init();
	}

	public static ClassBindings getInstance() {
		return INSTANCE;
	}

	private void init() {
		if (null == pathToMappingFile) {
			pathToMappingFile = defaultPath;
		}
	}

	/**
	 * Returns the mapping classname corresponding to the interface name passed
	 * 
	 * @param interfaceName
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public String getImplementingClass(String interfaceName, String type)
			throws Exception {

		String implementingClass = null;

		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		InputStream inputStream = classLoader
				.getResourceAsStream("bindings.xml");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = null;

		// For Client
		if (null == inputStream) {
			File configFile = new File(pathToMappingFile + "bindings.xml");
			if (!configFile.exists()) {
				throw new FileNotFoundException(
						"Class Bindings file (bindings.xml) not found. Kindly place it in the classPath");
			}

			doc = builder.parse(configFile);
		} else {
			// For Server
			doc = builder.parse(inputStream);
		}

		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = xpath.compile("//bindings[@name=\"Service\"]");

		NodeList nodeList = (NodeList) expr.evaluate(doc,
				XPathConstants.NODESET);

		for (int index = 0; index < nodeList.getLength(); index++) {
			Node currentNode = nodeList.item(index);

			if (currentNode.hasChildNodes()) {
				NodeList childNodes = currentNode.getChildNodes();
				for (int subIndex = 0; subIndex < childNodes.getLength(); subIndex++) {

					if (childNodes.item(subIndex) instanceof Element) {
						Element element = (Element) childNodes.item(subIndex);

						if (!(interfaceName.equalsIgnoreCase(element
								.getAttribute("interfaceName")) && type
								.equalsIgnoreCase(element.getAttribute("type")))) {
							continue;
						}

						implementingClass = element.getAttribute("value");
						break;
					}
				}

			}

		}

		return implementingClass;
	}

	public String getPathToBindingsFile() {
		return pathToMappingFile;
	}

	public void setPathToBindingsFile(String pathToMappingFile) {
		this.pathToMappingFile = pathToMappingFile;
		this.isPathSet = true;
	}

	public boolean isPathSet() {
		return isPathSet;
	}

}
