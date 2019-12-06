package cache;

import java.util.ArrayList;
import java.util.List;

import functionalUnits.MemoryUnit;
import stage.WB;

public class ICache {
	public static int k;
	public static final int block_size=4;
	public static final int block_num=4;
	public static int cycle_count;
	public static List<List<Integer>> i_cache;
	static int curr_cycle = 0;

	public static void burn_cycle() {
		curr_cycle++;

	}
	public static void setUpCache() {
		cycle_count=2*(k+MemoryUnit.cycle_count);
		i_cache= new ArrayList<List<Integer>>();
	}
	public static List<List<Integer>> getI_cache() {
		return i_cache;
	}

	public static void setI_cache(List<List<Integer>> i_cache) {
		ICache.i_cache = i_cache;
	}

	public static int getK() {
		return k;
	}

	public static void setK(int k) {
		ICache.k = k;
	}
	static int prev_inst_searched = -1;
	static int prev_inst_searched_overall = -1;
	public static boolean search_cache(int instruction) {//index cache
		//burn_cycle();
		if(prev_inst_searched_overall==-1 || !(instruction==(prev_inst_searched_overall))) {
			WB.setI_cache_request_total(WB.getI_cache_request_total()+1);
			
		}
		prev_inst_searched_overall=instruction;
		for(List<Integer> row : i_cache) {
			if(checkIfExistInCache(row,instruction)) {
				if(prev_inst_searched==-1 || !(instruction == (prev_inst_searched))) {
					WB.setI_cache_request_hits(WB.getI_cache_request_hits()+1);
				}
				prev_inst_searched = instruction;
				return true;
			}
		}
		return false;
	}
	private static boolean checkIfExistInCache(List<Integer> row, int instruction) {
		for(int ele : row) {
			if(ele==instruction) {
				return true;
			}
		}
		return false;
	}
	static int index_replace = 0;
	static boolean marked_by_me = false;
	public static boolean fill_cache(List<String> inst_list,int idx) {
		if(!MemoryUnit.isCache_busy()) {
			//occupy memory unit
			MemoryUnit.setCache_busy(true);
		
			marked_by_me=true;
		}
		if(marked_by_me) {
			//hack to incorporate memory bus contention
			burn_cycle();
		}

		if(curr_cycle>=cycle_count) {
			MemoryUnit.setCache_busy(false);
			marked_by_me=false;
			List<Integer> new_row = new ArrayList<>();
			for(int i=idx; i<inst_list.size() && i<=idx+block_size-1;i++) {
				new_row.add(i);
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
