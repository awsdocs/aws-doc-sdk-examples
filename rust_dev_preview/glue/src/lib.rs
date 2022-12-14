// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0.

pub mod cleanup;
pub mod clients;
pub mod prepare;
pub mod run;

use aws_sdk_glue::model::Table;
use clap::Parser;
use secrecy::Secret;
use std::time::Duration;
use tracing::warn;
use uuid::Uuid;

pub const CRAWLER_TARGET: &str = "s3://crawler-public-us-east-1/flight/2016/csv";
pub const EMPTY_TABLES: &Vec<Table> = &vec![];

const IAM_HELP: &str = r#"To create an appropriate role for running AWS Glue examples, apply the Amazon Cloud Development Kit (CDK) script in resources/cdk/glue_role_bucket.

--iam-role=$(aws iam get-role --role-name AWSGlueServiceRole-DocExample --output text --query Role.Arn)"#;

#[derive(Debug, clap::Parser)]
#[command(about)]
pub struct GlueScenarioArgs {
    #[arg(long, help=IAM_HELP)]
    pub iam_role: Secret<String>,
    #[arg(long, default_value = "rust_glue_mvp")]
    pub name: String,
    #[arg(long)]
    pub id: Option<String>,
    #[arg(long, default_value = "doc-example-glue")]
    pub bucket: String,
    #[arg(long)]
    pub cleanup: bool,
}

impl Default for GlueScenarioArgs {
    fn default() -> Self {
        Self {
            iam_role: Secret::new("arn:aws:iam::1111222233334444:role/missing".into()),
            name: "happy_test".into(),
            id: Some(Uuid::new_v4().to_string()),
            bucket: "happy_test_bucket".into(),
            cleanup: false,
        }
    }
}

#[derive(Debug)]
pub struct GlueScenario {
    iam_role: Secret<String>,
    crawler: String,
    database: String,
    bucket: String,
    job: String,
    script: String,
    config: String,
    should_cleanup: bool,
    wait_delay: Duration,
    tables: Vec<Table>,
    job_run_id: String,
}

impl GlueScenario {
    pub fn parse() -> Self {
        let args = GlueScenarioArgs::parse();
        GlueScenario::from_args(args)
    }

    pub fn from_args(args: GlueScenarioArgs) -> Self {
        let id = args.id.unwrap_or_else(|| format!("{}", Uuid::new_v4()));
        let base_name = format!("{}_{id}", args.name);
        let name = base_name.as_str();
        GlueScenario {
            iam_role: args.iam_role.to_owned(),
            crawler: format!("{name}_crawler"),
            database: format!("{name}_database"),
            bucket: format!("{name}_bucket").replace('_', "-"),
            job: format!("{name}_job"),
            script: "resources/flight_etl_job_script.py".to_string(),
            config: "resources/setup_scenario_getting_started.yaml".to_string(),
            should_cleanup: args.cleanup,
            wait_delay: Duration::from_secs(5),
            tables: vec![],
            job_run_id: String::new(),
        }
    }

    pub fn crawler(&self) -> &str {
        self.crawler.as_str()
    }

    pub fn database(&self) -> &str {
        self.database.as_str()
    }

    pub fn bucket(&self) -> &str {
        self.bucket.as_str()
    }

    pub fn job(&self) -> &str {
        self.job.as_str()
    }

    pub fn job_run_id(&self) -> &str {
        self.job_run_id.as_str()
    }
}

impl GlueScenario {
    pub async fn prepare(&mut self) -> Result<(), GlueMvpError> {
        self.prepare_s3().await?;

        let mut crawler = self.prepare_crawler().await?;
        self.tables = self.verify_crawler(&mut crawler).await?;

        warn!(?crawler.name, ?self.tables, "Crawler ready");
        Ok(())
    }

    pub async fn run(&mut self) -> Result<(), GlueMvpError> {
        warn!(?self, "Starting Rust Glue scenario");

        let job = self.prepare_job().await?;
        warn!(?job, "Job prepared");

        self.check_jobs().await?;
        self.job_run_id = self.run_job().await?;

        warn!(?self.job_run_id, "Job started");

        self.verify_job_run().await?;

        warn!(?self.job_run_id, "Job complete");

        Ok(())
    }

    pub async fn cleanup(&self) -> Result<(), GlueMvpError> {
        if self.should_cleanup {
            self.clean_job_run().await?;
            self.clean_scenario().await?;
            self.verify_clean_scenario().await?;
        }

        Ok(())
    }
}

#[derive(Debug, thiserror::Error)]
pub enum GlueMvpError {
    #[error("Glue SDK Error: {0}")]
    GlueSdk(aws_sdk_glue::Error),

    #[error("Glue Job Failure: {0}")]
    JobFail(String),

    #[error("S3 SDK Error: {0}")]
    S3Sdk(aws_sdk_s3::Error),

    #[error("Failed to clean up: {0}")]
    Cleanup(String),

    #[error("Unknown Glue MVP Error: {0}")]
    Unknown(String),
}

impl GlueMvpError {
    pub fn from_glue_sdk(err: impl Into<aws_sdk_glue::Error>) -> Self {
        GlueMvpError::GlueSdk(err.into())
    }

    pub fn from_s3_sdk(err: impl Into<aws_sdk_s3::Error>) -> Self {
        GlueMvpError::S3Sdk(err.into())
    }

    pub fn job_fail(err: impl Into<String>) -> Self {
        GlueMvpError::JobFail(err.into())
    }
}
