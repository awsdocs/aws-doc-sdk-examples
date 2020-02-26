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
use SplObjectStorage;
use stdClass;

/**
 * @covers \SebastianBergmann\Comparator\SplObjectStorageComparator<extended>
 *
 * @uses \SebastianBergmann\Comparator\Comparator
 * @uses \SebastianBergmann\Comparator\Factory
 * @uses \SebastianBergmann\Comparator\ComparisonFailure
 */
final class SplObjectStorageComparatorTest extends TestCase
{
    /**
     * @var SplObjectStorageComparator
     */
    private $comparator;

    protected function setUp(): void
    {
        $this->comparator = new SplObjectStorageComparator;
    }

    public function acceptsFailsProvider()
    {
        return [
            [new SplObjectStorage, new stdClass],
            [new stdClass, new SplObjectStorage],
            [new stdClass, new stdClass]
        ];
    }

    public function assertEqualsSucceedsProvider()
    {
        $object1 = new stdClass();
        $object2 = new stdClass();

        $storage1 = new SplObjectStorage();
        $storage2 = new SplObjectStorage();

        $storage3 = new SplObjectStorage();
        $storage3->attach($object1);
        $storage3->attach($object2);

        $storage4 = new SplObjectStorage();
        $storage4->attach($object2);
        $storage4->attach($object1);

        return [
            [$storage1, $storage1],
            [$storage1, $storage2],
            [$storage3, $storage3],
            [$storage3, $storage4]
        ];
    }

    public function assertEqualsFailsProvider()
    {
        $object1 = new stdClass;
        $object2 = new stdClass;

        $storage1 = new SplObjectStorage;

        $storage2 = new SplObjectStorage;
        $storage2->attach($object1);

        $storage3 = new SplObjectStorage;
        $storage3->attach($object2);
        $storage3->attach($object1);

        return [
            [$storage1, $storage2],
            [$storage1, $storage3],
            [$storage2, $storage3],
        ];
    }

    public function testAcceptsSucceeds(): void
    {
        $this->assertTrue(
          $this->comparator->accepts(
            new SplObjectStorage,
            new SplObjectStorage
          )
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
        $this->expectExceptionMessage('Failed asserting that two objects are equal.');

        $this->comparator->assertEquals($expected, $actual);
    }

    public function testAssertEqualsFails2(): void
    {
        $this->expectException(ComparisonFailure::class);
        $this->expectExceptionMessage('Failed asserting that two objects are equal.');

        $t = new SplObjectStorage();
        $t->attach(new \stdClass());

        $this->comparator->assertEquals($t, new \SplObjectStorage());
    }
}
