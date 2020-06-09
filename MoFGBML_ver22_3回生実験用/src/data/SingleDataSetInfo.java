package data;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SingleDataSetInfo extends DataSetInfo<SinglePattern>{
	// ************************************************************
	ArrayList<Double> average;
	// ************************************************************
	public SingleDataSetInfo() {}

	//並列分散実装用
	@Deprecated
	public SingleDataSetInfo(int Datasize, int Ndim, int Cnum, int setting, InetSocketAddress[] serverList){
		this.DataSize = Datasize;
		this.Ndim = Ndim;
		this.Cnum = Cnum;

		this.setting = setting;
		this.serverList = serverList;
	}

	@Deprecated
	public SingleDataSetInfo(int Ndim, int Cnum, int DataSize, ArrayList<SinglePattern> patterns) {
		this.Ndim = Ndim;
		this.Cnum = Cnum;
		this.DataSize = DataSize;
		this.patterns = patterns;
	}

	// ************************************************************

	public DataSetInfo<SinglePattern> newInstance(){
		return new SingleDataSetInfo();
	}

	public void sortPattern() {
		Collections.sort(this.patterns, new patternComparatorByConClass() );
	}

	public void calcAverage() {
		for(int i=0; i<this.patterns.get(0).getNdim(); i++) {
			double buf = 0;
			for(SinglePattern v: this.patterns) {
				buf += v.getDimValue(i);
			}
			buf /= this.getDataSize();
			average.add(buf);
		}
	}


	public ArrayList<Double> getAverage() {
		return average;
	}


	/**
	 * Sort by Conclusion Class
	 *
	 */
	public class patternComparatorByConClass implements Comparator<SinglePattern> {
		@Override
		public int compare(SinglePattern a, SinglePattern b) {
			double no1 = a.getConClass();
			double no2 = b.getConClass();

			if(no1 > no2) {
				return 1;
			} else if(no1 == no2) {
				return 0;
			} else {
				return -1;
			}
		}
	}

}
