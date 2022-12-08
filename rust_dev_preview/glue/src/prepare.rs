// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0.

use crate::{
    clients::{GLUE_CLIENT, S3_CLIENT},
    GlueMvpError, GlueScenario, CRAWLER_TARGET,
};
use aws_sdk_glue::model::{
    Crawler, CrawlerState, CrawlerTargets, DatabaseInput, Job, JobCommand, S3Target, Table,
};
use aws_sdk_s3::types::ByteStream;
use secrecy::ExposeSecret;
use std::future::Future;
use tracing::{info, instrument, warn};

pub trait SetupGlueScenarioExt {
    fn prepare_crawler(&self) -> dyn Future<Output = Result<Crawler, GlueMvpError>>;
    fn wait_for_crawler(&self) -> dyn Future<Output = Result<(), GlueMvpError>>;
    fn verify_crawler(&self) -> dyn Future<Output = Result<Vec<Table>, GlueMvpError>>;
    fn prepare_job(&self, _crawler: &Crawler) -> dyn Future<Output = Result<Job, GlueMvpError>>;
}

// Create the crawler, if it doesn't exist, and start it.
impl GlueScenario {
    pub async fn prepare_crawler(&self) -> Result<Crawler, GlueMvpError> {
        let glue = GLUE_CLIENT.get().await;

        let create_database = glue
            .create_database()
            .database_input(DatabaseInput::builder().name(self.database()).build())
            .send()
            .await;

        if let Err(sdk_err) = create_database {
            match sdk_err {
                aws_smithy_client::SdkError::ServiceError { ref err, raw: _ } => {
                    if err.is_already_exists_exception() {
                        info!("Found existing database");
                        Ok(())
                    } else {
                        Err(GlueMvpError::from_glue_sdk(sdk_err))
                    }
                }
                _ => Err(GlueMvpError::from_glue_sdk(sdk_err)),
            }?;
        }

        // snippet-start:[rust.glue.create_crawler]
        let create_crawler = glue
            .create_crawler()
            .name(self.crawler())
            .database_name(self.database())
            .role(self.iam_role.expose_secret())
            .targets(
                CrawlerTargets::builder()
                    .s3_targets(S3Target::builder().path(CRAWLER_TARGET).build())
                    .build(),
            )
            .send()
            .await;

        match create_crawler {
            Err(err) => {
                let glue_err: aws_sdk_glue::Error = err.into();
                match glue_err {
                    aws_sdk_glue::Error::AlreadyExistsException(_) => {
                        info!("Using existing crawler");
                        Ok(())
                    }
                    _ => Err(GlueMvpError::GlueSdk(glue_err)),
                }
            }
            Ok(_) => Ok(()),
        }?;
        // snippet-end:[rust.glue.create_crawler]

        // snippet-start:[rust.glue.start_crawler]
        let start_crawler = glue.start_crawler().name(self.crawler()).send().await;

        match start_crawler {
            Ok(_) => Ok(()),
            Err(err) => {
                let glue_err: aws_sdk_glue::Error = err.into();
                match glue_err {
                    aws_sdk_glue::Error::CrawlerRunningException(_) => Ok(()),
                    _ => Err(GlueMvpError::GlueSdk(glue_err)),
                }
            }
        }?;
        // snippet-end:[rust.glue.start_crawler]

        let crawler = glue
            .get_crawler()
            .name(self.crawler())
            .send()
            .await
            .map_err(GlueMvpError::from_glue_sdk)?
            .crawler()
            .ok_or_else(|| GlueMvpError::Unknown("Failed to get crawler".into()))?
            .to_owned();

        Ok(crawler)
    }

    #[instrument(skip(self, crawler), fields(crawler.last_updated, crawler.last_crawl))]
    pub async fn wait_for_crawler(&self, crawler: &mut Crawler) -> Result<(), GlueMvpError> {
        let glue = GLUE_CLIENT.get().await;
        let unknown_state = CrawlerState::Unknown("".into());
        let mut state = crawler.state().unwrap_or(&unknown_state).to_owned();

        // GetCrawler
        while state != CrawlerState::Ready {
            warn!(?state, "CrawlerState");
            tokio::time::sleep(self.wait_delay).await;

            // snippet-start:[rust.glue.get_crawler]
            let tmp_crawler = glue
                .get_crawler()
                .name(self.crawler())
                .send()
                .await
                .map_err(GlueMvpError::from_glue_sdk)?;
            // snippet-end:[rust.glue.get_crawler]

            let mut tmp_crawler = tmp_crawler
                .crawler()
                .ok_or_else(|| GlueMvpError::Unknown("Failed to get crawler".into()))?
                .to_owned();

            std::mem::swap(crawler, &mut tmp_crawler);

            state = crawler.state().unwrap_or(&unknown_state).to_owned();
        }

        Ok(())
    }

    /// Get the database created by the crawler and the tables in the database.
    /// Display these to the user.
    #[instrument(skip(self, crawler))]
    pub async fn verify_crawler(&self, crawler: &mut Crawler) -> Result<Vec<Table>, GlueMvpError> {
        let glue = GLUE_CLIENT.get().await;

        self.wait_for_crawler(crawler).await?;

        // snippet-start:[rust.glue.get_database]
        let database = glue
            .get_database()
            .name(self.database())
            .send()
            .await
            .map_err(GlueMvpError::from_glue_sdk)?
            .to_owned();
        let database = database
            .database()
            .ok_or_else(|| GlueMvpError::Unknown("Could not find database".into()))?;
        // snippet-end:[rust.glue.get_database]

        info!(?database, "Found crawler database");

        // snippet-start:[rust.glue.get_tables]
        let tables = glue
            .get_tables()
            .database_name(self.database())
            .send()
            .await
            .map_err(GlueMvpError::from_glue_sdk)?;

        let tables = tables
            .table_list()
            .ok_or_else(|| GlueMvpError::Unknown("No tables in database".into()))?;
        // snippet-end:[rust.glue.get_tables]

        Ok(Vec::from(tables))
    }

    pub async fn ensure_s3_bucket(&self) -> Result<(), GlueMvpError> {
        let s3 = S3_CLIENT.get().await;

        let _ = s3.create_bucket().bucket(self.bucket()).send().await;

        s3.put_bucket_policy()
            .bucket(self.bucket())
            .policy(format!(
                r#"{{
    "Version": "2012-10-17",
    "Statement": [
        {{
            "Sid": "Statement1",
            "Effect": "Allow",
            "Principal": {{
                "AWS": "{}"
            }},
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::{}/*"
        }}
    ]
}}"#,
                self.iam_role.expose_secret(),
                self.bucket()
            ))
            .send()
            .await
            .map_err(GlueMvpError::from_s3_sdk)?;

        Ok(())
    }

    #[instrument(skip(self))]
    pub async fn prepare_s3(&self) -> Result<(), GlueMvpError> {
        let s3 = S3_CLIENT.get().await;

        self.ensure_s3_bucket().await?;

        s3.put_object()
            .bucket(self.bucket())
            .key("job.py")
            .body(
                ByteStream::from_path(self.script.clone())
                    .await
                    .map_err(|err| GlueMvpError::Unknown(format!("{err:?}")))?,
            )
            .send()
            .await
            .map_err(GlueMvpError::from_s3_sdk)?;

        let put_object = s3
            .put_object()
            .bucket(self.bucket.as_str())
            .key("setup_scenario_getting_started.yaml")
            .body(
                ByteStream::from_path(self.config.clone())
                    .await
                    .map_err(|err| GlueMvpError::Unknown(format!("{err:?}")))?,
            )
            .send()
            .await;

        if let Err(err) = put_object {
            return Err(GlueMvpError::S3Sdk(err.into()));
        }

        Ok(())
    }

    // Upload Python ETL script to the user's Amazon Simple Storage Service (Amazon S3) bucket. It looks something like this: s3://doc-example-bucket-123456/flight_etl_job_script.py.
    // Create a job, pass it the AWS Identity and Access Management (IAM) role and the URL to the uploaded script.
    #[instrument(skip(self))]
    pub async fn prepare_job(self: &GlueScenario) -> Result<Job, GlueMvpError> {
        let glue = GLUE_CLIENT.get().await;

        // CreateJob
        // snippet-start:[rust.glue.create_job]
        let create_job = glue
            .create_job()
            .name(self.job())
            .role(self.iam_role.expose_secret())
            .command(
                JobCommand::builder()
                    .name("glueetl")
                    .python_version("3")
                    .script_location(format!("s3://{}/job.py", self.bucket()))
                    .build(),
            )
            .glue_version("3.0")
            .send()
            .await
            .map_err(GlueMvpError::from_glue_sdk)?;

        let job_name = create_job.name().ok_or_else(|| {
            GlueMvpError::Unknown("Did not get job name after creating job".into())
        })?;
        // snippet-end:[rust.glue.create_job]

        let get_job = glue
            .get_job()
            .job_name(job_name)
            .send()
            .await
            .map_err(GlueMvpError::from_glue_sdk)?;

        let job = get_job
            .job()
            .ok_or_else(|| GlueMvpError::Unknown("Could not find created job".into()))?
            .to_owned();

        Ok(job)
    }
}
