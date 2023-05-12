<?php

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

use Lambda\GettingStartedWithLambda;

include __DIR__ . "/vendor/autoload.php";

include "GettingStartedWithLambda.php";

$runner = new GettingStartedWithLambda();
$runner->run();
