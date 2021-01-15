package com.e.quizzy.Models;

import java.util.List;

public class item {

    private List<Categories>category;
    private List<Categories>random;

    public item(List<Categories> category, List<Categories> random) {
        this.category = category;
        this.random = random;
    }

    public List<Categories> getCategory() {
        return category;
    }

    public List<Categories> getRandom() {
        return random;
    }
}
