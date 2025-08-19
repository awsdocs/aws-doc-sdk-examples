// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

use aws_sdk_comprehend::{Client, Error as ComprehendError};
use aws_sdk_comprehend::types::{DominantLanguage, Entity, KeyPhrase, PiiEntity, SentimentScore, SyntaxToken};

/// A wrapper around the Comprehend client to provide higher-level operations.
pub struct ComprehendManager {
    client: Client,
}

impl ComprehendManager {
    /// Create a new ComprehendManager with the given client.
    pub fn new(client: Client) -> Self {
        Self { client }
    }

    /// Detect the dominant language in the given text.
    /// 
    /// # Arguments
    /// * `text` - The text to analyze
    /// 
    /// # Returns
    /// A vector of DominantLanguage structs with language codes and confidence scores
    pub async fn detect_dominant_language(&self, text: &str) -> Result<Vec<DominantLanguage>, ComprehendError> {
        let response = self
            .client
            .detect_dominant_language()
            .text(text)
            .send()
            .await?;

        Ok(response.languages.unwrap_or_default())
    }

    /// Detect entities in the given text.
    /// 
    /// # Arguments
    /// * `text` - The text to analyze
    /// * `language_code` - The language code (e.g., "en" for English)
    /// 
    /// # Returns
    /// A vector of Entity structs with entity information and confidence scores
    pub async fn detect_entities(&self, text: &str, language_code: &str) -> Result<Vec<Entity>, ComprehendError> {
        let response = self
            .client
            .detect_entities()
            .text(text)
            .language_code(language_code.into())
            .send()
            .await?;

        Ok(response.entities.unwrap_or_default())
    }

    /// Detect key phrases in the given text.
    /// 
    /// # Arguments
    /// * `text` - The text to analyze
    /// * `language_code` - The language code (e.g., "en" for English)
    /// 
    /// # Returns
    /// A vector of KeyPhrase structs with phrases and confidence scores
    pub async fn detect_key_phrases(&self, text: &str, language_code: &str) -> Result<Vec<KeyPhrase>, ComprehendError> {
        let response = self
            .client
            .detect_key_phrases()
            .text(text)
            .language_code(language_code.into())
            .send()
            .await?;

        Ok(response.key_phrases.unwrap_or_default())
    }

    /// Detect sentiment in the given text.
    /// 
    /// # Arguments
    /// * `text` - The text to analyze
    /// * `language_code` - The language code (e.g., "en" for English)
    /// 
    /// # Returns
    /// A tuple containing the sentiment string and sentiment scores
    pub async fn detect_sentiment(&self, text: &str, language_code: &str) -> Result<(String, Option<SentimentScore>), ComprehendError> {
        let response = self
            .client
            .detect_sentiment()
            .text(text)
            .language_code(language_code.into())
            .send()
            .await?;

        let sentiment = response.sentiment.map(|s| s.as_str().to_string()).unwrap_or_else(|| "UNKNOWN".to_string());
        let sentiment_score = response.sentiment_score;

        Ok((sentiment, sentiment_score))
    }

    /// Detect personally identifiable information (PII) in the given text.
    /// 
    /// # Arguments
    /// * `text` - The text to analyze
    /// * `language_code` - The language code (e.g., "en" for English)
    /// 
    /// # Returns
    /// A vector of PiiEntity structs with PII information and confidence scores
    pub async fn detect_pii_entities(&self, text: &str, language_code: &str) -> Result<Vec<PiiEntity>, ComprehendError> {
        let response = self
            .client
            .detect_pii_entities()
            .text(text)
            .language_code(language_code.into())
            .send()
            .await?;

        Ok(response.entities.unwrap_or_default())
    }

    /// Detect syntax elements in the given text.
    /// 
    /// # Arguments
    /// * `text` - The text to analyze
    /// * `language_code` - The language code (e.g., "en" for English)
    /// 
    /// # Returns
    /// A vector of SyntaxToken structs with syntax information
    pub async fn detect_syntax(&self, text: &str, language_code: &str) -> Result<Vec<SyntaxToken>, ComprehendError> {
        let response = self
            .client
            .detect_syntax()
            .text(text)
            .language_code(language_code.into())
            .send()
            .await?;

        Ok(response.syntax_tokens.unwrap_or_default())
    }
}