package parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import memory.DataMemory;

public class DataParser {

	public static List<Integer> data_list;
	public static void parse(String file_add) throws IOException {
		data_list = new ArrayList<Integer>();
		File file = new File(file_add);
		BufferedReader br = new BufferedReader(new FileReader(file)); 
		String line;
		while((line=br.readLine())!=null) {
			int decimal = Integer.parseInt(line,2);
			data_list.add(decimal);
			//System.out.println(line);
		}
		DataMemory.setDataList(data_list);
		br.close();
	}
	public List<Integer> getDataList() {
		return data_list;
	}
}
