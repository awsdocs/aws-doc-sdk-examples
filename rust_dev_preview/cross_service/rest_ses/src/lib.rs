pub mod healthz;
pub mod work_item;

use std::net::TcpListener;

use actix_web::{
    dev::Server,
    web::{get, scope},
    App, HttpServer,
};

use healthz::healthz;
use work_item::work_item_resource;

pub fn run(listener: TcpListener) -> Result<Server, std::io::Error> {
    let server = HttpServer::new(|| {
        App::new()
            .route("/healthz", get().to(healthz))
            .service(scope("/api").service(work_item_resource()))
    })
    .listen(listener)?
    .run();

    Ok(server)
}
