use aws_sdk_dynamodb::{model::AttributeValue, Client};
use axum::{extract::Path, routing::get, Extension, Router};
use tower_http::cors::{Any, CorsLayer};

use super::{Movie, MovieError, TABLE_NAME};

pub fn make_app() -> Router {
    let cors = CorsLayer::new().allow_origin(Any);
    Router::new()
        .route("/", get(|| async { "Hello, world!" }))
        .route(
            "/:year",
            get(
                |Path(year): Path<u16>, Extension(client): Extension<Client>| async move {
                    match movies_in_year(&client, TABLE_NAME, year).await {
                        Ok(movies) => axum::Json(movies),
                        Err(err) => {
                            tracing::warn!("{err:?}");
                            axum::Json(Vec::new())
                        }
                    }
                },
            ),
        )
        .layer(cors)
}

// snippet-start:[dynamodb.rust.movies-movies_in_year]
pub async fn movies_in_year(
    client: &Client,
    table_name: &str,
    year: u16,
) -> Result<Vec<Movie>, MovieError> {
    let results = client
        .query()
        .table_name(table_name)
        .key_condition_expression("#yr = :yyyy")
        .expression_attribute_names("#yr", "year")
        .expression_attribute_values(":yyyy", AttributeValue::N(year.to_string()))
        .send()
        .await?;

    if let Some(items) = results.items {
        let movies = items.iter().map(|v| v.into()).collect();
        Ok(movies)
    } else {
        Ok(vec![])
    }
}
// snippet-end:[dynamodb.rust.movies-movies_in_year]
