use tracing::{subscriber::set_global_default, Subscriber};
use tracing_bunyan_formatter::{BunyanFormattingLayer, JsonStorageLayer};
use tracing_log::LogTracer;
use tracing_subscriber::{fmt::MakeWriter, layer::SubscriberExt, EnvFilter, Registry};

pub fn get_subscriber<Sink>(
    name: String,
    env_filter: String,
    sink: Sink,
) -> impl Subscriber + Send + Sync
where
    Sink: for<'a> MakeWriter<'a> + Send + Sync + 'static,
{
    let env_filter =
        EnvFilter::try_from_default_env().unwrap_or_else(|_| EnvFilter::new(env_filter));
    let formatting_layer = BunyanFormattingLayer::new(name, sink);
    Registry::default()
        .with(env_filter)
        .with(JsonStorageLayer)
        .with(formatting_layer)
}

pub fn init_subscriber(subscriber: impl Subscriber + Send + Sync) {
    LogTracer::init().expect("Failed to init logger");
    set_global_default(subscriber).expect("Failed to set global subscriber");
}

use std::{
    io::{Result, Write},
    str::from_utf8,
};

use aws_sdk_cloudwatchlogs::model::InputLogEvent;

pub struct CloudWatchLogsWriter<'a> {
    client: &'a aws_sdk_cloudwatchlogs::Client,
    log_events: Vec<InputLogEvent>,
}

impl<'a> CloudWatchLogsWriter<'a> {
    pub fn new(client: &'a aws_sdk_cloudwatchlogs::Client) -> Self {
        CloudWatchLogsWriter {
            client,
            log_events: vec![],
        }
    }
}

impl<'a> Write for CloudWatchLogsWriter<'a> {
    fn write(&mut self, buf: &[u8]) -> Result<usize> {
        let len = buf.len();
        self.log_events.append(
            &mut buf
                .split(|c| *c == b'\n')
                .filter_map(|chars| from_utf8(chars).ok())
                .map(|s| {
                    InputLogEvent::builder()
                        .set_message(Some(s.to_string()))
                        .build()
                })
                .collect(),
        );
        Ok(len)
    }

    fn flush(&mut self) -> Result<()> {
        if !self.log_events.is_empty() {
            tokio::spawn(
                self.client
                    .put_log_events()
                    .set_log_events(Some(self.log_events.clone()))
                    .send(),
            );
        }
        self.log_events.clear();
        Ok(())
    }
}

pub struct CloudWatchLogsWriterFactory<'a> {
    client: &'a aws_sdk_cloudwatchlogs::Client,
}

impl<'a> CloudWatchLogsWriterFactory<'a> {
    pub fn new(client: &'a aws_sdk_cloudwatchlogs::Client) -> Self {
        CloudWatchLogsWriterFactory { client }
    }
}

impl<'a> MakeWriter<'a> for CloudWatchLogsWriterFactory<'a> {
    type Writer = CloudWatchLogsWriter<'a>;

    fn make_writer(&'a self) -> Self::Writer {
        CloudWatchLogsWriter::new(self.client)
    }
}
