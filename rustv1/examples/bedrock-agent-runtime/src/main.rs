use aws_config::{BehaviorVersion, SdkConfig};
use aws_sdk_bedrockagentruntime::{self as bedrockagentruntime, types::ResponseStream};

const BEDROCK_AGENT_ID: &str = "AJBHXXILZN";
const BEDROCK_AGENT_ALIAS_ID: &str = "AVKP1ITZAA";

#[::tokio::main]
async fn main() -> Result<(), Box<bedrockagentruntime::Error>> {
    let result = invoke_bedrock_agent("a prompt".to_string(), "random session id".to_string()).await?;
    println!("{}", result);
    Ok(())
}

async fn invoke_bedrock_agent(prompt: String, session_id: String) -> Result<String, bedrockagentruntime::Error> {
    let aws_config: SdkConfig = aws_config::load_defaults(BehaviorVersion::latest()).await;
    let bedrock_client = bedrockagentruntime::Client::new(&aws_config);

    let command_builder = bedrock_client
        .invoke_agent()
        .agent_id(BEDROCK_AGENT_ID)
        .agent_alias_id(BEDROCK_AGENT_ALIAS_ID)
        .session_id(session_id)
        .input_text(prompt);

    let response = command_builder.send().await?;

    let mut response_stream = response.completion;
    let mut full_agent_text_response = String::new();

    println!("Processing Bedrock agent response stream...");
    while let Some(event_result) = response_stream.recv().await.unwrap() {
        match event_result {
            ResponseStream::Chunk(chunk) => {
                if let Some(bytes) = chunk.bytes {
                    match String::from_utf8(bytes.into_inner()) {
                        Ok(text_chunk) => {
                            full_agent_text_response.push_str(&text_chunk);
                        }
                        Err(e) => {
                            eprintln!("UTF-8 decoding error for chunk: {}", e);
                        }
                    }
                }
            }
            _ => {
                println!("Received an unhandled event type from Bedrock stream.");
            }
        }
    }

    Ok(full_agent_text_response)
}
