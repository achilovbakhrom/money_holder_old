package com.jim.pocketaccounter.utils;

/**
 * Created by root on 10/2/16.
 */

public class TemplateSms {
    private String regex;
    private int type;

    public TemplateSms(String regex, int type) {
        this.regex = regex;
        this.type = type;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
