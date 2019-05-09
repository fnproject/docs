package com.example.fn.testing;

import com.example.fn.StringReverse;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class StringReverseTest {
    private StringReverse stringReverse = new StringReverse();

    @Test
    public void reverseEmptyString() {
        assertEquals("", reverse(""));
    }

    @Test
    public void reverseOfSingleCharacter() {
        assertEquals("a", reverse("a"));
    }

    @Test
    public void reverseHelloIsOlleh() {
        assertEquals("olleh", reverse("hello"));
    }

    private String reverse(String str) {
        return stringReverse.reverse(str);
    }
}
