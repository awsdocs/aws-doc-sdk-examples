/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

use aws_sdk_sqs::Client;
use clap::Parser;
use concurrency::Runtime;

const DEFAULT_CONCURRENCY_LIMIT: usize = 1_000;
const DEFAULT_RUNTIME: Runtime = Runtime::MultiThreaded;
const DEFAULT_TASK_COUNT: usize = 10_000;

#[derive(Parser, Debug)]
#[command(author, version, about, long_about = None)]
struct Args {
    /// The URL of the Message Queue to send test messages to.
    #[arg(long)]
    message_queue_url: String,

    /// The total number of test messages to send to the Message Queue.
    #[arg(long, default_value_t = DEFAULT_TASK_COUNT)]
    task_count: usize,

    /// The maximum number of test messages to send to the Message Queue at a time.
    #[arg(long, default_value_t = DEFAULT_CONCURRENCY_LIMIT)]
    concurrency_limit: usize,

    /// The runtime to use when running the tasks.
    #[arg(long, default_value_t = DEFAULT_RUNTIME)]
    runtime: Runtime,
}

fn main() {
    let args = Args::parse();

    let runtime = match args.runtime {
        Runtime::SingleThreaded => tokio::runtime::Builder::new_current_thread().build(),
        Runtime::MultiThreaded => tokio::runtime::Runtime::new(),
    }
    .unwrap();

    runtime.block_on(async move { async_main(args).await })
}

async fn async_main(args: Args) {
    // If you start encountering timeout errors, increase or disable the default timeouts.
    let sdk_config = aws_config::load_from_env().await;
    let client = Client::new(&sdk_config);

    let send_message_futures = (0..args.task_count).map(|i| {
        let message_body = format!("concurrency test message #{i}");
        let fut = client
            .send_message()
            .queue_url(&args.message_queue_url)
            .message_body(message_body)
            .send();

        async move {
            // We unwrap here in order to stop running futures as soon as one of them fails.
            fut.await.expect("request should succeed")
        }
    });

    let _res =
        concurrency::run_futures_concurrently(send_message_futures, args.concurrency_limit).await;
}
