package xml.result;

import java.util.ArrayList;

import fgbml.SinglePittsburgh;
import fuzzy.fml.KB;
import ga.Population;

public class Result_trial {
	ArrayList<Result_population> result = new ArrayList<Result_population>();
	public int trial;

	public Result_trial() {}

	public void setPopulation(Population<SinglePittsburgh> population, KB kb, int trial_input, int gen_input) {
		trial = trial_input;
		for(int i=0; i<result.size(); i++) {
			if(result.get(i).getGen() == gen_input) {
				result.set(i, new Result_population(population, kb, gen_input));
				return;
			}
		}
		result.add(new Result_population(population, kb, gen_input));
	}

	public ArrayList<Result_population> getResult() {
		return result;
	}

	public Result_population getResultPopulation(int i) {
		return result.get(i);
	}

	public void setResult(ArrayList<Result_population> result) {
		this.result = result;
	}

	public int getTrial() {
		return trial;
	}

	public void setTrial(int trial) {
		this.trial = trial;
	}

}
