use aws_sdk_dynamodb::{model::AttributeValue, Client, Error};
use axum::{routing::get, Router};

use super::Movie;

pub fn make_app(client: &Client, table_name: &str) -> Router {
    Router::new().route("/", get(|| async { "Hello, world!" }))
}

// snippet-start:[dynamodb.rust.movies-movies_in_year]
pub async fn movies_in_year(
    client: &Client,
    table_name: &str,
    year: u16,
) -> Result<Vec<Movie>, Error> {
    let results = client
        .query()
        .table_name(table_name)
        .key_condition_expression("#yr = :yyyy")
        .expression_attribute_names("#yr", "year")
        .expression_attribute_values(":yyyy", AttributeValue::N(year.to_string()))
        .send()
        .await?;

    let movies = results
        .items()
        .unwrap_or_default()
        .iter()
        .map(|v| {
            Movie::new(
                v.get("year").unwrap().as_n().unwrap().parse().unwrap(),
                v.get("title").unwrap().as_s().unwrap().parse().unwrap(),
            )
        })
        .collect();

    Ok(movies)
}
// snippet-end:[dynamodb.rust.movies-movies_in_year]
