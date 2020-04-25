package fuzzy.fml;

import java.io.File;
import java.util.ArrayList;

import data.SingleDataSetInfo;
import fuzzy.FuzzyPartitioning;
import fuzzy.fml.params.HomoTriangle_2_3_4_5;
import fuzzy.fml.params.HomoTriangle_3;
import fuzzy.fml.params.homogaussian_takigawa;
import fuzzy.fml.params.homorectangle_takigawa;
import fuzzy.fml.params.homotrapezoid_takigawa;
import fuzzy.fml.params.homotriangle_takigawa;
import jfml.FuzzyInferenceSystem;
import jfml.JFML;
import jfml.knowledgebase.KnowledgeBaseType;
import jfml.knowledgebase.variable.FuzzyVariableType;
import jfml.term.FuzzyTermType;

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

	public void classEntropyInit(SingleDataSetInfo tra, int K, double F) {
		ArrayList<ArrayList<double[]>> trapezoids = FuzzyPartitioning.startPartition(tra, K, F);

		this.Ndim = trapezoids.size();

		FSs = new FuzzySet[Ndim][];
		float[] dontCare = new float[] {0f, 1f};

		for(int dim_i = 0; dim_i < Ndim; dim_i++) {
			float[][] points = new float[trapezoids.get(dim_i).size()][4];
			for(int k = 0; k < points.length; k++) {
				for(int param_i = 0; param_i < 4; param_i++) {
					points[k][param_i] = (float)trapezoids.get(dim_i).get(k)[param_i];
				}
			}

			FSs[dim_i] = new FuzzySet[points.length + 1];
			FSs[dim_i][0] = new FuzzySet("0", FuzzyTermType.TYPE_rectangularShape, dontCare);
			for(int k = 0; k < points.length; k++) {
				FSs[dim_i][k+1] = new FuzzySet( String.valueOf(k+1),
												FuzzyTermType.TYPE_trapezoidShape,
												points[k]);
			}
			for(int k = 0; k < points.length; k++) {
				FSs[dim_i][k+1] = new FuzzySet( String.valueOf(k+1),
												FuzzyTermType.TYPE_trapezoidShape,
												points[k]);
			}
		}

	}

	public void threeTriangle(int Ndim) {
		this.Ndim = Ndim;

		FSs = new FuzzySet[Ndim][];
		float[] dontCare = new float[] {0f, 1f};
		float[][] params = HomoTriangle_3.getParams();

		for(int i = 0; i < Ndim; i++) {
			FSs[i] = new FuzzySet[params.length + 1];
			FSs[i][0] = new FuzzySet("0", FuzzyTermType.TYPE_rectangularShape, dontCare);
			for(int j = 0; j < params.length; j++) {
				FSs[i][j+1] = new FuzzySet(String.valueOf(j+1), FuzzyTermType.TYPE_triangularShape, params[j]);
			}
		}
	}
/********** 重要・必読 **********/
	/**
	 * 2-5分割の等分割三角型ファジィ集合 + Don't Careの15種を全attributeに定義<br>
	 * @param Ndim datasetの次元数
	 */
	public void homogeneousInit(int Ndim) {
		this.Ndim = Ndim;

		FSs = new FuzzySet[Ndim][];

		float[] dontCare = new float[] {0f, 1f};
		float[][] params = HomoTriangle_2_3_4_5.getParams();

		for(int i = 0; i < Ndim; i++) {
			FSs[i] = new FuzzySet[params.length + 1];
			//Don't Care
			FSs[i][0] = new FuzzySet("0", FuzzyTermType.TYPE_rectangularShape, dontCare);


			for(int j = 0; j < params.length; j++) {
				FSs[i][j+1] = new FuzzySet(String.valueOf(j+1), FuzzyTermType.TYPE_triangularShape, params[j]);
			}
		}

	}

	/**
	 * 三角型，区間型，台形型，ガウシアン型，Don't Care を持つファジィ集合
	 * @param Ndim
	 */
	public void multiInit(int Ndim) {
		this.Ndim = Ndim;

		FSs = new FuzzySet[Ndim][];

		float[] dontCare = new float[] {0f, 1f};
		float[][] params_triangle = homotriangle_takigawa.get_parms();
		float[][] params_gaussian = homogaussian_takigawa.get_parms();
		float[][] params_trapezoid = homotrapezoid_takigawa.get_parms();
		float[][] params_rectangle = homorectangle_takigawa.get_parms();
		int FuzzySetNum = params_triangle.length + params_gaussian.length + params_trapezoid.length + params_rectangle.length;

		for(int i = 0; i < Ndim; i++) {
			FSs[i] = new FuzzySet[FuzzySetNum + 1];
			//Don't Care
			FSs[i][0] = new FuzzySet("0", FuzzyTermType.TYPE_rectangularShape, dontCare);

			//三角形型メンバーシップ関数
			for(int j = 0; j < params_triangle.length; j++) {
				FSs[i][j+1] = new FuzzySet(String.valueOf(j+1), FuzzyTermType.TYPE_triangularShape, params_triangle[j]);
			}
		}

	}
	//三角形型メンバーシップ関数
	public void triangleInit(int Ndim) {
		this.Ndim = Ndim;

		FSs = new FuzzySet[Ndim][];

		float[] dontcare =new float[] {0f, 1f};
		float[][] params = homotriangle_takigawa.get_parms();

		for(int i=0; i<Ndim; i++){
			FSs[i] = new FuzzySet[params.length + 1];
			FSs[i][0] = new FuzzySet("0", FuzzyTermType.TYPE_rectangularShape, dontcare );

			for(int j=0; j< params.length; j++) {
				FSs[i][j+1] =  new FuzzySet(String.valueOf(j+1), FuzzyTermType.TYPE_triangularShape, params[j]);
			}
		}
	}

	//ガウス型メンバーシップ関数
	public void gaussianInit(int Ndim) {
		this.Ndim = Ndim;

		FSs = new FuzzySet[Ndim][];

		float[] dontcare =new float[] {0f, 1f};
		float[][] params = homogaussian_takigawa.get_parms();

		for(int i=0; i<Ndim; i++){
			FSs[i] = new FuzzySet[params.length + 1];
			FSs[i][0] = new FuzzySet("0", FuzzyTermType.TYPE_rectangularShape, dontcare );

			for(int j=0; j< params.length; j++) {
				FSs[i][j+1] =  new FuzzySet(String.valueOf(j+1), FuzzyTermType.TYPE_gaussianShape, params[j]);
			}
		}
	}

	//台形型メンバーシップ関数
	public void trapezoidInit(int Ndim) {
		this.Ndim = Ndim;

		FSs = new FuzzySet[Ndim][];

		float[] dontcare =new float[] {0f, 1f};
		float[][] params = homotrapezoid_takigawa.get_parms();

		for(int i=0; i<Ndim; i++){
			FSs[i] = new FuzzySet[params.length + 1];
			FSs[i][0] = new FuzzySet("0", FuzzyTermType.TYPE_rectangularShape, dontcare );

			for(int j=0; j< params.length; j++) {
				FSs[i][j+1] =  new FuzzySet(String.valueOf(j+1), FuzzyTermType.TYPE_trapezoidShape, params[j]);
			}
		}
	}

	//区間型メンバーシップ関数
	public void rectangleInit(int Ndim) {
		this.Ndim = Ndim;

		FSs = new FuzzySet[Ndim][];

		float[] dontcare =new float[] {0f, 1f};
		float[][] params = homorectangle_takigawa.get_parms();

		for(int i=0; i<Ndim; i++){
			FSs[i] = new FuzzySet[params.length + 1];
			FSs[i][0] = new FuzzySet("0", FuzzyTermType.TYPE_rectangularShape, dontcare );

			for(int j=0; j< params.length; j++) {
				FSs[i][j+1] =  new FuzzySet(String.valueOf(j+1), FuzzyTermType.TYPE_rectangularShape, params[j]);
			}
		}
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

}
