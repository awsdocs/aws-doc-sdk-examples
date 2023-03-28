/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use async_trait::async_trait;
use aws_credential_types::{cache::CredentialsCache, provider::ProvideCredentials, Credentials};
use aws_sdk_lambda::{config::Region, meta::PKG_VERSION, Client};
use aws_smithy_async::rt::sleep::{AsyncSleep, Sleep};
use aws_smithy_client::erase::DynConnector;
use aws_smithy_http::{body::SdkBody, result::ConnectorError};
use serde::Deserialize;
use wasm_bindgen::{prelude::*, JsCast};

macro_rules! log {
    ( $( $t:tt )* ) => {
        web_sys::console::log_1(&format!( $( $t )* ).into());
    }
}

#[derive(Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct AwsCredentials {
    pub access_key_id: String,
    pub secret_access_key: String,
    pub session_token: Option<String>,
}

#[wasm_bindgen(module = "env")]
extern "C" {
    fn now() -> f64;

    #[wasm_bindgen(js_name = retrieveCredentials)]
    fn retrieve_credentials() -> JsValue;
}

#[wasm_bindgen(start)]
pub fn start() {
    std::panic::set_hook(Box::new(console_error_panic_hook::hook));
    log!("initializing module...");
}

#[wasm_bindgen]
pub async fn main(region: String, verbose: bool) -> Result<String, String> {
    log!("");

    if verbose {
        log!("Lambda client version:   {}", PKG_VERSION);
        log!("Region:                  {}", region);
        log!("");
    }

    let credentials_provider = static_credential_provider();
    let credentials = credentials_provider.provide_credentials().await.unwrap();
    let access_key = credentials.access_key_id();

    let shared_config = aws_config::from_env()
        .sleep_impl(BrowserSleep)
        .region(Region::new(region))
        .credentials_cache(browser_credentials_cache())
        .credentials_provider(credentials_provider)
        .http_connector(DynConnector::new(Adapter::new(
            verbose,
            access_key == "access_key",
        )))
        .load()
        .await;
    let client = Client::new(&shared_config);

    let now = std::time::Duration::new(now() as u64, 0);
    log!("current date in unix timestamp: {}", now.as_secs());

    let resp = client
        .list_functions()
        .send()
        .await
        .map_err(|e| format!("{:?}", e))?;
    let functions = resp.functions().unwrap_or_default();

    for function in functions {
        log!(
            "Function Name: {}",
            function.function_name().unwrap_or_default()
        );
    }
    let output = functions.len().to_string();

    Ok(output)
}

#[derive(Debug, Clone)]
struct BrowserSleep;
impl AsyncSleep for BrowserSleep {
    fn sleep(&self, duration: std::time::Duration) -> Sleep {
        Sleep::new(Box::pin(async move {
            wasm_timer::Delay::new(duration).await.unwrap();
        }))
    }
}

fn static_credential_provider() -> impl ProvideCredentials {
    let credentials = serde_wasm_bindgen::from_value::<AwsCredentials>(retrieve_credentials())
        .expect("invalid credentials");
    Credentials::from_keys(
        credentials.access_key_id,
        credentials.secret_access_key,
        credentials.session_token,
    )
}

fn browser_credentials_cache() -> CredentialsCache {
    CredentialsCache::lazy_builder()
        .sleep(std::sync::Arc::new(BrowserSleep))
        .into_credentials_cache()
}

/// At this moment, there is no standard mechanism to make an outbound
/// HTTP request from within the guest Wasm module.
/// Eventually that will be defined by the WebAssembly System Interface:
/// https://github.com/WebAssembly/wasi-http
#[async_trait(?Send)]
trait MakeRequestBrowser {
    async fn send(
        parts: http::request::Parts,
        body: SdkBody,
    ) -> Result<http::Response<SdkBody>, JsValue>;
}

pub struct BrowserHttpClient {}

#[async_trait(?Send)]
impl MakeRequestBrowser for BrowserHttpClient {
    /// The [Fetch API](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API)
    /// will be used to actually send the outbound HTTP request.
    /// Most of the logic here is around converting from
    /// the [http::Request]'s shape to [web_sys::Request].
    async fn send(
        parts: http::request::Parts,
        body: SdkBody,
    ) -> Result<http::Response<SdkBody>, JsValue> {
        use js_sys::{Array, ArrayBuffer, Reflect, Uint8Array};
        use wasm_bindgen_futures::JsFuture;

        let mut opts = web_sys::RequestInit::new();
        opts.method(parts.method.as_str());
        opts.mode(web_sys::RequestMode::Cors);

        let body_pinned = std::pin::Pin::new(body.bytes().unwrap());
        if body_pinned.len() > 0 {
            let uint_8_array = unsafe { Uint8Array::view(&body_pinned) };
            opts.body(Some(&uint_8_array));
        }

        let request = web_sys::Request::new_with_str_and_init(&parts.uri.to_string(), &opts)?;

        for (name, value) in parts
            .headers
            .iter()
            .map(|(n, v)| (n.as_str(), v.to_str().unwrap()))
        {
            request.headers().set(name, value)?;
        }

        let window = web_sys::window().ok_or("could not get window")?;
        let promise = window.fetch_with_request(&request);
        let res_web = JsFuture::from(promise).await?;
        let res_web: web_sys::Response = res_web.dyn_into().unwrap();

        let promise_array = res_web.array_buffer()?;
        let array = JsFuture::from(promise_array).await?;
        let buf: ArrayBuffer = array.dyn_into().unwrap();
        let slice = Uint8Array::new(&buf);
        let body = slice.to_vec();

        let mut builder = http::Response::builder().status(res_web.status());
        for i in js_sys::try_iter(&res_web.headers())?.unwrap() {
            let array: Array = i?.into();
            let values = array.values();

            let prop = String::from("value").into();
            let key = Reflect::get(values.next()?.as_ref(), &prop)?
                .as_string()
                .unwrap();
            let value = Reflect::get(values.next()?.as_ref(), &prop)?
                .as_string()
                .unwrap();
            builder = builder.header(&key, &value);
        }
        let res_body = SdkBody::from(body);
        let res = builder.body(res_body).unwrap();
        Ok(res)
    }
}

pub struct MockedHttpClient {}

#[async_trait(?Send)]
impl MakeRequestBrowser for MockedHttpClient {
    async fn send(
        _parts: http::request::Parts,
        _body: SdkBody,
    ) -> Result<http::Response<SdkBody>, JsValue> {
        let body = "{
            \"Functions\": [
                {
                    \"FunctionName\": \"function-name-1\"
                },
                {
                    \"FunctionName\": \"function-name-2\"
                }
            ],
            \"NextMarker\": null
        }";
        let builder = http::Response::builder().status(200);
        let res = builder.body(SdkBody::from(body)).unwrap();
        Ok(res)
    }
}

#[derive(Debug, Clone)]
struct Adapter {
    verbose: bool,
    use_mock: bool,
}

impl Adapter {
    fn new(verbose: bool, use_mock: bool) -> Self {
        Self { verbose, use_mock }
    }
}

impl tower::Service<http::Request<SdkBody>> for Adapter {
    type Response = http::Response<SdkBody>;

    type Error = ConnectorError;

    #[allow(clippy::type_complexity)]
    type Future = std::pin::Pin<
        Box<dyn std::future::Future<Output = Result<Self::Response, Self::Error>> + Send + 'static>,
    >;

    fn poll_ready(
        &mut self,
        _cx: &mut std::task::Context<'_>,
    ) -> std::task::Poll<Result<(), Self::Error>> {
        std::task::Poll::Ready(Ok(()))
    }

    fn call(&mut self, req: http::Request<SdkBody>) -> Self::Future {
        let (parts, body) = req.into_parts();
        let uri = parts.uri.to_string();
        if self.verbose {
            log!("sending request to {}", uri);
            log!("http::Request parts: {:?}", parts);
            log!("http::Request body: {:?}", body);
            log!("");
        }

        let (tx, rx) = tokio::sync::oneshot::channel();

        log!("begin request...");
        let use_mock = self.use_mock;
        wasm_bindgen_futures::spawn_local(async move {
            let fut = if use_mock {
                MockedHttpClient::send(parts, body)
            } else {
                BrowserHttpClient::send(parts, body)
            };
            let _ = tx.send(
                fut.await
                    .unwrap_or_else(|_| panic!("failure while making request to: {}", uri)),
            );
        });

        Box::pin(async move {
            let response = rx.await.map_err(|e| ConnectorError::user(Box::new(e)))?;
            log!("response received");
            Ok(response)
        })
    }
}
