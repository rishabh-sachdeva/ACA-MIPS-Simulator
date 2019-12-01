package functionalUnits;

import java.util.ArrayList;
import java.util.List;

import cache.DCache;
import instructions.CycleMaintain;
import stage.WB;

public class MemoryUnit implements FunctionalUnit{
	public static int cycle_count = 2;//include memory stage, denpends on config
	static boolean busy = false;
	public static int curr_cycle = 0;
	public static String instruction;
	public static String inst_name;
	public static CycleMaintain cycle_stats;
	private static final int cycle_count_int_ops = 1;

	static List<String> int_ops = new ArrayList<>();
	public static void setIntOps() {
		int_ops.add("DADDI");
		int_ops.add("DADD");
		int_ops.add("DSUB");
		int_ops.add("DSUBI");
		int_ops.add("AND");
		int_ops.add("ANDI");
		int_ops.add("OR");
		int_ops.add("ORI");
	}
	public static CycleMaintain getCycle_stats() {
		return cycle_stats;
	}
	public static void setCycle_stats(CycleMaintain cycle_stats) {
		MemoryUnit.cycle_stats = cycle_stats;
	}

	public static String getInst_name() {
		return inst_name;
	}
	public static void setInst_name(String inst_name) {
		MemoryUnit.inst_name = inst_name;
	}
	public static String getInstruction() {
		return instruction;
	}
	public static void setInstruction(String instruction) {
		MemoryUnit.instruction = instruction;
	}
	public MemoryUnit(int cycle_count) {
		this.cycle_count=cycle_count;
	}
	public static void burn_cycle_int_ops(int cycle_num) {
		curr_cycle++;
		if(curr_cycle>=cycle_count_int_ops) {
			if(!WB.isBusy() && WB.getInst_name()==null && WB.getInstruction()==null) {
				WB.setInst_name(inst_name);
				WB.setInstruction(instruction);
				cycle_stats.setEX_end(cycle_num);
				WB.setCycle_stats(cycle_stats);
				curr_cycle=0;
				busy = false;
				setInst_name(null);
				setInstruction(null);
			}else {
				cycle_stats.setStruct_hazard(true);
			}
		}
	}
	static boolean just_searched=false;
	static int res1 = 0;
	static int res2 = 0;
	public static void burn_cycle_config(int cycle_num) {
		curr_cycle++;
		//handle cache here
		if(inst_name.endsWith(".D")) {
			String[] details = instruction.trim().split(" ",2)[1].trim().split(",");
			int bracket_open_idx = details[1].trim().indexOf('(');
			String reg_idx =(details[1].trim().substring(bracket_open_idx+2,details[1].trim().length()-1));
			String offset = details[1].trim().substring(0, bracket_open_idx);
			int add = memory.Register.getRegList().get(java.lang.Integer.parseInt(reg_idx));

			if(!just_searched) {
				res1 = DCache.searchInCache(DCache.findFirstEntry(add+java.lang.Integer.parseInt(offset)));
				res2 = DCache.searchInCache(DCache.findFirstEntry(add+java.lang.Integer.parseInt(offset)+4));
				just_searched=true;
			}
			if(res1==-1 && res2==-1) {
				//both miss
				cycle_count=2*DCache.getK();
			}
			else if(res1!=-1 && res2!=-1) {
				//both hit
				cycle_count=2 * DCache.getK();
			}else if(res1!=-1 || res2!=-1) {
				//one miss
				cycle_count=DCache.getK() + DCache.getMiss_time();
			} 
		}
		if(curr_cycle>=cycle_count) {
			if(!WB.isBusy() && WB.getInst_name()==null && WB.getInstruction()==null) {
				just_searched=false;
				WB.setInst_name(inst_name);
				WB.setInstruction(instruction);
				cycle_stats.setEX_end(cycle_num);
				WB.setCycle_stats(cycle_stats);
				curr_cycle=0;
				busy = false;
				setInst_name(null);
				setInstruction(null);
			}else {
				cycle_stats.setStruct_hazard(true);
			}
		
		}
	}
	public static int getCurrCycle() {
		return curr_cycle;
	}
	public static void setCurCycle(int curr_cycle) {
		Integer.curr_cycle = curr_cycle;
	}
	public static boolean isBusy() {
		return busy;
	}
	public void setBusy(boolean busy) {
		MemoryUnit.busy = busy;
	}
	@Override
	public boolean getPipeline() {
		return false;
	}
	@Override
	public void setPipeline(boolean if_pipeline) {

	}
	@Override
	public int getCycleCount() {
		return 0;
	}
	@Override
	public void setCycleCount(int cycle_count) {
		MemoryUnit.cycle_count=cycle_count;
	}
	public static void execute(int cycle_num) {
		//just burn cycle
		if(inst_name!=null && instruction!=null) {
			busy=true;
			if(int_ops.contains(inst_name)) {
				burn_cycle_int_ops(cycle_num);
			}else {
				burn_cycle_config(cycle_num);
			}

		}
	}
}
