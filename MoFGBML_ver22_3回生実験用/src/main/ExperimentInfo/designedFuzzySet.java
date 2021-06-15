package main.ExperimentInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

public class designedFuzzySet {
	int Ndim;
	ArrayList<String> FuzzyTypeName = new ArrayList<String>();
	ArrayList<Integer> FuzzyTypeID = new ArrayList<Integer>();
	ArrayList<int[]> ParitionNumList = new ArrayList<int[]>();
	ArrayList<Integer> PartitonType = new ArrayList<Integer>(); //0: 等分割 1:エントロピー導出分割
	designedFuzzySet(int Ndim){
		this.Ndim = Ndim;
	}
	/**
	 * ファジィセットの設定を追加する
	 *
	 * @param FuzzyTypeName ファジィセットの名前
	 * @param FuzzyTypeID ファジィセットのID
	 * @param ParitionNumList 分割数のリスト
	 */
	void addDesignedFuzzySet(String FuzzyTypeName, int FuzzyTypeID, int[] ParitionNumList, int PartitonType){
		this.FuzzyTypeName.add(FuzzyTypeName);
		this.FuzzyTypeID.add(FuzzyTypeID);
		this.ParitionNumList.add(ParitionNumList);
		this.PartitonType.add(PartitonType);
	}


	public String getFuzzyTypeName(int ID) {
		return FuzzyTypeName.get(ID);
	}
	public void setFuzzyTypeName(String fuzzyTypeName, int ID) {
		FuzzyTypeName.set(ID, fuzzyTypeName);
	}
	public Integer getFuzzyTypeID(int ID) {
		return FuzzyTypeID.get(ID);
	}
	public void setFuzzyTypeID(int fuzzyTypeID, int ID) {
		FuzzyTypeID.set(ID, fuzzyTypeID);
	}
	public int[] getParitionNumList(int ID) {
		return ParitionNumList.get(ID);
	}
	public void setParitionNumList(int[] paritionNumList, int ID) {
		ParitionNumList.set(ID, paritionNumList);
	}
	public Integer getPartitonType(int ID) {
		return PartitonType.get(ID);
	}
	public void setPartitonType(Integer partitonType, int ID) {
		PartitonType.set(ID, partitonType);
	}
	public int getParitionSum(){
		int sum = 0;
		for(int[] ParitionNumList_tmp : ParitionNumList) {
			for(int tmp: ParitionNumList_tmp) {
				sum += tmp;
			}
		}
		return sum;
	}

	public int getFuzzySetTypeNum() {
		return FuzzyTypeID.size();
	}

	public String toString() {
		String str = new String();
		String sep = System.lineSeparator();
		for(int i=0; i<FuzzyTypeID.size(); i++) {
			for(Field field : this.getClass().getDeclaredFields()) {
				try {
					if(field.get(this) instanceof ArrayList) {
						str += "\t" + field.getName() + " = " + exchange(((ArrayList<?>) field.get(this)).get(i)) + sep;
					}else {
						str += "\t" + field.getName() + " = " + field.get(this) + sep;
					}
				} catch(IllegalAccessException e) {
					/** **/
				}
			}
			str += sep;
		}
		return str;
	}

	public String exchange(Object obj) {
		String str = new String();
		if(obj instanceof ArrayList) {
			for(Object buf :(ArrayList<?>) obj) {
				str += buf.toString();
			}
		}else if(obj.getClass().isArray()) {
			str += Arrays.toString((int[]) obj);
		}else {
			str = obj.toString();
		}
		return str;
	}
}