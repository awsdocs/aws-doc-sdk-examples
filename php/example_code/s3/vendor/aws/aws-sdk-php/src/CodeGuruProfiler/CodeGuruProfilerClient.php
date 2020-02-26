<?php
namespace Aws\CodeGuruProfiler;

use Aws\AwsClient;

/**
 * This client is used to interact with the **Amazon CodeGuru Profiler** service.
 * @method \Aws\Result configureAgent(array $args = [])
 * @method \GuzzleHttp\Promise\Promise configureAgentAsync(array $args = [])
 * @method \Aws\Result createProfilingGroup(array $args = [])
 * @method \GuzzleHttp\Promise\Promise createProfilingGroupAsync(array $args = [])
 * @method \Aws\Result deleteProfilingGroup(array $args = [])
 * @method \GuzzleHttp\Promise\Promise deleteProfilingGroupAsync(array $args = [])
 * @method \Aws\Result describeProfilingGroup(array $args = [])
 * @method \GuzzleHttp\Promise\Promise describeProfilingGroupAsync(array $args = [])
 * @method \Aws\Result getProfile(array $args = [])
 * @method \GuzzleHttp\Promise\Promise getProfileAsync(array $args = [])
 * @method \Aws\Result listProfileTimes(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listProfileTimesAsync(array $args = [])
 * @method \Aws\Result listProfilingGroups(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listProfilingGroupsAsync(array $args = [])
 * @method \Aws\Result postAgentProfile(array $args = [])
 * @method \GuzzleHttp\Promise\Promise postAgentProfileAsync(array $args = [])
 * @method \Aws\Result updateProfilingGroup(array $args = [])
 * @method \GuzzleHttp\Promise\Promise updateProfilingGroupAsync(array $args = [])
 */
class CodeGuruProfilerClient extends AwsClient {}
