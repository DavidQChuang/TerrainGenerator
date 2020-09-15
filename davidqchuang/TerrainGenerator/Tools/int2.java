package davidqchuang.TerrainGenerator.Tools;

public class int2 {
	public int x;
	public int y;
	
	public final static int2 zero = new int2();

	public int2() {
		x = 0;
		y = 0;
	}

	public int2(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public int2(float2 v) {
		this.x = (int) v.x;
		this.y = (int) v.y;
	}
}
