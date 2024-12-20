// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

//! Application logic aware wrappers around AWS SDK Clients.
//!
//! These wrappers add default ARNs for SDK resources the application accesses to common calls.
use aws_config::SdkConfig;
use aws_sdk_rdsdata::operation::execute_statement::builders::ExecuteStatementFluentBuilder;
use aws_sdk_ses::operation::send_raw_email::builders::SendRawEmailFluentBuilder;
use mail_builder::headers::address::Address;
use secrecy::{ExposeSecret, Secret};
use serde::Deserialize;

use crate::configuration::{RdsSettings, SesSettings};

/// Amazon Relational Database Service (Amazon RDS) Data Client, with additional global request information.
/// The configured databae ARN, cluster ARN, and secret manager ARN will be used when executing statements or transactions from this Client.
#[derive(Clone)]
pub struct RdsClient {
    secret_arn: Secret<String>,
    cluster_arn: String,
    db_instance: String,
    client: aws_sdk_rdsdata::Client,
}

impl RdsClient {
    pub fn new(settings: &RdsSettings, sdk_config: &SdkConfig) -> Self {
        RdsClient {
            client: aws_sdk_rdsdata::Client::new(sdk_config),
            secret_arn: settings.secret_arn.clone(),
            cluster_arn: settings.cluster_arn.clone(),
            db_instance: settings.db_instance.clone(),
        }
    }

    /// Prepare an ExecuteStatement builder with the ARNs from this client.
    ///
    /// ```rust no_run
    /// # async fn example() {
    /// const CONFIG: &str = r#"{"cluster_arn":"arn:...","secret_arn":"arn:...","db_instance":"..."}"#;
    /// let settings: rest_ses::configuration::RdsSettings = serde_json::from_str(CONFIG).unwrap();
    /// let sdk_config = aws_config::load_from_env().await;
    /// let client = rest_ses::client::RdsClient::new(&settings, &sdk_config);
    ///
    /// client
    ///     .execute_statement()
    ///     .sql("INSERT values (:name) INTO table;")
    ///     .set_parameters(rest_ses::params![("name", "rust")])
    ///     .send()
    ///     .await
    ///     .unwrap();
    /// # }
    /// ```
    pub fn execute_statement(&self) -> ExecuteStatementFluentBuilder {
        self.client
            .execute_statement()
            .secret_arn(self.secret_arn.expose_secret())
            .resource_arn(self.cluster_arn.as_str())
            .database(self.db_instance.as_str())
    }
}

#[cfg(test)]
mod rds_client_for_test {
    use aws_smithy_runtime::client::http::test_util::ReplayEvent;
    use secrecy::Secret;

    use super::RdsClient;
    impl RdsClient {
        pub fn for_test(pairs: Vec<ReplayEvent>) -> Self {
            RdsClient {
                client: aws_sdk_rdsdata::Client::from_conf(
                    sdk_examples_test_utils::client_config!(aws_sdk_rdsdata)
                        .http_client(
                            aws_smithy_runtime::client::http::test_util::StaticReplayClient::new(
                                pairs,
                            ),
                        )
                        .build(),
                ),
                secret_arn: Secret::from("secret".to_string()),
                cluster_arn: "cluster".into(),
                db_instance: "db".into(),
            }
        }
    }
}

/// A newtype wrapper for Email addresses.
#[derive(Debug, Clone, Deserialize)]
pub struct Email(String);

impl AsRef<str> for Email {
    fn as_ref(&self) -> &str {
        self.0.as_str()
    }
}

impl From<Email> for Address<'_> {
    fn from(email: Email) -> Self {
        email.0.into()
    }
}

/// A newtype wrapper for ARNs.
#[derive(Debug, Clone, Deserialize)]
pub struct Arn(String);

impl From<Arn> for std::string::String {
    fn from(arn: Arn) -> Self {
        arn.0
    }
}

/// An Amazon Simple Email Service (Amazon SES) Client, with additional global request information.
/// All requests will use this source Email and Arn when sending via Amazon SES.
#[derive(Clone)]
pub struct SesClient {
    client: aws_sdk_ses::Client,
    source: Email,
    source_arn: Option<Arn>,
}

impl SesClient {
    pub fn new(settings: &SesSettings, sdk_config: &SdkConfig) -> Self {
        SesClient {
            client: aws_sdk_ses::Client::new(sdk_config),
            source: settings.source.clone(),
            source_arn: settings.source_arn.clone(),
        }
    }

    /// Returns an owned clone of the Email.
    pub fn from(&self) -> Email {
        self.source.clone()
    }

    /// Prepares a SendRawEmail Amazon SES request with the source email & ARN configured.
    pub fn send_raw_email(&self) -> SendRawEmailFluentBuilder {
        self.client
            .send_raw_email()
            .source(self.source.clone().0)
            .set_source_arn(self.source_arn.as_ref().map(|arn| arn.0.clone()))
    }
}

/// This is a declarative macro that builds a Vec<SqlParameter> to use in ExecuteStatementBuilder::set_parameters.
/// It requires that all parameters be sent as strings.
#[macro_export]
macro_rules! params {
    ($((
        $name:expr,
        $value:expr
    )),* ) => {
        {
            Some(vec![
            $(
                aws_sdk_rdsdata::types::SqlParameter::builder()
                    .name($name.to_string())
                    .value(aws_sdk_rdsdata::types::Field::StringValue($value.to_string().clone()))
                    .build(),
            )*
            ])
        }
    }
}
