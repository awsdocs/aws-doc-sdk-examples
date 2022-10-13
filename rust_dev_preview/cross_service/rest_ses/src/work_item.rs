use actix_web::{
    web::{self, Data, Json},
    HttpResponse, HttpResponseBuilder, Resource,
};
use chrono::{DateTime, Utc};
use serde::{Deserialize, Serialize};
use uuid::Uuid;

use crate::client::{param, RdsClient};

use self::rds_date_format::FORMAT;

#[derive(Clone, Debug, Default, Deserialize, Serialize)]
pub struct WorkItem {
    #[serde(default = "uuid::Uuid::new_v4")]
    pub idwork: Uuid,
    pub username: String,
    #[serde(with = "rds_date_format", default = "chrono::Utc::now")]
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

async fn create(item: Json<WorkItem>, client: Data<RdsClient>) -> HttpResponse {
    let item = item.0;

    {
        let item = item.clone();
        if let Err(e) = client
            .execute_statement(
                "INSERT INTO Work (idwork, username, date, description, guide, status) VALUES
                (:idwork, :username, :date, :description, :guide, :status);",
            )
            .parameters(param("idwork", item.idwork.to_string()))
            .parameters(param("username", item.username))
            .parameters(param("date", format!("{}", item.date.format(FORMAT))))
            .parameters(param("description", item.description))
            .parameters(param("guide", item.guide))
            .parameters(param("status", item.status))
            .send()
            .await
        {
            return HttpResponse::InternalServerError()
                .insert_header(("content-type", "application/json"))
                .body(format!(
                    "{{\"error\":\"Failed to insert into database\",\"cause\":\"{err:?}\"}}",
                    err = format!("{e:?}").replace("\"", "\\\"")
                ));
        }
    }

    // TODO Learn enough about actix that a custom error type can handle the ?
    // HttpResponse::Ok()
    //     .insert_header(("content-type", "application/json"))
    //     .body(serde_json::to_string_pretty(&item)?)

    let (mut response, body): (HttpResponseBuilder, String) =
        match serde_json::to_string_pretty(&item) {
            Ok(work_item) => (HttpResponse::Ok(), work_item),
            Err(_) => (
                HttpResponse::InternalServerError(),
                r#"{"error":"Could not serialize WorkItem"}"#.to_string(),
            ),
        };

    response
        .insert_header(("content-type", "application/json"))
        .body(body)
}

pub fn work_item_resource() -> Resource {
    web::resource("/items").route(web::post().to(create))
}

// from https://serde.rs/custom-date-format.html
mod rds_date_format {
    use chrono::{DateTime, TimeZone, Utc};
    use serde::{self, Deserialize, Deserializer, Serializer};

    pub const FORMAT: &'static str = "%Y-%m-%d";

    pub fn serialize<S>(date: &DateTime<Utc>, serializer: S) -> Result<S::Ok, S::Error>
    where
        S: Serializer,
    {
        let s = format!("{}", date.format(FORMAT));
        serializer.serialize_str(&s)
    }

    pub fn deserialize<'de, D>(deserializer: D) -> Result<DateTime<Utc>, D::Error>
    where
        D: Deserializer<'de>,
    {
        let s = String::deserialize(deserializer)?;
        Utc.datetime_from_str(&s, FORMAT)
            .map_err(serde::de::Error::custom)
    }
}
