<?php declare(strict_types=1);
/*
 * This file is part of resource-operations.
 *
 * (c) Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
namespace SebastianBergmann\ResourceOperations;

use PHPUnit\Framework\TestCase;

/**
 * @covers \SebastianBergmann\ResourceOperations\ResourceOperations
 */
final class ResourceOperationsTest extends TestCase
{
    public function testGetFunctions(): void
    {
        $functions = ResourceOperations::getFunctions();

        $this->assertIsArray($functions);
        $this->assertContains('fopen', $functions);
    }
}
