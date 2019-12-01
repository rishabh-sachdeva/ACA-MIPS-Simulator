package cache;

import java.util.ArrayList;
import java.util.List;

import functionalUnits.MemoryUnit;

public class ICache {
	public static int k;
	public static final int block_size=4;
	public static final int block_num=4;
	public static int cycle_count;
	public static List<List<String>> i_cache;
	static int curr_cycle = 0;

	public static void burn_cycle() {
		curr_cycle++;

	}
	public static void setUpCache() {
		cycle_count=2*(k+MemoryUnit.cycle_count);
		i_cache= new ArrayList<List<String>>();
	}
	public static List<List<String>> getI_cache() {
		return i_cache;
	}

	public static void setI_cache(List<List<String>> i_cache) {
		ICache.i_cache = i_cache;
	}

	public static int getK() {
		return k;
	}

	public static void setK(int k) {
		ICache.k = k;
	}
	public static boolean search_cache(String instruction) {
		//burn_cycle();
		for(List<String> row : i_cache) {
			if(row.contains(instruction)) {
				return true;
			}
		}
		return false;
	}
	static int index_replace = 0;
	public static boolean fill_cache(List<String> inst_list,int idx) {
		burn_cycle();
		/*if(idx==1) {
			//hack to get first instruction
			idx = 0;
		}*/
		if(curr_cycle>=cycle_count) {
			List<String> new_row = new ArrayList<>();
			for(int i=idx; i<inst_list.size() && i<=idx+block_size-1;i++) {
				new_row.add(inst_list.get(i));
			}
			if(i_cache.size()<=block_num) {
				i_cache.add(new_row);
			}else {
				if(index_replace>=block_num) {
					index_replace=0;
				}
				i_cache.set(index_replace, new_row);//LRU strategy
				index_replace++;
			}
			curr_cycle=0;
			return true;
		}
		return false;
	}
	public static boolean handleKcycleRequiremnt() {
		burn_cycle();
		if(curr_cycle>=k) {
			curr_cycle=0;
			return true;
		}
		return false;
	}
}
