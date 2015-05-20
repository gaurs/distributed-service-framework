package com.optimumalgorithms.framework.rmi;

import java.io.Serializable;
import java.util.Map;

/**
 * The main carrier of the data between Server and Client calls. The class
 * implements Serializable so as to make it possible to write the data between
 * network calls
 * 
 * @author gaurs
 * 
 */
public class ObjectData implements Serializable {

	private static final long serialVersionUID = 1L;
	private Map<String, Object> dataMap = null;

	public Map<String, Object> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, Object> dataMap) {
		this.dataMap = dataMap;
	}

	public ObjectData(Map<String, Object> dataMap) {
		super();
		this.dataMap = dataMap;
	}

}
