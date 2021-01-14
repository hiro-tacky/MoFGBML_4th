package output.result;

import java.util.ArrayList;

import fgbml.SinglePittsburgh;

public class Result_individual {
	public ArrayList<Double> f = new ArrayList<Double>();
	public double Dtra;
	public double Dtst;
	public double ruleNum;
	public double ruleLength;
	public double rank;
	public double crowding;

	public String mennbership;
	public SinglePittsburgh rule;

	public Result_individual(SinglePittsburgh input) {
		int objectiveNum = input.getObjectiveNum();
		rule = input;
		for(int o = 0; o < objectiveNum; o++) {
			f.add(input.getFitness(o));
		}
		//Dtra
		Dtra = input.getAppendix(0);
		//Dtst
		Dtst = input.getAppendix(1);
		//ruleNum
		ruleNum = input.getRuleSet().getRuleNum();
		//ruleLength
		ruleLength = input.getRuleSet().getRuleLength();
//
//		//rank
//		rank = input.getRank();
//		//crowding distance
//		crowding = input.getCrowding();

	}


	public ArrayList<Double> getF() {
		return f;
	}


	public void setF(ArrayList<Double> f) {
		this.f = f;
	}

	public double getCrowding() {
		return crowding;
	}

	public void setCrowding(double crowding) {
		this.crowding = crowding;
	}


	public double getDtra() {
		return Dtra;
	}

	public void setDtra(double dtra) {
		Dtra = dtra;
	}

	public double getDtst() {
		return Dtst;
	}

	public void setDtst(double dtst) {
		Dtst = dtst;
	}

	public double getRuleNum() {
		return ruleNum;
	}

	public void setRuleNum(double ruleNum) {
		this.ruleNum = ruleNum;
	}

	public double getRuleLength() {
		return ruleLength;
	}

	public void setRuleLength(double ruleLength) {
		this.ruleLength = ruleLength;
	}

	public double getRank() {
		return rank;
	}

	public void setRank(double rank) {
		this.rank = rank;
	}

	public String getMennbership() {
		return mennbership;
	}

	public void setMennbership(String mennbership) {
		this.mennbership = mennbership;
	}


	public SinglePittsburgh getRule() {
		return rule;
	}


	public void setRule(SinglePittsburgh rule) {
		this.rule = rule;
	}

}
