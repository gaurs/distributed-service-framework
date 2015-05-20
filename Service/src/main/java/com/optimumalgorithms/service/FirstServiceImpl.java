package com.optimumalgorithms.service;

public class FirstServiceImpl implements FirstService {

	public String tempMethod(String inputParameter) {
		return "Remote Service Called by : " + inputParameter;
	}
}
