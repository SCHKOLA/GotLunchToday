package de.schkola.kitchenscanner.util;

import java.util.List;

public class StatsResult {

    private int lunchA;
    private int lunchB;
    private int lunchS;
    private int dispensedA;
    private int dispensedB;
    private int dispensedS;
    private List<String> toDispenseA;
    private List<String> toDispenseB;
    private List<String> toDispenseS;

    public int getLunchA() {
        return lunchA;
    }

    public void setLunchA(int lunchA) {
        this.lunchA = lunchA;
    }

    public int getLunchB() {
        return lunchB;
    }

    public void setLunchB(int lunchB) {
        this.lunchB = lunchB;
    }

    public int getLunchS() {
        return lunchS;
    }

    public void setLunchS(int lunchS) {
        this.lunchS = lunchS;
    }

    public List<String> getToDispenseA() {
        return toDispenseA;
    }

    public void setToDispenseA(List<String> toDispenseA) {
        this.toDispenseA = toDispenseA;
    }

    public List<String> getToDispenseB() {
        return toDispenseB;
    }

    public void setToDispenseB(List<String> toDispenseB) {
        this.toDispenseB = toDispenseB;
    }

    public List<String> getToDispenseS() {
        return toDispenseS;
    }

    public void setToDispenseS(List<String> toDispenseS) {
        this.toDispenseS = toDispenseS;
    }

    public int getDispensedA() {
        return dispensedA;
    }

    public void setDispensedA(int dispensedA) {
        this.dispensedA = dispensedA;
    }

    public int getDispensedB() {
        return dispensedB;
    }

    public void setDispensedB(int dispensedB) {
        this.dispensedB = dispensedB;
    }

    public int getDispensedS() {
        return dispensedS;
    }

    public void setDispensedS(int dispensedS) {
        this.dispensedS = dispensedS;
    }
}
