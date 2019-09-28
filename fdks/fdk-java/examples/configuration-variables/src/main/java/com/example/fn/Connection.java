package com.example.fn;

import com.fnproject.fn.api.FnConfiguration;
//import com.fnproject.fn.api.FnConfiguration;
import com.fnproject.fn.api.RuntimeContext;


public class Connection {
	
		
	private String url;
	private String driver;
	private String user;
	private String password;

	   	
    
	
    @FnConfiguration
    public void config(RuntimeContext ctx) {
    	//Set value at the application configuration level
    	url = ctx.getConfigurationByKey("DB_URL")
    			.orElse("jdbc:oracle");
    	//Set value in the func.yaml
    	driver = ctx.getConfigurationByKey("DB_DRIVER")
    			.orElse("OracleDriver");
    	//Set value at the application configuration level
    	user = ctx.getConfigurationByKey("DB_USER")
    			.orElse("admin");	
    }
    
    public String getUrl() {
    	return  url;
    }

    
    public String getDriver() {
    	return  driver;        
    }
    
	
    public String getUser() {
        return  user;        
    }
    
	//Set value at the function configuration level
    public String getPassword() {
        password = System.getenv().getOrDefault("DB_PASSWORD", "admin");
    	return  password;        
    }
    
    public String getConnection() {
    	return "driver: " + getDriver() + "; url: " + url + "; user: " + getUser() + "; password: " + getPassword();
    }
}
