package xml;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fuzzy.fml.FuzzySet;
import fuzzy.fml.KB;

public class ExperimentSetMaster {
	@SuppressWarnings("rawtypes")
	/**
	 * ExperimentSet[trial][generation]
	 * generation は世代数．indexにはgeneration2indexで変換．違うことに注意
	 * setter, getter
	 */
	public ArrayList<ArrayList<ExperimentSet>> experimentSet = new ArrayList<ArrayList<ExperimentSet>>();
	public Map<Short, Short> generation2index = new HashMap<Short, Short>();

	/**
	 * dataset_ruleset.xmlを読み込む
	 *
	 * @param xmlPath xmlファイルのパス
	 * @throws Exception
	 */
	public ExperimentSetMaster(String xmlPath) throws Exception {
		System.out.println(xmlPath);
		// 1. DocumentBuilderFactoryのインスタンスを取得する
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// 2. DocumentBuilderのインスタンスを取得する
		DocumentBuilder builder = factory.newDocumentBuilder();
		// 3. DocumentBuilderにXMLを読み込ませ、Documentを作る
		System.out.println(Paths.get(xmlPath).toFile());
		Document document = builder.parse(Paths.get(xmlPath).toFile());
		// 4. Documentから、ルート要素を取得する
		Element root = document.getDocumentElement();
		// 5. root配下にある、population要素を取得する
		//trial ID は 0 スタート
		NodeList trials = root.getChildNodes();

		// 6. 取得したtrial要素でループする
		for (int trial_i = 0; trial_i < trials.getLength(); trial_i++) {
			// 7. trial要素をElementにキャストする
			Element trial = (Element) trials.item(trial_i);
			if(trial.getTagName() != "trial") {continue;}

			//各世代の個体群を求める
			NodeList populations = trial.getChildNodes();
			for(int population_i = 0; population_i < populations.getLength(); population_i++) {
				// 7. trial要素をElementにキャストする
				Element population = (Element) populations.item(population_i);
				if(population.getTagName() != "population") {continue;}
				int trial_id = Integer.parseInt(population.getAttribute("trial")),
						generation_id = Integer.parseInt(population.getAttribute("generation"));
				this.makeExperimentSet(trial_id, generation_id);
				Element knowledgeBase = (Element)population.getFirstChild();
				if(knowledgeBase.getTagName() == "KnowledgeBase") {
					this.getExperimentSet(trial_id, generation_id).setKb(this.XMLtoKB(knowledgeBase));
				}else {
					System.out.println("knowledgeBaseノードの探査に失敗");
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private void makeExperimentSet(int trial, int generation) {
		//trial の追加
		while(experimentSet.size() < trial+1) {
			experimentSet.add(new ArrayList<ExperimentSet>());
		}
		//generation の追加
		//generation のindex管理
		if(!generation2index.containsKey((short)generation)) {
			generation2index.put((short)generation, (short)generation2index.size());
		}
		//generation
		while(experimentSet.get(trial).size() < generation2index.size()) {
			experimentSet.get(trial).add(null);
		}
		experimentSet.get(trial).set(generation2index.get((short)generation), new ExperimentSet(trial, generation));
	}

	public int getGeneration2index(int generation) {
		if(!generation2index.containsKey((short)generation)) {
			generation2index.put((short)generation, (short)generation2index.size());
		}
		return generation2index.get((short)generation);
	}

	public void setGeneration2index(int generation) {
		if(!generation2index.containsKey((short)generation)) {
			generation2index.put((short)generation, (short)generation2index.size());
		}
	}

	@SuppressWarnings("rawtypes")
	private void setExperimentSet(int trial, int generation, ExperimentSet experimentSet_input) {
		//trial の追加
		while(experimentSet.size() <= trial+1) {
			experimentSet.add(new ArrayList<ExperimentSet>());
		}
		//generation の追加
		//generation のindex管理
		if(!generation2index.containsKey((short)generation)) {
			generation2index.put((short)generation, (short)generation2index.size());
		}
		//generation
		while(experimentSet.get(trial).size() <= generation2index.size()) {
			experimentSet.get(trial).add(null);
		}
		experimentSet.get(trial).set(generation2index.get((short)generation), experimentSet_input);
	}

	@SuppressWarnings("rawtypes")
	private ExperimentSet getExperimentSet(int trial, int generation) {
		return experimentSet.get(trial).get(generation2index.get((short)generation));
	}

	private void KBtoXML(KB kb) {

	}
	private KB XMLtoKB(Element knowledgeBase) {
		KB kb = new KB();
		NodeList fuzzySets = knowledgeBase.getChildNodes();
		FuzzySet[][] FSs = new FuzzySet[fuzzySets.getLength()][];

		for(int fuzzySet_i = 0; fuzzySet_i<fuzzySets.getLength(); fuzzySet_i++) {
			Element FuzzySet = (Element) fuzzySets.item(fuzzySet_i);
			if(FuzzySet.getTagName() != "FuzzySet") {continue;}

			NodeList FuzzyTerms = FuzzySet.getChildNodes();
			int dimension = Integer.parseInt(FuzzySet.getAttribute("dimension"));
			FSs[dimension] = new FuzzySet[FuzzyTerms.getLength()];

			for(int FuzzyTerm_i = 0; FuzzyTerm_i<FuzzyTerms.getLength(); FuzzyTerm_i++) {
				Element FuzzyTerm = (Element) FuzzyTerms.item(FuzzyTerm_i);
				if(FuzzyTerm.getTagName() != "FuzzyTerm") {continue;}

				int FuzzyTerm_id = Integer.parseInt(FuzzyTerm.getAttribute("ID"));

				String name = null;
				int Shape_Type_ID = -1, partitionNum = -1;
				float[] param = null;
				NodeList FuzzyTerm_ChildrenNodes = FuzzyTerm.getChildNodes();
				for(int FuzzyTermChildNode_i = 0; FuzzyTermChildNode_i<FuzzyTerm_ChildrenNodes.getLength(); FuzzyTermChildNode_i++) {
					Element FuzzyTermChildNode = (Element) FuzzyTerm_ChildrenNodes.item(FuzzyTermChildNode_i);
					switch(FuzzyTermChildNode.getTagName()) {
						case "name":
							name = FuzzyTermChildNode.getTextContent();
							break;
						case "Shape_Type_ID":
							Shape_Type_ID = Integer.parseInt(FuzzyTermChildNode.getTextContent());
							if(Shape_Type_ID == 99) {Shape_Type_ID = 9;} //旧バージョン対応(Dontcareが99になっている為)
							break;
						case "PartitionNum":
							partitionNum = Integer.parseInt(FuzzyTermChildNode.getTextContent());
							break;
						case "parameters":
							//parameters取得
							NodeList parameters = FuzzyTermChildNode.getChildNodes();
							param = new float[parameters.getLength()];
							for(int parameter_i=0; parameter_i<parameters.getLength(); parameter_i++) {
								Element parameter = (Element) parameters.item(parameter_i);
								param[Integer.parseInt(parameter.getAttribute("id"))] = Float.parseFloat(parameter.getTextContent());
							}
							break;
					}
				}
				if(!name.isEmpty() && Shape_Type_ID >= 0 && !param.equals(null)){
					FSs[dimension][FuzzyTerm_id] = new FuzzySet(name, Shape_Type_ID, param);
					FSs[dimension][FuzzyTerm_id].setPartitonNum(partitionNum);
				}else {
					System.out.println("xmlによるKB構築に失敗");
				}

				if(!FSs.equals(null)) {kb.setFSs(FSs); }else{ System.out.println("KBがnull");}
			}
		}
		return kb;
	}

}
