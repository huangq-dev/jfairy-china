# jFairy China

English | [简体中文](README_CN.md)

> China-Centric Mock Data Generator

[![Inspired by jFairy](https://img.shields.io/badge/inspired%20by-jFairy-blue)](https://github.com/SkillPanel/jfairy)

A **lightweight, dependency-free** Java library for generating China-specific mock data. Built from the ground up for developers who need authentic Chinese fake data for testing and development workflows.

---

## ✨ Key Features

- **Chinese ID Card Generation** - 18-digit ID numbers fully compliant with **GB/T 2260-2022** national standard and ISO 7064:1983.MOD 11-2 checksum algorithm
- **Authentic Chinese Names** - Surnames and given names derived from real demographic distribution data
- **2026 Administrative Divisions** - Updated province codes aligned with the latest national standards
- **Deterministic & Reproducible** - Seed-based random generation for consistent test data
- **Zero External Dependencies** - Pure Java implementation with only SnakeYAML for resource loading

---

## 🙏 Credits

jFairy China draws inspiration from the excellent [jFairy](https://github.com/SkillPanel/jfairy) library. While we admire jFairy's elegant API design, this project is **independently maintained** with deep customization for Chinese localization scenarios.

> 💡 **Note**: jFairy China is not an official branch of jFairy. It is a standalone project with its own codebase, optimized for Chinese regulatory requirements and data formats.

---

## 🚀 Quick Start

### Installation

This project is distributed via [JitPack](https://jitpack.io/). Add the JitPack repository and dependency to your Maven project:

**1. Add JitPack Repository**

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

**2. Add Dependency**

```xml
<dependency>
    <groupId>com.github.huangq-dev</groupId>
    <artifactId>jfairy-china</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

**Available Versions:**

| Version | Description |
|---------|-------------|
| `0.1.0-SNAPSHOT` | Latest development version |
| `main-SNAPSHOT` | Latest development build (includes unreleased features) |
| `{commit-hash}` | Specific commit build (e.g., `7f285d2`) |

### Usage

#### Basic Usage: Generate Names and ID Numbers

```java
import com.github.huangqdev.jfairychina.Fairy;
import com.github.huangqdev.jfairychina.provider.PersonProvider.Gender;

// Create instance with seed for reproducibility
Fairy fairy = Fairy.create(42);

// Generate a full Chinese name
String fullName = fairy.person().generateFullName();

// Generate gender-specific names
String maleName = fairy.person().generateFullName(Gender.MALE);
String femaleName = fairy.person().generateFullName(Gender.FEMALE);

// Generate valid 18-digit Chinese ID number
String idNumber = fairy.person().generateIdNumber();

// Validate ID number format
boolean isValid = fairy.person().isValidIdNumber(idNumber);
```

#### Advanced Usage: Generate Complete Virtual Person

```java
import com.github.huangqdev.jfairychina.Fairy;
import com.github.huangqdev.jfairychina.model.Person;

// Create instance
Fairy fairy = Fairy.create();

// Generate a complete virtual person
Person person = fairy.personFactory().generateRandomPerson();

System.out.println("Name: " + person.getFullName());
System.out.println("Gender: " + person.getGender());
System.out.println("ID Number: " + person.getIdentityCardNumber());
System.out.println("Birth Date: " + person.getBirthDate());
System.out.println("Province: " + person.getProvinceName() + " (" + person.getProvinceCode() + ")");

// Generate by specific gender
Person malePerson = fairy.personFactory().generateRandomPerson("MALE");
Person femalePerson = fairy.personFactory().generateRandomPerson("FEMALE");
```

---

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/github/huangqdev/jfairychina/
│   │   ├── Fairy.java                 # Unified entry point
│   │   ├── factory/
│   │   │   └── PersonFactory.java     # Person information factory
│   │   ├── model/
│   │   │   └── Person.java            # Person data model
│   │   ├── provider/
│   │   │   └── PersonProvider.java    # Person data generator
│   │   ├── repository/
│   │   │   └── DataRepository.java    # YAML resource loader
│   │   └── util/
│   │       └── ChineseIDUtils.java    # ID validation utilities
│   └── resources/data/zh_CN/
│       ├── surnames.yml               # Surname list
│       ├── firstnames.yml             # Given names (male/female)
│       └── id_codes.yml               # Province codes
└── test/java/com/github/huangqdev/jfairychina/  # Unit tests
```

---

## 🎯 Design Philosophy

- **Minimalist**: No heavy frameworks, no dependency injection
- **Standards-Driven**: Strict compliance with Chinese national standards
- **Factory Pattern**: Clean constructor-based dependency management
- **Java 8+ Modern**: Leverages Streams API and modern best practices
- **Production-Ready**: Built with defensive coding and comprehensive test coverage

---

## ⚠️ IMPORTANT LEGAL NOTICE

> **USE RESTRICTION**: This library is **exclusively** intended for **development and testing purposes only**. Generated data (ID cards, social credit codes, etc.) follows validation algorithms but does **not** represent real individuals or entities.
>
> **ILLEGAL USE PROHIBITED**: This library may **not** be used for: bypassing authentication systems, fraud, document forgery, illegal account registration, or any activity violating the laws of the People's Republic of China.
>
> **LEGAL LIABILITY**: Users bear full responsibility for any legal consequences (criminal or civil) arising from improper use. Authors and contributors assume no liability.
>
> **NON-AUTHENTICITY**: Generated data is synthetic by design. No guarantees are made regarding validity in real-world scenarios.
>
> **ACKNOWLEDGMENT**: Using this library signifies your full understanding and acceptance of these terms.

---

## 🛣️ Roadmap

| Version | Target Features |
|---------|----------------|
| v0.1.0 | Chinese ID Card, Chinese Names, PersonFactory |
| v0.2.0 | Mobile Phone Numbers (China Mobile/Unicom/Telecom) |
| v0.3.0 | Unified Social Credit Code (GB 32100-2015) |
| v0.4.0 | Bank Card Numbers with Luhn validation |
| v1.0.0 | Stable release with full feature set |

---

## 📜 License

Apache License 2.0. See [LICENSE](LICENSE) for details.