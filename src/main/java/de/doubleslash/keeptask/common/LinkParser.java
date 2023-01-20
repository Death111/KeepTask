package de.doubleslash.keeptask.common;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkParser {

    // Pattern for recognizing a URL, based off RFC 3986 https://stackoverflow.com/a/5713866
    private static final Pattern urlPattern = Pattern.compile(
            "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                    + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                    + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*]*)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    public List<TodoPart> splitAtLinks(String todo) {
        ArrayList<TodoPart> list = new ArrayList<>();

        Matcher matcher = urlPattern.matcher(todo);

        int lastIndex = 0;

        while (matcher.find()) {
            int matchStart = matcher.start(1);
            int matchEnd = matcher.end();

            if (matchStart != lastIndex) {
                String extractNormalText = todo.substring(lastIndex, matchStart);
                list.add(new TodoPart(extractNormalText, false));
            }

            String extractedLink = todo.substring(matchStart, matchEnd);
            list.add(new TodoPart(extractedLink, true));
            lastIndex = matchEnd;
        }

        if (lastIndex != todo.length()) {
            String extractNormalText = todo.substring(lastIndex);
            list.add(new TodoPart(extractNormalText, false));
        }

        return list;
    }
}
