//! The common entry point for starting the REST server, shared by tests and `main`.
use std::net::TcpListener;

use actix_web::{
    dev::Server,
    web::{scope, Data},
    App, HttpServer,
};
use tracing_actix_web::TracingLogger;

use crate::{
    client::{RdsClient, SesClient},
    healthz::healthz,
    report,
    telemetry::metrics_wrapper,
    work_item,
};

/// Given a TCP socket & AWS Clients, organize an actix server & start it listening!
pub fn run(
    listener: TcpListener,
    rds_client: RdsClient,
    ses_client: SesClient,
) -> Result<Server, std::io::Error> {
    let rds_client = Data::new(rds_client);
    let ses_client = Data::new(ses_client);
    let metrics = metrics_wrapper();
    let server = HttpServer::new(move || {
        App::new()
            .wrap(TracingLogger::default())
            .wrap(metrics.clone())
            .service(healthz)
            .service(
                scope("/api")
                    .service(work_item::collection::scope())
                    .service(report::send_report),
            )
            .app_data(rds_client.clone())
            .app_data(ses_client.clone())
    })
    .listen(listener)?
    .run();

    Ok(server)
}
