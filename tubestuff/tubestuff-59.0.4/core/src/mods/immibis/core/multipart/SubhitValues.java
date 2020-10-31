package mods.immibis.core.multipart;

public final class SubhitValues {
	private SubhitValues() {}
	

	/*
	public static boolean isCoverSystem(int subHit) {return subHit < 0;}
	public static int getTilePartIndex(int subHit) {return subHit;}
	public static int getCSPartIndex(int subHit) {return -1 - subHit;}
	public static int getFromCSPartIndex(int k) {return -1 - k;}
	public static int getFromTilePartIndex(int k) {return k;}
	*/
	
	private static final int MAGIC_NUMBER = 0x735D6C00; // chosen at random; lower 12 bits are 0
	public static boolean isCoverSystem(int subHit) {return (subHit & 0xFFFFFC00) == MAGIC_NUMBER;}
	public static int getPartIndex(int subHit) {return (subHit & 0x3FF);}
	public static int getFromCSPartIndex(int k) {return k | MAGIC_NUMBER;}
}
