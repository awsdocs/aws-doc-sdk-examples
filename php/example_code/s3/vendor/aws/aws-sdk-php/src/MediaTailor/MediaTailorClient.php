<?php
namespace Aws\MediaTailor;

use Aws\AwsClient;

/**
 * This client is used to interact with the **AWS MediaTailor** service.
 * @method \Aws\Result deletePlaybackConfiguration(array $args = [])
 * @method \GuzzleHttp\Promise\Promise deletePlaybackConfigurationAsync(array $args = [])
 * @method \Aws\Result getPlaybackConfiguration(array $args = [])
 * @method \GuzzleHttp\Promise\Promise getPlaybackConfigurationAsync(array $args = [])
 * @method \Aws\Result listPlaybackConfigurations(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listPlaybackConfigurationsAsync(array $args = [])
 * @method \Aws\Result listTagsForResource(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listTagsForResourceAsync(array $args = [])
 * @method \Aws\Result putPlaybackConfiguration(array $args = [])
 * @method \GuzzleHttp\Promise\Promise putPlaybackConfigurationAsync(array $args = [])
 * @method \Aws\Result tagResource(array $args = [])
 * @method \GuzzleHttp\Promise\Promise tagResourceAsync(array $args = [])
 * @method \Aws\Result untagResource(array $args = [])
 * @method \GuzzleHttp\Promise\Promise untagResourceAsync(array $args = [])
 */
class MediaTailorClient extends AwsClient {}
