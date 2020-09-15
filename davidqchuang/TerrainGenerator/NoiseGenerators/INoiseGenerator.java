package davidqchuang.TerrainGenerator.NoiseGenerators;

import davidqchuang.TerrainGenerator.Tools.float2;

public interface INoiseGenerator {
    float Generate(float x, float y);
    float Generate(float2 p);
}