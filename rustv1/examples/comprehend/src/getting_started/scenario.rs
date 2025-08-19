// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

use crate::comprehend::ComprehendManager;
use aws_sdk_comprehend::Client;

/// Demonstrates various Amazon Comprehend text analysis capabilities.
pub struct ComprehendScenario {
    manager: ComprehendManager,
}

impl ComprehendScenario {
    /// Create a new ComprehendScenario with the given client.
    pub fn new(client: Client) -> Self {
        Self {
            manager: ComprehendManager::new(client),
        }
    }

    /// Run the complete Comprehend basics scenario.
    /// 
    /// This demonstrates:
    /// - Language detection
    /// - Entity detection
    /// - Key phrase detection
    /// - Sentiment analysis
    /// - PII detection
    /// - Syntax analysis
    pub async fn run(&self) -> Result<(), Box<dyn std::error::Error>> {
        let sample_text = "Hello, my name is John Doe and I work at Amazon Web Services in Seattle. \
                          I'm really excited about machine learning and natural language processing! \
                          You can reach me at john.doe@example.com or call me at (555) 123-4567. \
                          Today is a wonderful day for learning about AWS services.";

        println!("=== Amazon Comprehend Basics Scenario ===");
        println!();
        println!("Sample text for analysis:");
        println!("\"{}\"", sample_text);
        println!();

        // Step 1: Detect dominant language
        println!("1. Detecting dominant language...");
        let languages = self.manager.detect_dominant_language(sample_text).await?;
        
        if languages.is_empty() {
            println!("   No languages detected.");
            return Ok(());
        }

        let primary_language = &languages[0];
        let language_code = primary_language.language_code().unwrap_or("en");
        
        println!("   Primary language: {} (confidence: {:.2}%)", 
                language_code, 
                primary_language.score().unwrap_or(0.0) * 100.0);
        
        if languages.len() > 1 {
            println!("   Other detected languages:");
            for lang in &languages[1..] {
                println!("     - {}: {:.2}%", 
                        lang.language_code().unwrap_or("unknown"),
                        lang.score().unwrap_or(0.0) * 100.0);
            }
        }
        println!();

        // Step 2: Detect entities
        println!("2. Detecting entities...");
        let entities = self.manager.detect_entities(sample_text, language_code).await?;
        
        if entities.is_empty() {
            println!("   No entities detected.");
        } else {
            println!("   Found {} entities:", entities.len());
            for entity in &entities {
                println!("     - {}: {} (confidence: {:.2}%)",
                        entity.r#type().unwrap().as_str(),
                        entity.text().unwrap_or("unknown"),
                        entity.score().unwrap_or(0.0) * 100.0);
            }
        }
        println!();

        // Step 3: Detect key phrases
        println!("3. Detecting key phrases...");
        let key_phrases = self.manager.detect_key_phrases(sample_text, language_code).await?;
        
        if key_phrases.is_empty() {
            println!("   No key phrases detected.");
        } else {
            println!("   Found {} key phrases:", key_phrases.len());
            for phrase in &key_phrases {
                println!("     - \"{}\" (confidence: {:.2}%)",
                        phrase.text().unwrap_or("unknown"),
                        phrase.score().unwrap_or(0.0) * 100.0);
            }
        }
        println!();

        // Step 4: Detect sentiment
        println!("4. Analyzing sentiment...");
        let (sentiment, sentiment_scores) = self.manager.detect_sentiment(sample_text, language_code).await?;
        
        println!("   Overall sentiment: {}", sentiment);
        if let Some(scores) = sentiment_scores {
            println!("   Sentiment scores:");
            println!("     - Positive: {:.2}%", scores.positive().unwrap_or(0.0) * 100.0);
            println!("     - Negative: {:.2}%", scores.negative().unwrap_or(0.0) * 100.0);
            println!("     - Neutral:  {:.2}%", scores.neutral().unwrap_or(0.0) * 100.0);
            println!("     - Mixed:    {:.2}%", scores.mixed().unwrap_or(0.0) * 100.0);
        } else {
            println!("   No sentiment scores available.");
        }
        println!();

        // Step 5: Detect PII entities
        println!("5. Detecting personally identifiable information (PII)...");
        let pii_entities = self.manager.detect_pii_entities(sample_text, language_code).await?;
        
        if pii_entities.is_empty() {
            println!("   No PII entities detected.");
        } else {
            println!("   Found {} PII entities:", pii_entities.len());
            for pii in &pii_entities {
                println!("     - {}: {} (confidence: {:.2}%)",
                        pii.r#type().unwrap().as_str(),
                        sample_text.chars()
                            .skip(pii.begin_offset().unwrap_or(0) as usize)
                            .take((pii.end_offset().unwrap_or(0) - pii.begin_offset().unwrap_or(0)) as usize)
                            .collect::<String>(),
                        pii.score().unwrap_or(0.0) * 100.0);
            }
        }
        println!();

        // Step 6: Detect syntax
        println!("6. Analyzing syntax (first 10 tokens)...");
        let syntax_tokens = self.manager.detect_syntax(sample_text, language_code).await?;
        
        if syntax_tokens.is_empty() {
            println!("   No syntax tokens detected.");
        } else {
            println!("   Found {} syntax tokens (showing first 10):", syntax_tokens.len());
            for (i, token) in syntax_tokens.iter().take(10).enumerate() {
                println!("     {}. \"{}\": {} (confidence: {:.2}%)",
                        i + 1,
                        token.text().unwrap_or("unknown"),
                        token.part_of_speech().unwrap().tag().unwrap().as_str(),
                        token.part_of_speech().unwrap().score().unwrap_or(0.0) * 100.0);
            }
            if syntax_tokens.len() > 10 {
                println!("     ... and {} more tokens", syntax_tokens.len() - 10);
            }
        }
        println!();

        println!("=== Comprehend Basics Scenario Complete ===");
        Ok(())
    }
}