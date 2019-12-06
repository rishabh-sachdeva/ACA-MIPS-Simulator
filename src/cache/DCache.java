package cache;

import java.util.ArrayList;
import java.util.List;

import cache.DCacheBlock.AddressAndData;
import functionalUnits.MemoryUnit;
import stage.WB;

public class DCache {
	public static int k; //d-cache from config
	public static int T; //main-memory from config
	public static int getT() {
		return T;
	}
	public static void setT(int t) {
		T = t;
	}
	public static int num_sets=2;
	public static int block_per_set=2;
	static int mru_set0 = 1; // 1 means block 1 and 2 means block 2.
	static int mru_set1 = 1;
	public static int Miss_time;
	public static int Hit_time;
	private static final int OFFSET=256;
	public static int getMiss_time() {
		return Miss_time;
	}

	public static int getHit_time(String instruction) {
		if(instruction.endsWith(".D")) {
			return 2*k;
		}else {
			return k;
		}
	}
	public static int getK() {
		return k;
	}
	public static void setK(int k) {
		DCache.k = k;
	}
	static List<DCacheSet> cache_set; //= new ArrayList<>();// this should have 2 sets 
	
	public static void setUpCacheSets() {
		cache_set = new ArrayList<>();
		DCacheSet set0 = new DCacheSet();
		DCacheSet set1 = new DCacheSet();
		cache_set.add( set0);
		cache_set.add( set1);
		setT(MemoryUnit.cycle_count);
		Miss_time = 2 * (T+k);
	}
	
	public static int findFirstEntry(int add){
		int first_add = (add/4 - (add/4)%4) *4;
		return first_add;
	}
	@SuppressWarnings("static-access")
	public static int searchInCache(int add) {
		 int set01 = findRightSet(add);//0 or 1th set
		 WB.setD_cache_request_total(WB.getD_cache_request_total()+1);
		 if(cache_set.get(set01).getBlock1()!=null && 
				 //!cache_set.get(set01).getBlock1().ifDirty && 
				 findIfExist(cache_set.get(set01).getBlock1().getWord_list(),add)) {
			 set_lru_block(set01,1);
			 WB.setD_cache_request_hits(WB.getD_cache_request_hits()+1);
			return memory.DataMemory.getDataList().get(add-OFFSET);
		 }else if(cache_set.get(set01).getBlock2()!=null && 
				 //!cache_set.get(set01).getBlock2().ifDirty && 
				 findIfExist(cache_set.get(set01).getBlock2().getWord_list(),add)) {
			 set_lru_block(set01,2);
			 WB.setD_cache_request_hits(WB.getD_cache_request_hits()+1);
			 return memory.DataMemory.getDataList().get(add-OFFSET);
		 }else {
			 insertInCache(add);
			 return -1;
		 }
	}
	@SuppressWarnings("static-access")
	public static int searchInCache_SD(int add) {
		 int set01 = findRightSet(add);//0 or 1th set
		 WB.setD_cache_request_total(WB.getD_cache_request_total()+1);
		 if(cache_set.get(set01).getBlock1()!=null && 
				 //!cache_set.get(set01).getBlock1().ifDirty && 
				 findIfExist(cache_set.get(set01).getBlock1().getWord_list(),add)) {
			 set_lru_block(set01,1);
			 WB.setD_cache_request_hits(WB.getD_cache_request_hits()+1);
			return memory.DataMemory.getDataList().get(add-OFFSET);
		 }else if(cache_set.get(set01).getBlock2()!=null && 
				 //!cache_set.get(set01).getBlock1().ifDirty && 
				 findIfExist(cache_set.get(set01).getBlock2().getWord_list(),add)) {
			 set_lru_block(set01,2);
			 WB.setD_cache_request_hits(WB.getD_cache_request_hits()+1);
			 return memory.DataMemory.getDataList().get(add-OFFSET);
		 }else {
			 insertInCache_SD(add);
			 return -1;
		 }
	}
	private static boolean findIfExist(List<AddressAndData> word_list, int add) {
		for(AddressAndData obj : word_list) {
			if(obj.getAddress()==add) {
				return true;
				//return memory.DataMemory.getDataList().get(add);
			}
		}
		return false;
	}
	private static void set_lru_block(int set_num, int block_in_set) {
		if(set_num==0) {
			mru_set0 = block_in_set;
		}else {
			mru_set1 = block_in_set;
		}
	}
	public static void insertInCache_SD(int address) {
		int set01 = findRightSet(address);//0 or 1th set
		insert(set01,address,true);//dirty it in SD 
	}
	
	public static void insertInCache(int address) {
		int set01 = findRightSet(address);//0 or 1th set
		insert(set01,address,false);//clean it in LD 
	}
	
	@SuppressWarnings("static-access")
	private static void insert(int set01, int address, boolean dirty_flag) {
		if(cache_set.get(set01).getBlock1()==null) {
			DCacheBlock block1 = createNewBlock(address);
			block1.setIfDirty(dirty_flag);//clean it in LD 
			cache_set.get(set01).setBlock1(block1);
		}else if(cache_set.get(set01).getBlock2()==null) {
			DCacheBlock block2 = createNewBlock(address);
			block2.setIfDirty(dirty_flag);//clean it in LD 
			cache_set.get(set01).setBlock2(block2);
		}else {
			//LRU strategy
			oveerride_least_recent_block(set01,address);
		}
	}
	@SuppressWarnings("static-access")
	private static void oveerride_least_recent_block(int set01,int address) {
		DCacheBlock block= createNewBlock(address);
		if(set01==0) {
			if(mru_set0==1) {
				//recently used is block one, so override 2
				cache_set.get(set01).setBlock2(block);
			}else if(mru_set0==2){
				//2 is most recent
				cache_set.get(set01).setBlock1(block);
			}
		}else {
			//set 1
			if(mru_set1==1) {
				//recently used is block one, so override 2
				cache_set.get(set01).setBlock2(block);
			}else if(mru_set1==2){
				//2 is most recent
				cache_set.get(set01).setBlock1(block);
			}
		}
	}
	private static DCacheBlock createNewBlock(int address) {
		DCacheBlock block = new DCacheBlock();
		List<AddressAndData> word_list = new ArrayList<>();
		AddressAndData add_data1 = new AddressAndData();
		add_data1.setAddress(address);
		add_data1.setData(memory.DataMemory.getDataList().get(address-OFFSET));
		AddressAndData add_data2 = new AddressAndData();

		add_data2.setAddress(address+4);
		add_data2.setData(memory.DataMemory.getDataList().get(address+4-OFFSET));
		AddressAndData add_data3 = new AddressAndData();

		add_data3.setAddress(address+8);
		add_data3.setData(memory.DataMemory.getDataList().get(address+8-OFFSET));
		AddressAndData add_data4 = new AddressAndData();

		add_data4.setAddress(address+12);
		add_data4.setData(memory.DataMemory.getDataList().get(address+12-OFFSET));
		word_list.add(add_data1);
		word_list.add(add_data2);
		word_list.add(add_data3);
		word_list.add(add_data4);
		block.setWord_list(word_list);
		return block;
	}
	private static int findRightSet(int word_address) {
		int block_address = word_address/block_per_set;
		int set_num = block_address%num_sets;
		return set_num;
	}
	
}
