# Kotlin Technology Stack & Build System

## Kotlin Development Environment

### Build Tools & Dependencies
- **Build System**: Gradle
- **Testing Framework**: JUnit 5
- **Build Plugin**: shadow
- **SDK Version**: AWS SDK for Kotlin v1
- **Kotlin Version**: 1.5.31 


### Common Build Commands

```bash
# Build and Package
./gradlew compileKotlin                # Compile source code
./gradlew build                       # Build with dependencies
./gradlew clean build                 # Clean and build

# Testing
./gradlew test                         # Run all tests
./gradlew test --tests ClassName      # Run specific test class
./gradlew test --tests ClassName.methodName  # Run specific test method

# Execution
./gradlew run

```

### Koltin-Specific Pattern Requirements

#### File Naming Conventions
- Use PascalCase for class names
- Service prefix pattern: `{Service}Action.kt` (e.g., `S3ListBuckets.kt`)
- Hello scenarios: `Hello{Service}.kt` (e.g., `HelloS3.kt`)
- Test files: `{Service}ActionTest.kt`

#### Hello Scenario Structure
- **Class naming**: `Hello{Service}.kt` class with main method with no class declaration
- **Method structure**: suspend main method as entry point. All methods need to be suspend.
- **Documentation**: Include comments explaining the purpose of the method.

#### Code Structure Standards
- **Package naming**: Use reverse domain notation (e.g., `com.example.s3`)
- **Class structure**: One kotlin file matching filename
- **Method naming**: Use camelCase for method names
- **Constants**: Use UPPER_SNAKE_CASE for static final variables
- **Imports**: Group imports logically (Kotlin standard, AWS SDK, other libraries)

#### Error Handling Patterns

In Kotlin, suspend functions are designed to be used with coroutines, which provide a way to write asynchronous code that is easy to read and maintain. When it comes to error handling in coroutines, you typically use structured concurrency and the try-catch.

```java
suspend fun listBucketObjects(bucketName: String) {
    try {
        val request = ListObjectsRequest {
            bucket = bucketName
        }

        S3Client.fromEnvironment { region = "us-east-1" }.use { s3 ->
            try {
                val response = s3.listObjects(request)
                response.contents?.forEach { myObject ->
                    println("The name of the key is ${myObject.key}")
                    println("The object is ${myObject.size?.let { calKb(it) }} KBs")
                    println("The owner is ${myObject.owner}")
                }
            } catch (e: AwsServiceException) {
                println("Service error: ${e.statusCode}, ${e.message}")
            } catch (e: SdkClientException) {
                println("Client error: ${e.message}")
            }
        }
    } catch (e: Exception) {
        println("Unexpected error: ${e.message}")
    }
}


```


#### Testing Standards
- **Test framework**: Use JUnit 5 annotations (`@Test`, `@BeforeEach`, `@AfterEach`)
- **Integration tests**: Mark with `@Tag("IntegrationTest")`
- **Resource management**: Use try-with-resources for AWS clients
- **Assertions**: Use JUnit 5 assertion methods
- **Test naming**: Use descriptive method names explaining test purpose


#### Gradle Project Structure for Kotlin
```
├── build.gradle.kts
├── settings.gradle.kts
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── {service}/
│   │   │               ├── Hello{Service}.kt
│   │   │               ├── {Service}Actions.kt
│   │   │               └── {Service}Scenario.kt
│   │   └── resources/
│   └── test/
│       ├── kotlin/
│       │   └── com/
│       │       └── example/
│       │           └── {service}/
│       │               └── {Service}Test.kt
│       └── resources/
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
└── gradlew
└── gradlew.bat
```

#### Documentation Requirements
- **Class Javadoc**: Include purpose, usage examples, and prerequisites
- **Method Javadoc**: Document parameters, return values, and exceptions
- **Inline comments**: Explain complex AWS service interactions
- **README sections**: Include Gradle setup and execution instructions

### Language-Specific Pattern Errors to Avoid
- ❌ **NEVER assume class naming without checking existing examples**
- ❌ **NEVER use snake_case for Kotlin class or method names**
- ❌ **NEVER forget to close AWS clients (use .fromEnvironment)**
- ❌ **NEVER ignore proper exception handling for AWS operations**
- ❌ **NEVER skip Gradle dependency management**

### Best Practices
- ✅ **ALWAYS follow the established Gradle project structure**
- ✅ **ALWAYS use PascalCase for class names and camelCase for methods**
- ✅ **ALWAYS use .fromEnvironment for AWS client management**
- ✅ **ALWAYS include proper exception handling for AWS service calls**
- ✅ **ALWAYS follow Kotlin naming conventions and package structure**
- ✅ **ALWAYS include comprehensive Javadoc documentation**



### Maven Configuration Requirements
- **AWS SDK BOM**: Include AWS SDK Bill of Materials for version management
- **Compiler plugin**: Configure for appropriate Java version
- **Shade plugin**: For creating executable JARs with dependencies
- **Surefire plugin**: Gradle uses the test task for running tests.

### Integration with Knowledge Base
Before creating Kotlin code examples:
1. Query `coding-standards-KB` for "Kotlin-code-example-standards"
2. Query `Kotlin-premium-KB` for "Kotlin implementation patterns"
3. Follow KB-documented patterns for Gradle structure and class organization
4. Validate against existing Kotlin examples only after KB consultation