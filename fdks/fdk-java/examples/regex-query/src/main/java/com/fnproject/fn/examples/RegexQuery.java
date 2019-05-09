package com.fnproject.fn.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexQuery {
    public Response query(Query query) {
        return new Response(query.regex, query.text, getMatches(query));
    }

    private List<Match> getMatches(Query query) {
        Pattern pattern = Pattern.compile(query.regex);
        Matcher matcher = pattern.matcher(query.text);
        List<Match> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(new Match(matcher.start(), matcher.end(), query.text.substring(matcher.start(), matcher.end())));
        }
        return matches;
    }
}
