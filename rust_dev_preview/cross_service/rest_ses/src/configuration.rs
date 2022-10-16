use secrecy::Secret;
use serde::Deserialize;

#[derive(Deserialize)]
pub struct Settings {
    pub application_name: String,
    pub log_level: String,
    pub application_port: u16,
    pub sdk_config: SdkSettings,
}

#[derive(Deserialize)]
pub struct SdkSettings {
    pub secret_arn: Secret<String>,
    pub cluster_arn: String,
    pub db_instance: String,
}

pub fn get_settings() -> Result<Settings, config::ConfigError> {
    let settings = config::Config::builder()
        .add_source(config::File::new(
            "configuration.yaml",
            config::FileFormat::Yaml,
        ))
        .build()?;
    settings.try_deserialize::<Settings>()
}
