package parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstParser {

	public static List<String> instruction_list;
	public static Map<String,Integer> loop_map;
	public static void parse(String file_add) throws IOException {
		loop_map= new HashMap<String, Integer>();
		File file = new File(file_add);
		instruction_list = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(file)); 
		String line;
		int idx=0;
		while((line=br.readLine())!=null) {
			if(line.contains(":")) {
				//starting loop instruction
				String[] details = line.split(":");
				loop_map.put(details[0].trim(), idx);
				line = details[1].trim();
			}
			instruction_list.add(line.trim().toUpperCase());
			idx++;
		}
		br.close();
	}
	public List<String> getInstructionList() {
		return instruction_list;
	}

}
