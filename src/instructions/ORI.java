package instructions;

import stage.ID;

public class ORI {
	public static int final_source=-1;
	public static int final_dest=-1;
	public static int getFinal_source() {
		return final_source;
	}
	public static void setFinal_source(int final_source) {
		ORI.final_source = final_source;
	}
	public static int getFinal_dest() {
		return final_dest;
	}
	public static void setFinal_dest(int final_dest) {
		ORI.final_dest = final_dest;
	}
	public static void execute(CycleMaintain cycle_stats) {
		
		int src1 = ID.getSrc1();
		int src2 = ID.getSrc2();
		final_dest= ID.getDest();
		final_source = (src1 | src2);
		cycle_stats.setFinal_src(final_source);
		cycle_stats.setFinal_dest(final_dest);

	}
}
