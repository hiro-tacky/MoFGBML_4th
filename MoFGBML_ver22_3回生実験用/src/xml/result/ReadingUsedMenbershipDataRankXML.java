package xml.result;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ReadingUsedMenbershipDataRankXML {
	/**
	 * dataset_UsedMenbershipDataRank.xmlを読み込む
	 *
	 * @param xmlPath xmlファイルのパス
	 * @throws Exception
	 */

	public ArrayList<ArrayList<HashMap<String, Integer>>> rank_name = new ArrayList<ArrayList<HashMap<String, Integer>>>();
	public ArrayList<ArrayList<HashMap<String, HashMap<Integer, Integer>>>> rank_partitionNum = new ArrayList<ArrayList<HashMap<String, HashMap<Integer, Integer>>>>();

	public ReadingUsedMenbershipDataRankXML(String xmlPath) throws Exception {
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
			int trial_id = Integer.parseInt(trial.getAttribute("ID"));
			NodeList fuzzyTermList_dim = trial.getChildNodes();

			rank_name.add(new ArrayList<HashMap<String, Integer>>());
			rank_partitionNum.add(new ArrayList<HashMap<String, HashMap<Integer, Integer>>>());

			//各世代の個体群を求める
			for(int dim_i = 0; dim_i < fuzzyTermList_dim.getLength(); dim_i++) {
				// 7. trial要素をElementにキャストする
				Element fuzzyTerm_dim = (Element) fuzzyTermList_dim.item(dim_i);
				int dimension_id = Integer.parseInt(fuzzyTerm_dim.getAttribute("dimension"));
				NodeList fuzzyTermList_name = fuzzyTerm_dim.getChildNodes();

				rank_name.get(trial_id).add(new HashMap<String, Integer>());
				rank_partitionNum.get(trial_id).add(new HashMap<String, HashMap<Integer, Integer>>());
				for (int name_i=0; name_i<fuzzyTermList_name.getLength(); name_i++) {
					Element fuzzyTerm_name = (Element) fuzzyTermList_name.item(name_i);
					NodeList fuzzyTermList_partitionNum = fuzzyTerm_name.getChildNodes();

					String name_tmp = fuzzyTerm_name.getAttribute("name");
					int rank_name_tmp = Integer.parseInt(fuzzyTerm_name.getAttribute("rank"));

					rank_name.get(trial_id).get(dimension_id).put(name_tmp, rank_name_tmp);
					rank_partitionNum.get(trial_id).get(dimension_id).put(name_tmp, new HashMap<Integer, Integer>());

					for (int partitionNum_i=0; partitionNum_i<fuzzyTermList_partitionNum.getLength(); partitionNum_i++) {
						Element fuzzyTerm_partitionNum = (Element) fuzzyTermList_partitionNum.item(partitionNum_i);

						int partitionNum_tmp = Integer.parseInt(fuzzyTerm_partitionNum.getAttribute("partitionNum"));
						int rankPartitionNum_tmp = Integer.parseInt(fuzzyTerm_partitionNum.getAttribute("rank"));
						rank_partitionNum.get(trial_id).get(dimension_id).get(name_tmp).put(partitionNum_tmp, rankPartitionNum_tmp);
					}
				}
			}
		}
	}

	public int getRank_name(int trial_id, int dim_id, String name) {
		return rank_name.get(trial_id).get(dim_id).get(name);
	}

	public int getRank_partitionNum(int trial_id, int dim_id, String name, int partitionNum) {
		return rank_partitionNum.get(trial_id).get(dim_id).get(name).get(partitionNum);
	}

	public ArrayList<String> getRankTop_name(int trial_id, int dim_id, int rank) {
		ArrayList<String> buf = new ArrayList<String>();
		for (Entry<String, Integer> entry : rank_name.get(trial_id).get(dim_id).entrySet()) {
			if(entry.getValue() <= rank) {
				buf.add(entry.getKey());
			}
		}
		return buf;
	}

	public ArrayList<nameAndPartitionNum_struct> getRankTop_partitionNum(int trial_id, int dim_id, int rank) {
		ArrayList<nameAndPartitionNum_struct> buf = new ArrayList<nameAndPartitionNum_struct>();
		HashMap<String, HashMap<Integer, Integer>> tmp = rank_partitionNum.get(trial_id).get(dim_id);
		for (Iterator<String> iterator = tmp.keySet().iterator(); iterator.hasNext(); ) {
			String name = iterator.next();
			for(Entry<Integer, Integer> entry : tmp.get(name).entrySet()) {
				if(entry.getValue() <= rank) {
					buf.add(new nameAndPartitionNum_struct(name, entry.getKey()));
				}
			}
		}
		return buf;
	}

	final class nameAndPartitionNum_struct{
		public String name;
		public int partitionNum;
		nameAndPartitionNum_struct(String name, int partitionNum){
			this.name = name;
			this.partitionNum = partitionNum;
		}
		public String getName() {
			return name;
		}
		public int getPartitionNum() {
			return partitionNum;
		}
	}
}
