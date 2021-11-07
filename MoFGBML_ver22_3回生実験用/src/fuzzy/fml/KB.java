package fuzzy.fml;

import java.io.File;
import java.util.Objects;

import data.SingleDataSetInfo;
import fuzzy.FuzzyPartitioning;
import jfml.FuzzyInferenceSystem;
import jfml.JFML;
import jfml.knowledgebase.KnowledgeBaseType;
import jfml.knowledgebase.variable.FuzzyVariableType;
import jfml.term.FuzzyTermType;
import main.Setting;

/**
 * Fuzzy Markup LanguageのKnowledgeBaseを扱うクラス
 *
 *
 * @params Ndim KBの次元数
 * @params FSs ファジィ集合[次元数][ファジィ集合の個体数]<br>
 *             FSs[Ndim][FuzzySetNum]
 */

public class KB {
	// ************************************************************
	//FML
	float domainLeft = 0f;
	float domainRight = 1f;

	int Ndim = 1;
	/**
	 * [Ndim][fuzzySetNum]
	 */
	FuzzySet[][] FSs;

	// ************************************************************
	public KB() {}

	// ************************************************************

	public void classEntropyInit(SingleDataSetInfo tra, int[] K, double F) {
		Partitions partitions = new Partitions(tra.getNdim());
		partitions.makePartition(tra, K);

		this.Ndim = partitions.getNdim();

		FSs = new FuzzySet[Ndim][];
		float[] dontCare = new float[] {0f, 1f};

		float[][][] params = partitions.trapezoid();
		for(int dim_i = 0; dim_i < Ndim; dim_i++) {
			float[][] points = new float[params[dim_i].length][4];
			for(int k = 0; k < points.length; k++) {
				for(int param_i = 0; param_i < 4; param_i++) {
					points[k][param_i] = params[dim_i][k][param_i];
				}
			}

			FSs[dim_i] = new FuzzySet[points.length + 1];
			FSs[dim_i][0] = new FuzzySet("DontCare", FuzzyTermType.TYPE_rectangularShape, dontCare);
			FSs[dim_i][0].setPartitonNum(0);
			for(int k = 0; k < points.length; k++) {
				FSs[dim_i][k+1] = new FuzzySet( String.valueOf(k+1),
												FuzzyTermType.TYPE_trapezoidShape,
												points[k]);
				FSs[dim_i][k+1].setPartitonNum(getPartitionNum(k, K));
			}
		}
	}

	/**
	 * エントロピーに基づいたガウシアン型ファジィ集合の生成
	 *
	 * @param tra データセット
	 * @param K 分割数のリスト
	 */
	public void MultiEntropyInit(SingleDataSetInfo tra, int[] K, double F) {
		this.Ndim = tra.getNdim();

		Partitions partitions = new Partitions(tra.getNdim());
		partitions.makePartition(tra, K);

		Partitions partitions_homo = new Partitions(tra.getNdim());
		partitions_homo.makeHomePartition(K);

		float[][][] params_triangle_inhome = FuzzyPartitioning.startPartition(tra, K, F);
		float[][][] params_gaussian_inhome = partitions.gaussian();
		float[][][] params_rectangle_inhome = partitions.rectangle();

		FSs = new FuzzySet[Ndim][];
		for(int dim_i=0; dim_i<this.Ndim; dim_i++) {
			int cnt = params_triangle_inhome[dim_i].length + params_gaussian_inhome[dim_i].length + params_rectangle_inhome[dim_i].length;
			FSs[dim_i] = new FuzzySet[cnt + 1];
		}
		this.setDontCare();

		fuzzySetInit_auto("InhomoFuzzy", FuzzyTermType.TYPE_trapezoidShape, params_triangle_inhome, K);
		fuzzySetInit_auto("InhomoGaussian", FuzzyTermType.TYPE_gaussianShape, params_gaussian_inhome, K);
		fuzzySetInit_auto("InhomoInterval", FuzzyTermType.TYPE_rectangularShape, params_rectangle_inhome, K);
	}
	/**
	 * エントロピーに基づいた区間型ファジィ集合の生成
	 *
	 * @param tra データセット
	 * @param K 分割数のリスト
	 */
	public void singleFuzzySetInit(SingleDataSetInfo tra, int[] K, double F, int FUZZY_SET_INITIALIZE, int FuzzySetType) {
		this.Ndim = tra.getNdim();

		float[][][] params = new float[this.Ndim][][];
		String fuzzyTermName = null;
		int FuzzyTermTypeID = 3;

		if(FUZZY_SET_INITIALIZE == 0 && FuzzySetType == 3) {
			Partitions partitions_homo = new Partitions(tra.getNdim());
			partitions_homo.makeHomePartition(K);
			params = partitions_homo.triangle();
			fuzzyTermName = "HomoFuzzy";
			FuzzyTermTypeID = FuzzyTermType.TYPE_triangularShape;
		}
		if(FUZZY_SET_INITIALIZE == 0 && FuzzySetType == 4) {
			Partitions partitions_homo = new Partitions(tra.getNdim());
			partitions_homo.makeHomePartition(K);
			params = partitions_homo.gaussian();
			fuzzyTermName = "HomoGaussian";
			FuzzyTermTypeID = FuzzyTermType.TYPE_gaussianShape;
		}
		if(FUZZY_SET_INITIALIZE == 0 && FuzzySetType == 7) {
			Partitions partitions_homo = new Partitions(tra.getNdim());
			partitions_homo.makeHomePartition(K);
			params = partitions_homo.trapezoid();
			fuzzyTermName = "HomoTrapezoid";
			FuzzyTermTypeID = FuzzyTermType.TYPE_trapezoidShape;
		}
		if(FUZZY_SET_INITIALIZE == 0 && FuzzySetType == 9) {
			Partitions partitions_homo = new Partitions(tra.getNdim());
			partitions_homo.makeHomePartition(K);
			params = partitions_homo.rectangle(K);
			fuzzyTermName = "HomoInterval";
			FuzzyTermTypeID = FuzzyTermType.TYPE_rectangularShape;
		}
		if(FUZZY_SET_INITIALIZE == 2 && (FuzzySetType == 3 || FuzzySetType == 7)) {
			Partitions partitions = new Partitions(tra.getNdim());
			partitions.makePartition(tra, K);
			params = FuzzyPartitioning.startPartition(tra, K, F);
			fuzzyTermName = "InhomoFuzzy";
			FuzzyTermTypeID = FuzzyTermType.TYPE_trapezoidShape;
		}
		if(FUZZY_SET_INITIALIZE == 2 && FuzzySetType == 4) {
			Partitions partitions = new Partitions(tra.getNdim());
			partitions.makePartition(tra, K);
			params = partitions.gaussian();
			fuzzyTermName = "InhomoGaussian";
			FuzzyTermTypeID = FuzzyTermType.TYPE_gaussianShape;
		}
		if(FUZZY_SET_INITIALIZE == 2 && FuzzySetType == 9) {
			Partitions partitions = new Partitions(tra.getNdim());
			partitions.makePartition(tra, K);
			params = partitions.rectangle(K);
			fuzzyTermName = "InhomoInterval";
			FuzzyTermTypeID = FuzzyTermType.TYPE_rectangularShape;
		}


		FSs = new FuzzySet[Ndim][];
		for(int dim_i=0; dim_i<this.Ndim; dim_i++) {
			FSs[dim_i] = new FuzzySet[params.length + 1];
		}
		this.setDontCare();

		fuzzySetInit_auto(fuzzyTermName, FuzzyTermTypeID, params, K);
	}
	/**
	 *
	 *
	 * @param tra
	 * @param K
	 * @param F
	 * @param FUZZY_SET_INITIALIZE [次元][FSs_num]
	 * @param FuzzySetType [次元][FSs_num]
	 * @param FSsNum
	 */
	public void DesignedFuzzySetInit(SingleDataSetInfo tra, int[] K, double F, int[][] FUZZY_SET_INITIALIZE, int[][] FuzzySetType, int FSsNum) {
		this.Ndim = tra.getNdim();
		FSs = new FuzzySet[Ndim][];


		Partitions partitions_homo = new Partitions(tra.getNdim());
		partitions_homo.makeHomePartition(K);

		Partitions partitions = new Partitions(tra.getNdim());
		partitions.makePartition(tra, K);

		float[][][] params_triangle_inhome = FuzzyPartitioning.startPartition(tra, K, F);
		float[][][] params_gaussian_inhome = partitions.gaussian();
		float[][][] params_rectangle_inhome = partitions.rectangle();
		float[][][] params_triangle_home = partitions_homo.triangle();
		float[][][] params_trapezoid_home = partitions_homo.trapezoid();
		float[][][] params_gaussian_home = partitions_homo.gaussian();
		float[][][] params_rectangle_home = partitions_homo.rectangle(K);

		for(int dim_i=0; dim_i<this.Ndim; dim_i++) {
			String[] fuzzyTermName = new String[FSsNum];
			int[] FuzzyTermTypeID = new int[FSsNum];
			float[][][] params = new float[FSsNum][][];

			int cnt = 0;
			for(int FSs_i=0; FSs_i<FSsNum; FSs_i++) {

				if(FUZZY_SET_INITIALIZE[dim_i][FSs_i] == 0 && FuzzySetType[dim_i][FSs_i] == 3) {
					params[FSs_i] = params_triangle_home[dim_i];
					fuzzyTermName[FSs_i] = "HomoFuzzy";
					FuzzyTermTypeID[FSs_i] = FuzzyTermType.TYPE_triangularShape;
				}
				if(FUZZY_SET_INITIALIZE[dim_i][FSs_i] == 0 && FuzzySetType[dim_i][FSs_i] == 4) {
					params[FSs_i] = params_gaussian_home[dim_i];
					fuzzyTermName[FSs_i] = "HomoGaussian";
					FuzzyTermTypeID[FSs_i] = FuzzyTermType.TYPE_gaussianShape;
				}
				if(FUZZY_SET_INITIALIZE[dim_i][FSs_i] == 0 && FuzzySetType[dim_i][FSs_i] == 7) {
					params[FSs_i] = params_trapezoid_home[dim_i];
					fuzzyTermName[FSs_i] = "HomoTrapezoid";
					FuzzyTermTypeID[FSs_i] = FuzzyTermType.TYPE_trapezoidShape;
				}
				if(FUZZY_SET_INITIALIZE[dim_i][FSs_i] == 0 && FuzzySetType[dim_i][FSs_i] == 9) {
					params[FSs_i] = params_rectangle_home[dim_i];
					fuzzyTermName[FSs_i] = "HomoInterval";
					FuzzyTermTypeID[FSs_i] = FuzzyTermType.TYPE_rectangularShape;
				}
				if(FUZZY_SET_INITIALIZE[dim_i][FSs_i] == 2 && (FuzzySetType[dim_i][FSs_i] == 3 || FuzzySetType[dim_i][FSs_i] == 7)) {
					params[FSs_i] = params_triangle_inhome[dim_i];
					fuzzyTermName[FSs_i] = "InhomoFuzzy";
					FuzzyTermTypeID[FSs_i] = FuzzyTermType.TYPE_trapezoidShape;
				}
				if(FUZZY_SET_INITIALIZE[dim_i][FSs_i] == 2 && FuzzySetType[dim_i][FSs_i] == 4) {
					params[FSs_i] = params_gaussian_inhome[dim_i];
					fuzzyTermName[FSs_i] = "InhomoGaussian";
					FuzzyTermTypeID[FSs_i] = FuzzyTermType.TYPE_gaussianShape;
				}
				if(FUZZY_SET_INITIALIZE[dim_i][FSs_i] == 2 && FuzzySetType[dim_i][FSs_i] == 9) {
					params[FSs_i] = params_rectangle_inhome[dim_i];
					fuzzyTermName[FSs_i] = "InhomoInterval";
					FuzzyTermTypeID[FSs_i] = FuzzyTermType.TYPE_rectangularShape;
				}
				for(int i=0; i<FSsNum; i++) {
					cnt += params[i].length;
				}
			}

			FSs[dim_i] = new FuzzySet[cnt+1];

			this.setDontCare(dim_i);
			for(int i=0; i<FSsNum; i++) {
				fuzzySetInit_auto(fuzzyTermName[i], FuzzyTermTypeID[i], params[i], K, dim_i);
			}
		}
	}


	public void MultiMixedInit(SingleDataSetInfo tra, int[] K, double F) {
		this.Ndim = tra.getNdim();

		Partitions partitions = new Partitions(tra.getNdim());
		partitions.makePartition(tra, K);

		Partitions partitions_homo = new Partitions(tra.getNdim());
		partitions_homo.makeHomePartition(K);

		float[][][] params_triangle_inhome = FuzzyPartitioning.startPartition(tra, K, F);
		float[][][] params_gaussian_inhome = partitions.gaussian();
		float[][][] params_rectangle_inhome = partitions.rectangle();
		float[][][] params_triangle_home = partitions_homo.triangle();
		float[][][] params_gaussian_home = partitions_homo.gaussian();
		float[][][] params_rectangle_home = partitions_homo.rectangle(K);

		FSs = new FuzzySet[Ndim][];
		for(int dim_i=0; dim_i<this.Ndim; dim_i++) {
			int cnt = params_triangle_inhome[dim_i].length + params_gaussian_inhome[dim_i].length + params_rectangle_inhome[dim_i].length
					+ params_triangle_home[dim_i].length + params_gaussian_home[dim_i].length + params_rectangle_home[dim_i].length;
			FSs[dim_i] = new FuzzySet[cnt + 1];
		}
		this.setDontCare();

		fuzzySetInit_auto("InhomoFuzzy", FuzzyTermType.TYPE_trapezoidShape, params_triangle_inhome, K);
		fuzzySetInit_auto("InhomoGaussian", FuzzyTermType.TYPE_gaussianShape, params_gaussian_inhome, K);
		fuzzySetInit_auto("InhomoInterval", FuzzyTermType.TYPE_rectangularShape, params_rectangle_inhome, K);
		fuzzySetInit_auto("HomoFuzzy", FuzzyTermType.TYPE_triangularShape, params_triangle_home, K);
		fuzzySetInit_auto("HomoGaussian", FuzzyTermType.TYPE_gaussianShape, params_gaussian_home, K);
		fuzzySetInit_auto("HomoInterval", FuzzyTermType.TYPE_rectangularShape, params_rectangle_home, K);
	}

	/**
	 * kがK分割の集合のうち，何番目の分割数の集合に属するかを返す．
	 * ex) k=7, K={2, 3, 4, 5}の場合，kの分割数は4．
	 * @param k k番目のファジィセット
	 * @param K 分割数の集合
	 * @return kがK分割のファジィセットのなかで何分割であるかの数．
	 */
	public int getPartitionNum(int k, int[] K) {
		int buf=0, i=0;
		for(;i < K.length; i++){
			buf += K[i];
			if(buf > k) break;
		}
		return K[i];
	}

	/**
	 * K分割の集合を返す．
	 * ex) K={2, 3, 4, 5}の場合，{2,2, 3,3,3, 4,4,4,4, 5,5,5,5,5}．
	 * @param k k番目のファジィセット
	 * @param K 分割数の集合
	 * @return kがK分割のファジィセットのなかで何分割であるかの数．
	 */
	public int[] getPartitionNumList(int[] K) {
		int sum=0, cnt=0;
		for(int tmp: K) {sum += tmp;}
		int[] buf = new int[sum];
		for(int tmp: K) {
			for(int i=0; i<tmp; i++) {
				buf[cnt] = tmp;
				cnt++;
			}
		}
		return buf;
	}

	public int sumOfList(int[] K) {
		int sum=0;
		for(int tmp: K) {sum += tmp;}
		return sum;
	}

	/**
	 * beginを始点，k個の名前の配列を生成する．
	 * ex) begin=3, k=3, str="FSs" return={"FSs_3", "FSs_4", "FSs_5"}
	 * @param k k番目のファジィセット
	 * @param K 分割数の集合
	 * @return kがK分割のファジィセットのなかで何分割であるかの数．
	 */
	public String[] getFuzzySetNameList(String str, int begin, int k) {
		String[] buf = new String[k];
		for(int i=0; i<k; i++) {
				buf[i] = str + "_" + String.valueOf(begin + i);
		}
		return buf;
	}

	/**
	 * 0を始点として，k個の名前の配列を生成する．
	 * ex) k=3, str="FSs" return={"FSs_0", "FSs_1", "FSs_2"}
	 * @param k k番目のファジィセット
	 * @param K 分割数の集合
	 * @return kがK分割のファジィセットのなかで何分割であるかの数．
	 */
	public String[] getFuzzySetNameList(String str, int k) {
		String[] buf = new String[k];
		for(int i=0; i<k; i++) {
				buf[i] = str + "_" + String.valueOf(i);
		}
		return buf;
	}
	/**
	 * 与えられたファジィタイプとパラメータから，ファジィセットをセットする．但し，単一の名前．
	 *
	 * @param FuzzySetName ファジィセットの名前
	 * @param FuzzyTermTypeID ファジィセットの形状のID FuzzyTermType.TYPE_****Shapeに準拠．
	 * @param params パラメータの集合[次元数][ファジィセットの数][パラメータ数]
	 * @param [次元数][ファジィセットの数] セットするファジィセットのID
	 */
	public void setFuzzySet(String[][] FuzzySetName, int[][] FuzzyTermTypeID, float[][][] params, int[][] FuzzySetID) {
		for(int dim_i=0; dim_i<this.FSs.length; dim_i++) {
			for(int FuzzySet_i=0; FuzzySet_i<FuzzySetID[dim_i].length; FuzzySet_i++) {
				FSs[dim_i][FuzzySetID[dim_i][FuzzySet_i]] = new FuzzySet( FuzzySetName[dim_i][FuzzySet_i],	FuzzyTermTypeID[dim_i][FuzzySet_i],
						params[dim_i][FuzzySet_i]);
			}
		}
	}

	/**
	 * 与えられたファジィタイプとパラメータから，ファジィセットをセットする．但し，単一の名前．
	 *
	 * @param FuzzySetName ファジィセットの名前
	 * @param FuzzyTermTypeID ファジィセットの形状のID FuzzyTermType.TYPE_****Shapeに準拠．
	 * @param params パラメータの集合[ファジィセットの数][パラメータ数]
	 * @param K 分割数集合
	 * @param hasDontCare Don't Careを含むか否か．
	 */
	public void setFuzzySet(String[] FuzzySetName, int[] FuzzyTermTypeID, float[][] params, int[] FuzzySetID) {
		for(int dim_i=0; dim_i<this.FSs.length; dim_i++) {
			for(int FuzzySet_i=0; FuzzySet_i<FuzzySetID.length; FuzzySet_i++) {
				FSs[dim_i][FuzzySetID[FuzzySet_i]] = new FuzzySet( FuzzySetName[FuzzySet_i], FuzzyTermTypeID[FuzzySet_i], params[FuzzySet_i]);
			}
		}
	}


	public void setPartitionNum(int[] PartitionNum, int[] FuzzySetID) {
		for(int dim_i=0; dim_i<this.FSs.length; dim_i++) {
			for(int FuzzySet_i=0; FuzzySet_i<FuzzySetID.length; FuzzySet_i++) {
				FSs[dim_i][FuzzySetID[FuzzySet_i]].setPartitonNum(PartitionNum[FuzzySet_i]);
			}
		}
	}

	public void setPartitionNum(int[][] PartitionNum, int[][] FuzzySetID) {
		for(int dim_i=0; dim_i<this.FSs.length; dim_i++) {
			for(int FuzzySet_i=0; FuzzySet_i<FuzzySetID.length; FuzzySet_i++) {
				FSs[dim_i][FuzzySetID[dim_i][FuzzySet_i]].setPartitonNum(PartitionNum[dim_i][FuzzySet_i]);
			}
		}
	}


	/**
	 * ファジィセットの自動追加．queueのaddみたいな感じ
	 * "HomoTriangel"１まとめで放りこむ
	 *
	 * @param FuzzySetName
	 * @param FuzzyTermTypeID
	 * @param params
	 * @param K
	 */
	public void fuzzySetInit_auto(String FuzzySetName, int FuzzyTermTypeID, float[][][] params, int[] K) {
		String[] fuzzySetNameList = getFuzzySetNameList(FuzzySetName, sumOfList(K));
		int[] partitionNumList = getPartitionNumList(K);
		for(int dim_i=0; dim_i<this.FSs.length; dim_i++) {
			for(int i=0; i<this.FSs[dim_i].length; i++) {
				if(Objects.isNull(this.FSs[dim_i][i])){
					for(int k=0; k<params[dim_i].length; k++) {
						FSs[dim_i][i+k] = new FuzzySet( fuzzySetNameList[k],
								FuzzyTermTypeID, params[dim_i][k]);
						FSs[dim_i][i+k].setPartitonNum(partitionNumList[k]);
					}
					break;
				}
			}
		}
	}

	/**
	 * ファジィセットの自動追加．queueのaddみたいな感じ
	 * "HomoTriangel"１まとめで放りこむ
	 *
	 * @param FuzzySetName
	 * @param FuzzyTermTypeID
	 * @param params
	 * @param K
	 */
	public void fuzzySetInit_auto(String FuzzySetName, int FuzzyTermTypeID, float[][] params, int[] K, int dim) {
		int[] partitionNumList = getPartitionNumList(K);
		String[] fuzzySetNameList = getFuzzySetNameList(FuzzySetName, sumOfList(K));
		for(int i=0; i<this.FSs[dim].length; i++) {
			if(Objects.isNull(this.FSs[dim][i])){
				for(int k=0; k<params.length; k++) {
					FSs[dim][i+k] = new FuzzySet( fuzzySetNameList[k],
							FuzzyTermTypeID, params[k]);
					FSs[dim][i+k].setPartitonNum(partitionNumList[k]);
				}
				break;
			}
		}
	}



	public void setDontCare() {
		for(int dim_i=0; dim_i<this.FSs.length; dim_i++) {
			FSs[dim_i][0] = new FuzzySet("DontCare", FuzzyTermType.TYPE_rectangularShape, new float[] {0f, 1f});
			FSs[dim_i][0].setPartitonNum(0);
		}
	}

	public void setDontCare(int dim) {
		FSs[dim][0] = new FuzzySet("DontCare", FuzzyTermType.TYPE_rectangularShape, new float[] {0f, 1f});
		FSs[dim][0].setPartitonNum(0);
	}

	public void threeTriangle(int Ndim) {
		int[] K = {3};

		this.Ndim = Ndim;
		FSs = new FuzzySet[Ndim][sumOfList(K)+1];

		Partitions partitions_homo = new Partitions(this.Ndim);
		partitions_homo.makeHomePartition(K);
		float[][][] params = partitions_homo.triangle();

		setDontCare();
		fuzzySetInit_auto("HomoTriangle", FuzzyTermType.TYPE_triangularShape, params, K);
	}
/********** 重要・必読 **********/
	/**
	 * 2-5分割の等分割三角型ファジィ集合 + Don't Careの15種を全attributeに定義<br>
	 * @param Ndim datasetの次元数
	 */
	public void homogeneousInit(int Ndim) {
		int[] K = {2, 3, 4, 5};

		this.Ndim = Ndim;
		FSs = new FuzzySet[Ndim][sumOfList(K)+1];

		Partitions partitions_homo = new Partitions(this.Ndim);
		partitions_homo.makeHomePartition(K);
		float[][][] params = partitions_homo.triangle();

		setDontCare();
		fuzzySetInit_auto("HomoTriangle", FuzzyTermType.TYPE_triangularShape, params, K);
	}

	/**
	 * 三角型，区間型，ガウシアン型，Don't Care を持つファジィ集合
	 * @param Ndim
	 */
	public void multiInit(int Ndim) {
		int K[] = Setting.PatitionNumSet;

		this.Ndim = Ndim;

		Partitions partitions_homo = new Partitions(this.Ndim);
		partitions_homo.makeHomePartition(K);

		float[][][] params_triangle = partitions_homo.triangle();
		float[][][] params_gaussian = partitions_homo.gaussian();
		float[][][] params_rectangle = partitions_homo.rectangle(K);

		FSs = new FuzzySet[Ndim][sumOfList(K)*3 + 1];
		setDontCare();

		fuzzySetInit_auto("HomoFuzzy", FuzzyTermType.TYPE_triangularShape, params_triangle, K);
		fuzzySetInit_auto("HomoGaussian", FuzzyTermType.TYPE_gaussianShape, params_gaussian, K);
		fuzzySetInit_auto("HomoInterval", FuzzyTermType.TYPE_rectangularShape, params_rectangle, K);

	}
	//三角形型メンバーシップ関数
	public void triangleInit(int Ndim) {
		this.Ndim = Ndim;
		int[] K = Setting.PatitionNumSet;

		Partitions partitions_homo = new Partitions(this.Ndim);
		partitions_homo.makeHomePartition(K);
		float[][][] params = partitions_homo.triangle();

		FSs = new FuzzySet[Ndim][sumOfList(K) + 1];
		setDontCare();
		fuzzySetInit_auto("HomoFuzzy", FuzzyTermType.TYPE_triangularShape, params, K);
	}

	//ガウス型メンバーシップ関数
	public void gaussianInit(int Ndim) {
		this.Ndim = Ndim;
		int[] K = Setting.PatitionNumSet;

		Partitions partitions_homo = new Partitions(this.Ndim);
		partitions_homo.makeHomePartition(K);
		float[][][] params = partitions_homo.gaussian();

		FSs = new FuzzySet[Ndim][sumOfList(K) + 1];
		setDontCare();
		fuzzySetInit_auto("HomoGaussian", FuzzyTermType.TYPE_gaussianShape, params, K);
	}

	//台形型メンバーシップ関数
	public void trapezoidInit(int Ndim) {
		this.Ndim = Ndim;
		int[] K = Setting.PatitionNumSet;

		Partitions partitions_homo = new Partitions(this.Ndim);
		partitions_homo.makeHomePartition(K);
		float[][][] params = partitions_homo.trapezoid();

		FSs = new FuzzySet[Ndim][sumOfList(K) + 1];
		setDontCare();
		fuzzySetInit_auto("HomoGaussian", FuzzyTermType.TYPE_trapezoidShape, params, K);
	}

	//区間型メンバーシップ関数
	public void rectangleInit(int Ndim) {
		this.Ndim = Ndim;
		int[] K = Setting.PatitionNumSet;

		Partitions partitions_homo = new Partitions(this.Ndim);
		partitions_homo.makeHomePartition(K);
		float[][][] params = partitions_homo.rectangle();

		FSs = new FuzzySet[Ndim][sumOfList(K) + 1];
		setDontCare();
		fuzzySetInit_auto("HomoInterval", FuzzyTermType.TYPE_rectangularShape, params, K);
	}

	/**
	 * XMLファイルを読み込んでFSs[][]を初期化するメソッド．<br>
	 * @param fileName String : 読み込むXMLファイルのパス
	 */
	public void inputFML(String fileName) {
		//Load XML file.
		File fml = new File(fileName);
		FuzzyInferenceSystem fs = JFML.load(fml);
		KnowledgeBaseType kb = fs.getKnowledgeBase();

		//#of Feature
		Ndim = kb.getKnowledgeBaseVariables().size();
		FSs = new FuzzySet[Ndim][];
		domainLeft = ((FuzzyVariableType)kb.getKnowledgeBaseVariables().get(0)).getDomainleft();
		domainRight = ((FuzzyVariableType)kb.getKnowledgeBaseVariables().get(0)).getDomainright();

		//#s of Fuzzy Sets for Each Feature.
		int[] termNum = new int[Ndim];

		for(int i = 0; i < Ndim; i++) {
			//Get Name of Variable
			String variableName = kb.getKnowledgeBaseVariables().get(i).getName();

			termNum[i] = ((FuzzyVariableType)kb.getVariable(variableName)).getTerms().size();
			FSs[i] = new FuzzySet[termNum[i]];
			for(int j = 0; j < termNum[i]; j++) {
				//Get Name of Fuzzy Set
				String termName =  ((FuzzyVariableType)kb.getVariable(variableName)).getTerms().get(j).getName();
				int shapeType = ((FuzzyTermType)kb.getVariable(variableName).getTerm(termName)).getType();
				float[] params = ((FuzzyTermType)kb.getVariable(variableName).getTerm(termName)).getParam();

				//Make Fuzzy Set
				FSs[i][j] = new FuzzySet(termName, shapeType, params);
			}
		}
	}

	public void outputFML(String fileName) {
		FuzzyInferenceSystem fs = new FuzzyInferenceSystem();
		KnowledgeBaseType kb = new KnowledgeBaseType();
		for(int dim_i = 0; dim_i < Ndim; dim_i++) {
			float domainLeft = 0f;
			float domainRight = 1f;
			FuzzyVariableType variable = new FuzzyVariableType(String.valueOf(dim_i), domainLeft, domainRight);
			for(int f = 0; f < FSs[dim_i].length; f++) {
				variable.addFuzzyTerm(FSs[dim_i][f].getTerm());
			}
			kb.addVariable(variable);
		}
		fs.setKnowledgeBase(kb);
		File fml = new File(fileName);
		JFML.writeFSTtoXML(fs, fml);
	}

	public double calcMembership(int attribute, int fuzzySet, double x) {
		double ans = FSs[attribute][fuzzySet].calcMembership(x);
		return ans;
	}

	public FuzzySet[][] getFSs(){
		return this.FSs;
	}

	public FuzzySet[] getFSs(int dim) {
		return this.FSs[dim];
	}

	public void setFSs(FuzzySet[][] fSs) {
		FSs = fSs;
	}

	public int getFSsnum(int dim) {
		return this.FSs[dim].length;
	}

	public int getNdim() {
		return Ndim;
	}

	public void setNdim(int ndim) {
		Ndim = ndim;
	}

}
