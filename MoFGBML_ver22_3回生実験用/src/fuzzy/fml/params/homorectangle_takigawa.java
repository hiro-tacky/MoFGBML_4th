package fuzzy.fml.params;

public class homorectangle_takigawa {
	static float[][] parms = new float[][] {

		new float[] {0f, 1f/2f},
		new float[] {1f/2f, 1f},

		new float[] {0f, 1f/3f},
		new float[] {1f/3f, 2f/3f},
		new float[] {2f/3f, 1f},

		new float[] {0f, 1f/4f},
		new float[] {1f/4f, 2f/4f},
		new float[] {2f/4f, 3f/4f},
		new float[] {3f/4f, 1f},

		new float[] {0f, 1f/5f},
		new float[] {1f/5f, 2f/5f},
		new float[] {2f/5f, 3f/5f},
		new float[] {3f/5f, 4f/5f},
		new float[] {4f/5f, 1f},

	};

	public static float[][] get_parms(){
		return parms;
	}
}
