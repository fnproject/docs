package com.example.fn.testing;

import com.example.fn.GreetingCountry;
import org.junit.Test;


import static junit.framework.TestCase.assertEquals;

public class GreetingCountryTest {
    private GreetingCountry confVar = new GreetingCountry();

    @Test
    public void getUserNameDefaultValue() {
        assertEquals("Anonymous", confVar.getUserName());
    }
    @Test
    public void getCountryDetaultValue() {
    	assertEquals("World", confVar.getCountry());
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
