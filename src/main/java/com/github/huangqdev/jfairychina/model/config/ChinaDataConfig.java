package com.github.huangqdev.jfairychina.model.config;

import java.util.List;
import java.util.Map;

/**
 * Strongly-typed configuration class for all Chinese mock data.
 * <p>
 * Maps to the merged "all.yml" configuration file. All fields are
 * populated by SnakeYAML's Constructor-based deserialization,
 * eliminating the need for unchecked casts.
 */
public class ChinaDataConfig {

    private List<String> surnames;
    private NameGroups firstnames;
    private Map<String, String> provinces;
    private List<String> cityDistrictCodes;

    public List<String> getSurnames() {
        return surnames;
    }

    public void setSurnames(List<String> surnames) {
        this.surnames = surnames;
    }

    public NameGroups getFirstnames() {
        return firstnames;
    }

    public void setFirstnames(NameGroups firstnames) {
        this.firstnames = firstnames;
    }

    public Map<String, String> getProvinces() {
        return provinces;
    }

    public void setProvinces(Map<String, String> provinces) {
        this.provinces = provinces;
    }

    public List<String> getCityDistrictCodes() {
        return cityDistrictCodes;
    }

    public void setCityDistrictCodes(List<String> cityDistrictCodes) {
        this.cityDistrictCodes = cityDistrictCodes;
    }
}
