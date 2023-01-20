package de.doubleslash.keeptask.common;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkParser {

    // Regular expression to match URLs
    String urlRegex = "https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    Pattern pattern = Pattern.compile(urlRegex);

    public List<TodoPart> splitAtLinks(String input) {
        ArrayList<TodoPart> list = new ArrayList<>();


        Matcher matcher = pattern.matcher(input);

        // Find all URLs in the input string
        int lastEnd = 0;
        while (matcher.find()) {
            // Append the normal text before the link
            list.add(new TodoPart(input.substring(lastEnd, matcher.start()), false));
            // link part
            list.add(new TodoPart(matcher.group(), true));
            lastEnd = matcher.end();
        }

        if (lastEnd != input.length()) {
            list.add(new TodoPart(input.substring(lastEnd), false));
        }
        return list;
    }
}
