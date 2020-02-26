<?php declare(strict_types=1);
/*
 * This file is part of php-invoker.
 *
 * (c) Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

class TestCallable
{
    public function test(int $sleep): bool
    {
        \sleep($sleep);

        return true;
    }
}
