package com.github.huangqdev.jfairychina.model.config;

import java.util.List;

/**
 * Container for gender-separated first name lists.
 * <p>
 * Maps to the "firstnames" section in the YAML configuration.
 */
public class NameGroups {

    private List<String> male;
    private List<String> female;

    public List<String> getMale() {
        return male;
    }

    public void setMale(List<String> male) {
        this.male = male;
    }

    public List<String> getFemale() {
        return female;
    }

    public void setFemale(List<String> female) {
        this.female = female;
    }
}
