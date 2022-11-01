/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

//! Don't worry! Be healthz!
use actix_web::{get, HttpResponse, Responder};

#[get("/healthz")]
pub async fn healthz() -> impl Responder {
    HttpResponse::Ok().body("OK\n")
}
