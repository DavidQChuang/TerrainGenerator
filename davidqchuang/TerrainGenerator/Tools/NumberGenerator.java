package davidqchuang.TerrainGenerator.Tools;

/// <summary>
/// A class for generating psuedo-random numbers.
/// </summary>
public class NumberGenerator {
    /// <summary>
    /// Bitmask for the IEEE float32 mantissa.
    /// </summary>
   	public static final int ieeeMantissa = 0x007FFFFF;
    /// <summary>
    /// Bits of 1.0 in IEEE float32.
    /// </summary>
   	public static final int ieeeOne = 0x3F800000; // 1.0 in IEEE binary32

    /// <summary>
    /// Hashes the given uint to a psuedo-random value.
    /// </summary>
    /// <param name="x"></param>
    /// <returns></returns>

    //[MethodImpl(MethodImplOptions.AggressiveInlining)]
    public static int hash(int x) {
        x ^= x << 13;
		x += (x << 10) + 1;
		x ^= x >> 17;
        x ^= x << 5;
        return x;
    }
    //x += (x << 10);
    //x ^= (x >> 6);
    //x += (x << 3);
    //x ^= (x >> 11);
    //x += (x << 15);

    /// <summary>
    /// Hashes the given uint2 to a psuedo-random value.
    /// </summary>
    /// <param name="v"></param>
    /// <returns></returns>
    //[MethodImpl(MethodImplOptions.AggressiveInlining)]
    public static int hash(int2 v) {
       return hash(v.x ^ hash(v.y));
    }
    /// <summary>
    /// Hashes the given uint3 to a psuedo-random value.
    /// </summary>
    /// <param name="v"></param>
    /// <returns></returns>
    //[MethodImpl(MethodImplOptions.AggressiveInlining)]
    public static int hash(int3 v) {
        return hash(v.x ^ hash(v.y) ^ hash(v.z));
    }


    /// <summary>
    /// Construct a float from the given uint with range [0,1) using low 23 bits.
    /// All zeroes yields 0.0, all ones yields the next smallest representable value below 1.0.
    /// </summary>
    public static float floatConstruct(int m) {
            m &= ieeeMantissa;                     // Keep only mantissa bits (fractional part)
            m |= ieeeOne;                          // Add fractional part to 1.0

            float f = Float.intBitsToFloat(m);       // Range [1:2]
            return f - 1.0f;      // Range [0:1]
    }

    // Pseudo-random value with range [0,1)
    //[MethodImpl(MethodImplOptions.AggressiveInlining)]
    public static float rand11(float x) { return floatConstruct(hash(Float.floatToIntBits((x)))); }
    public static float rand21(float2 v) { return floatConstruct(hash(new int2(Float.floatToIntBits(v.x),Float.floatToIntBits(v.y)))); }
    public static float rand31(float3 v) { return floatConstruct(hash(new int3(Float.floatToIntBits(v.x),Float.floatToIntBits(v.y),Float.floatToIntBits(v.z)))); }
    	
    public static float2 rand12(float x) { float o = rand11(x); return new float2(o, rand11(o)); }
    public static float2 rand22(float2 v) { float o = rand21(v); return new float2(o, rand11(o)); }
    public static float2 rand32(float3 v) { float o = rand31(v); return new float2(o, rand11(o)); }
}
