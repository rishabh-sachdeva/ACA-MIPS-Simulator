package functionalUnits;

import instructions.AND;
import instructions.ANDI;
import instructions.CycleMaintain;
import instructions.DADD;
import instructions.DADDI;
import instructions.DSUB;
import instructions.DSUBI;
import instructions.LW;
import instructions.OR;
import instructions.ORI;
import instructions.SW;

public class Integer implements FunctionalUnit{
	boolean if_pipeline = false;
	static int cycle_count = 1;//include memory stage
	static boolean busy = false;
	static int curr_cycle = 0;
	static String instruction;
	static String inst_name;
	public static CycleMaintain cycle_stats;

	public static CycleMaintain getCycle_stats() {
		return cycle_stats;
	}
	public static void setCycle_stats(CycleMaintain cycle_stats) {
		Integer.cycle_stats = cycle_stats;
	}


	public static String getInst_name() {
		return inst_name;
	}

	public static void setInst_name(String inst_name) {
		Integer.inst_name = inst_name;
	}

	public static String getInstruction() {
		return instruction;
	}

	public static void setInstruction(String instruction) {
		Integer.instruction = instruction;
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
		this.busy = busy;
	}
	public Integer(boolean if_pipeline, int cycle_count) {

		this.cycle_count=cycle_count;
		this.if_pipeline=if_pipeline;
	}
	@Override
	public void setPipeline(boolean if_pipeline) {
		this.if_pipeline = if_pipeline;
	}

	@Override
	public boolean getPipeline() {
		return if_pipeline;
	}
	@Override
	public int getCycleCount() {
		return cycle_count;
	}
	@Override
	public void setCycleCount(int cycle_count) {
		this.cycle_count = cycle_count;
	}
	public static void executeInstruction() {
		//burn_cycle();
		if(instruction!=null && Integer.inst_name!=null) {
			busy= true;
			switch(inst_name) {
			case "L.D":
				//nothing to execute here
				burn_cycle();
				if(setMemoryUnit()) {
					nullifyInstructions();
				}
				break;
			case "DADDI":
				DADDI.execute(cycle_stats);
				burn_cycle();
				if(setMemoryUnit()) {
					nullifyInstructions();
				}
				break;
			case "DADD":	
				DADD.execute(cycle_stats);
				burn_cycle();
				if(setMemoryUnit()) {
					nullifyInstructions();
				}
				break;

			case "LW":
				LW.execute(cycle_stats);
				burn_cycle();
				if(setMemoryUnit()) {
					nullifyInstructions();
				}
				break;
			case "S.D":
				burn_cycle();
				if(setMemoryUnit()) {
					nullifyInstructions();
				}
				break;
			case "SW":
				SW.execute(cycle_stats);
				burn_cycle();
				if(setMemoryUnit()) {
					nullifyInstructions();
				}
				break;
			case "DSUB":
				DSUB.execute(cycle_stats);
				burn_cycle();
				if(setMemoryUnit()) {
					nullifyInstructions();
				}
				break;
				
			case "DSUBI":
				DSUBI.execute(cycle_stats);
				burn_cycle();
				if(setMemoryUnit()) {
					nullifyInstructions();
				}
				break;	
			case "AND":
				AND.execute(cycle_stats);
				burn_cycle();
				if(setMemoryUnit()) {
					nullifyInstructions();
				}
				break;
			case "ANDI":
				ANDI.execute(cycle_stats);
				burn_cycle();
				if(setMemoryUnit()) {
					nullifyInstructions();
				}
				break;
			case "OR":
				OR.execute(cycle_stats);
				burn_cycle();
				if(setMemoryUnit()) {
					nullifyInstructions();
				}
				break;
			case "ORI":
				ORI.execute(cycle_stats);
				burn_cycle();
				if(setMemoryUnit()) {
					nullifyInstructions();
				}
				break;
			}
		}else {
			//burn_cycle();
		}
	}

	public static boolean setMemoryUnit() {
		if( MemoryUnit.getInstruction()==null
				&&instruction!=null && inst_name!=null) {
			MemoryUnit.setInstruction(instruction);
			MemoryUnit.setInst_name(Integer.inst_name);
			MemoryUnit.setCycle_stats(cycle_stats);
			//			burn_cycle();
			//nullifyInstructions();
			return true;
		}else {
			//struct hazard 
			cycle_stats.setStruct_hazard(true);
		}
		return false;
	}
}
