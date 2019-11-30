package stage;

import java.util.ArrayList;
import java.util.List;

import entry.Simulator;
import functionalUnits.Adder;
import functionalUnits.Divider;
import functionalUnits.Multiplier;
import hazard.UsedRF;
import instructions.CycleMaintain;
import parser.InstParser;

public class ID {
	static boolean busy;
	static int src1=0;
	static int src2=0;
	static int dest=0;
	static int cycle_count=1;
	public static String inst_name;
	public static CycleMaintain cycle_stats;
	public static List<String> adder_ops=new ArrayList<String>();
	public static void setUpAdderOps() {
		adder_ops.add("ADD.D");
		adder_ops.add("SUB.D");
	}
	public static List<String> int_ops=new ArrayList<String>();
	public static void setUpIntOps() {
		int_ops.add("DADD");
		int_ops.add("DADDI");
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
		ID.cycle_stats = cycle_stats;
	}
	public static void setInst_name(String inst_name) {
		ID.inst_name = inst_name;
	}
	public static void setCycleCount(int cycle_count) {
		ID.cycle_count = cycle_count;
	}

	public static String instruction;

	public static String getInstruction() {
		return instruction;
	}
	public static void setInstruction(String instruction) {
		ID.instruction = instruction;
	}
	static int curr_cycle = 0;

	public static void burn_cycle() {
		curr_cycle++;

	}
	public static void nullifyInstructions() {
		if(curr_cycle>=cycle_count) {
			setRegistersBusy_hazard_check();
			curr_cycle=0;
			busy = false;
			setInst_name(null);
			setInstruction(null);
		}
	}
	public static int getCycleCount() {
		return cycle_count;
	}
	public static int getSrc1() {
		return src1;
	}
	public static int getSrc2() {
		return src2;
	}

	public static int getDest() {
		return dest;
	}

	public static boolean isBusy() {
		return busy;
	}

	public void setBusy(boolean busy) {
		this.busy = busy;
	}

	public static boolean decode(int cycle_num) {
		if(inst_name==null) {
			return false;
		}
		busy= true;
		if(!inst_name.equalsIgnoreCase("BNE")&& !inst_name.equalsIgnoreCase("J")
				&& !inst_name.equalsIgnoreCase("BEQ") && !inst_name.equalsIgnoreCase("HLT")) {
			getReadRegisters_3_args();
		}
		switch(inst_name) {

		case "HLT":
			IF.instructions.clear();
			System.out.println("HLT  "+  cycle_num);
			ID.setInst_name(null);
			ID.setInstruction(null);
			return true;
		case "L.D":
			//F1, 32(R2)
			//no need to set src and dest here - FPs
			burn_cycle();
			if(!checkHazards("Integer") && setIntegerUnit(cycle_num)) {
				//set instructions busy to check for hazards
				nullifyInstructions();
			}
			return true;
			//break;
		case "LW":

			fetchLoadDetails(instruction);
			burn_cycle();
			if(!checkHazards("Integer") && setIntegerUnit(cycle_num)) {
				nullifyInstructions();
			}
			return true;
		case "SW":
			fetchStoreDetails(instruction);
			burn_cycle();
			return true;
		case "S.D":

			burn_cycle();
			if(!checkHazards("Integer") && setIntegerUnit(cycle_num)) {
				nullifyInstructions();
			}
			return true;
		default:
			//3 args
			if(inst_name.equalsIgnoreCase("BNE")|| inst_name.equalsIgnoreCase("J")
					|| inst_name.equalsIgnoreCase("BEQ")) {
				handle_branch_instructions(cycle_num);
				return true;
			}
			decodeThreeArgsIntInstructions(instruction);
			burn_cycle();

			if( adder_ops.contains(inst_name) && 
					Adder.if_pipeline && !checkHazards("Adder")) {
				cycle_stats.setEntry_FU(cycle_num+1);
				cycle_stats.setID_end(cycle_num);
				if(Adder.insertInPipeline(cycle_stats)) {
					nullifyInstructions();
				}else {
					cycle_stats.setStruct_hazard(true);
				}
			}else if(adder_ops.contains(inst_name) &&
					!Adder.if_pipeline && !checkHazards("Adder")) {
				cycle_stats.setEntry_FU(cycle_num+1);
				cycle_stats.setID_end(cycle_num);
				if(Adder.insert_when_no_pipeline(cycle_stats)) {
					nullifyInstructions();
				}else {
					cycle_stats.setStruct_hazard(true);

				}
			}else if(inst_name.equals("MUL.D") && Multiplier.if_pipeline &&!checkHazards("Multiplier")) {
				cycle_stats.setEntry_FU(cycle_num+1);
				cycle_stats.setID_end(cycle_num);
				if(Multiplier.insertInPipeline(cycle_stats)) {
					nullifyInstructions();
				}else {
					cycle_stats.setStruct_hazard(true);

				}
			}else if(inst_name.equals("MUL.D") && !Multiplier.if_pipeline && !checkHazards("Multiplier")) {
				cycle_stats.setEntry_FU(cycle_num+1);
				cycle_stats.setID_end(cycle_num);
				if(Multiplier.insert_when_no_pipeline(cycle_stats)) {
					nullifyInstructions();
				}else {
					cycle_stats.setStruct_hazard(true);

				}
			}else if( inst_name.equals("DIV.D") && Divider.if_pipeline && !checkHazards("Divider")) {
				cycle_stats.setEntry_FU(cycle_num+1);
				cycle_stats.setID_end(cycle_num);
				if(Divider.insertInPipeline(cycle_stats)) {
					nullifyInstructions();
				}else {
					cycle_stats.setStruct_hazard(true);

				}
			}else if(inst_name.equals("DIV.D") && !Divider.if_pipeline&& !checkHazards("Divider")) {
				cycle_stats.setEntry_FU(cycle_num+1);
				cycle_stats.setID_end(cycle_num);
				if(Divider.insert_when_no_pipeline(cycle_stats)) {
					nullifyInstructions();
				}else {
					cycle_stats.setStruct_hazard(true);

				}
			}//DADDI, DADD, DSUB, DSUBI
			else if(int_ops.contains(inst_name)&&!checkHazards("Integer") && setIntegerUnit(cycle_num)) {
				nullifyInstructions();
			}
			return true;
		}
	}

	private static void handle_branch_instructions(int cycle_num) {
		String label = null;
		if(inst_name.equalsIgnoreCase("J")) {
			//J branch found
			label = instruction.trim().split(" ")[1].trim();
		}else if(inst_name.equalsIgnoreCase("BNE")||inst_name.equalsIgnoreCase("BEQ")) {
			label = instruction.trim().split(" ",2)[1].trim().split(",")[2];

		}
		if(label!=null && !label.isEmpty() && continue_loop(cycle_num)) {
			//flush fetch stage here
			IF.idx=InstParser.loop_map.get(label.trim());//starting loop point
			IF.setInstruction(null);
			IF.setInst_name(null);
			IF.setBranch_detected(true);
			//nullifyInstructions();
			burn_cycle();
			nullifyInstructions_branch(cycle_num);
		}
	}
	private static void nullifyInstructions_branch(int cycle_num) {
		if(curr_cycle>=cycle_count) {
			curr_cycle=0;
			busy = false;
			System.out.println(instruction+"\t"+cycle_num);
			setInst_name(null);
			setInstruction(null);
		}		
	}

	private static boolean continue_loop(int cycle_num) {
		String[] details = instruction.trim().split(" ",2)[1].split(",");
		String first_arg=details[0].trim();
		String second_arg = details[1].trim();
		if(!checkHazards_branch(first_arg) && !checkHazards_branch(second_arg)) {
			int reg_idx_1=Integer.parseInt(first_arg.substring(1));
			int reg_idx_2=Integer.parseInt(second_arg.substring(1));
			int reg1_data = memory.Register.getRegList().get(reg_idx_1);
			int reg2_data = memory.Register.getRegList().get(reg_idx_2);

			if(inst_name.equalsIgnoreCase("BNE")) {
				if(reg1_data==reg2_data) {
					burn_cycle();
					nullifyInstructions_branch(cycle_num);
					return false;
				}else {
					return true;
				}
			}else if(inst_name.equalsIgnoreCase("BEQ")) {
				if(reg1_data!=reg2_data) {
					burn_cycle();
					nullifyInstructions_branch(cycle_num);
					return false;
				}else {
					return true;
				}
			}else if(inst_name.equals("J")) {
				return true;
			}
		}
		return false;
	}

	private static boolean checkHazards_branch(String reg) {
		boolean if_hazard=false;
		if(UsedRF.checkIfBusy(reg)) {
			cycle_stats.setRAW_hazard(true);
			if_hazard=true;
		}
		/*if(functionalUnits.Integer.getInstruction()!=null
				&& functionalUnits.Integer.getInst_name()!=null) {
			cycle_stats.setStruct_hazard(true);
			if_hazard=true;
		}*/
		return if_hazard;
	}

	private static boolean checkHazards(String unit) {
		boolean if_hazard = false;
		//struct hazards
		if(unit.equals("Integer") && 
				functionalUnits.Integer.getInstruction()!=null
				&& functionalUnits.Integer.getInst_name()!=null) {
			if_hazard=true;
			cycle_stats.setStruct_hazard(true);
		}
		if(UsedRF.checkIfBusy(read_reg1) || UsedRF.checkIfBusy(read_reg2)) {
			cycle_stats.setRAW_hazard(true);
			if_hazard=true;
			//return true;
		} if(UsedRF.checkIfBusy(write_reg)) {
			cycle_stats.setWAW_hazard(true);
			if_hazard=true;
			//return true;
		}
		return if_hazard;
	}

	static String write_reg;
	static String read_reg1;
	static String read_reg2;

	private static void getReadRegisters_3_args() {
		String[] details = instruction.trim().split(" ",2);
		String[] rightPart = details[1].trim().split(",");
		write_reg=rightPart[0].trim();
		read_reg1=rightPart[1].trim();
		if(rightPart.length>2) {
			read_reg2=rightPart[2].trim();
		}
	}

	private static void setRegistersBusy_hazard_check() {
		String write_register = getWriteRegister();
		UsedRF.setBusy(write_register);
	}

	private static String getWriteRegister() {
		String[] details = instruction.trim().split(" ",2);
		String[] rightPart = details[1].trim().split(",");
		String write_register = rightPart[0].trim();
		return write_register;
	}

	private static boolean setIntegerUnit(int cycle_num) {
		if( functionalUnits.Integer.getInst_name()==null && 
				functionalUnits.Integer.getInstruction()==null) {
			functionalUnits.Integer.setInstruction(instruction);
			functionalUnits.Integer.setInst_name(inst_name);
			//Execute.setInst_name(inst_name);
			//Execute.setInstruction(instruction);
			cycle_stats.setID_end(cycle_num);
			functionalUnits.Integer.setCycle_stats(cycle_stats);
			return true;
		}
		return false;
	}
	private static void decodeThreeArgsIntInstructions(String inst) {
		String[] details =inst.split(" ",2);
		String[] args = details[1].trim().split(",");
		dest = Integer.parseInt(args[0].trim().substring(1));
		List<Integer> registers = memory.Register.getRegList();

		src1 = registers.get(Integer.parseInt(args[1].trim().substring(1)));
		if(args[2].trim().contains("R") || args[2].trim().contains("F")) {
			src2 = registers.get(Integer.parseInt(args[2].trim().substring(1)));
		}else {
			src2 = (Integer.parseInt(args[2].trim()));

		}
	}

	private static void fetchStoreDetails(String inst) {
		//SW R3, 16(R4)
		//Mem[16+regs[R4]]<-Regs[R3]
		String[] details =inst.split(" ",2);
		String[] sourceAndDest = details[1].trim().split(",");
		int reg_beg = sourceAndDest[1].trim().indexOf("(");
		int offset = Integer.parseInt(sourceAndDest[1].trim().substring(0,reg_beg));
		String source_reg = sourceAndDest[1].trim().substring(reg_beg+1,sourceAndDest[1].trim().length()-1);
		List<Integer> registers = memory.Register.getRegList();
		List<Integer> memData = memory.DataMemory.getDataList();

		dest = registers.get(Integer.parseInt(source_reg.substring(1)))+offset;
		src1 = registers.get(Integer.parseInt(sourceAndDest[0].substring(1)));
	}

	private static void fetchLoadDetails(String instr) {
		//LW R1, 32(R2)
		//Regs[R1]<- Mem[32+Regs[R2]]
		String[] details =instr.split(" ",2);
		String[] sourceAndDest = details[1].trim().split(",");
		int reg_beg = sourceAndDest[1].trim().indexOf("(");
		int offset = Integer.parseInt(sourceAndDest[1].trim().substring(0,reg_beg));
		String source_reg = sourceAndDest[1].trim().substring(reg_beg+1,sourceAndDest[1].trim().length()-1);
		List<Integer> registers = memory.Register.getRegList();
		List<Integer> memData = memory.DataMemory.getDataList();
		//src1 = memData.get(registers.get(Integer.parseInt(source_reg.substring(1))))+offset;
		src1 = memData.get(registers.get(Integer.parseInt(source_reg.substring(1)))+offset - 256) ;

		dest = Integer.parseInt(sourceAndDest[0].substring(1));
	}
}
