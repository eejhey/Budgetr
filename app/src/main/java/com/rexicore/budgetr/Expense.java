package com.rexicore.budgetr;

/**
 * Created by Eddie on 2/4/2017.
 */

public class Expense {
    private int id;
    private String title;
    private double amount;
    private String group = "none";
    private String category;

    public Expense(int _id, String title, double amount, String category) {
        this.id = _id;
        this.title = title;
        this.amount = amount;
        this.category = category;
    }

    public Expense(int _id, String title, double amount, String group, String category) {
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

    public String getGroup() {
        return group;
    }

    public String getCategory() {
        return category;
    }
}
