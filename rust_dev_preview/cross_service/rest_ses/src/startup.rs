use std::net::TcpListener;

use actix_web::{
    dev::Server,
    web::{get, scope, Data},
    App, HttpServer,
};

use crate::{client::RdsClient, healthz::healthz, work_item::work_item_resource};

pub fn run(listener: TcpListener, rds_client: RdsClient) -> Result<Server, std::io::Error> {
    let rds_client = Data::new(rds_client);
    let server = HttpServer::new(move || {
        App::new()
            .route("/healthz", get().to(healthz))
            .service(scope("/api").service(work_item_resource()))
            .app_data(rds_client.clone())
    })
    .listen(listener)?
    .run();

    Ok(server)
}
