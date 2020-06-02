package output.result;

import java.util.ArrayList;

import fgbml.SinglePittsburgh;
import fuzzy.fml.KB;
import ga.Population;

public class Result_MoFGBML {
	public ArrayList<Result_trial> result = new ArrayList<Result_trial>();

	public Result_MoFGBML() {}

	public void setPopulation(Population<SinglePittsburgh> population, KB kb,int trial_input, int gen_input) {
		while(result.size() < trial_input+1) { result.add(new Result_trial()); }
		result.get(trial_input).setPopulation(population, kb, trial_input, gen_input);
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


}
