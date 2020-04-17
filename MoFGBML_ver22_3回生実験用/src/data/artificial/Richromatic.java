package data.artificial;

import java.util.ArrayList;

import method.MersenneTwisterFast;
import method.Output;

public class Richromatic {

	static int h = 500;
	static int dataSize = 20000;

//	static double a = 0.3;
//	static double b = 0.33;
//	static double r = 0.3;
	static double a = 0.2;
	static double b = 0.4;
	static double r = 0.5;

	public static void makeGrid() {
		//Make Grid
		int dataSize = (h+1)*(h+1);
		/* ********************************************************* */

		//Attribute
		int Ndim = 2;
		ArrayList<double[]> grid_vertical = new ArrayList<>();
		ArrayList<double[]> grid_horizontal = new ArrayList<>();
		for(int xx = 0; xx < h+1; xx++) {
			for(int yy = 0; yy < h+1; yy++) {
				double[] xy = new double[Ndim];
				xy[0] = (double)yy * 1.0/(double)h;
				xy[1] = (double)xx * 1.0/(double)h;
				grid_vertical.add(xy);

				xy = new double[Ndim];
				xy[0] = (double)xx * 1.0/(double)h;
				xy[1] = (double)yy * 1.0/(double)h;
				grid_horizontal.add(xy);
			}
		}

		//Label
		int Lnum = 3;
		ArrayList<int[]> labels_vertical = new ArrayList<>();
		ArrayList<int[]> labels_horizontal = new ArrayList<>();
		/* *************** */
		//Definition of richromatic
		double[][] centers = new double[Lnum][Ndim];	//#of label, #of features
		centers[0] = new double[] {a, b};
		centers[1] = new double[] {1.0-a, b};
		centers[2] = new double[] {0.5, b + (1.0-2.0*a)/2.0 * Math.sqrt(3.0)};
		/* *************** */

		for(int p = 0; p < dataSize; p++) {
			int[] label = new int[Lnum];
			//vertical
			for(int l = 0; l < Lnum; l++) {
				double x = grid_vertical.get(p)[0] - centers[l][0];
				double y = grid_vertical.get(p)[1] - centers[l][1];
				if((x*x + y*y) <= r*r) {
					label[l] = 1;
				}
				else {
					label[l] = 0;
				}
			}
			labels_vertical.add(label);

			//horizontal
			label = new int[Lnum];
			for(int l = 0; l < Lnum; l++) {
				double x = grid_horizontal.get(p)[0] - centers[l][0];
				double y = grid_horizontal.get(p)[1] - centers[l][1];
				if((x*x + y*y) <= r*r) {
					label[l] = 1;
				}
				else {
					label[l] = 0;
				}
			}
			labels_horizontal.add(label);
		}

		ArrayList<String> strs = new ArrayList<>();
		String str = "";
		String gridName;

		//Vertical
		for(int p = 0; p < grid_vertical.size(); p++) {
			str = String.valueOf(grid_vertical.get(p)[0]);
			str += "," + String.valueOf(grid_vertical.get(p)[1]);

			for(int l = 0; l < Lnum; l++) {
				str += "," + String.valueOf(labels_vertical.get(p)[l]);
			}
			str += ",";
			strs.add(str);
		}
		strs.add(0, String.valueOf(strs.size()) + "," + Ndim + "," + Lnum);

		gridName = "richromatic_grid_vertical_h-" + h + ".csv";
		Output.writeln(gridName, strs);

		//Horizontal
		strs = new ArrayList<>();
		str = "";
		for(int p = 0; p < grid_horizontal.size(); p++) {
			str = String.valueOf(grid_horizontal.get(p)[0]);
			str += "," + String.valueOf(grid_horizontal.get(p)[1]);

			for(int l = 0; l < Lnum; l++) {
				str += "," + String.valueOf(labels_horizontal.get(p)[l]);
			}
			str += ",";
			strs.add(str);
		}
		strs.add(0, String.valueOf(strs.size()) + "," + Ndim + "," + Lnum);
		gridName = "richromatic_grid_horizontal_h-" + h + ".csv";
		Output.writeln(gridName, strs);

		//Dtst(except label=(0,0,0))
		strs = new ArrayList<>();
		str = "";
		for(int p = 0; p < grid_horizontal.size(); p++) {
			str = String.valueOf(grid_horizontal.get(p)[0]);
			str += "," + String.valueOf(grid_horizontal.get(p)[1]);

//			boolean nonLabel = true;
			for(int l = 0; l < Lnum; l++) {
//				if(labels_horizontal.get(p)[l] == 1) {
//					nonLabel = false;
//				}
				str += "," + String.valueOf(labels_horizontal.get(p)[l]);
			}
//			if(nonLabel) {
//				continue;
//			}
			str += ",";
			strs.add(str);
		}
		strs.add(0, String.valueOf(strs.size()) + "," + Ndim + "," + Lnum);
		gridName = "richromatic_Dtst_h-" + h + ".csv";
		Output.writeln(gridName, strs);
	}

	public static void makeRichromatic() {
		int seed = 2019;
		MersenneTwisterFast rnd = new MersenneTwisterFast(seed);
		int Ndim = 2;
		int Lnum = 3;

		//#of label, #of features
		double[][] centers = new double[Lnum][Ndim];

		centers[0] = new double[] {a, b};
		centers[1] = new double[] {1.0-a, b};
		centers[2] = new double[] {0.5, b + (1.0-2.0*a)/2.0 * Math.sqrt(3.0)};

		double[][] patterns = new double[dataSize][Ndim];
		int[][] label = new int[dataSize][Lnum];

		int nowSize = 0;
		while(nowSize < dataSize) {

			patterns[nowSize][0] = rnd.nextDoubleII();
			patterns[nowSize][1] = rnd.nextDoubleII();

//			boolean nonLabel = true;
			for(int i = 0; i < Lnum; i++) {
				double x = patterns[nowSize][0] - centers[i][0];
				double y = patterns[nowSize][1] - centers[i][1];
				if((x*x + y*y) <= r*r) {
					label[nowSize][i] = 1;
//					nonLabel = false;
				}
				else {
					label[nowSize][i] = 0;
				}
			}

//			if(!nonLabel) {
//				nowSize++;
//			}
			nowSize++;
		}

		ArrayList<String> strs = new ArrayList<>();
		String str;

		str = "" + dataSize + "," + Ndim + "," + Lnum + ",";
		strs.add(str);

		for(int p= 0; p < dataSize; p++) {
			str = "";
			for(int n = 0; n < Ndim; n++) {
				str += patterns[p][n] + ",";
			}
			for(int l = 0; l < Lnum; l++) {
				str += label[p][l] + ",";
			}
			strs.add(str);
		}

		String fileName = "richromatic_" + dataSize + ".dat";
		Output.writeln(fileName, strs);

	}

	public static void makeArtificialSquare(String[] args) {
		int seed = 2019;
		MersenneTwisterFast rnd = new MersenneTwisterFast(seed);
		int dataSize = 1000;
		int Ndim = 2;
		int Lnum = 4;

		double r = 0.5;
		double[][] centers = new double[Lnum][Ndim];	//#of label, #of features
		double a = 0.3;

		centers[0] = new double[] {a, a};
		centers[1] = new double[] {1.0-a, a};
		centers[2] = new double[] {a, 1.0-a};
		centers[3] = new double[] {1.0-a, 1.0-a};


		double[][] patterns = new double[dataSize][Ndim];
		int[][] label = new int[dataSize][Lnum];

		int nowSize = 0;
		while(nowSize < dataSize) {

			patterns[nowSize][0] = rnd.nextDoubleII();
			patterns[nowSize][1] = rnd.nextDoubleII();

			boolean nonLabel = true;
			for(int i = 0; i < Lnum; i++) {
				double x = patterns[nowSize][0] - centers[i][0];
				double y = patterns[nowSize][1] - centers[i][1];
				if((x*x + y*y) <= r*r) {
					label[nowSize][i] = 1;
					nonLabel = false;
				}
				else {
					label[nowSize][i] = 0;
				}
			}

			if(!nonLabel) {
				nowSize++;
			}
		}

		ArrayList<String> strs = new ArrayList<>();
		String str;

		str = "" + dataSize + "," + Ndim + "," + Lnum + ",";
		strs.add(str);

		for(int p= 0; p < dataSize; p++) {
			str = "";
			for(int n = 0; n < Ndim; n++) {
				str += patterns[p][n] + ",";
			}
			for(int l = 0; l < Lnum; l++) {
				str += label[p][l] + ",";
			}
			strs.add(str);
		}

		String fileName = "ninja.dat";
		Output.writeln(fileName, strs);

	}
}
