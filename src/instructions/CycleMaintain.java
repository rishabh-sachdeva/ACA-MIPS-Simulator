package instructions;

public class CycleMaintain {
	int IF_end;
	int ID_end;
	int EX_end;
	int WB_end;
	int final_src=-1;
	int entry_FU = -1;
	boolean struct_hazard=false;
	String label = null;
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public boolean isStruct_hazard() {
		return struct_hazard;
	}
	public void setStruct_hazard(boolean struct_hazard) {
		this.struct_hazard = struct_hazard;
	}
	boolean WAR_hazard=false;
	public boolean isWAR_hazard() {
		return WAR_hazard;
	}
	public void setWAR_hazard(boolean wAR_hazard) {
		WAR_hazard = wAR_hazard;
	}
	boolean WAW_hazard=false;
	public boolean isWAW_hazard() {
		return WAW_hazard;
	}
	public void setWAW_hazard(boolean wAW_hazard) {
		WAW_hazard = wAW_hazard;
	}
	boolean RAW_hazard=false;
	
	public boolean isRAW_hazard() {
		return RAW_hazard;
	}
	public void setRAW_hazard(boolean rAW_hazard) {
		RAW_hazard = rAW_hazard;
	}
	public int getEntry_FU() {
		return entry_FU;
	}
	public void setEntry_FU(int entry_FU) {
		this.entry_FU = entry_FU;
	}
	String instruction;
	String inst_name;
	
	public String getInst_name() {
		return inst_name;
	}
	public void setInst_name(String inst_name) {
		this.inst_name = inst_name;
	}
	public String getInstruction() {
		return instruction;
	}
	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}
	public int getFinal_src() {
		return final_src;
	}
	public void setFinal_src(int final_src) {
		this.final_src = final_src;
	}
	public int getFinal_dest() {
		return final_dest;
	}
	public void setFinal_dest(int final_dest) {
		this.final_dest = final_dest;
	}
	int final_dest=-1;
	
	public int getIF_end() {
		return IF_end;
	}
	public void setIF_end(int iF_end) {
		IF_end = iF_end;
	}
	public int getID_end() {
		return ID_end;
	}
	public void setID_end(int iD_end) {
		ID_end = iD_end;
	}
	public int getEX_end() {
		return EX_end;
	}
	public void setEX_end(int eX_end) {
		EX_end = eX_end;
	}
	public int getWB_end() {
		return WB_end;
	}
	public void setWB_end(int wB_end) {
		WB_end = wB_end;
	}

}
