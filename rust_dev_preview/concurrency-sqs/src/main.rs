/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

use aws_config::timeout::TimeoutConfig;
use aws_sdk_sqs::Client;
use futures::future;
use std::sync::Arc;
use tokio::sync::Semaphore;
use tokio::time::{Duration, Instant};
use tracing::{debug, info};

const TASK_COUNT: usize = 10_000;
const CONCURRENCY_LIMIT: usize = 1_000;
const MESSAGE_QUEUE_URL: &str = "https://sqs.us-east-1.amazonaws.com/773090468836/test_queue";

fn main() {
    let runtime = {
        // Uncomment this line to run the example in a multi-threaded executor
        tokio::runtime::Runtime::new()
        // Uncomment this line to run the example in a single-threaded executor
        // tokio::runtime::Builder::new_current_thread().build()
    }.unwrap();

    runtime.block_on(send_sqs_messages())
}

async fn send_sqs_messages() {
    tracing_subscriber::fmt::init();

    info!("Sending {TASK_COUNT} messages, {CONCURRENCY_LIMIT} at a time");

    let sdk_config = aws_config::from_env()
        .timeout_config(
            TimeoutConfig::builder()
                .connect_timeout(Duration::from_secs(30))
                .read_timeout(Duration::from_secs(30))
                .build(),
        )
        .load()
        .await;
    let client = Client::new(&sdk_config);

    // a tokio semaphore can be used to ensure we only run up to <CONCURRENCY_LIMIT> requests
    // at once.
    let semaphore = Arc::new(Semaphore::new(CONCURRENCY_LIMIT));

    let task_creation_start = Instant::now();
    // create all the PutObject requests
    let futures: Vec<_> = (0..TASK_COUNT)
        // create a PutObject request for each payload
        .map(|i| {
            let client = client.clone();
            let key = format!("message_{:05}", i);
            let fut = client
                .send_message()
                .queue_url(MESSAGE_QUEUE_URL)
                .message_body(key)
                .send();
            // make a clone of the semaphore that can live in the future
            let semaphore = semaphore.clone();

            // because we wait on a permit from the semaphore, only <CONCURRENCY_LIMIT> futures
            // will be run at once.
            async move {
                let permit = semaphore
                    .acquire()
                    .await
                    .expect("we'll get one if we wait long enough");
                let res = fut.await.expect("request should succeed");
                drop(permit);
                res
            }
        })
        .collect();

    let task_creation_elapsed = task_creation_start.elapsed();

    debug!(
        "created {} futures in {task_creation_elapsed:?}",
        TASK_COUNT
    );
    debug!("running futures concurrently with future::join_all");
    let start = Instant::now();
    let res: Vec<_> = future::join_all(futures).await;
    debug!("future_joined_successfully, asserting that all tasks were run...");
    assert_eq!(TASK_COUNT, res.len());
    info!(
        "all {TASK_COUNT} tasks completed after {:?}",
        start.elapsed()
    );
}
