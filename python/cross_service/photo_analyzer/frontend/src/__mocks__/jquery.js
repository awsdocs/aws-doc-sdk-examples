// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Set up jQuery get and post for mocking.

const jQ = jest.requireActual("jquery");

const get = jest.fn(() => {
    return jQ.Deferred();
});

const post = jest.fn(() => {
    return jQ.Deferred();
});

export const $ = {
    ...jQ,
    get,
    post,
};

export default $;