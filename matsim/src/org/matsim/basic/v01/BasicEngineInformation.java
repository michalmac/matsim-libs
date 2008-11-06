/* *********************************************************************** *
 * project: org.matsim.*
 * KmlNetworkWriter.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2007 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */
package org.matsim.basic.v01;


/**
 * @author dgrether
 *
 */
public interface BasicEngineInformation {

	public enum FuelType {diesel, gasoline, electricity, biodiesel};
	
	public FuelType getFuelType();
	
	public double getGasConsumption();
	
	public void setFuelType(FuelType fueltype);
	
	public void setGasConsumption(double literPerMeter);
	
}
