package com.example.fn.testing;

import static junit.framework.TestCase.assertEquals;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.example.fn.Connection;
import com.fnproject.fn.testing.FnResult;
import com.fnproject.fn.testing.FnTestingRule;


public class ConnectionTest {
    
    @Rule
    public FnTestingRule fn;
    List<FnResult> results;   
    
    @Before
    public void setUp() {
    	fn = FnTestingRule.createDefault();	
    	fn.givenEvent().enqueue();    	
    	fn.thenRun(Connection.class, "config");
    }
    
    @Test
    public void getUrlDefaultValue() {	
    	fn.thenRun(Connection.class, "getUrl");  	
    	results = fn.getResults();
    	String url = results.get(1).getBodyAsString();
    	assertEquals("jdbc:oracle", url);
    }
    
    @Test
    public void getDriverDetaultValue() {
    	fn.thenRun(Connection.class, "getDriver");
    	results = fn.getResults();
    	String driver = results.get(1).getBodyAsString();
    	assertEquals("OracleDriver", driver);    	   
    }
    
    @Test
    public void getUsernameDefaultValue() {
    	fn.thenRun(Connection.class, "getUser");
    	results = fn.getResults();
    	String user = results.get(1).getBodyAsString();
    	assertEquals("admin", user);    	       	
    }
    
    @Test
    public void getPasswordDefaultValue() {
    	fn.thenRun(Connection.class, "getPassword");
    	results = fn.getResults();
    	String password = results.get(1).getBodyAsString();
    	assertEquals("admin", password);
    }
    
    

  
    
}
