package entry;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import functionalUnits.Adder;
import functionalUnits.Divider;
import functionalUnits.FunctionalUnit;
import functionalUnits.MemoryUnit;
import functionalUnits.Multiplier;
import instructions.LW;
import parser.ConfigParser;
import parser.DataParser;
import parser.InstParser;
import parser.RegParser;
import stage.Execute;
import stage.ID;
import stage.IF;
import stage.WB;

public class Simulator {
	public static List<String> instructions;
	public static void main(String[] args) throws IOException {
		//PARSE THE FILES
		String inst_file = args[0];
		String config_file = args[1];
		String data_file = args[2];
		String reg_file = args[3];
		System.out.println("********PARSING INSTRUCTIONS****************");
		InstParser.parse(inst_file);
		System.out.println("********PARSING CONFIG****************");
		ConfigParser.parse(config_file);
		System.out.println("********PARSING MEMORY DATA****************");
		DataParser.parse(data_file);
		System.out.println("********PARSING REGISTER DATA****************");
		RegParser.parse(reg_file);

		InstParser instP = new InstParser();
		instructions = instP.getInstructionList();

		ConfigParser confP = new ConfigParser();
		HashMap<String, FunctionalUnit> conf_map = confP.getConfig_map();

		DataParser dataP = new DataParser();
		List<Integer> memory_data = dataP.getDataList();

		RegParser regP = new RegParser();
		List<Integer> register_data = regP.getRegList();
		System.out.println("****PARSING COMPLETE******");

		int cycle = 1;

		String inst_name = null;
		String instruction = null;
		MemoryUnit.setIntOps();
		ID.setUpAdderOps();
		ID.setUpIntOps();
		IF.setInstruction(instructions.get(0));
		while(cycle<1000) {
			//WB.writeBack();
			WB.writeBack(cycle);
			Divider.execute(cycle);
			Multiplier.execute(cycle);
			Adder.execute(cycle);
			MemoryUnit.execute(cycle);
			functionalUnits.Integer.executeInstruction();
			//Execute.execute();
			ID.decode(cycle);
			
			IF.fetchInstruction(cycle);
			cycle++;
		}
	}

}
