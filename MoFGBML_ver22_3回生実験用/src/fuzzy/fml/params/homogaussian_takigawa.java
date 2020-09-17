package fuzzy.fml.params;

public class homogaussian_takigawa {
	static float[][] parms = new float[][] {
		//2
		calcGaussParam(0f, 1f/2f, 0.5f),
		calcGaussParam(1f, 1f/2f, 0.5f),
		//3
		calcGaussParam(0f, 1f/4f, 0.5f),
		calcGaussParam(1f/2f, 1f/4f, 0.5f),
		calcGaussParam(1f, 3f/4f, 0.5f),
		//4
		calcGaussParam(0f, 1f/6f, 0.5f),
		calcGaussParam(1f/3f, 1f/6f, 0.5f),
		calcGaussParam(2f/3f, 1f/2f, 0.5f),
		calcGaussParam(1f, 5f/6f, 0.5f),
		//5
		calcGaussParam(0f, 1f/8f, 0.5f),
		calcGaussParam(1f/4f, 1f/8f, 0.5f),
		calcGaussParam(1f/2f, 3f/8f, 0.5f),
		calcGaussParam(3f/4f, 5f/8f, 0.5f),
		calcGaussParam(1f, 7f/8f, 0.5f),
//		//6
//		calcGaussParam(0f, 1f/10f, 0.5f),
//		calcGaussParam(1f/5f, 1f/10f, 0.5f),
//		calcGaussParam(2f/5f, 3f/10f, 0.5f),
//		calcGaussParam(3f/5f, 1f/2f, 0.5f),
//		calcGaussParam(4f/5f, 7f/10f, 0.5f),
//		calcGaussParam(1f, 9f/10f, 0.5f),
//		//7
//		calcGaussParam(0f, 1f/12f, 0.5f),
//		calcGaussParam(1f/6f, 1f/12f, 0.5f),
//		calcGaussParam(1f/3f, 1f/4f, 0.5f),
//		calcGaussParam(1f/2f, 5f/12f, 0.5f),
//		calcGaussParam(2f/3f, 7f/12f, 0.5f),
//		calcGaussParam(5f/6f, 3f/4f, 0.5f),
//		calcGaussParam(1f, 11f/12f, 0.5f),
//		//8
//		calcGaussParam(0f, 1f/14f, 0.5f),
//		calcGaussParam(1f/7f, 1f/14f, 0.5f),
//		calcGaussParam(2f/7f, 3f/14f, 0.5f),
//		calcGaussParam(3f/7f, 5f/14f, 0.5f),
//		calcGaussParam(4f/7f, 1f/2f, 0.5f),
//		calcGaussParam(5f/7f, 9f/14f, 0.5f),
//		calcGaussParam(6f/7f, 11f/14f, 0.5f),
//		calcGaussParam(1f, 13f/14f, 0.5f),
//		//9
//		calcGaussParam(0f, 1f/16f, 0.5f),
//		calcGaussParam(1f/8f, 1f/16f, 0.5f),
//		calcGaussParam(1f/4f, 3f/16f, 0.5f),
//		calcGaussParam(3f/8f, 5f/16f, 0.5f),
//		calcGaussParam(1f/2f, 7f/16f, 0.5f),
//		calcGaussParam(5f/8f, 9f/16f, 0.5f),
//		calcGaussParam(3f/4f, 11f/16f, 0.5f),
//		calcGaussParam(7f/8f, 13f/16f, 0.5f),
//		calcGaussParam(1f, 15f/16f, 0.5f),
//		//10
//		calcGaussParam(0f, 1f/18f, 0.5f),
//		calcGaussParam(1f/9f, 1f/18f, 0.5f),
//		calcGaussParam(2f/9f, 1f/6f, 0.5f),
//		calcGaussParam(1f/3f, 5f/18f, 0.5f),
//		calcGaussParam(4f/9f, 7f/18f, 0.5f),
//		calcGaussParam(5f/9f, 1f/2f, 0.5f),
//		calcGaussParam(2f/3f, 11f/18f, 0.5f),
//		calcGaussParam(7f/9f, 13f/18f, 0.5f),
//		calcGaussParam(8f/9f, 5f/6f, 0.5f),
//		calcGaussParam(1f, 17f/18f, 0.5f),
//		//11
//		calcGaussParam(0f, 1f/20f, 0.5f),
//		calcGaussParam(1f/10f, 1f/20f, 0.5f),
//		calcGaussParam(1f/5f, 3f/20f, 0.5f),
//		calcGaussParam(3f/10f, 1f/4f, 0.5f),
//		calcGaussParam(2f/5f, 7f/20f, 0.5f),
//		calcGaussParam(1f/2f, 9f/20f, 0.5f),
//		calcGaussParam(3f/5f, 11f/20f, 0.5f),
//		calcGaussParam(7f/10f, 13f/20f, 0.5f),
//		calcGaussParam(4f/5f, 3f/4f, 0.5f),
//		calcGaussParam(9f/10f, 17f/20f, 0.5f),
//		calcGaussParam(1f, 19f/20f, 0.5f),
//		//12
//		calcGaussParam(0f, 1f/22f, 0.5f),
//		calcGaussParam(1f/11f, 1f/22f, 0.5f),
//		calcGaussParam(2f/11f, 3f/22f, 0.5f),
//		calcGaussParam(3f/11f, 5f/22f, 0.5f),
//		calcGaussParam(4f/11f, 7f/22f, 0.5f),
//		calcGaussParam(5f/11f, 9f/22f, 0.5f),
//		calcGaussParam(6f/11f, 1f/2f, 0.5f),
//		calcGaussParam(7f/11f, 13f/22f, 0.5f),
//		calcGaussParam(8f/11f, 15f/22f, 0.5f),
//		calcGaussParam(9f/11f, 17f/22f, 0.5f),
//		calcGaussParam(10f/11f, 19f/22f, 0.5f),
//		calcGaussParam(1f, 21f/22f, 0.5f),
//		//13
//		calcGaussParam(0f, 1f/24f, 0.5f),
//		calcGaussParam(1f/12f, 1f/24f, 0.5f),
//		calcGaussParam(1f/6f, 1f/8f, 0.5f),
//		calcGaussParam(1f/4f, 5f/24f, 0.5f),
//		calcGaussParam(1f/3f, 7f/24f, 0.5f),
//		calcGaussParam(5f/12f, 3f/8f, 0.5f),
//		calcGaussParam(1f/2f, 11f/24f, 0.5f),
//		calcGaussParam(7f/12f, 13f/24f, 0.5f),
//		calcGaussParam(2f/3f, 5f/8f, 0.5f),
//		calcGaussParam(3f/4f, 17f/24f, 0.5f),
//		calcGaussParam(5f/6f, 19f/24f, 0.5f),
//		calcGaussParam(11f/12f, 7f/8f, 0.5f),
//		calcGaussParam(1f, 23f/24f, 0.5f),
//		//14
//		calcGaussParam(0f, 1f/26f, 0.5f),
//		calcGaussParam(1f/13f, 1f/26f, 0.5f),
//		calcGaussParam(2f/13f, 3f/26f, 0.5f),
//		calcGaussParam(3f/13f, 5f/26f, 0.5f),
//		calcGaussParam(4f/13f, 7f/26f, 0.5f),
//		calcGaussParam(5f/13f, 9f/26f, 0.5f),
//		calcGaussParam(6f/13f, 11f/26f, 0.5f),
//		calcGaussParam(7f/13f, 1f/2f, 0.5f),
//		calcGaussParam(8f/13f, 15f/26f, 0.5f),
//		calcGaussParam(9f/13f, 17f/26f, 0.5f),
//		calcGaussParam(10f/13f, 19f/26f, 0.5f),
//		calcGaussParam(11f/13f, 21f/26f, 0.5f),
//		calcGaussParam(12f/13f, 23f/26f, 0.5f),
//		calcGaussParam(1f, 25f/26f, 0.5f),
//		//15
//		calcGaussParam(0f, 1f/28f, 0.5f),
//		calcGaussParam(1f/14f, 1f/28f, 0.5f),
//		calcGaussParam(1f/7f, 3f/28f, 0.5f),
//		calcGaussParam(3f/14f, 5f/28f, 0.5f),
//		calcGaussParam(2f/7f, 1f/4f, 0.5f),
//		calcGaussParam(5f/14f, 9f/28f, 0.5f),
//		calcGaussParam(3f/7f, 11f/28f, 0.5f),
//		calcGaussParam(1f/2f, 13f/28f, 0.5f),
//		calcGaussParam(4f/7f, 15f/28f, 0.5f),
//		calcGaussParam(9f/14f, 17f/28f, 0.5f),
//		calcGaussParam(5f/7f, 19f/28f, 0.5f),
//		calcGaussParam(11f/14f, 3f/4f, 0.5f),
//		calcGaussParam(6f/7f, 23f/28f, 0.5f),
//		calcGaussParam(13f/14f, 25f/28f, 0.5f),
//		calcGaussParam(1f, 27f/28f, 0.5f),
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


