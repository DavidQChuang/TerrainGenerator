package davidqchuang.TerrainGenerator.Tools;

public class MoreMath {
	public static float lerp(float x, float y, float k) {
		return x * k + y * (1 - k);
	}

	public static float2 lerp(float2 v1, float2 v2, float k) {
		return new float2(lerp(v1.x, v2.x, k), lerp(v1.y, v2.y, k));
	}
	
	public static float distance(float3 v1, float3 v2) {
		float x = v1.x - v2.x;
		float y = v1.y - v2.y;
		float z = v1.z - v2.z;
		
		return (float) Math.sqrt(x*x + y*y + z*z);
	}
	
	public static float distance(int2 v1, int2 v2) {
		int x = v1.x - v2.x;
		int y = v1.y - v2.y;
		
		return (float) Math.sqrt(x*x + y*y);
	}
	public static float distance(int2 v1, float2 v2) {
		float x = v1.x - v2.x;
		float y = v1.y - v2.y;
		
		return (float) Math.sqrt(x*x + y*y);
	}


	public static float dot(float2 v1, float2 v2) {
		return v1.x * v2.x + v1.y * v2.y;
	}
}
