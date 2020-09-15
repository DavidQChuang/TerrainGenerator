package davidqchuang.TerrainGenerator.NoiseGenerators;

import davidqchuang.TerrainGenerator.Tools.float2;

public class CombinedNoise extends NoiseGenerator<CombinedParams> {

	public CombinedNoise(CombinedParams p) {
		super(p);
	}

	@Override
	public float Generate(float2 coords) {
		float offset = Params.Noise2.Generate(coords);
		return Params.Noise1.Generate(new float2(offset + coords.x, coords.y));
	}
}