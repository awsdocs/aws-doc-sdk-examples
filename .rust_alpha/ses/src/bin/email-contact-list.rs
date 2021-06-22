/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_types::region::ProvideRegion;

use ses::model::Destination;
use ses::{Client, Config, Error, Region};

use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The contact list containing email addresses to send the message to.
    #[structopt(short, long)]
    contact_list: String,

    /// The AWS Region.
    #[structopt(short, long)]
    default_region: Option<String>,

    /// The email address of the sender.
    #[structopt(short, long)]
    from_address: String,

    /// The message of the email.
    #[structopt(short, long)]
    message: String,

    /// The subject of the email.
    #[structopt(short, long)]
    subject: String,
    /// Whether to display additional runtime information
    #[structopt(short, long)]
    verbose: bool,
}

/// Sends a message to the email addresses in the contact list.
/// # Arguments
///
/// * `-f FROM-ADDRESS` - The email address of the sender.
/// * `-m MESSAGE` - The email message that is sent.
/// * `-s SUBJECT` - The subject of the email message.
/// * `-c CONTACT-LIST` - The contact list with the email addresses of the recepients.
/// * `[-d DEFAULT-REGION]` - The region in which the client is created.
///    If not supplied, uses the value of the **AWS_DEFAULT_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    let Opt {
        contact_list,
        default_region,
        from_address,
        message,
        subject,
        verbose,
    } = Opt::from_args();

    let region = default_region
        .as_ref()
        .map(|region| Region::new(region.clone()))
        .or_else(|| aws_types::region::default_provider().region())
        .unwrap_or_else(|| Region::new("us-west-2"));

    if verbose {
        println!("SES client version: {}", ses::PKG_VERSION);
        println!("Region:             {:?}", &region);
        println!("From address:       {}", &from_address);
        println!("Contact list:       {}", &contact_list);
        println!("Subject:            {}", &subject);
        println!("Message:            {}", &message);
        println!();
    }

    let conf = Config::builder().region(region).build();
    let client = Client::from_conf(conf);

    // Get list of email addresses from contact list.
    let resp = client
        .list_contacts()
        .contact_list_name(contact_list)
        .send()
        .await;

    let contacts = resp.unwrap().contacts.unwrap_or_default();

    let cs: Option<Vec<String>> = Some(
        contacts
            .iter()
            .map(|i| (*i).email_address.unwrap_or_default())
            .collect(),
    );

    let mut dest = Destination::builder().build();
    dest.to_addresses = cs;

    match client
        .send_email()
        .from_email_address(from_address)
        .destination(dest)
        .send()
        .await
    {
        Ok(_) => {}
        Err(e) => {
            println!("Got an error sending email: {}", e);
        }
    }

    Ok(())
}
