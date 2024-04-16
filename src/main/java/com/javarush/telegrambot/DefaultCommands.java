package com.javarush.telegrambot;

public enum DefaultCommands {
    START("/start"),
    BYE("/bye"),
    HELP("/help");

    private final String code;

    DefaultCommands(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}