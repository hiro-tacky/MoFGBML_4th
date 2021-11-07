package xml.result;

import java.util.ArrayList;

import fgbml.SinglePittsburgh;
import fuzzy.fml.KB;
import ga.Population;
import method.Output;

/**
 * 全個体，全試行の結果を保持する用のクラス，toXML.javaで出力用．
 *
 * @author hirot
 *
 */
public class Result_MoFGBML {

	String rootDir;
	String id;
	String trialRoot;
	String tstFile;
	int nowCV;
	int nowRep;
	int nowTrial;

	public Result_dataset resultDataset;
	public ArrayList<Double> times = new ArrayList<Double>();
	public ArrayList<Double> evaTimes = new ArrayList<Double>();

	public ArrayList<Result_trial> result = new ArrayList<Result_trial>();

	public Result_MoFGBML() {
		resultDataset = new Result_dataset();
	}

	public void setPopulation(Population<SinglePittsburgh> population, KB kb,int gen_input) {
		while(result.size() < nowTrial+1) { result.add(new Result_trial()); }
		result.get(nowTrial).setPopulation(population, kb, nowTrial, gen_input);
	}

	public void addClassifyResult(Population<SinglePittsburgh> population) {
		resultDataset.addClassifyResult(population);
	}

	public void setDataset(String tstFile) {
		this.tstFile = tstFile;
		resultDataset.setDataset(tstFile);
	}

	public ArrayList<Result_trial> getResult() {
		return result;
	}

	public Result_trial getResultTrial(int i) {
		return result.get(i);
	}

	public void setResult(ArrayList<Result_trial> result) {
		this.result = result;
	}

	public int getNowCV() {
		return nowCV;
	}

	public void setNowCV(int nowCV) {
		this.nowCV = nowCV;
	}

	public int getNowRep() {
		return nowRep;
	}

	public void setNowRep(int nowRep) {
		this.nowRep = nowRep;
	}

	public int getNowTrial() {
		return nowTrial;
	}

	public void setNowTrial(int nowTrial) {
		this.nowTrial = nowTrial;
	}

	public String getRootDir() {
		return rootDir;
	}

	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}

	public String getTrialRoot() {
		return trialRoot;
	}

	public void setTrialRoot(String trialRoot) {
		this.trialRoot = trialRoot;
	}

	public void addTimes(double time) {
		times.add(time);
	}

	public void addEvaTimes(double evaTime) {
		evaTimes.add(evaTime);
	}

	public Result_dataset getResultDataset() {
		return resultDataset;
	}

	public void setResultDataset(Result_dataset resultDataset) {
		this.resultDataset = resultDataset;
	}

	public String getTstFile() {
		return tstFile;
	}

	public void setTstFile(String tstFile) {
		this.tstFile = tstFile;
	}

	public void outputTimes(String fileName) {
		String str = "";
		ArrayList<String> strs = new ArrayList<String>();

		//Header
		str = "CV";
		str += "," + "Time";
		str += "," + "evaTime";
		strs.add(str);

		//Contains
		for(int cv = 0; cv < times.size(); cv++) {
			str = String.valueOf(cv);
			str += "," + String.valueOf(times.get(cv));
			str += "," + String.valueOf(evaTimes.get(cv));
			strs.add(str);
		}

		Output.writeln(fileName, strs);
	}
}
