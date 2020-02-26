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
use stdClass;

/**
 * @covers \SebastianBergmann\Comparator\TypeComparator<extended>
 *
 * @uses \SebastianBergmann\Comparator\Comparator
 * @uses \SebastianBergmann\Comparator\Factory
 * @uses \SebastianBergmann\Comparator\ComparisonFailure
 */
final class TypeComparatorTest extends TestCase
{
    /**
     * @var TypeComparator
     */
    private $comparator;

    protected function setUp(): void
    {
        $this->comparator = new TypeComparator;
    }

    public function acceptsSucceedsProvider()
    {
        return [
            [true, 1],
            [false, [1]],
            [null, new stdClass],
            [1.0, 5],
            ['', '']
        ];
    }

    public function assertEqualsSucceedsProvider()
    {
        return [
            [true, true],
            [true, false],
            [false, false],
            [null, null],
            [new stdClass, new stdClass],
            [0, 0],
            [1.0, 2.0],
            ['hello', 'world'],
            ['', ''],
            [[], [1, 2, 3]]
        ];
    }

    public function assertEqualsFailsProvider()
    {
        return [
            [true, null],
            [null, false],
            [1.0, 0],
            [new stdClass, []],
            ['1', 1]
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
     * @dataProvider assertEqualsSucceedsProvider
     */
    public function testAssertEqualsSucceeds($expected, $actual): void
    {
        $exception = null;

        try {
            $this->comparator->assertEquals($expected, $actual);
        } catch (ComparisonFailure $exception) {
        }

        $this->assertNull($exception, 'Unexpected ComparisonFailure');
    }

    /**
     * @dataProvider assertEqualsFailsProvider
     */
    public function testAssertEqualsFails($expected, $actual): void
    {
        $this->expectException(ComparisonFailure::class);
        $this->expectExceptionMessage('does not match expected type');

        $this->comparator->assertEquals($expected, $actual);
    }
}
