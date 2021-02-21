package io.zingoworks.demospringbook.hello.message;

import org.springframework.stereotype.Component;

public class Message {

    private String text;

    private Message(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static Message newMessage(String text) {
        return new Message(text);
    }
}
