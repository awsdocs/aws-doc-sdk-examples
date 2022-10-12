use std::net::TcpListener;

use actix_web::{dev::Server, web::get, App, HttpResponse, HttpServer, Responder};

async fn healthz() -> impl Responder {
    HttpResponse::Ok().body("OK\n")
}

pub fn run(listener: TcpListener) -> Result<Server, std::io::Error> {
    let server = HttpServer::new(|| App::new().route("/healthz", get().to(healthz)))
        .listen(listener)?
        .run();

    Ok(server)
}
