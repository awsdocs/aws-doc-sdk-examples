use std::net::TcpListener;

use actix_web::{
    dev::Server,
    web::{get, scope, Data},
    App, HttpServer,
};
use tracing_actix_web::TracingLogger;

use crate::{client::RdsClient, healthz::healthz, work_item};

pub fn run(listener: TcpListener, rds_client: RdsClient) -> Result<Server, std::io::Error> {
    let rds_client = Data::new(rds_client);
    let server = HttpServer::new(move || {
        App::new()
            .wrap(TracingLogger::default())
            .route("/healthz", get().to(healthz))
            .service(scope("/api").service(work_item::collection::scope()))
            .app_data(rds_client.clone())
    })
    .listen(listener)?
    .run();

    Ok(server)
}
