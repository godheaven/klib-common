<p style="text-align:left">
  <img src="https://www.kanopus.cl/assets/kanopus_black.png" width="220" alt="Kanopus logo"/>
</p>

![Maven](https://img.shields.io/maven-central/v/cl.kanopus.util/klib-common) ![License](https://img.shields.io/badge/license-Apache%20License%202.0-blue) ![Java](https://img.shields.io/badge/java-17+-orange)

# klib-common

This project is designed as a base utility that is used by the other libraries in the Kanopus ecosystem.
Allowing to group commonly used functionalities that can be applied to any new project.

## ✨ Features

- Contains utilities to encrypt and decrypt data (CryptographyUtils)
- Contains utilities for file processing (FileUtils)
- Contains utilities for processing texts and dates (Utils)
- Contains utilities to transfer information from one class to another through reflection. (KanopusBeanUtils)
- Contains several additional utilities that will be used throughout the Kanopus ecosystem.

## 🚀 Installation

Add the dependency to your `pom.xml`:

```xml

<dependency>
	<groupId>cl.kanopus.util</groupId>
	<artifactId>klib-common</artifactId>
	<version>4.05.0</version>
</dependency>
```

## 🚀 Usage Guide

After adding the dependency to your project, import the relevant classes from `cl.kanopus.common` and use the utilities
shown below. The examples are concise and ready to paste into your code.

### 1) ChangeUtils — detect changes between objects

```java
import cl.kanopus.common.util.ChangeUtils;
import cl.kanopus.common.change.ChangeAction;

// Compare two objects (POJOs with getters/setters)
ChangeAction action = ChangeUtils.checkChange(sourceObj, targetObj);
        boolean hasChanges = ChangeUtils.hasChanges(sourceObj, targetObj);

        // Compare lists and get a list of Comparator<T> with the action per element
        List<Comparator<MyType>> diffs = ChangeUtils.checkChangeOnList(oldList, newList);
```

> Note: `checkChange` traverses properties (up to a certain depth) and detects CREATE / DELETE / UPDATE / NONE.

### 2) CryptographyUtils — symmetric encryption/decryption (AES)

```java
import cl.kanopus.common.util.crypto.CryptographyUtils;

// Configure passphrase and algorithm (simple example)
CryptographyUtils.setEncryptKey("mySecretPassphrase",CryptographyUtils.CryptoAlgorithm.AES_GCM);

String ciphertext = CryptographyUtils.encrypt("Text to encrypt");
String plaintext = CryptographyUtils.decrypt(ciphertext);

System.out.

println("Ciphertext: "+ciphertext);
System.out.

println("Plaintext: "+plaintext);
```

> Warning: in production, provide the key from a secret manager and avoid hardcoding it.

### 3) KanopusBeanUtils — copy/transform beans and lists

```java
import cl.kanopus.common.util.KanopusBeanUtils;

// Copy properties from a source object into a new instance of the target type
TargetDto dto = KanopusBeanUtils.copyProperties(entity, TargetDto.class);

// Copy properties into an existing instance
KanopusBeanUtils.

        copyProperties(source, existingTarget);

        // Copy lists of objects to another class
        List<TargetDto> dtos = KanopusBeanUtils.copyList(entities, TargetDto.class);
```

> Supports common conversions (dates, identifiable enums, lists, ImageBase64, etc.) and can accept property translation
> maps.

### 4) Utils — miscellaneous utilities (dates, text, numbers)

```java
import cl.kanopus.common.util.Utils;

import java.util.Date;

// Generate random text
String token = Utils.generateRandomText(16);

        // Number and date formatting
        String num = Utils.getNumberFormat(12345.67);
        String date = Utils.getDateFormat(new Date());

        // Validations and text manipulation
        boolean emailOk = Utils.isValidEmail("user@domain.com");
        List<String> lines = Utils.splitText("A long sentence that\nshould be split into lines", 20);

System.out.

        println(token +" "+num+" "+date+" validEmail="+emailOk);
```

### 5) NumberToLetterConverter

It demonstrates converting several numeric values to their uppercase Spanish textual representation (including the
word "PESOS").

```java
import cl.kanopus.common.util.format.NumberToLetterConverter;

public class NumberToLetterExample {
    public static void main(String[] args) {
        System.out.println(NumberToLetterConverter.convertNumberToLetter(1000.0)); // MIL PESOS
        System.out.println(NumberToLetterConverter.convertNumberToLetter(45000.0)); // CUARENTA Y CINCO MIL PESOS
        System.out.println(NumberToLetterConverter.convertNumberToLetter(300897.0)); // TRESCIENTOS MIL OCHOCIENTOS NOVENTA Y SIETE PESOS
        System.out.println(NumberToLetterConverter.convertNumberToLetter(1000500.0)); // UN MILLON QUINIENTOS PESOS
        System.out.println(NumberToLetterConverter.convertNumberToLetter(50.0)); // CINCUENTA PESOS
    }
}
```

## 👤 Author

⭐**Pablo Andrés Díaz Saavedra** — Founder of **Kanopus – Software Guided by the Stars**⭐

Kanopus is building a constellation of developers creating tools, libraries and platforms that simplify software
engineering.

[GitHub](https://github.com/godheaven) | [LinkedIn](https://www.linkedin.com/in/pablo-diaz-saavedra-4b7b0522/) | [Website](https://kanopus.cl)

## 📄 License

This software is licensed under the Apache License, Version 2.0. See the LICENSE file for details.
I hope you enjoy it.

[![Apache License, Version 2.0](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://opensource.org/license/apache-2-0)

## 🛟 Support

For support or questions contact: 📧 [soporte@kanopus.cl](mailto:soporte@kanopus.cl)
