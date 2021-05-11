package fuzzy.fml;

import jfml.term.FuzzyTermType;

/**
 * <h1>JFMLのFuzzyTermTypeクラスのWrapperクラス</h1>
 * <p>FuzzyTermTypeを用いて，1つのファジィ集合を定義する</p>
 * @param name オブジェクト名
 * @param shapetype sypte_typeのint，idを示す.
 */
public class FuzzySet {
	// ************************************************************
	String name;
	int shapeType;
	double weight = 1.0;
	FuzzyTermType term;
	int partitonNum = -1;

	float[] params;

	// ************************************************************
	public FuzzySet(String name, int shapeType, float[] params, int partitonNum) {
		this.name = name;
		this.shapeType = shapeType;
		this.params = params.clone();
		this.partitonNum = partitonNum;
		make();
	}

	public FuzzySet(String name, int shapeType, float[] params) {
		this.name = name;
		this.shapeType = shapeType;
		this.params = params.clone();
		make();
	}

	// ************************************************************
	public void make() {
		this.term = new FuzzyTermType(name, shapeType, params);
	}

	public FuzzyTermType getTerm() {
		return this.term;
	}

	/********** 重要・必読 **********/
	public double calcMembership(double x) {
		return term.getMembershipValue((float)x) * weight;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setShapeType(int shapeType) {
		this.shapeType = shapeType;
	}

	public int getShapeType() {
		return this.shapeType;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public void setParams(int i, float param) {
		if(this.params.length > i) {
			this.params[i] = param;
		}else{
			throw new IndexOutOfBoundsException("インデックスが範囲外");
		}
	}

	public void setParams(float[] params) {
		this.params = params.clone();
	}

	public int getPartitonNum() {
		return partitonNum;
	}

	public void setPartitonNum(int partitonNum) {
		this.partitonNum = partitonNum;
	}

	public float[] getParams() {
		return params;
	}

	public void setTerm(FuzzyTermType term) {
		this.term = term;
	}

}
