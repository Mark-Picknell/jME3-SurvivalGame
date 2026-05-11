[![Build Status](https://travis-ci.org/Mark-Picknell/jME3-SurvivalGame.svg?branch=master)](https://travis-ci.org/Mark-Picknell/jME3-SurvivalGame)
[![License](https://img.shields.io/badge/License-BSD%203--Clause-blue.svg)](https://opensource.org/licenses/BSD-3-Clause)
[![GitHub Stars](https://img.shields.io/github/stars/Mark-Picknell/jME3-SurvivalGame.svg)](https://github.com/Mark-Picknell/jME3-SurvivalGame/stargazers)
[![GitHub Issues](https://img.shields.io/github/issues/Mark-Picknell/jME3-SurvivalGame.svg)](https://github.com/Mark-Picknell/jME3-SurvivalGame/issues)
[![Current Version](https://img.shields.io/badge/version-1.0.0-green.svg)](https://github.com/Mark-Picknell/jME3-SurvivalGame)
[![Live Demo](https://img.shields.io/badge/demo-online-red.svg)]()

# jME3-SurvivalGame

## Game Design Notes

---

## Technical Highlights

---

## Art Direction

---

## Project set up

This is a Gradle project using JMonkey Engine and other Java libraries

### Modules : 

Game module `:game` : holds `build.gradle` dependencies for the game code & should hold your code.

Desktop module `:desktop` : holds `build.gradle` for desktop dependencies & uses the `:game` module, this module can hold the desktop gui.

---

## Running Locally

### Desktop : 

```gradle
./gradlew run
```

---

## Building Game :

### Desktop :

```bash
    $./gradlew :desktop:copyJars
```

### Distribute with a JRE

Distributing with a JRE means you'll need to provide an operating specific bundle for each OS you are
targeting (which is a disadvantage). You will not have to have a JRE locally installed (which is an advantage).

**Either:**

In your IDE execute the Gradle task distZip (which you'll find under Gradle > distributions > buildAllDistributions)

**Or:**

In the command line open at the root of this project enter the following command: gradlew buildAllDistributions

Then you will find a series of zip in the build/distributions folder. These zip(s) will contain your game, all the libraries to run it and an
OS specific JRE. (The same files will also be available unzipped in a folder, which may be useful if distributing via steampipe or similar).

**References :**

- Gradle DSL : https://docs.gradle.org/current/dsl/index.html
- Gradle for java : https://docs.gradle.org/current/userguide/multi_project_builds.html
- Gradle/Groovy Udacity course by Google : https://github.com/udacity/ud867/blob/master/1.11-Exercise-ConfigureFileSystemTasks/solution.gradle
- See JMonkeyEngine Desktop Example : https://github.com/Scrappers-glitch/basic-gradle-template
- See JMonkeyEngine RPI armhf Desktop Example : https://github.com/Scrappers-glitch/JmeCarPhysicsTestRPI

---

## Contributors

|                                                       Avatar                                                        |   Role    |                            Profile                            | Notes                                                                               |
|:-------------------------------------------------------------------------------------------------------------------:|:---------:|:-------------------------------------------------------------:|:------------------------------------------------------------------------------------|
| [![mark-picknell](https://github.com/mark-picknell.png?size=64)](https://hub.jmonkeyengine.org/users/mark-picknell) | Developer | [@SkidRunner](https://hub.jmonkeyengine.org/users/skidrunner) |                                                                                     |
|     [![skidrunner](https://github.com/skidrunner.png?size=64)](https://hub.jmonkeyengine.org/users/skidrunner)      | Developer |         [@SkidRunner](https://github.com/skidrunner)          | Mark Picknell's old profile no longer active on jMonkeyEngine3, Google, and GitHub. |
