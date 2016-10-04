package com.jim.pocketaccounter.utils;

/**
 * Created by root on 10/2/16.
 */

public class TemplateSms {
    private String regex;
    private int type;
    private int posAmountGroup;


    private int posAmountGroupSecond;

    public TemplateSms(String regex, int type, int posAmountGroup) {
        this.regex = regex;
        this.type = type;
        this.posAmountGroup = posAmountGroup;
    }
    public int getPosAmountGroupSecond() {
        return posAmountGroupSecond;
    }

    public void setPosAmountGroupSecond(int posAmountGroupSecond) {
        this.posAmountGroupSecond = posAmountGroupSecond;
    }

    public int getPosAmountGroup() {
        return posAmountGroup;
    }

    public void setPosAmountGroup(int posAmountGroup) {
        this.posAmountGroup = posAmountGroup;
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
