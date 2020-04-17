package fgbml.multilabel_ver3;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import data.MultiDataSetInfo;
import data.MultiPattern;
import fgbml.problem.FGBML;
import ga.Population;
import main.Setting;
import method.StaticFunction;

public abstract class Problem_MultiLabel extends FGBML<MultiPittsburgh>{

	// ************************************************************
	final int traID = 0;
	final int tstID = 1;

	MultiDataSetInfo Dtra;
	MultiDataSetInfo Dtst;

	/** 0:Dtra, 1:Dtst */
	boolean[] doMemorizeMissPatterns = new boolean[] {true, false};

	// ************************************************************
	public Problem_MultiLabel(MultiDataSetInfo Dtra, MultiDataSetInfo Dtst) {
		appendixNum = 6;

		this.Dtra = Dtra;
		this.Dtst = Dtst;
	}

	// ************************************************************

	public MultiDataSetInfo getDataSet(int dataID) {
		switch(dataID) {
			case traID:
				return Dtra;
			case tstID:
				return Dtst;
			default:
				return Dtra;
		}
	}

	/**
	 * <h1>Assignment each appendix information for individuals in population</h1>
	 * 	0:Exact-Match(Dtra), 1:F-measure(Dtra), 2:Hamming Loss(Dtra),<br>
	 *  3:Exact-Match(Dtst), 4:F-measure(Dtst), 5:Hamming Loss(Dtst)<br>
	 *	<br>
	 * @param population : {@literal Population<MultiPittsburgh>}
	 */
	@Override
	public void setAppendix(Population<MultiPittsburgh> population) {
		/** 0:Exact-Match(Dtra), 1:F-measure(Dtra), 2:Hamming Loss(Dtra),<br>
		 *  3:Exact-Match(Dtst), 4:F-measure(Dtst), 5:Hamming Loss(Dtst) */
		int appendixNum = 6;

		try {
			Setting.forkJoinPool.submit( () ->
				population.getIndividuals().parallelStream()
				.forEach( individual -> {
					double[] appendix = new double[appendixNum];

					//Dtra
					int[][] classified = getClassified(traID, individual);
					//Exact-Match
					appendix[0] = calcExactMatchError(traID, classified);
					//F-measure
					appendix[1] = calcFmeasure(traID, classified);
					//Hamming Loss
					appendix[2] = calcHammingLoss(traID, classified);

					//Dtst
					classified = getClassified(tstID, individual);
					//Exact-Match
					appendix[3] = calcExactMatchError(tstID, classified);
					//F-measure
					appendix[4] = calcFmeasure(tstID, classified);
					//Hamming Loss
					appendix[5] = calcHammingLoss(tstID, classified);

					individual.setAppendix(appendix);
				} )
			).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param dataID : int : 0:Dtra, 1:Dtst
	 * @param individual : MultiPittsburgh
	 * @param isEvaluation : boolean
	 * @return int[][] : [dataSize][Lnum]
	 */
	public int[][] getClassified(int dataID, MultiPittsburgh individual){
		MultiDataSetInfo dataset = getDataSet(dataID);
		int dataSize = dataset.getDataSize();
		int Lnum = dataset.getCnum();

		int[][] classified = new int[dataSize][Lnum];

		if(doMemorizeMissPatterns[dataID]) {
			individual.getRuleSet().clearMissPatterns();
			//Crear Fitness of Michigan rule
			for(int rule = 0; rule < individual.getRuleSet().getMicRules().size(); rule++) {
				individual.getRuleSet().getMicRule(rule).clearFitness();
			}
		}

		for(int p = 0; p < dataSize; p++) {
			classified[p] = individual.getRuleSet().classify(dataset.getPattern(p), doMemorizeMissPatterns[dataID]);
		}

		if(doMemorizeMissPatterns[dataID]) {
			individual.getRuleSet().removeRuleByFitness();
			individual.getRuleSet().calcRuleLength();
			individual.ruleset2michigan();
			individual.michigan2pittsburgh();
		}

		return classified;
	}

	/**
	 * <h1>for MOEA/D</h1>
	 * @param dataID : int : 0:Dtra, 1:Dtst
	 * @param individual : MultiPittsburgh
	 * @return int[][] : [dataSize][Lnum]
	 */
	public int[][] getClassifiedParallel(int dataID, MultiPittsburgh individual){
		MultiDataSetInfo dataset = getDataSet(dataID);
		int dataSize = dataset.getDataSize();
		int Lnum = dataset.getCnum();

		int[][] classified = new int[dataSize][Lnum];

		if(doMemorizeMissPatterns[dataID]) {
			individual.getRuleSet().clearMissPatterns();
			//Crear Fitness of Michigan rule
			for(int rule = 0; rule < individual.getRuleSet().getMicRules().size(); rule++) {
				individual.getRuleSet().getMicRule(rule).clearFitness();
			}
		}

		for(int p = 0; p < dataSize; p++) {
			classified[p] = individual.getRuleSet().classifyParallel(dataset.getPattern(p), doMemorizeMissPatterns[dataID]);
		}

		if(doMemorizeMissPatterns[dataID]) {
			individual.getRuleSet().removeRuleByFitness();
			individual.getRuleSet().calcRuleLength();
			individual.ruleset2michigan();
			individual.michigan2pittsburgh();
		}

		return classified;
	}

	/**
	 *
	 * @param dataID : int : 0:Dtra, 1:Dtst
	 * @param individual : MultiPittsburgh
	 * @return double : Hamming Loss [%]
	 */
	public double calcHammingLoss(int dataID, int[][] classified) {
		MultiDataSetInfo dataset = getDataSet(dataID);

		double loss = 0.0;
		int Lnum = dataset.getCnum();
		int dataSize = dataset.getDataSize();

		for(int p = 0; p < dataSize; p++) {
			double distance = StaticFunction.HammingDistance(classified[p], dataset.getPattern(p).getConClass());
			loss += distance / (double)Lnum;
		}

		loss = loss/(double)dataSize;
		return loss * 100.0;
	}

	/**
	 *
	 * @param dataID : int : 0:Dtra, 1:Dtst
	 * @param classified : int[][] : [dataSize][Lnum]
	 * @return double : F-measure [%]
	 */
	public double calcFmeasure(int dataID, int[][] classified) {
		MultiDataSetInfo dataset = getDataSet(dataID);

		int dataSize = dataset.getDataSize();

		double Fmeasure = 0.0;
		for(int p = 0; p < dataSize; p++) {
			MultiPattern pattern = dataset.getPattern(p);
			double precision = StaticFunction.PrecisionMetric(classified[p], pattern.getConClass());
			double recall = StaticFunction.RecallMetric(classified[p], pattern.getConClass());

			double f;
			if((precision + recall) == 0) {
				f = 0;
			}
			else {
				f = (2.0 * recall * precision) / (recall + precision);
			}

			Fmeasure += f;
		}

		Fmeasure = Fmeasure / (double)dataSize;

		return Fmeasure * 100.0;
	}

	/**
	 *
	 * @param dataID : int : 0:Dtra, 1:Dtst
	 * @param classified : int[][] : [dataSize][Lnum]
	 * @return double : 完全一致 誤識別率[%]
	 */
	public double calcExactMatchError(int dataID, int[][] classified) {
		MultiDataSetInfo dataset = getDataSet(dataID);

		double exactMatchNum = 0;	//if classification is exact-match, then this count increments.
		double errorRate = 0.0;

		int dataSize = dataset.getDataSize();
		for(int p = 0; p < dataSize; p++) {
			MultiPattern pattern = dataset.getPattern(p);
			if(Arrays.equals(classified[p], pattern.getConClass())) {
				exactMatchNum++;
			}
		}

		//range [0, 1]
		errorRate = ((double)dataSize - exactMatchNum) / (double)dataSize;

		return errorRate * 100.0;
	}


	/**
	 *
	 * @param dataID : int : 0:Dtra, 1:Dtst
	 * @param individual : MultiPittsburgh
	 * @return double : Hamming Loss
	 */
	@Deprecated
	public double calcHammingLoss(int dataID, MultiPittsburgh individual) {
		MultiDataSetInfo dataset = getDataSet(dataID);

		double loss = 0.0;
		final int Lnum = dataset.getCnum();
		int dataSize = dataset.getDataSize();

		if(doMemorizeMissPatterns[dataID]) {
			individual.getRuleSet().clearMissPatterns();
			//Crear Fitness of Michigan rule
			for(int rule = 0; rule < individual.getRuleSet().getMicRules().size(); rule++) {
				individual.getRuleSet().getMicRule(rule).clearFitness();
			}
		}

		Optional<Double> partSum =
			dataset.getPatterns().stream()
				.map(p -> StaticFunction.HammingDistance(individual.getRuleSet().classify(p, doMemorizeMissPatterns[dataID]),
														 p.getConClass()) )
				.map(distance -> distance/(double)Lnum )
				.reduce( (acc, l) -> acc+l );

		loss = partSum.orElse(0.0);
		loss = loss/(double)dataSize;
		return loss * 100.0;
	}

	/**
	 *
	 * @param dataID : int : 0:Dtra, 1:Dtst
	 * @param individual : MultiPittsburgh
	 * @return double : F-measure
	 */
	@Deprecated
	public double calcFmeasure(int dataID, MultiPittsburgh individual) {
		MultiDataSetInfo dataset = getDataSet(dataID);

		int dataSize = dataset.getDataSize();

		int[][] classified = getClassified(dataID, individual);

		double Fmeasure = 0.0;
		for(int p = 0; p < dataSize; p++) {
			MultiPattern pattern = dataset.getPattern(p);
			double precision = StaticFunction.PrecisionMetric(classified[p], pattern.getConClass());
			double recall = StaticFunction.RecallMetric(classified[p], pattern.getConClass());

			double f;
			if((precision + recall) == 0) {
				f = 0;
			}
			else {
				f = (2.0 * recall * precision) / (recall + precision);
			}

			Fmeasure += f;
		}

		Fmeasure = Fmeasure / (double)dataSize;

		return Fmeasure * 100.0;
	}

	/**
	 *
	 * @param dataID : int : 0:Dtra, 1:Dtst
	 * @param individual : MultiPittsburgh
	 * @return double : 完全一致 誤識別率[%]
	 */
	@Deprecated
	public double calcExactMatchError(int dataID, MultiPittsburgh individual) {
		MultiDataSetInfo dataset = getDataSet(dataID);

		double exactMatchNum = 0;	//if classification is exact-match, then this count increments.
		double errorRate = 0.0;

		if(doMemorizeMissPatterns[dataID]) {
			individual.getRuleSet().clearMissPatterns();
			//Crear Fitness of Michigan rule
			for(int rule = 0; rule < individual.getRuleSet().getMicRules().size(); rule++) {
				individual.getRuleSet().getMicRule(rule).clearFitness();
			}
		}

		int dataSize = dataset.getDataSize();
		for(int p = 0; p < dataSize; p++) {
			MultiPattern pattern = dataset.getPattern(p);
			int[] answerClass = individual.getRuleSet().classify(pattern, doMemorizeMissPatterns[dataID]);
			if(Arrays.equals(answerClass, pattern.getConClass())) {
				exactMatchNum++;
			}
		}

		//range [0, 1]
		errorRate = ((double)dataSize - exactMatchNum) / (double)dataSize;

		return errorRate * 100.0;
	}
}
