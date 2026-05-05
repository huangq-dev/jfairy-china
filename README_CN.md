# jFairy China

[English](README.md) | 简体中文

> 更懂中国国情的数据伪造工具

[![Inspired by jFairy](https://img.shields.io/badge/inspired%20by-jFairy-blue)](https://github.com/SkillPanel/jfairy)

一个**轻量、无依赖**的 Java 库，专为需要生成中国特色伪造数据的开发者设计，适用于测试和开发场景。

---

## ✨ 核心特性

- **身份证号码生成** - 18位身份证号码，完全符合 **GB/T 2260-2022** 国家标准和 ISO 7064:1983.MOD 11-2 校验码算法
- **中文姓名生成** - 基于真实人口分布数据的姓氏和名字
- **2026 最新行政区划** - 已更新至最新国家标准的省份编码数据（时效性保障）
- **确定性生成（支持随机种子）** - 基于种子的随机生成，确保测试数据可重现
- **零外部依赖** - 纯 Java 实现，仅使用 SnakeYAML 加载资源文件

---

## 🙏 致谢声明

jFairy China 的设计灵感来源于优秀的 [jFairy](https://github.com/SkillPanel/jfairy) 库。我们钦佩 jFairy 优雅的 API 设计，但本项目是**独立维护**的，针对中国本地化场景进行了深度定制开发。

> 💡 **说明**：jFairy China 并非 jFairy 的官方分支。这是一个拥有独立代码库的独立项目，专为中国监管要求和数据格式进行了优化。

---

## 🚀 快速开始

### 安装

在 Maven 项目中添加以下依赖：

```xml
<dependency>
    <groupId>com.github.huangq-dev</groupId>
    <artifactId>jfairy-china</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

### 使用示例

#### 基础用法：生成姓名和身份证

```java
import com.github.huangqdev.jfairychina.Fairy;
import com.github.huangqdev.jfairychina.provider.PersonProvider.Gender;

// 使用随机种子创建实例（保证可重现）
Fairy fairy = Fairy.create(42);

// 生成中文全名
String fullName = fairy.person().generateFullName();

// 按性别生成姓名
String maleName = fairy.person().generateFullName(Gender.MALE);
String femaleName = fairy.person().generateFullName(Gender.FEMALE);

// 生成有效的18位身份证号码
String idNumber = fairy.person().generateIdNumber();

// 验证身份证号码格式
boolean isValid = fairy.person().isValidIdNumber(idNumber);
```

#### 高级用法：生成完整的虚拟用户

```java
import com.github.huangqdev.jfairychina.Fairy;
import com.github.huangqdev.jfairychina.model.Person;

// 创建实例
Fairy fairy = Fairy.create();

// 生成完整的虚拟用户信息
Person person = fairy.personFactory().generateRandomPerson();

System.out.println("姓名: " + person.getFullName());
System.out.println("性别: " + person.getGender());
System.out.println("身份证号: " + person.getIdentityCardNumber());
System.out.println("出生日期: " + person.getBirthDate());
System.out.println("省份: " + person.getProvinceName() + " (" + person.getProvinceCode() + ")");

// 按指定性别生成
Person malePerson = fairy.personFactory().generateRandomPerson("MALE");
Person femalePerson = fairy.personFactory().generateRandomPerson("FEMALE");
```

---

## 📁 项目结构

```
src/
├── main/
│   ├── java/com/github/huangqdev/jfairychina/
│   │   ├── Fairy.java                 # 统一入口类
│   │   ├── factory/
│   │   │   └── PersonFactory.java     # 人员信息工厂
│   │   ├── model/
│   │   │   └── Person.java            # 人员数据模型
│   │   ├── provider/
│   │   │   └── PersonProvider.java    # 人员数据生成器
│   │   ├── repository/
│   │   │   └── DataRepository.java    # YAML 资源加载器
│   │   └── util/
│   │       └── ChineseIDUtils.java    # 身份证校验工具类
│   └── resources/data/zh_CN/
│       ├── surnames.yml               # 姓氏列表
│       ├── firstnames.yml             # 名字列表（男女）
│       └── id_codes.yml               # 省份编码
└── test/java/com/github/huangqdev/jfairychina/  # 单元测试
```

---

## 🎯 设计理念

- **极简主义**：无重型框架，无依赖注入
- **标准驱动**：严格遵循中国国家标准
- **工厂模式**：简洁的基于构造函数的依赖管理
- **Java 8+ 现代化**：采用 Streams API 和现代最佳实践
- **生产就绪**：防御性编程和全面的测试覆盖

---

## ⚠️ 【免责声明】

> **⚠️ 用途限制**：本库**仅限**用于**开发和测试目的**。生成的数据（身份证、统一社会信用代码等）仅遵循校验码逻辑，**不代表**任何真实个人或实体信息。
>
> **⚠️ 严禁非法用途**：**严禁**将本库用于以下行为：绕过实名认证系统、欺诈、伪造证件、非法注册账号或任何违反中华人民共和国法律法规的活动。
>
> **⚠️ 法律责任自负**：使用者因违反上述约定而产生的任何法律责任（包括刑事、民事责任），均由使用者本人承担，本项目作者及贡献者不承担任何直接或间接的连带责任。
>
> **⚠️ 非真实性保证**：尽管本项目致力于算法的准确性，但生成的数据本质上是伪造的。本项目不对生成数据在真实业务环境中的有效性作任何保证。
>
> **⚠️ 知情同意**：一旦您开始使用本项目，即视为您已完全理解并同意上述所有条款。

---

## 🛣️ 开发路线图

| 版本 | 目标功能 |
|------|----------|
| v0.1.0 | 身份证号码、中文姓名、PersonFactory |
| v0.2.0 | 手机号码（移动/联通/电信） |
| v0.3.0 | 统一社会信用代码（GB 32100-2015） |
| v0.4.0 | 银行卡号（带 Luhn 校验） |
| v1.0.0 | 稳定版本，完整功能集 |

---

## 📜 许可证

Apache License 2.0。详见 [LICENSE](LICENSE)。