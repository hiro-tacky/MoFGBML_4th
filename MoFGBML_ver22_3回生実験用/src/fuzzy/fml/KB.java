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
import main.Consts;
import main.Setting;
import main.ExperimentInfo.ExperimentInfo;
import main.ExperimentInfo.designedFuzzySet;

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
			FSs[dim_i][0] = new FuzzySet("DontCare", FuzzyTermType.TYPE_rectangularShape, dontCare, 0);
			for(int k = 0; k < points.length; k++) {
				FSs[dim_i][k+1] = new FuzzySet( String.valueOf(k+1),
												FuzzyTermType.TYPE_trapezoidShape,
												points[k], getPartitonNum(k, K));
			}
		}
	}
	/**
	 * エントロピーに基づいたガウシアン型ファジィ集合の生成
	 *
	 * @param tra データセット
	 * @param K 分割数のリスト
	 */
	public void classEntropyGaussianInit(SingleDataSetInfo tra, int[] K) {
		Partitions partitions = new Partitions(tra.getNdim());
		partitions.makePartition(tra, K);

		this.Ndim = partitions.getNdim();

		FSs = new FuzzySet[Ndim][];
		float[] dontCare = new float[] {0f, 1f};

		float[][][] params = partitions.gaussian();
		for(int dim_i = 0; dim_i < Ndim; dim_i++) {
			float[][] points = new float[params[dim_i].length][2];
			for(int k = 0; k < points.length; k++) {
				for(int param_i = 0; param_i < points[k].length; param_i++) {
					points[k][param_i] = params[dim_i][k][param_i];
				}
			}

			FSs[dim_i] = new FuzzySet[points.length + 1];
			FSs[dim_i][0] = new FuzzySet("DontCare", FuzzyTermType.TYPE_rectangularShape, dontCare, 0);
			FSs[dim_i][0].setShapeType(Consts.DONT_CARE_SHAPE_TYPE_ID);
			for(int k = 0; k < points.length; k++) {
				FSs[dim_i][k+1] = new FuzzySet( String.valueOf(k+1),
												FuzzyTermType.TYPE_gaussianShape,
												points[k], getPartitonNum(k, K));
			}
		}
	}
	/**
	 * エントロピーに基づいた区間型ファジィ集合の生成
	 *
	 * @param tra データセット
	 * @param K 分割数のリスト
	 */
	public void classEntropyRectangleInit(SingleDataSetInfo tra, int[] K) {
		Partitions partitions = new Partitions(tra.getNdim());
		partitions.makePartition(tra, K);

		this.Ndim = partitions.getNdim();

		FSs = new FuzzySet[Ndim][];
		float[] dontCare = new float[] {0f, 1f};

		float[][][] params = partitions.rectangle();
		for(int dim_i = 0; dim_i < Ndim; dim_i++) {
			float[][] points = new float[params[dim_i].length][2];
			for(int k = 0; k < points.length; k++) {
				for(int param_i = 0; param_i < points[k].length; param_i++) {
					points[k][param_i] = params[dim_i][k][param_i];
				}
			}

			FSs[dim_i] = new FuzzySet[points.length + 1];
			FSs[dim_i][0] = new FuzzySet("DontCare", FuzzyTermType.TYPE_rectangularShape, dontCare, 0);
			FSs[dim_i][0].setShapeType(Consts.DONT_CARE_SHAPE_TYPE_ID);
			for(int k = 0; k < points.length; k++) {
				FSs[dim_i][k+1] = new FuzzySet( String.valueOf(k+1),
												FuzzyTermType.TYPE_rectangularShape,
												points[k], getPartitonNum(k, K));
			}
		}
	}


	public void classEntropyMultiInit(SingleDataSetInfo tra, int[] K, double F) {
		Partitions partitions = new Partitions(tra.getNdim());
		partitions.makePartition(tra, K);

		Partitions partitions_homo = new Partitions(tra.getNdim());
		partitions_homo.makeHomePartition(K);

		this.Ndim = partitions.getNdim();

		FSs = new FuzzySet[Ndim][];
		float[] dontCare = new float[] {0f, 1f};

		float[][][][] params = new float[6][][][];
		params[0] = partitions.gaussian();
		params[1] = partitions.rectangle();
		params[2] = FuzzyPartitioning.startPartition(tra, K, F);
		params[3] = partitions_homo.gaussian();
		params[4] = partitions_homo.rectangle(K);
		params[5] = partitions_homo.triangle();


		for(int dim_i = 0; dim_i < Ndim; dim_i++) {
			int FuzzySetSize = 0;
			for(float buf[][][] : params) {
				FuzzySetSize += buf[dim_i].length;
			}

			FuzzySetSize++;
			FSs[dim_i] = new FuzzySet[FuzzySetSize];
			FSs[dim_i][0] = new FuzzySet("DontCare", FuzzyTermType.TYPE_rectangularShape, dontCare, 0);
			FSs[dim_i][0].setShapeType(Consts.DONT_CARE_SHAPE_TYPE_ID);

			int tmp = 1;
			for(int k=0; k<params[0][dim_i].length; k++) {
				FSs[dim_i][tmp] = new FuzzySet("InhomoGaussian_" + String.valueOf(k), FuzzyTermType.TYPE_gaussianShape, params[0][dim_i][k], getPartitonNum(k, K));
				tmp++;
			}
			for(int k=0; k<params[1][dim_i].length; k++) {
				FSs[dim_i][tmp] = new FuzzySet("InhomoInterval_" + String.valueOf(k), FuzzyTermType.TYPE_rectangularShape, params[1][dim_i][k], getPartitonNum(k, K));
				tmp++;
			}
			for(int k=0; k<params[2][dim_i].length; k++) {
				FSs[dim_i][tmp] = new FuzzySet("InhomoFuzzy_" + String.valueOf(k), FuzzyTermType.TYPE_trapezoidShape, params[2][dim_i][k], getPartitonNum(k, K));
				tmp++;
			}
			for(int k=0; k<params[3][dim_i].length; k++) {
				FSs[dim_i][tmp] = new FuzzySet( "HomoGaussian_" + String.valueOf(k), FuzzyTermType.TYPE_gaussianShape, params[3][dim_i][k], getPartitonNum(k, K));
				tmp++;
			}
			for(int k=0; k<params[4][dim_i].length; k++) {
				FSs[dim_i][tmp] = new FuzzySet( "HomoInterval_" + String.valueOf(k), FuzzyTermType.TYPE_rectangularShape, params[4][dim_i][k], getPartitonNum(k, K));
				tmp++;
			}
			for(int k=0; k<params[5][dim_i].length; k++) {
				FSs[dim_i][tmp] = new FuzzySet( "HomoFuzzy_" + String.valueOf(k), FuzzyTermType.TYPE_triangularShape, params[5][dim_i][k], getPartitonNum(k, K));
				tmp++;
			}
		}
	}

	/**
	 *
	 *
	 * @param tra
	 * @param K
	 * @param F
	 */
	public void designedInit(SingleDataSetInfo tra, double F) {
		this.Ndim = tra.getNdim();
		FSs = new FuzzySet[Ndim][];
		float[] dontCare = new float[] {0f, 1f};
		for(int dim_i = 0; dim_i<this.Ndim; dim_i++){
			designedFuzzySet DesignedFS = ExperimentInfo.getDesignedFuzzySet(dim_i);
			FSs[dim_i] = new FuzzySet[DesignedFS.getParitionSum() + 1];
			FSs[dim_i][0] = new FuzzySet("DontCare", FuzzyTermType.TYPE_rectangularShape, dontCare, 0);
			FSs[dim_i][0].setShapeType(Consts.DONT_CARE_SHAPE_TYPE_ID);
			Partitions partitions = new Partitions(tra.getNdim());
			for(int FuzzySet_i=0; FuzzySet_i<DesignedFS.getFuzzySetTypeNum(); FuzzySet_i++) {
				float[][][] pamras = null;
				if(DesignedFS.getPartitonType(FuzzySet_i) == 0) { //等分割
					partitions.makeHomePartition(DesignedFS.getParitionNumList(FuzzySet_i));
					switch(DesignedFS.getFuzzyTypeID(FuzzySet_i)) {
					case FuzzyTermType.TYPE_gaussianShape:
						pamras = partitions.gaussian();
						break;
					case FuzzyTermType.TYPE_rectangularShape:
						pamras = partitions.rectangle(DesignedFS.getParitionNumList(FuzzySet_i));
						break;
					case FuzzyTermType.TYPE_triangularShape:
						pamras = partitions.triangle();
						break;
					}
				}else if(DesignedFS.getPartitonType(FuzzySet_i) == 1){ //エントロピー導出分割
					partitions.makePartition(tra, DesignedFS.getParitionNumList(FuzzySet_i));
					switch(DesignedFS.getFuzzyTypeID(FuzzySet_i)) {
					case FuzzyTermType.TYPE_gaussianShape:
						pamras = partitions.gaussian();
						break;
					case FuzzyTermType.TYPE_rectangularShape:
						pamras = partitions.rectangle();
						break;
					case FuzzyTermType.TYPE_trapezoidShape:
						pamras = FuzzyPartitioning.startPartition(tra, DesignedFS.getParitionNumList(FuzzySet_i), F);
						break;
					}
				}
				addFuzzySet(DesignedFS.getFuzzyTypeName(FuzzySet_i), DesignedFS.getFuzzyTypeID(FuzzySet_i), pamras[dim_i], DesignedFS.getParitionNumList(FuzzySet_i), dim_i);
			}
		}
	}

	/**
	 * kがK分割の集合のうち，何番目の分割数の集合に属するかを返す．
	 * ex) k=7, K={2, 3, 4, 5}の場合，kの分割数は4．
	 * @param k k番目のファジィセット
	 * @param K 分割数の集合
	 * @return kがK分割のファジィセットのなかで何分割であるかの数．
	 */
	public int getPartitonNum(int k, int[] K) {
		int buf=0, i=0;
		for(;i < K.length; i++){
			buf += K[i];
			if(buf > k) break;
		}
		return K[i];
	}

	/**
	 * 与えられたファジィタイプとパラメータから，ファジィセットをセットする．但し，単一の名前．
	 *
	 * @param FuzzySetName ファジィセットの名前
	 * @param FuzzyTermTypeID ファジィセットの形状のID FuzzyTermType.TYPE_****Shapeに準拠．
	 * @param params パラメータの集合[次元数][ファジィセットの数][パラメータ数]
	 * @param K 分割数集合
	 * @param hasDontCare Don't Careを含むか否か．
	 */
	public void setFuzzySet(String FuzzySetName, int FuzzyTermTypeID, float[][][] params, int[] K, boolean hasDontCare) {
		if(hasDontCare) {
			float[] dontCare = new float[] {0f, 1f};
			for(int dim_i=0; dim_i<this.FSs.length; dim_i++) {
				this.FSs[dim_i] = new FuzzySet[params[dim_i].length + 1];
				this.FSs[dim_i][0] = new FuzzySet("DontCare", FuzzyTermType.TYPE_rectangularShape, dontCare, 0);
				for(int k=0; k<params[dim_i].length; k++) {
					FSs[dim_i][k+1] = new FuzzySet( FuzzySetName + "_" + String.valueOf(k+1),
							FuzzyTermTypeID, params[dim_i][k], getPartitonNum(k, K));
				}
			}
		}else {
			for(int dim_i=0; dim_i<this.FSs.length; dim_i++) {
				this.FSs[dim_i] = new FuzzySet[params[dim_i].length + 1];
				for(int k=0; k<params[dim_i].length; k++) {
					FSs[dim_i][k+1] = new FuzzySet( FuzzySetName + "_" + String.valueOf(k+1),
							FuzzyTermTypeID, params[dim_i][k], getPartitonNum(k, K));
				}
			}
		}
	}

	/**
	 * 与えられたファジィタイプとパラメータから，ファジィセットをセットする．但し，単一の名前．
	 *
	 * @param FuzzySetName ファジィセットの名前
	 * @param FuzzyTermTypeID ファジィセットの形状のID FuzzyTermType.TYPE_****Shapeに準拠．
	 * @param params パラメータの集合[次元数][ファジィセットの数][パラメータ数]
	 * @param K 分割数集合
	 * @param hasDontCare Don't Careを含むか否か．
	 */
	public void setFuzzySet(String FuzzySetName, int FuzzyTermTypeID, int dim, float[][] params, int[] K, boolean hasDontCare) {
		if(hasDontCare) {
			float[] dontCare = new float[] {0f, 1f};
			this.FSs[dim] = new FuzzySet[params.length + 1];
			this.FSs[dim][0] = new FuzzySet("DontCare", FuzzyTermType.TYPE_rectangularShape, dontCare, 0);
			for(int k=0; k<params.length; k++) {
				FSs[dim][k+1] = new FuzzySet( FuzzySetName + "_" + String.valueOf(k+1),
						FuzzyTermTypeID, params[k], getPartitonNum(k, K));
			}
		}else {
			this.FSs[dim] = new FuzzySet[params.length + 1];
			for(int k=0; k<params.length; k++) {
				FSs[dim][k+1] = new FuzzySet( FuzzySetName + "_" + String.valueOf(k+1),
						FuzzyTermTypeID, params[k], getPartitonNum(k, K));
			}
		}
	}

	/**
	 * 与えられたファジィタイプとパラメータから，ファジィセットをセットする．但し，単一の名前．
	 *
	 * @param FuzzySetName ファジィセットの名前
	 * @param FuzzyTermTypeID ファジィセットの形状のID FuzzyTermType.TYPE_****Shapeに準拠．
	 * @param params パラメータの集合[次元数][ファジィセットの数][パラメータ数]
	 * @param K 分割数集合
	 * @return 次のindex(埋まってる部分の最終index+1)
	 */
	public void addFuzzySet(String FuzzySetName, int FuzzyTermTypeID, int dim, float[][] params, int[] K) {
		for(int i=0; i<this.FSs[dim].length; i++) {
			if(Objects.isNull(this.FSs[dim][i])){
				for(int k=0; k<params.length; k++) {
					FSs[dim][k+i] = new FuzzySet( FuzzySetName + "_" + String.valueOf(k+i),
							FuzzyTermTypeID, params[k], getPartitonNum(k, K));
				}
				break;
			}
		}
	}

	/**
	 * 与えられたファジィタイプとパラメータから，ファジィセットをセットする．但し，単一の名前．
	 *
	 * @param FuzzySetName ファジィセットの名前
	 * @param FuzzyTermTypeID ファジィセットの形状のID FuzzyTermType.TYPE_****Shapeに準拠．
	 * @param params パラメータの集合[次元数][ファジィセットの数][パラメータ数]
	 * @param K 分割数集合
	 * @return 次のindex(埋まってる部分の最終index+1)
	 */
	public void addFuzzySet(String FuzzySetName, int FuzzyTermTypeID, float[][][] params, int[] K) {
		for(int dim=0; dim<params.length; dim++) {
			for(int i=0; i<this.FSs[dim].length; i++) {
				if(Objects.isNull(this.FSs[dim][i])){
					for(int k=0; k<params[dim].length; k++) {
						FSs[dim][k+i] = new FuzzySet( FuzzySetName + "_" + String.valueOf(k+i),
								FuzzyTermTypeID, params[dim][k], getPartitonNum(k, K));
					}
					break;
				}
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
	 * @return 次のindex(埋まってる部分の最終index+1)
	 */
	public void addFuzzySet(String FuzzySetName, int FuzzyTermTypeID, float[][] params, int[] K, int dim) {
		for(int i=0; i<this.FSs[dim].length; i++) {
			if(Objects.isNull(this.FSs[dim][i])){
				for(int k=0; k<params.length; k++) {
					FSs[dim][k+i] = new FuzzySet( FuzzySetName + "_" + String.valueOf(k+i),
							FuzzyTermTypeID, params[k], getPartitonNum(k, K));
				}
				break;
			}
		}
	}

	public void threeTriangle(int Ndim) {
		this.Ndim = Ndim;
		FSs = new FuzzySet[Ndim][];
		int[] K = {3};
		Partitions partitions_homo = new Partitions(this.Ndim);
		partitions_homo.makeHomePartition(K);
		float[][][] params = partitions_homo.triangle();

		setFuzzySet("HomoTriangle", FuzzyTermType.TYPE_triangularShape, params, K, true);
	}
/********** 重要・必読 **********/
	/**
	 * 2-5分割の等分割三角型ファジィ集合 + Don't Careの15種を全attributeに定義<br>
	 * @param Ndim datasetの次元数
	 */
	public void homogeneousInit(int Ndim) {
		this.Ndim = Ndim;
		FSs = new FuzzySet[Ndim][];
		int[] K = {2, 3, 4, 5};
		Partitions partitions_homo = new Partitions(this.Ndim);
		partitions_homo.makeHomePartition(K);
		float[][][] params = partitions_homo.triangle();

		setFuzzySet("HomoTriangle", FuzzyTermType.TYPE_triangularShape, params, K, true);
	}

	/**
	 * 三角型，区間型，台形型，ガウシアン型，Don't Care を持つファジィ集合
	 * @param Ndim
	 */
	public void multiInit(int Ndim) {
		this.Ndim = Ndim;
		FSs = new FuzzySet[Ndim][];

		int K[] = Setting.PatitionNumSet;
		Partitions partitions_homo = new Partitions(this.Ndim);
		partitions_homo.makeHomePartition(K);

		float[] dontCare = new float[] {0f, 1f};
		float[][][] params_triangle = partitions_homo.triangle();
		float[][][] params_gaussian = partitions_homo.gaussian();
		float[][][] params_rectangle = partitions_homo.rectangle(K);
		int FuzzySetNum = params_triangle.length + params_gaussian.length + params_rectangle.length;
		for(int i = 0; i < Ndim; i++) {
			FSs[i] = new FuzzySet[FuzzySetNum + 1];
			//Don't Care
			FSs[i][0] = new FuzzySet("DontCare", FuzzyTermType.TYPE_rectangularShape, dontCare);
			FSs[i][0].setShapeType(Consts.DONT_CARE_SHAPE_TYPE_ID);

			//三角形型メンバーシップ関数
			int k = 0;
			for(int j = 0; j < params_triangle.length; j++) {
				FSs[i][k+j+1] = new FuzzySet(String.valueOf(j+1), FuzzyTermType.TYPE_triangularShape, params_triangle[i][j], getPartitonNum(k, K));
			}
			k += params_triangle.length;
			for(int j = 0; j < params_gaussian.length; j++) {
				FSs[i][k+j+1] = new FuzzySet(String.valueOf(j+1), FuzzyTermType.TYPE_gaussianShape, params_gaussian[i][j], getPartitonNum(k, K));
			}
			k += params_gaussian.length;
			for(int j = 0; j < params_rectangle.length; j++) {
				FSs[i][k+j+1] = new FuzzySet(String.valueOf(j+1), FuzzyTermType.TYPE_rectangularShape, params_rectangle[i][j], getPartitonNum(k, K));
			}
		}

	}
	//三角形型メンバーシップ関数
	public void triangleInit(int Ndim) {
		this.Ndim = Ndim;
		FSs = new FuzzySet[Ndim][];
		int[] K = Setting.PatitionNumSet;

		Partitions partitions_homo = new Partitions(this.Ndim);
		partitions_homo.makeHomePartition(K);
		float[][][] params = partitions_homo.triangle();

		setFuzzySet("HomoTriangle", FuzzyTermType.TYPE_triangularShape, params, K, true);
	}

	//ガウス型メンバーシップ関数
	public void gaussianInit(int Ndim) {
		this.Ndim = Ndim;
		FSs = new FuzzySet[Ndim][];
		int[] K = Setting.PatitionNumSet;

		Partitions partitions_homo = new Partitions(this.Ndim);
		partitions_homo.makeHomePartition(K);
		float[][][] params = partitions_homo.gaussian();

		setFuzzySet("HomoGaussian", FuzzyTermType.TYPE_gaussianShape, params, K, true);
	}

	//台形型メンバーシップ関数
	public void trapezoidInit(int Ndim) {
		this.Ndim = Ndim;
		FSs = new FuzzySet[Ndim][];
		int[] K = Setting.PatitionNumSet;

		Partitions partitions_homo = new Partitions(this.Ndim);
		partitions_homo.makeHomePartition(K);
		float[][][] params = partitions_homo.trapezoid();

		setFuzzySet("HomoTrapezoid", FuzzyTermType.TYPE_trapezoidShape, params, K, true);
	}

	//区間型メンバーシップ関数
	public void rectangleInit(int Ndim) {
		this.Ndim = Ndim;
		FSs = new FuzzySet[Ndim][];
		int[] K = Setting.PatitionNumSet;

		Partitions partitions_homo = new Partitions(this.Ndim);
		partitions_homo.makeHomePartition(K);
		float[][][] params = partitions_homo.rectangle(K);

		setFuzzySet("HomoRectangle", FuzzyTermType.TYPE_rectangularShape, params, K, true);
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
				FSs[i][j] = new FuzzySet(termName, shapeType, params, 99);
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
