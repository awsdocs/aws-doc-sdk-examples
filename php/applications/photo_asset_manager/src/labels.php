<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace PhotoAssetManager;

use Aws\DynamoDb\DynamoDbClient;

function labels()
{
    $clientConfig = [
        'region' => 'us-west-2',
        'version' => 'latest',
    ];
    $DDBClient = new DynamoDbClient($clientConfig);

    $parameters = [
        'TableName' => getenv("LABELS_TABLE_NAME"),
    ];

    return getAllLabels($DDBClient, $parameters);
}

/**
 * @param DynamoDbClient $DDBClient
 * @param array $parameters
 * @return array
 */
function getAllLabels(DynamoDbClient $DDBClient, array $parameters): array
{
    $lastScannedKey = "tempValue";
    $labels = ["labels" => null];
    while ($lastScannedKey) {
        $result = $DDBClient->scan($parameters);

        foreach ($result['Items'] as $item) {
            $labels['labels'][$item['Label']['S']] = [
                "count" => $item['Count']['N'],
            ];
        }

        $lastScannedKey = $result['LastEvaluatedKey'];
        $parameters['ExclusiveStartKey'] = $result['LastEvaluatedKey'];
    }
    return $labels;
}
