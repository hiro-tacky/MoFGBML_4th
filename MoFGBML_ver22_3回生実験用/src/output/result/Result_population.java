package output.result;

import java.util.ArrayList;

import fgbml.SinglePittsburgh;
import fuzzy.fml.KB;
import ga.Population;

public class Result_population {
	ArrayList<Result_individual> result = new ArrayList<Result_individual>();
	public int popNum;
	public int gen;
	public KB kb;

	public Result_population(Population<SinglePittsburgh> population, KB kb_input, int gen_input) {
		gen = gen_input;
		popNum = population.getIndividuals().size();
		kb = kb_input;
		for(int i=0; i<popNum; i++) {
			result.add(new Result_individual(population.getIndividual(i)));
		}
	}

	public ArrayList<Result_individual> getResult() {
		return result;
	}

	public Result_individual getIindividual(int i) {
		return result.get(i);
	}

	public void setResult(ArrayList<Result_individual> result) {
		this.result = result;
	}

	public int getPopNum() {
		return popNum;
	}

	public void setPopNum(int popNum) {
		this.popNum = popNum;
	}

	public int getGen() {
		return gen;
	}

	public void setGen(int gen) {
		this.gen = gen;
	}

	public KB getKb() {
		return kb;
	}

	public void setKb(KB kb) {
		this.kb = kb;
	}

}
