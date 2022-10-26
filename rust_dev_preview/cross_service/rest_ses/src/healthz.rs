use actix_web::{HttpResponse, Responder};

/// Don't worry... be healthz...
pub async fn healthz() -> impl Responder {
    HttpResponse::Ok().body("OK\n")
}
