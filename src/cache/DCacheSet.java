package cache;

public class DCacheSet {
	public static DCacheBlock block1;
	public static DCacheBlock getBlock1() {
		return block1;
	}
	public static void setBlock1(DCacheBlock block1) {
		DCacheSet.block1 = block1;
	}
	public static DCacheBlock getBlock2() {
		return block2;
	}
	public static void setBlock2(DCacheBlock block2) {
		DCacheSet.block2 = block2;
	}
	public static DCacheBlock block2;
}
