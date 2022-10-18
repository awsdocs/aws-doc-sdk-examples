use std::net::{IpAddr, SocketAddr};

use color_eyre::Report;
use secrecy::Secret;
use serde::Deserialize;

#[derive(Deserialize)]
pub struct Settings {
    pub log_level: String,
    pub application: ApplicationSettings,
    pub sdk_config: SdkSettings,
}

impl Settings {
    pub fn address(&self) -> SocketAddr {
        SocketAddr::new(self.application.address.clone(), self.application.port)
    }
}

#[derive(Deserialize)]
pub struct ApplicationSettings {
    pub name: String,
    pub address: IpAddr,
    pub port: u16,
}

#[derive(Deserialize)]
pub struct SdkSettings {
    pub secret_arn: Secret<String>,
    pub cluster_arn: String,
    pub db_instance: String,
}

#[derive(Debug)]
pub enum SettingsError {
    Config(config::ConfigError),
    Eyre(Report),
}

pub fn init_environment() -> Result<Environment, SettingsError> {
    let environment: Environment = std::env::var("APP_ENVIRONMENT")
        .unwrap_or_else(|_| "local".into())
        .try_into()
        .expect("failed to parse APP_ENVIRONMENT");

    // Convenient for development. Production should set its flags itself.
    if !matches!(environment, Environment::Production) {
        if std::env::var("RUST_LIB_BACKTRACE").is_err() {
            std::env::set_var("RUST_LIB_BACKTRACE", "1")
        }
        if std::env::var("RUST_LOG").is_err() {
            std::env::set_var("RUST_LOG", "info")
        }
    }

    // Again, nice for development.
    color_eyre::install().map_err(SettingsError::Eyre)?;

    Ok(environment)
}

pub fn get_settings(environment: &Environment) -> Result<Settings, SettingsError> {
    let base_dir = std::env::current_dir().expect("Failed to determine cwd");
    let config_dir = base_dir.join("configuration");
    let base_yaml = "base.yaml";
    let environment_yaml = format!("{}.yaml", environment.as_str());

    let settings = config::Config::builder()
        .add_source(config::File::from(config_dir.join(base_yaml)))
        .add_source(config::File::from(config_dir.join(environment_yaml)))
        .add_source(
            config::Environment::with_prefix("APP")
                .prefix_separator("_")
                .separator("__"),
        )
        .build()
        .map_err(SettingsError::Config)?;

    settings
        .try_deserialize::<Settings>()
        .map_err(SettingsError::Config)
}

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
