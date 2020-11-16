package davidqchuang.TerrainGenerator.Algorithms;

import davidqchuang.TerrainGenerator.HeightmapNode;
import davidqchuang.TerrainGenerator.Tools.*;

public class AlgorithmRain extends Algorithm<ErosionParams> {
	@Override
    public HeightmapNode[][] Execute(HeightmapNode[][] nodes, ErosionParams p) {
        System.out.println("Eroding heightmap.");

        int2 mapSize = new int2(nodes.length, nodes[0].length);

        for (int i = 0; i < p.IterationsPerTile * mapSize.x * mapSize.y; i++) {
            float2 pos = NumberGenerator.rand22(new float2(p.Seed, i));
            pos.x *= mapSize.x - 1; pos.y *= mapSize.y - 1;
            float2 velocitySide = NumberGenerator.rand22(new float2(p.Seed, i));
            velocitySide.x *= p.InitialSpeed; velocitySide.y *= p.InitialSpeed;

            float water = p.InitialWater;

            float sediment = 0;
            for (int l = 0; l < p.DropletLifetime; l++) {
                int2 nodePos = new int2(pos);
                float2 offset = new float2(pos.x - nodePos.x, pos.y - nodePos.y);
                // direction vector with x/y left/right/forward/back.
                float2 acceleration = MoreMath.lerp(dropletDirection(nodes, pos, nodePos, offset), float2.zero, p.Inertia);
                
                // update direction and position from new calculated direction
                velocitySide.x -= acceleration.x; velocitySide.y -= acceleration.y;
                velocitySide = MoreMath.lerp(velocitySide, float2.zero, p.Friction);

                // normalize delta
                //deltaSide = math.normalize(deltaSide);

                // set deltaHeight to original height
                float deltaHeight = nodes[nodePos.x][nodePos.y].height;

                // move the droplet
                pos.x += velocitySide.x; pos.y += velocitySide.y;
                nodePos = new int2(pos);
                offset = new float2(pos.x - nodePos.x, pos.y - nodePos.y);

                // stop simulating if it flowed onto or over edge of map
                if (pos.x < 1 || pos.x >= mapSize.x - 1 || pos.y < 1 || pos.y >= mapSize.y - 1) {
                    water = 0;
                    break;
                }

                // finish deltaHeight
                deltaHeight = nodes[nodePos.x][nodePos.y].height - deltaHeight;

                // calculate the speed.
                float speed = (float) Math.sqrt(MoreMath.distance(float3.zero, new float3(velocitySide, deltaHeight)));

                // capacity is max(speed * water * capFac, minCap).
                float capacity = Math.max(speed * water * p.SedimentCapacityFactor, p.MinimumSedimentCapacity);

                if (sediment > capacity || speed > 0) {
                    float deposit = (speed > 0) ? Math.min(speed, sediment) : (sediment - capacity) * p.DepositSpeed;
                    sediment -= deposit;

                    // Deposit, weighted by distance to the node.
                    float d00 = deposit * (1 - offset.x) * (1 - offset.y); // origin, decreases with offset.
                    float d01 = deposit * offset.x * (1 - offset.y);
                    float d10 = deposit * (1 - offset.x) * offset.y;
                    float d11 = deposit * offset.x * offset.y; // opposite, increases with offset.

                    HeightmapNode node00 = nodes[nodePos.x][nodePos.y];
                    HeightmapNode node01 = nodes[nodePos.x][nodePos.y + 1];
                    HeightmapNode node10 = nodes[nodePos.x + 1][nodePos.y];
                    HeightmapNode node11 = nodes[nodePos.x + 1][nodePos.y + 1];

                    node00.height += d00;
                    node01.height += d01;
                    node10.height += d10;
                    node11.height += d11;

                    nodes[nodePos.x][nodePos.y] = node00;
                    nodes[nodePos.x][nodePos.y + 1] = node01;
                    nodes[nodePos.x + 1][nodePos.y] = node10;
                    nodes[nodePos.x + 1][nodePos.y + 1] = node11;

                } else {
                    float erode = Math.min((capacity - sediment) * p.ErodeSpeed, -speed);
                    // Use erosion brush to erode from all nodes inside the droplet's erosion radius
                    for (int x = -p.ErosionRadius; x < p.ErosionRadius; x++) {
                        for (int y = -p.ErosionRadius; y < p.ErosionRadius; y++) {
                        	float2 brushOffset = new float2(x, y);
                        	float2 brushPos = new float2(nodePos.x + brushOffset.x, nodePos.y + brushOffset.y);

                            // continue if out of brush bounds
                            float brushDist = MoreMath.distance(brushOffset, pos);
                            if (brushDist > p.ErosionRadius) continue;

                            // continue if out of map bounds
                            if (!(brushPos.x > 0 && brushPos.x < mapSize.x && brushPos.y > 0 && brushPos.y < mapSize.y)) continue;

                            // Erode, weigted by the formula in 'erode' and the brush weight formula below.
                            float erosion = erode * (p.ErosionRadius - brushDist) / p.ErosionRadius / 2;
                            nodes[(int)brushPos.x][(int)brushPos.y].height += erosion;
                            sediment += erosion;
                        }
                    }
                }

                water *= (1 - p.EvaporateSpeed);
            }

            int2 posI = new int2(pos);
            if (posI.x >= 0 && posI.x < mapSize.x && posI.y >= 0 && posI.y < mapSize.y) {
                //Debug.Log(posI + "/" + water);
                nodes[posI.x][posI.y].moisture += water + 1;
            }
        }

        	return nodes;
    }

    private float2 dropletDirection(HeightmapNode[][] nodes, float2 pos, int2 iPos, float2 oPos) {
        float h00 = nodes[iPos.x][ iPos.y].height;
        float h01 = nodes[iPos.x][iPos.y + 1].height;
        float h10 = nodes[iPos.x + 1][iPos.y].height;
        float h11 = nodes[iPos.x + 1][iPos.y + 1].height;

        // return 3D vector pointing in direction of the lowest surrounding point.

        return new float2(
            MoreMath.lerp(h10 - h00, h11 - h01, 1 - oPos.x),
            MoreMath.lerp(h01 - h00, h11 - h10, 1 - oPos.y));
    }
}