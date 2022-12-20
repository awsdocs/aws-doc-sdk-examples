/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

use clap::ValueEnum;
use futures::future;
use std::fmt::{self, Display, Formatter};
use std::future::Future;
use std::sync::Arc;
use tokio::sync::Semaphore;
use tokio::time::Instant;
use tracing::{debug, info};

#[derive(Clone, Debug, ValueEnum)]
pub enum Runtime {
    /// Use the single-threaded async runtime to run concurrent requests.
    SingleThreaded,
    /// Use the multi-threaded async runtime to run concurrent requests.
    MultiThreaded,
}

impl Display for Runtime {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        write!(
            f,
            "{}",
            match self {
                Runtime::SingleThreaded => "single-threaded",
                Runtime::MultiThreaded => "multi-threaded",
            }
        )
    }
}

pub async fn run_futures_concurrently<I, T, F>(task_futures: I, concurrency_limit: usize) -> Vec<T>
where
    F: Future<Output = T>,
    I: Iterator<Item = F> + ExactSizeIterator,
{
    tracing_subscriber::fmt::init();
    let task_count = task_futures.len();
    info!("Sending {task_count} messages, {concurrency_limit} at a time");

    // a tokio semaphore can be used to ensure we only run up to <concurrency_limit> requests
    // at once.
    let semaphore = Arc::new(Semaphore::new(concurrency_limit));

    // Marry each task future with a semaphore. `future::join_all` takes iterators as input so
    // there's no need to `.collect()` here.
    let futures = task_futures.into_iter().map(|fut| {
        // make a clone of the semaphore that can live in the future
        let semaphore = semaphore.clone();
        // because we wait on a permit from the semaphore, only <concurrency_limit> futures
        // will be run at once.
        async move {
            let permit = semaphore
                .acquire()
                .await
                .expect("we'll get one if we wait long enough");
            let res = fut.await;
            drop(permit);
            res
        }
    });

    debug!("running futures concurrently with future::join_all");
    let start = Instant::now();
    let res: Vec<_> = future::join_all(futures).await;
    debug!("future_joined_successfully, asserting that all tasks were run...");
    assert_eq!(task_count, res.len());
    info!(
        "all {task_count} tasks completed after {:?}",
        start.elapsed()
    );

    res
}
