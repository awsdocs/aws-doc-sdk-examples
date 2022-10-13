use std::net::TcpListener;

use serde::Deserialize;

use actix_web::{
    dev::Server,
    web::{get, post, Json},
    App, HttpResponse, HttpServer, Responder,
};
use uuid::Uuid;

#[derive(Debug, Default, Deserialize)]
struct WorkItem {
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

async fn healthz() -> impl Responder {
    HttpResponse::Ok().body("OK\n")
}

async fn create(item: Json<WorkItem>) -> impl Responder {
    if item.username.is_empty() {
        HttpResponse::BadRequest().body("Missing username")
    } else {
        HttpResponse::Ok().body(format!("adding item for {user}", user = item.username))
    }
}

pub fn run(listener: TcpListener) -> Result<Server, std::io::Error> {
    let server = HttpServer::new(|| {
        App::new()
            .route("/healthz", get().to(healthz))
            .route("/api/items", post().to(create))
    })
    .listen(listener)?
    .run();

    Ok(server)
}
