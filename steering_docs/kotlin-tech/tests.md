# Kotlin Test Generation

## MANDATORY: Knowledge Base Consultation (FIRST STEP)
**🚨 CRITICAL - Must be completed BEFORE any code generation**

```bash
# Step 1: List available knowledge bases
ListKnowledgeBases()

# Step 2: Query coding standards (REQUIRED)
QueryKnowledgeBases("coding-standards-KB", "Kotlin-code-example-standards")

# Step 3: Query implementation patterns (REQUIRED)  
QueryKnowledgeBases("Kotlin-premium-KB", "Kotlin implementation patterns testing")

# Step 4: AWS service research (REQUIRED)
search_documentation("What is [AWS Service] and what are its key API operations?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")
```

**FAILURE TO COMPLETE KNOWLEDGE BASE CONSULTATION WILL RESULT IN INCORRECT CODE STRUCTURE**

## Purpose
Generate comprehensive test suites including unit tests, integration tests, and scenario tests using JUnit 5 and MockK for AWS SDK for Kotlin.

## Requirements
- **JUnit 5**: Use JUnit Jupiter for all tests
- **MockK**: Mock AWS SDK clients for unit tests
- **Coroutines Testing**: Use kotlinx-coroutines-test for suspend function testing
- **Complete Data**: Use complete AWS data structures in tests
- **Test Tags**: Use JUnit tags for test categorization
- **Error Coverage**: Test all error conditions from specification

## File Structure
```
kotlin/services/{service}/src/test/kotlin/
├── {Service}ActionsTest.kt       # Unit tests for actions
├── {Service}IntegrationTest.kt   # Integration tests
└── {Service}ScenarioTest.kt      # Scenario tests
```

## Gradle Test Configuration

### Dependencies in build.gradle.kts
```kotlin
plugins {
    kotlin("jvm") version "1.9.10"
    application
}

dependencies {
    // AWS SDK
    implementation("aws.sdk.kotlin:{service}:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    
    // Test Dependencies
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
}

tasks.test {
    useJUnitPlatform()
    exclude("**/*IntegrationTest*")
}

tasks.register<Test>("integrationTest") {
    useJUnitPlatform()
    include("**/*IntegrationTest*")
    group = "verification"
    description = "Runs integration tests"
}
```

## Unit Test Pattern
```kotlin
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.{service}

import aws.sdk.kotlin.services.{service}.{Service}Client
import aws.sdk.kotlin.services.{service}.model.*
import aws.smithy.kotlin.runtime.ServiceException
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

@Tag("unit")
class {Service}ActionsTest {

    private lateinit var mockClient: {Service}Client
    private lateinit var {service}Actions: {Service}Actions

    @BeforeEach
    fun setUp() {
        mockClient = mockk()
        {service}Actions = {Service}Actions()
    }

    @Test
    fun `test {actionName} success`() = runTest {
        // Arrange
        val testParam = "test-value"
        val expectedResponse = {ActionName}Response {
            {responseField} = "response-value"
        }

        coEvery { mockClient.{actionName}(any<{ActionName}Request>()) } returns expectedResponse

        // Act
        val result = {service}Actions.{actionName}(mockClient, testParam)

        // Assert
        assertNotNull(result)
        assertEquals("response-value", result.{responseField})
        
        coVerify { mockClient.{actionName}(any<{ActionName}Request>()) }
    }

    @ParameterizedTest
    @ValueSource(strings = ["BadRequestException", "InternalServerErrorException", "ResourceNotFoundException"])
    fun `test {actionName} service exception`(errorCode: String) = runTest {
        // Arrange
        val testParam = "test-value"
        val serviceException = {Service}Exception.builder {
            message = "Test error message"
        }.build()

        coEvery { mockClient.{actionName}(any<{ActionName}Request>()) } throws serviceException

        // Act & Assert
        assertFailsWith<{Service}Exception> {
            {service}Actions.{actionName}(mockClient, testParam)
        }
        
        coVerify { mockClient.{actionName}(any<{ActionName}Request>()) }
    }

    @Test
    fun `test {actionName} general exception`() = runTest {
        // Arrange
        val testParam = "test-value"
        val exception = RuntimeException("General error")

        coEvery { mockClient.{actionName}(any<{ActionName}Request>()) } throws exception

        // Act & Assert
        assertFailsWith<RuntimeException> {
            {service}Actions.{actionName}(mockClient, testParam)
        }
        
        coVerify { mockClient.{actionName}(any<{ActionName}Request>()) }
    }

    @Test
    fun `test list{Resources} with pagination`() = runTest {
        // Arrange
        val page1Response = List{Resources}Response {
            {resources} = listOf(
                {Resource} {
                    {resourceId} = "resource-1"
                    {resourceName} = "test-resource-1"
                },
                {Resource} {
                    {resourceId} = "resource-2"
                    {resourceName} = "test-resource-2"
                }
            )
            nextToken = "token-1"
        }
        
        val page2Response = List{Resources}Response {
            {resources} = listOf(
                {Resource} {
                    {resourceId} = "resource-3"
                    {resourceName} = "test-resource-3"
                }
            )
            nextToken = null
        }

        coEvery { mockClient.list{Resources}(match<List{Resources}Request> { it.nextToken == null }) } returns page1Response
        coEvery { mockClient.list{Resources}(match<List{Resources}Request> { it.nextToken == "token-1" }) } returns page2Response

        // Act
        val result = {service}Actions.list{Resources}(mockClient)

        // Assert
        assertEquals(3, result.size)
        assertEquals("resource-1", result[0].{resourceId})
        assertEquals("resource-2", result[1].{resourceId})
        assertEquals("resource-3", result[2].{resourceId})
        
        coVerify(exactly = 2) { mockClient.list{Resources}(any<List{Resources}Request>()) }
    }
}
```

## Complete AWS Data Structures

### CRITICAL: Use Complete AWS Response Data
```kotlin
// ❌ WRONG - Minimal data that fails validation
val resources = listOf(
    {Resource} {
        {resourceId} = "resource-1"
    }
)

// ✅ CORRECT - Complete AWS data structure
val resources = listOf(
    {Resource} {
        {resourceId} = "resource-1"
        {resourceName} = "test-resource"
        {resourceArn} = "arn:aws:service:region:account:resource/resource-1"
        {resourceStatus} = {ResourceStatus}.Active
        {createdAt} = aws.smithy.kotlin.runtime.time.Instant.now()
        {updatedAt} = aws.smithy.kotlin.runtime.time.Instant.now()
        {tags} = mapOf("Environment" to "Test")
    }
)
```

## Integration Test Pattern
```kotlin
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.{service}

import aws.sdk.kotlin.services.{service}.{Service}Client
import aws.sdk.kotlin.services.{service}.model.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.AfterAll
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertEquals

@Tag("integration")
class {Service}IntegrationTest {

    companion object {
        private lateinit var {service}Client: {Service}Client
        private lateinit var {service}Actions: {Service}Actions
        private var testResourceId: String? = null

        @BeforeAll
        @JvmStatic
        fun setUp() {
            {service}Client = {Service}Client {
                region = "us-east-1"
            }
            {service}Actions = {Service}Actions()
        }

        @AfterAll
        @JvmStatic
        fun tearDown() = runTest {
            // Clean up test resources
            testResourceId?.let { resourceId ->
                try {
                    {service}Actions.deleteResource({service}Client, resourceId)
                } catch (e: Exception) {
                    // Ignore cleanup errors
                }
            }
            {service}Client.close()
        }
    }

    @Test
    fun `test resource lifecycle`() = runTest {
        try {
            // Create resource
            testResourceId = {service}Actions.createResource({service}Client, "test-resource")
            assertNotNull(testResourceId)

            // Get resource
            val resource = {service}Actions.getResource({service}Client, testResourceId!!)
            assertNotNull(resource)
            assertEquals(testResourceId, resource.{resourceId})

            // List resources (should include our test resource)
            val resources = {service}Actions.listResources({service}Client)
            assertTrue(resources.any { it.{resourceId} == testResourceId })

        } catch (e: Exception) {
            throw AssertionError("Integration test failed: ${e.message}", e)
        }
    }

    @Test
    fun `test service connectivity`() = runTest {
        // Test basic service connectivity
        val resources = {service}Actions.listResources({service}Client)
        assertNotNull(resources)
    }
}
```

## Scenario Test Pattern
```kotlin
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.{service}

import aws.sdk.kotlin.services.{service}.{Service}Client
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertTrue

@Tag("integration")
class {Service}ScenarioTest {

    private lateinit var {service}Client: {Service}Client
    private lateinit var outputStream: ByteArrayOutputStream
    private lateinit var originalOut: PrintStream

    @BeforeEach
    fun setUp() {
        {service}Client = {Service}Client {
            region = "us-east-1"
        }
        
        // Capture System.out for testing
        outputStream = ByteArrayOutputStream()
        originalOut = System.out
        System.setOut(PrintStream(outputStream))
    }

    @AfterEach
    fun tearDown() = runTest {
        System.setOut(originalOut)
        {service}Client.close()
    }

    @Test
    fun `test scenario with mocked input`() = runTest {
        // Mock user inputs for automated testing
        val simulatedInput = "n\nn\ny\n" // No existing resource, no details, yes cleanup
        System.setIn(ByteArrayInputStream(simulatedInput.toByteArray()))

        // Run scenario
        try {
            // Assuming main function exists in {Service}Basics or similar
            main(arrayOf("us-east-1"))
        } catch (e: Exception) {
            throw AssertionError("Scenario test failed: ${e.message}", e)
        }

        // Verify output contains expected messages
        val output = outputStream.toString()
        assertTrue(output.contains("Welcome to the {AWS Service} basics scenario!"))
        assertTrue(output.contains("Setting up {AWS Service}"))
    }

    @Test
    fun `test scenario with existing resources`() = runTest {
        // Create a test resource first
        var testResourceId: String? = null
        try {
            val actions = {Service}Actions()
            testResourceId = actions.createResource({service}Client, "test-resource")

            // Mock user inputs to use existing resource
            val simulatedInput = "y\nn\ny\n" // Yes existing, no details, yes cleanup
            System.setIn(ByteArrayInputStream(simulatedInput.toByteArray()))

            // Run scenario
            main(arrayOf("us-east-1"))

            val output = outputStream.toString()
            assertTrue(output.contains("Found"))
            assertTrue(output.contains("existing resource"))

        } finally {
            // Clean up test resource
            testResourceId?.let { resourceId ->
                try {
                    {Service}Actions().deleteResource({service}Client, resourceId)
                } catch (e: Exception) {
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
cd kotlin/services/{service}
./gradlew test --exclude-task integrationTest
```

### Integration Tests Only
```bash
cd kotlin/services/{service}
./gradlew integrationTest
```

### All Tests
```bash
cd kotlin/services/{service}
./gradlew test integrationTest
```

### Specific Test Class
```bash
./gradlew test --tests "{Service}ActionsTest"
```

## Coroutines Testing

### Using runTest
```kotlin
@Test
fun `test suspend function`() = runTest {
    // Test suspend functions here
    val result = suspendingFunction()
    assertNotNull(result)
}
```

### Testing Coroutine Scope
```kotlin
@Test
fun `test with custom scope`() = runTest {
    val testScope = TestScope()
    
    testScope.launch {
        // Test coroutine operations
    }
    
    testScope.advanceUntilIdle()
}
```

## Test Requirements Checklist
- ✅ **JUnit 5 annotations** (@Test, @BeforeEach, @AfterEach)
- ✅ **MockK for unit tests** (mockk(), coEvery, coVerify)
- ✅ **Coroutines testing** (runTest, TestScope)
- ✅ **Complete AWS data structures** in all tests
- ✅ **Proper test tags** (@Tag("integration"))
- ✅ **Error condition coverage** per specification
- ✅ **Integration test cleanup** (try/finally blocks)
- ✅ **Region specification** ("us-east-1")
- ✅ **Resource lifecycle testing** (create, read, delete)
- ✅ **Parameterized tests** for multiple error conditions

## Test Categories

### Unit Tests
- ✅ **Mock AWS clients** using MockK
- ✅ **Test individual suspend functions** in isolation
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
- ❌ Missing Gradle test configuration
- ❌ Not using runTest for suspend function tests
- ❌ Incorrect MockK usage (coEvery vs every)

## Kotlin-Specific Testing Features

### Suspend Function Testing
```kotlin
@Test
fun `test suspend function`() = runTest {
    val result = suspendFunction()
    assertNotNull(result)
}
```

### Coroutine Exception Testing
```kotlin
@Test
fun `test coroutine exception`() = runTest {
    assertFailsWith<ServiceException> {
        suspendFunctionThatThrows()
    }
}
```

### Extension Function Testing
```kotlin
@Test
fun `test extension function`() {
    val resources = listOf(/* test data */)
    val filtered = resources.filterActive()
    assertEquals(expectedCount, filtered.size)
}
```