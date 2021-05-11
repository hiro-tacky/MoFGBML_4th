package emo.algorithms;

import fgbml.Pittsburgh;
import fgbml.problem.FGBML;
import method.MersenneTwisterFast;
import output.result.Result_MoFGBML;
import time.TimeWatcher;

@SuppressWarnings("rawtypes")
public abstract class Algorithm<T extends Pittsburgh> {
	// ************************************************************


	// ************************************************************


	// ************************************************************

	public abstract void main(	FGBML mop, /*OutputClass output,*/ T instance, MersenneTwisterFast rnd,
								TimeWatcher timeWatcher, TimeWatcher evaWatcher, Result_MoFGBML master);

}
