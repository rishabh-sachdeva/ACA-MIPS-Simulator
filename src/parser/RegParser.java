package parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import memory.Register;

public class RegParser {

	public static List<Integer> reg_list;
	public static void parse(String file_add) throws IOException {
		reg_list = new ArrayList<Integer>();
		File file = new File(file_add);
		BufferedReader br = new BufferedReader(new FileReader(file)); 
		String line;
		while((line=br.readLine())!=null) {
			int decimal = Integer.parseInt(line,2);
			reg_list.add(decimal);
			//System.out.println(line);
		}
		Register.setRegList(reg_list);
		br.close();
	}
	public List<Integer> getRegList() {
		return reg_list;
	}
}
