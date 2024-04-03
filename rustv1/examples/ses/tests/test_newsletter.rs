// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

use std::io::Cursor;

use anyhow::Result;
use aws_sdk_sesv2::operation::create_contact::{CreateContactError, CreateContactOutput};
use aws_sdk_sesv2::operation::create_contact_list::{
    CreateContactListError, CreateContactListOutput,
};
use aws_sdk_sesv2::operation::create_email_identity::{
    CreateEmailIdentityError, CreateEmailIdentityOutput,
};
use aws_sdk_sesv2::operation::create_email_template::CreateEmailTemplateOutput;
use aws_sdk_sesv2::operation::delete_contact_list::{
    DeleteContactListError, DeleteContactListOutput,
};
use aws_sdk_sesv2::operation::delete_email_identity::DeleteEmailIdentityError;
use aws_sdk_sesv2::operation::delete_email_template::{
    DeleteEmailTemplateError, DeleteEmailTemplateOutput,
};
use aws_sdk_sesv2::operation::list_contacts::{ListContactsError, ListContactsOutput};
use aws_sdk_sesv2::operation::send_email::{SendEmailError, SendEmailOutput};
use aws_sdk_sesv2::types::error::{
    AccountSuspendedException, AlreadyExistsException, LimitExceededException,
    MailFromDomainNotVerifiedException, MessageRejected, NotFoundException, SendingPausedException,
};
use aws_sdk_sesv2::types::{Contact, IdentityType};
use aws_sdk_sesv2::Client;
use aws_smithy_mocks_experimental::{mock, mock_client, RuleMode};
use ses_code_examples::newsletter::SESWorkflow;

// Test prepare_application method
#[tokio::test]
async fn test_prepare_application_success() -> Result<()> {
    let mut stdin = Cursor::new("test@example.com\n".as_bytes());
    let mut stdout = Vec::<u8>::new();

    // Mock AWS SDK calls
    let mock_email_identity = mock!(Client::create_email_identity)
        .match_requests(|req| req.email_identity() == Some("test@example.com"))
        .then_output(|| {
            CreateEmailIdentityOutput::builder()
                .identity_type(IdentityType::EmailAddress)
                .verified_for_sending_status(true)
                .build()
        });

    let mock_contact_list = mock!(Client::create_contact_list)
        .match_requests(|req| req.contact_list_name() == Some("weekly-coupons-newsletter"))
        .then_output(|| CreateContactListOutput::builder().build());

    let mock_email_template = mock!(Client::create_email_template)
        .match_requests(|req| req.template_name() == Some("weekly-coupons"))
        .then_output(|| CreateEmailTemplateOutput::builder().build());

    let client = mock_client!(
        aws_sdk_sesv2,
        RuleMode::Sequential,
        &[
            &mock_email_identity,
            &mock_contact_list,
            &mock_email_template,
        ]
    );

    let mut workflow = SESWorkflow::new(client, &mut stdin, &mut stdout);

    // Run the method
    workflow.prepare_application().await?;

    // Assert the output
    let output = String::from_utf8(stdout)?;
    assert!(output.contains("Email identity created successfully."));
    assert!(output.contains("Contact list created successfully."));
    assert!(output.contains("Email template created successfully."));

    Ok(())
}

#[tokio::test]
async fn test_prepare_application_error_identity_already_exists() -> Result<()> {
    let mut stdin = Cursor::new("test@example.com\n".as_bytes());
    let mut stdout = Vec::<u8>::new();

    // Mock AWS SDK calls
    let mock_email_identity = mock!(Client::create_email_identity)
        .match_requests(|req| req.email_identity() == Some("test@example.com"))
        .then_error(|| {
            CreateEmailIdentityError::AlreadyExistsException(
                AlreadyExistsException::builder().build(),
            )
        });

    let mock_contact_list = mock!(Client::create_contact_list)
        .match_requests(|req| req.contact_list_name() == Some("weekly-coupons-newsletter"))
        .then_output(|| CreateContactListOutput::builder().build());

    let mock_email_template = mock!(Client::create_email_template)
        .match_requests(|req| req.template_name() == Some("weekly-coupons"))
        .then_output(|| CreateEmailTemplateOutput::builder().build());

    let client = mock_client!(
        aws_sdk_sesv2,
        RuleMode::Sequential,
        &[
            &mock_email_identity,
            &mock_contact_list,
            &mock_email_template,
        ]
    );

    let mut workflow = SESWorkflow::new(client, &mut stdin, &mut stdout);

    // Run the method
    workflow.prepare_application().await?;

    // Assert the output
    let output = String::from_utf8(stdout)?;
    assert!(output.contains("Email identity already exists, skipping creation."));
    assert!(output.contains("Contact list created successfully."));
    assert!(output.contains("Email template created successfully."));

    Ok(())
}

#[tokio::test]
async fn test_prepare_application_error_identity_not_found() -> Result<()> {
    let mut stdin = Cursor::new("test@example.com\n".as_bytes());
    let mut stdout = Vec::<u8>::new();

    // Mock AWS SDK calls
    let mock_email_identity = mock!(Client::create_email_identity)
        .match_requests(|req| req.email_identity() == Some("test@example.com"))
        .then_error(|| {
            CreateEmailIdentityError::NotFoundException(NotFoundException::builder().build())
        });

    let client = mock_client!(aws_sdk_sesv2, RuleMode::Sequential, &[&mock_email_identity]);

    let mut workflow = SESWorkflow::new(client, &mut stdin, &mut stdout);

    // Run the method
    let result = workflow.prepare_application().await;

    // Check that the error is propagated
    assert!(result.is_err());
    assert!(format!("{result:?}").contains("Error creating email identity:"));

    Ok(())
}

#[tokio::test]
async fn test_prepare_application_error_identity_limit_exceeded() -> Result<()> {
    let mut stdin = Cursor::new("test@example.com\n".as_bytes());
    let mut stdout = Vec::<u8>::new();

    // Mock AWS SDK calls
    let mock_email_identity = mock!(Client::create_email_identity)
        .match_requests(|req| req.email_identity() == Some("test@example.com"))
        .then_error(|| {
            CreateEmailIdentityError::LimitExceededException(
                LimitExceededException::builder().build(),
            )
        });

    let client = mock_client!(aws_sdk_sesv2, RuleMode::Sequential, &[&mock_email_identity]);

    let mut workflow = SESWorkflow::new(client, &mut stdin, &mut stdout);

    // Run the method
    let result = workflow.prepare_application().await;

    // Check that the error is propagated
    assert!(result.is_err());
    assert!(format!("{result:?}").contains("Error creating email identity:"));

    Ok(())
}

#[tokio::test]
async fn test_prepare_application_error_contact_list_limit_exceeded() -> Result<()> {
    let mut stdin = Cursor::new("test@example.com\n".as_bytes());
    let mut stdout = Vec::<u8>::new();

    // Mock AWS SDK calls
    let mock_email_identity = mock!(Client::create_email_identity)
        .match_requests(|req| req.email_identity() == Some("test@example.com"))
        .then_output(|| {
            CreateEmailIdentityOutput::builder()
                .identity_type(IdentityType::EmailAddress)
                .verified_for_sending_status(true)
                .build()
        });

    let mock_contact_list = mock!(Client::create_contact_list)
        .match_requests(|req| req.contact_list_name() == Some("weekly-coupons-newsletter"))
        .then_error(|| {
            CreateContactListError::LimitExceededException(
                LimitExceededException::builder().build(),
            )
        });

    let client = mock_client!(
        aws_sdk_sesv2,
        RuleMode::Sequential,
        &[&mock_email_identity, &mock_contact_list]
    );

    let mut workflow = SESWorkflow::new(client, &mut stdin, &mut stdout);

    // Run the method
    let result = workflow.prepare_application().await;

    // Assert the output
    let output = String::from_utf8(stdout)?;
    assert!(output.contains("Email identity created successfully."));

    // Check that the error is propagated
    assert!(result.is_err());
    assert!(format!("{result:?}").contains("Error creating contact list:"));

    Ok(())
}

#[tokio::test]
async fn test_gather_subscriber_emails_success() -> Result<()> {
    let mut stdin = Cursor::new("user@example.com\n".as_bytes());
    let mut stdout = Vec::<u8>::new();

    // Mock AWS SDK calls
    let mock_create_contact_1 = mock!(Client::create_contact)
        .match_requests(|req| {
            req.email_address() == Some("user+ses-weekly-newsletter-1@example.com")
        })
        .then_output(|| CreateContactOutput::builder().build());

    let mock_create_contact_2 = mock!(Client::create_contact)
        .match_requests(|req| {
            req.email_address() == Some("user+ses-weekly-newsletter-2@example.com")
        })
        .then_output(|| CreateContactOutput::builder().build());

    let mock_create_contact_3 = mock!(Client::create_contact)
        .match_requests(|req| {
            req.email_address() == Some("user+ses-weekly-newsletter-3@example.com")
        })
        .then_output(|| CreateContactOutput::builder().build());

    let mock_send_email_1 = mock!(Client::send_email)
        .match_requests(|req| {
            req.destination()
                .unwrap()
                .to_addresses()
                .contains(&"user+ses-weekly-newsletter-1@example.com".into())
        })
        .then_output(|| SendEmailOutput::builder().message_id("email-1").build());

    let mock_send_email_2 = mock!(Client::send_email)
        .match_requests(|req| {
            req.destination()
                .unwrap()
                .to_addresses()
                .contains(&"user+ses-weekly-newsletter-2@example.com".into())
        })
        .then_output(|| SendEmailOutput::builder().message_id("email-2").build());

    let mock_send_email_3 = mock!(Client::send_email)
        .match_requests(|req| {
            req.destination()
                .unwrap()
                .to_addresses()
                .contains(&"user+ses-weekly-newsletter-3@example.com".into())
        })
        .then_output(|| SendEmailOutput::builder().message_id("email-3").build());

    let client = mock_client!(
        aws_sdk_sesv2,
        RuleMode::Sequential,
        &[
            &mock_create_contact_1,
            &mock_send_email_1,
            &mock_create_contact_2,
            &mock_send_email_2,
            &mock_create_contact_3,
            &mock_send_email_3,
        ]
    );

    let mut workflow = SESWorkflow::new(client, &mut stdin, &mut stdout);

    // Run the method
    workflow.gather_subscriber_emails().await?;

    // Assert the output
    let output = String::from_utf8(stdout)?;
    assert!(output.contains("Contact created for user+ses-weekly-newsletter-1@example.com"));
    assert!(output.contains(
        "Welcome email sent to user+ses-weekly-newsletter-1@example.com with message ID email-1"
    ));
    assert!(output.contains("Contact created for user+ses-weekly-newsletter-2@example.com"));
    assert!(output.contains(
        "Welcome email sent to user+ses-weekly-newsletter-2@example.com with message ID email-2"
    ));
    assert!(output.contains("Contact created for user+ses-weekly-newsletter-3@example.com"));
    assert!(output.contains(
        "Welcome email sent to user+ses-weekly-newsletter-3@example.com with message ID email-3"
    ));

    Ok(())
}

#[tokio::test]
async fn test_gather_subscriber_emails_error_contact_already_exists() -> Result<()> {
    let mut stdin = Cursor::new("user@example.com\n".as_bytes());
    let mut stdout = Vec::<u8>::new();

    // Mock AWS SDK calls
    let mock_create_contact_1 = mock!(Client::create_contact)
        .match_requests(|req| {
            req.email_address() == Some("user+ses-weekly-newsletter-1@example.com")
        })
        .then_output(|| CreateContactOutput::builder().build());

    let mock_send_welcome_1 = mock!(Client::send_email)
        .match_requests(|req| {
            req.destination()
                .unwrap()
                .to_addresses()
                .contains(&"user+ses-weekly-newsletter-1@example.com".into())
        })
        .then_output(|| SendEmailOutput::builder().build());

    let mock_create_contact_2 = mock!(Client::create_contact)
        .match_requests(|req| {
            req.email_address() == Some("user+ses-weekly-newsletter-2@example.com")
        })
        .then_error(|| {
            CreateContactError::AlreadyExistsException(AlreadyExistsException::builder().build())
        });

    let mock_send_welcome_2 = mock!(Client::send_email)
        .match_requests(|req| {
            req.destination()
                .unwrap()
                .to_addresses()
                .contains(&"user+ses-weekly-newsletter-2@example.com".into())
        })
        .then_output(|| SendEmailOutput::builder().build());

    let mock_create_contact_3 = mock!(Client::create_contact)
        .match_requests(|req| {
            req.email_address() == Some("user+ses-weekly-newsletter-3@example.com")
        })
        .then_output(|| CreateContactOutput::builder().build());

    let mock_send_welcome_3 = mock!(Client::send_email)
        .match_requests(|req| {
            req.destination()
                .unwrap()
                .to_addresses()
                .contains(&"user+ses-weekly-newsletter-3@example.com".into())
        })
        .then_output(|| SendEmailOutput::builder().build());

    let client = mock_client!(
        aws_sdk_sesv2,
        RuleMode::Sequential,
        &[
            &mock_create_contact_1,
            &mock_send_welcome_1,
            &mock_create_contact_2,
            &mock_send_welcome_2,
            &mock_create_contact_3,
            &mock_send_welcome_3,
        ]
    );

    let mut workflow = SESWorkflow::new(client, &mut stdin, &mut stdout);

    // Run the method
    workflow.gather_subscriber_emails().await?;

    // Assert the output
    let output = String::from_utf8(stdout)?;
    assert!(output.contains("Contact created for user+ses-weekly-newsletter-1@example.com"));
    assert!(output.contains(
        "Contact already exists for user+ses-weekly-newsletter-2@example.com, skipping creation."
    ));
    assert!(output.contains("Contact created for user+ses-weekly-newsletter-3@example.com"));

    Ok(())
}

#[tokio::test]
async fn test_gather_subscriber_emails_error_send_email() -> Result<()> {
    let mut stdin = Cursor::new("user@example.com\n".as_bytes());
    let mut stdout = Vec::<u8>::new();

    // Mock AWS SDK calls
    let mock_create_contact_1 = mock!(Client::create_contact)
        .match_requests(|req| {
            req.email_address() == Some("user+ses-weekly-newsletter-1@example.com")
        })
        .then_output(|| CreateContactOutput::builder().build());

    let mock_send_email_1 = mock!(Client::send_email)
        .match_requests(|req| {
            req.destination()
                .unwrap()
                .to_addresses()
                .contains(&"user+ses-weekly-newsletter-1@example.com".into())
        })
        .then_error(|| {
            SendEmailError::AccountSuspendedException(AccountSuspendedException::builder().build())
        });

    let client = mock_client!(
        aws_sdk_sesv2,
        RuleMode::Sequential,
        &[&mock_create_contact_1, &mock_send_email_1,]
    );

    let mut workflow = SESWorkflow::new(client, &mut stdin, &mut stdout);

    // Run the method
    let result = workflow.gather_subscriber_emails().await;

    // Check that the error is propagated
    assert!(result.is_err());
    assert!(format!("{result:?}")
        .contains("Error sending welcome email to user+ses-weekly-newsletter-1@example.com:"));

    Ok(())
}

#[tokio::test]
async fn test_send_coupon_newsletter_success() -> Result<()> {
    let mut stdin = Cursor::new("".as_bytes());
    let mut stdout = Vec::<u8>::new();

    // Mock AWS SDK calls
    let mock_list_contacts = mock!(Client::list_contacts)
        .match_requests(|req| req.contact_list_name() == Some("weekly-coupons-newsletter"))
        .then_output(|| {
            ListContactsOutput::builder()
                .contacts(Contact::builder().email_address("user@example.com").build())
                .build()
        });

    let mock_send_email = mock!(Client::send_email)
        .match_requests(|req| {
            req.destination()
                .unwrap()
                .to_addresses()
                .contains(&"user@example.com".into())
        })
        .then_output(|| {
            SendEmailOutput::builder()
                .message_id("newsletter-email")
                .build()
        });

    let client = mock_client!(
        aws_sdk_sesv2,
        RuleMode::Sequential,
        &[&mock_list_contacts, &mock_send_email,]
    );

    let mut workflow = SESWorkflow::new(client, &mut stdin, &mut stdout);
    workflow.set_verified_email("sender@example.com".into());

    // Run the method
    workflow.send_coupon_newsletter().await?;

    // Assert the output
    let output = String::from_utf8(stdout)?;
    assert!(output.contains("Newsletter sent to user@example.com with message ID newsletter-email"));

    Ok(())
}

#[tokio::test]
async fn test_send_coupon_newsletter_error_template_already_exists() -> Result<()> {
    let mut stdin = Cursor::new("".as_bytes());
    let mut stdout = Vec::<u8>::new();

    // Mock AWS SDK calls
    let mock_list_contacts = mock!(Client::list_contacts)
        .match_requests(|req| req.contact_list_name() == Some("weekly-coupons-newsletter"))
        .then_error(|| ListContactsError::NotFoundException(NotFoundException::builder().build()));

    let client = mock_client!(aws_sdk_sesv2, RuleMode::Sequential, &[&mock_list_contacts]);

    let mut workflow = SESWorkflow::new(client, &mut stdin, &mut stdout);
    workflow.set_verified_email("sender@example.com".into());

    // Run the method
    let result = workflow.send_coupon_newsletter().await;

    // Check that the error is propagated
    assert!(result.is_err());
    assert!(
        format!("{result:?}").contains("Error retrieving contact list weekly-coupons-newsletter:")
    );

    Ok(())
}

#[tokio::test]
async fn test_send_coupon_newsletter_error_account_suspended() -> Result<()> {
    let mut stdin = Cursor::new("".as_bytes());
    let mut stdout = Vec::<u8>::new();

    // Mock AWS SDK calls
    let mock_list_contacts = mock!(Client::list_contacts)
        .match_requests(|req| req.contact_list_name() == Some("weekly-coupons-newsletter"))
        .then_output(|| {
            ListContactsOutput::builder()
                .contacts(Contact::builder().email_address("user@example.com").build())
                .build()
        });

    let mock_send_email = mock!(Client::send_email)
        .match_requests(|req| {
            req.destination()
                .unwrap()
                .to_addresses()
                .contains(&"user@example.com".into())
        })
        .then_error(|| {
            SendEmailError::AccountSuspendedException(AccountSuspendedException::builder().build())
        });

    let client = mock_client!(
        aws_sdk_sesv2,
        RuleMode::Sequential,
        &[&mock_list_contacts, &mock_send_email,]
    );

    let mut workflow = SESWorkflow::new(client, &mut stdin, &mut stdout);
    workflow.set_verified_email("sender@example.com".into());

    // Run the method
    let result = workflow.send_coupon_newsletter().await;

    // Check that the error is propagated
    assert!(result.is_err());
    assert!(format!("{result:?}").contains("Error sending newsletter to user@example.com:"));

    Ok(())
}

#[tokio::test]
async fn test_send_coupon_newsletter_error_mail_from_domain_not_verified() -> Result<()> {
    let mut stdin = Cursor::new("".as_bytes());
    let mut stdout = Vec::<u8>::new();

    // Mock AWS SDK calls
    let mock_list_contacts = mock!(Client::list_contacts)
        .match_requests(|req| req.contact_list_name() == Some("weekly-coupons-newsletter"))
        .then_output(|| {
            ListContactsOutput::builder()
                .contacts(Contact::builder().email_address("user@example.com").build())
                .build()
        });

    let mock_send_email = mock!(Client::send_email)
        .match_requests(|req| {
            req.destination()
                .unwrap()
                .to_addresses()
                .contains(&"user@example.com".into())
        })
        .then_error(|| {
            SendEmailError::MailFromDomainNotVerifiedException(
                MailFromDomainNotVerifiedException::builder().build(),
            )
        });

    let client = mock_client!(
        aws_sdk_sesv2,
        RuleMode::Sequential,
        &[&mock_list_contacts, &mock_send_email,]
    );

    let mut workflow = SESWorkflow::new(client, &mut stdin, &mut stdout);
    workflow.set_verified_email("sender@example.com".into());

    // Run the method
    let result = workflow.send_coupon_newsletter().await;

    // Check that the error is propagated
    assert!(result.is_err());
    assert!(format!("{result:?}").contains("Error sending newsletter to user@example.com:"));

    Ok(())
}

#[tokio::test]
async fn test_send_coupon_newsletter_error_message_rejected() -> Result<()> {
    let mut stdin = Cursor::new("".as_bytes());
    let mut stdout = Vec::<u8>::new();

    // Mock AWS SDK calls
    let mock_list_contacts = mock!(Client::list_contacts)
        .match_requests(|req| req.contact_list_name() == Some("weekly-coupons-newsletter"))
        .then_output(|| {
            ListContactsOutput::builder()
                .contacts(Contact::builder().email_address("user@example.com").build())
                .build()
        });

    let mock_send_email = mock!(Client::send_email)
        .match_requests(|req| {
            req.destination()
                .unwrap()
                .to_addresses()
                .contains(&"user@example.com".into())
        })
        .then_error(|| SendEmailError::MessageRejected(MessageRejected::builder().build()));

    let client = mock_client!(
        aws_sdk_sesv2,
        RuleMode::Sequential,
        &[&mock_list_contacts, &mock_send_email,]
    );

    let mut workflow = SESWorkflow::new(client, &mut stdin, &mut stdout);
    workflow.set_verified_email("sender@example.com".into());

    // Run the method
    let result = workflow.send_coupon_newsletter().await;

    // Check that the error is propagated
    assert!(result.is_err());
    assert!(format!("{result:?}").contains("Error sending newsletter to user@example.com:"));

    Ok(())
}

#[tokio::test]
async fn test_send_coupon_newsletter_error_sending_paused() -> Result<()> {
    let mut stdin = Cursor::new("".as_bytes());
    let mut stdout = Vec::<u8>::new();

    // Mock AWS SDK calls
    let mock_list_contacts = mock!(Client::list_contacts)
        .match_requests(|req| req.contact_list_name() == Some("weekly-coupons-newsletter"))
        .then_output(|| {
            ListContactsOutput::builder()
                .contacts(Contact::builder().email_address("user@example.com").build())
                .build()
        });

    let mock_send_email = mock!(Client::send_email)
        .match_requests(|req| {
            req.destination()
                .unwrap()
                .to_addresses()
                .contains(&"user@example.com".into())
        })
        .then_error(|| {
            SendEmailError::SendingPausedException(SendingPausedException::builder().build())
        });

    let client = mock_client!(
        aws_sdk_sesv2,
        RuleMode::Sequential,
        &[&mock_list_contacts, &mock_send_email,]
    );

    let mut workflow = SESWorkflow::new(client, &mut stdin, &mut stdout);
    workflow.set_verified_email("sender@example.com".into());

    // Run the method
    let result = workflow.send_coupon_newsletter().await;

    // Check that the error is propagated
    assert!(result.is_err());
    assert!(format!("{result:?}").contains("Error sending newsletter to user@example.com:"));

    Ok(())
}
#[tokio::test]
async fn test_cleanup_success() -> Result<()> {
    let mut stdin = Cursor::new("n\n".as_bytes());
    let mut stdout = Vec::<u8>::new();

    // Mock AWS SDK calls
    let mock_delete_contact_list = mock!(Client::delete_contact_list)
        .match_requests(|req| req.contact_list_name() == Some("weekly-coupons-newsletter"))
        .then_output(|| DeleteContactListOutput::builder().build());

    let mock_delete_email_template = mock!(Client::delete_email_template)
        .match_requests(|req| req.template_name() == Some("weekly-coupons"))
        .then_output(|| DeleteEmailTemplateOutput::builder().build());

    let client = mock_client!(
        aws_sdk_sesv2,
        RuleMode::Sequential,
        &[&mock_delete_contact_list, &mock_delete_email_template,]
    );

    let mut workflow = SESWorkflow::new(client, &mut stdin, &mut stdout);

    // Run the method
    workflow.cleanup().await?;

    // Assert the output
    let output = String::from_utf8(stdout)?;
    assert!(output.contains("Contact list deleted successfully."));
    assert!(output.contains("Email template deleted successfully."));
    assert!(output.contains("Skipping deletion of email identity."));

    Ok(())
}

#[tokio::test]
async fn test_cleanup_error_contact_list_not_found() -> Result<()> {
    let mut stdin = Cursor::new("n\n".as_bytes());
    let mut stdout = Vec::<u8>::new();

    // Mock AWS SDK calls
    let mock_delete_contact_list = mock!(Client::delete_contact_list)
        .match_requests(|req| req.contact_list_name() == Some("weekly-coupons-newsletter"))
        .then_error(|| {
            DeleteContactListError::NotFoundException(NotFoundException::builder().build())
        });

    let mock_delete_email_template = mock!(Client::delete_email_template)
        .match_requests(|req| req.template_name() == Some("weekly-coupons"))
        .then_output(|| DeleteEmailTemplateOutput::builder().build());

    let client = mock_client!(
        aws_sdk_sesv2,
        RuleMode::Sequential,
        &[&mock_delete_contact_list, &mock_delete_email_template,]
    );

    let mut workflow = SESWorkflow::new(client, &mut stdin, &mut stdout);

    // Run the method
    let result = workflow.cleanup().await;

    // Assert the output
    assert!(result.is_err());
    assert!(format!("{result:?}").contains("Error deleting contact list:"));

    Ok(())
}

#[tokio::test]
async fn test_cleanup_error_template_not_found() -> Result<()> {
    let mut stdin = Cursor::new("n\n".as_bytes());
    let mut stdout = Vec::<u8>::new();

    // Mock AWS SDK calls
    let mock_delete_contact_list = mock!(Client::delete_contact_list)
        .match_requests(|req| req.contact_list_name() == Some("weekly-coupons-newsletter"))
        .then_output(|| DeleteContactListOutput::builder().build());

    let mock_delete_email_template = mock!(Client::delete_email_template)
        .match_requests(|req| req.template_name() == Some("weekly-coupons"))
        .then_error(|| {
            DeleteEmailTemplateError::NotFoundException(NotFoundException::builder().build())
        });

    let client = mock_client!(
        aws_sdk_sesv2,
        RuleMode::Sequential,
        &[&mock_delete_contact_list, &mock_delete_email_template,]
    );

    let mut workflow = SESWorkflow::new(client, &mut stdin, &mut stdout);

    // Run the method
    let result = workflow.cleanup().await;

    // Assert the output
    let output = String::from_utf8(stdout)?;
    assert!(output.contains("Contact list deleted successfully."));

    assert!(result.is_err());
    assert!(format!("{result:?}").contains("Error deleting email template:"));

    Ok(())
}

#[tokio::test]
async fn test_cleanup_error_identity_not_found() -> Result<()> {
    let mut stdin = Cursor::new("y\n".as_bytes());
    let mut stdout = Vec::<u8>::new();

    // Mock AWS SDK calls
    let mock_delete_contact_list = mock!(Client::delete_contact_list)
        .match_requests(|req| req.contact_list_name() == Some("weekly-coupons-newsletter"))
        .then_output(|| DeleteContactListOutput::builder().build());

    let mock_delete_email_template = mock!(Client::delete_email_template)
        .match_requests(|req| req.template_name() == Some("weekly-coupons"))
        .then_output(|| DeleteEmailTemplateOutput::builder().build());

    let mock_delete_email_identity = mock!(Client::delete_email_identity)
        .match_requests(|req| req.email_identity() == Some("test@example.com"))
        .then_error(|| {
            DeleteEmailIdentityError::NotFoundException(NotFoundException::builder().build())
        });

    let client = mock_client!(
        aws_sdk_sesv2,
        RuleMode::Sequential,
        &[
            &mock_delete_contact_list,
            &mock_delete_email_template,
            &mock_delete_email_identity,
        ]
    );

    let mut workflow = SESWorkflow::new(client, &mut stdin, &mut stdout);
    workflow.set_verified_email("test@example.com".into());

    // Run the method
    let result = workflow.cleanup().await;

    // Assert the output
    let output = String::from_utf8(stdout)?;
    assert!(output.contains("Contact list deleted successfully."));
    assert!(output.contains("Email template deleted successfully."));

    assert!(result.is_err());
    assert!(format!("{result:?}").contains("Error deleting email identity:"));

    Ok(())
}
