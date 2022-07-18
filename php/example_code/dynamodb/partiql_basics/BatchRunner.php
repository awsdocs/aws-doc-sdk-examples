<?php

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

use DynamoDb\PartiQL_Basics\GettingStartedWithPartiQLBatch;

include "vendor\autoload.php";

include "GettingStartedWithPartiQLBatch.php";

$runner = new GettingStartedWithPartiQLBatch();
$runner->run();
