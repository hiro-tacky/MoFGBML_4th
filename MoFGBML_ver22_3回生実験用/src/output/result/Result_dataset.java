package output.result;

import data.Input;
import data.SingleDataSetInfo;

public class Result_dataset {
	SingleDataSetInfo Dtst = new SingleDataSetInfo();

	public Result_dataset(String tstFile) {
		Input.inputFile(this.Dtst, tstFile);
	}

}
