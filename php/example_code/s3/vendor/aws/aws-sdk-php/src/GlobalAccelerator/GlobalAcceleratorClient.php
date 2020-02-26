<?php
namespace Aws\GlobalAccelerator;

use Aws\AwsClient;

/**
 * This client is used to interact with the **AWS Global Accelerator** service.
 * @method \Aws\Result createAccelerator(array $args = [])
 * @method \GuzzleHttp\Promise\Promise createAcceleratorAsync(array $args = [])
 * @method \Aws\Result createEndpointGroup(array $args = [])
 * @method \GuzzleHttp\Promise\Promise createEndpointGroupAsync(array $args = [])
 * @method \Aws\Result createListener(array $args = [])
 * @method \GuzzleHttp\Promise\Promise createListenerAsync(array $args = [])
 * @method \Aws\Result deleteAccelerator(array $args = [])
 * @method \GuzzleHttp\Promise\Promise deleteAcceleratorAsync(array $args = [])
 * @method \Aws\Result deleteEndpointGroup(array $args = [])
 * @method \GuzzleHttp\Promise\Promise deleteEndpointGroupAsync(array $args = [])
 * @method \Aws\Result deleteListener(array $args = [])
 * @method \GuzzleHttp\Promise\Promise deleteListenerAsync(array $args = [])
 * @method \Aws\Result describeAccelerator(array $args = [])
 * @method \GuzzleHttp\Promise\Promise describeAcceleratorAsync(array $args = [])
 * @method \Aws\Result describeAcceleratorAttributes(array $args = [])
 * @method \GuzzleHttp\Promise\Promise describeAcceleratorAttributesAsync(array $args = [])
 * @method \Aws\Result describeEndpointGroup(array $args = [])
 * @method \GuzzleHttp\Promise\Promise describeEndpointGroupAsync(array $args = [])
 * @method \Aws\Result describeListener(array $args = [])
 * @method \GuzzleHttp\Promise\Promise describeListenerAsync(array $args = [])
 * @method \Aws\Result listAccelerators(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listAcceleratorsAsync(array $args = [])
 * @method \Aws\Result listEndpointGroups(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listEndpointGroupsAsync(array $args = [])
 * @method \Aws\Result listListeners(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listListenersAsync(array $args = [])
 * @method \Aws\Result updateAccelerator(array $args = [])
 * @method \GuzzleHttp\Promise\Promise updateAcceleratorAsync(array $args = [])
 * @method \Aws\Result updateAcceleratorAttributes(array $args = [])
 * @method \GuzzleHttp\Promise\Promise updateAcceleratorAttributesAsync(array $args = [])
 * @method \Aws\Result updateEndpointGroup(array $args = [])
 * @method \GuzzleHttp\Promise\Promise updateEndpointGroupAsync(array $args = [])
 * @method \Aws\Result updateListener(array $args = [])
 * @method \GuzzleHttp\Promise\Promise updateListenerAsync(array $args = [])
 */
class GlobalAcceleratorClient extends AwsClient {}
