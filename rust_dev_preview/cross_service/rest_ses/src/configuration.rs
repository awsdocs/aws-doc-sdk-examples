use std::net::{IpAddr, SocketAddr};

use color_eyre::Report;
use secrecy::Secret;
use serde::Deserialize;

use crate::client::{Arn, Email};

/// Top level settings, for organization.
#[derive(Debug, Deserialize)]
pub struct Settings {
    pub log_level: String,
    pub application: ApplicationSettings,
    pub rds: RdsSettings,
    pub ses: SesSettings,
}

impl Settings {
    pub fn address(&self) -> SocketAddr {
        SocketAddr::new(self.application.address.clone(), self.application.port)
    }
}

/// Application specific settings, for the HTTP server.
#[derive(Debug, Deserialize)]
pub struct ApplicationSettings {
    pub name: String,
    pub address: IpAddr,
    pub port: u16,
}

/// Settings for the RDS client, primarily the DB & Cluster to access.
#[derive(Debug, Deserialize)]
pub struct RdsSettings {
    pub secret_arn: Secret<String>,
    pub cluster_arn: String,
    pub db_instance: String,
}

/// Settings for the SES client, primarily the source email & ARN.
#[derive(Debug, Deserialize)]
pub struct SesSettings {
    pub source: Email,
    pub source_arn: Option<Arn>,
}

/// Any errors when loading the settings. Why are environments so complicated.
#[derive(Debug)]
pub enum SettingsError {
    Config(config::ConfigError),
    Eyre(Report),
}

const DEFAULT_ENVIRONMENT: &str = &"local";
const DEFAULT_LOG_LEVEL: &str = &"trace";
const DEFAULT_BACKTRACE: &str = &"1";

/// Attempt to find the environment, and pre-set any environment variables.
/// Valid environments are in [Environment].
pub fn init_environment() -> Result<Environment, SettingsError> {
    let environment: Environment = std::env::var("APP_ENVIRONMENT")
        .unwrap_or_else(|_| DEFAULT_ENVIRONMENT.into())
        .try_into()
        .expect("failed to parse APP_ENVIRONMENT");

    // Convenient for development. Production should set its flags itself.
    if !matches!(environment, Environment::Production) {
        if std::env::var("RUST_LIB_BACKTRACE").is_err() {
            std::env::set_var("RUST_LIB_BACKTRACE", DEFAULT_BACKTRACE)
        }
        if std::env::var("RUST_LOG").is_err() {
            std::env::set_var("RUST_LOG", DEFAULT_LOG_LEVEL)
        }
    }

    // Again, nice for development.
    color_eyre::install().map_err(SettingsError::Eyre)?;

    Ok(environment)
}

/// Load settings, given an [Environment].
/// Looks in base.yaml, {environment}.yaml, and then APP_* env variables.
pub fn get_settings(environment: &Environment) -> Result<Settings, SettingsError> {
    let base_dir = std::env::current_dir().expect("Failed to determine cwd");
    let config_dir = base_dir.join("configuration");
    let base_yaml = "base.yaml";
    let environment_yaml = format!("{}.yaml", environment.as_str());

    let settings_loader = config::Config::builder()
        .add_source(config::File::from(config_dir.join(base_yaml)))
        .add_source(config::File::from(config_dir.join(environment_yaml)))
        .add_source(
            config::Environment::with_prefix("APP")
                .prefix_separator("_")
                .separator("__"), // By convention, to deconflict variables with _s.
        )
        .build()
        .map_err(SettingsError::Config)?;

    settings_loader
        .try_deserialize::<Settings>()
        .map_err(SettingsError::Config)
}

/// Valid environments for this app.
pub enum Environment {
    Local,
    Production,
}

impl Environment {
    pub fn as_str(&self) -> &'static str {
        match self {
            Environment::Local => "local",
            Environment::Production => "production",
        }
    }
}

impl TryFrom<String> for Environment {
    type Error = String;

    fn try_from(value: String) -> Result<Self, Self::Error> {
        match value.to_lowercase().as_str() {
            "local" => Ok(Environment::Local),
            "production" => Ok(Environment::Production),
            other => Err(format!(
                "Unknown environment {other}. Please use 'local' or 'production'."
            )),
        }
    }
}
