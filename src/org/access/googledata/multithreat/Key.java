package org.access.googledata.multithreat;

public class Key {
	
	/*
	 * 
	 * AIzaSyAAFXsoqBB01OnruUYmWZ6HhsE16UpLChI
	 * AIzaSyCpA6UguHRWNxHDMKQDc7Ff-iR0XCA2VTI
	 * AIzaSyCjaTxBRbNqya8EkGbbLZ04KlBhCIGV6cw
	 * AIzaSyDybcMp1TOtj8oUJucVrJ4d6RR3ne34WDw
	 * AIzaSyDCK8JXchtFijZLI4qE3n-UmejIBpzfXgk
	 * AIzaSyBsYWjm-x4q88OEhWObqzjBES2EiThFRPM
	 * AIzaSyCivn14ak9OYS-gECsSqK_NDPY2DQyFOPY
	 *
	 * */
	
	enum KEY_STATUS {
		OK{
			@Override
			public String toString() {
				// TODO Auto-generated method stub
				return "OK";
			}
		},
		OVER_QUERY{
			@Override
			public String toString() {
				// TODO Auto-generated method stub
				return "OVER_QUERY";
			}
		},
		INVALID_KEY{
			@Override
			public String toString() {
				// TODO Auto-generated method stub
				return "INVALID_KEY";
			}
		}
	}
	
	private final String key;
	private Integer usage_count;
	private KEY_STATUS stats = null;
	
	private boolean usablity=true;
	
	private final Integer max_usage = 1000;

	public Key(String key,Integer usage){
		this.key = key;
		this.usage_count = usage;
		this.stats = KEY_STATUS.OK;
	}
	
	public Key(String key,KEY_STATUS stats){
		this.key = key;
		this.usage_count = -1;
		this.stats= stats;
		usablity=false;
	}
	
	
	public Integer getUsageCount() {
		return usage_count;
	}

	public String getKey() {
		return key;
	}


	public Integer getMaxUsage() {
		return max_usage;
	}

	public boolean isUsable() {
		// TODO Auto-generated method stub
		return (usage_count<max_usage && usage_count>=0) && usablity && stats==KEY_STATUS.OK;
	}

	public void use() {
		// TODO Auto-generated method stub
		usage_count++;
		
	}
	
	public void setUnusable(){
		this.usablity=false;
	}
	
	public void setOverQuery(){
		this.stats=KEY_STATUS.OVER_QUERY;
		this.usablity=false;
	}
	
	public void setInvalid(){
		this.stats=KEY_STATUS.INVALID_KEY;
		this.setUnusable();
	}
	
	public KEY_STATUS getStats(){
		return stats;
	}
	
}
