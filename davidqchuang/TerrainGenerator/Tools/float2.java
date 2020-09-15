package davidqchuang.TerrainGenerator.Tools;

public class float2 {
	public float x;
	public float y;
	
	public final static float2 zero = new float2();

	public float2() {
		x = 0;
		y = 0;
	}

	public float2(float x, float y) {
		this.x = x;
		this.y = y;
	}
}
