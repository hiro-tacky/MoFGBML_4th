package fgbml.problem;

import data.DataSetInfo;
import emo.problems.MOP;
import ga.Individual;
import ga.Population;

@SuppressWarnings("rawtypes")
/**
 * FGBML
 * @author hirot
 *
 * @param appendixNUm FGBMLの付加情報の数
 */
public abstract class FGBML<T extends Individual> extends MOP<T>{

	// ************************************************************
	DataSetInfo train;
	DataSetInfo test;

	protected int appendixNum;

	// ************************************************************


	// ************************************************************

	public abstract void setAppendix(Population<T> population);
	/**
	 *
	 * @return trian(学習データ)
	 */
	public DataSetInfo getTrain() {
		return this.train;
	}

	/**
	 * Shallow Copy
	 * @param train
	 */
	public void setTrain(DataSetInfo train) {
		this.train = train;
	}

	public DataSetInfo getTest() {
		return this.test;
	}

	/**
	 * Shallow Copy
	 * @param test
	 */
	public void setTest(DataSetInfo test) {
		this.test = test;
	}

	public int getAppendixNum() {
		return this.appendixNum;
	}

	public void setAppendixNum(int appendixNum) {
		this.appendixNum = appendixNum;
	}


}
