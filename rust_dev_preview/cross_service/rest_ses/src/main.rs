use std::net::TcpListener;

use rest_ses::run;

#[tokio::main]
async fn main() -> std::io::Result<()> {
    let listener = TcpListener::bind("127.0.0.1:8000").expect("Failed to bind a TcpListener!");
    println!("Listening on {addr}", addr = listener.local_addr()?);
    run(listener)?.await
}
