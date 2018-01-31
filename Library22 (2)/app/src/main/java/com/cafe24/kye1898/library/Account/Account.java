package com.cafe24.kye1898.library.Account;

/**
 * Created by YeEun on 2017-07-16.
 */

public class Account {
    //닉네임
    //목표량

    int numOfGoal;
    int alarm;

    public Account(int numOfGoal,int alarm) {
        this.numOfGoal = numOfGoal;
        this.alarm = alarm;
    }

    public int getNumOfGoal() {
        return numOfGoal;
    }

    public void setNumOfGoal(int numOfGoal) {
        this.numOfGoal = numOfGoal;
    }

    public int getAlarm() {
        return alarm;
    }

    public void setAlarm(int alarm) {
        this.alarm = alarm;
    }
}
