package com.example.fn;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Match {
    public final int start;
    public final int end;
    public final String match;

    @JsonCreator
    public Match(@JsonProperty("start") int start, @JsonProperty("end") int end, @JsonProperty("match") String match) {
        this.start = start;
        this.end = end;
        this.match = match;
    }
}
