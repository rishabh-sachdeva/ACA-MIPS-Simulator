package parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import cache.DCache;
import cache.ICache;
import functionalUnits.Adder;
import functionalUnits.Divider;
import functionalUnits.FunctionalUnit;
import functionalUnits.MemoryUnit;
import functionalUnits.Multiplier;

public class ConfigParser {
	public static HashMap<String,FunctionalUnit> config_map;

	public static HashMap<String, FunctionalUnit> getConfig_map() {
		return config_map;
	}

	public static void parse(String file_add) throws IOException {

		File file = new File(file_add);
		BufferedReader br = new BufferedReader(new FileReader(file)); 
		String line;
		config_map = new HashMap<>();
		while((line=br.readLine())!=null) {

			String[] configuration = line.split(":");
			String[] cycleAndPipeLine = configuration[1].split(",");

			switch(configuration[0].trim().toLowerCase()) {
			case "fp adder":
				FunctionalUnit adder = new Adder(cycleAndPipeLine[1].trim().equals("yes")?true:false,
						Integer.parseInt(cycleAndPipeLine[0].trim()));
				config_map.put("adder",adder);
				Adder.cycle_count=Integer.parseInt(cycleAndPipeLine[0].trim());
				Adder.if_pipeline=cycleAndPipeLine[1].trim().equals("yes")?true:false;
				break;
			case "fp multiplier":
				FunctionalUnit mult = new Multiplier(cycleAndPipeLine[1].trim().equals("yes")?true:false,
						Integer.parseInt(cycleAndPipeLine[0].trim()));
				config_map.put("multiplier",mult);
				Multiplier.cycle_count=Integer.parseInt(cycleAndPipeLine[0].trim());
				Multiplier.if_pipeline=cycleAndPipeLine[1].trim().equals("yes")?true:false;
				break;
			case "fp divider":
				FunctionalUnit div = new Divider(cycleAndPipeLine[1].trim().equals("yes")?true:false,
						Integer.parseInt(cycleAndPipeLine[0].trim()));
				config_map.put("divider", div);
				Divider.if_pipeline=cycleAndPipeLine[1].trim().equals("yes")?true:false;
				Divider.cycle_count=Integer.parseInt(cycleAndPipeLine[0].trim());
				break;
			case "main memory":
				//			System.out.println("Main memory in config parser");
				MemoryUnit.cycle_count = Integer.parseInt(configuration[1].trim());
				break;
			case "i-cache":
				ICache.setK(Integer.parseInt(line.trim().split(":")[1].trim()));
				break;
			case "d-cache":
				DCache.setK(Integer.parseInt(line.trim().split(":")[1].trim()));
				break;
			}
		}
		br.close();
	}
}
