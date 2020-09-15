package davidqchuang.TerrainGenerator.Visualization;

public class Vertex {
	double x;
	double y;
	double z;

	public Vertex(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vertex scale(double s) {
		this.x *= s;
		this.y *= s;
		this.z *= s;
		return this;
	}
}