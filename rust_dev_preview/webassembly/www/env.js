/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

export const now = () => {
    return Date.now();
};

export const retrieveCredentials = () => {
    return {
        accessKeyId: "access_key",
        secretAccessKey: "secret_key",
        sessionToken: "session_token"
    }
}
