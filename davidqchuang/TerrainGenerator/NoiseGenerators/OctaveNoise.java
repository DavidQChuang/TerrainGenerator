package davidqchuang.TerrainGenerator.NoiseGenerators;

import davidqchuang.TerrainGenerator.Tools.float2;

public class OctaveNoise extends NoiseGenerator<OctaveParams> {
    public OctaveNoise(OctaveParams p) {
    	super(p);
    }

    @Override
    public float Generate(float2 coords) {
        float amplitude = 1, freq = 1;
        float sum = 0;
        int i;

        for (i = 0; i < Params.Octaves; i++) {
            sum += Params.NoiseGen.Generate(new float2(coords.x * freq, coords.y * freq)) * amplitude;
            amplitude *= Params.AmplitudeMultiplier;
            freq *= Params.FrequencyMultiplier;
        }
        return sum;
    }
}