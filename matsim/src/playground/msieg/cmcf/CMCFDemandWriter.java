/* *********************************************************************** *
 * project: org.matsim.*
 * CMCFDemandWriter.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
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

package playground.msieg.cmcf;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.matsim.basic.v01.Id;
import org.matsim.gbl.Gbl;
import org.matsim.network.MatsimNetworkReader;
import org.matsim.network.NetworkLayer;
import org.matsim.network.Node;
import org.matsim.population.Act;
import org.matsim.population.Leg;
import org.matsim.population.MatsimPopulationReader;
import org.matsim.population.Plan;
import org.matsim.population.Population;
import org.matsim.population.PopulationReader;
import org.matsim.world.MatsimWorldReader;

import playground.msieg.structure.Commodities;
import playground.msieg.structure.Commodity;

public class CMCFDemandWriter implements PopulationReader {

	private final Population plans;
	private final PopulationReader popReader;
	//The input tag has to be specified before converting starts,
	//it must equal the network name, otherwise CMCF won't run.
	private String inputNetwork = "notspecified";
	private String plansPath;
	
//	public CMCFDemandWriter(){
//		this.plans = new Population(Population.NO_STREAMING);
//		this.popReader = new MatsimPopulationReader(this.plans);
//	}
	
	public CMCFDemandWriter(String configPath){
		this.plans = new Population(Population.NO_STREAMING);
		this.popReader = new MatsimPopulationReader(this.plans);
		if (Gbl.getConfig() == null) {
			Gbl.createConfig(new String[] { configPath, "config_v1.dtd" });
		}
		this.loadWorld();
		this.loadNetwork();
		this.plansPath = Gbl.getConfig().plans().getInputFile();
		this.setInputNetwork(Gbl.getConfig().network().getInputFile());
	}
	
	public CMCFDemandWriter(String configPath, String plansPath){
		this(configPath);
		this.plansPath = plansPath;
	}
	
	public void readFile(){
		System.out.println("\t<!--- trying to load file: "+plansPath+" -->");
		this.popReader.readFile(this.plansPath);
	}
	
	public void readFile(String filename) {
		this.popReader.readFile(filename);
	}
	

	public void setInputNetwork(String inputNetwork) {
		this.inputNetwork = inputNetwork;
	}
	
	/**
	 * load the world
	 *
	 */
	protected boolean loadWorld() {
		if (Gbl.getConfig().world().getInputFile() != null) {
			final MatsimWorldReader worldReader = new MatsimWorldReader(Gbl.getWorld());
			worldReader.readFile(Gbl.getConfig().world().getInputFile());
			return true;
		}
		else {
			System.out.println("No World input file given in config.xml!");
			return false;
		}
	}

	/**
	 * load the network
	 *
	 * @return the network layer
	 */
	protected NetworkLayer loadNetwork() {
		// - read network: which buildertype??
		NetworkLayer network = (NetworkLayer) Gbl.getWorld().createLayer(
				NetworkLayer.LAYER_TYPE, null);
		new MatsimNetworkReader(network).readFile(Gbl.getConfig().network().getInputFile());
		return network;
	}
	

	/**
	 * Converts plan file and prints out to console, equivalent to call convert(null)
	 */
	public void convert(){
		this.convert(null);
	}
	
	
	/**
	 * Converts the plans file into CMCF format and writes it to the specified Writer.
	 * If null is given, output is being printed out to the console.
	 * Make sure you have called read() before calling this function.
	 * @param out the Writer where to write the output, or null
	 */
	public void convert(Writer out){
		log("<demandgraph>\n", out);
		this.convertHeader(out, (byte) 1);
		this.convertDemands(out, (byte) 1);
		log("</demandgraph>", out);
	}
	
	/**
	 * Make something like:
	 *       <header>
	 *               <name>anyname</name>
 	 *               <date>1226498697127</date>
 	 *               <author>msieg</author>
 	 *               <creator>CMCFDemandWriter</creator>
 	 *               <input>sourcenetwork</input>
 	 *       </header>
 	 *       
 	 * Very important is the input tag, CMCF only accepts the demand file,
 	 * if the input tag equals the name of the network!
	 * @param out
	 * @param tabs
	 */
	private void convertHeader(Writer out, byte tabs){
		String tab="";
		while(tabs-- > 0)
			tab += '\t';
		log(tab+"<header>\n", out);
		log(tab+"\t<name>"+this.plans.getName()+"</name>\n", out);
		log(tab+"\t<date>"+System.currentTimeMillis()+"</date>\n", out);
		log(tab+"\t<creator>"+this.getClass().getSimpleName()+"</creator>\n", out);
		log(tab+"\t<input>"+this.inputNetwork+"</input>\n", out);
		log(tab+"</header>\n", out);
	}
	
	
	/**
	 * Make something like:
	 *          <demands>
     *            <commodity id="1">
     *                   <from>6</from>
     *                   <to>9</to>
     *                   <demand>400.0</demand>
     *           </commodity>
	 *			</demands>
	 * @param out
	 * @param tabs
	 */
	private void convertDemands(Writer out, byte tabs){
		String tab="";
		while(tabs-- > 0)
			tab += '\t';
		//in advance, read input data and store all the demands accumulated:
		Commodities<Node> com = new Commodities<Node>();

		Plan plan;
		Act act1, act2;
		Leg leg;
		for (Id id : this.plans.getPersons().keySet()) {
			//System.out.print('.');
			
			plan = this.plans.getPerson(id).getSelectedPlan();
			act1 = (Act) plan.getFirstActivity();
			leg = plan.getNextLeg(act1);
			act2 = plan.getNextActivity(leg);
			com.add( act1.getLink().getToNode(), act2.getLink().getFromNode(), 1);
		}
		
		//now write the output
		log(tab+"<demands>\n", out);
		int counter = 1;
		for(Commodity<Node> c: com){
			log(tab+"\t<commodity id=\""+(counter++)+"\">\n", out);
			log(tab+"\t\t<from>"+c.getOrigin().getOrigId()+"</from>\n", out);
			log(tab+"\t\t<to>"+c.getDestination().getOrigId()+"</to>\n", out);
			log(tab+"\t\t<demand>"+c.getDemand()+"</demand>\n", out);
			log(tab+"\t</commodity>\n", out);
		}
		log(tab+"</demands>\n", out);
	}
	
	private void log(String s, Writer out){
		if(out == null){
			System.out.print(s);
		}
		else{
			try {
				out.write(s);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		if(args.length==0){
			System.out.println("Usage: java CMCFDemandWriter config.xml [matsimPlansFile.xml] [outputFile] [input] \n" +
					"\t second argument is the plansFile to convert, if not given, then the plans file in the config is used." +
					"\t third argument is optional if not given, then output is written to console.\n" +
					"\t fourth argument is also optional, if not given then its set to 'unspecified'," +
					" this is the name of the network used, necessary for CMCF!");
			return;
		}
		CMCFDemandWriter cdw = null;
		if(args.length==1)
			cdw = new CMCFDemandWriter(args[0]);
		else 
			cdw = new CMCFDemandWriter(args[0], args[1]);
		Writer out=null;
		if(args.length>2){
			System.out.print(" Trying to access output file '"+args[2]+"' ... ");
			try {
				out = new FileWriter(args[2]);
				System.out.println(" [DONE]");
			} catch (IOException e) {
				e.printStackTrace();
				out = null;
				System.out.println(" Sorry, but access denied, writing output to console.");
			}
		}
		if(args.length < 4){
			System.out.println("<!-- WARNING: The input network has not been specified, probably CMCF won't accept the data. -->");
			cdw.setInputNetwork("unspecified");
		}
		else
			cdw.setInputNetwork(args[3]);
		//do it
		cdw.readFile();
		cdw.convert(out);
	}
	
}
