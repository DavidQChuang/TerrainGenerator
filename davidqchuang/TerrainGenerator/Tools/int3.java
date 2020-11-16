package davidqchuang.TerrainGenerator.Tools;

public class int3 {
	public int x;
	public int y;
	public int z;

	public final static int3 zero = new int3();

	public int3() {
		x = 0;
		y = 0;
		z = 0;
	}

	public int3(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public float3 toFloat3() {
		return new float3(x, y, z);
	}
}
