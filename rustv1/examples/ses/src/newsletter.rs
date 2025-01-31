// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

use anyhow::{anyhow, Result};
use aws_sdk_sesv2::{
    operation::{
        create_contact::CreateContactError, create_contact_list::CreateContactListError,
        create_email_identity::CreateEmailIdentityError,
        create_email_template::CreateEmailTemplateError,
    },
    types::{
        Body, Contact, Content, Destination, EmailContent, EmailTemplateContent,
        ListManagementOptions, Message, Template,
    },
    Client,
};
use std::io::{BufRead, Write};
use tracing::info;

const CONTACT_LIST_NAME: &str = "weekly-coupons-newsletter";
const TEMPLATE_NAME: &str = "weekly-coupons";

/// The SESWorkflow struct encapsulates the entire SES v2 Coupon Newsletter Workflow.
pub struct SESWorkflow<'a> {
    client: Client,
    stdin: &'a mut dyn BufRead,
    stdout: &'a mut dyn Write,
    verified_email: String,
}

impl<'a> SESWorkflow<'a> {
    /// Creates a new instance of the SESWorkflow struct.
    ///
    /// # Arguments
    ///
    /// * `client` - The AWS SDK for Rust SES v2 client.
    /// * `stdin` - A mutable reference to the standard input stream.
    /// * `stdout` - A mutable reference to the standard output stream.
    pub fn new(client: Client, stdin: &'a mut dyn BufRead, stdout: &'a mut dyn Write) -> Self {
        Self {
            client,
            stdin,
            stdout,
            verified_email: "".into(),
        }
    }

    /// Prepares the application by creating a verified email identity and a contact list.
    pub async fn prepare_application(&mut self) -> Result<()> {
        // Prompt the user for a verified email address
        writeln!(self.stdout, "Enter the verified email address to use: ")?;
        self.stdout.flush().unwrap();
        let mut verified_email = String::new();
        self.stdin.read_line(&mut verified_email).unwrap();
        self.verified_email = verified_email.trim().to_string();

        // snippet-start:[sesv2.rust.create-email-identity]
        match self
            .client
            .create_email_identity()
            .email_identity(self.verified_email.clone())
            .send()
            .await
        {
            Ok(_) => writeln!(self.stdout, "Email identity created successfully.")?,
            Err(e) => match e.into_service_error() {
                CreateEmailIdentityError::AlreadyExistsException(_) => {
                    writeln!(
                        self.stdout,
                        "Email identity already exists, skipping creation."
                    )?;
                }
                e => return Err(anyhow!("Error creating email identity: {}", e)),
            },
        }
        // snippet-end:[sesv2.rust.create-email-identity]

        // Create the contact list
        // snippet-start:[sesv2.rust.create-contact-list]
        match self
            .client
            .create_contact_list()
            .contact_list_name(CONTACT_LIST_NAME)
            .send()
            .await
        {
            Ok(_) => writeln!(self.stdout, "Contact list created successfully.")?,
            Err(e) => match e.into_service_error() {
                CreateContactListError::AlreadyExistsException(_) => {
                    writeln!(
                        self.stdout,
                        "Contact list already exists, skipping creation."
                    )?;
                }
                e => return Err(anyhow!("Error creating contact list: {}", e)),
            },
        }
        // snippet-end:[sesv2.rust.create-contact-list]

        // snippet-start:[sesv2.rust.create-email-template]
        let template_html =
            std::fs::read_to_string("../resources/newsletter/coupon-newsletter.html")
                .unwrap_or_else(|_| "Missing coupon-newsletter.html".to_string());
        let template_text =
            std::fs::read_to_string("../resources/newsletter/coupon-newsletter.txt")
                .unwrap_or_else(|_| "Missing coupon-newsletter.txt".to_string());

        // Create the email template
        let template_content = EmailTemplateContent::builder()
            .subject("Weekly Coupons Newsletter")
            .html(template_html)
            .text(template_text)
            .build();

        match self
            .client
            .create_email_template()
            .template_name(TEMPLATE_NAME)
            .template_content(template_content)
            .send()
            .await
        {
            Ok(_) => writeln!(self.stdout, "Email template created successfully.")?,
            Err(e) => match e.into_service_error() {
                CreateEmailTemplateError::AlreadyExistsException(_) => {
                    writeln!(
                        self.stdout,
                        "Email template already exists, skipping creation."
                    )?;
                }
                e => return Err(anyhow!("Error creating email template: {}", e)),
            },
        }
        // snippet-end:[sesv2.rust.create-email-template]

        Ok(())
    }

    /// Gathers subscriber email addresses and sends welcome emails.
    pub async fn gather_subscriber_emails(&mut self) -> Result<()> {
        // Prompt the user for a base email address
        writeln!(
            self.stdout,
            "Enter a base email address for subscribing (e.g., user@example.com): "
        )?;
        self.stdout.flush().unwrap();
        let mut base_email = String::new();
        self.stdin.read_line(&mut base_email).unwrap();
        let base_email = base_email.trim().to_string();

        // Create 3 variants of the email address as {user email}+ses-weekly-newsletter-{i}@{user domain}
        let (user_email, user_domain) = base_email.split_once('@').unwrap();
        let mut emails = Vec::with_capacity(3);
        for i in 1..=3 {
            let email = format!("{}+ses-weekly-newsletter-{}@{}", user_email, i, user_domain);
            emails.push(email);
        }

        // Create a contact and send a welcome email for each email address
        for email in emails {
            // Create the contact
            // snippet-start:[sesv2.rust.create-contact]
            match self
                .client
                .create_contact()
                .contact_list_name(CONTACT_LIST_NAME)
                .email_address(email.clone())
                .send()
                .await
            {
                Ok(_) => writeln!(self.stdout, "Contact created for {}", email)?,
                Err(e) => match e.into_service_error() {
                    CreateContactError::AlreadyExistsException(_) => writeln!(
                        self.stdout,
                        "Contact already exists for {}, skipping creation.",
                        email
                    )?,
                    e => return Err(anyhow!("Error creating contact for {}: {}", email, e)),
                },
            }
            // snippet-end:[sesv2.rust.create-contact]

            // Send the welcome email
            // snippet-start:[sesv2.rust.send-email.simple]
            let welcome_html = std::fs::read_to_string("../resources/newsletter/welcome.html")
                .unwrap_or_else(|_| "Missing welcome.html".to_string());
            let welcome_txt = std::fs::read_to_string("../resources/newsletter/welcome.txt")
                .unwrap_or_else(|_| "Missing welcome.txt".to_string());
            let email_content = EmailContent::builder()
                .simple(
                    Message::builder()
                        .subject(
                            Content::builder()
                                .data("Welcome to the Weekly Coupons Newsletter")
                                .build()?,
                        )
                        .body(
                            Body::builder()
                                .html(Content::builder().data(welcome_html).build()?)
                                .text(Content::builder().data(welcome_txt).build()?)
                                .build(),
                        )
                        .build(),
                )
                .build();

            match self
                .client
                .send_email()
                .from_email_address(self.verified_email.clone())
                .destination(Destination::builder().to_addresses(email.clone()).build())
                .content(email_content)
                .send()
                .await
            {
                Ok(output) => {
                    if let Some(message_id) = output.message_id {
                        writeln!(
                            self.stdout,
                            "Welcome email sent to {} with message ID {}",
                            email, message_id
                        )?;
                    } else {
                        writeln!(self.stdout, "Welcome email sent to {}", email)?;
                    }
                }
                Err(e) => return Err(anyhow!("Error sending welcome email to {}: {}", email, e)),
            }
            // snippet-end:[sesv2.rust.send-email.simple]
        }

        Ok(())
    }

    /// Sends the coupon newsletter to the subscribers.
    pub async fn send_coupon_newsletter(&mut self) -> Result<()> {
        // Retrieve the list of contacts
        // snippet-start:[sesv2.rust.list-contacts]
        let contacts: Vec<Contact> = match self
            .client
            .list_contacts()
            .contact_list_name(CONTACT_LIST_NAME)
            .send()
            .await
        {
            Ok(list_contacts_output) => {
                list_contacts_output.contacts.unwrap().into_iter().collect()
            }
            Err(e) => {
                return Err(anyhow!(
                    "Error retrieving contact list {}: {}",
                    CONTACT_LIST_NAME,
                    e
                ))
            }
        };
        // snippet-end:[sesv2.rust.list-contacts]

        // Send the newsletter to each contact
        for email in contacts {
            let email = email.email_address.unwrap();

            // snippet-start:[sesv2.rust.send-email.template]
            let coupons = std::fs::read_to_string("../resources/newsletter/sample_coupons.json")
                .unwrap_or_else(|_| r#"{"coupons":[]}"#.to_string());
            let email_content = EmailContent::builder()
                .template(
                    Template::builder()
                        .template_name(TEMPLATE_NAME)
                        .template_data(coupons)
                        .build(),
                )
                .build();

            match self
                .client
                .send_email()
                .from_email_address(self.verified_email.clone())
                .destination(Destination::builder().to_addresses(email.clone()).build())
                .content(email_content)
                .list_management_options(
                    ListManagementOptions::builder()
                        .contact_list_name(CONTACT_LIST_NAME)
                        .build()?,
                )
                .send()
                .await
            {
                Ok(output) => {
                    if let Some(message_id) = output.message_id {
                        writeln!(
                            self.stdout,
                            "Newsletter sent to {} with message ID {}",
                            email, message_id
                        )?;
                    } else {
                        writeln!(self.stdout, "Newsletter sent to {}", email)?;
                    }
                }
                Err(e) => return Err(anyhow!("Error sending newsletter to {}: {}", email, e)),
            }
            // snippet-end:[sesv2.rust.send-email.template]
        }

        Ok(())
    }

    /// Monitors the sending activity and provides insights.
    pub async fn monitor(&mut self) -> Result<()> {
        // Check if the user wants to review the monitoring dashboard
        writeln!(
            self.stdout,
            "Do you want to review the monitoring dashboard? (y/n): "
        )?;
        self.stdout.flush().unwrap();
        let mut response = String::new();
        self.stdin.read_line(&mut response).unwrap();

        if response.trim().eq_ignore_ascii_case("y") {
            // Open the SES monitoring dashboard in the default browser
            open::that("https://console.aws.amazon.com/ses/home#/account")?;

            writeln!(
                self.stdout,
                "The SES monitoring dashboard has been opened in your default browser."
            )?;
            writeln!(
                self.stdout,
                "Review the sending activity, open and click rates, bounces, complaints, and more."
            )?;
        } else {
            writeln!(self.stdout, "Skipping the monitoring dashboard review.")?;
        }

        writeln!(self.stdout, "Press any key to continue.")?;
        self.stdout.flush().unwrap();
        let mut response = String::new();
        self.stdin.read_line(&mut response).unwrap();

        Ok(())
    }

    /// Cleans up the resources created during the workflow.
    pub async fn cleanup(&mut self) -> Result<()> {
        info!("Cleaning up resources...");

        // snippet-start:[sesv2.rust.delete-contact-list]
        match self
            .client
            .delete_contact_list()
            .contact_list_name(CONTACT_LIST_NAME)
            .send()
            .await
        {
            Ok(_) => writeln!(self.stdout, "Contact list deleted successfully.")?,
            Err(e) => return Err(anyhow!("Error deleting contact list: {e}")),
        }
        // snippet-end:[sesv2.rust.delete-contact-list]

        // snippet-start:[sesv2.rust.delete-email-template]
        match self
            .client
            .delete_email_template()
            .template_name(TEMPLATE_NAME)
            .send()
            .await
        {
            Ok(_) => writeln!(self.stdout, "Email template deleted successfully.")?,
            Err(e) => {
                return Err(anyhow!("Error deleting email template: {e}"));
            }
        }
        // snippet-end:[sesv2.rust.delete-email-template]

        // Delete the email identity
        writeln!(
            self.stdout,
            "Do you want to delete the verified email identity? (y/n): "
        )?;
        self.stdout.flush().unwrap();
        let mut response = String::new();
        self.stdin.read_line(&mut response).unwrap();

        if response.trim().eq_ignore_ascii_case("y") {
            // snippet-start:[sesv2.rust.delete-email-identity]
            match self
                .client
                .delete_email_identity()
                .email_identity(self.verified_email.clone())
                .send()
                .await
            {
                Ok(_) => writeln!(self.stdout, "Email identity deleted successfully.")?,
                Err(e) => {
                    return Err(anyhow!("Error deleting email identity: {}", e));
                }
            }
            // snippet-end:[sesv2.rust.delete-email-identity]
        } else {
            writeln!(self.stdout, "Skipping deletion of email identity.")?;
        }

        info!("Cleanup completed.");

        Ok(())
    }

    pub async fn run(&mut self) -> Result<()> {
        self.prepare_application().await?;
        self.gather_subscriber_emails().await?;
        self.send_coupon_newsletter().await?;
        self.monitor().await?;
        Ok(())
    }

    pub fn set_verified_email(&mut self, verified_email: String) {
        self.verified_email = verified_email;
    }
}
