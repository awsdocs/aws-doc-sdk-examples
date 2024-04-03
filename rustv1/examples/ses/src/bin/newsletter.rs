// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

use anyhow::{anyhow, Result};
use aws_sdk_sesv2::Client;
use ses_code_examples::newsletter::SESWorkflow;

const INTRO: &str = "
Welcome to the Amazon SES v2 Coupon Newsletter Workflow!

This workflow will help you:
1. Prepare a verified email identity and contact list for your newsletter.
2. Gather subscriber email addresses and send them a welcome email.
3. Send a weekly coupon newsletter to your subscribers using email templates.
4. Monitor your sending activity and metrics in the AWS console.

Let's get started!
";

/// The main entry point of the newsletter workflow.
///
/// # Arguments
///
/// None
#[tokio::main]
async fn main() -> Result<()> {
    tracing_subscriber::fmt::init();

    println!("{}", INTRO);

    // Initialize the SES client
    let config = aws_config::from_env().load().await;
    let client = Client::new(&config);

    // Initialize the SESWorkflow struct
    let mut stdin = std::io::stdin().lock();
    let mut stdout = std::io::stdout().lock();
    let mut workflow = SESWorkflow::new(client, &mut stdin, &mut stdout);

    // Execute the workflow steps
    let run = workflow.run().await;
    if let Err(e) = run {
        return Err(anyhow!("Error in run: {e}"));
    }
    let _ = workflow.cleanup().await;

    Ok(())
}
