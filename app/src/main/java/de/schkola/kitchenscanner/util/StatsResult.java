package de.schkola.kitchenscanner.util;

import java.util.ArrayList;

public class StatsResult {

    private int lunchA;
    private int lunchB;
    private int lunchS;
    private int dispensedA;
    private int dispensedB;
    private int dispensedS;
    private ArrayList<String> toDispenseA;
    private ArrayList<String> toDispenseB;
    private ArrayList<String> toDispenseS;

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

    public ArrayList<String> getToDispenseA() {
        return toDispenseA;
    }

    public void setToDispenseA(ArrayList<String> toDispenseA) {
        this.toDispenseA = toDispenseA;
    }

    public ArrayList<String> getToDispenseB() {
        return toDispenseB;
    }

    public void setToDispenseB(ArrayList<String> toDispenseB) {
        this.toDispenseB = toDispenseB;
    }

    public ArrayList<String> getToDispenseS() {
        return toDispenseS;
    }

    public void setToDispenseS(ArrayList<String> toDispenseS) {
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
