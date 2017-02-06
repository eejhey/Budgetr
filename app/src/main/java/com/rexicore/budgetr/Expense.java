package com.rexicore.budgetr;

import java.text.NumberFormat;

/**
 * Created by Eddie on 2/4/2017.
 */

public class Expense {
    private int id;
    private String title;
    private double amount;
    private long group = -1;
    private String category;

    public Expense(int _id, String title, double amount, String category) {
        this.id = _id;
        this.title = title;
        this.amount = amount;
        this.category = category;
    }

    public Expense(int _id, String title, double amount, long group, String category) {
        this.id = _id;
        this.title = title;
        this.amount = amount;
        this.group = group;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getAmount() {
        return amount;
    }

    public long getGroup() {
        return group;
    }

    public String getCategory() {
        return category;
    }

    public static String format(double amount) {
        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        return "$" + formatter.format(amount);
    }
}
