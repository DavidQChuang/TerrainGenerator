package davidqchuang.TerrainGenerator.Visualization;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import davidqchuang.TerrainGenerator.Biomes;
import davidqchuang.TerrainGenerator.HeightmapNode;
import davidqchuang.TerrainGenerator.Tools.int2;

public class MeshGenerators {
   public static final int scale = 2;
   
//   public static List<Triangle> generateGlobe(MapGenerator map){
//      List<Triangle> circle = new ArrayList<>();
//      int subdivisions = map.pM;
//      
//      double nextY = Math.PI / subdivisions;
//      double nextT = Math.PI * 2 / subdivisions;
//      
//      for(MapGenerator.TectonicPlate te : map.tectonics) {
//          Color c;
////          if(te.direction.y < 0) o = new Color(0,(int)(Math.random()*100),255);
////          int s = 150 + (int)te.direction.y;
//          int s = 150;
//          for(int2 v : te.coordinates) {
//              double y = nextY * v.y + Math.PI * 0.5;
//              double t = nextT * v.x;
//              if(v.x == te.x && v.y == te.y) c = Color.red;
//              else {
//                  c = new Color(0,(int)(map.partitions[v.x][v.y].height*5+100)&0xFF, 0);
//                  if(map.partitions[v.x][v.y].height < 0) 
//                      c = new Color(0,0,(int)Math.abs(map.partitions[v.x][v.y].height*5+100)&0xFF);
//              }
//              double factor = Math.abs(Math.cos(y));
//              double nextfactor = Math.abs(Math.cos(y+nextY));
//              s = map.partitions[v.x][v.y].height + 180;
//              if(s < 180) s = 180;
//              circle.add(new Triangle(
//                      new Vertex(Math.cos(t)*nextfactor,Math.sin(y+nextY),Math.sin(t)*nextfactor).scale(s),
//                      new Vertex(Math.cos(t)*factor,Math.sin(y),Math.sin(t)*factor).scale(s),
//                      new Vertex(Math.cos(t+nextT)*nextfactor,Math.sin(y+nextY),Math.sin(t+nextT)*nextfactor).scale(s),
//                      c));
//              circle.add(new Triangle(
//                      new Vertex(Math.cos(t+nextT)*factor,Math.sin(y),Math.sin(t+nextT)*factor).scale(s),
//                      new Vertex(Math.cos(t)*factor,Math.sin(y),Math.sin(t)*factor).scale(s),
//                      new Vertex(Math.cos(t+nextT)*nextfactor,Math.sin(y+nextY),Math.sin(t+nextT)*nextfactor).scale(s),
//                      c));
//          }
//      }
//      
//      return circle;
//   }
   
//   public static List<Triangle> generateTectonicGlobe(MapGenerator map){
//      List<Triangle> tcircle = new ArrayList<>();
//      
//      int subdivisions = map.pM;
//      
//      double nextY = Math.PI / subdivisions;
//      double nextT = Math.PI * 2 / subdivisions;
//      
//      for(MapGenerator.TectonicPlate te : map.tectonics) {
//          Color c = new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
////          int s = 150 + (int)te.direction.y;
//          int s = 180;
//          for(int2 v : te.coordinates) {
//              double y = nextY * v.y + Math.PI * 0.5;
//              double t = nextT * v.x;
//              
//              double factor = Math.abs(Math.cos(y));
//              double nextfactor = Math.abs(Math.cos(y+nextY));
//              tcircle.add(new Triangle(
//                      new Vertex(Math.cos(t)*nextfactor,Math.sin(y+nextY),Math.sin(t)*nextfactor).scale(s),
//                      new Vertex(Math.cos(t)*factor,Math.sin(y),Math.sin(t)*factor).scale(s),
//                      new Vertex(Math.cos(t+nextT)*nextfactor,Math.sin(y+nextY),Math.sin(t+nextT)*nextfactor).scale(s),
//                      c));
//              tcircle.add(new Triangle(
//                      new Vertex(Math.cos(t+nextT)*factor,Math.sin(y),Math.sin(t+nextT)*factor).scale(s),
//                      new Vertex(Math.cos(t)*factor,Math.sin(y),Math.sin(t)*factor).scale(s),
//                      new Vertex(Math.cos(t+nextT)*nextfactor,Math.sin(y+nextY),Math.sin(t+nextT)*nextfactor).scale(s),
//                      c));
//          }
//      }
//      
//      return tcircle;
//   }
   
//   public static List<Triangle> generateFlatMap(MapGenerator map){
//      List<Triangle> flatmap = new ArrayList<>();
//      int s = scale * 2;
//      int br = map.pM/2;
//      
//      for(MapGenerator.TectonicPlate te : map.tectonics) {
//          Color o = new Color((float)Math.random(), (float)Math.random(), 0);
//          Color c = o;
////          if(te.direction.y < 0) o = Color.blue;
//          for(int2 v : te.coordinates) {
////              if(v.x == te.x && v.y == te.y) c = Color.red;
////              else c = o;
//              c = new Color(50,(int)Math.min(Math.max(map.partitions[v.x][v.y].height*5+100, 0), 255),50);
//              if(map.partitions[v.x][v.y].height < 0) c = new Color(0,0,(int)Math.min(Math.max(map.partitions[v.x][v.y].height+100, 0), 255));
//              flatmap.add(new Triangle(
//                      new Vertex(v.x-br,v.y-br,map.partitions[v.x][v.y].height).scale(s),
//                      new Vertex(v.x+1-br,v.y-br,map.partitions[v.x][v.y].height).scale(s),
//                      new Vertex(v.x+1-br,v.y+1-br,map.partitions[v.x][v.y].height).scale(s),
//                      c));
//              flatmap.add(new Triangle(
//                      new Vertex(v.x-br,v.y-br,map.partitions[v.x][v.y].height).scale(s),
//                      new Vertex(v.x-br,v.y+1-br,map.partitions[v.x][v.y].height).scale(s),
//                      new Vertex(v.x+1-br,v.y+1-br,map.partitions[v.x][v.y].height).scale(s),
//                      c));
//          }
//      }
//      
//      return flatmap;
//   }
}
