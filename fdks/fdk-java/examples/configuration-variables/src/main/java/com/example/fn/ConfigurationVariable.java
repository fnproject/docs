package com.example.fn;

public class ConfigurationVariable {
    public String getUserName() {
        String userName = System.getenv().getOrDefault("userName", "Anonymous");
    	return  userName;        
    }

    public String getCountry() {
        String country = System.getenv().getOrDefault("country", "Wold");
    	return  country;        
    }
    
    public String hello() {
    	return "Hello " + getUserName() + " from " + getCountry() + "!";
    }
}
