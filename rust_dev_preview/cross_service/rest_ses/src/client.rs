use std::option::Option;

use aws_config::SdkConfig;
use aws_sdk_rdsdata::client::fluent_builders::ExecuteStatement;
use aws_sdk_ses::client::fluent_builders::SendRawEmail;
use mail_builder::headers::address::Address;
use secrecy::{ExposeSecret, Secret};
use serde::Deserialize;

use crate::configuration::{RdsSettings, SesSettings};

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

    pub fn execute_statement(&self, sql: &str) -> ExecuteStatement {
        self.client
            .execute_statement()
            .secret_arn(self.secret_arn.expose_secret())
            .resource_arn(self.cluster_arn.as_str())
            .database(self.db_instance.as_str())
            .sql(sql)
    }
}

impl Clone for RdsClient {
    fn clone(&self) -> Self {
        Self {
            secret_arn: self.secret_arn.clone(),
            cluster_arn: self.cluster_arn.clone(),
            db_instance: self.db_instance.clone(),
            client: self.client.clone(),
        }
    }
}

#[derive(Debug, Clone, Deserialize)]
pub struct Email(String);

impl AsRef<str> for Email {
    fn as_ref(&self) -> &str {
        self.0.as_str()
    }
}

impl<'a> Into<Address<'a>> for Email {
    fn into(self) -> Address<'a> {
        self.0.into()
    }
}

#[derive(Debug, Clone, Deserialize)]
pub struct Arn(String);

impl Into<std::string::String> for Arn {
    fn into(self) -> std::string::String {
        self.0.clone()
    }
}

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

    /// Returns an owned clone of the Email
    pub fn from(&self) -> Email {
        self.source.clone()
    }

    /// Prepares a SendRawEmail SES request with the source email & arn configured.
    pub fn send_raw_email(&self) -> SendRawEmail {
        self.client
            .send_raw_email()
            .source(self.source.clone().0)
            .set_source_arn(self.source_arn.as_ref().map(|arn| arn.0.clone()))
    }
}

/// This is a declarative macro that builds a Vec<SqlParameter> to use in ExecuteStatementBuilder::set_parameters.
/// It requires that all parameters will be sent as strings.
///
/// ```
/// client.execute_statement("INSERT values (:name) INTO table;")
/// .set_parameters(params![("name"), ("rust")])
/// ```
#[macro_export]
macro_rules! params {
    ($((
        $name:expr,
        $value:expr
    )),* ) => {
        {
            let mut v = Vec::new();
            $(
                v.push(aws_sdk_rdsdata::model::SqlParameter::builder()
                    .name($name)
                    .value(aws_sdk_rdsdata::model::Field::StringValue($value.clone()))
                    .build());
            )*
            Some(v)
        }
    }
}
