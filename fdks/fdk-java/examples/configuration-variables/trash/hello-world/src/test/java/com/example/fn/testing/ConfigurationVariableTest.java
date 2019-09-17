package com.example.fn.testing;

import com.example.fn.ConfigurationVariable;
import org.junit.Test;


import static junit.framework.TestCase.assertEquals;

public class ConfigurationVariableTest {
    private ConfigurationVariable confVar = new ConfigurationVariable();

    @Test
    public void getUserNameDefaultUser() {
        assertEquals("Anonymous", confVar.getUserName());
    }

   /* @Test
    public void reverseOfSingleCharacter() {
        assertEquals("a", reverse("a"));
    }

    @Test
    public void reverseHelloIsOlleh() {
        assertEquals("olleh", reverse("hello"));
    }

    private String reverse(String str) {
        return stringReverse.reverse(str);
    }*/
    
}
