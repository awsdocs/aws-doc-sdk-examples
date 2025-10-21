# Java Technology Stack & Build System

## Java v2 Development Environment

### Build Tools & Dependencies
- **Build System**: Apache Maven
- **Testing Framework**: JUnit 5
- **Build Plugin**: Apache Maven Shade Plugin
- **SDK Version**: AWS SDK for Java v2
- **Java Version**: JDK 17

### Common Build Commands

```bash
# Build and Package
mvn clean compile                 # Compile source code
mvn package                       # Build with dependencies
mvn clean package                 # Clean and build

# Testing
mvn test                          # Run all tests
mvn test -Dtest=ClassName         # Run specific test class
mvn test -Dtest=ClassName#methodName  # Run specific test method

# Execution
java -cp target/PROJECT-1.0-SNAPSHOT.jar com.example.Main
mvn exec:java -Dexec.mainClass="com.example.Main"
```

### Java-Specific Pattern Requirements

#### File Naming Conventions
- Use PascalCase for class names
- Service prefix pattern: `{Service}Action.java` (e.g., `S3ListBuckets.java`)
- Hello scenarios: `Hello{Service}.java` (e.g., `HelloS3.java`)
- Test files: `{Service}ActionTest.java`

#### Hello Scenario Structure
- **Class naming**: `Hello{Service}.java` class with main method
- **Method structure**: Static main method as entry point
- **Documentation**: Include Javadoc explaining the hello example purpose

#### Code Structure Standards
- **Package naming**: Use reverse domain notation (e.g., `com.example.s3`)
- **Class structure**: One public class per file matching filename
- **Method naming**: Use camelCase for method names
- **Constants**: Use UPPER_SNAKE_CASE for static final variables
- **Imports**: Group imports logically (Java standard, AWS SDK, other libraries)

#### Error Handling Patterns
```java
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.model.S3Exception;

public class ExampleClass {
    public void exampleMethod() {
        try (S3Client s3Client = S3Client.builder().build()) { //For Hello examples, use Sync service clients. For Scenario examples or working with service client, use the Java Async client. 
            // AWS service call
            var response = s3Client.operation();
            // Process response
        } catch (S3Exception e) {
            // Handle service-specific exceptions
            System.err.println("S3 Error: " + e.awsErrorDetails().errorMessage());
            throw e;
        } catch (SdkException e) {
            // Handle general SDK exceptions
            System.err.println("SDK Error: " + e.getMessage());
            throw e;
        }
    }
}
```

#### Testing Standards
- **Test framework**: Use JUnit 5 annotations (`@Test`, `@BeforeEach`, `@AfterEach`)
- **Integration tests**: Mark with `@Tag("IntegrationTest")` or similar
- **Resource management**: Use Github standards for AWS clients
- **Assertions**: Use JUnit 5 assertion methods
- **Test naming**: Use descriptive method names explaining test purpose

#### Maven Project Structure
```
src/
├── main/
│   └── java/
│       └── com/
│           └── example/
│               └── {service}/
│                   ├── Hello{Service}.java
│                   ├── {Service}Actions.java
│                   └── {Service}Scenario.java
└── test/
    └── java/
        └── com/
            └── example/
                └── {service}/
                    └── {Service}Test.java
```

#### Documentation Requirements
- **Class Javadoc**: Include purpose, usage examples, and prerequisites
- **Method Javadoc**: Document parameters, return values, and exceptions
- **Inline comments**: Explain complex AWS service interactions
- **README sections**: Include Maven setup and execution instructions

### Language-Specific Pattern Errors to Avoid
- ❌ **NEVER assume class naming without checking existing examples**
- ❌ **NEVER use snake_case for Java class or method names**
- ❌ **NEVER forget to close AWS clients (use try-with-resources)**
- ❌ **NEVER ignore proper exception handling for AWS operations**
- ❌ **NEVER skip Maven dependency management**

### Best Practices
- ✅ **ALWAYS follow the established Maven project structure**
- ✅ **ALWAYS use PascalCase for class names and camelCase for methods**
- ✅ **ALWAYS use try-with-resources for AWS client management**
- ✅ **ALWAYS include proper exception handling for AWS service calls**
- ✅ **ALWAYS follow Java naming conventions and package structure**
- ✅ **ALWAYS include comprehensive Javadoc documentation**

### Maven Configuration Requirements
- **AWS SDK BOM**: Include AWS SDK Bill of Materials for version management
- **Compiler plugin**: Configure for appropriate Java version
- **Shade plugin**: For creating executable JARs with dependencies
- **Surefire plugin**: For test execution configuration

### Integration with Knowledge Base
Before creating Java code examples:
1. Query `coding-standards-KB` for "Java-code-example-standards"
2. Query `Java-premium-KB` for "Java implementation patterns"
3. Follow KB-documented patterns for Maven structure and class organization
4. Validate against existing Java examples only after KB consultation