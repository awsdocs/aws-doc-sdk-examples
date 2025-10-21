# JavaScript Technology Stack & Build System

## JavaScript/Node.js Development Environment

### Build Tools & Dependencies
- **Runtime**: Node.js (LTS version recommended)
- **Package Manager**: npm
- **Testing Framework**: Jest
- **Code Formatting**: Prettier
- **Linting**: Biome (or ESLint)
- **SDK Version**: AWS SDK for JavaScript v3

### Common Build Commands

```bash
# Dependencies
npm install                        # Install dependencies
npm ci                            # Clean install from package-lock.json

# Testing
npm test                          # Run all tests
npm run test:unit                 # Run unit tests
npm run test:integration          # Run integration tests

# Code Quality
npm run lint                      # Lint code
npm run format                    # Format code with Prettier

# Execution
node src/hello-{service}.js       # Run hello scenario
npm start                         # Run main application
```

### JavaScript-Specific Pattern Requirements

#### File Naming Conventions
- Use kebab-case for file names
- Service prefix pattern: `{service}-action.js` (e.g., `s3-list-buckets.js`)
- Hello scenarios: `hello-{service}.js` (e.g., `hello-s3.js`)
- Test files: `{service}-action.test.js`

#### Hello Scenario Structure
- **File naming**: `hello-{service}.js` or hello function in main module
- **Function structure**: Async function as main entry point
- **Documentation**: Include JSDoc comments explaining the hello example purpose

#### Code Structure Standards
- **Module system**: Use ES6 modules (import/export) or CommonJS (require/module.exports)
- **Function naming**: Use camelCase for function names
- **Constants**: Use UPPER_SNAKE_CASE for constants
- **Classes**: Use PascalCase for class names
- **Async/Await**: Use async/await for asynchronous operations

#### Error Handling Patterns
```javascript
import { S3Client, ListBucketsCommand } from "@aws-sdk/client-s3";

const client = new S3Client({ region: "us-east-1" });

async function listBuckets() {
    try {
        const command = new ListBucketsCommand({});
        const response = await client.send(command);
        
        console.log("Buckets:", response.Buckets);
        return response.Buckets;
    } catch (error) {
        if (error.name === "NoSuchBucket") {
            console.error("Bucket not found:", error.message);
        } else if (error.name === "AccessDenied") {
            console.error("Access denied:", error.message);
        } else {
            console.error("AWS SDK Error:", error.message);
        }
        throw error;
    }
}

export { listBuckets };
```

#### Testing Standards
- **Test framework**: Use Jest with appropriate matchers
- **Integration tests**: Mark with appropriate test descriptions
- **Async testing**: Use async/await in test functions
- **Mocking**: Use Jest mocks for unit tests when appropriate
- **Test naming**: Use descriptive test names explaining test purpose

#### Project Structure
```
src/
├── hello-{service}.js
├── {service}-actions.js
├── {service}-scenarios.js
└── tests/
    ├── {service}-actions.test.js
    └── {service}-integration.test.js
```

#### Package.json Configuration
```json
{
  "name": "{service}-examples",
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "test": "jest",
    "test:unit": "jest --testPathPattern=unit",
    "test:integration": "jest --testPathPattern=integration",
    "lint": "biome check .",
    "format": "prettier --write ."
  },
  "dependencies": {
    "@aws-sdk/client-{service}": "^3.0.0",
    "@aws-sdk/credential-providers": "^3.0.0"
  },
  "devDependencies": {
    "jest": "^29.0.0",
    "prettier": "^3.0.0",
    "@biomejs/biome": "^1.0.0"
  }
}
```

#### Documentation Requirements
- **JSDoc comments**: Use `/**` for function and class documentation
- **Parameter documentation**: Document parameters with `@param`
- **Return documentation**: Document return values with `@returns`
- **Example documentation**: Include `@example` blocks
- **README sections**: Include npm setup and execution instructions

### AWS SDK v3 Specific Patterns

#### Client Configuration
```javascript
import { S3Client } from "@aws-sdk/client-s3";
import { fromEnv } from "@aws-sdk/credential-providers";

const client = new S3Client({
    region: process.env.AWS_REGION || "us-east-1",
    credentials: fromEnv(), // Optional: explicit credential provider
});
```

#### Command Pattern Usage
```javascript
import { S3Client, PutObjectCommand } from "@aws-sdk/client-s3";

const client = new S3Client({ region: "us-east-1" });

async function uploadObject(bucketName, key, body) {
    const command = new PutObjectCommand({
        Bucket: bucketName,
        Key: key,
        Body: body,
    });
    
    return await client.send(command);
}
```

### Language-Specific Pattern Errors to Avoid
- ❌ **NEVER use snake_case for JavaScript identifiers**
- ❌ **NEVER forget to handle Promise rejections**
- ❌ **NEVER mix callback and Promise patterns**
- ❌ **NEVER ignore proper error handling for AWS operations**
- ❌ **NEVER skip npm dependency management**

### Best Practices
- ✅ **ALWAYS use kebab-case for file names**
- ✅ **ALWAYS use camelCase for JavaScript identifiers**
- ✅ **ALWAYS use async/await for asynchronous operations**
- ✅ **ALWAYS include proper error handling for AWS service calls**
- ✅ **ALWAYS use AWS SDK v3 command pattern**
- ✅ **ALWAYS include comprehensive JSDoc documentation**
- ✅ **ALWAYS handle environment variables for configuration**

### Environment Configuration
- **AWS Region**: Use `AWS_REGION` environment variable
- **Credentials**: Support AWS credential chain (environment, profile, IAM roles)
- **Configuration**: Use environment variables for service-specific settings

### Integration with Knowledge Base
Before creating JavaScript code examples:
1. Query `coding-standards-KB` for "JavaScript-code-example-standards"
2. Query `JavaScript-premium-KB` for "JavaScript implementation patterns"
3. Follow KB-documented patterns for project structure and module organization
4. Validate against existing JavaScript examples only after KB consultation