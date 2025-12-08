<details>
  <summary><h2>Program Flow and Readability</h2></summary>

### Guidelines
* Code examples should prioritize educational value and readability:
* Examples should not include code that only duplicates an API reference without additional information or context. Examples should do _something_ with response objects that contain information specific to the call.
* Function and variable names must be clear and free of typos, with consistent capitalization and naming patterns.
* Complex logic should include descriptive comments.
* Examples must not include unreacheable code, code that does not compile or run, or code that is commented out.
* Prefer to create files as part of the examples to limit including multiple "[example].txt" files throughout the repository.
* When constructing SQL queries, use parameterized values instead of directly concatenating commands with user input.
* AWS Console setup should not be required in order for the example to run. Any setup should happen as part of a CFN or CDK template or script, or in the program itself. The only exception is for "feature access", such as enabling access to a Bedrock model.
* Provide customers with appropriate context in published documentation through metadata and snippet organization.

### Specification Instructions
* Describe the program flow and instructions for readability, file creation, and CFN or CDK resource deployment. 
</details>

<details>
  <summary><h2>Code Comments</h2></summary>

### Guidelines
* Code comments must be descriptive, use complete sentences (with punctuation), and be free of typos or grammatical errors. 
* Language owners may establish their own patterns for comments (such as parameter and method descriptions) and should follow those patterns consistently.

### Specification Instructions
* Describe any important comments that should be included in all implementations.
</details>

<details>
  <summary><h2>Pagination</h2></summary>

### Guidelines
* When pagination is available (determined by checking the service client for paginators), the available paginator must be used. 
* In cases where a subset of items is intentionally fetched, it should be noted in the code comments.
* If the intent is listing "all" items, pagination should be included to return all pages of data.
* Hello Service examples should still demonstrate pagination if available, but can log the total count of items (along with a subset of the list, if desired) instead of listing all items.

### Specification Instructions
* Indicate where pagination is required.
</details>

<details>
  <summary><h2>Waiters</h2></summary>

### Guidelines
* When a waiter is available, it should be used in the example. 
* If no waiter is available, the program should poll for the applicable status before continuing. 
* If a sleep() function is used, a descriptive comment must be included explaining why.

### Specification Instructions
* Indicate where a waiter is required.
</details>

<details>
  <summary><h2>Error Handling</h2></summary>

### Guidelines
* Each discrete service call with specific exceptions (such as "resource not found" or "resource already exists") should follow the appropriate spec to handle the error gracefully.
  * If a spec is not available for an API call, exceptions should be extended with additional information about the action that the API call is being made for, and then raised as appropriate for the language.
* Examples should not break/quit for their exception handling unless there is a reason to do so, so that resources can be cleaned up gracefully.

### Specification Instructions
* Each discrete service call with specific exceptions (such as "resource not found" or "resource already exists") should have an appropriate action to inform the user.
  * **Examples:**
  * A call to Create a resource that results in Resource Exists
    * If the scenario can continue with any resource having the same name, the scenario spec may opt to inform the user and continue.
    * If the scenario must have the specific resource it attempted to create, the scenario spec should inform the user with an error and finish (possibly without cleanup).
  * A call to Describe resource that does not exist
    * The scenario should warn the user that the resource does not exist, and may either continue execution if the resource is optional to the completion of the scenario or may skip to clean up of created resources if the resource was critical to the completion of the scenario.
* Some SDKs have modeled exceptions, while others have generic exceptions with string error codes and messages. The latter can be especially confusing to users, and scenarios should take care to properly identify either type of exception.
</details>

<details>
  <summary><h2>Resource Strings</h2></summary>

### Guidelines
* Resource ARNs or names should either be entered by the user, provided as program arguments, or loaded from a separate configuration. 
* Code should not use hard-coded strings or "<REPLACE_THIS_STRING>" placeholders to access necessary resources. 
* Examples should not use hard-coded Regions unless necessary for the example.

### Specification Instructions
* Specify how program arguments should be loaded.
</details>

<details>
  <summary><h2>S3 Bucket Names</h2></summary>

### Guidelines
* When creating a new bucket
  * A user provided prefix for bucket names is required. If a prefix cannot be found, the example should exit without creating a bucket.
  * Assume the existence of an environment variable, `S3_BUCKET_NAME_PREFIX`, that can be used if needed.
  * The bucket prefix is postfixed with a unique id before use. e.g. `${S3_BUCKET_NAME_PREFIX}-${uuid()}`
* When referencing an existing bucket
  * Access existing bucket names in whatever fashion is most appropriate, just don’t use `S3_BUCKET_NAME_PREFIX`
* Integration tests
  * The same rules for new buckets apply to integration tests. If a user chooses to run our tests, but does not provide `S3_BUCKET_NAME_PREFIX`, the tests should fail.

### Specification Instructions
* Specifications should identify places where use of the `S3_BUCKET_NAME_PREFIX` environment variable is required.

</details>

<details>
  <summary><h2>Security (Username/Passwords)</h2></summary>

### Guidelines
* User names and passwords or other security artifacts should be entered by the user and not referenced as hard-coded strings. 
* They should not be stored or retained, and only pass through to the necessary service, in cases such as Cognito user setup or RDS admin setup actions which require a password.

### Specification Instructions
* Describe any special handling for security items.
</details>

<details>
  <summary><h2>Test Coverage</h2></summary>

### Guidelines
* New code should have test coverage (can be unit or integration) for each method or logical operation block. 
* Refer to the SDK language specification page for test tool details.

### Specification Instructions
* Follow general guidance for testing, no additional specification requirements.
</details>

<details>
  <summary><h2>Configuration Explanations</h2></summary>

### Guidelines
* If any user configuration, program Args, or other setup is required, they should be described in the code comments and/or the README for that service or services(s).

### Specification Instructions
* Include descriptions for configurations if they are language-agnostic.
</details>

<details>
  <summary><h2>Resource Creation and Cleanup</h2></summary>

### Guidelines
* Scenarios should include one or more “clean up” functions. These should be highly error resistant, logging but not stopping on any resource removal errors. 
* The clean up function should always run, possibly by storing any error(s) from the main body and reporting them after attempting clean up.
* Resources created as part of an example should be cleaned up as part of the program. 
* Clean up can include a y/n question to the user before deleting resources. 
* If a scenario does not complete due to errors, it should attempt to run the cleanup operation before exiting.

### Specification Instructions
* Include a description if anything other than a y/n question is needed.
</details>

<details>
  <summary><h2>Digital Assets and Sample Files</h2></summary>

### Guidelines
* Examples should follow the repository standards for adding and managing digital assets and sample files.

### Specification Instructions
* Include instructions for retrieving/using any shared digital assets. Prefer shared assets over duplication in each language folder.
</details>

<details>
  <summary><h2>Hello Service</h2></summary>

### Guidelines
* Should demonstrate a single service action to get customers started using an SDK with a service.
* Should be copy-paste runnable to reduce any blocks for the user, ideally in a main function or similar.
* Include imports, service client creation, etc.
* Make a single service call to something that requires no input (ListBuckets, etc.). If Hello Service exists for other languages, use the same Action so they are all consistent.
* If pagination is appropriate/available, use it. You may also limit the number of results.
* Print something useful about the output, don't just dump the output (bucket names, etc.).
* Error handling is optional and only if it makes sense.

### Specification Instructions
* The first implementation for an example (Basic or Workflow) must also include the Hello Service as part of the specification.
</details>

<details>
  <summary><h2>SDK Language Tools</h2></summary>

| Language | Package | Version | Formatter | Linter | Checker | Unit | Base Language Guide |
| -------- | ------- | --------| --------- | ------ | ------- | ---- | -------------- |
|CLI | | | |shellcheck | | |[Shellcheck linter](https://github.com/koalaman/shellcheck)| |
|C++ |git |main | | | | |[C++ Coding Standards Guide](https://github.com/aws/aws-sdk-cpp/blob/main/docs/CODING_STANDARDS.md) |
|.NET |nuget |SDK V3 (.NET 6 or later) |dotnet format | dotnet format | dotnet build | XUnit | [C# (.NET) Code Conventions](https://github.com/dotnet/runtime/blob/main/docs/coding-guidelines/coding-style.md)|
|Go |Go Mod |go-v2 v1.15.3 |gofmt |`golangci-lint` |go build |testing (builtin) |[Go dev](https://go.dev/) |
|Java |Maven |2 | |checkstyle |checkstyle |JUnit |[Oracle Java Code Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-contents.html) |
|JavaScript |NPM |^3.210.0 |prettier |eslint |typescript |vitest |[AirBnB base guide](https://github.com/airbnb/javascript) |
|Kotlin |gradle |0.30.1-beta |ktfmt |ktlint |kotlin | |[Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html) |
|PHP |composer |3.283.2 |phpcs (PSR-12) |phpcs |php |phpunit |[PSR-12 Basic Coding Standard for PHP](https://www.php-fig.org/psr/psr-12/) |
|Python |Pip |boto3>= 1.26.79 |Black |pylint |mypy |pytest |[PEP 8 - Style Guide for Python Code](https://peps.python.org/pep-0008/) |
|Ruby |gem | | | | | |[Ruby Style Guide.](https://github.com/rubocop/ruby-style-guide) |
|Rust |Cargo |next |cargo fmt |cargo clippy |cargo check |cargo test |[Rust Style Guide](https://doc.rust-lang.org/nightly/style-guide/) |
|Swift | |0.28.0 | | | | |[Swift Style Guide](https://google.github.io/swift/) |
</details>



