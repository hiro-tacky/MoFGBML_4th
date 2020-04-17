package fgbml.multilabel_ver3;

import data.MultiDataSetInfo;

public class MOP_HammingLoss extends Problem_MultiLabel{
	// ************************************************************


	// ************************************************************
	public MOP_HammingLoss(MultiDataSetInfo Dtra, MultiDataSetInfo Dtst) {
		super(Dtra, Dtst);

		this.objectiveNum = 2;
		this.optimizer = new int[] {MIN, MIN};

		doMemorizeMissPatterns = new boolean[] {true, false};
		setTrain(Dtra);
		setTest(Dtst);
	}

	// ************************************************************

	@Override
	public void evaluate(MultiPittsburgh individual) {
		int[][] classified = getClassified(traID, individual);

		double f1 = calcHammingLoss(traID, classified);
		double f2 = individual.getRuleSet().getRuleNum();

		double[] fitness = new double[] {f1, f2};
		individual.setFitness(fitness);
	}

	@Override
	public void evaluateParallel(MultiPittsburgh individual) {
		int[][] classified = getClassifiedParallel(traID, individual);

		double f1 = calcHammingLoss(traID, classified);
		double f2 = individual.getRuleSet().getRuleNum();

		double[] fitness = new double[] {f1, f2};
		individual.setFitness(fitness);
	}


}
