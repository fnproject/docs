package com.example.fn;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Query {
    public final String regex;
    public final String text;

    @JsonCreator
    public Query(@JsonProperty("regex") String regex, @JsonProperty("text") String text) {
        this.regex = regex;
        this.text = text;
    }
}
