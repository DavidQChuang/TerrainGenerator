package davidqchuang.TerrainGenerator.NoiseGenerators;

import davidqchuang.TerrainGenerator.Tools.*;

public class PerlinNoise extends NoiseGenerator<Integer> {
	// Dictionary<int2, float2> GradientMap = new Dictionary<int2, float2>();

	public PerlinNoise(int p) {
		super(p);
		// GradientMap = new Dictionary<int2, float2>();
	}

	float2 generateGradient(int2 coords) {
		double radians = (NumberGenerator.rand31(new float3(Params, coords)) * Math.PI * 2);
		return new float2((float) Math.cos(coords.x * radians), (float) Math.sin(coords.y * radians));
	}

	float getGradient(int ix, int iy, float2 fVec) {
		return getGradient(new int2(ix, iy), fVec);
	}

	// Computes the dot product of the distance and gradient vectors.
	float getGradient(int2 iVec, float2 fVec) {
		// if (!GradientMap.ContainsKey(iVec)) GradientMap[iVec] =
		// generateGradient(iVec);

		// Compute the distance vector
		float2 dist = new float2(fVec.x - iVec.x, fVec.y - iVec.y);
		// Grab the gradient
		float2 grad = generateGradient(iVec);

		// Compute the dot-product
		return MoreMath.dot(dist, grad);
	}

	// Compute Perlin noise at coordinates x, y
	@Override
	public float Generate(float2 coords) {
        // Determine grid cell coordinates
        int x0 = (int)coords.x;
        int x1 = x0 + 1;
        int y0 = (int)coords.y;
        int y1 = y0 + 1;

        // Determine interpolation weights
        // Could also use higher order polynomial/s-curve here
        float2 weights = new float2(coords.x - x0, coords.y - y0);

        // Interpolate between grid point gradients
        float value, n0, n1, ix0, ix1;

        n0 = getGradient(x0, y0, coords);
        n1 = getGradient(x1, y0, coords);
        ix0 = MoreMath.lerp(n0, n1, weights.x);

        n0 = getGradient(x0, y1, coords);
        n1 = getGradient(x1, y1, coords);
        ix1 = MoreMath.lerp(n0, n1, weights.x);

        value = MoreMath.lerp(ix0, ix1, weights.y);
        return value;
    }
}