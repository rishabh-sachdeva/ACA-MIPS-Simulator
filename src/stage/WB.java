package stage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	private static final int FIXED_LEN = 30;
	
	public static int i_cache_request_total=0;
	public static int i_cache_request_hits=0;
	public static int d_cache_request_total=0;
	public static int d_cache_request_hits=0;
	public static int getI_cache_request_total() {
		return i_cache_request_total;
	}
	public static void setI_cache_request_total(int i_cache_request_total) {
		WB.i_cache_request_total = i_cache_request_total;
	}

	public static int getI_cache_request_hits() {

		return i_cache_request_hits;
	}
	public static void setI_cache_request_hits(int i_cache_request_hits) {
		WB.i_cache_request_hits = i_cache_request_hits;
	}
	public static int getD_cache_request_total() {
		return d_cache_request_total;
	}
	public static void setD_cache_request_total(int d_cache_request_total) {
		WB.d_cache_request_total = d_cache_request_total;
	}
	public static int getD_cache_request_hits() {
		return d_cache_request_hits;
	}
	public static void setD_cache_request_hits(int d_cache_request_hits) {
		WB.d_cache_request_hits = d_cache_request_hits;
	}


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
		if(write_issued) {
			try {
				writeCommand();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
	static int write_hack_counter = 0;
	static boolean write_issued = false;
	public static void writeCommand() throws IOException {
		write_issued=true;
		write_hack_counter++;
		if(write_hack_counter==20) {
			writeToFile();
		}
	}
	public static void writeToFile() throws IOException {
		Collections.sort(results,new Comparator<CycleMaintain>() {

			@Override
			public int compare(CycleMaintain o1, CycleMaintain o2) {
				
				return o1.getIF_end()-o2.getIF_end();
			}
		});
		FileWriter writer = new FileWriter(Simulator.result_file); 
		writer.write(TAB_SPACE+"Instruction"+TAB_SPACE+TAB_SPACE+TAB_SPACE+"FT"+TAB_SPACE+"ID"+TAB_SPACE+"EX"+TAB_SPACE+"WB"
				+TAB_SPACE+"RAW"+TAB_SPACE+"WAR"+TAB_SPACE+"WAW"+TAB_SPACE+"Struct"+System.lineSeparator());
		for(CycleMaintain stats: results) {
			writer.write(statsToString(stats) + System.lineSeparator());
		}
		writer.write(System.lineSeparator());
		writer.write("Total number of access requests for instruction cache: "+getI_cache_request_total()+System.lineSeparator());
		writer.write(System.lineSeparator());
		writer.write("Number of instruction cache hits:  "+getI_cache_request_hits()+System.lineSeparator());
		writer.write(System.lineSeparator());
		writer.write("Total number of access requests for data cache:  "+getD_cache_request_total()+System.lineSeparator());
		writer.write(System.lineSeparator());
		writer.write("Number of data cache hits: "+getD_cache_request_hits()+System.lineSeparator());
		
		System.out.println("****************File Writing Done*****************");
		writer.close();
	}
	private static String statsToString(CycleMaintain stats) {
		String raw_haz = stats.isRAW_hazard()?"Y":"N";
		String war_haz = stats.isWAR_hazard()?"Y":"N";
		String waw_haz = stats.isWAW_hazard()?"Y":"N";
		String struct_haz =stats.isStruct_hazard()?"Y":"N";
		String id_end = String.valueOf(stats.getID_end());
		String if_end =String.valueOf(stats.getIF_end()) ;
		String label =(stats.getLabel());

		if(if_end.equals("0")) {
			if_end = " ";
		}
		if(id_end.equals("0")) {
			id_end = " ";
		}
		String ex_end =String.valueOf(stats.getEX_end());
		if(ex_end.equals("0")) {
			ex_end = " ";
		}
		String wb_end =String.valueOf(stats.getWB_end());
		if(wb_end.equals("0")) {
			wb_end = " ";
		}	
		if(label==null) {
			label=" ";
		}else {
			label +=":";
		}
		String line = label +TAB_SPACE+ stats.getInstruction()+ getSpaces(stats.getInstruction())+TAB_SPACE+if_end+  TAB_SPACE+id_end + TAB_SPACE+ ex_end+ TAB_SPACE+
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
