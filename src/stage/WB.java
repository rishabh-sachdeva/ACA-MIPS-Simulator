	package stage;

import java.util.List;

import hazard.UsedRF;
import instructions.CycleMaintain;

public class WB {
	static boolean busy;
	static int curr_cycle = 0;
	private static int cycle_count=1;
	public static String instruction;
	public static String inst_name;
	public static CycleMaintain cycle_stats;

	public static CycleMaintain getCycle_stats() {
		return cycle_stats;
	}
	public static void setCycle_stats(CycleMaintain cycle_stats) {
		WB.cycle_stats = cycle_stats;
	}
	public static String getInst_name() {
		return inst_name;
	}
	public static void setInst_name(String inst_name) {
		WB.inst_name = inst_name;
	}
	public static String getInstruction() {
		return instruction;
	}

	public static void setInstruction(String instruction) {
		WB.instruction = instruction;
	}

	public static void burn_cycle() {
		curr_cycle++;
		if(curr_cycle==cycle_count) {
			curr_cycle=0;
			busy = false;
			setInst_name(null);
			setInstruction(null);
		}
	}

	public static boolean isBusy() {
		return busy;
	}

	public void setBusy(boolean busy) {
		this.busy = busy;
	}

	public static void writeBack(int cycle_num) {

		if(instruction!=null && inst_name!=null) {
			free_registers();
			int val = cycle_stats.getFinal_src();//Instruction.getFinal_dest();
			int destination = cycle_stats.getFinal_dest();//Instruction.getFinal_source();
			if(destination!=-1 && val!=-1) { //no WB needed otherwise
				busy= true;
				burn_cycle();
				List<Integer> reg = memory.Register.getRegList();
				reg.set(destination,val);
				cycle_stats.setWB_end(cycle_num);
				printStats(cycle_stats);
			}else if(inst_name.endsWith(".D")) {
				busy= true;
				burn_cycle();
				cycle_stats.setWB_end(cycle_num);
				printStats(cycle_stats);
			}
		}
	}
	private static void free_registers() {
		String write_register = getWriteRegister();
		UsedRF.releaseRegister(write_register);
	}

	private static String getWriteRegister() {
		String[] details = instruction.trim().split(" ",2);
		String[] rightPart = details[1].trim().split(",");
		String write_register = rightPart[0].trim();
		return write_register;
	}

	private static void printStats(CycleMaintain stats) {
		System.out.print(stats.getInstruction());
		System.out.print("\t");
		System.out.print(stats.getIF_end()+"\t");
		System.out.print(stats.getID_end()+ "\t");
		System.out.print(stats.getEX_end()+ "\t");
		System.out.print(stats.getWB_end()+"\t");
		System.out.print((stats.isRAW_hazard()?"Y":"N")+"\t");
		System.out.print((stats.isWAR_hazard()?"Y":"N")+"\t");
		System.out.print((stats.isWAW_hazard()?"Y":"N")+"\t");
		System.out.print((stats.isStruct_hazard()?"Y":"N")+"\t");

		System.out.println("\n");
	}
}
