package davidqchuang.TerrainGenerator;

import java.util.ArrayList;
import java.util.List;

import davidqchuang.TerrainGenerator.Algorithms.*;
import davidqchuang.TerrainGenerator.NoiseGenerators.*;
import davidqchuang.TerrainGenerator.Tools.*;

public class HeightmapGenerator {
	public int Seed;

	public int mapSizeX, mapSizeY;
	
	public HeightmapGenerator(int mapX, int mapY, int seed) {
		Seed = seed;
		
		mapSizeX = mapX;
		mapSizeY = mapY;
	}
	
	public HeightmapNode[][] generateBaseHeightmap(int2 offset){
		return generateBaseHeightmap(offset, 
			0.32f,
         	0.20f,
         	0.10f);
	}

	public HeightmapNode[][] generateBaseHeightmap(int2 offset, float dampness, float flatness, float highness) {
		HeightmapNode[][] Nodes = new HeightmapNode[mapSizeX][];
		for(int x = 0; x < mapSizeX; x++) {
			Nodes[x] = new HeightmapNode[mapSizeY];
		}
        
        float flatness2Weight = (float)Math.sqrt(flatness);
        float highness2Units = highness * 100;

        OctaveParams oP1 = new OctaveParams();
        oP1.NoiseGen = new PerlinNoise(NumberGenerator.hash(Seed));
		oP1.Octaves = 8;
		oP1.AmplitudeMultiplier = 0.25f;
		oP1.FrequencyMultiplier = 2.0f;
        OctaveNoise octaveNoise = new OctaveNoise(oP1);
		
        OctaveParams oP2 = new OctaveParams();
        oP2.NoiseGen = octaveNoise;
        oP2.Octaves = 8;
        oP2.AmplitudeMultiplier = 2.0f;
        oP2.FrequencyMultiplier = 0.5f;
        OctaveNoise octaveNoise2 = new OctaveNoise(oP2);

        // Generates base heights and sets moistures.
        // y<0 is moisture 6.
        (new Algorithm<Integer>() {
        	@Override
        	public HeightmapNode[][] Execute(HeightmapNode[][] n, Integer p){
                for (int x = 0; x < n.length; x++) {
                    for (int y = 0; y < n[0].length; y++) {
                        HeightmapNode node = new HeightmapNode();

                        node.height = (octaveNoise2.Generate(x, y));
                        
                        // moisture setting
                        if(node.height < 0)
                        	node.moisture = 6;
                        else
                        	node.moisture = dampness;

                        node.height = (node.height * flatness2Weight);
                        node.height = MoreMath.lerp(node.height, highness2Units, flatness2Weight);

                        n[x][y] = node;
                    }
                }
                return n;
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
        
        // doesn't do anything right now, may fix.
        (new AlgorithmRain()).Execute(Nodes, rPs);

        (new AlgorithmSmooth()).Execute(Nodes, 0.6f);

        (new AlgorithmRound()).Execute(Nodes, 1.0f);
        
        return Nodes;
	}
	
	public HeightmapNode[][] distributeMoisture(HeightmapNode[][] Nodes) {
        System.out.println("Generating moistures.");

        System.out.println("Finding water nodes.");
        List<int2> waterCoords = new ArrayList<int2>();
        for(int x = 0; x < mapSizeX; x++) {
            for(int y = 0; y < mapSizeY; y++) {
                if (Biomes.GetStrata(Nodes[x][y].moisture, Biomes.MoistureStrata) == Biomes.MoistureStrata.length - 1) {
                    waterCoords.add(new int2(x, y));
                }
            }
        }

        System.out.println("Accumulating moisture by distance to water nodes.");
        for(int x = 0; x < mapSizeX; x++) {
            for(int y = 0; y < mapSizeY; y++) {
                int2 coords = new int2(x, y);
                if (waterCoords.contains(coords)) continue;

                float accumulatedMoisture = 0;
                // 'y', aka 'height' is stored in the z component of the int3 here for consistency with rest of the x-y heightmap-oriented code. 
                int3 p1 = new int3(x, y, (int)Nodes[x][y].height);

	            for(int2 p : waterCoords) {
                    if (accumulatedMoisture > 7) break;

                    int3 p2 = new int3(p.x, p.y, (int)Nodes[p.x][p.y].height);

                    float dist = MoreMath.distance(p1, p2);
                    accumulatedMoisture += dist / Math.pow(dist, 2) * 0.8f + 0.1f;
                }

                HeightmapNode node = Nodes[x][y];
                node.moisture += accumulatedMoisture;
                Nodes[x][y] = node;
            }
            if (x % (mapSizeX / 4) != 0) continue;
            System.out.println("# Calculated " + x * mapSizeY + "/" + mapSizeX * mapSizeY + " coordinates.");
        }
        
        return Nodes;
	}
}
