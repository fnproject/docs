package com.example.fn.testing;

import static junit.framework.TestCase.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import com.example.fn.Connection;
import com.fnproject.fn.testing.FnTestingRule;

public class ConnectionTest {
    private Connection conn = new Connection();

    @Rule
    public final FnTestingRule fn = FnTestingRule.createDefault();
    
    @Test
    public void getUrlDefaultValue() {
    	fn.setConfig("DB_URL", "jdbc:oracle");
    	assertEquals("jdbc:oracle", conn.getUrl());
    }
    
    @Test
    public void getDriverDetaultValue() {
    	assertEquals("OracleDriver", conn.getDriver());    	
    }
    
    @Test
    public void getUsernameDefaultValue() {
    	fn.setConfig("DB_USER", "admin");
    	assertEquals("admin", conn.getUser());
    }
    
    @Test
    public void getPasswordDefaultValue() {
    	fn.setConfig("DB_PASSWORD", "admin");    	
    	assertEquals("admin", conn.getPassword());
    }
    
    

  
    
}
