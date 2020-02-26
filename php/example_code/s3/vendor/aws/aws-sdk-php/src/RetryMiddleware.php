<?php
namespace Aws;

use Aws\Exception\AwsException;
use GuzzleHttp\Exception\RequestException;
use Psr\Http\Message\RequestInterface;
use GuzzleHttp\Promise\PromiseInterface;
use GuzzleHttp\Promise;

/**
 * @internal Middleware that retries failures.
 */
class RetryMiddleware
{
    private static $retryStatusCodes = [
        500 => true,
        502 => true,
        503 => true,
        504 => true
    ];

    private static $retryCodes = [
        // Throttling error
        'RequestLimitExceeded'                   => true,
        'Throttling'                             => true,
        'ThrottlingException'                    => true,
        'ThrottledException'                     => true,
        'ProvisionedThroughputExceededException' => true,
        'RequestThrottled'                       => true,
        'BandwidthLimitExceeded'                 => true,
        'RequestThrottledException'              => true,
        'TooManyRequestsException'               => true,
        'IDPCommunicationError'                  => true,
        'EC2ThrottledException'                  => true,
    ];

    private $decider;
    private $delay;
    private $nextHandler;
    private $collectStats;

    public function __construct(
        callable $decider,
        callable $delay,
        callable $nextHandler,
        $collectStats = false
    ) {
        $this->decider = $decider;
        $this->delay = $delay;
        $this->nextHandler = $nextHandler;
        $this->collectStats = (bool) $collectStats;
    }

    /**
     * Creates a default AWS retry decider function.
     *
     * The optional $additionalRetryConfig parameter is an associative array
     * that specifies additional retry conditions on top of the ones specified
     * by default by the Aws\RetryMiddleware class, with the following keys:
     *
     * - errorCodes: (string[]) An indexed array of AWS exception codes to retry.
     *   Optional.
     * - statusCodes: (int[]) An indexed array of HTTP status codes to retry.
     *   Optional.
     * - curlErrors: (int[]) An indexed array of Curl error codes to retry. Note
     *   these should be valid Curl constants. Optional.
     *
     * @param int $maxRetries
     * @param array $additionalRetryConfig
     * @return callable
     */
    public static function createDefaultDecider(
        $maxRetries = 3,
        $additionalRetryConfig = []
    ) {
        $retryCurlErrors = [];
        if (extension_loaded('curl')) {
            $retryCurlErrors[CURLE_RECV_ERROR] = true;
        }

        return function (
            $retries,
            CommandInterface $command,
            RequestInterface $request,
            ResultInterface $result = null,
            $error = null
        ) use ($maxRetries, $retryCurlErrors, $additionalRetryConfig) {
            // Allow command-level options to override this value
            $maxRetries = null !== $command['@retries'] ?
                $command['@retries']
                : $maxRetries;

            $isRetryable = self::isRetryable(
                $result,
                $error,
                $retryCurlErrors,
                $additionalRetryConfig
            );

            if ($retries >= $maxRetries) {
                if (!empty($error)
                    && $error instanceof AwsException
                    && $isRetryable
                ) {
                    $error->setMaxRetriesExceeded();
                }
                return false;
            }

            return $isRetryable;
        };
    }

    private static function isRetryable(
        $result,
        $error,
        $retryCurlErrors,
        $additionalRetryConfig = []
    ) {
        $errorCodes = self::$retryCodes;
        if (!empty($additionalRetryConfig['errorCodes'])
            && is_array($additionalRetryConfig['errorCodes'])
        ) {
            foreach($additionalRetryConfig['errorCodes'] as $code) {
                $errorCodes[$code] = true;
            }
        }

        $statusCodes = self::$retryStatusCodes;
        if (!empty($additionalRetryConfig['statusCodes'])
            && is_array($additionalRetryConfig['statusCodes'])
        ) {
            foreach($additionalRetryConfig['statusCodes'] as $code) {
                $statusCodes[$code] = true;
            }
        }

        if (!empty($additionalRetryConfig['curlErrors'])
            && is_array($additionalRetryConfig['curlErrors'])
        ) {
            foreach($additionalRetryConfig['curlErrors'] as $code) {
                $retryCurlErrors[$code] = true;
            }
        }

        if (!$error) {
            if (!isset($result['@metadata']['statusCode'])) {
                return false;
            }
            return isset($statusCodes[$result['@metadata']['statusCode']]);
        }

        if (!($error instanceof AwsException)) {
            return false;
        }

        if ($error->isConnectionError()) {
            return true;
        }

        if (isset($errorCodes[$error->getAwsErrorCode()])) {
            return true;
        }

        if (isset($statusCodes[$error->getStatusCode()])) {
            return true;
        }

        if (count($retryCurlErrors)
            && ($previous = $error->getPrevious())
            && $previous instanceof RequestException
        ) {
            if (method_exists($previous, 'getHandlerContext')) {
                $context = $previous->getHandlerContext();
                return !empty($context['errno'])
                    && isset($retryCurlErrors[$context['errno']]);
            }

            $message = $previous->getMessage();
            foreach (array_keys($retryCurlErrors) as $curlError) {
                if (strpos($message, 'cURL error ' . $curlError . ':') === 0) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Delay function that calculates an exponential delay.
     *
     * Exponential backoff with jitter, 100ms base, 20 sec ceiling
     *
     * @param $retries - The number of retries that have already been attempted
     *
     * @return int
     *
     * @link https://aws.amazon.com/blogs/architecture/exponential-backoff-and-jitter/
     */
    public static function exponentialDelay($retries)
    {
        return mt_rand(0, (int) min(20000, (int) pow(2, $retries) * 100));
    }

    /**
     * @param CommandInterface $command
     * @param RequestInterface $request
     *
     * @return PromiseInterface
     */
    public function __invoke(
        CommandInterface $command,
        RequestInterface $request = null
    ) {
        $retries = 0;
        $requestStats = [];
        $monitoringEvents = [];
        $handler = $this->nextHandler;
        $decider = $this->decider;
        $delay = $this->delay;

        $request = $this->addRetryHeader($request, 0, 0);

        $g = function ($value) use (
            $handler,
            $decider,
            $delay,
            $command,
            $request,
            &$retries,
            &$requestStats,
            &$monitoringEvents,
            &$g
        ) {
            $this->updateHttpStats($value, $requestStats);

            if ($value instanceof MonitoringEventsInterface) {
                $reversedEvents = array_reverse($monitoringEvents);
                $monitoringEvents = array_merge($monitoringEvents, $value->getMonitoringEvents());
                foreach ($reversedEvents as $event) {
                    $value->prependMonitoringEvent($event);
                }
            }
            if ($value instanceof \Exception || $value instanceof \Throwable) {
                if (!$decider($retries, $command, $request, null, $value)) {
                    return Promise\rejection_for(
                        $this->bindStatsToReturn($value, $requestStats)
                    );
                }
            } elseif ($value instanceof ResultInterface
                && !$decider($retries, $command, $request, $value, null)
            ) {
                return $this->bindStatsToReturn($value, $requestStats);
            }

            // Delay fn is called with 0, 1, ... so increment after the call.
            $delayBy = $delay($retries++);
            $command['@http']['delay'] = $delayBy;
            if ($this->collectStats) {
                $this->updateStats($retries, $delayBy, $requestStats);
            }

            // Update retry header with retry count and delayBy
            $request = $this->addRetryHeader($request, $retries, $delayBy);

            return $handler($command, $request)->then($g, $g);
        };

        return $handler($command, $request)->then($g, $g);
    }

    private function addRetryHeader($request, $retries, $delayBy)
    {
        return $request->withHeader('aws-sdk-retry', "{$retries}/{$delayBy}");
    }

    private function updateStats($retries, $delay, array &$stats)
    {
        if (!isset($stats['total_retry_delay'])) {
            $stats['total_retry_delay'] = 0;
        }

        $stats['total_retry_delay'] += $delay;
        $stats['retries_attempted'] = $retries;
    }

    private function updateHttpStats($value, array &$stats)
    {
        if (empty($stats['http'])) {
            $stats['http'] = [];
        }

        if ($value instanceof AwsException) {
            $resultStats = isset($value->getTransferInfo('http')[0])
                ? $value->getTransferInfo('http')[0]
                : [];
            $stats['http'] []= $resultStats;
        } elseif ($value instanceof ResultInterface) {
            $resultStats = isset($value['@metadata']['transferStats']['http'][0])
                ? $value['@metadata']['transferStats']['http'][0]
                : [];
            $stats['http'] []= $resultStats;
        }
    }

    private function bindStatsToReturn($return, array $stats)
    {
        if ($return instanceof ResultInterface) {
            if (!isset($return['@metadata'])) {
                $return['@metadata'] = [];
            }

            $return['@metadata']['transferStats'] = $stats;
        } elseif ($return instanceof AwsException) {
            $return->setTransferInfo($stats);
        }

        return $return;
    }
}
