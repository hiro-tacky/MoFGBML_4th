package main;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
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

	/** FuzzySetType <br>
	* 99:multi 3:triangular 4:gaussian 7:trapezoid 9:rectangular */
	public static int FuzzySetType = 99;

	/** #of Population */
	public static int populationSize = 50;
	/** #of Offspring */
	public static int offspringSize = 50;

	// ************************************************************
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

	public String getStaticValues() {
		StringBuilder sb = new StringBuilder();
		String sep = System.lineSeparator();
		sb.append("Class: " + this.getClass().getCanonicalName() + sep);
		sb.append("Settings: " + sep);
		for(Field field : this.getClass().getDeclaredFields()) {
			try {
				field.setAccessible(true);
				sb.append(field.getName() + " = " + field.get(this) + sep);
			} catch(IllegalAccessException e) {
				sb.append(field.getName() + " = " + "access denied" + sep);
			}
		}
		return sb.toString();
	}

}
