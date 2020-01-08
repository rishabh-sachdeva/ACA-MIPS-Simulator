package entry;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import cache.DCache;
import cache.ICache;
import functionalUnits.Adder;
import functionalUnits.Divider;
import functionalUnits.FunctionalUnit;
import functionalUnits.MemoryUnit;
import functionalUnits.Multiplier;
import parser.ConfigParser;
import parser.DataParser;
import parser.InstParser;
import parser.RegParser;
import stage.ID;
import stage.IF;
import stage.WB;

/**
 * ACA FINAL PROJECT _ MIPS SIMULATOR
 * 
 * @author Rishabh
 *
 */
public class Simulator {
	public static List<String> instructions;
	public static String result_file;
	public static void main(String[] args) throws IOException {
		//PARSE THE FILES
		if(args.length<5) {
			System.out.println("Please provide all the arguments");
			System.exit(0);
		}
		String inst_file = args[0];
		String data_file = args[1];
		String reg_file = args[2];
		String config_file = args[3];
		result_file = args[4];
		System.out.println("********PARSING INSTRUCTIONS****************");
		InstParser.parse(inst_file);

		System.out.println("********PARSING MEMORY DATA****************");
		DataParser.parse(data_file);
		System.out.println("********PARSING REGISTER DATA****************");
		RegParser.parse(reg_file);
		System.out.println("********PARSING CONFIG****************");
		ConfigParser.parse(config_file);
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
		IF.setInstruction(instructions.get(0).trim());
		ICache.setUpCache();
		DCache.setUpCacheSets();
		while(cycle<10000) {
			WB.writeBack(cycle);
			Divider.execute(cycle);
			Multiplier.execute(cycle);
			Adder.execute(cycle);
			MemoryUnit.execute(cycle);
			functionalUnits.Integer.executeInstruction();
			ID.decode(cycle);
			
			IF.fetchInstruction(cycle);
			cycle++;
		}
	}

}
