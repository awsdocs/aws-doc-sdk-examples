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
 * @covers \SebastianBergmann\Comparator\DoubleComparator<extended>
 *
 * @uses \SebastianBergmann\Comparator\Comparator
 * @uses \SebastianBergmann\Comparator\Factory
 * @uses \SebastianBergmann\Comparator\ComparisonFailure
 */
final class DoubleComparatorTest extends TestCase
{
    /**
     * @var DoubleComparator
     */
    private $comparator;

    protected function setUp(): void
    {
        $this->comparator = new DoubleComparator;
    }

    public function acceptsSucceedsProvider()
    {
        return [
            [0, 5.0],
            [5.0, 0],
            ['5', 4.5],
            [1.2e3, 7E-10],
            [3, \acos(8)],
            [\acos(8), 3],
            [\acos(8), \acos(8)]
        ];
    }

    public function acceptsFailsProvider()
    {
        return [
            [5, 5],
            ['4.5', 5],
            [0x539, 02471],
            [5.0, false],
            [null, 5.0]
        ];
    }

    public function assertEqualsSucceedsProvider()
    {
        return [
            [2.3, 2.3],
            ['2.3', 2.3],
            [5.0, 5],
            [5, 5.0],
            [5.0, '5'],
            [1.2e3, 1200],
            [2.3, 2.5, 0.5],
            [3, 3.05, 0.05],
            [1.2e3, 1201, 1],
            [(string) (1 / 3), 1 - 2 / 3],
            [1 / 3, (string) (1 - 2 / 3)]
        ];
    }

    public function assertEqualsFailsProvider()
    {
        return [
            [2.3, 4.2],
            ['2.3', 4.2],
            [5.0, '4'],
            [5.0, 6],
            [1.2e3, 1201],
            [2.3, 2.5, 0.2],
            [3, 3.05, 0.04],
            [3, \acos(8)],
            [\acos(8), 3],
            [\acos(8), \acos(8)]
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
