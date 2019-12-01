package functionalUnits;

import java.util.LinkedList;
import java.util.Queue;

import instructions.CycleMaintain;
import stage.WB;

public class Multiplier implements FunctionalUnit{
	public static boolean if_pipeline = false;
	public static int cycle_count = 0;
	boolean busy = false;
	static boolean wb_pending = false;

	static Queue<CycleMaintain> queue = new LinkedList<>();
	private static CycleMaintain wb_pending_instruction; 

	public Multiplier(boolean if_pipeline, int cycle_count) {

		Multiplier.cycle_count=cycle_count;
		Multiplier.if_pipeline=if_pipeline;
	}

	public boolean isBusy() {
		return busy;
	}

	public void setBusy(boolean busy) {
		this.busy = busy;
	}
	public boolean getPipeline() {
		return if_pipeline;
	}
	public void setPipeline(boolean if_pipeline) {
		this.if_pipeline = if_pipeline;
	}


	public int getCycleCount() {
		return cycle_count;
	}
	public void setCycleCount(int cycle_count) {
		this.cycle_count = cycle_count;
	}
	public static boolean insert_when_no_pipeline(CycleMaintain instr_obj) {
		if(queue.size()==0) {
			queue.offer(instr_obj);
			return true;
		}
		return false;
	}
	public static boolean insertInPipeline(CycleMaintain instr_obj) {
		if(queue.size()<cycle_count) {
			queue.offer(instr_obj);
			return true;
		}
		return false;
	}
	public static void execute(int cycle_num) {
		if(!wb_pending && queue.size()>=0 && queue.peek()!=null && 
				cycle_num - queue.peek().getEntry_FU() + 1>=cycle_count) {
			CycleMaintain inst_obj = queue.remove();

			setWBdetails(cycle_num, inst_obj);
		}else if(wb_pending) {
			setWBdetails(cycle_num, wb_pending_instruction);;
		}
	}
	private static void setWBdetails(int cycle_num, CycleMaintain inst_obj) {
		if(!WB.isBusy() && WB.getInst_name()==null && WB.getInstruction()==null) {
			WB.setInst_name(inst_obj.getInst_name());
			WB.setInstruction(inst_obj.getInstruction());
			inst_obj.setEX_end(cycle_num);
			WB.setCycle_stats(inst_obj);
			wb_pending=false;
			wb_pending_instruction=null;
		}else {
			inst_obj.setStruct_hazard(true);
			wb_pending_instruction = inst_obj;
			wb_pending=true;
		}
	}
}
