package davidqchuang.TerrainGenerator;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MapGenerator {
   public int seed;

   public int erosionRadius = 3;
   public int tectonicPlates = 20;

   public float waterRatio = 0.2f;
   public float minorTectonicRate = 0.7f;

   public float inertia = .05f;

   public float sedimentCapacityFactor = 4;
   public float minSedimentCapacity = .01f;

   public float erodeSpeed = .3f;
   public float depositSpeed = .3f;
   public float evaporateSpeed = .01f;
   public float gravity = 4;

   // max map size, max partition size.
   public int sM, pM;
   public int xPC, yPC;

   public static class Partition {
      public int[][] heightMap;
      public int[][][] biomeMap;
      public int[][] numericBiomeMap;
      public int[][] offsetMap;

      public int tectonicPlate;
      public int height;

      public Partition() {
         heightMap = null;
         biomeMap = null;
         numericBiomeMap = null;
         offsetMap = null;

         tectonicPlate = -1;
         height = 0;
      }

      public Partition(int sM, int[][] hm, int[][][] bm, int[][] nbm) {
         heightMap = hm;
         biomeMap = bm;
         numericBiomeMap = nbm;
         offsetMap = new int[sM][sM];

         tectonicPlate = -1;
         height = 0;
      }
   }

   public static class TectonicPlate {
      public Vector3 direction; // x - drift, y - desired elevation, z - drift
      public int x, y;
      public boolean minor;
      public List<Int2> coordinates;
      public Color color;

      public TectonicPlate(float x, float f, float z) {
         direction = new Vector3(x, f, z);
         coordinates = new ArrayList<>();
         color = new Color((float) Math.random(), (float) Math.random(), (float) Math.random());
      }
   }

   public void generatePartitionLerp(int x, int y) {

   }

   public Partition[][] partitions;
   public TectonicPlate[] tectonics;

   public int[][] heightMap;
   public int[][][] biomeMap;
   public int[][] numericBiomeMap;

   List<Perlin> p;
   List<Octave> o;
   List<Combined> c;

   public String output;
   public String[] phases;
   public int progressPercent;

   public MapGenerator(int s, int sM, int pM) {
      this.seed = s;
      this.sM = sM;
      this.pM = pM;

      biomeMap = new int[sM][sM][2];
      heightMap = new int[sM][sM];
      numericBiomeMap = new int[sM][sM];
      partitions = new Partition[pM][pM];
      tectonics = new TectonicPlate[tectonicPlates];

      p = new ArrayList<>();
      o = new ArrayList<>();
      c = new ArrayList<>();

      output = "";
      phases = new String[3];
      progressPercent = 0;
   }

   public static class Vector3 {
      float x, y, z;

      public Vector3(float x, float y, float z) {
         this.x = x;
         this.y = y;
         this.z = z;
      }
   }

   public static class Vector2 {
      float x, y;

      public Vector2(float x, float y) {
         this.x = x;
         this.y = y;
      }

      public float magnitude() {
         return (float) Math.sqrt(x * x + y * y);
      }
   }

   public static class Int2 {
      int x, y;

      public Int2(int x, int y) {
         this.x = x;
         this.y = y;
      }

      public float magnitude() {
         return (float) Math.sqrt(x * x + y * y);
      }
   }

   public void storePartition(int x, int y) {
      partitions[x][y].heightMap = heightMap;
      partitions[x][y].biomeMap = biomeMap;
      partitions[x][y].numericBiomeMap = numericBiomeMap;
   }

   public void loadPartition(int x, int y) {
      partitions[x][y].heightMap = heightMap;
      partitions[x][y].biomeMap = biomeMap;
      partitions[x][y].numericBiomeMap = numericBiomeMap;
   }

   public void Output(String o) {
      Output(o, 0);
   }

   public void Output(String o, int indent) {
      progressPercent = 0; // reset progress upon changing phase
      phases[indent] = o;
      String s = "";
      for (int i = 0; i < indent; i++) {
         s += "  ";
      }
      s += o;
      output += s + "\n";
      // Sets anything with a lower indent to ""
      for (int i = indent + 1; i < 3; i++) {
         phases[i] = "";
      }
      System.out.println(o);
   }

   public String Output(int p, int m) {
      m = Math.max(m, 1);
      progressPercent = (int) ((float) p / m * 100);
      return progressPercent + "%";
   }

   public void erode(int iterations, int lifetime, float initialSpeed, float initialWater) {
      Output("Eroding");
      Output(0, 1);
      float[][] m = new float[sM][sM];
      float[][] waterV = new float[sM][sM];
      Output("Copying heightmap", 1);
      for (int x = 0; x < sM; x++) {
         for (int z = 0; z < sM; z++) {
            m[x][z] = heightMap[x][z];
         }
      }
      Output("Simulating raindrops", 1);
      for (int i = 0; i < iterations; i++) {
         float pX = (float) (Math.random() * sM - 1);
         float pZ = (float) (Math.random() * sM - 1);
         float dX = 0;
         float dZ = 0;
         float speed = initialSpeed;
         float water = initialWater;
         float sediment = 0;
         Output(i, iterations);
         if (i + 1 % (iterations / 4) == 0) {
            Output(Output(i, iterations), 2);
         }
         for (int l = 0; l < lifetime; l++) {
            int nX = (int) pX; // int value of droplet position
            int nZ = (int) pZ; // for use in heightmap array index
            float oX = pX - nX; // offset inside cell
            float oZ = pZ - nZ;

            Vector3 dV = dropletDirection(m, pX, pZ);

            // update direction and position from new calculated direction
            dX = (dX * inertia - dV.x * (1 - inertia));
            dZ = (dZ * inertia - dV.z * (1 - inertia));
            float magnitude = (float) Math.sqrt(dX * dX + dZ * dZ);
            if (magnitude != 0) {
               dX /= magnitude;
               dZ /= magnitude;
            }

            pX += dX;
            pZ += dZ;

            // stop simulating if it flowed over edge of map
            if ((dX == 0 && dZ == 0) || pX < 0 || pX >= sM - 1 || pZ < 0 || pZ >= sM - 1) {
               break;
            }

            float dH = dropletDirection(m, pX, pZ).y - dV.y;
            float capacity = Math.max(dH * speed * water * sedimentCapacityFactor, minSedimentCapacity);

            if (sediment > capacity || dH > 0) {
               float deposit = (dH > 0) ? Math.min(dH, sediment) : (sediment - capacity) * depositSpeed;
               sediment -= deposit;
               m[nX][nZ] += deposit * (1 - oX) * (1 - oZ);
               m[nX][nZ + 1] += deposit * oX * (1 - oZ);
               m[nX + 1][nZ] += deposit * (1 - oX) * oZ;
               m[nX + 1][nZ + 1] += deposit * oX * oZ;
            } else {
               float erode = Math.min((capacity - sediment) * erodeSpeed, -dH);
               final int radius = 3;
               float[][] brush = brush(radius);
               // Use erosion brush to erode from all nodes inside the droplet's erosion radius
               for (int x = 0; x < radius * 2 - 1; x++) {
                  for (int y = 0; y < radius * 2 - 1; y++) {
                     int fX = (int) (x - radius + 1) + nX;
                     int fY = (int) (y - radius + 1) + nZ;
                     if (fX > 0 && fX < sM - 1 && fY > 0 && fY < sM - 1) {
                        float wErode = erode * brush[x][y];
                        float dSediment = ((m[fX][fY] < wErode) ? m[fX][fY] : wErode);

                        m[fX][fY] -= dSediment;
                        sediment += dSediment;
                     }
                  }
               }
            }
            speed = speed * speed + dH * gravity;
            if (speed < 0) {
               break;
            }
            speed = (float) Math.sqrt(speed);
            water *= (1 - evaporateSpeed);
         }
         int aX = (int) Math.min(Math.max(pX, 0), sM - 1);
         int aZ = (int) Math.min(Math.max(pZ, 0), sM - 1);
         waterV[aX][aZ] += water;
         waterV[aX][aZ] += water;
      }
      for (int x = 0; x < sM; x++) {
         for (int z = 0; z < sM; z++) {
            heightMap[x][z] = (int) m[x][z];
         }
      }
   }

   public float[][] brush(int radius) {
      float[][] brush = new float[radius * 2 - 1][radius * 2 - 1];
      for (int x = 0; x < radius * 2 - 1; x++) {
         for (int y = 0; y < radius * 2 - 1; y++) {
            int fX = (int) (x - radius + 1);
            int fY = (int) (y - radius + 1);
            brush[x][y] = Math.max(radius - (float) Math.sqrt(fX * fX + fY * fY), 0) / radius;
         }
      }
      return brush;
   }

   private Vector3 dropletDirection(float[][] map, float x, float z) {
      int cX = (int) x;
      int cZ = (int) z;

      float oX = x - cX;
      float oZ = z - cZ;

      float hNW = map[cZ][cX];
      float hNE = map[cZ][cX + 1];
      float hSW = map[cZ + 1][cX];
      float hSE = map[cZ + 1][cX + 1];
      
      // return 3D vector pointing in direction of the lowest surrounding point.
      
      return new Vector3(
            (float) lerp(hSW - hNW, hSE - hNE, oX), 
            (hNW + hNE + hSW + hSE) / 4,
            (float) lerp(hNE - hNW, hSE - hSW, oZ));   
   }

   public boolean[][] indexMap(int i) {
      boolean[][] map = new boolean[sM][sM];
      for (int x = 0; x < sM; x++) {
         for (int z = 0; z < sM; z++) {
            if (heightMap[x][z] == i)
               map[x][z] = true;
            else
               map[x][z] = false;
         }
      }
      return map;
   }

   public void algorithmicMapPass(HeightMapAlgorithm h, double weight) {
      h.compute(sM, weight);
      Output("Performing additive pass of heightmap at a weight of " + weight);
   }

   public void algorithmicMapPassByIndex(HeightMapAlgorithm h, boolean[][] index, double weight) {
      h.computeIndex(index, sM, weight);
      Output("Performing " + h + " algorithmic pass of heightmap at a weight of " + weight);
   }

   public void heightMapPass(int[][] map1, double weight) {
      for (int x = 0; x < sM; x++) {
         for (int z = 0; z < sM; z++) {
            heightMap[x][z] = noiseLerp(map1[x][z], heightMap[x][z], weight);
         }
      }
      Output("Performing additive pass of heightmap at a weight of " + weight);
   }

   public void heightMapPass(NoiseGenerator noise, double weight) {
      for (int x = 0; x < sM; x++) {
         for (int z = 0; z < sM; z++) {
            heightMap[x][z] = noiseLerp(noise, x, z, heightMap[x][z], weight);
         }
      }
      Output("Performing additive " + noise + " noise pass of heightmap at a weight of " + weight);
   }

   public void heightMapPassByIndex(int[][] map1, boolean[][] indesMap, double weight) {
      for (int x = 0; x < sM; x++) {
         for (int z = 0; z < sM; z++) {
            if (indesMap[x][z]) {
               heightMap[x][z] = noiseLerp(map1[x][z], heightMap[x][z], weight);
            }
         }
      }
      Output("Performing lerp pass by index of heightmap at a weight of " + weight);
   }

   public void heightMapPassByIndex(NoiseGenerator noise, boolean[][] indesMap, double weight) {
      for (int x = 0; x < sM; x++) {
         for (int z = 0; z < sM; z++) {
            if (indesMap[x][z]) {
               heightMap[x][z] = noiseLerp(noise, x, z, heightMap[x][z], weight);
            }
         }
      }
      Output("Performing lerp " + noise + " noise pass of heightmap at a weight of " + weight);
   }

   public void heightMapPassByIndex(int[][] map1, float[][] indesMap, float index, double weight) {
      for (int x = 0; x < sM; x++) {
         for (int z = 0; z < sM; z++) {
            if (indesMap[x][z] == index) {
               heightMap[x][z] = noiseLerp(map1[x][z], heightMap[x][z], weight);
            }
         }
      }
      Output("Performing lerp pass by index of heightmap at a weight of " + weight);
   }

   public void heightMapPassByIndex(NoiseGenerator noise, float[][] indesMap, float index, double weight) {
      for (int x = 0; x < sM; x++) {
         for (int z = 0; z < sM; z++) {
            if (indesMap[x][z] == index) {
               heightMap[x][z] = noiseLerp(noise, x, z, heightMap[x][z], weight);
            }
         }
      }
      Output("Performing lerp " + noise + " noise pass of heightmap at a weight of " + weight);
   }

   public void additiveMapPass(NoiseGenerator noise) {
      for (int x = 0; x < sM; x++) {
         for (int z = 0; z < sM; z++) {
            heightMap[x][z] += noise.compute(x, z);
         }
      }
      Output("Performing additive " + noise + " noise pass of heightmap");
   }

   public void additiveMapPass(int[][] noise) {
      for (int x = 0; x < sM; x++) {
         for (int z = 0; z < sM; z++) {
            heightMap[x][z] += noise[x][z];
         }
      }
      Output("Performing additive pass of heightmap");
   }

   public void additiveMapPassByIndex(NoiseGenerator noise, boolean[][] indesMap) {
      for (int x = 0; x < sM; x++) {
         for (int z = 0; z < sM; z++) {
            if (indesMap[x][z]) {
               heightMap[x][z] += noise.compute(x, z);
            }
         }
      }
      Output("Performing " + noise + " noise pass by index of heightmap");
   }

   public void additiveMapPassByIndex(NoiseGenerator noise, float[][] indesMap, float index) {
      for (int x = 0; x < sM; x++) {
         for (int z = 0; z < sM; z++) {
            if (indesMap[x][z] == index) {
               heightMap[x][z] += noise.compute(x, z);
            }
         }
      }
      Output("Performing " + noise + " noise pass by index of heightmap");
   }

   public void additiveMapPassByIndex(int[][] map, float[][] indesMap, float index) {
      for (int x = 0; x < sM; x++) {
         for (int z = 0; z < sM; z++) {
            if (indesMap[x][z] == index) {
               heightMap[x][z] += map[x][z];
            }
         }
      }
      Output("Performing additive pass by index of heightmap");
   }

   public void river(int[][] heightMap) {
      int[] heights = new int[heightMap.length * 2 + heightMap[0].length * 2];
      int i = 0;
      for (int x = 0; x < heightMap.length; x++) {
         // x = 0
         if (x == 0) {
            for (int y = 0; y < heightMap[0].length; y++) {
               heights[i] = heightMap[x][y];
               i++;
            }
         } else if (x == heightMap.length - 1) {
            for (int y = 0; y < heightMap[0].length; y++) {
               heights[i] = heightMap[x][y];
               i++;
            }
         }

         // y = 0
         heights[i] = heightMap[x][0];
         i++;
         // y = heightMap[0].length-1
         heights[i] = heightMap[x][heightMap[0].length - 1];
         i++;
      }
      quicksort(heights, 0, heights.length - 1);
   }

   public void quicksort(int[] A, int lo, int hi) {
      if (lo < hi) {
         int p = partition(A, lo, hi);
         quicksort(A, lo, p - 1);
         quicksort(A, p + 1, hi);
      }
   }

   public int partition(int[] A, int lo, int hi) {
      int pivot = A[hi];
      int i = lo;
      for (int j = lo; j <= hi; j++) {
         if (A[j] < pivot) {
            int temp = A[i];
            A[i] = A[j];
            A[j] = temp;
            i++;
         }
      }
      int temp = A[i];
      A[i] = A[hi];
      A[hi] = temp;
      return i;
   }

   public int[][][] calculateBiomeMap(NoiseGenerator noise) {
      Output("Generating biome map");
      List<Point> waterCoords = new ArrayList<>();

      double[][][] biomes = new double[sM][sM][2];
      for (int x = 0; x < sM; x++) {
         for (int z = 0; z < sM; z++) {
            if (heightMap[x][z] <= 0) {
               waterCoords.add(new Point(x, z));
               biomes[x][z] = new double[] { 7, 1 };
            }
         }
      }
      
      Output("Setting biome moistures", 1);
      
      for (int x = 0; x < sM; x++) {
         for (int y = 0; y < sM; y++) {
            if (biomes[x][y][0] == 7)
               continue;
            for (Point p : waterCoords) {
               double dist = Math.sqrt((x - p.x) * (x - p.x) + (y - p.y) * (y - p.y));
               biomes[x][y][0] += dist / Math.pow(dist, 2) * 0.4;
            }
            biomes[x][y][0] = Math.min(Math.max(biomes[x][y][0], 1), 6);
            biomes[x][y][1] = biomeElevationStrata(heightMap[x][y]);
         }
         if ((x + 1) % (sM / 4) == 0) {
            Output((x + 1) + "/" + sM + " coordinates filled", 2);
         }
      }

      for (int x = 0; x < sM; x++) {
         for (int z = 0; z < sM; z++) {
            this.biomeMap[x][z][0] = (int) biomes[x][z][0];
            this.biomeMap[x][z][1] = (int) biomes[x][z][1];
         }
      }
      generateNumericBiomeMap(biomeMap);
      return biomeMap;
   }

   public float[][] generateNumericBiomeMap(int[][][] biomeMap) {
      float[][] biomes = new float[sM][sM];
      for (int x = 0; x < sM; x++) {
         for (int z = 0; z < sM; z++) {
            biomes[x][z] = Biomes.biomeID(biomeMap[x][z]);
         }
      }
      return biomes;
   }

// for sorting another list at the same time as another using only one list's values
   public static void dualSort(double[] A, int[][] A2, int lo, int hi) {
      while (lo < hi) {
         int p = dualPartition(A, A2, lo, hi);
         if ((p - 1) - lo <= hi - (p + 1)) {
            dualSort(A, A2, lo, p - 1);

            lo = p + 1;
         } else {
            dualSort(A, A2, p + 1, hi);
            // Prepare for tail recursion
            hi = p - 1;
         }
      }
   }

   public static int dualPartition(double[] A, int[][] A2, int lo, int hi) {
      double pivot = A[hi];
      int i = lo;
      for (int j = lo; j <= hi; j++) {
         if (A[j] < pivot) {
            double temp = A[i];
            A[i] = A[j];
            A[j] = temp;
            int[] temp2 = A2[i];
            A2[i] = A2[j];
            A2[j] = temp2;
            i++;
         }
      }
      double temp = A[i];
      A[i] = A[hi];
      A[hi] = temp;

      int[] temp2 = A2[i];
      A2[i] = A2[hi];
      A2[hi] = temp2;
      return i;
   }

   public static boolean existsIn(int[][] array, int[] obj) {
      for (int[] i : array) {
         if (obj[0] == i[0] && obj[1] == i[1])
            return true;
      }
      return false;
   }

   public static boolean existsIn(int[][] array, int a, int b) {
      for (int[] i : array) {
         if (a == i[0] && b == i[1])
            return true;
      }
      return false;
   }

   public int biomeElevationStrata(int h) {
      if (h < 7) {
         return 1;
      } else if (h < 14) {
         return 2;
      } else if (h < 20) {
         return 3;
      } else {
         return 4;
      }
   }
   
   public void biomePass(int biome) {
      boolean[][] index = indexMap(biome);

      switch (biome) {
      case Biomes.BA: // barren, and scorched are all flat and have dead trees
      case Biomes.SC:
         // dead trees

         break;
      case Biomes.TU: // tundra has living trees
      case Biomes.SN: // snow is like tundra but not flat
         // living trees
         if (biome == Biomes.SN)
            break; // don't flatten if snowy
         Smooth smooth = new Smooth(heightMap);
         // flatten
         algorithmicMapPassByIndex(smooth, index, 0.5);
         break;
      case Biomes.TA:
         break;
      case Biomes.SH:
         break;
      case Biomes.TD:
         break;
      case Biomes.RF:
         break;
      case Biomes.DF:
         break;
      case Biomes.GR:
         break;
      case Biomes.TR:
         break;
      case Biomes.TS:
         break;
      case Biomes.SD:
         break;
      }
   }

   public void calculateTectonicMap() {
      Output("Generating tectonics");
      Random rand = new Random(seed);
      Output("Creating plates", 1);
      for (int i = 0; i < tectonicPlates; i++) {
         boolean water = rand.nextFloat() > waterRatio;
         float magFactor = rand.nextFloat() * 1.6f + 0.4f;
         tectonics[i] = new TectonicPlate((float) Math.cos(rand.nextDouble() * Math.PI * 2) * magFactor,
               (rand.nextFloat() * 6 * (water ? -2 : 1)),
               (float) Math.sin(rand.nextDouble() * Math.PI * 2) * magFactor);
         tectonics[i].x = rand.nextInt(pM);
         tectonics[i].y = rand.nextInt(pM - pM / 5) + pM / 10;
         if (i == 0) {
            tectonics[i].x = pM / 2;
            tectonics[i].y = 0;
            tectonics[i].direction.y = Math.abs(tectonics[i].direction.y); // poles are never water
         } else if (i == 1) {
            tectonics[i].x = pM / 2;
            tectonics[i].y = pM - 1;
            tectonics[i].direction.y = Math.abs(tectonics[i].direction.y); // poles are never water
         }

         tectonics[i].minor = rand.nextFloat() > minorTectonicRate;
//            Output(Float.toString(tectonics[i].direction.y));
      }
      Output("Calculating voronoi", 1);
      for (int x = 0; x < pM; x++) {
         // bottom hemisphere pretends to be top in latitude because dist formula doesn't
         // work for <45deg latitude
         for (int y = 0; y < pM / 2; y++) {
            int closest = -1;
            float dist = pM * pM;
            for (int i = 0; i < tectonicPlates; i++) {
               float angle = (float) greatCircleDistance(tectonics[i].x, x, tectonics[i].y, y, pM, pM);
               if (angle < dist) {
                  closest = i;
                  dist = angle;
               }
            }
            if (closest != -1) {
               if (partitions[x][y] == null)
                  partitions[x][y] = new Partition();
               partitions[x][y].tectonicPlate = closest;
               tectonics[closest].coordinates.add(new Int2(x, y));
            }
         }
         // calculate top hemisphere distances normally
         for (int y = pM / 2; y < pM; y++) {
            int closest = -1;
            float dist = pM * pM;
            for (int i = 0; i < tectonicPlates; i++) {
               float angle = (float) greatCircleDistance(tectonics[i].x, x, tectonics[i].y, y, pM, pM);
               if (angle < dist) {
                  closest = i;
                  dist = angle;
               }
            }
            if (closest != -1) {
               if (partitions[x][y] == null)
                  partitions[x][y] = new Partition();
               partitions[x][y].tectonicPlate = closest;
               tectonics[closest].coordinates.add(new Int2(x, y));
            }
         }
      }
      List<Vector3> hotspots = new ArrayList<>();
      List<Vector3> boundaries = new ArrayList<>();
      for (int i = 0; i < tectonicPlates; i++) {
         TectonicPlate t = tectonics[i];

         for (Int2 v : t.coordinates) {
            boolean hotspot = false;
            Vector3 t1 = t.direction;
            // cycle through -1 and 1
            int touchingTiles = 0;
            for (int x = -1; x <= 1; x++) {
               for (int y = -1; y <= 1; y++) {
                  if ((x == 0 && y == 0) || v.x + x < 0 || v.x + x >= pM || v.y + y < 0 || v.y + y >= pM || v.x >= pM
                        || v.y >= pM) {
                     continue;
                  }
                  int i2 = partitions[v.x + x][v.y + y].tectonicPlate;
                  Vector3 t2 = tectonics[i2].direction;
                  if (partitions[v.x + x][v.y + y].tectonicPlate != i) {
                     if (touchingTiles == 0) {
                        boundaries.add(new Vector3(v.x, v.y, partitions[v.x][v.y].height));
                     }
                     touchingTiles++;
                  }
                  //
                  if (touchingTiles > 3) {
                     // calculate magnitude of sum of vectors to determine if they are colliding
                     float magnitudeOfCollision = magnitude(t1.x + t2.x, t1.z + t2.z);

                     // if magnitude of collision is less than both vectors' magnitudes then they are
                     // colliding
                     if (magnitudeOfCollision < magnitude(t1.x, t1.z) && magnitudeOfCollision < magnitude(t2.x, t2.z)) {
                        // mountain
                        // TODO: to scale
//                                partitions[v.x][v.y].height += lerp(Math.abs(t2.y), t2.y, 0.4) + lerp(Math.abs(t1.y),t1.y,0.4);
                        hotspots.add(new Vector3(v.x, v.y, partitions[v.x][v.y].height));
                     }
                     if (magnitudeOfCollision > magnitude(t1.x, t1.z) * 1.5f
                           || magnitudeOfCollision > magnitude(t2.x, t2.z) * 1.5f) {
                        // ocean crevice
                        // TODO: to scale
                     }
                  }
               } // for -1 < y < 1
            } // for -1 < x < 1
         } // for int2 v in t.coordinates
      }
      Octave oct = new Octave(new ContinuousPerlin(seed, 128, 128), 3);

      Output("Calculating global terrain heightmap");
      for (int x = 0; x < pM; x++) {
         for (int y = 0; y < pM; y++) {
            if ((x * pM + y) % (pM * pM / 8) == 0) {
               Output(x * pM + y + "/" + pM * pM + " coordinates filled", 1);
            }
//                float h=0;
//                double distRoot = greatCircleDistance(x, tectonics[partitions[x][y].tectonicPlate].x, y, tectonics[partitions[x][y].tectonicPlate].y, pM, pM);
//                double distBoundary = pM*pM;
//                float boundaryHeight = 0;
//                float plateHeight = tectonics[partitions[x][y].tectonicPlate].direction.y;
//                
//                for (int i = 0; i < boundaries.size(); i++) {
//                    Vector3 p = boundaries.get(i);
//                    double dist = greatCircleDistance(x,p.x,y,p.y,pM,pM);
//                    if(dist < distBoundary) {
//                        distBoundary = dist;
//                        boundaryHeight = p.z;
//                    }
//                }
//
//                double t = distBoundary / (distBoundary + distRoot);
//                for (Vector3 p : hotspots) {
//                    double dist = greatCircleDistance(x,p.x,y,p.y,pM,pM);
//                    h += (0.5 / (dist+0.2f)) * (p.z / 20);
//                }
//                if (t < 0.5) {
//                    t = t / 0.5;
//                    partitions[x][y].height = (int) (plateHeight + Math.pow(t - 1, 2) * (boundaryHeight - plateHeight));
//                } else {
//                    partitions[x][y].height = (int) plateHeight;
//                }
//                partitions[x][y].height = (int) (h - 15)*2;
            partitions[x][y].height = (int) (oct.compute(x, y, (int) (pM)) / 2);
         }
      }
      int[][] h = new int[pM][pM];
      for (int x = 0; x < pM; x++) {
         for (int z = 0; z < pM; z++) {
            h[x][z] = partitions[x][z].height;
         }
      }
//        Smooth smooth = new Smooth(h);
//        smooth.compute(pM, 0.4);
//        Flatten flatten = new Flatten(h);
//        flatten.compute(pM, 2);
      for (int x = 0; x < pM; x++) {
         for (int z = 0; z < pM; z++) {
            partitions[x][z].height = h[x][z];
         }
      }

   }

   public static double greatCircleDistance(float x1, float x2, float y1, float y2, float xM, float yM) {
      if (x1 == x2 && y1 == y2) {
         return 0;
      }
      float long1 = (float) ((2 * Math.PI / xM) * x1 - Math.PI);
      float long2 = (float) ((2 * Math.PI / xM) * x2 - Math.PI);
      float lat1 = (float) ((Math.PI / 2 / xM) * y1);
      float lat2 = (float) ((Math.PI / 2 / xM) * y2);
      if (y2 < yM / 2) {
         lat1 = (float) (Math.PI / 2 - (Math.PI / 2 / yM) * y1);
         lat2 = (float) (Math.PI / 2 - (Math.PI / 2 / yM) * y2);
      }
      float dx = Math.abs(long1 - long2);
      return Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(dx));
   }

   public static float magnitude(float x, float y) {
      return (float) Math.sqrt(x * x + y * y);
   }

   /*
    * heightMap, a 2D array of size width X depth waterLevel, a constant of 32
    * noise1, a combined noise function with two octave noise functions (of octave
    * 8) as input. noise2, a combined noise function with two octave noise
    * functions (of octave 8) as input. noise3, an octave noise function (of octave
    * 6)
    */
   public int[][] calculateHeightMap(int xO, int zO) {
      Output("Creating base heightmap");
      Combined noise1 = new Combined(new Octave(new Perlin(seed), 8), new Octave(new Perlin(seed + 1), 8));
      c.add(noise1);
      Combined noise2 = new Combined(new Octave(new Perlin(seed + 2), 8), new Octave(new Perlin(seed + 3), 8));
      c.add(noise2);
      Octave noise3 = new Octave(new Perlin(seed + 4), 6);
      o.add(noise3);

      // heightMap = new int[xS][zS];
      for (int x = 0; x < sM; x++) {
         for (int z = 0; z < sM; z++) {
            double heightResult;

            double heightLow = noise1.compute(x * 1.3, z * 1.3) / 6 - 4;
            double heightHigh = noise2.compute(x * 1.3, z * 1.3) / 5 + 6;

            if (noise3.compute(x, z) / 8 > 0) {
               heightResult = heightLow;
            } else {
               heightResult = Math.max(heightLow, heightHigh);

            }
            heightResult /= 2;

            if (heightResult < 0) {
               heightResult *= 0.8;
            }

            heightMap[x][z] = Math.abs((int) (heightResult));
         }

      }
      return heightMap;
   }

   // TODO:
//    public static double lerp(float v0, float v1, float t) {
//      return (1 - t) * v0 + t * v1;
//    }
   public static double terp(double t) {
      return t * t * t * (t * (t * 6 - 15) + 10);
   }

   // fast lerp, imprecise
   public static double lerp(double a0, double a1, double w) {
      return a0 + w * (a1 - a0);
   }

   public static int noiseLerpBetween(NoiseGenerator noise, int x, int z, int height, int h, int n, double weight) {
      weight = Math.min(Math.max(weight, 0), 1);
      return (int) lerp(height * h, noise.compute(x, z) * n, weight);
//        return (int) (height * h * (1.0 - weight) + noise.compute(x, z) * n * weight);
   }

   public static int noiseLerp(NoiseGenerator noise, int x, int z, int height, double weight) {
      weight = Math.min(Math.max(weight, 0), 1);
      return (int) (height * (1.0 - weight) + noise.compute(x, z) * weight);
   }

   public static int noiseLerp(int h0, int h1, double weight) {
      weight = Math.max(weight, 0);
      weight = Math.min(weight, 1);
      return (int) (h1 * (1.0 - weight) + h0 * weight);
   }

   public static abstract class HeightMapAlgorithm {
      int[][] map;

      public HeightMapAlgorithm(int[][] m) {
         map = m;
      }

      public void setMap(int[][] m) {
         map = m;
      }

      public abstract String action();

      public abstract void compute(int mS, double k);

      public abstract void computeIndex(boolean[][] index, int mS, double k);

      @Override
      public String toString() {
         return action();
      }
   }

   public static class Flatten extends HeightMapAlgorithm {
      public Flatten(int[][] m) {
         super(m);
      }

      public String action() {
         return "Smoothing";
      }

      public void compute(int mS, double k) {
         for (int x = 0; x < mS; x++) {
            for (int z = 0; z < mS; z++) {
               map[x][z] = (int) lerp(map[x][z], k, 0.5f);
            }
         }
      }

      public void computeIndex(boolean[][] index, int mS, double k) {
         /* Rows, left to right */
      }

   }

   public static class Smooth extends HeightMapAlgorithm {
      public Smooth(int[][] m) {
         super(m);
      }

      public String action() {
         return "Smoothing";
      }

      public void compute(int mS, double k) {
         /* Rows, left to right */
         for (int x = 1; x < mS; x++) {
            for (int z = 0; z < mS; z++) {
               map[x][z] = (int) (map[x - 1][z] * (1 - k) + map[x][z] * k);
            }
         }

         /* Rows right to left */
         for (int x = mS - 2; x < -1; x--) {
            for (int z = 0; z < mS; z++) {
               map[x][z] = (int) (map[x + 1][z] * (1 - k) + map[x][z] * k);
            }
         }

         /* Columns, bottom to top */
         for (int x = 0; x < mS; x++) {
            for (int z = 1; z < mS; z++) {
               map[x][z] = (int) (map[x][z - 1] * (1 - k) + map[x][z] * k);
            }
         }

         /* Columns, top to bottom */
         for (int x = 0; x < mS; x++) {
            for (int z = mS; z < -1; z--) {
               map[x][z] = (int) (map[x][z + 1] * (1 - k) + map[x][z] * k);
            }
         }
      }

      public void computeIndex(boolean[][] index, int mS, double k) {
         /* Rows, left to right */
         for (int x = 1; x < mS; x++)
            for (int z = 0; z < mS; z++) {
               if (index[x][z]) {
                  map[x][z] = (int) (map[x - 1][z] * (1 - k) + map[x][z] * k);
               }
            }

         /* Rows right to left */
         for (int x = mS - 2; x < -1; x--)
            for (int z = 0; z < mS; z++) {
               if (index[x][z]) {
                  map[x][z] = (int) (map[x + 1][z] * (1 - k) + map[x][z] * k);
               }
            }

         /* Columns, bottom to top */
         for (int x = 0; x < mS; x++)
            for (int z = 1; z < mS; z++) {
               if (index[x][z]) {
                  map[x][z] = (int) (map[x][z - 1] * (1 - k) + map[x][z] * k);
               }
            }

         /* Columns, top to bottom */
         for (int x = 0; x < mS; x++)
            for (int z = mS; z < -1; z--) {
               if (index[x][z]) {
                  map[x][z] = (int) (map[x][z + 1] * (1 - k) + map[x][z] * k);
               }
            }
      }

   }

   public static abstract class NoiseGenerator {
      public abstract String action();

      public abstract double compute(double x, double z);

      @Override
      public String toString() {
         return action();
      }
   }

   public static class Perlin extends NoiseGenerator {
      public String action() {
         return "Perlin";
      }

      HashMap<Integer, Map<Integer, double[]>> map = new HashMap<Integer, Map<Integer, double[]>>();
      int seed;
      Random rand;

      /*
       * Function to linearly interpolate between a0 and a1 Weight w should be in the
       * range [0.0, 1.0]
       *
       * as an alternative, this slightly faster equivalent function (macro) can be
       * used: #define lerp(a0, a1, w) ((a0) + (w)*((a1) - (a0)))
       */
      public Perlin(int seed) {
         this.seed = seed;
         this.rand = new Random(seed);
      }

      double[] generateGradient(int x1, int z1) {
         double radians = (rand.nextDouble() * Math.PI * 2);
         return new double[] { Math.cos(radians), Math.sin(radians) };
      }

      // Computes the dot product of the distance and gradient vectors.
      double dotGridGradient(int ix, int iy, double x, double y) {
         // Compute the distance vector
         // 0 - 0.0
         // 9.96 - 9
         double dx = x - (double) ix;
         double dy = y - (double) iy;
         if (!map.containsKey(ix)) {
            map.put(ix, new HashMap<Integer, double[]>());
         }
         if (!map.get(ix).containsKey(iy)) {
            map.get(ix).put(iy, generateGradient(ix, iy));
         }
         // Compute the dot-product
         return (dx * map.get(ix).get(iy)[0] + dy * map.get(ix).get(iy)[1]);
      }

      // Compute Perlin noise at coordinates x, y
      public double compute(double x, double y) {

         // Determine grid cell coordinates
         int x0 = (int) x;
         int x1 = x0 + 1;
         int y0 = (int) y;
         int y1 = y0 + 1;

         // Determine interpolation weights
         // Could also use higher order polynomial/s-curve here
         double sx = x - x0;
         double sy = y - y0;

         // Interpolate between grid point gradients
         double n0, n1, ix0, ix1, value;

         n0 = dotGridGradient(x0, y0, x, y);
         n1 = dotGridGradient(x1, y0, x, y);
         ix0 = lerp(n0, n1, sx);

         n0 = dotGridGradient(x0, y1, x, y);
         n1 = dotGridGradient(x1, y1, x, y);
         ix1 = lerp(n0, n1, sx);

         value = lerp(ix0, ix1, sy);
         return value;
      }
   }

   public static class ContinuousPerlin extends Perlin {
      public String action() {
         return "Continuous Perlin";
      }

      public static final char[] perm = { 151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140,
            36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252,
            219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165,
            71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55,
            46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200,
            196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202,
            38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170,
            213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108,
            110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191,
            179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176,
            115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66,
            215, 61, 156, 180, 151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30,
            69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117,
            35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139,
            48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40,
            244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196, 135, 130,
            116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118,
            126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170, 213, 119, 248,
            152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113,
            224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241,
            81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50,
            45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156,
            180 };
      int width, height;

      /*
       * Function to linearly interpolate between a0 and a1 Weight w should be in the
       * range [0.0, 1.0]
       *
       * as an alternative, this slightly faster equivalent function (macro) can be
       * used: #define lerp(a0, a1, w) ((a0) + (w)*((a1) - (a0)))
       */
      public ContinuousPerlin(int seed, int w, int h) {
         super(seed);
         this.width = w;
         this.height = h;
      }

      public void setSize(int s) {
         width = s;
         height = s;
      }

      float grad(int hash, float x, float y) {
         int h = hash & 7; // Convert low 3 bits of hash code
         float u = h < 4 ? x : y; // into 8 simple gradient directions,
         float v = h < 4 ? y : x; // and compute the dot product with (x,y).
         return (float) (((h & 1) != 0 ? -u : u) + ((h & 2) != 0 ? -2.0 * v : 2.0 * v));
      }

      float noise(float x, float y) {
         int ix0, iy0, ix1, iy1;
         float fx0, fy0, fx1, fy1;
         float s, t, nx0, nx1, n0, n1;

         ix0 = (int) x; // Integer part of x
         iy0 = (int) y; // Integer part of y
         fx0 = x - ix0; // Fractional part of x
         fy0 = y - iy0; // Fractional part of y
         fx1 = fx0 - 1.0f;
         fy1 = fy0 - 1.0f;
         ix1 = ((ix0 + 1) % width) & 0xff; // Wrap to 0..px-1 and wrap to 0..255
         iy1 = ((iy0 + 1) % height) & 0xff; // Wrap to 0..py-1 and wrap to 0..255
         ix0 = (ix0 % width) & 0xff;
         iy0 = (iy0 % height) & 0xff;

         t = (float) terp(fy0);
         s = (float) terp(fx0);

         nx0 = grad(perm[ix0 + perm[iy0]], fx0, fy0);
         nx1 = grad(perm[ix0 + perm[iy1]], fx0, fy1);
         n0 = (float) lerp(nx0, nx1, t);

         nx0 = grad(perm[ix1 + perm[iy0]], fx1, fy0);
         nx1 = grad(perm[ix1 + perm[iy1]], fx1, fy1);
         n1 = (float) lerp(nx0, nx1, t);

         return (float) (0.507f * (lerp(n0, n1, s)));
      }
   }

   public static class Octave extends NoiseGenerator {
      public String action() {
         return "Octave";
      }

      int octaves;
      Perlin perlin;

      float amplitude = 2.0f;
      float frequency = 0.5f;

      public Octave() {
         this.octaves = 0;
         amplitude = 2;
         frequency = 0.5f;
      }

      public Octave(Perlin p, int o) {
         this.perlin = p;
         this.octaves = o;
         amplitude = 2;
         frequency = 0.5f;
      }

      public double compute(double x, double z) {
         float amplitude = 1, freq = 1;
         float sum = 0;
         int i;

         for (i = 0; i < octaves; i++) {
            sum += perlin.compute(x * freq, z * freq) * amplitude;
            amplitude *= 2.0f;
            freq *= 0.5f;
         }
         return sum;
      }

      public void setAmp(float a) {
         amplitude = a;
      }

      public void setFreq(float a) {
         frequency = a;
      }

      public double compute(double x, double z, int s) {
         float amplitude = 1, freq = 1;
         float sum = 0;
         int i;

         for (i = 0; i < octaves; i++) {
            if (perlin instanceof ContinuousPerlin) {
               ContinuousPerlin cont = (ContinuousPerlin) perlin;
               cont.setSize((int) (s * freq));

               sum += amplitude * cont.noise((float) (x * freq), (float) (z * freq));
            }
            amplitude *= this.amplitude;
            freq *= this.frequency;
         }
         return sum;
      }
   }

   public static class Combined extends NoiseGenerator {
      public String action() {
         return "Combined " + noise1 + " " + noise2 + " ";
      }

      NoiseGenerator noise1;
      NoiseGenerator noise2;

      public Combined() {
      }

      public Combined(NoiseGenerator one, NoiseGenerator two) {
         noise1 = one;
         noise2 = two;
      }

      public double compute(double x, double z) {
         double offset = noise2.compute(x, z);
         return noise1.compute(x + offset, z);
      }
   }

   public static class Flat extends NoiseGenerator {
      public String action() {
         return "Flat " + num;
      }

      double num;

      public Flat() {
         num = 0;
      }

      public Flat(double x) {
         num = x;
      }

      public void setHeight(double h) {
         num = h;
      }

      public double compute(double x, double z) {
         return num;
      }
   }

   public static class Biomes {
      // SN - snow
      public static final int SN = 0;
      // TU - tundra
      public static final int TU = 1;
      // BA - barren
      public static final int BA = 2;
      // SC - scorched
      public static final int SC = 3;
      // TA - taiga
      public static final int TA = 4;
      // SH - shrubland
      public static final int SH = 5;
      // TD - temperate desert
      public static final int TD = 6;
      // RF - temperate rainforest
      public static final int RF = 7;
      // DF - teperate deciduous forest
      public static final int DF = 8;
      // GR - grassland
      public static final int GR = 9;
      // TR - tropical rainforst
      public static final int TR = 10;
      // TS - tropical seasonal forest
      public static final int TS = 11;
      // SD - subtropical desert
      public static final int SD = 12;
      // WA - water
      public static final int WA = 13;
      // SA - saltwater
      public static final int SA = 14;

      public static final int biomeCount = 15;
      public static final int[] biomes = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 };
      public static final String[] shortnames = new String[] { "SN", "TU", "BA", "SC", "TA", "SH", "TD", "RF", "DF",
            "GR", "TR", "TS", "SD", "WA", "SA" };
      public static final String[] longnames = new String[] { "Snowy", "Tundra", "Barren", "Scorched", "Taiga",
            "Shrubland", "Temperate Desert", "Temperate Rainforest", "Temperate Deciduous Forest", "Grassland",
            "Tropical Rainforest", "Tropical Seasonal Forest", "Subtropical Desert", "Freshwater", "Saltwater" };
      public static final Color[] colors = new Color[] { new Color(255, 255, 255), new Color(230, 230, 230),
            new Color(160, 160, 160), new Color(89, 89, 89), new Color(98, 240, 134), new Color(180, 225, 84),
            new Color(255, 200, 0), new Color(25, 200, 18), new Color(0, 255, 0), new Color(125, 255, 0),
            new Color(10, 100, 10), new Color(100, 130, 0), new Color(215, 215, 105), new Color(0, 0, 255),
            new Color(0, 50, 255) };

      public static int biomeID(int m, int e) {
         if (m == 7) {
            return WA;
         } else if (m == 6) {
            switch (e) {
            case 4:
               return SN;
            case 3:
               return TA;
            case 2:
               return RF;
            case 1:
               return TR;
            }
         } else if (m == 5) {
            switch (e) {
            case 4:
               return SN;
            case 3:
               return TA;
            case 2:
               return DF;
            case 1:
               return TR;
            }
         } else if (m == 4) {
            switch (e) {
            case 4:
               return SN;
            case 3:
               return SH;
            case 2:
               return DF;
            case 1:
               return TS;
            }
         } else if (m == 3) {
            switch (e) {
            case 4:
               return TU;
            case 3:
               return SH;
            case 2:
               return GR;
            case 1:
               return TS;
            }
         } else if (m == 2) {
            switch (e) {
            case 4:
               return BA;
            case 3:
               return TD;
            case 2:
               return GR;
            case 1:
               return GR;
            }
         } else if (m == 1) {
            switch (e) {
            case 4:
               return SC;
            case 3:
               return TD;
            case 2:
               return TD;
            case 1:
               return SD;
            }
         }
         return -1;
      }

      public static int biomeID(int[] biomeMap) {
         return biomeID(biomeMap[0], biomeMap[1]);
      }

      public static String shortname(int m, int e) {
         return shortnames[biomeID(m, e)];
      }

      public static String shortname(int i) {
         return shortnames[i];
      }

      public static String longname(int m, int e) {
         return longnames[biomeID(m, e)];
      }

      public static String longname(int i) {
         return longnames[i];
      }

      public static Color color(int m, int e) {
         return colors[biomeID(m, e)];
      }

      public static Color color(int i) {
         return colors[i];
      }
   }
}