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
package com.github.huangqdev.jfairychina;

import com.github.huangqdev.jfairychina.factory.PersonFactory;
import com.github.huangqdev.jfairychina.provider.PersonProvider;
import com.github.huangqdev.jfairychina.repository.DataRepository;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Unified entry point for generating Chinese mock data.
 * <p>
 * This class serves as the main facade for the jFairy China library,
 * providing access to various data generators through provider classes.
 * <p>
 * Thread-safety: This class is thread-safe. The default factory method
 * uses ThreadLocalRandom for optimal concurrent performance.
 */
public class Fairy {

    private final Random random;
    private final DataRepository dataRepository;
    private final PersonProvider personProvider;
    private final PersonFactory personFactory;

    private Fairy(Random random) {
        this.random = random;
        this.dataRepository = new DataRepository();
        this.personProvider = new PersonProvider(random, dataRepository);
        this.personFactory = new PersonFactory(dataRepository, random);
    }

    /**
     * Creates a Fairy instance with ThreadLocalRandom for optimal thread safety.
     *
     * @return a new Fairy instance
     */
    public static Fairy create() {
        return new Fairy(ThreadLocalRandom.current());
    }

    /**
     * Creates a Fairy instance with a seeded Random for reproducible generation.
     *
     * @param seed the seed value for deterministic generation
     * @return a new Fairy instance
     */
    public static Fairy create(int seed) {
        return new Fairy(new Random(seed));
    }

    /**
     * Creates a Fairy instance with the specified Random generator.
     *
     * @param random the random number generator
     * @return a new Fairy instance
     */
    public static Fairy create(Random random) {
        return new Fairy(random);
    }

    /**
     * Gets the person provider for generating basic person data.
     *
     * @return the person provider
     */
    public PersonProvider person() {
        return personProvider;
    }

    /**
     * Gets the person factory for generating complete person models.
     *
     * @return the person factory
     */
    public PersonFactory personFactory() {
        return personFactory;
    }

    /**
     * Gets the random number generator.
     *
     * @return the random instance
     */
    public Random getRandom() {
        return random;
    }
}