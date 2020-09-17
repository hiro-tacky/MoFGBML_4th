package emo.problems;

import main.Consts;

/**
 *
 *
 * @param <T>
 * T: Individualを継承したクラス
 */

/**
 *
 * @param MIN 最小化目的問題用id (deault = 1)
 * @param MAX 最小化目的問題用id (deault = -1)
 * @param optimier MIN or MAX 識別用
 * @param objectiveNum 目的数
 * @param <T>
 */
public abstract class MOP<T> {
	// ************************************************************
	protected int MIN = Consts.MINIMIZE;
	protected int MAX = Consts.MAXIMIZE;

	protected int[] optimizer;

	protected int objectiveNum;

	// ************************************************************
	public MOP() {}

	// ************************************************************
	public abstract void evaluate(T t);

	public abstract void evaluateParallel(T t);

	public void setObjectiveNum(int objectiveNum) {
		this.objectiveNum = objectiveNum;
	}

	public int getObjectiveNum() {
		return this.objectiveNum;
	}

	public int[] getOptimizer() {
		return this.optimizer;
	}

}
