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

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DataRepository {

    /**
     * 资源文件基础路径
     */
    private static final String BASE_PATH = "data/zh_CN/";

    /**
     * YAML 解析器实例（线程安全）
     */
    private static final Yaml YAML = new Yaml();

    /**
     * 资源缓存（线程安全）
     */
    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    /**
     * 加载姓氏列表
     *
     * @return 姓氏列表
     */
    @SuppressWarnings("unchecked")
    public List<String> loadSurnames() {
        return (List<String>) getOrLoad("surnames.yml", () -> {
            try (InputStream is = getResourceStream("surnames.yml")) {
                return YAML.load(is);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load surnames.yml", e);
            }
        });
    }

    /**
     * 加载名字列表（按性别分组）
     *
     * @return 名字映射，key 为 "male" 或 "female"
     */
    @SuppressWarnings("unchecked")
    public Map<String, List<String>> loadFirstNames() {
        return (Map<String, List<String>>) getOrLoad("firstnames.yml", () -> {
            try (InputStream is = getResourceStream("firstnames.yml")) {
                return YAML.load(is);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load firstnames.yml", e);
            }
        });
    }

    /**
     * 加载省份编码映射
     *
     * @return 省份编码到名称的映射
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> loadProvinceCodes() {
        Map<String, Object> data = (Map<String, Object>) getOrLoad("id_codes.yml", () -> {
            try (InputStream is = getResourceStream("id_codes.yml")) {
                return YAML.load(is);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load id_codes.yml", e);
            }
        });
        return (Map<String, String>) data.get("provinces");
    }

    /**
     * 获取省份编码列表
     *
     * @return 所有省份编码的列表
     */
    public List<String> getProvinceCodeList() {
        return loadProvinceCodes().entrySet().stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 获取或加载资源（线程安全）
     * 使用 ConcurrentHashMap.computeIfAbsent 保证线程安全，
     * 不同资源的加载互不干扰
     *
     * @param key    资源键名
     * @param loader 资源加载器
     * @return 加载的资源对象
     */
    private Object getOrLoad(String key, SupplierWithException<Object> loader) {
        // ConcurrentHashMap.computeIfAbsent 本身是原子操作，不需要额外同步
        return cache.computeIfAbsent(key, k -> {
            try {
                return loader.get();
            } catch (Exception e) {
                // 移除缓存中的无效条目
                cache.remove(k);
                throw new RuntimeException("Failed to load resource: " + k, e);
            }
        });
    }

    /**
     * 获取资源输入流
     *
     * @param fileName 文件名
     * @return 资源输入流
     * @throws RuntimeException 如果资源不存在
     */
    private InputStream getResourceStream(String fileName) {
        // 路径安全性检查：防止路径遍历攻击
        validateFileName(fileName);

        String path = BASE_PATH + fileName;
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);

        // 降级到当前类的 ClassLoader
        if (is == null) {
            is = getClass().getClassLoader().getResourceAsStream(path);
        }

        if (is == null) {
            throw new RuntimeException("Resource not found: " + path);
        }

        return is;
    }

    /**
     * 验证文件名安全性
     * 防止路径遍历攻击（如 ../../etc/passwd）
     *
     * @param fileName 文件名
     * @throws IllegalArgumentException 如果文件名不安全
     */
    private void validateFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }

        // 检查是否包含路径遍历字符
        if (fileName.contains("..")) {
            throw new IllegalArgumentException("File name contains invalid characters: " + fileName);
        }

        // 检查是否包含目录分隔符
        if (fileName.contains("/") || fileName.contains("\\")) {
            throw new IllegalArgumentException("File name cannot contain path separators: " + fileName);
        }

        // 检查是否包含空字符
        if (fileName.contains("\0")) {
            throw new IllegalArgumentException("File name contains null character: " + fileName);
        }
    }

    /**
     * 带异常的 Supplier 接口
     *
     * @param <T> 返回类型
     */
    @FunctionalInterface
    private interface SupplierWithException<T> {
        T get() throws Exception;
    }
}