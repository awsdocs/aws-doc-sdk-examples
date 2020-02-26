<?php declare(strict_types=1);
/*
 * This file is part of php-invoker.
 *
 * (c) Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
namespace SebastianBergmann\Invoker;

use PHPUnit\Framework\TestCase;

/**
 * @covers \SebastianBergmann\Invoker\Invoker
 */
final class InvokerTest extends TestCase
{
    /**
     * @var \TestCallable
     */
    private $callable;

    /**
     * @var Invoker
     */
    private $invoker;

    protected function setUp(): void
    {
        $this->callable = new \TestCallable;
        $this->invoker  = new Invoker;
    }

    /**
     * @requires extension pcntl
     */
    public function testExecutionOfCallableIsNotAbortedWhenTimeoutIsNotReached(): void
    {
        $this->assertTrue(
            $this->invoker->invoke([$this->callable, 'test'], [0], 1)
        );
    }

    /**
     * @requires extension pcntl
     */
    public function testExecutionOfCallableIsAbortedWhenTimeoutIsReached(): void
    {
        $this->expectException(TimeoutException::class);
        $this->expectExceptionMessage('Execution aborted after 1 second');

        $this->invoker->invoke([$this->callable, 'test'], [2], 1);
    }

    /**
     * @requires extension pcntl
     */
    public function testRequirementsCanBeChecked(): void
    {
        $this->assertTrue($this->invoker->canInvokeWithTimeout());
    }
}
