<?php

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace AwsUtilities;

interface RunnableExample
{
    public function runExample();
    public function helloService();
    public function cleanUp();
}
