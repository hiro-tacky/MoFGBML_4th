package fgbml.subdivision_ver2;

import data.SingleDataSetInfo;
import fgbml.SinglePittsburgh;

public class SubMOP4 extends Problem_Subdivision{
	// ************************************************************


	// ************************************************************
	public SubMOP4(SingleDataSetInfo Dtra, SingleDataSetInfo Dtst, SingleDataSetInfo Dsubtra, SingleDataSetInfo Dvalid) {
		super(Dtra, Dtst, Dsubtra, Dvalid);

		this.objectiveNum = 2;

		this.optimizer = new int[] {MIN, MIN};

		doMemorizeMissPatterns = new boolean[] {false, false, true, false};
		setTrain(Dsubtra);
		setTest(Dtst);
	}

	// ************************************************************

	@Override
	public void evaluate(SinglePittsburgh individual) {
		subValidEvaluate(individual);
		double f1 = individual.getAppendix(validID);
		double f2 = individual.getRuleSet().getRuleNum();
		double[] fitness = new double[] {f1, f2};

		individual.setFitness(fitness);
	}

	@Override
	public void evaluateParallel(SinglePittsburgh individual) {
		subValidEvaluateParallel(individual);
		double f1 = individual.getAppendix(validID);
		double f2 = individual.getRuleSet().getRuleNum();
		double[] fitness = new double[] {f1, f2};

		individual.setFitness(fitness);
	}


}
