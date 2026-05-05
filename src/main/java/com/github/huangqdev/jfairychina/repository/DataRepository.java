/*
 * Copyright (c) 2026 Qiang (Quentin) Huang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.huangqdev.jfairychina.repository;

import com.github.huangqdev.jfairychina.model.config.ChinaDataConfig;
import com.github.huangqdev.jfairychina.model.config.NameGroups;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Thread-safe repository for loading Chinese mock data from YAML configuration.
 * <p>
 * Loads a single merged configuration file (all.yml) using SnakeYAML's
 * Constructor-based deserialization for full type safety. The configuration
 * is lazily loaded and cached on first access.
 */
public class DataRepository {

    /**
     * Merged configuration file name
     */
    private static final String CONFIG_FILE = "all.yml";

    /**
     * Resource base path
     */
    private static final String BASE_PATH = "data/";

    /**
     * Lazily loaded and cached configuration (volatile for double-checked locking)
     */
    private volatile ChinaDataConfig config;

    /**
     * Loads the full configuration from the merged YAML file.
     * Uses double-checked locking for thread-safe lazy initialization.
     *
     * @return the loaded configuration
     */
    private ChinaDataConfig getConfig() {
        if (config == null) {
            synchronized (this) {
                if (config == null) {
                    config = loadConfig();
                }
            }
        }
        return config;
    }

    /**
     * Loads surnames list.
     *
     * @return list of Chinese surnames
     */
    public List<String> loadSurnames() {
        return getConfig().getSurnames();
    }

    /**
     * Loads first names grouped by gender.
     *
     * @return map with "male" and "female" keys
     */
    public Map<String, List<String>> loadFirstNames() {
        NameGroups groups = getConfig().getFirstnames();
        return java.util.Collections.unmodifiableMap(
            new java.util.HashMap<String, List<String>>() {{
                put("male", groups.getMale());
                put("female", groups.getFemale());
            }}
        );
    }

    /**
     * Loads province code to name mapping.
     *
     * @return province code to name map
     */
    public Map<String, String> loadProvinceCodes() {
        return getConfig().getProvinces();
    }

    /**
     * Gets the list of all province codes.
     *
     * @return list of province codes
     */
    public List<String> getProvinceCodeList() {
        return loadProvinceCodes().keySet().stream()
                .collect(Collectors.toList());
    }

    /**
     * Loads city and district codes list.
     *
     * @return list of 4-digit city/district codes
     */
    public List<String> loadCityDistrictCodes() {
        return getConfig().getCityDistrictCodes();
    }

    /**
     * Loads and parses the merged YAML configuration file.
     *
     * @return the parsed configuration object
     * @throws RuntimeException if the configuration file cannot be loaded
     */
    private ChinaDataConfig loadConfig() {
        validateFileName(CONFIG_FILE);

        String path = BASE_PATH + CONFIG_FILE;
        try (InputStream is = getResourceStream(path)) {
            LoaderOptions loaderOptions = new LoaderOptions();
            Yaml yaml = new Yaml(new Constructor(ChinaDataConfig.class, loaderOptions));
            return yaml.load(is);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration: " + path, e);
        }
    }

    /**
     * Gets a resource input stream from the classpath.
     *
     * @param path the resource path
     * @return the input stream
     * @throws RuntimeException if the resource is not found
     */
    private InputStream getResourceStream(String path) {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);

        if (is == null) {
            is = getClass().getClassLoader().getResourceAsStream(path);
        }

        if (is == null) {
            throw new RuntimeException("Resource not found: " + path);
        }

        return is;
    }

    /**
     * Validates the file name to prevent path traversal attacks.
     *
     * @param fileName the file name to validate
     * @throws IllegalArgumentException if the file name is unsafe
     */
    private void validateFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }

        if (fileName.contains("..")) {
            throw new IllegalArgumentException("File name contains invalid characters: " + fileName);
        }

        if (fileName.contains("\0")) {
            throw new IllegalArgumentException("File name contains null character: " + fileName);
        }
    }
}
