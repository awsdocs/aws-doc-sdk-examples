## General Structure

* One example crate per SDK service, with the same name as the published crate minus `aws-sdk-`.
* Each crate has a `lib.rs` in the root, which uses pub mod for each module part.
    * Skipped if crates only have bin examples.
* `bin` dir for all binaries (CLI programs and Scenario runners).
    * Keep user interaction mainly in the bin areas, do not put user interaction in supporting libraries.
    * Use the [inquire](https://crates.io/crates/inquire) crate for complex user input, and `stdin().read_line()` for simple user input.
* Decompose scenario actions into functions in `lib/[scenario_name].rs`, possibly as part of a struct with scenario information to manage the communication saga as en Epic. (See [aurora scenario](https://github.com/awsdocs/aws-doc-sdk-examples/blob/de7b1ee3fae2e3cd7d81a24c17345040f76b1d75/rustv1/examples/aurora/src/aurora_scenario/mod.rs))
* One client wrapper for integration test mocking, if necessary. (See [aurora/src/rds.rs](https://github.com/awsdocs/aws-doc-sdk-examples/blob/de7b1ee3fae2e3cd7d81a24c17345040f76b1d75/rustv1/examples/aurora/src/rds.rs))
* Initialize client once, in main, using behavior_subject, and clone when necessary.

## General Program Flow and Readability

* When to prefer Loop vs Iterator:
    * If there is a clear transformation from `T` to `U`, prefer an iterator.
    * Do not nest control flow inside an iterator.
        * Extract the logic to a dedicated function.
        * Prefer a for loop if the function would be difficult to extract.
        * Prefer an extracted function if the logic is nuanced and should be tested.
* How deep to go in nesting vs when to extract a new function?
    * Two deep is fine, three deep is pushing it, four deep is probably too much.
    * Prefer an extracted function if the logic is nuanced and should be tested.
* When to Trait vs bare functions?
    * Examples rarely have enough complexity that a Trait is worth the mental overhead.
    * bare functions or a struct to manage the Epic communication saga, if necessary.

## Pagination, Waiters, and Error Handling

* All operations are async.
* Use [tokio](https://tokio.rs/) for async runtime.
* List operations typically provide `.into_paginator()` as an async iterator. Use them whenever available, unless the example is specifically showing non-paginator pieces.
    ```rust
    let page_size = page_size.unwrap_or(10);
    let items: Result<Vec<_>, _> = client
        .scan()
        .table_name(table)
        .limit(page_size)
        .into_paginator()
        .items()
        .send()
        .collect()
        .await;

    println!("Items in table (up to {page_size}):");
    for item in items? {
        println!("   {:?}", item);
    }
    ```
* Use builtin waters as `client.wait_until_...` whenever available.  (Example in [EC2](https://github.com/awsdocs/aws-doc-sdk-examples/blob/2546e4ac8c7963c5a97ac838917e9b9dcbe0ba29/rustv1/examples/ec2/src/bin/reboot-instance.rs#L29-L51))
  ```rust
    let wait_status_ok = client
        .wait_until_instance_status_ok()
        .instance_ids(id)
        .wait(Duration::from_secs(60))
        .await;

    match wait_status_ok {
        Ok(_) => println!("Rebooted instance {id}, it is started with status OK."),
        Err(err) => return Err(err.into()),
    }
  ```
* Modeled errors for scenarios (see [Aurora](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/rustv1/examples/aurora/src/aurora_scenario/mod.rs#L55-L85)) (hand-modeled or thiserror).
  ```rust
    #[derive(Debug, PartialEq, Eq)]
    pub struct ScenarioError {
        message: String,
        context: Option<MetadataError>,
    }

    impl ScenarioError {
        pub fn with(message: impl Into<String>) -> Self {
            ScenarioError {
                message: message.into(),
                context: None,
            }
        }

        pub fn new(message: impl Into<String>, err: &dyn ProvideErrorMetadata) -> Self {
            ScenarioError {
                message: message.into(),
                context: Some(MetadataError::from(err)),
            }
        }
    }

    impl std::error::Error for ScenarioError {}
    impl Display for ScenarioError {
        fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
            match &self.context {
                Some(c) => write!(f, "{}: {}", self.message, c),
                None => write!(f, "{}", self.message),
            }
        }
    }
  ```
* Client::Error for one-off “scripts” or single-action examples. (See example in [EC2](https://github.com/awsdocs/aws-doc-sdk-examples/blob/de7b1ee3fae2e3cd7d81a24c17345040f76b1d75/rustv1/examples/ec2/src/bin/ec2-helloworld.rs#L21-L33)).
    ```rust
    use aws_sdk_ec2::{Client, Error};

    async fn show_regions(client: &Client) -> Result<(), Error> {
        let rsp = client.describe_regions().send().await?;

        for region in rsp.regions() {
            // ...
        }

        Ok(())
    }
    ```
* Anyhow and .into_service_error for things in the middle. (See example in [SES](https://github.com/awsdocs/aws-doc-sdk-examples/blob/208abff74308c11f700d8321eab0f625393ffdb4/rustv1/examples/ses/src/newsletter.rs#L239-L256)).
    ```rust
            let contacts: Vec<Contact> = match self
            .client
            .list_contacts()
            .contact_list_name(CONTACT_LIST_NAME)
            .send()
            .await
        {
            Ok(list_contacts_output) => {
                list_contacts_output.contacts.unwrap().into_iter().collect()
            }
            Err(e) => {
                return Err(anyhow!(
                    "Error retrieving contact list {}: {}",
                    CONTACT_LIST_NAME,
                    e
                ))
            }
        };
    ```

## Runtime Resources

* include_bytes! or include_str! for compile-time data.
* fs::read_to_string for glob load, fs::File for streaming, tie to SdkBody when needed (see example in s3).
* When showing examples that handle PII, use the [secrets](https://crates.io/crates/secrets) crate.

## Test Coverage

* Unit tests in `#[cfg(test)] mod test` or `test.rs`, integration tests in the tests folder.
    * See fully worked example in [Aurora](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/rustv1/examples/aurora/src/aurora_scenario/tests.rs)
    * Unit test
    ```rust
    use crate::rds::MockRdsImpl;

    #[tokio::test]
    async fn test_scenario_set_engine_not_create() {
        let mut mock_rds = MockRdsImpl::default();

        mock_rds
            .expect_create_db_cluster_parameter_group()
            .with(
                eq("RustSDKCodeExamplesDBParameterGroup"),
                eq("Parameter Group created by Rust SDK Code Example"),
                eq("aurora-mysql"),
            )
            .return_once(|_, _, _| Ok(CreateDbClusterParameterGroupOutput::builder().build()));

        let mut scenario = AuroraScenario::new(mock_rds);

        let set_engine = scenario.set_engine("aurora-mysql", "aurora-mysql8.0").await;

        assert!(set_engine.is_err());
    }
    ```
    * Mocks
    ```rust
    #[cfg(test)]
    use mockall::automock;

    #[cfg(test)]
    pub use MockRdsImpl as Rds;
    #[cfg(not(test))]
    pub use RdsImpl as Rds;

    pub struct RdsImpl {
        pub inner: RdsClient,
    }

    #[cfg_attr(test, automock)]
    impl RdsImpl {
        pub fn new(inner: RdsClient) -> Self {
            RdsImpl { inner }
        }

        pub async fn describe_db_engine_versions(
            &self,
            engine: &str,
        ) -> Result<DescribeDbEngineVersionsOutput, SdkError<DescribeDBEngineVersionsError>> {
            self.inner
                .describe_db_engine_versions()
                .engine(engine)
                .send()
                .await
        }

        // etc
    }
    ```

* Coverage with [cargo-llm-cov](https://lib.rs/crates/cargo-llvm-cov)

## Configuration Explanations

* For command line examples, prefer using clap and command line args.
* For server and lambda examples, prefer using args.
* Use the actix-web crate for HTTP servers.

## AWS Resource Creation and Cleanup

* For standalone scenarios, there are generally one or more “clean up” functions. These should be highly error resistant, logging but not stopping on any resource removal errors. The clean up function should always run, possibly by storing any error(s) from the main body and reporting them after attempting clean up.
* When implementing a scenario, try to start with the clean up.
* Scenarios requiring clean up may add a “—no-cleanup” or "—cleanup=false" flag to skip performing the cleanup step.
* Follow the spec for other cleanup decisions.