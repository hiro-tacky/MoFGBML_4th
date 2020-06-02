package main;

import method.MersenneTwisterFast;
import method.ResultMaster;
import output.result.Result_MoFGBML;

public interface Experiment {

	public void startExperiment(String[] args, String traFile, String tstFile,
								MersenneTwisterFast rnd, ResultMaster resultMaster, Result_MoFGBML master);
}
