// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

//! IO Utilities wrapper to allow automock for requests and user input prompts.

use std::{fmt::Display, io::Write, path::PathBuf};

use inquire::{validator::ValueRequiredValidator, InquireError};

use aws_sdk_ec2::types::Image;

use crate::ec2::EC2Error;

#[cfg(test)]
use mockall::automock;

#[cfg(not(test))]
pub use UtilImpl as Util;

#[cfg(test)]
pub use MockUtilImpl as Util;

#[derive(Default)]
pub struct UtilImpl;

#[cfg_attr(test, automock)]
impl UtilImpl {
    pub fn prompt_key_name(&self) -> Result<String, EC2Error> {
        inquire::Text::new("Enter a unique name for your key: ")
            .with_validator(ValueRequiredValidator::default())
            .with_default("my_key")
            .prompt()
            .map_err(|e| EC2Error::new(format!("Failed to get name for key pair. {e:?}")))
    }
    pub fn should_clean_resources(&self) -> bool {
        inquire::Confirm::new("Clean up resources?")
            .with_default(true)
            .prompt()
            .unwrap_or(false)
    }

    pub fn enter_to_continue(&self) -> Result<String, InquireError> {
        inquire::Text::new("Press Enter when you're ready to continue the demo.").prompt()
    }

    pub fn select_scenario_image(
        &self,
        amzn2_images: Vec<ScenarioImage>,
    ) -> Result<ScenarioImage, EC2Error> {
        inquire::Select::new(
            "Select an Amazon Linux 2 AMI for this instance",
            amzn2_images,
        )
        .prompt()
        .map_err(|e| EC2Error::new(format!("Could not determine desired AMI ({e:?})")))
    }

    pub fn should_continue_waiting(&self) -> bool {
        inquire::Confirm::new("Continue waiting?")
            .with_default(true)
            .prompt()
            .unwrap_or(false)
    }

    pub fn select_instance_type(
        &self,
        instance_types: Vec<aws_sdk_ec2::types::InstanceType>,
    ) -> Result<aws_sdk_ec2::types::InstanceType, EC2Error> {
        inquire::Select::new("Select an instance type for this instance:", instance_types)
            .prompt()
            .map_err(|e| {
                EC2Error::new(format!(
                    "Could not determine the desired instance type ({e:?})"
                ))
            })
    }

    pub fn should_add_to_security_group(&self) -> bool {
        inquire::Confirm::new("Add this rule to your security group?")
            .with_default(true)
            .prompt()
            .unwrap_or(true)
    }

    pub fn prompt_security_group_name(&self) -> Result<String, EC2Error> {
        inquire::Text::new("Enter a unique name for your security group: ")
            .with_validator(ValueRequiredValidator::default())
            .with_default("my_group")
            .prompt()
            .map_err(|e| EC2Error::new(format!("Failed to get name for security group! {e:?}")))
    }

    pub fn should_list_key_pairs(&self) -> Result<bool, EC2Error> {
        inquire::Confirm::new("Do you want to list some of your key pairs?")
            .with_default(false)
            .prompt()
            .or(Ok(false))
            .map_err(|e: InquireError| EC2Error::new(format!("Failed to ask question. {e:?}")))
    }

    /// Utility to perform a GET request and return the body as UTF-8, or an appropriate EC2Error.
    pub async fn do_get(&self, url: &str) -> Result<String, EC2Error> {
        reqwest::get(url)
            .await
            .map_err(|e| EC2Error::new(format!("Could not request ip from {url}: {e:?}")))?
            .error_for_status()
            .map_err(|e| EC2Error::new(format!("Failure status from {url}: {e:?}")))?
            .text_with_charset("utf-8")
            .await
            .map_err(|e| EC2Error::new(format!("Failed to read response from {url}: {e:?}")))
    }

    pub fn write_secure(
        &self,
        key_name: &str,
        path: &PathBuf,
        material: String,
    ) -> Result<(), EC2Error> {
        let mut file = open_file_0600(path)?;
        file.write(material.as_bytes()).map_err(|e| {
            EC2Error::new(format!("Failed to write {key_name} to {path:?} ({e:?})"))
        })?;
        Ok(())
    }

    pub fn remove(&self, path: &PathBuf) -> std::io::Result<()> {
        std::fs::remove_file(path)
    }
}

#[cfg(unix)]
fn open_file_0600(path: &PathBuf) -> Result<std::fs::File, EC2Error> {
    use std::os::unix::fs::OpenOptionsExt;
    std::fs::OpenOptions::new()
        .write(true)
        .create(true)
        .truncate(true)
        .mode(0o600)
        .open(path.clone())
        .map_err(|e| EC2Error::new(format!("Failed to create {path:?} ({e:?})")))
}

#[cfg(not(unix))]
fn open_file(path: &PathBuf) -> Result<File, EC2Error> {
    fs::File::create(path.clone())
        .map_err(|e| EC2Error::new(format!("Failed to create {path:?} ({e:?})")))?
}

/// Image doesn't impl Display, which is necessary for inquire to use it in a Select.
/// This wraps Image and provides a Display impl.
#[derive(PartialEq, Debug)]
pub struct ScenarioImage(pub Image);
impl From<Image> for ScenarioImage {
    fn from(value: Image) -> Self {
        ScenarioImage(value)
    }
}
impl Display for ScenarioImage {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(
            f,
            "{}: {}",
            self.0.name().unwrap_or("(unknown)"),
            self.0.description().unwrap_or("unknown")
        )
    }
}
