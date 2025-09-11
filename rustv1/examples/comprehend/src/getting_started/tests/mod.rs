// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

use aws_config::BehaviorVersion;
use aws_sdk_comprehend::Client;
use crate::getting_started::scenario::ComprehendScenario;

#[tokio::test]
async fn test_comprehend_scenario() {
    let config = aws_config::defaults(BehaviorVersion::latest())
        .load()
        .await;
    let client = Client::new(&config);
    let scenario = ComprehendScenario::new(client);

    // Run the scenario - this will make real API calls
    let result = scenario.run().await;
    
    // The scenario should complete successfully
    assert!(result.is_ok(), "Comprehend scenario should complete successfully: {:?}", result);
}

#[tokio::test]
async fn test_detect_language() {
    let config = aws_config::defaults(BehaviorVersion::latest())
        .load()
        .await;
    let client = Client::new(&config);
    
    let response = client
        .detect_dominant_language()
        .text("Hello, how are you today?")
        .send()
        .await;
    
    assert!(response.is_ok(), "Language detection should succeed");
    
    let languages = response.unwrap().languages.unwrap_or_default();
    assert!(!languages.is_empty(), "Should detect at least one language");
    
    // The first language should be English with high confidence
    let primary_language = &languages[0];
    assert_eq!(primary_language.language_code().unwrap_or(""), "en");
    assert!(primary_language.score().unwrap_or(0.0) > 0.9, "English detection confidence should be high");
}

#[tokio::test]
async fn test_detect_sentiment() {
    let config = aws_config::defaults(BehaviorVersion::latest())
        .load()
        .await;
    let client = Client::new(&config);
    
    let response = client
        .detect_sentiment()
        .text("I love using AWS services! They are fantastic.")
        .language_code("en".into())
        .send()
        .await;
    
    assert!(response.is_ok(), "Sentiment detection should succeed");
    
    let sentiment_response = response.unwrap();
    assert!(sentiment_response.sentiment.is_some(), "Should detect sentiment");
    assert!(sentiment_response.sentiment_score.is_some(), "Should have sentiment scores");
}

#[tokio::test]
async fn test_detect_entities() {
    let config = aws_config::defaults(BehaviorVersion::latest())
        .load()
        .await;
    let client = Client::new(&config);
    
    let response = client
        .detect_entities()
        .text("John works at Amazon in Seattle.")
        .language_code("en".into())
        .send()
        .await;
    
    assert!(response.is_ok(), "Entity detection should succeed");
    
    let entities = response.unwrap().entities.unwrap_or_default();
    assert!(!entities.is_empty(), "Should detect entities");
}