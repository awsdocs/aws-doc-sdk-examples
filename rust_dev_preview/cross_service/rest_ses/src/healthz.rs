use actix_web::{HttpResponse, Responder};

pub async fn healthz() -> impl Responder {
    HttpResponse::Ok().body("OK\n")
}
