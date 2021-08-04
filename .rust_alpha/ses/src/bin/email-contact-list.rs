/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_sdk_ses::model::{Body, Content, Destination, EmailContent, Message};
use aws_sdk_ses::{Client, Config, Error, Region, PKG_VERSION};
use aws_types::region;
use aws_types::region::ProvideRegion;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The contact list containing email addresses to send the message to.
    #[structopt(short, long)]
    contact_list: String,

    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The email address of the sender.
    #[structopt(short, long)]
    from_address: String,

    /// The message of the email.
    #[structopt(short, long)]
    message: String,

    /// The subject of the email.
    #[structopt(short, long)]
    subject: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

/// Sends a message to the email addresses in the contact list in the Region.
/// # Arguments
///
/// * `-f FROM-ADDRESS` - The email address of the sender.
/// * `-m MESSAGE` - The email message that is sent.
/// * `-s SUBJECT` - The subject of the email message.
/// * `-c CONTACT-LIST` - The contact list with the email addresses of the recepients.
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
        from_address,
        message,
        subject,
        verbose,
    } = Opt::from_args();

    let region = region::ChainProvider::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    println!();

    if verbose {
        println!("SES client version: {}", PKG_VERSION);
        println!("Region:             {}", region.region().unwrap().as_ref());
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
        .await?;

    let contacts = resp.contacts.unwrap_or_default();

    let cs: String = contacts
        .into_iter()
        .map(|i| i.email_address.unwrap_or_default())
        .collect();

    let dest = Destination::builder().to_addresses(cs).build();
    let subject_content = Content::builder().data(subject).charset("UTF-8").build();
    let body_content = Content::builder().data(message).charset("UTF-8").build();
    let body = Body::builder().text(body_content).build();

    let msg = Message::builder()
        .subject(subject_content)
        .body(body)
        .build();

    let email_content = EmailContent::builder().simple(msg).build();

    client
        .send_email()
        .from_email_address(from_address)
        .destination(dest)
        .content(email_content)
        .send()
        .await?;

    println!("Email sent to list");

    Ok(())
}
