package stage;

import java.util.List;

import functionalUnits.Integer;

//Execute Stage
public class Execute {
	/*static String instruction;
	static String inst_name;


	public static String getInst_name() {
		return inst_name;
	}

	public static void setInst_name(String inst_name) {
		Execute.inst_name = inst_name;
	}

	public static String getInstruction() {
		return instruction;
	}

	public static void setInstruction(String instruction) {
		Execute.instruction = instruction;
	}

	public static void execute() {
		//call Functional Unit first
		if(inst_name==null) {
			return;
		}
		switch(inst_name) {
		case "L.D":
			if(!Integer.isBusy()) {
				Integer.executeInstruction(inst_name);
			}
			//LW.execute(instruction);
			break;
		case "DADDI":
			if(!Integer.isBusy()) {
				Integer.executeInstruction(inst_name);
			}else {
				Integer.burn_cycle();
				Integer.setMemoryUnit();
				Integer.nullifyInstructions();
			}
			break;
		case "DADD":
			Integer.executeInstruction(inst_name);
			break;
		}
	}*/
}
