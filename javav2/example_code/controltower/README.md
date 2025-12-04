# AWS Control Tower code examples for the SDK for Java 2.x

## Overview

This is a workspace where you can find the following AWS SDK for Java 2.x AWS Control Tower examples.

## ⚠ Important

* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege.

## Code examples

### Actions

The following examples show you how to perform actions using the AWS SDK for Java 2.x.

* [List landing zones](src/main/java/com/example/controltower/ControlTowerActions.java) (`ListLandingZones`)
* [List baselines](src/main/java/com/example/controltower/ControlTowerActions.java) (`ListBaselines`)
* [List enabled baselines](src/main/java/com/example/controltower/ControlTowerActions.java) (`ListEnabledBaselines`)
* [Enable baseline](src/main/java/com/example/controltower/ControlTowerActions.java) (`EnableBaseline`)
* [Disable baseline](src/main/java/com/example/controltower/ControlTowerActions.java) (`DisableBaseline`)
* [Get baseline operation](src/main/java/com/example/controltower/ControlTowerActions.java) (`GetBaselineOperation`)
* [List enabled controls](src/main/java/com/example/controltower/ControlTowerActions.java) (`ListEnabledControls`)
* [Enable control](src/main/java/com/example/controltower/ControlTowerActions.java) (`EnableControl`)
* [Disable control](src/main/java/com/example/controltower/ControlTowerActions.java) (`DisableControl`)
* [Get control operation](src/main/java/com/example/controltower/ControlTowerActions.java) (`GetControlOperation`)

### Scenarios

The following examples show you how to implement common scenarios.

* [Learn the basics](src/main/java/com/example/controltower/ControlTowerScenario.java) - Learn the basics by checking setup, managing baselines and controls.

### Hello

* [Hello Control Tower](src/main/java/com/example/controltower/HelloControlTower.java) - Get started with AWS Control Tower.

## Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region configured.
- Java 17 or later
- Maven 3.6 or later
- AWS Control Tower must be set up in your account

## Install

To build and run the examples, navigate to the directory that contains a `pom.xml` file and run the following command:

```
mvn compile
```

## Run the examples

### Instructions

All examples can be run individually. For example:

```
mvn exec:java -Dexec.mainClass="com.example.controltower.HelloControlTower" -Dexec.args="us-east-1"
```

### Hello Control Tower

This example shows you how to get started using AWS Control Tower.

```
mvn exec:java -Dexec.mainClass="com.example.controltower.HelloControlTower" -Dexec.args="us-east-1"
```

### Learn the basics

This interactive scenario runs at a command prompt and shows you how to use AWS Control Tower to do the following:

1. Check Control Tower setup and list landing zones
2. List available baselines for governance
3. List currently enabled baselines
4. Enable baselines for organizational units
5. List and enable controls for compliance
6. Monitor operation status
7. Clean up resources

```
mvn exec:java -Dexec.mainClass="com.example.controltower.ControlTowerScenario" -Dexec.args="us-east-1"
```

## Run the tests

Unit tests in this module use JUnit 5. To run all of the tests, 
run the following in your [GitHub root]/javav2/example_code/controltower folder.

```
mvn test
```

## Additional resources

- [AWS Control Tower User Guide](https://docs.aws.amazon.com/controltower/latest/userguide/)
- [AWS Control Tower API Reference](https://docs.aws.amazon.com/controltower/latest/APIReference/)
- [AWS SDK for Java 2.x (AWS Control Tower)](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/controltower/package-summary.html)