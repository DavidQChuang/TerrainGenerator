package davidqchuang.TerrainGenerator.Tools;

public class float3 {
	public float x;
	public float y;
	public float z;
	
	public final static float3 zero = new float3();

	public float3() {
		x = 0;
		y = 0;
		z = 0;
	}

	public float3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public float3(float2 v, float z) {
		this.x = v.x;
		this.y = v.y;
		this.z = z;
	}
	public float3(int x, int2 v) {
		this.x = x;
		this.y = v.x;
		this.z = v.y;
	}
}
