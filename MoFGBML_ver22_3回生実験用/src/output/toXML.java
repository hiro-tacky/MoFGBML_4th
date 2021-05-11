package output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import data.SingleDataSetInfo;
import data.SinglePattern;
import fgbml.Michigan;
import fgbml.SinglePittsburgh;
import fuzzy.Rule;
import fuzzy.SingleRule;
import fuzzy.SingleRuleSet;
import fuzzy.fml.FuzzySet;
import fuzzy.fml.KB;
import jfml.term.FuzzyTermType;
import output.result.Result_MoFGBML;
import output.result.Result_dataset;
import output.result.Result_individual;
import output.result.Result_population;
import output.result.Result_trial;

public class toXML {
	DocumentBuilderFactory factory;
	DocumentBuilder builder;
	DOMImplementation domImpl;
	protected Document document;
	TransformerFactory transFactory;
	Transformer transformer;
	String name_xml;

	/**
	 * setSettingでインスタンス生成してから
	 * rbのデータを書き込む
	 * XML出力の際はoutputで
	 * basicのみ対応，追加データがある場合は継承クラスで追加ヨロシクゥ！
	 * @param rb rulebase
	 * @throws Exception
	 */
	public toXML(String name_input) throws ParserConfigurationException, TransformerConfigurationException {
		name_xml = name_input;
		factory = DocumentBuilderFactory.newInstance();
		builder = factory.newDocumentBuilder();
		domImpl = builder.getDOMImplementation();
		document = domImpl.createDocument("",name_xml,null);
		transFactory = TransformerFactory.newInstance();
		transformer = transFactory.newTransformer();
	}
//
//
//	public void KBtoXML(KB kb_SP) {
//		//全体
//		Element master=document.getDocumentElement();
//
//		/**kb**/
//		Element kb = addChildNode("KB", master);
//
//		//name_kb
//		addChildNode_value("name", kb, kb_SP.name);
//
//		//DomainLeft
//		addChildNode_value("DomainLeft", kb, String.valueOf(kb_SP.DomainLeft));
//
//		//DomainRight
//		addChildNode_value("DomainRight", kb, String.valueOf(kb_SP.DomainRight));
//		//fuzzyvariabletype"s" ファジィ変数集合(次元別分割集合)
//		Element fuzzyvariabletypes = addChildNode("fuzzyvariabletypes", kb);
//		for(int i=0; i<kb_SP.getFuzzyVariableType_ArrayList().size(); i++) {
//			//fuzzyvariabletype ファジィ変数集合(次元別)
//			Element fuzzyvariabletype = addChildNode("fuzzyvariabletype", fuzzyvariabletypes);
//			fuzzyvariabletype.setAttribute("id", String.valueOf(i));
//
//			//個々のファジィ変数
//			for(int j=0; j<kb_SP.getFuzzyVariableType(i).getTerms().size(); j++) {
//				FuzzySet vv = (FuzzySet) kb_SP.getFuzzyVariableType(i).getFuzzyTerm(j);
//				Element fuzzytermtype = addChildNode("fuzzytermtype", fuzzyvariabletype);
//				fuzzytermtype.setAttribute("id", String.valueOf(j));
//				//name_fuzzytermtype
//				addChildNode_value("name", fuzzytermtype, vv.getName());
//				//type
//				addChildNode_value("Shape_Type_ID", fuzzytermtype, String.valueOf(vv.getType()));
//				String ShapeName = null;
//				switch(vv.getType()) {
//					case 0: ShapeName = "rightLinearShape"; break;
//					case 1: ShapeName = "leftLinearShape"; break;
//					case 2: ShapeName = "piShape"; break;
//					case 3: ShapeName = "triangularShape"; break;
//					case 4: ShapeName = "gaussianShape"; break;
//					case 5: ShapeName = "rightGaussianShape"; break;
//					case 6: ShapeName = "leftGaussianShape"; break;
//					case 7: ShapeName = "trapezoidShape"; break;
//					case 8: ShapeName = "singletonShape"; break;
//					case 9: ShapeName = "rectangularShape"; break;
//					case 10: ShapeName = "zShape"; break;
//					case 11: ShapeName = "sShape"; break;
//					case 12: ShapeName = "pointSetShape"; break;
//					case 13: ShapeName = "pointSetMonotonicShape"; break;
//					case 14: ShapeName = "circularDefinition"; break;
//					case 15: ShapeName = "customShape"; break;
//					case 16: ShapeName = "customMonotonicShape"; break;
//				}
//				addChildNode_value("Shape_Type", fuzzytermtype, ShapeName);
//				//parameters
//				float[] param = vv.getParam();
//				Element params = addChildNode("parameters", fuzzytermtype);
//				for(int k=0; k<param.length; k++) {
//					Element parameter = document.createElement("parameter");
//					parameter.setAttribute("id", String.valueOf(k));
//					Text textContents = document.createTextNode(String.valueOf(param[k]));
//					parameter.appendChild(textContents);
//					params.appendChild(parameter);
//				}
//			}
//		}
//	}


	@SuppressWarnings("rawtypes")
	public void SinglePittsburghToXML(SinglePittsburgh input) {
		/** 全体 */
		Element master=document.getDocumentElement();

		Element singlepittsburgh = addChildNode("SinglePittsburgh", master);
		singlepittsburgh.setAttribute("ID", String.valueOf(input.getID()));

		//gene
		Object Gene[] = input.getGene();

		Element gene = addChildNode("gene", singlepittsburgh);
			for(int i=0; i<Gene.length; i++) {
				Element element = addChildNode_value("element", gene, String.valueOf(input.getGene(i)));
				element.setAttribute("ID", String.valueOf(i));
			}

		//fitness
		double Fitness[] = input.getFitness();
		Element fitness = addChildNode("fitness", singlepittsburgh);
		for(int i=0; i<Fitness.length; i++) {
			Element element = addChildNode_value("element", fitness, String.valueOf(input.getFitness(i)));
			element.setAttribute("ID", String.valueOf(i));
		}

		//geneNum
		addChildNode_value("ganeNum", singlepittsburgh, String.valueOf(input.getGeneNum()));

		//objectiveNum
		addChildNode_value("objectiveNum", singlepittsburgh, String.valueOf(input.getObjectiveNum()));

		//Constraint
		double Constraint[] = input.getConstraint();
		if(Constraint == null) {
			addChildNode_value("constraint", singlepittsburgh, "null");
		}else {
			Element constraint = addChildNode("constraint", singlepittsburgh);
			for(int i=0; i<Constraint.length; i++) {
				Element element = addChildNode_value("element", constraint, String.valueOf(input.getConstraint(i)));
				element.setAttribute("ID", String.valueOf(i));
			}
		}

		//feasible
		addChildNode_value("objectiveNum", singlepittsburgh, String.valueOf(input.isFeasible()));

		//rank
		addChildNode_value("rank", singlepittsburgh, String.valueOf(input.getRank()));

		//crowding
		addChildNode_value("crowding", singlepittsburgh, String.valueOf(input.getCrowding()));

		//ruleSet
		SingleRuleSet ruleSet = input.getRuleSet();
		Element singleruleset = addChildNode("ruleSet", singlepittsburgh);
		singleruleset.setAttribute("Class", "SingleRuleSet");

			//micRules
			ArrayList<SingleRule> micRules = ruleSet.getMicRules();
			Element micrules = addChildNode("micRules", singleruleset);

			for(int i=0; i<micRules.size(); i++) {
				SingleRule SingleRule = micRules.get(i);
				Element singlerule = addChildNode("SingleRule", micrules);
				singlerule.setAttribute("ID", String.valueOf(i));
				singlerule.setAttribute("Class", "SingleRule");

				//rule
				int Rule[] = SingleRule.getRule();
				Element rule = addChildNode("rule", singlerule);
				for(int j=0; j<Rule.length; j++) {
					Element element = addChildNode_value("element", rule, String.valueOf(Rule[j]));
					element.setAttribute("ID", String.valueOf(j));
				}

				//conclusion
				addChildNode_value("conclusion", singlerule, String.valueOf(SingleRule.getConc()));

				//cf
				addChildNode_value("cf", singlerule, String.valueOf(SingleRule.getCf()));

				//ruleLength
				addChildNode_value("ruleLength", singlerule, String.valueOf(SingleRule.getRuleLength()));

				//ncp
				addChildNode_value("ncp", singlerule, String.valueOf(SingleRule.getNCP()));

				//nmp
				addChildNode_value("nmp", singlerule, String.valueOf(SingleRule.getNMP()));

				//fitness
				addChildNode_value("fitness", singlerule, String.valueOf(SingleRule.getFitness()));
			}

			//ruleNum
			addChildNode_value("ruleNum", singleruleset, String.valueOf(ruleSet.getRuleNum()));

			//ruleLength
			addChildNode_value("ruleLength", singleruleset, String.valueOf(ruleSet.getRuleLength()));

			//missPatterns
			ArrayList<Integer> missPatterns = ruleSet.getMissPatterns();
			Element misspatterns = addChildNode("missPatterns", singleruleset);
			for(int i=0; i<missPatterns.size(); i++) {
				Element element = addChildNode_value("element", misspatterns, String.valueOf(ruleSet.getMissPattern(i)));
				element.setAttribute("ID", String.valueOf(i));
			}

		//michigan
		Michigan[] Michigan = input.getMichigan();
		Element michigan = addChildNode("michigan", singlepittsburgh);
		for(int i=0; i<Michigan.length; i++) {
			Element element = addChildNode("element", michigan);
			singleruleset.setAttribute("Class", "Michigan");
			Rule Rule = Michigan[i].getRule();

			//rule
			int Rules[] = Rule.getRule();
			Element rule = addChildNode("rule", element);
			for(int j=0; j<Rules.length; j++) {
				Element element_sub = addChildNode_value("element", rule, String.valueOf(Rules[j]));
				element_sub.setAttribute("ID", String.valueOf(j));
			}

			//conclusion
			addChildNode("conclusion", element);

			//cf
			addChildNode_value("cf", element, String.valueOf(Rule.getCf()));

			//ruleLength
			addChildNode_value("ruleLength", element, String.valueOf(Rule.getRuleLength()));

			//ncp
			addChildNode_value("ncp", element, String.valueOf(Rule.getNCP()));

			//nmp
			addChildNode_value("nmp", element, String.valueOf(Rule.getNMP()));

			//fitness
			addChildNode_value("fitness", element, String.valueOf(Rule.getFitness()));
		}

		//ruleNum
		addChildNode_value("ruleNum", singlepittsburgh, String.valueOf(input.getRuleNum()));

		//Ndim
		addChildNode_value("Ndim", singlepittsburgh, String.valueOf(input.getNdim()));

		//Appendix
		double Appendix[] = input.getAppendix();
		Element appendix = addChildNode("appendix", singlepittsburgh);
		for(int i=0; i<Appendix.length; i++) {
			Element element = addChildNode_value("element", appendix, String.valueOf(input.getAppendix(i)));
			element.setAttribute("ID", String.valueOf(i));
		}

	}

	public void KBtoXML(KB kb) {
		//全体
		Element master=document.getDocumentElement();

		FuzzySet[][] FSs = kb.getFSs();
		Element fss = addChildNode("FSs", master);

		for(int i=0; i<FSs.length; i++) {
			FuzzySet FS[] = FSs[i];
			Element fs = addChildNode("FS", fss);
			fs.setAttribute("dimension", String.valueOf(i));
			for(int j=0; j<FS.length; j++) {
				Element ft = addChildNode("FuzzyTerm", fs);
				FuzzySet vv = FSs[i][j];
				//name_fuzzytermtype
				addChildNode_value("name", ft, vv.getName());
				//type
				addChildNode_value("Shape_Type_ID", ft, String.valueOf(vv.getShapeType()));
				String ShapeName = null;
				switch(vv.getShapeType()) {
					case 0: ShapeName = "rightLinearShape"; break;
					case 1: ShapeName = "leftLinearShape"; break;
					case 2: ShapeName = "piShape"; break;
					case 3: ShapeName = "triangularShape"; break;
					case 4: ShapeName = "gaussianShape"; break;
					case 5: ShapeName = "rightGaussianShape"; break;
					case 6: ShapeName = "leftGaussianShape"; break;
					case 7: ShapeName = "trapezoidShape"; break;
					case 8: ShapeName = "singletonShape"; break;
					case 9: ShapeName = "rectangularShape"; break;
					case 10: ShapeName = "zShape"; break;
					case 11: ShapeName = "sShape"; break;
					case 12: ShapeName = "pointSetShape"; break;
					case 13: ShapeName = "pointSetMonotonicShape"; break;
					case 14: ShapeName = "circularDefinition"; break;
					case 15: ShapeName = "customShape"; break;
					case 16: ShapeName = "customMonotonicShape"; break;
				}
				addChildNode_value("Shape_Type", ft, ShapeName);
				//parameters
				FuzzyTermType ftt = vv.getTerm();
				float[] param = ftt.getParam();
				Element params = addChildNode("parameters", ft);
				for(int k=0; k<param.length; k++) {
					Element parameter = document.createElement("parameter");
					parameter.setAttribute("id", String.valueOf(k));
					Text textContents = document.createTextNode(String.valueOf(param[k]));
					parameter.appendChild(textContents);
					params.appendChild(parameter);
				}
			}
		}

	}

	public void RuleSetToXML(Result_MoFGBML input) {

		//全体
		Element master=document.getDocumentElement();

		for(int trial_i=0; trial_i<input.getResult().size(); trial_i++) {
			Element trial = addChildNode("trial", master);
			Result_trial Trial = input.getResultTrial(trial_i);

			for(int generation_j=0; generation_j<Trial.getResult().size(); generation_j++) {
				//出力を最終世代に限定
				generation_j = Trial.getResult().size() - 1;

				Element pop = addChildNode("population", trial);
				Result_population Pop = Trial.getResultPopulation(generation_j);
				pop.setAttribute("generation", String.valueOf(Pop.getGen()));
				pop.setAttribute("trial", String.valueOf(Trial.getTrial()));


				KB kb = Pop.getKb();

				FuzzySet[][] FSs = kb.getFSs();
				Element fss = addChildNode("KnowledgeBase", pop);
				fss.setAttribute("generation", String.valueOf(Pop.getGen()));
				fss.setAttribute("trial", String.valueOf(Trial.getTrial()));

				for(int FuzzySetDim_k=0; FuzzySetDim_k<FSs.length; FuzzySetDim_k++) {
					FuzzySet FS[] = FSs[FuzzySetDim_k];
					Element fs = addChildNode("FuzzySet", fss);
					fs.setAttribute("dimension", String.valueOf(FuzzySetDim_k));
					for(int FuzzySet_l=0; FuzzySet_l<FS.length; FuzzySet_l++) {
						Element ft = addChildNode("FuzzyTerm", fs);
						ft.setAttribute("ID", String.valueOf(FuzzySet_l));
						FuzzySet vv = FSs[FuzzySetDim_k][FuzzySet_l];
						//name_fuzzytermtype
						addChildNode_value("name", ft, vv.getName());
						//type
						addChildNode_value("Shape_Type_ID", ft, String.valueOf(vv.getShapeType()));
						String ShapeName = null;
						switch(vv.getShapeType()) {
							case 0: ShapeName = "rightLinearShape"; break;
							case 1: ShapeName = "leftLinearShape"; break;
							case 2: ShapeName = "piShape"; break;
							case 3: ShapeName = "triangularShape"; break;
							case 4: ShapeName = "gaussianShape"; break;
							case 5: ShapeName = "rightGaussianShape"; break;
							case 6: ShapeName = "leftGaussianShape"; break;
							case 7: ShapeName = "trapezoidShape"; break;
							case 8: ShapeName = "singletonShape"; break;
							case 9: ShapeName = "rectangularShape"; break;
							case 10: ShapeName = "zShape"; break;
							case 11: ShapeName = "sShape"; break;
							case 12: ShapeName = "pointSetShape"; break;
							case 13: ShapeName = "pointSetMonotonicShape"; break;
							case 14: ShapeName = "circularDefinition"; break;
							case 15: ShapeName = "customShape"; break;
							case 16: ShapeName = "customMonotonicShape"; break;
							case 99: ShapeName = "DontCare"; break;
						}
						addChildNode_value("Shape_Type", ft, ShapeName);
						//parameters
						FuzzyTermType ftt = vv.getTerm();
						float[] param = ftt.getParam();
						Element params = addChildNode("parameters", ft);
						for(int m=0; m<param.length; m++) {
							Element parameter = document.createElement("parameter");
							parameter.setAttribute("id", String.valueOf(m));
							Text textContents = document.createTextNode(String.valueOf(param[m]));
							parameter.appendChild(textContents);
							params.appendChild(parameter);
						}
						if(vv.getPartitonNum() >= 0)addChildNode_value("PartitionNum", ft, String.valueOf(vv.getPartitonNum()));
//						addChildNode_value("weight", ft, String.valueOf(vv.getWeight()));
					}
				}

				int popSize = Pop.getResult().size();
				for(int individual_n=0; individual_n<popSize; individual_n++) {

					Result_individual Individual = Pop.getIindividual(individual_n);
					SinglePittsburgh singlePittsburg = Individual.getRule();

					Element individual = addChildNode("individual", pop);
					individual.setAttribute("generation", String.valueOf(Pop.getGen()));
					individual.setAttribute("trial", String.valueOf(Trial.getTrial()));
					individual.setAttribute("ruleNum", String.valueOf(singlePittsburg.getRuleNum()));

					for(int l=0; l<Individual.getF().size(); l++) {
						addChildNode_value("f"+String.valueOf(l), individual, String.valueOf(Individual.getF().get(l)));
					}

					//rank
//					addChildNode_value("rank", individual, String.valueOf(singlePittsburg.getRank()));

					//crowding
//					addChildNode_value("crowding", individual, String.valueOf(singlePittsburg.getCrowding()));

					//ruleSet
					SingleRuleSet ruleSet = singlePittsburg.getRuleSet();
					Element singleruleset = addChildNode("ruleSet", individual);

					//micRules
					ArrayList<SingleRule> micRules = ruleSet.getMicRules();

					for(int l=0; l<micRules.size(); l++) {
						SingleRule SingleRule = micRules.get(l);
						Element singlerule = addChildNode("SingleRule", singleruleset);
						singlerule.setAttribute("ID", String.valueOf(l));

						//rule
						int Rule[] = SingleRule.getRule();
						Element rule = addChildNode("rule", singlerule);
						for(int m=0; m<Rule.length; m++) {
							Element element = addChildNode_value("element", rule, String.valueOf(Rule[m]));
							element.setAttribute("ID", String.valueOf(m));
						}

						//conclusion
						addChildNode_value("conclusion", singlerule, String.valueOf(SingleRule.getConc()));

						//cf
						addChildNode_value("cf", singlerule, String.valueOf(SingleRule.getCf()));

						//fitness
						addChildNode_value("fitness", singlerule, String.valueOf(SingleRule.getFitness()));
					}
				}
			}
		}
	}


	public void ResultToXML(Result_MoFGBML input) {
		//全体
		Element master=document.getDocumentElement();

		for(int trial_i=0; trial_i<input.getResult().size(); trial_i++) {
			Element trial = addChildNode("trial", master);
			Result_trial Trial = input.getResultTrial(trial_i);
			trial.setAttribute("trial", String.valueOf(Trial.getTrial()));

			for(int population_j=0; population_j<Trial.getResult().size(); population_j++) {
				//出力を最終世代に限定
				population_j = Trial.getResult().size() - 1;
				Element pop = addChildNode("population", trial);
				Result_population Pop = Trial.getResultPopulation(population_j);
				pop.setAttribute("ID", String.valueOf(population_j));
				pop.setAttribute("generation", String.valueOf(Pop.getGen()));
				pop.setAttribute("trial", String.valueOf(Trial.getTrial()));


				int popSize = Pop.getResult().size();
				for(int individual_k=0; individual_k<popSize; individual_k++) {

					Result_individual Individual = Pop.getIindividual(individual_k);
					Element individual = addChildNode("individual", pop);
					individual.setAttribute("ID", String.valueOf(individual_k));
					individual.setAttribute("generation", String.valueOf(Pop.getGen()));
					individual.setAttribute("trial", String.valueOf(Trial.getTrial()));

					for(int l=0; l<Individual.getF().size(); l++) {
						addChildNode_value("f"+String.valueOf(l), individual, String.valueOf(Individual.getF().get(l)));
					}

					addChildNode_value("Dtra", individual, String.valueOf(Individual.getDtra()));

					addChildNode_value("Dtst", individual, String.valueOf(Individual.getDtst()));

					addChildNode_value("ruleNum", individual, String.valueOf(Individual.getRuleNum()));

					addChildNode_value("ruleLength", individual, String.valueOf(Individual.getRuleLength()));

//					addChildNode_value("rank", individual, String.valueOf(Individual.getRank()));

//					addChildNode_value("crowding", individual, String.valueOf(Individual.getCrowding()));

				}
			}
		}
	}


	public void classifyResultToXML(Result_dataset input) {
		//全体
		Element master=document.getDocumentElement();

		for(int trial_i=0; trial_i<input.getTrialNum(); trial_i++) {
			Element trial = addChildNode("trial", master);
			trial.setAttribute("trial", String.valueOf(trial_i));
			//データセット
			Element dataset_node = addChildNode("dataseet", trial);
			SingleDataSetInfo Dtst = input.getDtst().get(trial_i);
			//データセットの各パターン
			for(SinglePattern pattern: Dtst.getPatterns()) {
				Element pattern_node = addChildNode("pattern", dataset_node);
				pattern_node.setAttribute("ID", String.valueOf(pattern.getID()));
				//各属性値
				for(int dim_i=0; dim_i<pattern.getX().length; dim_i++) {
					Element attribute_node = addChildNode_value("attribute", pattern_node, String.valueOf(pattern.getDimValue(dim_i)));
					attribute_node.setAttribute("dim", String.valueOf(dim_i));
				}
			}
			//識別結果
			Element classifyResult_node = addChildNode("classifyResult", trial);
			for(int individual_i=0; individual_i<input.getClassifyResult().get(trial_i).size(); individual_i++) {
				//個体別
				Element individual_node = addChildNode("individual", classifyResult_node);
				individual_node.setAttribute("ID", String.valueOf(individual_i));
				//各パターン
				for(int pattern_i=0; pattern_i<input.getClassifyResult().get(trial_i).get(individual_i).length; pattern_i++) {
					int[] classifyResult_tmp = input.getClassifyResult().get(trial_i).get(individual_i)[pattern_i];
					Element pattern_node = addChildNode("pattern", individual_node);
					pattern_node.setAttribute("patternID", String.valueOf(pattern_i));
					addChildNode_value("classifiedClass", pattern_node, String.valueOf(classifyResult_tmp[0]));
					addChildNode_value("classifiedResult", pattern_node, String.valueOf(classifyResult_tmp[1]));
				}
			}
		}
	}

//	public void RBtoXML(RB rb) throws Exception{
//		KB kb_buf = rb.kb;
//		//全体
//		Element master=document.getDocumentElement();
//
//		/**kb**/
//		Element kb = addChildNode("KB", master);
//
//		//name_kb
//		addChildNode_value("name", kb, kb_buf.name);
//
//		//DomainLeft
//		addChildNode_value("DomainLeft", kb, String.valueOf(kb_buf.DomainLeft));
//
//		//DomainRight
//		addChildNode_value("DomainRight", kb, String.valueOf(kb_buf.DomainRight));
//		//fuzzyvariabletype"s" ファジィ変数集合(次元別分割集合)
//		Element fuzzyvariabletypes = addChildNode("fuzzyvariabletypes", kb);
//		for(int i=0; i<kb_buf.getFuzzyVariableType_ArrayList().size(); i++) {
//			//fuzzyvariabletype ファジィ変数集合(次元別)
//			Element fuzzyvariabletype = addChildNode("fuzzyvariabletype", fuzzyvariabletypes);
//			fuzzyvariabletype.setAttribute("id", String.valueOf(i));
//
//			//個々のファジィ変数
//			List<FuzzyTermType> vv = kb_buf.getFuzzyVariableType(i).getTerms();
//			for(int j=0; j<vv.size(); j++) {
//				Element fuzzytermtype = addChildNode("fuzzytermtype", fuzzyvariabletype);
//				fuzzytermtype.setAttribute("id", String.valueOf(j));
//				//name_fuzzytermtype
//				addChildNode_value("name", fuzzytermtype, vv.get(j).getName());
//				//type
//				addChildNode_value("Shape_Type_ID", fuzzytermtype, String.valueOf(vv.get(j).getType()));
//				String ShapeName = null;
//				switch(vv.get(j).getType()) {
//					case 0: ShapeName = "rightLinearShape"; break;
//					case 1: ShapeName = "leftLinearShape"; break;
//					case 2: ShapeName = "piShape"; break;
//					case 3: ShapeName = "triangularShape"; break;
//					case 4: ShapeName = "gaussianShape"; break;
//					case 5: ShapeName = "rightGaussianShape"; break;
//					case 6: ShapeName = "leftGaussianShape"; break;
//					case 7: ShapeName = "trapezoidShape"; break;
//					case 8: ShapeName = "singletonShape"; break;
//					case 9: ShapeName = "rectangularShape"; break;
//					case 10: ShapeName = "zShape"; break;
//					case 11: ShapeName = "sShape"; break;
//					case 12: ShapeName = "pointSetShape"; break;
//					case 13: ShapeName = "pointSetMonotonicShape"; break;
//					case 14: ShapeName = "circularDefinition"; break;
//					case 15: ShapeName = "customShape"; break;
//					case 16: ShapeName = "customMonotonicShape"; break;
//				}
//				addChildNode_value("Shape_Type", fuzzytermtype, ShapeName);
//				//parameters
//				float[] param = vv.get(j).getParam();
//				Element params = addChildNode("parameters", fuzzytermtype);
//				for(int k=0; k<param.length; k++) {
//					Element parameter = document.createElement("parameter");
//					parameter.setAttribute("id", String.valueOf(k));
//					Text textContents = document.createTextNode(String.valueOf(param[k]));
//					parameter.appendChild(textContents);
//					params.appendChild(parameter);
//				}
//			}
//		}
//		//rule_num
//		addChildNode_value("RuleNumber", master, String.valueOf(rb.rule_num));
//		//dim
//		addChildNode_value("Dimension", master, String.valueOf(rb.dim));
//		//rules
//		Element rules = addChildNode("Rules", master);
//		ArrayList<Rule> Rules = rb.getRule_ArrayList();
//		for(int i=0; i<Rules.size(); i++) {
//			Rule rule_buf = Rules.get(i);
//			Element rule = addChildNode("Rule", rules);
//			rule.setAttribute("id", String.valueOf(i));
//			rule.setAttribute("DimensionNumber", String.valueOf(rb.dim));
//			rule.setAttribute("RuleNumber", String.valueOf(rb.rule_num));
//			addChildNode_value("Weight", rule, String.valueOf(rule_buf.getCf()));
//			//EA
//			ArrayList<EvaluateAttribute> eas = rule_buf.getEvaluateAttribute();
//			Element EAs = addChildNode("EavaluateAttributes", rule);
//			for(int j=0; j<eas.size(); j++) {
//				EvaluateAttribute ea = eas.get(j);
//				FuzzyTermType FTT = (FuzzyTermType)ea.getFuzzyTerm();
//				Element EA = addChildNode("FuzzyTerm", EAs);
//				EA.setAttribute("Dimension", String.valueOf(j));
//				//ID of fuzzyterm in kb
//				addChildNode_value("FuzzyTermID", EA, String.valueOf(kb_buf.findID(j, ea.getFuzzyTerm().getName())));
//				//name_fuzzytermtype
//				addChildNode_value("name", EA, ea.getFuzzyTerm().getName());
//				//type
//				addChildNode_value("Shape_Type_ID", EA, String.valueOf(ea.getFuzzyTerm().getType()));
//				String ShapeName = null;
//				switch(ea.getFuzzyTerm().getType()) {
//					case 0: ShapeName = "rightLinearShape"; break;
//					case 1: ShapeName = "leftLinearShape"; break;
//					case 2: ShapeName = "piShape"; break;
//					case 3: ShapeName = "triangularShape"; break;
//					case 4: ShapeName = "gaussianShape"; break;
//					case 5: ShapeName = "rightGaussianShape"; break;
//					case 6: ShapeName = "leftGaussianShape"; break;
//					case 7: ShapeName = "trapezoidShape"; break;
//					case 8: ShapeName = "singletonShape"; break;
//					case 9: ShapeName = "rectangularShape"; break;
//					case 10: ShapeName = "zShape"; break;
//					case 11: ShapeName = "sShape"; break;
//					case 12: ShapeName = "pointSetShape"; break;
//					case 13: ShapeName = "pointSetMonotonicShape"; break;
//					case 14: ShapeName = "circularDefinition"; break;
//					case 15: ShapeName = "customShape"; break;
//					case 16: ShapeName = "customMonotonicShape"; break;
//				}
//				addChildNode_value("Shape_Type", EA, ShapeName);
//				//parameters
//				float[] param = FTT.getParam();
//				Element params = addChildNode("parameters", EA);
//				for(int k=0; k<param.length; k++) {
//					Element parameter = document.createElement("parameter");
//					parameter.setAttribute("id", String.valueOf(k));
//					Text textContents = document.createTextNode(String.valueOf(param[k]));
//					parameter.appendChild(textContents);
//					params.appendChild(parameter);
//				}
//			}
//		}
//		addChildNode_value("RuleNumber", rules, String.valueOf(rb.rule_num));
//	}

	/**
	 * 親ノードに子ノードを追加する．生成された子ノードを返す
	 * @param node_name 生成したい子ノードの名
	 * @param parent 追加先の親ノード
	 * @return 生成された子ノード
	 */
	protected Element addChildNode(String node_name, Element parent) {
		Element v = document.createElement(node_name);
		parent.appendChild(v);
		return v;
	}

	/**
	 * 親ノードに子ノードを追加する. 値を追加
	 * @param node_name 生成したい子ノードの名
	 * @param parent 追加先の親ノード
	 * @param value 要素の持つ値
	 */
	protected Element addChildNode_value(String node_name, Element parent, String value) {
		Element v = document.createElement(node_name);
		Text textContents = document.createTextNode(value);
		v.appendChild(textContents);
		parent.appendChild(v);
		return v;
	}

	/**
	 * 書き込まれたdocファイルをxml形式に書き出す
	 *
	 * @throws FileNotFoundException
	 * @throws TransformerException
	 */
	public void output() throws FileNotFoundException, TransformerException {
		DOMSource source = new DOMSource(document);
		String FileName = null;
		FileName = name_xml + ".xml";
		File newXML = new File(FileName);
		FileOutputStream os = new FileOutputStream(newXML);
		StreamResult result = new StreamResult(os);
		transformer.transform(source, result);
	}
	/**
	 * 書き込まれたdocファイルをxml形式に書き出す
	 * ファイル名指定可能(.xml拡張子ついてても，ついてなくてもOK)
	 * @param FileName ファイル名
	 * @throws FileNotFoundException
	 * @throws TransformerException
	 */
	public void output(String FileName, String file_dir) throws FileNotFoundException, TransformerException {
		if(!(FileName.endsWith(".xml"))) {
			FileName = FileName +".xml";
		}
//		String path = new File(".").getAbsoluteFile().getParent();
//		FileName = path + "\\xml\\" + FileName;
		String sep = File.separator;
		FileName = file_dir + sep + FileName;
		File newXML = new File(FileName);
		DOMSource source = new DOMSource(document);
		FileOutputStream os = new FileOutputStream(newXML);
		StreamResult result = new StreamResult(os);
		transformer.transform(source, result);
	}


}
