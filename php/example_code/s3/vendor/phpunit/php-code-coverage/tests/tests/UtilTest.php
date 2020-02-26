<?php declare(strict_types=1);
/*
 * This file is part of phpunit/php-code-coverage.
 *
 * (c) Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
namespace SebastianBergmann\CodeCoverage;

use PHPUnit\Framework\TestCase;

/**
 * @covers SebastianBergmann\CodeCoverage\Util
 */
class UtilTest extends TestCase
{
    public function testPercent(): void
    {
        $this->assertEquals(100, Util::percent(100, 0));
        $this->assertEquals(100, Util::percent(100, 100));
        $this->assertEquals(
            '100.00%',
            Util::percent(100, 100, true)
        );
    }
}
