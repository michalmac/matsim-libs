/* *********************************************************************** *
 * project: org.matsim.*																															*
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
package playground.benjamin.income;

import org.matsim.core.api.population.Plan;
import org.matsim.core.config.groups.CharyparNagelScoringConfigGroup;
import org.matsim.core.scoring.CharyparNagelScoringParameters;
import org.matsim.core.scoring.ScoringFunction;
import org.matsim.core.scoring.ScoringFunctionAccumulator;
import org.matsim.core.scoring.ScoringFunctionFactory;
import org.matsim.core.scoring.charyparNagel.ActivityScoringFunction;
import org.matsim.core.scoring.charyparNagel.AgentStuckScoringFunction;
import org.matsim.core.scoring.charyparNagel.MoneyScoringFunction;


/**
 * @author dgrether
 *
 */
public class BKickIncomeScoringFunctionFactory implements ScoringFunctionFactory {

	private CharyparNagelScoringConfigGroup configGroup;
	private CharyparNagelScoringParameters params;

	public BKickIncomeScoringFunctionFactory(
			CharyparNagelScoringConfigGroup charyparNagelScoring) {
		this.configGroup = charyparNagelScoring;
		this.params = new CharyparNagelScoringParameters(configGroup);
	}

	/**
	 * @see org.matsim.core.scoring.ScoringFunctionFactory#getNewScoringFunction(org.matsim.core.api.population.Plan)
	 */
	public ScoringFunction getNewScoringFunction(Plan plan) {

		ScoringFunctionAccumulator scoringFunctionAccumulator = new ScoringFunctionAccumulator();

		scoringFunctionAccumulator.addScoringFunction(new ActivityScoringFunction(plan, params));

		scoringFunctionAccumulator.addScoringFunction(new BKickLegScoring(plan, params));

		scoringFunctionAccumulator.addScoringFunction(new MoneyScoringFunction(params));

		scoringFunctionAccumulator.addScoringFunction(new AgentStuckScoringFunction(params));

		return scoringFunctionAccumulator;
	
	}

}
