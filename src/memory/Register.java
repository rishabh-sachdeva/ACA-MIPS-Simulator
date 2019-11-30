package memory;

import java.util.List;

public class Register {
	public static List<Integer> reg_list;

	public static List<Integer> getRegList() {
		return reg_list;
	}

	public static void setRegList(List<Integer> reg_list) {
		Register.reg_list = reg_list;
	}

}
