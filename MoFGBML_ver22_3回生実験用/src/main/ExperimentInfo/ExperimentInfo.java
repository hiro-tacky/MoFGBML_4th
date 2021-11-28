package main.ExperimentInfo;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;


/**
 * 比較実験用に変更するパラメータを保持する．
 *
 * コマンドライン引数として，
 * カレントディレクトリ
 * setting.propertiesのファイル名
 * を与える．<br>
 * <br>
 * 例: > Java -jar xx.jar ./ setting<br>
 * <br>
 * setSettings()メソッドに，以上の引数を与えてプロパティファイルを読み込む．
 *
 *
 */
public class ExperimentInfo {
	/* ********************************************************* */
	/** Do use command line argument for Below parameters<br>
	 *  true:use, false:properties file */
	public static boolean useArgs = true;
	/** Dataset Name */
	public static String dataName = "iris";
	/** Place of stored results */
	public static String saveDir = "results";

	/** 一時的 **/
	public static String resultRoot;

	/** FuzzySetType <br>
	* 99:multi 3:triangle 4:gaussian 7:trapezoid 9:rectangle */
	public static int FuzzySetType = 99;

	/** 使用中のファジィセットの名前 */
	public static String FuzzyTypeName = "multi";

	/** #of Population */
	public static int populationSize = 50;
	/** #of Offspring */
	public static int offspringSize = 50;

	/** num of experimentInfoSet */
	public static int experimentInfoSetNum = 5;

    /**
     * <h1>ファジィ集合の初期化方法</h1>
     * 0: 2-5分割 homogeneous triangle fuzzy partitions.<br>
     * 1: Input XML file<br>
     * 2: Inhomogeneous<br>
     */
	public static int FUZZY_SET_INITIALIZE = 0;

	/** 比較実験のセットのリスト．experimentInfoSet()のswitch関数のindexに対応 */
	public static int[] experimentInfoSetList = {7};

	/** 比較実験のセットのリストから実行中の実験セットの"リスト"のindex．最初からやる場合は-1に設定*/
	public static int experimentInfoSetListIndex = -1;

	/** 実験中のセットのID．experimentInfoSet()のswitch関数のindexに対応 */
	public static int experimentInfoSetListID = 0;

	public static ArrayList<designedFuzzySet> designedFuzzySet = new ArrayList<designedFuzzySet>();

	private static String sep = File.separator;

	public static ArrayList<Integer> FUZZY_SET_NUM = new ArrayList<Integer>();			//ファジィ集合の種類数

	public static String XML_path;

	public static int dataRankNum;
	// ************************************************************


	/**
	 * 引数に応じた実験設定に変更する<br>
	 * 設定した実験セットのindexをexperimentInfoSetListに入れると実行される．
	 * @param index
	 */
	public static void experimentInfoSet(int index){
		designedFuzzySet buf;
		switch (index) {
		case 0:
			ExperimentInfo.setSaveDir("default" + sep + "triangle");
			ExperimentInfo.setFuzzySetType(3);
			ExperimentInfo.setFuzzyTypeName("triangle");
			ExperimentInfo.FUZZY_SET_INITIALIZE = 0;
			break;

		case 1:
			ExperimentInfo.setSaveDir("entropy" + sep + "triangle");
			ExperimentInfo.setFuzzySetType(3);
			ExperimentInfo.setFuzzyTypeName("triangle");
			ExperimentInfo.FUZZY_SET_INITIALIZE = 2;
			break;

		case 2:
			ExperimentInfo.setSaveDir("default" + sep + "rectangle");
			ExperimentInfo.setFuzzySetType(9);
			ExperimentInfo.setFuzzyTypeName("rectangle");
			ExperimentInfo.FUZZY_SET_INITIALIZE = 0;
			break;

		case 3:
			ExperimentInfo.setSaveDir("entropy" + sep + "rectangle");
			ExperimentInfo.setFuzzySetType(9);
			ExperimentInfo.setFuzzyTypeName("rectangle");
			ExperimentInfo.FUZZY_SET_INITIALIZE = 2;
			break;

		case 4:
			ExperimentInfo.setSaveDir("default" + sep + "gaussian");
			ExperimentInfo.setFuzzySetType(4);
			ExperimentInfo.setFuzzyTypeName("gaussian");
			ExperimentInfo.FUZZY_SET_INITIALIZE = 0;
			break;

		case 5:
			ExperimentInfo.setSaveDir("entropy" + sep + "gaussian");
			ExperimentInfo.setFuzzySetType(4);
			ExperimentInfo.setFuzzyTypeName("gaussian");
			ExperimentInfo.FUZZY_SET_INITIALIZE = 2;
			break;

		case 6:
			ExperimentInfo.setSaveDir("default" + sep + "multi");
			ExperimentInfo.setFuzzySetType(99);
			ExperimentInfo.setFuzzyTypeName("multi");
			ExperimentInfo.FUZZY_SET_INITIALIZE = 0;
			break;

		case 7:
			ExperimentInfo.setSaveDir("default_entropy_mixed" + sep + "multi");
			ExperimentInfo.setFuzzySetType(99);
			ExperimentInfo.setFuzzyTypeName("multi");
			ExperimentInfo.FUZZY_SET_INITIALIZE = 3;
			break;

		case 8:
			ExperimentInfo.setSaveDir("default_entropy_mixed" + sep + "triangle");
			ExperimentInfo.setFuzzySetType(0);
			ExperimentInfo.setFuzzyTypeName("multi");
			ExperimentInfo.FUZZY_SET_INITIALIZE = 3;
			break;

		case 9:
			ExperimentInfo.setSaveDir("designedFuzzySet" + sep + "triangle");
			ExperimentInfo.setFuzzySetType(0);
			ExperimentInfo.setXML_path(Paths.get("").toAbsolutePath() + "/usedMenbershipDataRank" + "/test/wine/default/multi/wine_UsedMenbershipDataRank.xml");
			ExperimentInfo.FUZZY_SET_INITIALIZE = 4;
			ExperimentInfo.dataRankNum = 3;
			break;

//		case 2:
//			ExperimentInfo.setSaveDir("default_entropy_mixed" + sep + "triangle");
//			ExperimentInfo.setFuzzySetType(3);
//			ExperimentInfo.setFuzzyTypeName("triangle");
//			ExperimentInfo.FUZZY_SET_INITIALIZE = 2;
//			break;
//
////		case 2:
////			ExperimentInfo.setSaveDir("default" + sep + "gaussian");
////			ExperimentInfo.setFuzzySetType(4);
////			ExperimentInfo.setFuzzyTypeName("gaussian");
////			ExperimentInfo.FUZZY_SET_INITIALIZE = 0;
////			break;
////
//		case 3:
//			ExperimentInfo.setSaveDir("default" + sep + "trapezoid");
//			ExperimentInfo.setFuzzySetType(7);
//			ExperimentInfo.setFuzzyTypeName("trapezoid");
//			ExperimentInfo.FUZZY_SET_INITIALIZE = 0;
//			break;
//
//		case 4:
//			ExperimentInfo.setSaveDir("default" + sep + "rectangle");
//			ExperimentInfo.setFuzzySetType(9);
//			ExperimentInfo.setFuzzyTypeName("rectangle");
//			ExperimentInfo.FUZZY_SET_INITIALIZE = 0;
//			break;

		//不均等分割ガウシアン: InhomoGaussian, FuzzyTermType.TYPE_gaussianShape
		//不均等分割区間: InhomoInterval, FuzzyTermType.TYPE_rectangularShape
		//不均等分割線形型ファジィ: InhomoFuzzy, FuzzyTermType.TYPE_trapezoidShape
		//均等分割ガウシアンHomoGaussian, FuzzyTermType.TYPE_gaussianShape
		//均等分割区間: HomoInterval, FuzzyTermType.TYPE_rectangularShape
		//均等分割線形型: HomoFuzzy, FuzzyTermType.TYPE_triangularShape
//		case 5:
//			//Iris
//			ExperimentInfo.setSaveDir("designedFuzzySet" + sep + "samePartitionNum");
//			ExperimentInfo.FUZZY_SET_INITIALIZE = 3;
//			ExperimentInfo.setFuzzySetType(99);
//			ExperimentInfo.setFuzzyTypeName("samePartitionNum");
//
//			ExperimentInfo.designedFuzzySet = new ArrayList<designedFuzzySet>();
//			// dim: 0
//			buf = new designedFuzzySet(4);
//			buf.addDesignedFuzzySet("HomoGaussian", FuzzyTermType.TYPE_gaussianShape, new int[] {2, 3, 4, 5}, 0);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			ExperimentInfo.FUZZY_SET_NUM.add(14);
//			// dim: 1
//			buf = new designedFuzzySet(4);
//			buf.addDesignedFuzzySet("HomoGaussian", FuzzyTermType.TYPE_gaussianShape, new int[] {2, 3, 4, 5}, 0);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			ExperimentInfo.FUZZY_SET_NUM.add(14);
//			// dim: 2
//			buf = new designedFuzzySet(4);
//			buf.addDesignedFuzzySet("InhomoFuzzy", FuzzyTermType.TYPE_trapezoidShape, new int[] {2, 3, 4, 5}, 1);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			ExperimentInfo.FUZZY_SET_NUM.add(14);
//			// dim: 3
//			buf = new designedFuzzySet(4);
//			buf.addDesignedFuzzySet("InhomoInterval", FuzzyTermType.TYPE_rectangularShape, new int[] {2, 3, 4, 5}, 1);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			ExperimentInfo.FUZZY_SET_NUM.add(14);
//			break;
//
//		case 6:
//			//Iris
//			ExperimentInfo.setSaveDir("designedFuzzySet" + sep + "diffPartitionNum");
//			ExperimentInfo.FUZZY_SET_INITIALIZE = 3;
//			ExperimentInfo.setFuzzySetType(99);
//			ExperimentInfo.setFuzzyTypeName("samePartitionNum");
//
//			// dim: 0
//			buf = new designedFuzzySet(4);
//			buf.addDesignedFuzzySet("HomoGaussian", FuzzyTermType.TYPE_gaussianShape, new int[] {2, 3, 4, 5}, 0);
//			buf.addDesignedFuzzySet("InhomoGaussian", FuzzyTermType.TYPE_gaussianShape, new int[] {2, 3, 4, 5}, 1);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			ExperimentInfo.FUZZY_SET_NUM.add(14);
//			// dim: 1
//			buf = new designedFuzzySet(4);
//			buf.addDesignedFuzzySet("HomoGaussian", FuzzyTermType.TYPE_gaussianShape, new int[] {2, 3, 4, 5}, 0);
//			buf.addDesignedFuzzySet("InhomoGaussian", FuzzyTermType.TYPE_gaussianShape, new int[] {2, 3, 4, 5}, 1);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			ExperimentInfo.FUZZY_SET_NUM.add(14);
//			// dim: 2
//			buf = new designedFuzzySet(4);
//			buf.addDesignedFuzzySet("InhomoFuzzy", FuzzyTermType.TYPE_trapezoidShape, new int[] {2, 3, 4, 5}, 1);
//			buf.addDesignedFuzzySet("InhomoInterval", FuzzyTermType.TYPE_rectangularShape, new int[] {2, 3, 4, 5}, 1);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			ExperimentInfo.FUZZY_SET_NUM.add(14);
//			// dim: 3
//			buf = new designedFuzzySet(4);
//			buf.addDesignedFuzzySet("InhomoInterval", FuzzyTermType.TYPE_rectangularShape, new int[] {2, 3, 4, 5}, 1);
//			buf.addDesignedFuzzySet("InhomoGaussian", FuzzyTermType.TYPE_gaussianShape, new int[] {2, 3, 4, 5}, 1);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			ExperimentInfo.FUZZY_SET_NUM.add(14);
//			break;
//
//		//不均等分割ガウシアン: InhomoGaussian, FuzzyTermType.TYPE_gaussianShape
//		//不均等分割区間: InhomoInterval, FuzzyTermType.TYPE_rectangularShape
//		//不均等分割線形型ファジィ: InhomoFuzzy, FuzzyTermType.TYPE_trapezoidShape
//		//均等分割ガウシアンHomoGaussian, FuzzyTermType.TYPE_gaussianShape
//		//均等分割区間: HomoInterval, FuzzyTermType.TYPE_rectangularShape
//		//均等分割線形型: HomoFuzzy, FuzzyTermType.TYPE_triangularShape
//		case 7:
//			//Phoneme
//			ExperimentInfo.setSaveDir("designedFuzzySet" + sep + "samePartitionNum");
//			ExperimentInfo.FUZZY_SET_INITIALIZE = 3;
//			ExperimentInfo.setFuzzySetType(99);
//			ExperimentInfo.setFuzzyTypeName("samePartitionNum");
//
//			// dim: 0
//			buf = new designedFuzzySet(5);
//			buf.addDesignedFuzzySet("InhomoFuzzy", FuzzyTermType.TYPE_trapezoidShape, new int[] {2, 3, 4, 5}, 1);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			ExperimentInfo.FUZZY_SET_NUM.add(14);
//			// dim: 1
//			buf = new designedFuzzySet(5);
//			buf.addDesignedFuzzySet("InhomoGaussian", FuzzyTermType.TYPE_gaussianShape, new int[] {2, 3, 4, 5}, 1);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			ExperimentInfo.FUZZY_SET_NUM.add(14);
//			// dim: 2
//			buf = new designedFuzzySet(5);
//			buf.addDesignedFuzzySet("HomoInterval", FuzzyTermType.TYPE_rectangularShape, new int[] {2, 3, 4, 5}, 0);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			ExperimentInfo.FUZZY_SET_NUM.add(14);
//			// dim: 3
//			buf = new designedFuzzySet(5);
//			buf.addDesignedFuzzySet("InhomoGaussian", FuzzyTermType.TYPE_gaussianShape, new int[] {2, 3, 4, 5}, 1);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			ExperimentInfo.FUZZY_SET_NUM.add(14);
//			// dim: 4
//			buf = new designedFuzzySet(5);
//			buf.addDesignedFuzzySet("HomoGaussian", FuzzyTermType.TYPE_gaussianShape, new int[] {2, 3, 4, 5}, 0);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			ExperimentInfo.FUZZY_SET_NUM.add(14);
//			break;
//
//		case 8:
//			//Phoneme
//			ExperimentInfo.setSaveDir("designedFuzzySet" + sep + "diffPartitionNum");
//			ExperimentInfo.FUZZY_SET_INITIALIZE = 3;
//			ExperimentInfo.setFuzzySetType(99);
//			ExperimentInfo.setFuzzyTypeName("samePartitionNum");
//
//			// dim: 0
//			buf = new designedFuzzySet(5);
//			buf.addDesignedFuzzySet("InhomoInterval", FuzzyTermType.TYPE_rectangularShape, new int[] {2}, 1);
//			ExperimentInfo.FUZZY_SET_NUM.add(12);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			// dim: 1
//			buf = new designedFuzzySet(5);
//			buf.addDesignedFuzzySet("InhomoInterval", FuzzyTermType.TYPE_rectangularShape, new int[] {3}, 1);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			ExperimentInfo.FUZZY_SET_NUM.add(8);
//			// dim: 2
//			buf = new designedFuzzySet(5);
//			buf.addDesignedFuzzySet("HomoInterval", FuzzyTermType.TYPE_rectangularShape, new int[] {2}, 0);
//			ExperimentInfo.FUZZY_SET_NUM.add(12);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			// dim: 3
//			buf = new designedFuzzySet(5);
//			buf.addDesignedFuzzySet("InhomoGaussian", FuzzyTermType.TYPE_gaussianShape, new int[] {2}, 1);
//			ExperimentInfo.FUZZY_SET_NUM.add(10);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			// dim: 4
//			buf = new designedFuzzySet(5);
//			buf.addDesignedFuzzySet("HomoGaussian", FuzzyTermType.TYPE_gaussianShape, new int[] {2}, 0);
//			ExperimentInfo.FUZZY_SET_NUM.add(7);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			break;
//
//		case 9:
//			//yeast
//			ExperimentInfo.setSaveDir("designedFuzzySet" + sep + "samePartitionNum");
//			ExperimentInfo.FUZZY_SET_INITIALIZE = 3;
//			ExperimentInfo.setFuzzySetType(99);
//			ExperimentInfo.setFuzzyTypeName("samePartitionNum");
//
//			// dim: 0
//			buf = new designedFuzzySet(5);
//			buf.addDesignedFuzzySet("HomoGaussian", FuzzyTermType.TYPE_gaussianShape, new int[] {2, 3, 4, 5}, 0);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			ExperimentInfo.FUZZY_SET_NUM.add(14);
//			// dim: 1
//			buf = new designedFuzzySet(5);
//			buf.addDesignedFuzzySet("InhomoFuzzy", FuzzyTermType.TYPE_trapezoidShape, new int[] {2, 3, 4, 5}, 1);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			ExperimentInfo.FUZZY_SET_NUM.add(14);
//			// dim: 2
//			buf = new designedFuzzySet(5);
//			buf.addDesignedFuzzySet("InhomoGaussian", FuzzyTermType.TYPE_gaussianShape, new int[] {2, 3, 4, 5}, 1);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			ExperimentInfo.FUZZY_SET_NUM.add(14);
//			// dim: 3
//			buf = new designedFuzzySet(5);
//			buf.addDesignedFuzzySet("HomoGaussian", FuzzyTermType.TYPE_gaussianShape, new int[] {2, 3, 4, 5}, 0);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			ExperimentInfo.FUZZY_SET_NUM.add(14);
//			// dim: 4
//			buf = new designedFuzzySet(5);
//			buf.addDesignedFuzzySet("InhomoFuzzy", FuzzyTermType.TYPE_trapezoidShape, new int[] {2, 3, 4, 5}, 1);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			ExperimentInfo.FUZZY_SET_NUM.add(14);
//			// dim:5
//			buf = new designedFuzzySet(5);
//			buf.addDesignedFuzzySet("InhomoFuzzy", FuzzyTermType.TYPE_trapezoidShape, new int[] {2, 3, 4, 5}, 1);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			ExperimentInfo.FUZZY_SET_NUM.add(14);
//			buf = new designedFuzzySet(5);
//			// dim:6
//			buf.addDesignedFuzzySet("HomoFuzzy", FuzzyTermType.TYPE_triangularShape, new int[] {2, 3, 4, 5}, 0);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			ExperimentInfo.FUZZY_SET_NUM.add(14);
//			buf = new designedFuzzySet(5);
//			// dim:7
//			buf.addDesignedFuzzySet("HomoFuzzy", FuzzyTermType.TYPE_triangularShape, new int[] {2, 3, 4, 5}, 0);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			ExperimentInfo.FUZZY_SET_NUM.add(14);
//			break;
//
//			//不均等分割ガウシアン: InhomoGaussian, FuzzyTermType.TYPE_gaussianShape
//			//不均等分割区間: InhomoInterval, FuzzyTermType.TYPE_rectangularShape
//			//不均等分割線形型ファジィ: InhomoFuzzy, FuzzyTermType.TYPE_trapezoidShape
//			//均等分割ガウシアンHomoGaussian, FuzzyTermType.TYPE_gaussianShape
//			//均等分割区間: HomoInterval, FuzzyTermType.TYPE_rectangularShape
//			//均等分割線形型: HomoFuzzy, FuzzyTermType.TYPE_triangularShape
//
//			//不均等分割ガウシアン: buf.addDesignedFuzzySet("InhomoGaussian", FuzzyTermType.TYPE_gaussianShape, new int[] {}, 1);
//			//不均等分割区間: buf.addDesignedFuzzySet("InhomoInterval", FuzzyTermType.TYPE_rectangularShape, new int[] {}, 1);
//			//不均等分割線形型ファジィ: buf.addDesignedFuzzySet("InhomoFuzzy", FuzzyTermType.TYPE_trapezoidShape, new int[] {}, 1);
//			//均等分割ガウシアン: buf.addDesignedFuzzySet("HomoGaussian", FuzzyTermType.TYPE_gaussianShape, new int[] {}, 0);
//			//均等分割区間: buf.addDesignedFuzzySet("HomoInterval", FuzzyTermType.TYPE_rectangularShape, new int[] {}, 0);
//			//均等分割線形型: buf.addDesignedFuzzySet("HomoFuzzy", FuzzyTermType.TYPE_triangularShape, new int[] {}, 0);
//
//		case 10:
//			//Yeast
//			ExperimentInfo.setSaveDir("designedFuzzySet" + sep + "diffPartitionNum");
//			ExperimentInfo.FUZZY_SET_INITIALIZE = 3;
//			ExperimentInfo.setFuzzySetType(99);
//			ExperimentInfo.setFuzzyTypeName("samePartitionNum");
//
//			// dim: 0
//			buf = new designedFuzzySet(5);
//			buf.addDesignedFuzzySet("HomoGaussian", FuzzyTermType.TYPE_gaussianShape, new int[] {2}, 0);
//			ExperimentInfo.FUZZY_SET_NUM.add(12);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			// dim: 1
//			buf = new designedFuzzySet(5);
//			buf.addDesignedFuzzySet("InhomoGaussian", FuzzyTermType.TYPE_gaussianShape, new int[] {3}, 1);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			ExperimentInfo.FUZZY_SET_NUM.add(8);
//			// dim: 2
//			buf = new designedFuzzySet(5);
//			buf.addDesignedFuzzySet("InhomoGaussian", FuzzyTermType.TYPE_gaussianShape, new int[] {4}, 1);
//			ExperimentInfo.FUZZY_SET_NUM.add(12);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			// dim: 3
//			buf = new designedFuzzySet(5);
//			buf.addDesignedFuzzySet("HomoGaussian", FuzzyTermType.TYPE_gaussianShape, new int[] {3}, 0);
//			ExperimentInfo.FUZZY_SET_NUM.add(10);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			// dim: 4
//			buf = new designedFuzzySet(5);
//			buf.addDesignedFuzzySet("InhomoFuzzy", FuzzyTermType.TYPE_trapezoidShape, new int[] {2}, 1);
//			ExperimentInfo.FUZZY_SET_NUM.add(7);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			//dim: 5
//			buf = new designedFuzzySet(5);
//			buf.addDesignedFuzzySet("InhomoFuzzy", FuzzyTermType.TYPE_trapezoidShape, new int[] {3}, 1);
//			ExperimentInfo.FUZZY_SET_NUM.add(7);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			//dim: 6
//			buf = new designedFuzzySet(5);
//			buf.addDesignedFuzzySet("HomoFuzzy", FuzzyTermType.TYPE_triangularShape, new int[] {3}, 0);
//			ExperimentInfo.FUZZY_SET_NUM.add(7);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			//dim: 7
//			buf = new designedFuzzySet(5);
//			buf.addDesignedFuzzySet("HomoFuzzy", FuzzyTermType.TYPE_triangularShape, new int[] {4}, 0);
//			ExperimentInfo.FUZZY_SET_NUM.add(7);
//			ExperimentInfo.designedFuzzySet.add(buf);
//			break;
		}
	}

	/** 次の実験セットに行く<br>
	 * 設定した実験セットのindexをexperimentInfoSetListに入れると実行される．
	 * @return 次の実験セットが存在する:正<br>存在しない:負 */
	public static boolean nextExperimentInfoSet() {
		if(ExperimentInfo.experimentInfoSetListIndex + 1 < ExperimentInfo.experimentInfoSetList.length) {
			ExperimentInfo.experimentInfoSetListIndex++;
			ExperimentInfo.setExperimentInfoSetListID(
					ExperimentInfo.experimentInfoSetList[ExperimentInfo.experimentInfoSetListIndex]);
			ExperimentInfo.experimentInfoSet(ExperimentInfo.experimentInfoSetListID);
			return true;
		}else {
			return false;
		}
	}

	public static void setSettings(String dir, String source) {

		URLClassLoader urlLoader = null;
		ResourceBundle bundle = null;
		try {
			urlLoader = new URLClassLoader(new URL[] {new File(dir).toURI().toURL()});
			bundle = ResourceBundle.getBundle(source, Locale.getDefault(), urlLoader);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		if(bundle.containsKey("useArgs")) { useArgs = Boolean.parseBoolean(bundle.getString("useArgs")); }

		if(bundle.containsKey("dataName")) { dataName = bundle.getString("dataName"); }

		if(bundle.containsKey("saveDir")) { saveDir = bundle.getString("saveDir"); }

		if(bundle.containsKey("FuzzySetType")){ FuzzySetType = Integer.parseInt(bundle.getString("FuzzySetType"));}

		if(bundle.containsKey("populationSize")) { populationSize = Integer.parseInt(bundle.getString("populationSize")); }

		if(bundle.containsKey("offspringSize")) { offspringSize = Integer.parseInt(bundle.getString("offspringSize")); }

		//設定値を追加したい時は，以下のvariableとkeyを追加すれば良い
		//if(bundle.containsKey("key")){
		//	variable = Integer.parseInt(bundle.getString("key"));
		//}

		bundle = null;

	}

	@SuppressWarnings("unchecked")
	public String getStaticValues() {
		StringBuilder sb = new StringBuilder();
		String sep = System.lineSeparator();
		sb.append("Class: " + this.getClass().getCanonicalName() + sep);
		sb.append("Settings: " + sep);
		for(Field field : this.getClass().getDeclaredFields()) {
			try {
				if(field.getName() == "designedFuzzySet") {
					for(int i=0; i<designedFuzzySet.size(); i++) {
						sb.append("dim:" + String.valueOf(i) + sep);
						ArrayList<main.ExperimentInfo.designedFuzzySet> obj = (ArrayList<designedFuzzySet>)field.get(this);
						sb.append(obj.get(i).toString());
					}
				}else {
					field.setAccessible(true);
					sb.append(field.getName() + " = " + field.get(this) + sep);
				}
			} catch(IllegalAccessException e) {
				sb.append(field.getName() + " = " + "access denied" + sep);
			}
		}
		return sb.toString();
	}
	public static boolean isUseArgs() {
		return useArgs;
	}
	public static void setUseArgs(boolean useArgs) {
		ExperimentInfo.useArgs = useArgs;
	}
	public static String getDataName() {
		return dataName;
	}
	public static void setDataName(String dataName) {
		ExperimentInfo.dataName = dataName;
	}
	public static String getSaveDir() {
		return saveDir;
	}
	public static void setSaveDir(String saveDir) {
		ExperimentInfo.saveDir = saveDir;
	}
	public static int getFuzzySetType() {
		return FuzzySetType;
	}
	public static void setFuzzySetType(int fuzzySetType) {
		FuzzySetType = fuzzySetType;
	}
	public static String getFuzzyTypeName() {
		return FuzzyTypeName;
	}
	public static void setFuzzyTypeName(String fuzzyTypeName) {
		FuzzyTypeName = fuzzyTypeName;
	}
	public static int getPopulationSize() {
		return populationSize;
	}
	public static void setPopulationSize(int populationSize) {
		ExperimentInfo.populationSize = populationSize;
	}
	public static int getOffspringSize() {
		return offspringSize;
	}
	public static void setOffspringSize(int offspringSize) {
		ExperimentInfo.offspringSize = offspringSize;
	}
	public static int getExperimentInfoSetNum() {
		return experimentInfoSetNum;
	}
	public static void setExperimentInfoSetNum(int experimentInfoSetNum) {
		ExperimentInfo.experimentInfoSetNum = experimentInfoSetNum;
	}
	public static int[] getExperimentInfoSetList() {
		return experimentInfoSetList;
	}
	public static void setExperimentInfoSetList(int[] experimentInfoSetList) {
		ExperimentInfo.experimentInfoSetList = experimentInfoSetList;
	}
	public static int getExperimentInfoSetListIndex() {
		return experimentInfoSetListIndex;
	}
	public static void setExperimentInfoSetListIndex(int experimentInfoSetListIndex) {
		ExperimentInfo.experimentInfoSetListIndex = experimentInfoSetListIndex;
	}
	public static int getExperimentInfoSetListID() {
		return experimentInfoSetListID;
	}
	public static void setExperimentInfoSetListID(int experimentInfoSetListID) {
		ExperimentInfo.experimentInfoSetListID = experimentInfoSetListID;
	}

	public static int getFUZZY_SET_INITIALIZE() {
		return FUZZY_SET_INITIALIZE;
	}

	public static void setFUZZY_SET_INITIALIZE(int fUZZY_SET_INITIALIZE) {
		FUZZY_SET_INITIALIZE = fUZZY_SET_INITIALIZE;
	}

	public static ArrayList<designedFuzzySet> getDesignedFuzzySet() {
		return designedFuzzySet;
	}

	public static main.ExperimentInfo.designedFuzzySet getDesignedFuzzySet(int dim) {
		return designedFuzzySet.get(dim);
	}

	public static void setDesignedFuzzySet(ArrayList<designedFuzzySet> designedFuzzySet) {
		ExperimentInfo.designedFuzzySet = designedFuzzySet;
	}

	public static String getResultRoot() {
		return resultRoot;
	}

	public static void setResultRoot(String resultRoot) {
		ExperimentInfo.resultRoot = resultRoot;
	}

	public static String getXML_path() {
		return XML_path;
	}

	public static void setXML_path(String xML_path) {
		XML_path = xML_path;
	}


}
