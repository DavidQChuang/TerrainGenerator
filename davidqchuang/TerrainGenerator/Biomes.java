package davidqchuang.TerrainGenerator;

import java.awt.Color;
import java.util.List;

public class Biomes {
    public enum Biome {
    	INVALID(0),
        ///<summary> SN - snow</summary>
        SN(1),
        ///<summary> TU - tundra</summary>
        TU(2),
        ///<summary> BA - barren</summary>
        BA(3),
        ///<summary> SC - scorched</summary>
        SC(4),
        ///<summary> TA - taiga</summary>
        TA(5),
        ///<summary> SH - shrubland</summary>
        SH(6),
        ///<summary> TD - temperate desert</summary>
        TD(7),
        ///<summary> RF - temperate rainforest</summary>
        RF(8),
        ///<summary> DF - teperate deciduous forest</summary>
        DF(9),
        ///<summary> GR - grassland</summary>
        GR(10),
        ///<summary> TR - tropical rainforest</summary>
        TR(11),
        ///<summary> TS - tropical seasonal forest</summary>
        TS(12),
        ///<summary> SD - subtropical desert</summary>
        SD(13),
        ///<summary> WA - water</summary>
        WA(14),
        ///<summary> SA - saltwater</summary>
        SA(15);
    	public int index;
    	
    	Biome(int x){
    		index = x;
    	}
    }

    public static final String[] Names = new String[] {
        "Snowy", "Tundra", "Barren", "Scorched", "Taiga",
        "Shrubland", "Temperate Desert", "Temperate Rainforest", "Temperate Deciduous Forest", "Grassland",
        "Tropical Rainforest", "Tropical Seasonal Forest", "Subtropical Desert", "Freshwater", "Saltwater" };
    public static final String[] ShortNames = new String[] {
            "SN", "TU", "BA", "SC", "TA",
            "SH", "TD", "RF", "TD", "GR",
            "TR", "TS", "SD", "WA", "SA" };
    public static final Color[] Colors = new Color[] {
        new Color(255, 255, 255, 255), new Color(230, 230, 230, 255), new Color(160, 160, 160, 255), new Color(89, 89, 89, 255), new Color(98, 240, 134, 255),
        new Color(180, 225, 84, 255), new Color(255, 200, 0, 255), new Color(25, 200, 18, 255), new Color(0, 255, 0, 255), new Color(125, 255, 0, 255),
        new Color(10, 100, 10, 255), new Color(100, 130, 0, 255), new Color(215, 215, 105, 255), new Color(0, 0, 255, 255), new Color(0, 50, 255, 255) };

    public static Biome GetBiome(float height, float moisture) {
    	return GetBiome(GetStrata(height, HeightStrata), GetStrata(moisture, MoistureStrata));
    }

    private static Biome GetBiome(int strata, int moisture) {
        if (moisture >= 7) {
            return Biome.WA;
        } else if (moisture == 6) {
            switch (strata) {
                case 4:
                    return Biome.SN;
                case 3:
                    return Biome.TA;
                case 2:
                    return Biome.RF;
                case 1:
                    return Biome.TR;
            }
        } else if (moisture == 5) {
            switch (strata) {
                case 4:
                    return Biome.SN;
                case 3:
                    return Biome.TA;
                case 2:
                    return Biome.DF;
                case 1:
                    return Biome.TR;
            }
        } else if (moisture == 4) {
            switch (strata) {
                case 4:
                    return Biome.SN;
                case 3:
                    return Biome.SH;
                case 2:
                    return Biome.DF;
                case 1:
                    return Biome.TS;
            }
        } else if (moisture == 3) {
            switch (strata) {
                case 4:
                    return Biome.TU;
                case 3:
                    return Biome.SH;
                case 2:
                    return Biome.GR;
                case 1:
                    return Biome.TS;
            }
        } else if (moisture == 2) {
            switch (strata) {
                case 4:
                    return Biome.BA;
                case 3:
                    return Biome.TD;
                case 2:
                    return Biome.GR;
                case 1:
                    return Biome.GR;
            }
        } else if (moisture <= 1) {
            switch (strata) {
                case 4:
                    return Biome.SC;
                case 3:
                    return Biome.TD;
                case 2:
                    return Biome.TD;
                case 1:
                    return Biome.SD;
            }
        }
       	return Biome.INVALID;
    }

    public static Color GetBiomeColor(float height, float moisture) {
    	return Colors[GetBiome(height, moisture).index - 1];
    }

    public static int[] MoistureStrata = new int[] {
        1, 4, 8, 16, 32, 128
    };
    public static int[] HeightStrata = new int[] {
        7, 14, 20
    };

    public static int GetStrata(float height, int[] strata) {
        int i;
        for (i = 0; i < strata.length; i++) {
            if (height < strata[i]) {
                return i + 1;
            }
        }

        return i + 1;
    }
}