package davidqchuang.TerrainGenerator;

import davidqchuang.TerrainGenerator.Algorithms.*;
import davidqchuang.TerrainGenerator.NoiseGenerators.*;
import davidqchuang.TerrainGenerator.Tools.*;

public class HeightmapGenerator {
	public HeightmapNode[][] Nodes;

	public int Seed;

	public HeightmapGenerator(int mapX, int mapY, int seed) {
		Nodes = new HeightmapNode[mapX][mapY];
		Seed = seed;
	}

	public void generateBaseHeightmap(int2 offset) {
		int mapSizeX = Nodes.length;
		int mapSizeY = Nodes[0].length;
		
        float dampness = 8.0f;
        float flatness = 0.5f;
        float bumpness = 0.5f;
        float highness = 10.0f;

        OctaveParams oP1 = new OctaveParams();
        oP1.NoiseGen = new PerlinNoise(Seed + 0);
		oP1.Octaves = 8;
		oP1.AmplitudeMultiplier = 2.0f;
		oP1.FrequencyMultiplier = 0.5f;
		
        OctaveParams oP2 = new OctaveParams();
        oP2.NoiseGen = new PerlinNoise(Seed + 1);
        oP2.Octaves = 6;
        oP2.AmplitudeMultiplier = 2.0f;
        oP2.FrequencyMultiplier = 0.5f;
        
        OctaveNoise octaveNoise = new OctaveNoise(oP1);
        OctaveNoise octaveNoise2 = new OctaveNoise(oP2);
        
        CombinedParams cP1 = new CombinedParams();
        cP1.Noise1 = octaveNoise;
        cP1.Noise2 = octaveNoise;
        
        CombinedNoise combinedNoise = new CombinedNoise(cP1);

        System.out.println("Generating base heightmap.");
        //Generate the base heightmap.
        for (int x = 0; x < mapSizeX; x++) {
            for (int y = 0; y < mapSizeY; y++) {
                int cX = x + offset.x;
                int cY = y + offset.y;
                float heightResult;

                float heightLow = combinedNoise.Generate(cX * 1.3f, cY * 1.3f) / 6 - 4;
                float heightHigh = combinedNoise.Generate(cX * 1.3f, cY * 1.3f) / 5 + 6;

                if (octaveNoise2.Generate(cX, cY) / 8 > 0) {
                    heightResult = heightLow;
                } else {
                    heightResult = Math.max(heightLow, heightHigh);

                }
                heightResult /= 2;

                if (heightResult < 0) {
                    heightResult *= 0.8;
                }

                Nodes[x][y] = new HeightmapNode();
                Nodes[x][y].height = Math.abs(heightResult);
            }
            
            if (x % (mapSizeX / 4) != 0) continue;
            System.out.println("# Calculated " + x * mapSizeY + "/" + mapSizeX * mapSizeY + " coordinates.");
        }
        
        NoiseLerpParams nlP1 = new NoiseLerpParams();
        nlP1.NoiseGen = octaveNoise2;
        nlP1.Weight = 0.6f;
        nlP1.Coordinates = float2.zero;
        
        (new AlgorithmNoiseLerp()).Execute(Nodes, nlP1);

        (new LocalAlgorithm<Integer>() {
        	@Override
        	public HeightmapNode Execute(HeightmapNode node, Integer p){
                node.height += highness;
				return node;
            }
        }).Execute(Nodes, 0);

        ErosionParams rPs = new ErosionParams();
        rPs.IterationsPerTile = 1;
        rPs.DropletLifetime = 30;
        rPs.InitialSpeed = 1;
        rPs.InitialWater = 1;

        rPs.ErosionRadius = 3;

        rPs.Inertia = 0.05f;
        rPs.Friction = 0.1f;

        rPs.SedimentCapacityFactor = 4;
        rPs.MinimumSedimentCapacity = 0.01f;

        rPs.ErodeSpeed = 1.0f;
        rPs.DepositSpeed = 1.0f;
        rPs.EvaporateSpeed = 0.05f;
        rPs.DropletGravity = 4;

        rPs.Seed = Seed;
        
        rPs.InitialWater *= dampness;
        
        (new AlgorithmRain()).Execute(Nodes, rPs);

        (new LocalAlgorithm<Integer>() {
        	@Override
        	public HeightmapNode Execute(HeightmapNode node, Integer p){
                node.height = MoreMath.lerp(node.height, highness, bumpness);
                node.height = MoreMath.lerp(node.height, 0, flatness);
				return node;
            }
        }).Execute(Nodes, 0);

        (new AlgorithmSmooth()).Execute(Nodes, 0.6f);

        (new AlgorithmRound()).Execute(Nodes, 1.0f);
	}
}
