use chrono::{DateTime, Utc};
use serde::{Deserialize, Serialize};
use uuid::Uuid;

#[derive(Clone, Debug, Default, Deserialize, Serialize)]
pub struct WorkItem {
    #[serde(default = "uuid::Uuid::new_v4")]
    pub idwork: Uuid,
    pub username: String,
    #[serde(default = "chrono::Utc::now")]
    date: DateTime<Utc>,
    #[serde(default)]
    pub description: String,
    #[serde(default)]
    pub guide: String,
    #[serde(default)]
    pub status: String,
    #[serde(default)]
    pub archive: u8,
}

const RDS_DATE_FORMAT: &'static str = "%Y-%m-%d";
pub mod collection {
    use actix_web::{
        post,
        web::{self, Data, Json},
        Either, HttpResponse, Scope,
    };
    use serde_json::json;

    use super::WorkItem;
    use crate::client::RdsClient;

    pub fn scope() -> Scope {
        web::scope("/items").service(create)
    }

    #[post("")]
    #[tracing::instrument(
        name = "Request Create new WorkItem",
        skip(item, client),
        fields(work_item.user = %item.username, work_item.guide = %item.guide,)
    )]
    pub async fn create(
        item: Json<WorkItem>,
        client: Data<RdsClient>,
    ) -> Either<Json<WorkItem>, HttpResponse> {
        let item = item.0;

        match super::repository::create(item, &client).await {
            Ok(item) => Either::Left(Json(item)),
            Err(e) => Either::Right(HttpResponse::InternalServerError().json(
                json!({"error": "Failed to insert into database", "cause": format!("{e:?}")}),
            )),
        }
    }
}

mod repository {
    use super::{WorkItem, RDS_DATE_FORMAT};
    use crate::{client::RdsClient, params};

    #[tracing::instrument(name = "Repository Create new WorkItem", skip(item, client))]
    pub async fn create(
        item: WorkItem,
        client: &RdsClient,
    ) -> Result<WorkItem, aws_sdk_rdsdata::Error> {
        client
            .execute_statement(
                "INSERT INTO Work (idwork, username, date, description, guide, status) VALUES
                (:idwork, :username, :date, :description, :guide, :status);",
            )
            .set_parameters(params!(
                ("idwork", item.idwork.to_string()),
                ("username", item.username),
                ("date", format!("{}", item.date.format(RDS_DATE_FORMAT))),
                ("description", item.description),
                ("guide", item.guide),
                ("status", item.status)
            ))
            .send()
            .await
            .map_err(|err| {
                tracing::error!("Failed to execute create user query: {err:?}");
                err
            })?;

        Ok(item)
    }
}
