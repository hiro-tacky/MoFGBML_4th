package output.result;

import data.Input;
import data.SingleDataSetInfo;
import main.Setting;

public class Result_dataset {
	SingleDataSetInfo[] Dtst = new SingleDataSetInfo[Setting.repeatTimes * Setting.crossValidationNum];

	private Result_dataset() {};

	public void SetResultDataset(String tstFile, int trialID) {
		Input.inputFile(this.Dtst[trialID], tstFile);
	}

}
