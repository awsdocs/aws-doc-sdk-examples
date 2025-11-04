# PHP Interactive Scenario Generation

## MANDATORY: Knowledge Base Consultation (FIRST STEP)
**🚨 CRITICAL - Must be completed BEFORE any code generation**

```bash
# Step 1: List available knowledge bases
ListKnowledgeBases()

# Step 2: Query coding standards (REQUIRED)
QueryKnowledgeBases("coding-standards-KB", "PHP-code-example-standards")

# Step 3: Query implementation patterns (REQUIRED)  
QueryKnowledgeBases("PHP-premium-KB", "PHP implementation patterns structure")

# Step 4: AWS service research (REQUIRED)
search_documentation("What is [AWS Service] and what are its key API operations?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")
```

**FAILURE TO COMPLETE KNOWLEDGE BASE CONSULTATION WILL RESULT IN INCORRECT CODE STRUCTURE**

## Purpose
Generate interactive scenarios that demonstrate complete workflows using multiple service operations in a guided, educational manner. Implementation must be based on the service SPECIFICATION.md file.

## Requirements
- **Specification-Driven**: MUST read the `scenarios/basics/{service}/SPECIFICATION.md`
- **Interactive**: Use `testable_readline()` for user input and clear output
- **Educational**: Break complex workflows into logical phases
- **Comprehensive**: Cover setup, demonstration, examination, and cleanup
- **Error Handling**: Graceful error handling with user-friendly messages
- **Service Classes**: MUST use service wrapper classes for all operations
- **Runner Pattern**: MUST implement interactive Runner.php pattern

## File Structure
```
example_code/{service}/
├── Runner.php                  # Interactive menu runner
├── {Service}Service.php        # Service wrapper class
├── composer.json
├── README.md
└── tests/
    ├── {Service}Test.php
    └── phpunit.xml
```## MA
NDATORY Pre-Implementation Steps

### Step 1: Read Service Specification
**CRITICAL**: Always read `scenarios/basics/{service}/SPECIFICATION.md` first to understand:
- **API Actions Used**: Exact operations to implement
- **Proposed Example Structure**: Setup, demonstration, examination, cleanup phases
- **Error Handling**: Specific error codes and handling requirements
- **Scenario Flow**: Step-by-step workflow description

### Step 2: Extract Implementation Requirements
From the specification, identify:
- **Setup Phase**: What resources need to be created/configured
- **Demonstration Phase**: What operations to demonstrate
- **Examination Phase**: What data to display and how to filter/analyze
- **Cleanup Phase**: What resources to clean up and user options

## Runner Pattern Structure
**MANDATORY for most services:**

```php
<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

require 'vendor/autoload.php';

use {Service}\{Service}Service;

echo "Welcome to the {AWS Service} examples!\n";
echo "Choose an option:\n";
echo "1. Hello {Service}\n";
echo "2. Run {Service} Basics Scenario\n";
echo "3. List {Resources}\n";
echo "4. Create {Resource}\n";
echo "5. Delete {Resource}\n";
echo "0. Exit\n";

$choice = testable_readline("Enter your choice: ");

switch ($choice) {
    case '1':
        require 'Hello{Service}.php';
        break;
    case '2':
        $service = new {Service}Service();
        runBasicsScenario($service);
        break;
    case '3':
        $service = new {Service}Service();
        $service->list{Resources}();
        break;
    case '4':
        $service = new {Service}Service();
        $resourceName = testable_readline("Enter resource name: ");
        $service->create{Resource}($resourceName);
        break;
    case '5':
        $service = new {Service}Service();
        $resourceId = testable_readline("Enter resource ID: ");
        $service->delete{Resource}($resourceId);
        break;
    case '0':
        echo "Goodbye!\n";
        break;
    default:
        echo "Invalid choice\n";
}
```

## Scenario Phase Structure (Based on Specification)

### Setup Phase
- **Read specification Setup section** for exact requirements
- Check for existing resources as specified
- Create necessary resources using service methods
- Configure service settings per specification
- Verify setup completion as described

### Demonstration Phase  
- **Follow specification Demonstration section** exactly
- Perform core service operations using service methods
- Generate sample data if specified in the specification
- Show service capabilities as outlined
- Provide educational context from specification

### Examination Phase
- **Implement specification Examination section** requirements
- List and examine results using service methods
- Filter and analyze data as specified
- Display detailed information per specification format
- Allow user interaction as described in specification

### Cleanup Phase
- **Follow specification Cleanup section** guidance
- Offer cleanup options with warnings from specification
- Handle cleanup errors gracefully using service methods
- Provide alternative management options as specified
- Confirm completion per specification

## User Interaction Patterns

### Input Handling
```php
// Yes/No questions
$useExisting = testable_readline("Use existing resource? (y/n): ");
$isYes = strtolower($useExisting) === 'y';

// Text input
$resourceName = testable_readline("Enter resource name: ");

// Numeric input with validation
do {
    $count = testable_readline("How many items? ");
} while (!is_numeric($count) || $count < 1);
```

### Information Display
```php
// Progress indicators
echo "✓ Operation completed successfully\n";
echo "⚠ Warning message\n";
echo "✗ Error occurred\n";

// Formatted output
echo str_repeat('-', 60) . "\n";
echo "Found " . count($items) . " items:\n";
foreach ($items as $item) {
    echo "  • {$item['name']}\n";
}
```

## Runner Requirements
- ✅ **ALWAYS** read and implement based on `scenarios/basics/{service}/SPECIFICATION.md`
- ✅ **ALWAYS** provide interactive menu for multiple examples
- ✅ **ALWAYS** include hello scenario as first option
- ✅ **ALWAYS** handle user input gracefully using `testable_readline()`
- ✅ **ALWAYS** include proper error handling
- ✅ **ALWAYS** provide clear output and feedback
- ✅ **ALWAYS** use service wrapper classes for all AWS operations
- ✅ **ALWAYS** implement proper cleanup in finally block
- ✅ **ALWAYS** break scenario into logical phases per specification
- ✅ **ALWAYS** include error handling per specification's Errors section
- ✅ **ALWAYS** provide educational context and explanations from specification