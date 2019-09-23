package com.example.fn;

public class GreetingCountry {
    public String getUserName() {
        String userName = System.getenv().getOrDefault("USERNAME", "Anonymous");
    	return  userName;        
    }

    public String getCountry() {
        String country = System.getenv().getOrDefault("COUNTRY", "World");
    	return  country;        
    }
    
    public String hello() {
    	return "Hello " + getUserName() + " from " + getCountry() + "!";
    }
}
