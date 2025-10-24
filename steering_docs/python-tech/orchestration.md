# Python Code Generation Orchestration

## Purpose
Coordinate the modular components to generate complete AWS SDK code examples. Each component can be used independently or in sequence.

## Component Dependencies

```mermaid
graph TD
    A[Knowledge Base Consultation] --> B[Hello Example]
    A --> C[Wrapper Class]
    A --> D[Scenario]
    
    C --> E[Tests - Stubber Creation]
    E --> F[Tests - Unit Tests]
    E --> G[Tests - Integration Tests]
    E --> H[Tests - Scenario Tests]
    
    B --> I[Metadata Generation]
    C --> I
    D --> I
    
    I --> J[README Generation]
    
    K[Service Specification] --> I
    K --> C
    K --> D
```

## Execution Workflows

### Full Service Implementation
Complete implementation of a new AWS service:

```bash
# 1. Knowledge Base Consultation (MANDATORY FIRST)
# Use ListKnowledgeBases + QueryKnowledgeBases for standards and patterns

# 2. Create Service Stubber (MANDATORY BEFORE TESTS)
# Create python/test_tools/{service}_stubber.py
# Add to python/test_tools/stubber_factory.py

# 3. Generate Core Components
# - Hello example: {service}_hello.py
# - Wrapper class: {service}_wrapper.py  
# - Scenario: scenario_{service}_basics.py
# - Requirements: requirements.txt

# 4. Generate Test Suite
# - conftest.py with ScenarioData
# - test_{service}_wrapper.py
# - test_{service}_scenario.py
# - test_{service}_integration.py

# 5. Generate Metadata
# - Read service specification for exact metadata keys
# - Create .doc_gen/metadata/{service}_metadata.yaml

# 6. Generate Documentation
# - Run writeme tool to create/update README.md
```

### Individual Component Updates

#### Update Hello Example Only
```bash
# Focus: hello.md guidance
# Files: {service}_hello.py
# Validation: Run hello example, check output
```

#### Update Wrapper Class Only  
```bash
# Focus: wrapper.md guidance
# Files: {service}_wrapper.py
# Validation: Run unit tests for wrapper methods
```

#### Update Scenario Only
```bash
# Focus: scenario.md guidance  
# Files: scenario_{service}_basics.py
# Validation: Run scenario tests, check user interaction
```

#### Update Tests Only
```bash
# Focus: tests.md guidance
# Files: All test files in test/ directory
# Validation: Run pytest with all markers
```

#### Update Metadata Only
```bash
# Focus: metadata.md guidance
# Files: .doc_gen/metadata/{service}_metadata.yaml
# Validation: Run writeme tool validation
```

#### Update Documentation Only
```bash
# Focus: readme.md guidance
# Files: README.md (generated)
# Validation: Check README completeness and accuracy
```

## Quality Gates

### Component-Level Validation
Each component has specific validation requirements:

#### Hello Example Validation
```bash
PYTHONPATH=python:python/example_code/{service} python python/example_code/{service}/{service}_hello.py
```

#### Wrapper Class Validation
```bash
PYTHONPATH=python:python/example_code/{service} python -c "from {service}_wrapper import {Service}Wrapper; wrapper = {Service}Wrapper.from_client(); print('✅ Wrapper functions working')"
```

#### Scenario Validation
```bash
PYTHONPATH=python:python/example_code/{service} python python/example_code/{service}/scenario_{service}_basics.py
```

#### Test Validation
```bash
PYTHONPATH=python:python/example_code/{service} python -m pytest python/example_code/{service}/test/ -m "not integ" -v
PYTHONPATH=python:python/example_code/{service} python -m pytest python/example_code/{service}/test/ -m "integ" -v
```

#### Code Quality Validation
```bash
black python/example_code/{service}/
pylint --rcfile=.github/linters/.python-lint python/example_code/{service}/
```

#### Documentation Validation
```bash
cd .tools/readmes
source .venv/bin/activate
python -m writeme --languages Python:3 --services {service}
```

### Integration Validation
Full integration testing across all components:

```bash
# 1. All unit tests pass
python -m pytest python/example_code/{service}/test/ -m "not integ" -v

# 2. All integration tests pass
python -m pytest python/example_code/{service}/test/ -m "integ" -v

# 3. All examples execute successfully
PYTHONPATH=python:python/example_code/{service} python python/example_code/{service}/{service}_hello.py
PYTHONPATH=python:python/example_code/{service} python python/example_code/{service}/scenario_{service}_basics.py

# 4. Code quality passes
black python/example_code/{service}/
pylint --rcfile=.github/linters/.python-lint python/example_code/{service}/

# 5. Documentation generates successfully
cd .tools/readmes && source .venv/bin/activate && python -m writeme --languages Python:3 --services {service}
```

## Component Selection Guide

### When to Use Individual Components

#### Hello Example Only
- Quick service introduction needed
- Testing basic service connectivity
- Creating minimal working example

#### Wrapper Class Only
- Need reusable service operations
- Building foundation for other examples
- Focusing on error handling patterns

#### Scenario Only
- Demonstrating complete workflows
- Educational/tutorial content
- Interactive user experiences

#### Tests Only
- Improving test coverage
- Adding new test cases
- Fixing test infrastructure

#### Metadata Only
- Documentation pipeline integration
- Updating snippet references
- Fixing metadata validation errors

#### Documentation Only
- README updates needed
- Documentation refresh
- Link validation and updates

### When to Use Full Workflow
- New service implementation
- Complete service overhaul
- Major structural changes
- Initial service setup

## Error Recovery

### Component Failure Handling
If any component fails, you can:

1. **Fix and retry** the specific component
2. **Skip and continue** with other components
3. **Rollback changes** and restart from known good state

### Common Recovery Scenarios

#### Test Failures
```bash
# Fix test issues and re-run
python -m pytest python/example_code/{service}/test/ -v --tb=short
```

#### Metadata Validation Failures
```bash
# Check metadata syntax
python -c "import yaml; yaml.safe_load(open('.doc_gen/metadata/{service}_metadata.yaml'))"

# Validate against specification
# Compare with scenarios/basics/{service}/SPECIFICATION.md
```

#### Documentation Generation Failures
```bash
# Check for missing dependencies
cd .tools/readmes && source .venv/bin/activate && pip list

# Validate metadata first
python -m writeme --languages Python:3 --services {service} --verbose
```

This modular approach allows for targeted updates, easier debugging, and more maintainable code generation processes.