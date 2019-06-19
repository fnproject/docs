package com.example.fn;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Response {
    public final String regex;
    public final String text;
    public final List<Match> matches;

    @JsonCreator
   public Response(@JsonProperty("regex") String regex, @JsonProperty("text") String text, @JsonProperty("matches") List<Match> matches) {
        this.regex = regex;
        this.text = text;
        this.matches = matches;
    }
}
