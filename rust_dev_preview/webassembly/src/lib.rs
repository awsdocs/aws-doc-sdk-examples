/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use async_trait::async_trait;
use aws_credential_types::{provider::ProvideCredentials, Credentials};
use aws_sdk_lambda::config::{AsyncSleep, Region, Sleep};
use aws_sdk_lambda::primitives::SdkBody;
use aws_sdk_lambda::{meta::PKG_VERSION, Client};
use aws_smithy_async::time::TimeSource;
use aws_smithy_runtime_api::client::http::request::Request;
use aws_smithy_runtime_api::client::result::ConnectorError;
use aws_smithy_runtime_api::{
    client::{
        http::{
            HttpClient, HttpConnector, HttpConnectorFuture, HttpConnectorSettings,
            SharedHttpConnector,
        },
        orchestrator::HttpRequest,
        runtime_components::RuntimeComponents,
    },
    shared::IntoShared,
};

use serde::Deserialize;
use std::time::SystemTime;
use wasm_bindgen::{prelude::*, JsCast};
use wasm_timer::UNIX_EPOCH;

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
    console_error_panic_hook::set_once();
    tracing_wasm::set_as_global_default();
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
        .time_source(BrowserNow)
        .credentials_provider(credentials_provider)
        .http_client(Adapter::new(verbose, access_key == "access_key"))
        .load()
        .await;
    tracing::info!("sdk config: {:#?}", shared_config);
    let client = Client::new(&shared_config);

    let resp = client
        .list_functions()
        .send()
        .await
        .map_err(|e| format!("{e:?}"))?;

    let functions = resp.functions();
    for function in functions {
        log!(
            "Function Name: {}",
            function.function_name().unwrap_or_default()
        );
    }

    let output = functions.len().to_string();

    Ok(output)
}

#[derive(Debug)]
struct BrowserNow;
impl TimeSource for BrowserNow {
    fn now(&self) -> SystemTime {
        let offset = wasm_timer::SystemTime::now()
            .duration_since(UNIX_EPOCH)
            .unwrap();
        std::time::UNIX_EPOCH + offset
    }
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

/// At this moment, there is no standard mechanism to make an outbound
/// HTTP request from within the guest Wasm module.
/// Eventually that will be defined by the WebAssembly System Interface:
/// https://github.com/WebAssembly/wasi-http
#[async_trait(?Send)]
trait MakeRequestBrowser {
    async fn send(req: Request) -> Result<http::Response<SdkBody>, JsValue>;
}

pub struct BrowserHttpClient {}

#[async_trait(?Send)]
impl MakeRequestBrowser for BrowserHttpClient {
    /// The [Fetch API](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API)
    /// will be used to actually send the outbound HTTP request.
    /// Most of the logic here is around converting from
    /// the [http::Request]'s shape to [web_sys::Request].
    async fn send(req: Request) -> Result<http::Response<SdkBody>, JsValue> {
        use js_sys::{Array, ArrayBuffer, Reflect, Uint8Array};
        use wasm_bindgen_futures::JsFuture;

        let mut opts = web_sys::RequestInit::new();
        opts.method(req.method());
        opts.mode(web_sys::RequestMode::Cors);

        let body_pinned = std::pin::Pin::new(req.body().bytes().unwrap());
        if body_pinned.len() > 0 {
            let uint_8_array = unsafe { Uint8Array::view(&body_pinned) };
            opts.body(Some(&uint_8_array));
        }

        let request = web_sys::Request::new_with_str_and_init(req.uri(), &opts)?;

        for (name, value) in req.headers() {
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
    async fn send(_req: Request) -> Result<http::Response<SdkBody>, JsValue> {
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

impl HttpConnector for Adapter {
    fn call(&self, req: HttpRequest) -> HttpConnectorFuture {
        {
            if self.verbose {
                log!("sending request to {}", req.uri());
                log!("http::Request method: {:?}", req.method());
                log!("http::Request headers: {:?}", req.headers());
                log!("http::Request body: {:?}", req.body());
                log!("");
            }
        }

        let (tx, rx) = tokio::sync::oneshot::channel();

        log!("begin request...");
        let uri = req.uri().to_string();
        let use_mock = self.use_mock;
        wasm_bindgen_futures::spawn_local(async move {
            let fut = if use_mock {
                MockedHttpClient::send(req)
            } else {
                BrowserHttpClient::send(req)
            };
            tx.send(
                fut.await
                    .unwrap_or_else(|_| panic!("sending request to {uri}")),
            )
            .expect("sent request to channel");
        });

        HttpConnectorFuture::new(async move {
            let response = rx.await.map_err(|e| ConnectorError::user(Box::new(e)))?;
            log!("response received");
            Ok(response)
        })
    }
}

impl HttpClient for Adapter {
    fn http_connector(
        &self,
        _settings: &HttpConnectorSettings,
        _components: &RuntimeComponents,
    ) -> SharedHttpConnector {
        self.clone().into_shared()
    }
}
