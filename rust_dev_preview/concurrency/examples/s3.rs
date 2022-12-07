/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

use aws_sdk_s3::types::ByteStream;
use aws_sdk_s3::Client;
use clap::Parser;
use concurrency::Runtime;
use std::iter::repeat_with;

const DEFAULT_CONCURRENCY_LIMIT: usize = 1_000;
const DEFAULT_KEY_PREFIX: &str = "concurrency_test/object";
const DEFAULT_OBJECT_SIZE_IN_BYTES: usize = 100_000;
const DEFAULT_RUNTIME: Runtime = Runtime::MultiThreaded;
const DEFAULT_TASK_COUNT: usize = 10_000;

#[derive(Parser, Debug)]
#[command(author, version, about, long_about = None)]
struct Args {
    /// The name of the S3 bucket that test objects will be uploaded to.
    #[arg(long)]
    bucket: String,

    /// The prefix used to create keys for objects to be uploaded. For example, with the default
    /// prefix 'concurrency_test/object', the key of the first uploaded object will have the key
    /// 'concurrency_test/object_00000.txt'.
    #[arg(long, default_value_t = DEFAULT_KEY_PREFIX.to_string())]
    key_prefix: String,

    /// The size of each uploaded object in bytes (100KB by default.) Each object will be a random
    /// alphanumeric string. The total amount of data uploaded will be equal to
    /// <task-count> * <object_size_in_bytes>. Larger sizes cause task creation to take more time.
    /// All of the strings are stored in memory while sending the requests so make sure you have
    /// enough RAM before setting this to a very large value.
    #[arg(long, default_value_t = DEFAULT_OBJECT_SIZE_IN_BYTES)]
    object_size_in_bytes: usize,

    /// The total number of objects to upload to the S3 bucket.
    #[arg(long, default_value_t = DEFAULT_TASK_COUNT)]
    task_count: usize,

    /// The maximum number of uploads running at a time.
    #[arg(long, default_value_t = DEFAULT_CONCURRENCY_LIMIT)]
    concurrency_limit: usize,

    /// The runtime to use when running the tasks, either single or multi-threaded
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
    let sdk_config = aws_config::load_from_env().await;
    let client = Client::new(&sdk_config);

    let send_message_futures = (0..args.task_count).map(|i| {
        let key = format!("{}_{i:05}.txt", args.key_prefix);
        let body: Vec<_> = repeat_with(fastrand::alphanumeric)
            .take(args.object_size_in_bytes)
            .map(|c| c as u8)
            .collect();
        let fut = client
            .put_object()
            .bucket(&args.bucket)
            .key(key)
            .body(ByteStream::from(body))
            .send();

        async move {
            // We unwrap here in order to stop running futures as soon as one of them fails.
            fut.await.expect("request should succeed")
        }
    });

    let _res =
        concurrency::run_futures_concurrently(send_message_futures, args.concurrency_limit).await;
}
