use aws_sdk_rdsdata::{
    client::fluent_builders::ExecuteStatement,
    model::{Field, SqlParameter},
    Client,
};
use secrecy::{ExposeSecret, Secret};

use crate::configuration::SdkSettings;

pub struct RdsClient {
    secret_arn: Secret<String>,
    cluster_arn: String,
    db_instance: String,
    client: Client,
}

impl RdsClient {
    pub fn new(settings: &SdkSettings, sdk_config: &aws_config::SdkConfig) -> Self {
        RdsClient {
            client: Client::new(sdk_config),
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

pub fn param(name: &str, value: String) -> SqlParameter {
    SqlParameter::builder()
        .name(name)
        .value(Field::StringValue(value.clone()))
        .build()
}

#[macro_export]
macro_rules! params {
    ($((
        $name:expr,
        $value:expr
    )),* ) => {
        {
            use crate::client::param;
            let mut v = Vec::new();
            $(
                v.push(param($name, $value.to_owned()));
            )*
            Some(v)
        }
    }
}
