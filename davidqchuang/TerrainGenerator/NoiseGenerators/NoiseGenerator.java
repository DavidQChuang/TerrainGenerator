package davidqchuang.TerrainGenerator.NoiseGenerators;

import davidqchuang.TerrainGenerator.Tools.float2;

public abstract class NoiseGenerator<TParam> implements INoiseGenerator {
	public TParam Params;

	public NoiseGenerator(TParam p) {
		Params = p;
	}

	@Override
	public float Generate(float x, float y) {
		return Generate(new float2(x, y));
	}

	@Override
	public abstract float Generate(float2 y);
}