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
 * @covers \SebastianBergmann\Comparator\ArrayComparator<extended>
 *
 * @uses \SebastianBergmann\Comparator\Comparator
 * @uses \SebastianBergmann\Comparator\Factory
 * @uses \SebastianBergmann\Comparator\ComparisonFailure
 */
final class ArrayComparatorTest extends TestCase
{
    /**
     * @var ArrayComparator
     */
    private $comparator;

    protected function setUp(): void
    {
        $this->comparator = new ArrayComparator;
        $this->comparator->setFactory(new Factory);
    }

    public function acceptsFailsProvider()
    {
        return [
            [[], null],
            [null, []],
            [null, null]
        ];
    }

    public function assertEqualsSucceedsProvider()
    {
        return [
            [
                ['a' => 1, 'b' => 2],
                ['b' => 2, 'a' => 1]
            ],
            [
                [1],
                ['1']
            ],
            [
                [3, 2, 1],
                [2, 3, 1],
                0,
                true
            ],
            [
                [2.3],
                [2.5],
                0.5
            ],
            [
                [[2.3]],
                [[2.5]],
                0.5
            ],
            [
                [new Struct(2.3)],
                [new Struct(2.5)],
                0.5
            ],
        ];
    }

    public function assertEqualsFailsProvider()
    {
        return [
            [
                [],
                [0 => 1]
            ],
            [
                [0 => 1],
                []
            ],
            [
                [0 => null],
                []
            ],
            [
                [0 => 1, 1 => 2],
                [0 => 1, 1 => 3]
            ],
            [
                ['a', 'b' => [1, 2]],
                ['a', 'b' => [2, 1]]
            ],
            [
                [2.3],
                [4.2],
                0.5
            ],
            [
                [[2.3]],
                [[4.2]],
                0.5
            ],
            [
                [new Struct(2.3)],
                [new Struct(4.2)],
                0.5
            ]
        ];
    }

    public function testAcceptsSucceeds(): void
    {
        $this->assertTrue(
          $this->comparator->accepts([], [])
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
    public function testAssertEqualsSucceeds($expected, $actual, $delta = 0.0, $canonicalize = false): void
    {
        $exception = null;

        try {
            $this->comparator->assertEquals($expected, $actual, $delta, $canonicalize);
        } catch (ComparisonFailure $exception) {
        }

        $this->assertNull($exception, 'Unexpected ComparisonFailure');
    }

    /**
     * @dataProvider assertEqualsFailsProvider
     */
    public function testAssertEqualsFails($expected, $actual, $delta = 0.0, $canonicalize = false): void
    {
        $this->expectException(ComparisonFailure::class);
        $this->expectExceptionMessage('Failed asserting that two arrays are equal');

        $this->comparator->assertEquals($expected, $actual, $delta, $canonicalize);
    }
}
