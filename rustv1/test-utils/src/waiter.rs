// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

use std::{
    error::Error,
    fmt::Display,
    time::{Duration, SystemTime},
};
use tokio;

// Wait at most 25 seconds.
const MAX_WAIT: Duration = Duration::from_secs(5 * 60);
// Wait half a second at a time.
const DEFAULT_INTERVAL: Duration = Duration::from_millis(500);

#[derive(Debug)]
pub struct WaitError(Duration);
impl Error for WaitError {}
impl Display for WaitError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(
            f,
            "Waiter expired while sleeping for {:.3}s",
            self.0.as_secs_f32()
        )
    }
}

pub struct Waiter {
    start: SystemTime,
    max: Duration,
    interval: Duration,
}

impl Waiter {
    pub fn builder() -> WaiterBuilder {
        WaiterBuilder::default()
    }

    /**
     * *
     */
    fn new(max: Duration, interval: Duration) -> Self {
        Waiter {
            start: SystemTime::now(),
            max,
            interval,
        }
    }

    pub async fn sleep(&self) -> Result<(), WaitError> {
        if SystemTime::now()
            .duration_since(self.start)
            .unwrap_or(Duration::MAX)
            > self.max
        {
            Err(WaitError(self.max))
        } else {
            tokio::time::sleep(self.interval).await;
            Ok(())
        }
    }
}

impl Default for Waiter {
    fn default() -> Self {
        Waiter::new(MAX_WAIT, DEFAULT_INTERVAL)
    }
}

#[derive(Default)]
pub struct WaiterBuilder {
    max: Option<Duration>,
    poll: Option<Duration>,
}

impl WaiterBuilder {
    pub fn poll(mut self, poll: Duration) -> Self {
        self.poll = Some(poll);
        self
    }

    pub fn max(mut self, max: Duration) -> Self {
        self.max = Some(max);
        self
    }

    pub fn build(self) -> Waiter {
        Waiter::new(
            self.max.unwrap_or(MAX_WAIT),
            self.poll.unwrap_or(DEFAULT_INTERVAL),
        )
    }
}

/// Create an async block to repeat a request until the result of the test is true.
/// The test will work on a completed request - that is, retries, errors, etc will happen before
/// the test is called. The response is considered done and successful by the SDK.
/// For example, this can be used to wait for a long-running operation to change to `status: Done|Cancelled`.
///
/// - $waiter is a Waiter used to sleep between attempts, with a maximum timeout.
/// - $req is an expr that evaluates to an API call that can be `.clone().send().await`ed.
/// - $test is an expr that should be an Fn which gets passed the successful response of the
#[macro_export]
macro_rules! wait_on {
    (
        $req: expr,
        $test: expr
    ) => {
        wait_on!($crate::waiter::Waiter::default(), $req, $test)
    };
    (
        $waiter: expr,
        $req: expr,
        $test: expr
    ) => {
        async {
            let mut sleep = Ok(());
            let mut response = $req.clone().send().await;
            while sleep.is_ok() {
                if response.as_ref().map($test).unwrap_or(false) {
                    break;
                }
                response = $req.clone().send().await;
                sleep = $waiter.sleep().await;
            }
            response
        }
    };
}
