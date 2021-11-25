package xml.result;

import java.util.ArrayList;

import data.Input;
import data.SingleDataSetInfo;
import fgbml.SinglePittsburgh;
import ga.Population;

public class Result_dataset {
	public ArrayList<SingleDataSetInfo> Dtst = new ArrayList<SingleDataSetInfo>();
	public ArrayList<ArrayList<int[][]>> classifyResult = new ArrayList<ArrayList<int[][]>>();
	public int trialNum;

	public Result_dataset() {
		trialNum = 0;
	};

	public void setDataset(String tstFile) {
		SingleDataSetInfo DtstBuf = new SingleDataSetInfo();
		Input.inputFile(DtstBuf, tstFile);
		Dtst.add(DtstBuf);
	}

	public void addClassifyResult(Population<SinglePittsburgh> population) {
		ArrayList<int[][]> buf = new ArrayList<int[][]>();
		for(SinglePittsburgh individual: population.getIndividuals()) {
			int[][] classifyResult_buf = individual.getRuleSet().classifyResult(Dtst.get(Dtst.size()-1));
			buf.add(classifyResult_buf);
		}
		classifyResult.add(buf);
		trialNum++;
	}

	public ArrayList<SingleDataSetInfo> getDtst() {
		return Dtst;
	}

	public void setDtst(ArrayList<SingleDataSetInfo> dtst) {
		Dtst = dtst;
	}

	public ArrayList<ArrayList<int[][]>> getClassifyResult() {
		return classifyResult;
	}

	public void setClassifyResult(ArrayList<ArrayList<int[][]>> classifyResult) {
		this.classifyResult = classifyResult;
	}

	public int getTrialNum() {
		return trialNum;
	}

	public void setTrialNum(int trialNum) {
		this.trialNum = trialNum;
	}

}
