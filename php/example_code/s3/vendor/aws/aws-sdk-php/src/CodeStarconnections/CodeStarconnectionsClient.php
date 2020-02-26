<?php
namespace Aws\CodeStarconnections;

use Aws\AwsClient;

/**
 * This client is used to interact with the **AWS CodeStar connections** service.
 * @method \Aws\Result createConnection(array $args = [])
 * @method \GuzzleHttp\Promise\Promise createConnectionAsync(array $args = [])
 * @method \Aws\Result deleteConnection(array $args = [])
 * @method \GuzzleHttp\Promise\Promise deleteConnectionAsync(array $args = [])
 * @method \Aws\Result getConnection(array $args = [])
 * @method \GuzzleHttp\Promise\Promise getConnectionAsync(array $args = [])
 * @method \Aws\Result listConnections(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listConnectionsAsync(array $args = [])
 */
class CodeStarconnectionsClient extends AwsClient {}
