	package stage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import entry.Simulator;
import hazard.UsedRF;
import instructions.CycleMaintain;

public class WB {
	private static final String TAB_SPACE = "\t";
	static boolean busy;
	static int curr_cycle = 0;
	private static int cycle_count=1;
	public static String instruction;
	public static String inst_name;
	public static CycleMaintain cycle_stats;
	public static List<CycleMaintain> results = new ArrayList<>();
	private static final int FIXED_LEN = 20;

	public static void add_result(CycleMaintain row) {
		results.add(row);
	}
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
		results.add(stats);
		System.out.print(stats.getInstruction());
		System.out.print(TAB_SPACE);
		System.out.print(stats.getIF_end()+TAB_SPACE);
		System.out.print(stats.getID_end()+ TAB_SPACE);
		System.out.print(stats.getEX_end()+ TAB_SPACE);
		System.out.print(stats.getWB_end()+TAB_SPACE);
		System.out.print((stats.isRAW_hazard()?"Y":"N")+TAB_SPACE);
		System.out.print((stats.isWAR_hazard()?"Y":"N")+TAB_SPACE);
		System.out.print((stats.isWAW_hazard()?"Y":"N")+TAB_SPACE);
		System.out.print((stats.isStruct_hazard()?"Y":"N")+TAB_SPACE);

		System.out.println("\n");
	}
	public static void writeToFile() throws IOException {
		FileWriter writer = new FileWriter(Simulator.result_file); 
		for(CycleMaintain stats: results) {
		  writer.write(statsToString(stats) + System.lineSeparator());
		}
		writer.close();
	}
	private static String statsToString(CycleMaintain stats) {
		String raw_haz = stats.isRAW_hazard()?"Y":"N";
		String war_haz = stats.isWAR_hazard()?"Y":"N";
		String waw_haz = stats.isWAW_hazard()?"Y":"N";
		String struct_haz =stats.isStruct_hazard()?"Y":"N";
		String if_end =String.valueOf(stats.getIF_end()) ;
		if(if_end.equals("0")) {
			if_end = " ";
		}
		String ex_end =String.valueOf(stats.getEX_end());
		if(ex_end.equals("0")) {
			ex_end = " ";
		}
		String wb_end =String.valueOf(stats.getWB_end());
		if(wb_end.equals("0")) {
			wb_end = " ";
		}	
		String line = stats.getInstruction()+ getSpaces(stats.getInstruction())+if_end+  TAB_SPACE+stats.getID_end() + TAB_SPACE+ ex_end+ TAB_SPACE+
		wb_end + TAB_SPACE+ raw_haz + TAB_SPACE+ war_haz +  TAB_SPACE+waw_haz + TAB_SPACE+ struct_haz;
		return line;
	}
	private static String getSpaces(String instruction) {
		int num_spaces_req = FIXED_LEN - instruction.length();
		String spacing="";
		for(int i=0;i<num_spaces_req;i++) {
			spacing +=" ";
		}
		return spacing;
	}
}
