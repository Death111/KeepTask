package de.doubleslash.keeptask.controller;

import de.doubleslash.keeptask.common.LinkParser;
import de.doubleslash.keeptask.common.TodoPart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class LinkParserTest {


    private LinkParser linkParser;

    @BeforeEach
    void beforeTest() {
        linkParser = new LinkParser();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Hello world!", "http no link"})
    void shouldFindNoLinksWhenNoLinksExist(String todo) {
        List<TodoPart> split = linkParser.splitAtLinks(todo);

        assertThat(split).hasSize(1);
        assertEquals(split.get(0), new TodoPart(todo, false));
    }

    @Test
    void shouldFindOneLinkWhenOneLinksExist() {
        String todo = "Hello https://www.google.de how are you?";
        List<TodoPart> split = linkParser.splitAtLinks(todo);
        assertAll(
                () -> assertThat(split).hasSize(3),
                () -> assertEquals(split.get(0), new TodoPart("Hello ", false)),
                () -> assertEquals(split.get(1), new TodoPart("https://www.google.de", true)),
                () -> assertEquals(split.get(2), new TodoPart(" how are you?", false))
        );
    }

    @Test
    void shouldFindTwoLinksWhenTwoLinksExist() {
        String todo = "Hello https://www.google.de how are you? Please check http://google.de!";
        List<TodoPart> split = linkParser.splitAtLinks(todo);
        assertAll(
                () ->  assertThat(split).hasSize(5),
                () -> assertEquals(split.get(0), new TodoPart("Hello ", false)),
                () -> assertEquals(split.get(1), new TodoPart("https://www.google.de", true)),
                () -> assertEquals(split.get(2), new TodoPart(" how are you? Please check ", false)),
                () -> assertEquals(split.get(3), new TodoPart("http://google.de", true)),
                () -> assertEquals(split.get(4), new TodoPart("!", false))
        );
    }

    private void assertEquals(TodoPart part1, TodoPart part2) {
        assertAll(
                () -> assertThat(part1.getStringValue()).isEqualTo(part2.getStringValue()),
                () -> assertThat(part1.isLink()).isEqualTo(part2.isLink())
        );
    }
}
