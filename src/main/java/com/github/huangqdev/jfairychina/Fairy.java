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

    public static Fairy create() {
        return new Fairy(new Random());
    }

    public static Fairy create(int seed) {
        return new Fairy(new Random(seed));
    }

    public static Fairy create(Random random) {
        return new Fairy(random);
    }

    public PersonProvider person() {
        return personProvider;
    }

    public PersonFactory personFactory() {
        return personFactory;
    }

    public Random getRandom() {
        return random;
    }
}