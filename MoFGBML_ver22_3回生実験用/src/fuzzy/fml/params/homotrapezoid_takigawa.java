package fuzzy.fml.params;

public class homotrapezoid_takigawa {
	static float[][] parms = new float[][] {

	new float[] {0f, 0f, 0.25f, 0.75f},
	new float[] {0.25f, 0.75f, 1f, 1f},

	new float[] {0f, 0f, 1f/8f, 3f/8f},
	new float[] {1f/8f, 3f/8f, 5f/8f, 7f/8f},
	new float[] {5f/8f, 7f/8f, 1f, 1f},

	new float[] {0f, 0f, 1f/12f, 3f/12f},
	new float[] {1f/12f, 3f/12f, 5f/12f, 7f/12f},
	new float[] {5f/12f, 7f/12f, 9f/12f, 11f/12f},
	new float[] {9f/12f, 11f/12f, 1f, 1f},

	new float[] {0f, 0f, 1f/16f, 3f/16f},
	new float[] {1f/16f, 3f/16f, 5f/16f, 7f/16f},
	new float[] {5f/16f, 7f/16f, 9f/16f, 11f/16f},
	new float[] {9f/16f, 11f/16f, 13f/16f, 15f/16f},
	new float[] {13f/16f, 15f/16f, 1f, 1f},

	};

	public static float[][] get_parms(){
		return parms;
	}
}
