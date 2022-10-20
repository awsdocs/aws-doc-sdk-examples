use actix_web::{
    get, post, put,
    web::{self, Data, Json, Path, Query},
    Scope,
};

use super::{WorkItem, WorkItemArchived, WorkItemError};
use crate::client::RdsClient;

pub fn scope() -> Scope {
    web::scope("/items")
        .service(create)
        .service(retrieve)
        .service(list)
        .service(archive)
        .service(delete)
}

#[post("")]
#[tracing::instrument(
        name = "Request Create new WorkItem",
        skip(item, client),
        fields(work_item.user = %item.name, work_item.guide = %item.guide,)
    )]
async fn create(
    item: Json<WorkItem>,
    client: Data<RdsClient>,
) -> Result<Json<WorkItem>, WorkItemError> {
    super::repository::create(item.0, &client).await.map(Json)
}

#[get("/{id}")]
#[tracing::instrument(name = "Request Retrieve single WorkItem", skip(client))]
async fn retrieve(
    itemid: Path<String>,
    client: Data<RdsClient>,
) -> Result<Json<WorkItem>, WorkItemError> {
    super::repository::retrieve(itemid.to_string(), &client)
        .await
        .map(Json)
}

#[get("/")]
#[tracing::instrument(name = "Request list all WorkItem", skip(client))]
async fn list(
    archived: Option<Query<String>>,
    client: Data<RdsClient>,
) -> Result<Json<Vec<WorkItem>>, WorkItemError> {
    let archived = archived.map(|a| a.as_str().into()).unwrap_or_default();
    super::repository::list(archived, &client).await.map(Json)
}

#[put("/{itemid}:archive")]
#[tracing::instrument(name = "Request archive WorkItem", skip(client))]
async fn archive(
    itemid: Path<String>,
    client: Data<RdsClient>,
) -> Result<Json<WorkItem>, WorkItemError> {
    let mut item = super::repository::retrieve(itemid.to_string(), &client).await?;

    item.archive = WorkItemArchived::Archived;

    let item = super::repository::update(&item, &client).await?;

    Ok(Json(item))
}

#[actix_web::delete("/{itemid}")]
#[tracing::instrument(name = "Request delete WorkItem", skip(client))]
async fn delete(itemid: Path<String>, client: Data<RdsClient>) -> Result<Json<()>, WorkItemError> {
    super::repository::delete(itemid.to_string(), &client).await?;
    Ok(Json(()))
}
