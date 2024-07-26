// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

//! Scenario that uses the AWS SDK for Rust (the SDK) with Amazon Elastic Compute Cloud
//! (Amazon EC2) to do the following:
//!
//! * Create a key pair that is used to secure SSH communication between your computer and
//!   an EC2 instance.
//! * Create a security group that acts as a virtual firewall for your EC2 instances to
//!   control incoming and outgoing traffic.
//! * Find an Amazon Machine Image (AMI) and a compatible instance type.
//! * Create an instance that is created from the instance type and AMI you select, and
//!   is configured to use the security group and key pair created in this example.
//! * Stop and restart the instance.
//! * Create an Elastic IP address and associate it as a consistent IP address for your instance.
//! * Connect to your instance with SSH, using both its public IP address and your Elastic IP
//!   address.
//! * Clean up all of the resources created by this example.

use std::{fmt::Display, net::Ipv4Addr};

use crate::{
    ec2::{EC2Error, EC2},
    getting_started::key_pair::KeyPairManager,
    ssm::SSM,
};
use aws_sdk_ec2::types::Image;
use aws_sdk_ssm::types::Parameter;
use inquire::{validator::ValueRequiredValidator, InquireError};

use super::{
    elastic_ip::ElasticIpManager, instance::InstanceManager, security_group::SecurityGroupManager,
};

pub struct Ec2InstanceScenario {
    ec2: EC2,
    ssm: SSM,
    key_pair_manager: KeyPairManager,
    security_group_manager: SecurityGroupManager,
    instance_manager: InstanceManager,
    elastic_ip_manager: ElasticIpManager,
}

impl Ec2InstanceScenario {
    pub fn new(ec2: EC2, ssm: SSM) -> Self {
        Ec2InstanceScenario {
            ec2,
            ssm,
            key_pair_manager: Default::default(),
            security_group_manager: Default::default(),
            instance_manager: Default::default(),
            elastic_ip_manager: Default::default(),
        }
    }

    pub async fn run(&mut self) -> Result<(), EC2Error> {
        self.create_and_list_key_pairs().await?;
        self.create_security_group().await?;
        self.create_instance().await?;
        self.stop_and_start_instance().await?;
        self.associate_elastic_ip().await?;
        self.stop_and_start_instance().await?;
        Ok(())
    }

    /// 1. Creates an RSA key pair and saves its private key data as a .pem file in secure
    ///    temporary storage. The private key data is deleted after the example completes.
    /// 2. Optionally, lists the first five key pairs for the current account.
    pub async fn create_and_list_key_pairs(&mut self) -> Result<(), EC2Error> {
        println!( "Let's create an RSA key pair that you can be use to securely connect to your EC2 instance.");

        let key_name = inquire::Text::new("Enter a unique name for your key: ")
            .with_validator(ValueRequiredValidator::default())
            .with_default("my_key")
            .prompt()
            .map_err(|e| EC2Error::new(format!("Failed to get name for key pair. {e:?}")))?;

        self.key_pair_manager.create(&self.ec2, key_name).await?;

        println!(
            "Created a key pair {} and saved the private key to {:?}.",
            self.key_pair_manager
                .key_pair()
                .key_name()
                .ok_or_else(|| EC2Error::new("No key name after creating key"))?,
            self.key_pair_manager
                .key_file_path()
                .ok_or_else(|| EC2Error::new("No key file after creating key"))?
        );

        if inquire::Confirm::new("Do you want to list some of your key pairs?")
            .with_default(false)
            .prompt()
            .or(Ok(false))
            .map_err(|e: InquireError| EC2Error::new(format!("Failed to ask question. {e:?}")))?
        {
            for pair in self.key_pair_manager.list(&self.ec2).await? {
                println!(
                    "Found {:?} key {} with fingerprint:\t{:?}",
                    pair.key_type(),
                    pair.key_name().unwrap_or("Unknown"),
                    pair.key_fingerprint()
                );
            }
        }

        Ok(())
    }

    /// 1. Creates a security group for the default VPC.
    /// 2. Adds an inbound rule to allow SSH. The SSH rule allows only
    ///    inbound traffic from the current computer’s public IPv4 address.
    /// 3. Displays information about the security group.
    ///
    /// This function uses <http://checkip.amazonaws.com> to get the current public IP
    /// address of the computer that is running the example. This method works in most
    /// cases. However, depending on how your computer connects to the internet, you
    /// might have to manually add your public IP address to the security group by using
    /// the AWS Management Console.
    pub async fn create_security_group(&mut self) -> Result<(), EC2Error> {
        println!("Let's create a security group to manage access to your instance.");
        let group_name = inquire::Text::new("Enter a unique name for your security group: ")
            .with_validator(ValueRequiredValidator::default())
            .with_default("my_group")
            .prompt()
            .map_err(|e| EC2Error::new(format!("Failed to get name for security group! {e:?}")))?;

        self.security_group_manager
            .create(
                &self.ec2,
                group_name,
                "Security group for example: get started with instances.",
            )
            .await?;

        println!(
            "Created security group {} in your default VPC {}.",
            self.security_group_manager.group_name(),
            self.security_group_manager
                .vpc_id()
                .unwrap_or("(unknown vpc)")
        );

        let check_ip = do_get("https://checkip.amazonaws.com").await?;
        let current_ip_address: Ipv4Addr = check_ip.trim().parse().map_err(|e| {
            EC2Error::new(format!(
                "Failed to convert response {} to IP Address: {e:?}",
                check_ip
            ))
        })?;

        println!("Your public IP address seems to be {current_ip_address}");
        if inquire::Confirm::new("Add this rule to your security group?")
            .with_default(true)
            .prompt()
            .unwrap_or(true)
        {
            match self
                .security_group_manager
                .authorize_ingress(&self.ec2, current_ip_address)
                .await
            {
                Ok(_) => println!("Security group rules updated"),
                Err(err) => eprintln!("Couldn't update security group rules: {err:?}"),
            }
        }
        println!("{}", self.security_group_manager);

        Ok(())
    }

    // snippet-start:[ec2.rust.create_instance.scenario]
    /// 1. Gets a list of Amazon Linux 2 AMIs from AWS Systems Manager. Specifying the
    ///    '/aws/service/ami-amazon-linux-latest' path returns only the latest AMIs.
    /// 2. Gets and displays information about the available AMIs and lets you select one.
    /// 3. Gets a list of instance types that are compatible with the selected AMI and
    ///    lets you select one.
    /// 4. Creates an instance with the previously created key pair and security group,
    ///    and the selected AMI and instance type.
    /// 5. Waits for the instance to be running and then displays its information.
    pub async fn create_instance(&mut self) -> Result<(), EC2Error> {
        let ami = self.find_image().await?;

        let instance_types = self
            .ec2
            .list_instance_types(&ami.0)
            .await
            .map_err(|e| e.add_message("Could not find instance types"))?;
        println!(
            "There are several instance types that support the {} architecture of the image.",
            ami.0
                .architecture
                .as_ref()
                .ok_or_else(|| EC2Error::new(format!("Missing architecture in {:?}", ami.0)))?
        );
        let instance_type =
            inquire::Select::new("Select an instance type for this instance:", instance_types)
                .prompt()
                .map_err(|e| {
                    EC2Error::new(format!(
                        "Could not determine the desired instance type ({e:?})"
                    ))
                })?;

        println!("Creating your instance and waiting for it to start...");
        self.instance_manager
            .create(
                &self.ec2,
                ami.0
                    .image_id()
                    .ok_or_else(|| EC2Error::new("Could not find image ID"))?,
                instance_type,
                self.key_pair_manager.key_pair(),
                self.security_group_manager
                    .security_group()
                    .map(|sg| vec![sg])
                    .ok_or_else(|| EC2Error::new("Could not find security group"))?,
            )
            .await
            .map_err(|e| e.add_message("Scenario failed to create instance"))?;

        while let Err(err) = self
            .ec2
            .wait_for_instance_ready(self.instance_manager.instance_id(), None)
            .await
        {
            println!("{err}");
            if !inquire::Confirm::new("Continue waiting?")
                .with_default(true)
                .prompt()
                .map_err(|_| err)?
            {
                break;
            }
        }

        println!("Your instance is ready:\n{}", self.instance_manager);

        self.display_ssh_info();

        Ok(())
    }
    // snippet-end:[ec2.rust.create_instance.scenario]

    // snippet-start:[ec2.rust.find_image.scenario]
    async fn find_image(&mut self) -> Result<ScenarioImage, EC2Error> {
        let params: Vec<Parameter> = self
            .ssm
            .list_path("/aws/service/ami-amazon-linux-latest")
            .await
            .map_err(|e| e.add_message("Could not find parameters for available images"))?
            .into_iter()
            .filter(|param| param.name().is_some_and(|name| name.contains("amzn2")))
            .collect();
        let amzn2_images: Vec<ScenarioImage> = self
            .ec2
            .list_images(params)
            .await
            .map_err(|e| e.add_message("Could not find images"))?
            .into_iter()
            .map(ScenarioImage::from)
            .collect();
        println!("We will now create an instance from an Amazon Linux 2 AMI");
        let ami = inquire::Select::new(
            "Select an Amazon Linux 2 AMI for this instance",
            amzn2_images,
        )
        .prompt()
        .map_err(|e| EC2Error::new(format!("Could not determine desired AMI ({e:?})")))?;
        Ok(ami)
    }
    // snippet-end:[ec2.rust.find_image.scenario]

    // 1. Stops the instance and waits for it to stop.
    // 2. Starts the instance and waits for it to start.
    // 3. Displays information about the instance.
    // 4. Displays an SSH connection string. When an Elastic IP address is associated
    //    with the instance, the IP address stays consistent when the instance stops
    //    and starts.
    pub async fn stop_and_start_instance(&self) -> Result<(), EC2Error> {
        println!("Let's stop and start your instance to see what changes.");
        println!("Stopping your instance and waiting until it's stopped...");
        self.instance_manager.stop(&self.ec2).await?;
        println!("Your instance is stopped. Restarting...");
        self.instance_manager.start(&self.ec2).await?;
        println!("Your instance is running.");
        println!("{}", self.instance_manager);
        if self.elastic_ip_manager.public_ip() == "0.0.0.0" {
            println!("Every time your instance is restarted, its public IP address changes.");
        } else {
            println!(
                "Because you have associated an Elastic IP with your instance, you can connect by using a consistent IP address after the instance restarts."
            );
        }
        self.display_ssh_info();
        Ok(())
    }

    /// 1. Allocates an Elastic IP address and associates it with the instance.
    /// 2. Displays an SSH connection string that uses the Elastic IP address.
    async fn associate_elastic_ip(&mut self) -> Result<(), EC2Error> {
        self.elastic_ip_manager.allocate(&self.ec2).await?;
        println!(
            "Allocated static Elastic IP address: {}",
            self.elastic_ip_manager.public_ip()
        );

        self.elastic_ip_manager
            .associate(&self.ec2, self.instance_manager.instance_id())
            .await?;
        println!("Associated your Elastic IP with your instance.");
        println!("You can now use SSH to connect to your instance by using the Elastic IP.");
        self.display_ssh_info();
        Ok(())
    }

    /// Displays an SSH connection string that can be used to connect to a running
    /// instance.
    fn display_ssh_info(&self) {
        let ip_addr = if self.elastic_ip_manager.has_allocation() {
            self.elastic_ip_manager.public_ip()
        } else {
            self.instance_manager.instance_ip()
        };
        let key_file_path = self.key_pair_manager.key_file_path().unwrap();
        println!("To connect, open another command prompt and run the following command:");
        println!("\nssh -i {} ec2-user@{ip_addr}\n", key_file_path.display());
        let _ = inquire::Text::new("Press Enter when you're ready to continue the demo.").prompt();
    }

    /// 1. Disassociate and delete the previously created Elastic IP.
    /// 2. Terminate the previously created instance.
    /// 3. Delete the previously created security group.
    /// 4. Delete the previously created key pair.
    pub async fn clean_up(self) {
        println!("Let's clean everything up. This example created these resources:");
        println!(
            "\tKey pair: {}",
            self.key_pair_manager
                .key_pair()
                .key_name()
                .unwrap_or("(unknown key pair)")
        );
        println!(
            "\tSecurity group: {}",
            self.security_group_manager.group_name()
        );
        println!(
            "\tInstance: {}",
            self.instance_manager.instance_display_name()
        );
        if inquire::Confirm::new("Clean up resources?")
            .with_default(true)
            .prompt()
            .unwrap_or(false)
        {
            if let Err(err) = self.elastic_ip_manager.remove(&self.ec2).await {
                eprintln!("{err}")
            }
            if let Err(err) = self.instance_manager.delete(&self.ec2).await {
                eprintln!("{err}")
            }
            if let Err(err) = self.security_group_manager.delete(&self.ec2).await {
                eprintln!("{err}");
            }
            if let Err(err) = self.key_pair_manager.delete(&self.ec2).await {
                eprintln!("{err}");
            }
        } else {
            println!("Ok, not cleaning up any resources!");
        }
    }
}

/// Utility to perform a GET request and return the body as UTF-8, or an appropriate EC2Error.
async fn do_get(url: &str) -> Result<String, EC2Error> {
    reqwest::get(url)
        .await
        .map_err(|e| EC2Error::new(format!("Could not request ip from {url}: {e:?}")))?
        .error_for_status()
        .map_err(|e| EC2Error::new(format!("Failure status from {url}: {e:?}")))?
        .text_with_charset("utf-8")
        .await
        .map_err(|e| EC2Error::new(format!("Failed to read response from {url}: {e:?}")))
}

/// Image doesn't impl Display, which is necessary for inquire to use it in a Select.
/// This wraps Image and provides a Display impl.
struct ScenarioImage(Image);
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
