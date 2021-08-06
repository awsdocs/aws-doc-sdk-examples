/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_sdk_ses::{Client, Config, Error, Region, PKG_VERSION};
use aws_types::region;
use aws_types::region::ProvideRegion;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The name of the contact list.
    #[structopt(short, long)]
    contact_list: String,

    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The email address of the contact to add to the contact list.
    #[structopt(short, long)]
    email_address: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

/// Adds a new contact to the contact list in the Region.
/// # Arguments
///
/// * `-c CONTACT-LIST` - The name of the contact list.
/// * `-e EMAIL-ADDRESS` - The email address of the contact to add to the contact list.
/// * `[-r REGION]` - The Region in which the client is created.
///    If not supplied, uses the value of the **AWS_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt {
        contact_list,
        region,
        email_address,
        verbose,
    } = Opt::from_args();

    let region = region::ChainProvider::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    println!();

    if verbose {
        println!("SES client version: {}", PKG_VERSION);
        println!("Region:             {}", region.region().unwrap().as_ref());
        println!("Contact list:       {}", &contact_list);
        println!("Email address:      {}", &email_address);
        println!();
    }

    let conf = Config::builder().region(region).build();
    let client = Client::from_conf(conf);

    client
        .create_contact()
        .contact_list_name(contact_list)
        .email_address(email_address)
        .send()
        .await?;

    println!("Created contact");

    Ok(())
}
