package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ForkJoinPool;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import data.Input;
import fgbml.mofgbml.MoFGBML;
import fgbml.multilabel_ver3.MultiLabel_ver3;
import fgbml.subdivision_ver2.Subdivision_ver2;
import method.MersenneTwisterFast;
import method.Output;
import method.ResultMaster;
import output.toXML;
import output.result.Result_MoFGBML;


/**
 * .jarファイルの書き出しの際は，使用したstartExperiment()を持つクラス名で書き出すこと．
 * また，エクスポート時は
 *   .classpath
 *   src/setting.properties
 *   src/consts.properties
 * のチェックを外してエクスポートすること．<br>
 * <br>
 * <h1>Command Line Arguments</h1>
 * <p>
 * args[0] : String : ./<br>
 * args[1] : String : consts (.propertiesファイル名)<br>
 * args[2] : String : setting (.propertiesファイル名)<br>
 * args[3] : String : dataName<br>
 * args[4] : int : parallelCores<br>
 * args[5] : String : saveDir<br>
 * </p>
 */

//src/jfml/term/FuzzyTerm.java にこの部分追加したら、エラー消える
//public int getType(){
//    return this.type;
//}
public class Main {
	/**
	 * main関数
	 *
	 * @param args 実行の構成からの引数
	 */
	public static void main(String[] args) {
		/* ********************************************************* */
		String version = "22.0";
		System.out.println("ver.: " + version);

		/* ********************************************************* */
		//設定ファイル読込 - Load .properties
		String currentDir = args[0];
		String constsSource = args[1];
		String settingSource = args[2];
		Consts.setConsts(currentDir, constsSource);
		Setting.setSettings(currentDir, settingSource);
		//コマンドライン引数読込 - Load command line arguments
		Setting.dataName = args[3];
		Setting.parallelCores = Integer.parseInt(args[4]);
		Setting.saveDir = args[5];
		//並列用fork join pool 生成
		if(Setting.calclationType == 0) {
			Setting.forkJoinPool = new ForkJoinPool(Setting.parallelCores);
		}


		/* ********************************************************* */
		//基本データ出力
		System.out.println("Processors: " + Runtime.getRuntime().availableProcessors() + " ");
		System.out.println();

		System.out.print("args: ");
		for(int i = 0; i < args.length; i++) {
			System.out.print(args[i] + " ");
		}
		System.out.println();
		System.out.println();


		/* ********************************************************* */
		Date start = new Date();
		System.out.println("START: ");
		System.out.println(start);

		/* ********************************************************* */
		//Repeat x-fold cross-validation

		
		//Make result directries
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmm");
		String sep = File.separator;
		String id = format.format(calendar.getTime());
		/*format: ".\result\iris_20191021-1255"*/


		//ファイル名構築
		String resultRoot = System.getProperty("user.dir") + sep + "result" + sep
							+ Setting.saveDir + sep + Setting.dataName + "_" + id;
		
		//実験試行
		int FuzzySetType[] = {99, 3, 4, 7, 9};
		/** 99:multi 3:triangular 4:gaussian 7:trapezoid 9:rectangular */
		for(int v :FuzzySetType) {
			Setting.FuzzySetType = v;
			repeatExection(args, resultRoot);
		}
		/* ********************************************************* */

		Date end = new Date();
		System.out.println("END: ");
		System.out.println(end);
		/* ********************************************************* */
	}

		/**
		 * 実験を実際に試行する部分
		 *
		 * @param args 実行の構成からの引数
		 */
	public static void repeatExection(String[] args, String dir_path) {
		/* ********************************************************* */
		//The names of files
		//データセットのファイル名作成
		String[][] traFiles = new String[Setting.repeatTimes][Setting.crossValidationNum];
		String[][] tstFiles = new String[Setting.repeatTimes][Setting.crossValidationNum];
		for(int rep_i = 0; rep_i < Setting.repeatTimes; rep_i++) {
			for(int cv_i = 0; cv_i < Setting.crossValidationNum; cv_i++) {
				traFiles[rep_i][cv_i] = Input.makeFileNameOne(cv_i, rep_i, true);
				tstFiles[rep_i][cv_i] = Input.makeFileNameOne(cv_i, rep_i, false);
			}
		}

		Result_MoFGBML master = new Result_MoFGBML();
		/* ********************************************************* */
		//Make result directries
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmm");
		String sep = File.separator;
		String id = format.format(calendar.getTime());
		/*format: ".\result\iris_20191021-1255"*/


		//ファイル名構築
		String resultRoot = dir_path + sep + Setting.dataName + "_" + id;
		Output.mkdirs(resultRoot);

		/* ********************************************************* */
		//Output "Experimental Settings"
		String consts = (new Consts()).getStaticValues();
		String settings = (new Setting()).getStaticValues();
		String fileName = resultRoot + sep + "Consts_" + id + ".txt";
		Output.writeln(fileName, consts);
		fileName = resultRoot + sep + "Setting_" + id + ".txt";
		Output.writeln(fileName, settings);

		/* ********************************************************* */
		//Result Master
		ResultMaster resultMaster = new ResultMaster(resultRoot, id);

		/* ********************************************************* */
		//Experiment
		Experiment main = setExperiment();

		MersenneTwisterFast rnd = new MersenneTwisterFast(Setting.seed);
		int count = 0;
		for(int rep_i = 0; rep_i < Setting.repeatTimes; rep_i++) {
			for(int cv_i = 0; cv_i < Setting.crossValidationNum; cv_i++) {
				//make now trial Directory
				resultMaster.setNowRep(rep_i);
				resultMaster.setNowCV(cv_i);
				resultMaster.setTrialRoot(resultRoot + sep + "trial" + rep_i+cv_i);
				resultMaster.setNowTrial(count);
				count++;

				System.out.println(Setting.dataName + " : TRIAL: " + rep_i + cv_i);

				main.startExperiment(args,
									 traFiles[rep_i][cv_i], tstFiles[rep_i][cv_i],
									 rnd, resultMaster, master);

				System.out.println();
			}
		}
		/* ********************************************************* */
		//Output Times
		fileName = resultRoot + sep + "Times_" + id + ".csv";
		resultMaster.outputTimes(fileName);

		String FuzzyTypeName = "";
		switch(Setting.FuzzySetType) {
			case 99: FuzzyTypeName = "multi"; break;
			case 3: FuzzyTypeName = "triangle"; break;
			case 4: FuzzyTypeName = "gaussian"; break;
			case 7: FuzzyTypeName = "trapezoid"; break;
			case 9: FuzzyTypeName = "rectangle"; break;
		}
		try {
			toXML result = new toXML("result");
			toXML ruleset = new toXML("ruleset");
			result.ResultToXML(master);
			ruleset.RuleSetToXML(master);
			result.output(args[3] + "_" +  FuzzyTypeName + "_result", resultRoot);
			ruleset.output(args[3] + "_" + FuzzyTypeName + "_ruleset", resultRoot);
		} catch (TransformerConfigurationException | ParserConfigurationException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (FileNotFoundException | TransformerException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}

	public static Experiment setExperiment() {
		Experiment main = null;

		switch(Setting.experimentName) {
		case "MoFGBML":
			main = new MoFGBML();
			break;
		case "Subdivision":
			main = new Subdivision_ver2();
			break;
		case "MoFGBMLML":
			main = new MultiLabel_ver3();
			break;
		}

		return main;
	}

}
















