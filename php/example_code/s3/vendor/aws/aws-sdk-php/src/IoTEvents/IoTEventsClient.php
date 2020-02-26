<?php
namespace Aws\IoTEvents;

use Aws\AwsClient;

/**
 * This client is used to interact with the **AWS IoT Events** service.
 * @method \Aws\Result createDetectorModel(array $args = [])
 * @method \GuzzleHttp\Promise\Promise createDetectorModelAsync(array $args = [])
 * @method \Aws\Result createInput(array $args = [])
 * @method \GuzzleHttp\Promise\Promise createInputAsync(array $args = [])
 * @method \Aws\Result deleteDetectorModel(array $args = [])
 * @method \GuzzleHttp\Promise\Promise deleteDetectorModelAsync(array $args = [])
 * @method \Aws\Result deleteInput(array $args = [])
 * @method \GuzzleHttp\Promise\Promise deleteInputAsync(array $args = [])
 * @method \Aws\Result describeDetectorModel(array $args = [])
 * @method \GuzzleHttp\Promise\Promise describeDetectorModelAsync(array $args = [])
 * @method \Aws\Result describeInput(array $args = [])
 * @method \GuzzleHttp\Promise\Promise describeInputAsync(array $args = [])
 * @method \Aws\Result describeLoggingOptions(array $args = [])
 * @method \GuzzleHttp\Promise\Promise describeLoggingOptionsAsync(array $args = [])
 * @method \Aws\Result listDetectorModelVersions(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listDetectorModelVersionsAsync(array $args = [])
 * @method \Aws\Result listDetectorModels(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listDetectorModelsAsync(array $args = [])
 * @method \Aws\Result listInputs(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listInputsAsync(array $args = [])
 * @method \Aws\Result listTagsForResource(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listTagsForResourceAsync(array $args = [])
 * @method \Aws\Result putLoggingOptions(array $args = [])
 * @method \GuzzleHttp\Promise\Promise putLoggingOptionsAsync(array $args = [])
 * @method \Aws\Result tagResource(array $args = [])
 * @method \GuzzleHttp\Promise\Promise tagResourceAsync(array $args = [])
 * @method \Aws\Result untagResource(array $args = [])
 * @method \GuzzleHttp\Promise\Promise untagResourceAsync(array $args = [])
 * @method \Aws\Result updateDetectorModel(array $args = [])
 * @method \GuzzleHttp\Promise\Promise updateDetectorModelAsync(array $args = [])
 * @method \Aws\Result updateInput(array $args = [])
 * @method \GuzzleHttp\Promise\Promise updateInputAsync(array $args = [])
 */
class IoTEventsClient extends AwsClient {}
