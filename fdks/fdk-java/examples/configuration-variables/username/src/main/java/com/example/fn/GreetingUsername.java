package com.example.fn;

public class GreetingUsername {
    public String getUserName() {
        String userName = System.getenv().getOrDefault("USERNAME", "Anonymous");
    	return  userName;        
    }

    
    
    public String hello() {
    	return "Hello " + getUserName() + "!";
    }
}
