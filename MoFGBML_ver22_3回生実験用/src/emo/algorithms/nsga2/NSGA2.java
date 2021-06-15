package emo.algorithms.nsga2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ForkJoinPool;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import emo.algorithms.Algorithm;
import fgbml.Pittsburgh;
import fgbml.SinglePittsburgh;
import fgbml.problem.FGBML;
import fuzzy.StaticFuzzyFunc;
import ga.GAFunctions;
import ga.Individual;
import ga.Population;
import ga.PopulationManager;
import main.Consts;
import main.Setting;
import main.ExperimentInfo.ExperimentInfo;
import method.MersenneTwisterFast;
import method.Output;
import method.Sort;
import method.StaticFunction;
import output.toXML;
import output.result.Result_MoFGBML;
import output.result.Result_dataset;
import output.result.Result_population;
import time.TimeWatcher;

/**
 * NSGA2クラスを継承してselectionCriteria()メソッドをオーバーライドすれば，
 * 異なるアルゴリズムを作成することが可能．<br>
 *
 */

@SuppressWarnings("rawtypes")
public class NSGA2<T extends Pittsburgh> extends Algorithm<T>{
	// ************************************************************

	// ************************************************************
	public NSGA2() {}

	// ************************************************************


	@SuppressWarnings("unchecked")
	@Override
	public void main(	FGBML mop, /*OutputClass output,*/ T instance, MersenneTwisterFast rnd,
						TimeWatcher timeWatcher, TimeWatcher evaWatcher, Result_MoFGBML master) {
		/* ********************************************************* */
		//START:

		int Ndim = mop.getTrain().getNdim();
		int genCount = 0;	//Generation Count
		int evaCount = 0;	//Evaluation Count
		ForkJoinPool forkJoinPool = Setting.forkJoinPool;

		//Preparing Population Manager
		PopulationManager<Population<T>> manager = new PopulationManager<>();

		/* ********************************************************* */
		//Step 1. Population Initialization
		Population<T> population = new Population<>();
		for(int pop = 0; pop < Setting.populationSize; pop++) {
			//Initialization
			T individual = (T)instance.newInstance(Ndim, Consts.INITIATION_RULE_NUM, mop.getObjectiveNum());

			while(true) {
				//RuleSet Generation
				if(Consts.DO_HEURISTIC_GENERATION) {
					//Heuristic Rule Generation
					individual.initHeuristic(mop.getTrain(), rnd);
				}
				else {
					//Random Antecedent Generation
					individual.initRand(rnd);
				}

				//Consequent Part Learning
				individual.getRuleSet().learning(mop.getTrain(), forkJoinPool);

				//Reducing Same Rules
				individual.getRuleSet().removeRule();
				individual.getRuleSet().radixSort();
				individual.getRuleSet().calcRuleLength();

				if(individual.getRuleSet().getMicRules().size() != 0) {
					break;
				}
			}

			individual.ruleset2michigan();
			individual.michigan2pittsburgh();
			population.addIndividual(individual);
		}
		manager.setPopulation(population);

		/* ********************************************************* */
		//Step 2. Initial Population Evaluation
		evaWatcher.start();
		manager.populationEvaluation(mop);
		evaWatcher.stop();
		evaCount += manager.getPopulation().getIndividuals().size();

		// Non-dominated Sort Initial Population
		ArrayList<ArrayList<Individual_nsga2>> F_ = nonDominatedSort(manager.getPopulation(), mop.getOptimizer());
		for(int i = 0; i < F_.size(); i++) {
			crowdingDistanceAssignment(F_.get(i));
		}
		genCount++;
		System.out.print("0");

		// Save Initial Population
		timeWatcher.stop();
		//Appendix Information
		mop.setAppendix(manager.getPopulation());
//		output.savePopulationOrOffspring(manager, resultMaster, true);
		timeWatcher.start();

		/* ********************************************************* */
		//Step 3. GA Searching Frame
		int detailCount = 1;
		while(true) {
			/* ********************************************************* */
			//Output "Period" per const interval.
			if(genCount % Consts.PER_SHOW_GENERATION_NUM == 0) {
				if(detailCount % Consts.PER_SHOW_GENERATION_DETAIL == 0) {
					System.out.print(genCount);
				} else {
					System.out.print(".");
				}
				detailCount++;
			}

			//The termination criteria judge
			if(StaticFunction.terminationJudge(genCount, evaCount)) {
				break;
			}

			/* ********************************************************* */
			//Offspring Generation
			Population<T> offspring = new Population<>();
			for(int q = 0; q < Setting.offspringSize; q++) {
				T child = null;

				while(true) {
					//Step 1. Mating Selection
					int tournamentSize = 2;
					int parentSize = 2;
					Individual_nsga2[] parent = tournamentSelection(manager.getPopulation(), parentSize, tournamentSize, rnd);

					//Step 2. Crossover
						//GA type Selection (Michigan or Pittsburgh)
					if(rnd.nextDouble() < (double)Consts.RULE_OPE_RT) {
						//Michigan Type Crossover (Child Generation)
						child = (T)GAFunctions.michiganCrossover(mop, (Pittsburgh)parent[0], rnd);
					} else {
						//Pittsburgh Type Crossover (Child Generation)
						Pittsburgh[] cast = new Pittsburgh[parentSize];
						for(int i = 0; i < parentSize; i++) {
							cast[i] = (Pittsburgh)parent[i];	//Shallow Copy
						}
						child = (T)GAFunctions.pittsburghCrossover(cast, rnd);
					}

					//Step 3. Mutation
					if(ExperimentInfo.FuzzySetType == 99) {
						GAFunctions.pittsburghMutationMulti(child, mop.getTrain(), rnd);
					}else {
						GAFunctions.pittsburghMutation(child, mop.getTrain(), rnd);
					}

					//Step 4. Learning
					child.getRuleSet().learning(mop.getTrain(), forkJoinPool);

					//Step 5. Rule Deletion
					child.getRuleSet().removeRule();
					child.getRuleSet().radixSort();
					child.getRuleSet().calcRuleLength();

					//If child don't have any rule, the child should not be evaluated.
					//Then new child will be generated.
					if(child.getRuleSet().getMicRules().size() != 0) {
						break;
					}
				}

				child.ruleset2michigan();
				child.michigan2pittsburgh();
				offspring.addIndividual(child);

			}
			manager.setOffspring(offspring);

			//Offspring Evaluation
			evaWatcher.start();
			manager.offspringEvaluation(mop);
			evaWatcher.stop();
			evaCount += manager.getOffspring().getIndividuals().size();

			/* ********************************************************* */
			//Environmental Selection
			//NSGA-II Main Frame
			mainFrame(manager, mop.getOptimizer());
			genCount++;

			/* ********************************************************* */
			//Save current Population & new Offspring
			timeWatcher.stop();
			if(genCount % Setting.timingOutput == 0) {
				//Appendix Information
				//Population
				mop.setAppendix(manager.getPopulation());
				//Offspring
				mop.setAppendix(manager.getOffspring());

				Result_population result_population = new Result_population((Population<SinglePittsburgh>) manager.getPopulation(), StaticFuzzyFunc.kb, genCount);
				Result_dataset result_dataset = new Result_dataset();
				try {
					String sep = File.separator;
					toXML result = new toXML("result");
					toXML ruleset = new toXML("ruleset");
					toXML ClassifyResult = new toXML("classifyResult");
					result.ResultToXML(result_population);
					ruleset.RuleSetToXML(result_population, master.getNowTrial());
					result_dataset.setDataset(master.getTstFile());
					result_dataset.addClassifyResult((Population<SinglePittsburgh>) manager.getPopulation());
					ClassifyResult.classifyResultToXML(result_dataset);
					Output.mkdirs(ExperimentInfo.resultRoot + sep + "trial_" + String.valueOf(master.getNowTrial()) + sep + "gen_" + String.valueOf(genCount));
					result.output(ExperimentInfo.dataName + "_result", ExperimentInfo.resultRoot + sep + "trial_" + String.valueOf(master.getNowTrial()) + sep + "gen_" + String.valueOf(genCount));
					ruleset.output(ExperimentInfo.dataName + "_ruleset", ExperimentInfo.resultRoot + sep + "trial_" + String.valueOf(master.getNowTrial()) + sep + "gen_" + String.valueOf(genCount));
					ClassifyResult.output(ExperimentInfo.dataName + "_classifyResult", ExperimentInfo.resultRoot + sep + "trial_" + String.valueOf(master.getNowTrial()) + sep + "gen_" + String.valueOf(genCount));
				} catch (TransformerConfigurationException | ParserConfigurationException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				} catch (FileNotFoundException | TransformerException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}

				//Save Current Population
//				output.savePopulationOrOffspring(manager, resultMaster, true);
				//Save Offspring
//				output.savePopulationOrOffspring(manager, resultMaster, false);
				master.setPopulation((Population<SinglePittsburgh>) manager.getPopulation(), StaticFuzzyFunc.kb, genCount);
			}
			timeWatcher.start();
		}
		master.addClassifyResult((Population<SinglePittsburgh>) manager.getPopulation());
	}

	/**
	 * Selection Criteria with NSGA-II specific parameters, rank and crowding.<br>
	 * First criteria is that the Pareto rank is better.<br>
	 * Second criteria is Crowded-Comparison Operater.<br>
	 * 	The operator is composed two criteria.<br>
	 *	First, lower Pareto rank is better.<br>
	 *	Second (if Pareto rank is same), higher crowding distance is better.<br>
	 *
	 * @param a
	 * @param b
	 * @return true: winner a, false: winner b.
	 */
	public boolean selectionCriteria(Individual_nsga2 a, Individual_nsga2 b) {
		boolean winner = true;
		if(a.getRank() < b.getRank()) {
			winner = true;
		} else if(a.getRank() > b.getRank()) {
			winner = false;
		} else {
			if(a.getCrowding() > b.getCrowding()) {
				winner = true;
			} else if(a.getCrowding() < b.getCrowding()) {
				winner = false;
			} else {// a.crowding == b.crowding
				if(a.getID() < b.getID()) {
					winner = true;
				} else {
					winner = false;
				}
			}
		}

		return winner;
	}


	/**
	 * 各世代で子個体生成後にNSGA-II MainFrameを呼び出す．<br>
	 * mainFrame実行後，managerのpopulationが更新されている．<br>
	 *
	 * @param manager
	 * @param optimizer :  minimize: 1, maximize: -1
	 * @param rnd
	 */
	@SuppressWarnings({ "unchecked" })
	public void mainFrame(PopulationManager manager, int[] optimizer) {
		Population<Individual_nsga2> margePopulation = new Population<Individual_nsga2>();
		//Add Population
		int popSize = ((Population)manager.getPopulation()).getIndividuals().size();
		for(int i = 0; i < popSize; i++) {
			margePopulation.addIndividual((Individual_nsga2)((Population)manager.getPopulation()).getIndividual(i));
		}

		//Add Offspring
		int offspringSize = ((Population)manager.getOffspring()).getIndividuals().size();
		for(int i = 0; i < offspringSize; i++) {
			margePopulation.addIndividual((Individual_nsga2)((Population)manager.getOffspring()).getIndividual(i));
		}

		//Set UniqueID
		for(int i = 0; i < margePopulation.getIndividuals().size(); i++) {
			margePopulation.getIndividual(i).setID(i);
		}

		ArrayList<ArrayList<Individual_nsga2>> F_ = nonDominatedSort(margePopulation, optimizer);
		//Assignment Crowding Distance in Each Front
		for(int i = 0; i < F_.size(); i++) {
			crowdingDistanceAssignment(F_.get(i));
		}
		int i = 0;	//Current Front
		Population<Individual_nsga2> nextPopulation = new Population<Individual_nsga2>();
		while((nextPopulation.getIndividuals().size() + F_.get(i).size()) <= popSize) {
			for(int j = 0; j < F_.get(i).size(); j++) {
				nextPopulation.addIndividual(F_.get(i).get(j));
			}
			i++;
		}

		//Sort F_i by NSGA-II Selection Criteria
		Collections.sort(F_.get(i), new Comparator<Individual_nsga2>() {
			@Override
			public int compare(Individual_nsga2 o1, Individual_nsga2 o2) {
				if(selectionCriteria(o1, o2)) {
					return -1;
				} else {
					return 1;
				}
			}
		});

		//Population Update
		int addNum = popSize - nextPopulation.getIndividuals().size();
		ArrayList<Individual_nsga2> reCrowding = new ArrayList<Individual_nsga2>();
		for(int p = 0; p < addNum; p++) {
			reCrowding.add(F_.get(i).get(p));
		}
		//Reassignment Crowding Distance
		crowdingDistanceAssignment(reCrowding);
		for(int p = 0; p < addNum; p++) {
			nextPopulation.addIndividual(reCrowding.get(p));
		}

		manager.setPopulation(nextPopulation);
	}

	/**
	 * <h1>Mating Selection by Tournament Selection</h1>
	 * Selected Two Parents from P by NSGA-II Criteria<br>
	 * @param P : Population
	 * @param parentSize : int : #of parent
	 * @param tournamentSize : int : #of candidate
	 * @param rnd
	 * @return : Individual_nsga2[] : Chosen Parents
	 */
	public Individual_nsga2[] tournamentSelection(Population P, int parentSize, int tournamentSize, MersenneTwisterFast rnd) {
		MersenneTwisterFast uniqueRnd = new MersenneTwisterFast(rnd.nextInt());
		int popSize = P.getIndividuals().size();
		Individual_nsga2[] parents = new Individual_nsga2[parentSize];

		Individual_nsga2 winner;
		Individual_nsga2 candidate;

		for(int i = 0; i < parents.length; i++) {
			winner = (Individual_nsga2)P.getIndividual(uniqueRnd.nextInt(popSize));
			for(int j = 1; j < tournamentSize; j++) {
				candidate = (Individual_nsga2)P.getIndividual(uniqueRnd.nextInt(popSize));
				if(selectionCriteria(candidate, winner)) {
					winner = candidate;
				}
			}
			parents[i] = winner;
		}
		return parents;
	}


	/**
	 * Assignment Rank for Each Individual
	 *
	 * @param P
	 * @param optimizer :  minimize: 1, maximize: -1
	 * @return
	 * Sets of solutions in each front.
	 */
	public ArrayList<ArrayList<Individual_nsga2>> nonDominatedSort(Population P, int[] optimizer) {
		int popSize = P.getIndividuals().size();

		//The number of solutions that dominate p
		int[] n_ = new int[popSize];

		//Individual q in "S_p" is dominated by p
		@SuppressWarnings("unchecked")
		ArrayList<Integer>[] S_ = new ArrayList[popSize];

		//The set of solutions in Rank i.
		ArrayList<ArrayList<Integer>> F_ = new ArrayList<ArrayList<Integer>>();
		F_.add(new ArrayList<Integer>());	//Initialize the first front

		ArrayList<ArrayList<Individual_nsga2>> FF_ = new ArrayList<ArrayList<Individual_nsga2>>();
		FF_.add(new ArrayList<Individual_nsga2>());

		//for each p in pop
		for(int p = 0; p < popSize; p++) {
			S_[p] = new ArrayList<Integer>();
			n_[p] = 0;

			//for each q in pop
			for(int q = 0; q < popSize; q++) {
				if(p == q) continue;

				if(isDominate((Individual)P.getIndividual(p), (Individual)P.getIndividual(q), optimizer)) {
					//Is p dominating q?
					S_[p].add(q);
				}
				else if(isDominate((Individual)P.getIndividual(q), (Individual)P.getIndividual(p), optimizer)) {
					//Is p dominated by q?
					n_[p]++;
				}
			}
			if(n_[p] == 0) {
				((Individual_nsga2)P.getIndividual(p)).setRank(0);
				F_.get(0).add(p);
				FF_.get(0).add((Individual_nsga2)P.getIndividual(p));
			}
		}

		int i = 0; //Initialize the front counter
		while(F_.get(i).size() != 0) {
			F_.add(new ArrayList<Integer>());	//new Front produced
			FF_.add(new ArrayList<Individual_nsga2>());
			for(int pp = 0; pp < F_.get(i).size(); pp++) {
				//Index p from original population P
				int p = F_.get(i).get(pp);

				for(int qq = 0; qq < S_[p].size(); qq++) {
					//Index q from original population P
					int q = S_[p].get(qq);
					n_[q]--;

					if(n_[q] == 0) {
						((Individual_nsga2)P.getIndividual(q)).setRank(i + 1);
						F_.get(i+1).add(q);
						FF_.get(i+1).add((Individual_nsga2)P.getIndividual(q));
					}
				}
			}
			i++;
		}

		return FF_;
	}

	/**
	 * Assignment Crowding Distance for Each Individual
	 * @param P : ArrayList{@literal <Individual_nsga2>} : Individuals which have same ranks. (Population in same front)
	 */
	public void crowdingDistanceAssignment(ArrayList<Individual_nsga2> P) {
		int popSize = P.size();
		if(popSize == 0) {
			return;
		}

		//Initialize Distance
		for(int i = 0; i < popSize; i++) {
			P.get(i).setCrowding(0.0);
		}

		int objective = P.get(0).getObjectiveNum();
		for(int o = 0; o < objective; o++) {

			double max = Double.NEGATIVE_INFINITY;
			double min = Double.POSITIVE_INFINITY;

			double[] fitness = new double[popSize];
			int[] order = new int[popSize];
			for(int p = 0; p < popSize; p++) {
				order[p] = p;
				fitness[p] = P.get(p).getFitness(o);
				if(max < fitness[p] ){
					max = fitness[p];
				}
				if(min > fitness[p]) {
					min = fitness[p];
				}
			}
			order = Sort.sort(fitness, 0);

			P.get(order[0]).addCrowding(Double.POSITIVE_INFINITY);
			P.get(order[order.length-1]).addCrowding(Double.POSITIVE_INFINITY);

			//If all fitness value in objective o is same
			boolean sameAll = false;
			for(int i = 1; i < fitness.length; i++) {
				if(fitness[i-1] == fitness[i]) {
					sameAll = true;
				} else {
					sameAll = false;
					break;
				}
			}
			for(int i = 1; i < popSize - 1; i++) {
				if(sameAll) {
					double distance = 0;
					P.get(order[i]).addCrowding(distance);
				} else {
					double distance = (P.get(order[i+1]).getFitness(o) - P.get(order[i-1]).getFitness(o)) / (max - min);
					P.get(order[i]).addCrowding(distance);
				}
			}

		}

	}

	/**
	 *
	 * @param optimizer : minimize: 1, maximize: -1
	 * @return boolean : Is p dominating q ?
	 */
	public boolean isDominate(Individual p, Individual q, int[] optimizer) {
		boolean isDominate = false;

		for(int o = 0; o < optimizer.length; o++) {
			if(optimizer[o] * p.getFitness(o) > optimizer[o] * q.getFitness(o)) {
				isDominate = false;
				break;
			}
			else if(optimizer[o] * p.getFitness(o) < optimizer[o] * q.getFitness(o)) {
				isDominate = true;
			}
		}

		return isDominate;
	}
}
