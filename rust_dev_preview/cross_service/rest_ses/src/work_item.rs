use actix_web::{
    web::{self, Json},
    HttpResponse, Resource, Responder,
};
use serde::Deserialize;
use uuid::Uuid;

#[derive(Debug, Default, Deserialize)]
pub struct WorkItem {
    #[serde(default = "uuid::Uuid::new_v4")]
    pub idwork: Uuid,
    #[serde(default)]
    pub username: String,
    // date - A date value that specifies the date the item was created.
    #[serde(default)]
    pub description: String,
    #[serde(default)]
    pub guide: String,
    #[serde(default)]
    pub status: String,
    #[serde(default)]
    pub archive: u8,
}

async fn create(item: Json<WorkItem>) -> impl Responder {
    if item.username.is_empty() {
        HttpResponse::BadRequest().body("Missing username")
    } else {
        HttpResponse::Ok().body(format!("adding item for {user}", user = item.username))
    }
}

pub fn work_item_resource() -> Resource {
    web::resource("/items").route(web::post().to(create))
}
