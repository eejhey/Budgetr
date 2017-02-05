package com.rexicore.budgetr;

/**
 * Created by Eddie on 2/4/2017.
 */

public class Expense {
    private int id;
    private String title;
    private int amount;
    private String category;

    public Expense(int _id, String title, int amount, String category) {
        this.id = _id;
        this.title = title;
        this.amount = amount;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }
}
