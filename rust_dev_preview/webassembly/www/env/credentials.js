/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

const EXAMPLE_CREDENTIALS = {
    accessKeyId: "access_key",
    secretAccessKey: "secret_key",
    sessionToken: "session_token"
};

const parseCookie = str =>
  str
  .split(';')
  .map(v => v.split('='))
  .reduce((acc, v) => {
    if (v[0] && v[1]) {
        acc[decodeURIComponent(v[0].trim())] = decodeURIComponent(v[1].trim());
    }
    return acc;
  }, {});

export const retrieveCredentials = () => {
    let cookie = parseCookie(document.cookie ?? "");
    return cookie.credentials_aws ? JSON.parse(cookie.credentials_aws) : EXAMPLE_CREDENTIALS;
}

export const setCredentials = () => {
    const credentials = encodeURIComponent(JSON.stringify({
        ...EXAMPLE_CREDENTIALS,
    }));
    document.cookie = `credentials_aws=${credentials}; max-age=43200; path=/;`;
}
