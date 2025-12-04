# Java Test Generation

## MANDATORY: Knowledge Base Consultation (FIRST STEP)
**🚨 CRITICAL - Must be completed BEFORE any code generation**

```bash
# Step 1: List available knowledge bases
ListKnowledgeBases()

# Step 2: Query coding standards (REQUIRED)
QueryKnowledgeBases("coding-standards-KB", "Java-code-example-standards")

# Step 3: Query implementation patterns (REQUIRED)  
QueryKnowledgeBases("Java-premium-KB", "Java implementation patterns testing")

# Step 4: AWS service research (REQUIRED)
search_documentation("What is [AWS Service] and what are its key API operations?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")
```

**FAILURE TO COMPLETE KNOWLEDGE BASE CONSULTATION WILL RESULT IN INCORRECT CODE STRUCTURE**

## Purpose
Generate comprehensive test suites including unit tests, integration tests, and scenario tests using JUnit 5 and Mockito for AWS SDK for Java V2.

## Requirements
- **JUnit 5**: Use JUnit Jupiter for all tests
- **Mockito**: Mock AWS SDK clients for unit tests
- **Complete Data**: Use complete AWS data structures in tests
- **Test Groups**: Use JUnit tags for test categorization
- **Error Coverage**: Test all error conditions from specification

## File Structure
```
javav2/example_code/{service}/src/test/java/
├── {Service}ActionsTest.java       # Unit tests for actions
├── {Service}IntegrationTest.java   # Integration tests
└── {Service}ScenarioTest.java      # Scenario tests
```

## Maven Test Configuration

### Dependencies in pom.xml
```xml
<dependencies>
    <!-- AWS SDK -->
    <dependency>
        <groupId>software.amazon.awssdk</groupId>
        <artifactId>{service}</artifactId>
        <version>${aws.java.sdk.version}</version>
    </dependency>
    
    <!-- Test Dependencies -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>5.5.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-junit-jupiter</artifactId>
        <version>5.5.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.1.2</version>
            <configuration>
                <groups>!integration</groups>
            </configuration>
        </plugin>
    </plugins>
</build>

<profiles>
    <profile>
        <id>integration</id>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-surefire-plugin</artifactId>
                    <configuration>
                        <groups>integration</groups>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

## Unit Test Pattern
```java
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.{service};

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import software.amazon.awssdk.services.{service}.{Service}Client;
import software.amazon.awssdk.services.{service}.model.*;
import software.amazon.awssdk.core.exception.SdkException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class {Service}ActionsTest {

    @Mock
    private {Service}Client {service}Client;

    private {Service}Actions {service}Actions;

    @BeforeEach
    void setUp() {
        {service}Actions = new {Service}Actions();
    }

    @Test
    void test{ActionName}_Success() {
        // Arrange
        String testParam = "test-value";
        {ActionName}Response expectedResponse = {ActionName}Response.builder()
                .{responseField}("response-value")
                .build();

        when({service}Client.{actionName}(any({ActionName}Request.class)))
                .thenReturn(expectedResponse);

        // Act
        {ActionName}Response result = {service}Actions.{actionName}({service}Client, testParam);

        // Assert
        assertNotNull(result);
        assertEquals("response-value", result.{responseField}());
        
        verify({service}Client).{actionName}(any({ActionName}Request.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"BadRequestException", "InternalServerErrorException", "ResourceNotFoundException"})
    void test{ActionName}_ServiceException(String errorCode) {
        // Arrange
        String testParam = "test-value";
        {Service}Exception serviceException = ({Service}Exception) {Service}Exception.builder()
                .awsErrorDetails(AwsErrorDetails.builder()
                        .errorCode(errorCode)
                        .errorMessage("Test error message")
                        .build())
                .build();

        when({service}Client.{actionName}(any({ActionName}Request.class)))
                .thenThrow(serviceException);

        // Act & Assert
        {Service}Exception exception = assertThrows({Service}Exception.class, () -> 
                {service}Actions.{actionName}({service}Client, testParam));
        
        assertEquals(errorCode, exception.awsErrorDetails().errorCode());
        verify({service}Client).{actionName}(any({ActionName}Request.class));
    }

    @Test
    void test{ActionName}_SdkException() {
        // Arrange
        String testParam = "test-value";
        SdkException sdkException = SdkException.builder()
                .message("SDK error occurred")
                .build();

        when({service}Client.{actionName}(any({ActionName}Request.class)))
                .thenThrow(sdkException);

        // Act & Assert
        SdkException exception = assertThrows(SdkException.class, () -> 
                {service}Actions.{actionName}({service}Client, testParam));
        
        assertEquals("SDK error occurred", exception.getMessage());
        verify({service}Client).{actionName}(any({ActionName}Request.class));
    }
}
```

## Complete AWS Data Structures

### CRITICAL: Use Complete AWS Response Data
```java
// ❌ WRONG - Minimal data that fails validation
List<{Resource}> resources = List.of(
    {Resource}.builder()
        .{resourceId}("resource-1")
        .build()
);

// ✅ CORRECT - Complete AWS data structure
List<{Resource}> resources = List.of(
    {Resource}.builder()
        .{resourceId}("resource-1")
        .{resourceName}("test-resource")
        .{resourceArn}("arn:aws:service:region:account:resource/resource-1")
        .{resourceStatus}({ResourceStatus}.ACTIVE)
        .{createdAt}(Instant.now())
        .{updatedAt}(Instant.now())
        .{tags}(Map.of("Environment", "Test"))
        .build()
);
```

## Integration Test Pattern
```java
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.{service};

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.{service}.{Service}Client;
import software.amazon.awssdk.services.{service}.model.*;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
class {Service}IntegrationTest {

    private static {Service}Client {service}Client;
    private static {Service}Actions {service}Actions;
    private static String testResourceId;

    @BeforeAll
    static void setUp() {
        {service}Client = {Service}Client.builder()
                .region(Region.US_EAST_1)
                .build();
        {service}Actions = new {Service}Actions();
    }

    @AfterAll
    static void tearDown() {
        // Clean up test resources
        if (testResourceId != null) {
            try {
                {service}Actions.deleteResource({service}Client, testResourceId);
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }
        if ({service}Client != null) {
            {service}Client.close();
        }
    }

    @Test
    void testResourceLifecycle() {
        try {
            // Create resource
            testResourceId = {service}Actions.createResource({service}Client);
            assertNotNull(testResourceId);

            // Get resource
            {Resource} resource = {service}Actions.getResource({service}Client, testResourceId);
            assertNotNull(resource);
            assertEquals(testResourceId, resource.{resourceId}());

            // List resources (should include our test resource)
            List<{Resource}> resources = {service}Actions.listResources({service}Client);
            assertTrue(resources.stream()
                    .anyMatch(r -> testResourceId.equals(r.{resourceId}())));

        } catch (Exception e) {
            fail("Integration test failed: " + e.getMessage());
        }
    }

    @Test
    void testServiceConnectivity() {
        // Test basic service connectivity
        assertDoesNotThrow(() -> {
            List<{Resource}> resources = {service}Actions.listResources({service}Client);
            assertNotNull(resources);
        });
    }
}
```

## Scenario Test Pattern
```java
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.{service};

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.{service}.{Service}Client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
class {Service}ScenarioTest {

    private {Service}Client {service}Client;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        {service}Client = {Service}Client.builder()
                .region(Region.US_EAST_1)
                .build();
        
        // Capture System.out for testing
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        if ({service}Client != null) {
            {service}Client.close();
        }
    }

    @Test
    void testScenarioWithMockedInput() {
        // Mock user inputs for automated testing
        String simulatedInput = "n\nn\ny\n"; // No existing resource, no details, yes cleanup
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        // Run scenario
        assertDoesNotThrow(() -> {
            {Service}Scenario.main(new String[]{"us-east-1"});
        });

        // Verify output contains expected messages
        String output = outputStream.toString();
        assertTrue(output.contains("Welcome to the {AWS Service} basics scenario!"));
        assertTrue(output.contains("Setting up {AWS Service}"));
    }

    @Test
    void testScenarioWithExistingResources() {
        // Create a test resource first
        String testResourceId = null;
        try {
            {Service}Actions actions = new {Service}Actions();
            testResourceId = actions.createResource({service}Client);

            // Mock user inputs to use existing resource
            String simulatedInput = "y\nn\ny\n"; // Yes existing, no details, yes cleanup
            System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

            // Run scenario
            assertDoesNotThrow(() -> {
                {Service}Scenario.main(new String[]{"us-east-1"});
            });

            String output = outputStream.toString();
            assertTrue(output.contains("Found"));
            assertTrue(output.contains("existing resource"));

        } finally {
            // Clean up test resource
            if (testResourceId != null) {
                try {
                    new {Service}Actions().deleteResource({service}Client, testResourceId);
                } catch (Exception e) {
                    // Ignore cleanup errors
                }
            }
        }
    }
}
```

## Test Execution Commands

### Unit Tests Only
```bash
cd javav2/example_code/{service}
mvn test -Dgroups="!integration"
```

### Integration Tests Only
```bash
cd javav2/example_code/{service}
mvn test -Dgroups="integration"
```

### All Tests
```bash
cd javav2/example_code/{service}
mvn test
```

### Specific Test Class
```bash
mvn test -Dtest="{Service}ActionsTest"
```

## Test Requirements Checklist
- ✅ **JUnit 5 annotations** (@Test, @BeforeEach, @AfterEach)
- ✅ **Mockito for unit tests** (@Mock, @ExtendWith(MockitoExtension.class))
- ✅ **Complete AWS data structures** in all tests
- ✅ **Proper test tags** (@Tag("integration"))
- ✅ **Error condition coverage** per specification
- ✅ **Integration test cleanup** (try/finally blocks)
- ✅ **Region specification** (Region.US_EAST_1)
- ✅ **Resource lifecycle testing** (create, read, delete)
- ✅ **Parameterized tests** for multiple error conditions

## Test Categories

### Unit Tests
- ✅ **Mock AWS clients** using Mockito
- ✅ **Test individual methods** in isolation
- ✅ **Cover success and error cases**
- ✅ **Fast execution** (no real AWS calls)

### Integration Tests
- ✅ **Use real AWS clients** and services
- ✅ **Test complete workflows** end-to-end
- ✅ **Require AWS credentials** and permissions
- ✅ **Include cleanup logic** to avoid resource leaks

### Scenario Tests
- ✅ **Test complete scenarios** with mocked user input
- ✅ **Verify console output** and user interactions
- ✅ **Test multiple user paths** (existing resources, new resources)
- ✅ **Integration test category** (requires real AWS)

## Common Test Failures to Avoid
- ❌ Using incomplete AWS data structures in mocks
- ❌ Missing test tags for integration tests
- ❌ Not handling cleanup in integration tests
- ❌ Forgetting to set AWS region in test clients
- ❌ Not testing all error conditions from specification
- ❌ Not mocking user inputs in scenario tests
- ❌ Missing Maven test configuration
- ❌ Not using JUnit 5 annotations properly