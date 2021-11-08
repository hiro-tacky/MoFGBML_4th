package main;

import method.MersenneTwisterFast;
import xml.result.Result_MoFGBML;

public interface Experiment {

	public void startExperiment(String[] args, String traFile, String tstFile,
								MersenneTwisterFast rnd, Result_MoFGBML master);
}
