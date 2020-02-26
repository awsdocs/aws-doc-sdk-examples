<?php

declare(strict_types=1);

namespace phpDocumentor\Reflection\DocBlock\Tags;

use Closure;
use phpDocumentor\Reflection\DocBlock\Tag;
use ReflectionClass;
use ReflectionFunction;
use Throwable;
use function array_map;
use function array_walk_recursive;
use function get_class;
use function get_resource_type;
use function is_object;
use function is_resource;
use function sprintf;

/**
 * This class represents an exception during the tag creation
 *
 * Since the internals of the library are relaying on the correct syntax of a docblock
 * we cannot simply throw exceptions at all time because the exceptions will break the creation of a
 * docklock. Just silently ignore the exceptions is not an option because the user as an issue to fix.
 *
 * This tag holds that error information until a using application is able to display it. The object wil just behave
 * like any normal tag. So the normal application flow will not break.
 */
final class InvalidTag implements Tag
{
    /** @var string */
    private $name;

    /** @var string */
    private $body;

    /** @var Throwable|null */
    private $throwable;

    private function __construct(string $name, string $body)
    {
        $this->name = $name;
        $this->body = $body;
    }

    public function getException() : ?Throwable
    {
        return $this->throwable;
    }

    public function getName() : string
    {
        return $this->name;
    }

    /**
     * @return self
     *
     * @inheritDoc
     */
    public static function create(string $body, string $name = '')
    {
        return new self($name, $body);
    }

    public function withError(Throwable $exception) : self
    {
        $this->flattenExceptionBacktrace($exception);
        $tag            = new self($this->name, $this->body);
        $tag->throwable = $exception;

        return $tag;
    }

    /**
     * Removes all complex types from backtrace
     *
     * Not all objects are serializable. So we need to remove them from the
     * stored exception to be sure that we do not break existing library usage.
     */
    private function flattenExceptionBacktrace(Throwable $exception) : void
    {
        $traceProperty = (new ReflectionClass('Exception'))->getProperty('trace');
        $traceProperty->setAccessible(true);

        $flatten =
            /** @param mixed $value */
            static function (&$value) : void {
                if ($value instanceof Closure) {
                    $closureReflection = new ReflectionFunction($value);
                    $value             = sprintf(
                        '(Closure at %s:%s)',
                        $closureReflection->getFileName(),
                        $closureReflection->getStartLine()
                    );
                } elseif (is_object($value)) {
                    $value = sprintf('object(%s)', get_class($value));
                } elseif (is_resource($value)) {
                    $value = sprintf('resource(%s)', get_resource_type($value));
                }
            };

        do {
            $trace = array_map(
                static function (array $call) use ($flatten) : array {
                    array_walk_recursive($call['args'], $flatten);

                    return $call;
                },
                $exception->getTrace()
            );
            $traceProperty->setValue($exception, $trace);
            $exception = $exception->getPrevious();
        } while ($exception !== null);

        $traceProperty->setAccessible(false);
    }

    public function render(?Formatter $formatter = null) : string
    {
        if ($formatter === null) {
            $formatter = new Formatter\PassthroughFormatter();
        }

        return $formatter->format($this);
    }

    public function __toString() : string
    {
        return $this->body;
    }
}
