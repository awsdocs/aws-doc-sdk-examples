// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#![allow(clippy::result_large_err)]

use async_recursion::async_recursion;
use futures::join;
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
    // Internal error is used in Debug output
    #[allow(dead_code)]
    FromCloudwatchLogs(Error),
    // Internal error is used in Debug output
    #[allow(dead_code)]
    FromChronoParse(chrono::ParseError),
}

#[derive(Clone)]
pub struct DateRange(DateTime<Utc>, DateTime<Utc>);
impl DateRange {
    pub fn split(&self) -> (DateRange, DateRange) {
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

        let (a, b) = join!(self.large_query(&r1), self.large_query(&r2));

        let logs = [logs, a?, b?].concat();

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

#[cfg(test)]
mod test {
    use super::*;
    use aws_sdk_cloudwatchlogs::operation::start_query::StartQueryOutput;
    use aws_smithy_mocks_experimental::{mock, mock_client, RuleMode};
    use chrono::{TimeZone, Utc};

    // Test the behavior of the DateRange::split function.
    #[tokio::test]
    async fn test_date_range_split() {
        let start_date = DateTime::parse_from_rfc3339("2024-02-01 12:00:00Z")
            .unwrap()
            .with_timezone(&Utc);
        let end_date = DateTime::parse_from_rfc3339("2024-02-10 12:00:00Z")
            .unwrap()
            .with_timezone(&Utc);
        let date_range = DateRange(start_date, end_date);

        // Act: Call the split method on this DateRange instance.
        let (first_half, second_half) = date_range.split();

        // Assert: Verify that the resulting DateRanges cover the entire span of the original DateRange without overlap or gaps.
        assert_eq!(
            first_half.0, start_date,
            "First half should start at start date"
        );
        assert_eq!(
            second_half.1, end_date,
            "Second half should end at end date"
        );

        assert_eq!(
            first_half.1,
            second_half.0 - Duration::from_millis(1),
            "No separation from start and end dates"
        );
    }

    // Test the large_query method for a range with less than the limit of log entries.
    #[tokio::test]
    async fn test_large_query_with_small_range() {
        let start_date = DateTime::parse_from_rfc3339("2024-02-01 12:00:00Z")
            .unwrap()
            .with_timezone(&Utc);
        let end_date = DateTime::parse_from_rfc3339("2024-02-10 12:00:00Z")
            .unwrap()
            .with_timezone(&Utc);
        let date_range = DateRange(start_date, end_date);

        // Arrange: Set up a scenario where the query returns fewer logs than the limit.
        let start_query = mock!(Client::start_query)
            .then_output(|| StartQueryOutput::builder().query_id("1").build());
        let small_result = mock!(Client::get_query_results)
            .match_requests(|req| matches!(req.query_id(), Some("1")))
            .then_output(|| {
                GetQueryResultsOutput::builder()
                    .status(QueryStatus::Complete)
                    .set_results(Some(vec![
                        vec![
                            ResultField::builder()
                                .field("@message")
                                .value("test 1")
                                .build(),
                            ResultField::builder()
                                .field("@timestamp")
                                .value("2024-02-02 12:00:00")
                                .build(),
                        ],
                        vec![
                            ResultField::builder()
                                .field("@message")
                                .value("test 2")
                                .build(),
                            ResultField::builder()
                                .field("@timestamp")
                                .value("2024-02-03 12:00:00")
                                .build(),
                        ],
                    ]))
                    .build()
            });

        let client = mock_client!(aws_sdk_cloudwatchlogs, &[&start_query, &small_result]);

        let query = CloudWatchLongQuery::new(client, "testing".into(), date_range.clone());
        // Act: Invoke the large_query method with this range.
        let response = query.large_query(&date_range).await.unwrap();

        // Assert: Ensure that the method returns the correct logs without further splitting the range.
        assert_eq!(start_query.num_calls(), 1);
        assert_eq!(response.len(), 2);
    }

    // Test the get_query_results method's handling of different query statuses.
    #[tokio::test]
    async fn test_get_query_results_statuses_for_waiter() {
        let start_date = DateTime::parse_from_rfc3339("2024-02-01 12:00:00Z")
            .unwrap()
            .with_timezone(&Utc);
        let end_date = DateTime::parse_from_rfc3339("2024-02-10 12:00:00Z")
            .unwrap()
            .with_timezone(&Utc);
        let date_range = DateRange(start_date, end_date);

        let get_query_results_0 = mock!(Client::get_query_results).then_output(|| {
            GetQueryResultsOutput::builder()
                .status(QueryStatus::Running)
                .build()
        });
        let get_query_results_1 = mock!(Client::get_query_results).then_output(|| {
            GetQueryResultsOutput::builder()
                .status(QueryStatus::Complete)
                .set_results(Some(vec![
                    vec![
                        ResultField::builder()
                            .field("@message")
                            .value("test 1")
                            .build(),
                        ResultField::builder()
                            .field("@timestamp")
                            .value("2024-02-02 12:00:00")
                            .build(),
                    ],
                    vec![
                        ResultField::builder()
                            .field("@message")
                            .value("test 2")
                            .build(),
                        ResultField::builder()
                            .field("@timestamp")
                            .value("2024-02-03 12:00:00")
                            .build(),
                    ],
                ]))
                .build()
        });

        let client = mock_client!(
            aws_sdk_cloudwatchlogs,
            RuleMode::Sequential,
            &[&get_query_results_0, &get_query_results_1]
        );

        // Arrange: Mock different responses from CloudWatch Logs with varying statuses.
        let query = CloudWatchLongQuery::new(client, "testing".into(), date_range.clone());
        let query_id = "1";

        // Act: Call the get_query_results method with these mocked responses.
        let response = query.get_query_results(query_id).await.unwrap();

        // Assert: Verify that the method handles different statuses correctly, particularly error statuses.
        assert_eq!(get_query_results_0.num_calls(), 1);
        assert_eq!(get_query_results_1.num_calls(), 1);
        assert_eq!(response.results.unwrap().len(), 2);
    }

    // Test for correct parsing of timestamps in get_last_log_date.
    #[tokio::test]
    async fn test_get_last_log_date_parsing() {
        // Arrange: Provide a set of log entries with known timestamps.
        let results = vec![
            vec![ResultField::builder()
                .field("@timestamp")
                .value("2022-01-01T12:00:00")
                .build()],
            vec![ResultField::builder()
                .field("@timestamp")
                .value("2022-01-02T12:00:00")
                .build()],
            vec![ResultField::builder()
                .field("@timestamp")
                .value("2022-01-03T12:00:00")
                .build()],
        ];

        // Act: Call the get_last_log_date function with these log entries.
        let last_log_date = get_last_log_date(&results).unwrap();

        // Assert: Verify that the function returns the correct last timestamp.
        assert_eq!(
            Some(last_log_date),
            Utc.with_ymd_and_hms(2022, 1, 3, 12, 0, 0).single()
        );
    }
}
