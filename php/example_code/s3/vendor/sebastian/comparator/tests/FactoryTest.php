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
 * @covers \SebastianBergmann\Comparator\Factory
 *
 * @uses \SebastianBergmann\Comparator\Comparator
 * @uses \SebastianBergmann\Comparator\Factory
 * @uses \SebastianBergmann\Comparator\ComparisonFailure
 */
final class FactoryTest extends TestCase
{
    public function instanceProvider()
    {
        $tmpfile = \tmpfile();

        return [
            [null, null, ScalarComparator::class],
            [null, true, ScalarComparator::class],
            [true, null, ScalarComparator::class],
            [true, true, ScalarComparator::class],
            [false, false, ScalarComparator::class],
            [true, false, ScalarComparator::class],
            [false, true, ScalarComparator::class],
            ['', '', ScalarComparator::class],
            ['0', '0', ScalarComparator::class],
            ['0', 0, NumericComparator::class],
            [0, '0', NumericComparator::class],
            [0, 0, NumericComparator::class],
            [1.0, 0, DoubleComparator::class],
            [0, 1.0, DoubleComparator::class],
            [1.0, 1.0, DoubleComparator::class],
            [[1], [1], ArrayComparator::class],
            [$tmpfile, $tmpfile, ResourceComparator::class],
            [new \stdClass, new \stdClass, ObjectComparator::class],
            [new \DateTime, new \DateTime, DateTimeComparator::class],
            [new \SplObjectStorage, new \SplObjectStorage, SplObjectStorageComparator::class],
            [new \Exception, new \Exception, ExceptionComparator::class],
            [new \DOMDocument, new \DOMDocument, DOMNodeComparator::class],
            // mixed types
            [$tmpfile, [1], TypeComparator::class],
            [[1], $tmpfile, TypeComparator::class],
            [$tmpfile, '1', TypeComparator::class],
            ['1', $tmpfile, TypeComparator::class],
            [$tmpfile, new \stdClass, TypeComparator::class],
            [new \stdClass, $tmpfile, TypeComparator::class],
            [new \stdClass, [1], TypeComparator::class],
            [[1], new \stdClass, TypeComparator::class],
            [new \stdClass, '1', TypeComparator::class],
            ['1', new \stdClass, TypeComparator::class],
            [new ClassWithToString, '1', ScalarComparator::class],
            ['1', new ClassWithToString, ScalarComparator::class],
            [1.0, new \stdClass, TypeComparator::class],
            [new \stdClass, 1.0, TypeComparator::class],
            [1.0, [1], TypeComparator::class],
            [[1], 1.0, TypeComparator::class],
        ];
    }

    /**
     * @dataProvider instanceProvider
     */
    public function testGetComparatorFor($a, $b, $expected): void
    {
        $factory = new Factory;
        $actual  = $factory->getComparatorFor($a, $b);
        $this->assertInstanceOf($expected, $actual);
    }

    public function testCustomComparatorCanBeRegistered(): void
    {
        $comparator = new TestClassComparator;

        $factory = new Factory;
        $factory->register($comparator);

        $a        = new TestClass;
        $b        = new TestClass;
        $expected = TestClassComparator::class;
        $actual   = $factory->getComparatorFor($a, $b);

        $factory->unregister($comparator);
        $this->assertInstanceOf($expected, $actual);
    }

    public function testCustomComparatorCanBeUnregistered(): void
    {
        $comparator = new TestClassComparator;

        $factory = new Factory;
        $factory->register($comparator);
        $factory->unregister($comparator);

        $a        = new TestClass;
        $b        = new TestClass;
        $expected = ObjectComparator::class;
        $actual   = $factory->getComparatorFor($a, $b);

        $this->assertInstanceOf($expected, $actual);
    }

    public function testCustomComparatorsCanBeReset(): void
    {
        $comparator = new TestClassComparator;

        $factory = new Factory;
        $factory->register($comparator);
        $factory->reset();

        $a        = new TestClass;
        $b        = new TestClass;
        $expected = ObjectComparator::class;
        $actual   = $factory->getComparatorFor($a, $b);

        $this->assertInstanceOf($expected, $actual);
    }

    public function testIsSingleton(): void
    {
        $f = Factory::getInstance();
        $this->assertSame($f, Factory::getInstance());
    }
}
