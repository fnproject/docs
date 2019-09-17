package com.example.fn;

public class ConfigurationVariable {
    public String getUserName() {
        String userName = System.getenv().getOrDefault("userName", "Anonymous");
    	//String userName = /*System.getenv("userName");*/System.getProperty("userName");
    	//System.getenv("userName")
    	return  userName;
        //return new StringBuilder(str).reverse().toString();
    }
    
    public String hello() {
    	return "Hello " + getUserName() + "!";
    }
}
