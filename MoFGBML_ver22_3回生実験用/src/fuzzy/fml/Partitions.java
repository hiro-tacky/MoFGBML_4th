package fuzzy.fml;

import java.util.ArrayList;

import data.SingleDataSetInfo;
import fuzzy.FuzzyPartitioning;

/**
 * エントロピーに基づいたパラメータを生成する．
 *
 * @author hirot
 *
 */
public class Partitions {
	/** データセットの次元数 */
	int Ndim;
	/** 分割境界集合[次元][分割数][境界] ＊[境界]は2分割なら{0.0, 0.3, 1.0}といった感じ*/
	ArrayList<ArrayList<ArrayList<Double>>> partitions;
	/**	次元ごとの分割数の総数のリスト */
	int[] numPartitions;

	public Partitions(int Ndim) {
		this.Ndim = Ndim;
		this.partitions = new ArrayList<ArrayList<ArrayList<Double>>>();
		this.numPartitions = new int[Ndim];
	}


	/**
	 * 分割区間を生成する
	 * @param tra データセット
	 * @param K 分割数のリスト
	 */
	public void makePartition(SingleDataSetInfo tra, int[] K) {
		this.partitions = FuzzyPartitioning.makePartition(tra, K);
		for(int dim_i=0; dim_i<this.Ndim; dim_i++) {
			this.numPartitions[dim_i] = numPartitions(dim_i);
		}
	}

	/**
	 * 等分割の分割区間を生成する
	 * @param K 分割数のリスト
	 */
	public void makeHomePartition(int[] K) {
		ArrayList<ArrayList<ArrayList<Double>>> partitions_set = new ArrayList<ArrayList<ArrayList<Double>>>();
		for(int i_dim=0; i_dim<this.Ndim; i_dim++) {
			ArrayList<ArrayList<Double>> partition_list = new ArrayList<ArrayList<Double>>();
			for(int k: K) {
				ArrayList<Double> partition = new ArrayList<Double>();
				for(int i=0; i<=k; i++) {
					partition.add( (double)(2*i-1)/((k-1)*2) );
				}
				partition_list.add(partition);
			}
			partitions_set.add(partition_list);
		}
		this.partitions = partitions_set;
		for(int dim_i=0; dim_i<this.Ndim; dim_i++) {
			this.numPartitions[dim_i] = numPartitions(dim_i);
		}
	}

	/**
	 * 任意の次元の分割数を求める
	 * @param dim 任意の次元数
 	 * @return
	 */
	public int numPartitions(int dim) {
		int numPartitions = 0;
		for(ArrayList<Double> partitions_list: this.partitions.get(dim)) {
			numPartitions += partitions_list.size()-1;
		}
		return numPartitions;
	}

	/**
	 * ガウシアン型のパラメータを生成する．
	 *
	 * @return パラメータ[次元][ファジイセット][パラメータ]
	 */
	public float[][][] gaussian(){
		float[][][] params = new float[this.Ndim][][];
		for(int dim_i=0; dim_i<this.partitions.size(); dim_i++) {
			params[dim_i] = new float[this.numPartitions[dim_i]][2];
			int tmp = 0;
			for(ArrayList<Double> partition_list: this.partitions.get(dim_i)) {
				for(int i=0; i<partition_list.size()-1; i++) {
					//最初と最後だけ頂点が区間端になるようにする．
					if(i == 0){
						params[dim_i][tmp+i] = calcGaussParam(0, (float)(double)partition_list.get(i+1), 0.5f);
					}else if(i == partition_list.size()-2) {
						params[dim_i][tmp+i] = calcGaussParam(1, (float)(double)partition_list.get(i), 0.5f);
					}else {
						double left = partition_list.get(i), right = partition_list.get(i+1);
						params[dim_i][tmp+i] = calcGaussParam((float)(left + right)/2, (float)(double)partition_list.get(i), 0.5f);
					}
				}
				tmp += partition_list.size()-1;
			}
		}
		return params;
	}

	/**
	 * 区間型のパラメータを生成する．
	 *
	 * @return パラメータ[次元][ファジイセット][パラメータ]
	 */
	public float[][][] rectangle(){
		float[][][] params = new float[this.Ndim][][];
		for(int dim_i=0; dim_i<this.partitions.size(); dim_i++) {
			params[dim_i] = new float[this.numPartitions[dim_i]][2];
			int tmp = 0;
			for(ArrayList<Double> partition_list: this.partitions.get(dim_i)) {
				for(int i=0; i<partition_list.size()-1; i++) {
					params[dim_i][tmp+i] = new float[] {(float)(double)partition_list.get(i), (float)(double)partition_list.get(i+1)};
				}
				tmp += partition_list.size()-1;
			}
		}
		return params;
	}

	/**
	 * 区間型のパラメータを生成する．
	 *
	 * @return パラメータ[次元][ファジイセット][パラメータ]
	 */
	public float[][][] rectangle(int[] K){
		float[][][] params = new float[this.Ndim][][];
		for(int dim_i=0; dim_i<this.partitions.size(); dim_i++) {
			params[dim_i] = new float[this.numPartitions[dim_i]][2];
			int tmp = 0;
			for(int k : K) {
				for(int i=0; i<k; i++) {
					params[dim_i][tmp+i] = new float[] {(float)i*(1f/k), (float)(i+1)*(1f/k)};
				}
				tmp += k;
			}
		}
		return params;
	}
	/**
	 * 平均 mean の正規分布(係数なし，x=meanのときvalue=1)について，
	 * 引数に与えられた，(x, value)を通る平均meanの正規分布の標準偏差を計算するメソッド
	 * @param mean
	 * @param x
	 * @param value
	 * @return
	 */
	public static float[] calcGaussParam(float mean, float x, float value) {
		float[] param;

		float variance;		//分散
		float deviation;	//標準偏差
		float numerator;	//分子
		float denominator;	//分母

		numerator = -((x - mean) * (x - mean));
		denominator = 2f * (float)Math.log(value);

		variance = numerator / denominator;
		deviation = (float)Math.sqrt(variance);

		param = new float[] {mean, deviation};

		return param;
	}

	/**
	 * 三角形型のパラメータを生成する．
	 *
	 * @return パラメータ[次元][ファジイセット][パラメータ]
	 */
	public float[][][] triangle(){
		float[][][] params = new float[this.Ndim][][];
		for(int dim_i=0; dim_i<this.partitions.size(); dim_i++) {
			params[dim_i] = new float[this.numPartitions[dim_i]][3];
			int tmp = 0;
			for(ArrayList<Double> partition_list: this.partitions.get(dim_i)) {
				for(int i=0; i<partition_list.size()-1; i++) {
					if(i == 0) {
						params[dim_i][tmp+i] = new float[] {0f, 0f, 2*(float)(double)partition_list.get(i+1)};
					}else if(i == partition_list.size()-2) {
						params[dim_i][tmp+i] = new float[] {2*(float)(double)partition_list.get(i) - 1f, 1f, 1f};
					}else {
						float left = (float)(double)partition_list.get(i), right = (float)(double)partition_list.get(i+1);
						params[dim_i][tmp+i] = new float[] {left*3f/2f - right/2f, (left+right)/2f, right*3f/2f - left/2f};
					}
				}
				tmp += partition_list.size()-1;
			}
		}
		return params;
	}

	/**
	 * 台形型のパラメータを生成する．
	 *
	 * @return パラメータ[次元][ファジイセット][パラメータ]
	 */
	public float[][][] trapezoid(){
		float[][][] params = new float[this.Ndim][][];
		for(int dim_i=0; dim_i<this.partitions.size(); dim_i++) {
			params[dim_i] = new float[this.numPartitions[dim_i]][4];
			int tmp = 0;
			for(ArrayList<Double> partition_list: this.partitions.get(dim_i)) {
				for(int i=0; i<partition_list.size()-1; i++) {
					if(i == 0) {
						params[dim_i][tmp+i] = new float[] {0f, 0f, (float)0.5*(float)(double)partition_list.get(i+1), (float)1.5*(float)(double)partition_list.get(i+1)};
					}else if(i == partition_list.size()-2) {
						params[dim_i][tmp+i] = new float[] {(float)1.5*(float)(double)partition_list.get(i) - 0.5f, (float)0.5*(float)(double)partition_list.get(i) + 0.5f, 1f, 1f};
					}else {
						float left = (float)(double)partition_list.get(i), right = (float)(double)partition_list.get(i+1);
						params[dim_i][tmp+i] = new float[] {left*5f/4f - right/4f, left*3f/4f + right/4f, right*3f/4f + left/4f, right*5f/4f - left/4f};
					}
				}
				tmp += partition_list.size()-1;
			}
		}
		return params;
	}

	public int getNdim() {
		return Ndim;
	}

	public void setNdim(int ndim) {
		Ndim = ndim;
	}

	public ArrayList<ArrayList<ArrayList<Double>>> getPartitions() {
		return partitions;
	}

	public void setPartitions(ArrayList<ArrayList<ArrayList<Double>>> partitions) {
		this.partitions = partitions;
	}

	public int[] getNumPartitions() {
		return numPartitions;
	}

	public void setNumPartitions(int[] numPartitions) {
		this.numPartitions = numPartitions;
	}


}
