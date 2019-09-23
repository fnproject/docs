package com.example.fn.testing;

import com.example.fn.GreetingUsername;
import org.junit.Test;


import static junit.framework.TestCase.assertEquals;

public class GreetingUsernameTest {
    private GreetingUsername confVar = new GreetingUsername();

    @Test
    public void getUserNameDefaultValue() {
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
