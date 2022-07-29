<?php

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

use DynamoDb\PartiQL_Basics\GettingStartedWithPartiQL;

include "vendor\autoload.php";

include "GettingStartedWithPartiQL.php";

$runner = new GettingStartedWithPartiQL();
$runner->run();
