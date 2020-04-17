package fgbml.multilabel_ver3;

import fgbml.problem.OutputClass;
import ga.Population;

public class Output_MultiLabel extends OutputClass<MultiPittsburgh>{

	@Override
	public String outputPittsburgh(Population<MultiPittsburgh> population) {
		String ln = System.lineSeparator();
		String strs = "";
		String str = "";

		int popSize = population.getIndividuals().size();
		int objectiveNum = population.getIndividual(0).getObjectiveNum();

		//Header
		str = "id";
		for(int o = 0; o < objectiveNum; o++) {
			str += "," + "f" + String.valueOf(o);
		}
		str += "," + "rank";
		str += "," + "crowding";
		//Appendix
		str += "," + "ExactMatchError_Dtra";
		str += "," + "Fmeasure_Dtra";
		str += "," + "HammingLoss_Dtra";
		str += "," + "ExactMatchError_Dtst";
		str += "," + "Fmeasure_Dtst";
		str += "," + "HammingLoss_Dtst";
		str += "," + "ruleNum";
		str += "," + "ruleLength";
		strs += str + ln;

		//Population
		for(int p = 0; p < popSize; p++) {
			//id
			str = String.valueOf(p);
			//fitness
			for(int o = 0; o < objectiveNum; o++) {
				str += "," + population.getIndividual(p).getFitness(o);
			}
			//rank
			str += "," + population.getIndividual(p).getRank();
			//crowding distance
			str += "," + population.getIndividual(p).getCrowding();
			//Exact-Match for Dtra
			str += "," + population.getIndividual(p).getAppendix(0);
			//F-measure for Dtra
			str += "," + population.getIndividual(p).getAppendix(1);
			//Hamming Loss for Dtra
			str += "," + population.getIndividual(p).getAppendix(2);
			//Exact-Match for Dtst
			str += "," + population.getIndividual(p).getAppendix(3);
			//F-measure for Dtst
			str += "," + population.getIndividual(p).getAppendix(4);
			//Hamming Loss for Dtst
			str += "," + population.getIndividual(p).getAppendix(5);
			//ruleNum
			str += "," + population.getIndividual(p).getRuleSet().getRuleNum();
			//ruleLength
			str += "," + population.getIndividual(p).getRuleSet().getRuleLength();

			strs += str + ln;
		}

		return strs;
	}

	@Override
	public String outputRuleSet(Population<MultiPittsburgh> population) {
		String ln = System.lineSeparator();
		String row = "***************************************";
		String hyphen = "---";

		String strs = "";
		String str = "";

		int popSize = population.getIndividuals().size();
		int Ndim = population.getIndividual(0).getNdim();
		int Lnum = population.getIndividual(0).getRuleSet().getMicRule(0).getConc().length;
		int objectiveNum = population.getIndividual(0).getObjectiveNum();

		for(int pop = 0; pop < popSize; pop++) {
			int ruleNum = population.getIndividual(pop).getRuleNum();

			strs += row + ln;
			strs += "pop_" + pop + ln;
			strs += "ruleNum: " + ruleNum + ln;
			strs += "rank: " + population.getIndividual(pop).getRank() + ln;
			strs += "crowding: " + population.getIndividual(pop).getCrowding() + ln;
			strs += "--- Dtra ---" + ln;
			strs += "   Exact-Match Error: " + population.getIndividual(pop).getAppendix(0) + ln;
			strs += "   F-measure: " + population.getIndividual(pop).getAppendix(1) + ln;
			strs += "   Hamming Loss: " + population.getIndividual(pop).getAppendix(2) + ln;
			strs += "--- Dtst ---" + ln;
			strs += "   Exact-Match Error: " + population.getIndividual(pop).getAppendix(3) + ln;
			strs += "   F-measure: " + population.getIndividual(pop).getAppendix(4) + ln;
			strs += "   Hamming Loss: " + population.getIndividual(pop).getAppendix(5) + ln;
			for(int o = 0; o < objectiveNum; o++) {
				strs += "f"+o+": " + population.getIndividual(pop).getFitness(o) + ln;
			}
			strs += hyphen + ln;

			//Rules
			for(int rule = 0; rule < ruleNum; rule++) {
				//id
				str = "Rule_" + String.format("%02d", rule) + ":";
				//rule
				for(int n = 0; n < Ndim; n++) {
					str += " " + String.format("%2d", population.getIndividual(pop).getRuleSet().getMicRule(rule).getRule(n));
				}
				//class
				str += ", " + "Class:";
				for(int l = 0; l < Lnum; l++) {
					str += " " + population.getIndividual(pop).getRuleSet().getMicRule(rule).getConc(l);
				}
				//cf Mean
				str += ", " + "CF_mean: " + population.getIndividual(pop).getRuleSet().getMicRule(rule).getCf();
				//CF Vector
				str += ", " + "CF_vector:";
				for(int l = 0; l < Lnum; l++) {
					str += " " + population.getIndividual(pop).getRuleSet().getMicRule(rule).getCFVector(l);
				}
				//fitness
				str += ", " + "Fitness: " + population.getIndividual(pop).getRuleSet().getMicRule(rule).getFitness();

				strs += str + ln;
			}
			strs += row + ln;
			strs += "" + ln;
		}

		return strs;
	}

}













