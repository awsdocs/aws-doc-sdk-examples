/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use std::{
    error::Error,
    fmt::Display,
    time::{Duration, SystemTime},
};
use tokio;

// Wait at most 25 seconds.
const MAX_WAIT: Duration = Duration::from_secs(5 * 60);
// Wait half a second at a time.
const POLL_TIME: Duration = Duration::from_millis(500);

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
    poll: Duration,
}

impl Waiter {
    pub fn builder() -> WaiterBuilder {
        WaiterBuilder::default()
    }

    fn new(max: Duration, poll: Duration) -> Self {
        Waiter {
            start: SystemTime::now(),
            max,
            poll,
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
            tokio::time::sleep(self.poll).await;
            Ok(())
        }
    }
}

impl Default for Waiter {
    fn default() -> Self {
        Waiter::new(MAX_WAIT, POLL_TIME)
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
        Waiter::new(self.max.unwrap_or(MAX_WAIT), self.poll.unwrap_or(POLL_TIME))
    }
}
