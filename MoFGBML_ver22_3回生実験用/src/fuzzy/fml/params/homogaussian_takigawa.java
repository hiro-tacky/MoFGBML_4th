package fuzzy.fml.params;

public class homogaussian_takigawa {
	static float[][] parms = new float[][] {
		calcGaussParam(0f, 0.5f, 0.5f),
		calcGaussParam(1f, 0.5f, 0.5f),

		calcGaussParam(0f, 0.25f, 0.5f),
		calcGaussParam(0.5f, 0.25f, 0.5f),
		calcGaussParam(1f, 0.75f, 0.5f),

		calcGaussParam(0f, (1f/6f), 0.5f),
		calcGaussParam(1f/3f, (1f/6f), 0.5f),
		calcGaussParam(2f/3f, (3f/6f), 0.5f),
		calcGaussParam(1f, (5f/6f), 0.5f),

		calcGaussParam(0f, 0.125f, 0.5f),
		calcGaussParam(0.25f, 0.125f, 0.5f),
		calcGaussParam(0.5f, 0.375f, 0.5f),
		calcGaussParam(0.75f, 0.625f, 0.5f),
		calcGaussParam(1f, 0.875f, 0.5f)
	};

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

	public static float[][] get_parms(){
		return parms;
	}
}


