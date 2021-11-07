package xml;

import fgbml.Pittsburgh;
import fuzzy.fml.KB;
import ga.Population;

@SuppressWarnings("rawtypes")
public class ExperimentSet<T extends Pittsburgh> {
	public Population<T> population;
	public int trial;
	public int generation;
	public KB kb;

	ExperimentSet(int trial, int generation){
		this.trial = trial;
		this.generation = generation;
	}

	public int getTrial() {
		return trial;
	}

	public void setTrial(int trial) {
		this.trial = trial;
	}

	public int getGeneration() {
		return generation;
	}

	public void setGeneration(int generation) {
		this.generation = generation;
	}

	public KB getKb() {
		return kb;
	}

	public void setKb(KB kb) {
		this.kb = kb;
	}


}
