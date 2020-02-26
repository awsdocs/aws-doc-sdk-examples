<?php
/*
 * This file is part of sebastian/comparator.
 *
 * (c) Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
namespace SebastianBergmann\Comparator;

use PHPUnit\Framework\TestCase;

/**
 * @covers \SebastianBergmann\Comparator\NumericComparator<extended>
 *
 * @uses \SebastianBergmann\Comparator\Comparator
 * @uses \SebastianBergmann\Comparator\Factory
 * @uses \SebastianBergmann\Comparator\ComparisonFailure
 */
final class NumericComparatorTest extends TestCase
{
    /**
     * @var NumericComparator
     */
    private $comparator;

    protected function setUp(): void
    {
        $this->comparator = new NumericComparator;
    }

    public function acceptsSucceedsProvider()
    {
        return [
            [5, 10],
            [8, '0'],
            ['10', 0],
            [0x74c3b00c, 42],
            [0755, 0777]
        ];
    }

    public function acceptsFailsProvider()
    {
        return [
            ['5', '10'],
            [8, 5.0],
            [5.0, 8],
            [10, null],
            [false, 12]
        ];
    }

    public function assertEqualsSucceedsProvider()
    {
        return [
            [1337, 1337],
            ['1337', 1337],
            [0x539, 1337],
            [02471, 1337],
            [1337, 1338, 1],
            ['1337', 1340, 5],
            [INF, INF],
        ];
    }

    public function assertEqualsFailsProvider()
    {
        return [
            [1337, 1338],
            ['1338', 1337],
            [0x539, 1338],
            [1337, 1339, 1],
            ['1337', 1340, 2],
        ];
    }

    /**
     * @dataProvider acceptsSucceedsProvider
     */
    public function testAcceptsSucceeds($expected, $actual): void
    {
        $this->assertTrue(
          $this->comparator->accepts($expected, $actual)
        );
    }

    /**
     * @dataProvider acceptsFailsProvider
     */
    public function testAcceptsFails($expected, $actual): void
    {
        $this->assertFalse(
          $this->comparator->accepts($expected, $actual)
        );
    }

    /**
     * @dataProvider assertEqualsSucceedsProvider
     */
    public function testAssertEqualsSucceeds($expected, $actual, $delta = 0.0): void
    {
        $exception = null;

        try {
            $this->comparator->assertEquals($expected, $actual, $delta);
        } catch (ComparisonFailure $exception) {
        }

        $this->assertNull($exception, 'Unexpected ComparisonFailure');
    }

    /**
     * @dataProvider assertEqualsFailsProvider
     */
    public function testAssertEqualsFails($expected, $actual, $delta = 0.0): void
    {
        $this->expectException(ComparisonFailure::class);
        $this->expectExceptionMessage('matches expected');

        $this->comparator->assertEquals($expected, $actual, $delta);
    }
}
