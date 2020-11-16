package davidqchuang.TerrainGenerator.NoiseGenerators;

import java.util.Random;

import davidqchuang.TerrainGenerator.Tools.*;

/// <summary>
/// Perlin noise generator taking a uint seed for the random number generation as a parameter.
/// </summary>
public class PerlinNoise extends NoiseGenerator<Integer> {
	private static int[] m_permutations;

	private static float2[] m_gradients;

	public PerlinNoise(Integer seed) {
		super(seed);

		m_permutations = CalculatePermutation();
		m_gradients = CalculateGradients();
	}

	private int[] CalculatePermutation() {
		var p = new int[256];

		for (var i = 0; i < p.length; i++) {
			p[i] = i;
		}

		/// shuffle the array
		for (var i = 0; i < p.length; i++) {
			var source = (int) (NumberGenerator.rand21(new float2(Params, i)) * p.length);

			var t = p[i];
			p[i] = p[source];
			p[source] = t;
		}
		
		return p;
	}

	private float2[] CalculateGradients() {
		var grad = new float2[256];

		for (var i = 0; i < grad.length; i++) {
			float2 gradient = new float2();

			gradient.x = (NumberGenerator.rand21(new float2(Params, i)) * 2 - 1);
			gradient.y = (NumberGenerator.rand21(new float2(gradient.x, Params.intValue())) * 2 - 1);

			grad[i] = MoreMath.normalize(gradient);
		}

		return grad;
	}

	private static float Drop(float t) {
		t = Math.abs(t);
		return 1f - t * t * t * (t * (t * 6 - 15) + 10);
	}

	private static float Q(float u, float v) {
		return Drop(u) * Drop(v);
	}

	public float Generate(float2 v) {
		return Generate(v.x, v.y);
	}

	@Override
	public float Generate(float x, float y) {
		var cell = new float2((int) x, (int) y);

		float total;

		total = AddCorner(x, y, cell, new float2(0, 0));
		total += AddCorner(x, y, cell, new float2(0, 1));
		total += AddCorner(x, y, cell, new float2(1, 0));
		total += AddCorner(x, y, cell, new float2(1, 1));

		return Math.max(Math.min(total, 1f), -1f);
	}

	float AddCorner(float x, float y, float2 cell, float2 n) {
		var ijx = cell.x + n.x;
		var ijy = cell.y + n.y;

		var uvx = x - ijx;
		var uvy = y - ijy;

		if (ijx < 0)
			ijx = m_permutations.length + ijx % m_permutations.length;
		if (ijy < 0)
			ijy = m_permutations.length + ijy % m_permutations.length;

		int index;
		index = m_permutations[(int) ijx % m_permutations.length];
		index = m_permutations[(index + (int) ijy) % m_permutations.length];

		var grad = m_gradients[index % m_gradients.length];

		return Q(uvx, uvy) * MoreMath.dot(grad, new float2(uvx, uvy));
	}
}