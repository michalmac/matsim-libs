/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2016 by the members listed in the COPYING,        *
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

package org.matsim.contrib.drt.run;

import org.matsim.contrib.dvrp.run.ConfigConsistencyCheckers;
import org.matsim.core.config.Config;
import org.matsim.core.config.consistency.ConfigConsistencyChecker;

public class DrtConfigConsistencyChecker implements ConfigConsistencyChecker {
	@Override
	public void checkConsistency(Config config) {
		if (!ConfigConsistencyCheckers.isEitherSingleOrMultiModeDeclared(DrtConfigGroup.get(config),
				MultiModeDrtConfigGroup.get(config))) {
			throw new RuntimeException(
					"Either DrtConfigGroup or MultiModeDrtConfigGroup must be defined at the config top level");
		}
	}
}
