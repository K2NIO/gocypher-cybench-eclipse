/*
 * Copyright (C) 2020, K2N.IO.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package com.gocypher.cybench.plugin.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportUIModel {

	private Map<String,NameValueEntry>baseProperties ;
	
	private List<NameValueEntry> listOfBenchmarks ;
	
	private Map<String,List<NameValueEntry>> benchmarksAttributes ;
	
	private List<NameValueEntry> listOfHwProperties ;
	private List<NameValueEntry> listOfJVMProperties ;
	private List<NameValueEntry> listOfOverviewProperties ;
	
	
	public ReportUIModel() {
		this.baseProperties = new HashMap<>() ;
		this.listOfBenchmarks = new ArrayList<>() ;
		this.benchmarksAttributes = new HashMap<>() ;
		this.listOfHwProperties = new ArrayList<>() ;
		this.listOfJVMProperties = new ArrayList<>() ;
		this.listOfOverviewProperties = new ArrayList<>() ;
		
		
	}
	
	public void addBaseProperty (String name, String value) {
		this.baseProperties.put(name, new NameValueEntry(name,value)) ;
	}
	public void addToListOfOverview (String name, String value) {
		this.listOfOverviewProperties.add (new NameValueEntry(name,value)) ;
	}
	
	public void addToListOfBenchmarks (String name, String value) {
		this.listOfBenchmarks.add (new NameValueEntry(name,value)) ;
	}
	
	public void addToBenchmarksAttributes (String benchmarkName,String attributeName, String attributeValue) {
		if (this.benchmarksAttributes.get(benchmarkName) == null) {
			this.benchmarksAttributes.put(benchmarkName, new ArrayList<>()) ;			
		}
		this.benchmarksAttributes.get(benchmarkName).add( new NameValueEntry(attributeName,attributeValue)) ;
	}
	public String getReportTitle () {
		String title = "Report-" ;
		if (this.baseProperties.get("benchReportName") != null && this.baseProperties.get("benchReportName").hasValue()) {
			title = this.baseProperties.get("benchReportName").getValue() + "-" ;
		}
		if (this.baseProperties.get("totalScore") != null && this.baseProperties.get("totalScore").hasValue()) {
			title += this.baseProperties.get("totalScore").getValue() ;
		}
		
		if (this.baseProperties.get("timestamp") != null && this.baseProperties.get("timestamp").hasValue()) {
			title += " ("+this.baseProperties.get("timestamp").getValue()+")" ;
		}
		return title ;
		
	}
	public String getReportExternalUrl () {
		if (this.baseProperties.get("reportURL") != null) {
			return this.baseProperties.get("reportURL").getValue() ;
		}
		return null ;
	}
	public Map<String, NameValueEntry> getBaseProperties() {
		return baseProperties;
	}

	public void setBaseProperties(Map<String, NameValueEntry> baseProperties) {
		this.baseProperties = baseProperties;
	}

	public List<NameValueEntry> getListOfBenchmarks() {
		return listOfBenchmarks;
	}

	public void setListOfBenchmarks(List<NameValueEntry> listOfBenchmarks) {
		this.listOfBenchmarks = listOfBenchmarks;
	}

	public Map<String, List<NameValueEntry>> getBenchmarksAttributes() {
		return benchmarksAttributes;
	}

	public void setBenchmarksAttributes(Map<String, List<NameValueEntry>> benchmarksAttributes) {
		this.benchmarksAttributes = benchmarksAttributes;
	}

	public List<NameValueEntry> getListOfHwProperties() {
		return listOfHwProperties;
	}

	public void setListOfHwProperties(List<NameValueEntry> listOfHwProperties) {
		this.listOfHwProperties = listOfHwProperties;
	}

	public List<NameValueEntry> getListOfJVMProperties() {
		return listOfJVMProperties;
	}

	public void setListOfJVMProperties(List<NameValueEntry> listOfJVMProperties) {
		this.listOfJVMProperties = listOfJVMProperties;
	}

	public List<NameValueEntry> getListOfOverviewProperties() {
		return listOfOverviewProperties;
	}

	public void setListOfOverviewProperties(List<NameValueEntry> listOfOverviewProperties) {
		this.listOfOverviewProperties = listOfOverviewProperties;
	}
	
		

}
