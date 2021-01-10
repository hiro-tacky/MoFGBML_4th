package fgbml.mofgbml;

import data.Input;
import data.SingleDataSetInfo;
import emo.algorithms.Algorithm;
import emo.algorithms.moead.MOEA_D;
import emo.algorithms.nsga2.NSGA2;
import fgbml.SinglePittsburgh;
import fgbml.problem.OutputClass;
import fuzzy.StaticFuzzyFunc;
import main.Consts;
import main.Experiment;
import main.Setting;
import method.MersenneTwisterFast;
import method.ResultMaster;
import output.result.Result_MoFGBML;
import time.TimeWatcher;

public class MoFGBML implements Experiment {

	public void startExperiment(String[] args,
								String traFile, String tstFile,
								MersenneTwisterFast rnd, ResultMaster resultMaster, Result_MoFGBML master) {
		/* ********************************************************* */
		//START:
		/* ********************************************************* */
		//Load Dataset
		SingleDataSetInfo Dtra = new SingleDataSetInfo();
		SingleDataSetInfo Dtst = new SingleDataSetInfo();
		Input.inputFile(Dtra, traFile);
		Input.inputFile(Dtst, tstFile);

		/* ********************************************************* */
		//Initialize Fuzzy Sets
		/********** 重要・必読 **********/
//		StaticFuzzyFunc.initFuzzy(Dtra);
		StaticFuzzyFunc.initfuzzy_multi(Dtra);

		/* ********************************************************* */
		//MOP No. を入力
		int mopNo;
		if(args.length < 7) {
			mopNo = 1;	//MOP1
		} else {
			mopNo = Integer.parseInt(args[6]);
		}

		/* ********************************************************* */
		//Generate Problem
		//mopを作成
		Problem_MoFGBML mop = getMOP(mopNo, Dtra, Dtst);

		/* ********************************************************* */
		//Generate Algorithm
		Algorithm<SinglePittsburgh> algorithm;

		/* ********************************************************* */
		//Generate OutputClass
		//このMoFGBMLに関しては使ってない，
		OutputClass<SinglePittsburgh> output = new Output_MoFGBML();

		/* ********************************************************* */
		//Generate Individual Instance
		SinglePittsburgh instance = new SinglePittsburgh();

		/* ********************************************************* */
		//Timer start
		TimeWatcher timeWatcher = new TimeWatcher();	//All Exprimeint executing time
		TimeWatcher evaWatcher = new TimeWatcher();		//Evaluating time
		timeWatcher.start();

		/* ********************************************************* */
		/* ********************************************************* */
		//GA Start
		if(Setting.emoType == Consts.NSGA2) {
			algorithm = new NSGA2<SinglePittsburgh>();
			algorithm.main( mop, output, instance,
							resultMaster, rnd,
							timeWatcher, evaWatcher, master);
		}
		else if(Setting.emoType == Consts.WS ||
				Setting.emoType == Consts.TCHEBY ||
				Setting.emoType == Consts.PBI ||
				Setting.emoType == Consts.AOF) {
			algorithm = new MOEA_D<SinglePittsburgh>();
			algorithm.main( mop, output, instance,
							resultMaster, rnd,
							timeWatcher, evaWatcher, master);
		}
		/* ********************************************************* */
		/* ********************************************************* */

		//GA End
		timeWatcher.stop();
		resultMaster.addTimes( timeWatcher.getSec() );
		resultMaster.addEvaTimes( evaWatcher.getSec() );

		System.out.println();
	}


	/**
	 * Get MOPx Class<br>
	 * @param mopNo : int : MOPx
	 * @param Dtra
	 * @param Dtst
	 * @return
	 */
	public Problem_MoFGBML getMOP(int mopNo, SingleDataSetInfo Dtra, SingleDataSetInfo Dtst) {
		Problem_MoFGBML mop = null;
		switch(mopNo) {
		case 1:
			mop = new MOP1(Dtra, Dtst);
			break;
		}
		return mop;
	}
}
