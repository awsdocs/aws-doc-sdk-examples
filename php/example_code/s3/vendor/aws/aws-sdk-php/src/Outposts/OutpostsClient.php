<?php
namespace Aws\Outposts;

use Aws\AwsClient;

/**
 * This client is used to interact with the **AWS Outposts** service.
 * @method \Aws\Result createOutpost(array $args = [])
 * @method \GuzzleHttp\Promise\Promise createOutpostAsync(array $args = [])
 * @method \Aws\Result getOutpost(array $args = [])
 * @method \GuzzleHttp\Promise\Promise getOutpostAsync(array $args = [])
 * @method \Aws\Result getOutpostInstanceTypes(array $args = [])
 * @method \GuzzleHttp\Promise\Promise getOutpostInstanceTypesAsync(array $args = [])
 * @method \Aws\Result listOutposts(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listOutpostsAsync(array $args = [])
 * @method \Aws\Result listSites(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listSitesAsync(array $args = [])
 */
class OutpostsClient extends AwsClient {}
