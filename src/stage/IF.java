package stage;

import java.util.List;

import cache.ICache;
import entry.Simulator;
import functionalUnits.MemoryUnit;
import instructions.CycleMaintain;

public class IF {

	static boolean busy=false;
	static  int cycle_count=1;
	static int curr_cycle = 0;
	static List<String> instructions = Simulator.instructions;
	public static String instruction;
	public static String inst_name;
	public static boolean branch_detected=false;
	public static boolean isBranch_detected() {
		return branch_detected;
	}
	public static void setBranch_detected(boolean branch_detected) {
		IF.branch_detected = branch_detected;
	}
	public static String getInst_name() {
		return inst_name;
	}
	public static void setInst_name(String inst_name) {
		IF.inst_name = inst_name;
	}
	public static String getInstruction() {
		return instruction;
	}
	public static void setInstruction(String instruction) {
		IF.instruction = instruction;
	}

	public static void setCycle_count(int cycle_count) {
		IF.cycle_count = cycle_count;
	}

	public static int getCycle_count() {
		return cycle_count;
	}



	public boolean isBusy() {
		return busy;
	}

	public void setBusy(boolean busy) {
		this.busy = busy;
	}
	public static void burn_cycle() {
		curr_cycle++;

	}
	public static void nullifyInstructions() {
		if(curr_cycle>=cycle_count) {
			curr_cycle=0;
			busy = false;
			setInst_name(null);
			setInstruction(null);
		}
	}
	public static int idx =1;

	public static String fetchInstruction( int cycle_num) {
		if(instruction==null && !branch_detected) {
			if(instructions.size()>idx) {
				instruction = instructions.get(idx);
				idx++;
			}
		}else if(branch_detected) {
			branch_detected=false; // hack to skip one cycle
		}
		boolean cache_set=false;
		if(instruction!=null) {
			if(!ICache.search_cache(instruction)) {
				cache_set = ICache.fill_cache(instructions, idx-1);

				//return null;
			}else if(ICache.handleKcycleRequiremnt()){
				//handle k cycle requirement to search cache
					cache_set = true;
				}
			busy= true;
			burn_cycle();
			String[] details = instruction.split(" ",2);
			inst_name=details[0].trim();
			if( ID.instruction==null && ID.inst_name==null && cache_set) {
				CycleMaintain stats = new CycleMaintain();
				stats.setIF_end(cycle_num);
				stats.setInstruction(instruction);
				stats.setInst_name(inst_name);
				ID.setCycle_stats(stats);
				ID.setInst_name(inst_name);
				ID.setInstruction(instruction);
				nullifyInstructions();
				//idx++;
			}
			return inst_name;
		}
		else {
			//IF is busy
			return null;
		}
	}

}
