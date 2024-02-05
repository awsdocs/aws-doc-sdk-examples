// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#![allow(clippy::result_large_err)]

use async_recursion::async_recursion;
use std::{
    collections::HashSet,
    env,
    fmt::Display,
    time::{Duration, Instant},
};
use tracing::info;

use aws_config::BehaviorVersion;
use aws_sdk_cloudwatchlogs::{
    error::ProvideErrorMetadata,
    operation::get_query_results::GetQueryResultsOutput,
    types::{QueryStatus, ResultField},
    Client, Error,
};
use chrono::{DateTime, Utc};

use sdk_examples_test_utils::wait_on;

#[derive(Debug)]
enum LargeQueryError {
    DateOutOfBounds,
    FromCloudwatchLogs(Error),
    FromChronoParse(chrono::ParseError),
}

struct DateRange(DateTime<Utc>, DateTime<Utc>);
impl DateRange {
    fn split(&self) -> (DateRange, DateRange) {
        let mid = (self.1 - self.0) / 2;
        (
            DateRange(self.0, self.0 + mid),
            DateRange(self.0 + mid + Duration::from_millis(1), self.1),
        )
    }
}

impl Display for DateRange {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "from {} to {}", self.0.format("%+"), self.1.format("%+"))
    }
}

struct CloudWatchLongQuery {
    client: Client,
    log_group_name: String,
    date_range: DateRange,
    limit: i32,
    results: Vec<Vec<ResultField>>,
    elapsed_time: Option<Duration>,
    status_done: HashSet<QueryStatus>,
}

impl CloudWatchLongQuery {
    fn new(client: Client, log_group_name: String, date_range: DateRange) -> Self {
        Self {
            client,
            log_group_name,
            date_range,
            limit: 10_000,
            results: vec![],
            elapsed_time: None,
            status_done: HashSet::from([
                QueryStatus::Complete,
                QueryStatus::Failed,
                QueryStatus::Cancelled,
                QueryStatus::Timeout,
            ]),
        }
    }

    pub async fn run(&mut self) -> Result<(), LargeQueryError> {
        self.elapsed_time = None;
        self.results.clear();
        let start = Instant::now();

        let results = self.large_query(&self.date_range).await;
        self.elapsed_time = Some(start.elapsed());

        results.map(|e| {
            self.results = e;
        })
    }

    #[async_recursion]
    async fn large_query(
        &self,
        date_range: &DateRange,
    ) -> Result<Vec<Vec<ResultField>>, LargeQueryError> {
        info!("Running query {date_range}");

        let logs = self.query(date_range).await?;

        info!(
            "Query date range {date_range} found {} entries.",
            logs.len()
        );

        if logs.len() < self.limit.try_into().unwrap() {
            return Ok(logs);
        }

        let mut last_log_date: DateTime<Utc> = get_last_log_date(&logs)?;
        last_log_date += Duration::from_millis(1);
        let (r1, r2) = DateRange(last_log_date, date_range.1).split();

        let logs = [
            logs,
            self.large_query(&r1).await?,
            self.large_query(&r2).await?,
        ]
        .concat();

        Ok(logs)
    }

    async fn get_query_results(
        &self,
        query_id: &str,
    ) -> Result<GetQueryResultsOutput, LargeQueryError> {
        wait_on!(
            self.client.get_query_results().query_id(query_id),
            |get_query_results| {
                eprintln!("{:?}", get_query_results.status);
                self.status_done.contains(
                    get_query_results
                        .status()
                        .unwrap_or(&QueryStatus::UnknownValue),
                )
            }
        )
        .await
        .map_err(|err| LargeQueryError::FromCloudwatchLogs(err.into()))
    }

    async fn query(
        &self,
        date_range: &DateRange,
    ) -> Result<Vec<Vec<ResultField>>, LargeQueryError> {
        let query_id = self.start_query(date_range).await?;
        info!("Started query {date_range} as {query_id}");
        let results = self.get_query_results(query_id.as_str()).await?;
        info!("Finished query {query_id}");
        Ok(results.results.unwrap_or_default())
    }

    async fn start_query(&self, date_range: &DateRange) -> Result<String, LargeQueryError> {
        let response = self
            .client
            .start_query()
            .log_group_name(self.log_group_name.clone())
            .query_string("fields @timestamp, @message | sort @timestamp asc")
            .start_time(date_range.0.timestamp())
            .end_time(date_range.1.timestamp())
            .limit(self.limit)
            .send()
            .await;
        match response {
            Ok(start) => Ok(start.query_id.expect("start query query_id")),
            Err(err) => {
                if err
                    .message()
                    .unwrap_or_default()
                    .starts_with("Query's end date and time")
                {
                    Err(LargeQueryError::DateOutOfBounds)
                } else {
                    Err(LargeQueryError::FromCloudwatchLogs(err.into()))
                }
            }
        }
    }
}

fn get_last_log_date(results: &[Vec<ResultField>]) -> Result<DateTime<Utc>, LargeQueryError> {
    let last = results
        .last()
        .expect("last query item")
        .iter()
        .find(|e| matches!(e.field(), Some("@timestamp")))
        .expect("timestamp field")
        .value()
        .expect("timestamp field value");

    DateTime::parse_from_rfc3339(format!("{last}Z").as_str())
        .map(|e| e.to_utc())
        .map_err(LargeQueryError::FromChronoParse)
}

#[tokio::main]
async fn main() -> Result<(), LargeQueryError> {
    tracing_subscriber::fmt::init();

    let group = env::var("QUERY_GROUP").unwrap_or("/workflows/cloudwatch-logs/large-query".into());
    let start_date = DateTime::from_timestamp_millis(
        env::var("QUERY_START_DATE")
            .expect("lookup QUERY_START_DATE")
            .parse()
            .unwrap(),
    )
    .expect("parse start date")
    .with_timezone(&Utc);
    let end_date = DateTime::from_timestamp_millis(
        env::var("QUERY_END_DATE")
            .expect("lookup QUERY_END_DATE")
            .parse()
            .unwrap(),
    )
    .expect("parse end date")
    .with_timezone(&Utc);

    println!("{group}");
    println!("{start_date}");
    println!("{end_date}");

    let shared_config = aws_config::from_env()
        .behavior_version(BehaviorVersion::latest())
        .load()
        .await;
    let client = Client::new(&shared_config);

    let mut query = CloudWatchLongQuery::new(client, group, DateRange(start_date, end_date));

    query.run().await.expect("run query to completion");

    eprintln!("Total results: {}", query.results.len());

    Ok(())
}
