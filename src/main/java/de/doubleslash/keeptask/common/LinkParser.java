package de.doubleslash.keeptask.common;

import de.doubleslash.keeptask.model.TodoPart;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkParser {

  String urlRegex = "https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
  String markdownRegex = "\\[([^\\]]+)\\]\\((" + urlRegex + ")\\)";
  Pattern linkOnlyPattern = Pattern.compile("^" + urlRegex);
  Pattern markdownPattern = Pattern.compile("^" + markdownRegex);
  Pattern untilNextWordPattern = Pattern.compile("^(\\S*\\s+)\\S");

  public List<TodoPart> splitAtLinks(String input) {
    List<TodoPart> list = new ArrayList<>();

    String restOfTodo = input;
    do {
      TodoPart mdLink = getMarkDownLink(restOfTodo);
      if (mdLink != null) {
        list.add(mdLink);
        restOfTodo = restOfTodo.replaceAll("^" + markdownRegex, "");
        continue;
      }

      TodoPart link = getLink(restOfTodo);
      if (link != null) {
        list.add(link);
        restOfTodo = restOfTodo.replaceAll("^" + urlRegex, "");
        continue;
      }

      Matcher matcher = untilNextWordPattern.matcher(restOfTodo);
      if (matcher.find()) {
        String group1 = matcher.group(1);
        list.add(new TodoPart(group1));
        restOfTodo = restOfTodo.substring(group1.length());
      } else {
        list.add(new TodoPart(restOfTodo));
        restOfTodo = "";
      }

    } while (restOfTodo.length() != 0);

    List<TodoPart> newList = combineNonLinkPartsToOne(list);

    return newList;
  }

  private static List<TodoPart> combineNonLinkPartsToOne(List<TodoPart> list) {
    List<TodoPart> newList = new ArrayList<>();
    String tmpString = "";
    for (int i = 0; i < list.size(); i++) {
      TodoPart todoPart = list.get(i);
      if (!(todoPart).isLink()) {
        tmpString += todoPart.getStringValue();
      } else {
        if (!tmpString.isEmpty()) {
          newList.add(new TodoPart(tmpString));
          tmpString = "";
        }
        newList.add(todoPart);
      }
    }
    if (!tmpString.isEmpty()) {
      newList.add(new TodoPart(tmpString));
    }
    return newList;
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
