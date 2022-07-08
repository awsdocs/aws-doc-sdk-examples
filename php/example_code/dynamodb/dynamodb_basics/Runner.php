<?php

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

use DynamoDb\Basics\GettingStartedWithDynamoDB;

include "vendor\autoload.php";

include "GettingStartedWithDynamoDB.php";

$runner = new GettingStartedWithDynamoDB();
$runner->run();
