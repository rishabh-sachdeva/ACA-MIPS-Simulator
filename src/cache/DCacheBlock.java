package cache;

import java.util.ArrayList;
import java.util.List;

public class DCacheBlock {
	public static class AddressAndData{
		int address;
		int data;
		public int getAddress() {
			return address;
		}
		public void setAddress(int address) {
			this.address = address;
		}
		public int getData() {
			return data;
		}
		public void setData(int data) {
			this.data = data;
		}
	}
	List<AddressAndData> word_list = new ArrayList<>();//size must be 4
	public List<AddressAndData> getWord_list() {
		return word_list;
	}
	public void setWord_list(List<AddressAndData> word_list) {
		this.word_list = word_list;
	}

}
