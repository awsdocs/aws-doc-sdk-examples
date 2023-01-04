// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0.

use aws_sdk_glue::model::{JobRun, JobRunState};
use futures::StreamExt;
use tracing::{info, instrument, warn};

use crate::{clients::GLUE_CLIENT, GlueMvpError, GlueScenario};

impl GlueScenario {
    // List jobs for the user's account.
    #[instrument(skip(self))]
    pub async fn check_jobs(&self) -> Result<(), GlueMvpError> {
        let glue = GLUE_CLIENT.get().await;

        // snippet-start:[rust.glue.list_jobs]
        let mut list_jobs = glue.list_jobs().into_paginator().send();
        while let Some(list_jobs_output) = list_jobs.next().await {
            match list_jobs_output {
                Ok(list_jobs) => {
                    if let Some(jobs) = list_jobs.job_names() {
                        info!(?jobs, "Found these jobs")
                    }
                }
                Err(err) => return Err(GlueMvpError::from_glue_sdk(err)),
            }
        }
        // snippet-end:[rust.glue.list_jobs]

        Ok(())
    }

    // Start a job run and pass it these arguments. Because these are expected by the ETL script, they must match exactly:
    // --input_database: [name of the database created by the crawler]
    // --input_table: [name of the table created by the crawler]
    // --output_bucket_url: [URL to the scaffold bucket you created for the user]
    #[instrument(skip(self))]
    pub async fn run_job(&self) -> Result<String, GlueMvpError> {
        let glue = GLUE_CLIENT.get().await;

        // snippet-start:[rust.glue.start_job_run]
        let job_run_output = glue
            .start_job_run()
            .job_name(self.job())
            .arguments("--input_database", self.database())
            .arguments(
                "--input_table",
                self.tables
                    .get(0)
                    .ok_or_else(|| GlueMvpError::Unknown("Missing crawler table".into()))?
                    .name()
                    .ok_or_else(|| GlueMvpError::Unknown("Crawler table without a name".into()))?,
            )
            .arguments("--output_bucket_url", self.bucket())
            .send()
            .await
            .map_err(GlueMvpError::from_glue_sdk)?;

        let job = job_run_output
            .job_run_id()
            .ok_or_else(|| GlueMvpError::Unknown("Missing run id from just started job".into()))?
            .to_string();
        // snippet-end:[rust.glue.start_job_run]

        Ok(job)
    }

    #[instrument(skip(self))]
    pub async fn wait_for_job_run(&self, job_run_id: &str) -> Result<(), GlueMvpError> {
        let glue = GLUE_CLIENT.get().await;
        let unknown_state = aws_sdk_glue::model::JobRunState::from("unknown");

        // snippet-start:[rust.glue.get_job_run]
        let get_job_run = || async {
            Ok::<JobRun, GlueMvpError>(
                glue.get_job_run()
                    .job_name(self.job())
                    .run_id(job_run_id.to_string())
                    .send()
                    .await
                    .map_err(GlueMvpError::from_glue_sdk)?
                    .job_run()
                    .ok_or_else(|| GlueMvpError::Unknown("Failed to get job_run".into()))?
                    .to_owned(),
            )
        };

        let mut job_run = get_job_run().await?;
        let mut state = job_run.job_run_state().unwrap_or(&unknown_state).to_owned();

        while matches!(
            state,
            JobRunState::Starting | JobRunState::Stopping | JobRunState::Running
        ) {
            info!(?state, "Waiting for job to finish");
            tokio::time::sleep(self.wait_delay).await;

            job_run = get_job_run().await?;
            state = job_run.job_run_state().unwrap_or(&unknown_state).to_owned();
        }
        // snippet-end:[rust.glue.get_job_run]

        Ok(())
    }

    // Loop and get the job run until it returns state 'SUCCEEDED', 'STOPPED', 'FAILED', or 'TIMEOUT'.
    // Output data is stored in a group of files in the user's bucket.
    // Either direct them to look at it or download a file for them and display some of the results.
    // Get job run detail for a job run.
    #[instrument(skip(self))]
    pub async fn verify_job_run(&self) -> Result<(), GlueMvpError> {
        let glue = GLUE_CLIENT.get().await;
        self.wait_for_job_run(self.job_run_id()).await?;

        // GetJobRun
        let get_job_run_output = glue
            .get_job_run()
            .job_name(self.job())
            .run_id(self.job_run_id())
            .send()
            .await
            .map_err(GlueMvpError::from_glue_sdk)?;

        if let Some(job_run) = get_job_run_output.job_run() {
            warn!(?job_run.job_run_state, ?job_run.error_message, "Job run finished");
            if job_run.job_run_state() == Some(JobRunState::Failed).as_ref() {
                return Err(GlueMvpError::job_fail(
                    job_run.error_message().unwrap_or_default(),
                ));
            }
        }

        Ok(())
    }
}
