package com.jim.pocketaccounter.utils;

/**
 * Created by root on 10/2/16.
 */

public class TemplateSms {
    private String regex;
    private int type;
    private String posAmountGroupName;

    public TemplateSms(String regex, int type, String posAmountGroup) {
        this.regex = regex;
        this.type = type;
        this.posAmountGroupName = posAmountGroup;
    }

    public String getPosAmountGroup() {
        return posAmountGroupName;
    }

    public void setPosAmountGroup(String posAmountGroup) {
        this.posAmountGroupName = posAmountGroup;
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
