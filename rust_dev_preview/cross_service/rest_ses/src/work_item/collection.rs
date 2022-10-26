//! Provides scoped HTTP endpoints for a REST WorkItem collection.
//! This includes the common REST HTTP endpoints, as well as RPC-like endpoints.
//!
//! * `GET /items/` for list
//! * `GET /items/{itemid} to retrieve
//! * `POST /items/` for create
//! * `PUT /items/{itemid}` to update
//! * `DELETE /items/{itemid}` to delete
//! * `PUT /items/{itemid}:archive` to mark an item as archived

use actix_web::{
    get, post, put,
    web::{self, Data, Json, Path, Query},
    Scope,
};

use super::{WorkItem, WorkItemArchived, WorkItemError};
use crate::client::RdsClient;

/// Create the root collection scope.
pub fn scope() -> Scope {
    web::scope("/items")
        .service(create)
        .service(retrieve)
        .service(list)
        .service(update)
        .service(delete)
        .service(archive)
}

/// Create a single WorkItem, serialized as a JSON body.
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

/// Retrieve a single WorkItem, in a JSON body, specified by a URL parameter.
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

/// List all WorkItems, with an optional archived query parameter.
/// The archived parameter defaults to active items.
#[get("/")]
#[tracing::instrument(name = "Request list all WorkItem", skip(client))]
async fn list(
    archived: Option<Query<String>>,
    client: Data<RdsClient>,
) -> Result<Json<Vec<WorkItem>>, WorkItemError> {
    let archived = archived
        .map(|a| a.0)
        .unwrap_or_default()
        .as_str()
        .try_into()?;
    super::repository::list(archived, &client).await.map(Json)
}

/// Update a WorkItem, in a JSON body.
/// The JSON body ID must match the path ID.
/// If they do not match, returns a 404.
#[put("/{itemid}")]
#[tracing::instrument(name = "Request update WorkItem", skip(client))]
async fn update(
    itemid: Path<String>,
    item: Json<WorkItem>,
    client: Data<RdsClient>,
) -> Result<Json<()>, WorkItemError> {
    if item.idwork().to_string() != itemid.to_string() {
        return Err(WorkItemError::MissingItem(itemid.to_string()));
    }
    super::repository::update(&item.0, &client).await?;
    Ok(Json(()))
}

/// Deleate a WorkItem, by given id.
#[actix_web::delete("/{itemid}")]
#[tracing::instrument(name = "Request delete WorkItem", skip(client))]
async fn delete(itemid: Path<String>, client: Data<RdsClient>) -> Result<Json<()>, WorkItemError> {
    super::repository::delete(itemid.to_string(), &client).await?;
    Ok(Json(()))
}

/// RPC-like action to archive an item.
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
