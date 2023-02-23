package de.doubleslash.keeptask.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LinkParser {

    String urlRegex = "https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    String markdownRegex = "\\[([\\w\\s\\d]+)\\]\\((" + urlRegex + ")\\)";
    Pattern linkOnlyPattern = Pattern.compile(urlRegex);
    Pattern markdownPattern = Pattern.compile(markdownRegex);

    public List<TodoPart> splitAtLinks(String input) {
        List<TodoPart> list = new ArrayList<>();

        String[] split = input.split("\\s");

        String tmp = "";
        for (String part : split) {
            TodoPart mdLink = getMarkDownLink(part);
            if (mdLink != null) {
                if(!tmp.isEmpty()){
                    list.add(new TodoPart(tmp));
                    tmp = "";
                }
                list.add(mdLink);
                continue;
            }

            TodoPart link = getLink(part);
            if (link != null) {
                if(!tmp.isEmpty()){
                    list.add(new TodoPart(tmp));
                    tmp = "";
                }
                list.add(link);
                continue;
            }

            tmp += part + " ";
        }

        if(!tmp.isBlank()){
            list.add(new TodoPart(tmp));
        }

        return list;
    }

    private TodoPart getMarkDownLink(String inputString) {
        Matcher matcher = markdownPattern.matcher(inputString);
        if (matcher.find()) {
            String group1 = matcher.group(1);
            String group2 = matcher.group(2);
            return new TodoPart(group1, group2);
        }
        return null;
    }

    private TodoPart getLink(String inputString) {
        Matcher matcher = linkOnlyPattern.matcher(inputString);
        if (matcher.find()) {
            return new TodoPart(matcher.group(), matcher.group());
        }
        return null;
    }
}
