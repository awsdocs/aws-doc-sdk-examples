// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0.

use aws_sdk_glue::model::CrawlerState;
use tracing::{instrument, warn};

use crate::{clients::GLUE_CLIENT, GlueMvpError, GlueScenario};

impl GlueScenario {
    /// Delete the demo job.
    #[instrument(skip(self))]
    pub async fn clean_job_run(&self) -> Result<(), GlueMvpError> {
        let glue = GLUE_CLIENT.get().await;

        // snippet-start:[rust.glue.delete_job]
        glue.delete_job()
            .job_name(self.job())
            .send()
            .await
            .map_err(GlueMvpError::from_glue_sdk)?;
        // snippet-end:[rust.glue.delete_job]

        // snippet-start:[rust.glue.delete_table]
        for t in &self.tables {
            glue.delete_table()
                .name(
                    t.name()
                        .ok_or_else(|| GlueMvpError::Unknown("Couldn't find table".to_string()))?,
                )
                .database_name(self.database())
                .send()
                .await
                .map_err(GlueMvpError::from_glue_sdk)?;
        }
        // snippet-end:[rust.glue.delete_table]

        Ok(())
    }

    /// Delete the database and tables. Delete the crawler.
    #[instrument(skip(self))]
    pub async fn clean_scenario(&self) -> Result<(), GlueMvpError> {
        let glue = GLUE_CLIENT.get().await;

        // Wait for crawler to finish.
        let unknown_state = aws_sdk_glue::model::CrawlerState::from("unknown");
        let crawler = glue
            .get_crawler()
            .name(self.crawler())
            .send()
            .await
            .map_err(GlueMvpError::from_glue_sdk)?;
        let crawler = crawler
            .crawler()
            .ok_or_else(|| GlueMvpError::Unknown("Failed to get crawler".into()))?;
        let mut state = crawler.state().unwrap_or(&unknown_state).to_owned();

        // GetCrawler
        while matches!(state, CrawlerState::Running | CrawlerState::Stopping) {
            warn!(?state, "Waiting for crawler to stop");
            tokio::time::sleep(self.wait_delay).await;
            let crawler = glue
                .get_crawler()
                .name(self.crawler())
                .send()
                .await
                .map_err(GlueMvpError::from_glue_sdk)?;
            let crawler = crawler
                .crawler()
                .ok_or_else(|| GlueMvpError::Unknown("Failed to get crawler".into()))?;

            state = crawler.state().unwrap_or(&unknown_state).to_owned();
        }

        // DeleteDatabase
        // snippet-start:[rust.glue.delete_database]
        glue.delete_database()
            .name(self.database())
            .send()
            .await
            .map_err(GlueMvpError::from_glue_sdk)?;
        // snippet-end:[rust.glue.delete_database]

        // DeleteCrawler
        // snippet-start:[rust.glue.delete_crawler]
        glue.delete_crawler()
            .name(self.crawler())
            .send()
            .await
            .map_err(GlueMvpError::from_glue_sdk)?;
        // snippet-end:[rust.glue.delete_crawler]

        Ok(())
    }

    #[instrument(skip(self))]
    pub async fn verify_clean_scenario(&self) -> Result<(), GlueMvpError> {
        let glue = GLUE_CLIENT.get().await;

        let database = glue
            .get_database()
            .name(self.database())
            .send()
            .await
            .map_err(GlueMvpError::from_glue_sdk)?
            .to_owned();

        if let Some(database) = database.database() {
            return Err(GlueMvpError::Cleanup(
                database.name().unwrap_or_default().into(),
            ));
        }

        let crawler = glue
            .get_crawler()
            .name(self.crawler())
            .send()
            .await
            .map_err(GlueMvpError::from_glue_sdk)?;

        if let Some(crawler) = crawler.crawler() {
            return Err(GlueMvpError::Cleanup(
                crawler.name().unwrap_or_default().into(),
            ));
        };

        Ok(())
    }
}
