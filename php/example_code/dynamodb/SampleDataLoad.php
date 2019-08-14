<?php
// snippet-sourcedescription:[SampleDataLoad.php demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[PHP]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.php.codeexample.SampleDataLoad] 

/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
*/

require 'vendor/autoload.php';

date_default_timezone_set('UTC');

use Aws\DynamoDb\Exception\DynamoDbException;

$sdk = new Aws\Sdk([
    'region'   => 'us-west-2',
    'version'  => 'latest'
]);

$dynamodb = $sdk->createDynamoDb();

# Setup some local variables for dates

$oneDayAgo = date('Y-m-d H:i:s', strtotime('-1 days'));
$sevenDaysAgo = date('Y-m-d H:i:s', strtotime('-7 days'));
$fourteenDaysAgo = date('Y-m-d H:i:s', strtotime('-14 days'));
$twentyOneDaysAgo = date('Y-m-d H:i:s', strtotime('-21 days'));

$tableName = 'ProductCatalog';
echo "Adding data to the $tableName table...\n";

try {
    $response = $dynamodb->batchWriteItem([
        'RequestItems' => [
            $tableName => [
                [
                    'PutRequest' => [
                        'Item' => [
                            'Id'              => ['N' => '1101'],
                            'Title'           => ['S' => 'Book 101 Title'],
                            'ISBN'            => ['S' => '111-1111111111'],
                            'Authors'         => ['SS' => ['Author1']],
                            'Price'           => ['N' => '2'],
                            'Dimensions'      => ['S' => '8.5 x 11.0 x 0.5'],        
                            'PageCount'       => ['N' => '500'],        
                            'InPublication'   => ['N' => '1'],        
                            'ProductCategory' => ['S' => 'Book']
                        ]
                    ],
                ],
                [
                    'PutRequest' => [
                        'Item' => [
                            'Id'              => ['N' => '102'],
                            'Title'           => ['S' => 'Book 102 Title'],
                            'ISBN'            => ['S' => '222-2222222222'], 
                            'Authors'         => ['SS' => ['Author1', 'Author2']],
                            'Price'           => ['N' => '20'], 
                            'Dimensions'      => ['S' => '8.5 x 11.0 x 0.8'], 
                            'PageCount'       => ['N' => '600'], 
                            'InPublication'   => ['N' => '1'], 
                            'ProductCategory' => ['S' => 'Book']                    
                        ]
                    ],
                ],
                [
                    'PutRequest' => [
                        'Item' => [
                            'Id'              => ['N' => '103'], 
                            'Title'           => ['S' => 'Book 103 Title'], 
                            'ISBN'            => ['S' => '333-3333333333'], 
                            'Authors'         => ['SS' => ['Author1', 'Author2']],
                            'Price'           => ['N' => '2000'], 
                            'Dimensions'      => ['S' => '8.5 x 11.0 x 1.5'], 
                            'PageCount'       => ['N' => '600'], 
                            'InPublication'   => ['N' => '0'], 
                            'ProductCategory' => ['S' => 'Book']                  
                        ]
                    ],
                ],
                [
                    'PutRequest' => [
                        'Item' => [
                            'Id'              => ['N' => '201'], 
                            'Title'           => ['S' => '18-Bike-201'], 
                            'Description'     => ['S' => '201 Description'], 
                            'BicycleType'     => ['S' => 'Road'], 
                            'Brand'           => ['S' => 'Mountain A'], 
                            'Price'           => ['N' => '100'], 
                            'Color'           => ['SS' => ['Red', 'Black']], 
                            'ProductCategory' => ['S' => 'Bicycle']            
                        ]
                    ],
                ],
                [
                    'PutRequest' => [
                        'Item' => [
                            'Id'              => ['N' => '202'], 
                            'Title'           => ['S' => '21-Bike-202'], 
                            'Description'     => ['S' => '202 Description'], 
                            'BicycleType'     => ['S' => 'Road'], 
                            'Brand'           => ['S' => 'Brand-Company A'], 
                            'Price'           => ['N' => '200'], 
                            'Color'           => ['SS' => ['Green', 'Black']],
                            'ProductCategory' => ['S' => 'Bicycle']
                        ]
                    ],
                ],
                [
                    'PutRequest' => [
                        'Item' => [
                            'Id'              => ['N' => '203'],  
                            'Title'           => ['S' => '19-Bike-203'], 
                            'Description'     => ['S' => '203 Description'], 
                            'BicycleType'     => ['S' => 'Road'], 
                            'Brand'           => ['S' => 'Brand-Company B'], 
                            'Price'           => ['N' => '300'], 
                            'Color'           => ['SS' => ['Red', 'Green', 'Black']], 
                            'ProductCategory' => ['S' => 'Bicycle']                    
                        ]
                    ],
                ],
                [
                    'PutRequest' => [
                        'Item' => [
                            'Id'              => ['N' => '204'],  
                            'Title'           => ['S' => '18-Bike-204'], 
                            'Description'     => ['S' => '204 Description'], 
                            'BicycleType'     => ['S' => 'Mountain'], 
                            'Brand'           => ['S' => 'Brand-Company B'], 
                            'Price'           => ['N' => '400'], 
                            'Color'           => ['SS' => ['Red']], 
                            'ProductCategory' => ['S' => 'Bicycle']
                        ]
                    ],
                ],
                [
                    'PutRequest' => [
                        'Item' => [
                            'Id'              => ['N' => '205'], 
                            'Title'           => ['S' => '20-Bike-205'],
                            'Description'     => ['S' => '205 Description'],
                            'BicycleType'     => ['S' => 'Hybrid'],
                            'Brand'           => ['S' => 'Brand-Company C'],
                            'Price'           => ['N' => '500'],
                            'Color'           => ['SS' => ['Red', 'Black']],
                            'ProductCategory' => ['S' => 'Bicycle']            
                        ]
                    ]
                ]
            ],
        ],
    ]);
    echo "done.\n";
} catch (DynamoDbException $e) {
    echo $e->getMessage() . "\n";
    exit ("Unable to load data into $tableName\n");
}




$tableName = 'Forum';
echo "Adding data to the $tableName table...\n";

try {
    $response = $dynamodb->batchWriteItem([
        'RequestItems' => [
            $tableName => [
                [
                    'PutRequest' => [
                        'Item' => [
                            'Name'     => ['S' => 'Amazon DynamoDB'],
                            'Category' => ['S' => 'Amazon Web Services'],
                            'Threads'  => ['N' => '0'],
                            'Messages' => ['N' => '0'],
                            'Views'    => ['N' => '1000']
                        ]
                    ]
                ],
                [
                    'PutRequest' => [
                        'Item' => [
                            'Name'     => ['S' => 'Amazon S3'],
                            'Category' => ['S' => 'Amazon Web Services'],
                            'Threads'  => ['N' => '0']
                        ]
                    ]
                ],
            ]
        ]
    ]);
    echo "done.\n";
    
} catch (DynamoDbException $e) {
    echo $e->getMessage() . "\n";
    exit ("Unable to load data into $tableName\n");
}

$tableName = 'Thread';
echo "Adding data to the $tableName table...\n";

try {
    $response = $dynamodb->batchWriteItem([
        'RequestItems' => [
            $tableName => [
                [
                    'PutRequest' => [
                        'Item' => [
                            'ForumName'          => ['S'=>'Amazon DynamoDB'],
                            'Subject'            => ['S'=> 'DynamoDB Thread 1'],
                            'Message'            => ['S'=>'DynamoDB thread 1 message'],
                            'LastPostedBy'       => ['S'=>'User A'],
                            'LastPostedDateTime' => ['S'=>$fourteenDaysAgo],
                            'Views'              => ['N'=>'0'],
                            'Replies'            => ['N'=>'0'],
                            'Answered'           => ['N'=>'0'],
                            'Tags'               => ['SS' => ['index', 'primarykey', 'table']]
                        ]
                    ],
                    'PutRequest' => [
                        'Item' => [
                            'ForumName'          => ['S'=>'Amazon DynamoDB'],
                            'Subject'            => ['S'=> 'DynamoDB Thread 2'],
                            'Message'            => ['S'=>'DynamoDB thread 2 message'],
                            'LastPostedBy'       => ['S'=>'User A'],
                            'LastPostedDateTime' => ['S'=>$twentyOneDaysAgo],
                            'Views'              => ['N'=>'0'],
                            'Replies'            => ['N'=>'0'],
                            'Answered'           => ['N'=>'0'],
                            'Tags'               => ['SS' => ['index', 'partitionkey', 'sortkey']]
                        ]
                    ],
                    'PutRequest' => [
                        'Item' => [
                            'ForumName'          => ['S'=>'Amazon S3'],
                            'Subject'            => ['S'=> 'S3 Thread 1'],
                            'Message'            => ['S'=>'S3 Thread 3 message'],
                            'LastPostedBy'       => ['S'=>'User A'],
                            'LastPostedDateTime' => ['S'=>$sevenDaysAgo],
                            'Views'              => ['N'=>'0'],
                            'Replies'            => ['N'=>'0'],
                            'Answered'           => ['N'=>'0'],
                            'Tags'               => ['SS' => ['largeobjects', 'multipart upload']]
                        ]
                    ]
                ]
            ]
        ]
    ]);
    echo "done.\n";

} catch (DynamoDbException $e) {
    echo $e->getMessage() . "\n";
    exit ("Unable to load data into $tableName\n");
}

$tableName = 'Reply';
echo "Adding data to the $tableName table...\n";

try {
    $response = $dynamodb->batchWriteItem([
        'RequestItems' => [
            $tableName => [
                [
                    'PutRequest' => [
                        'Item' => [
                            'Id'            => ['S' => 'Amazon DynamoDB#DynamoDB Thread 1'],
                            'ReplyDateTime' => ['S' => $fourteenDaysAgo], 
                            'Message'       => ['S' => 'DynamoDB Thread 1 Reply 2 text'],
                            'PostedBy'      => ['S' => 'User B']
                        ]
                    ]
                ],
                [
                    'PutRequest' => [
                        'Item' => [
                            'Id'            => ['S' => 'Amazon DynamoDB#DynamoDB Thread 2'], 
                            'ReplyDateTime' => ['S' => $twentyOneDaysAgo], 
                            'Message'       => ['S' => 'DynamoDB Thread 2 Reply 3 text'],
                            'PostedBy'      => ['S' => 'User B']
                        ]
                    ]
                ],
                [
                    'PutRequest' => [
                        'Item' => [
                            'Id'            => ['S' => 'Amazon DynamoDB#DynamoDB Thread 2'],
                            'ReplyDateTime' => ['S' => $sevenDaysAgo],
                            'Message'       => ['S' => 'DynamoDB Thread 2 Reply 2 text'],
                            'PostedBy'      => ['S' => 'User A']
                        ]
                    ]
                ],
                [
                    'PutRequest' => [
                        'Item' => [
                            'Id'            => ['S' => 'Amazon DynamoDB#DynamoDB Thread 2'],
                            'ReplyDateTime' => ['S' => $oneDayAgo], 
                            'Message'       => ['S' => 'DynamoDB Thread 2 Reply 1 text'],
                            'PostedBy'      => ['S' => 'User A']
                        ]
                    ]
                ]
            ],
        ]
      ]);

echo "done.\n";
} catch (DynamoDbException $e) {
    echo $e->getMessage() . "\n";
    exit ("Unable to load data into $tableName\n");
}


// snippet-end:[dynamodb.php.codeexample.SampleDataLoad] 
?>