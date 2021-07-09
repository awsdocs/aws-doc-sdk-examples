/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use std::process::exit;

/// Sends a message to and receives the message from a queue.
#[tokio::main]
async fn main() -> Result<(), sqs::Error> {
    tracing_subscriber::fmt::init();
    let client = sqs::Client::from_env();
    let queues = client.list_queues().send().await?;
    let mut queue_urls = queues.queue_urls.unwrap_or_default();
    let queue_url = match queue_urls.pop() {
        Some(url) => url,
        None => {
            eprintln!("No queues in this account. Please create a queue to proceed");
            exit(1);
        }
    };

    println!(
        "Sending and receiving messages on with URL: `{}`",
        queue_url
    );

    let rsp = client
        .send_message()
        .queue_url(&queue_url)
        .message_body("hello from my queue")
        .message_group_id("MyGroup")
        .send()
        .await?;

    println!("Response from sending a message: {:#?}", rsp);

    let rcv_message_output = client
        .receive_message()
        // TODO: this should not be required, https://github.com/awslabs/smithy-rs/issues/439
        .max_number_of_messages(1)
        .queue_url(&queue_url)
        .send()
        .await?;

    for message in rcv_message_output.messages.unwrap_or_default() {
        println!("Got the message: {:#?}", message);
    }

    Ok(())
}
