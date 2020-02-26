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
 * @covers \SebastianBergmann\Comparator\MockObjectComparator<extended>
 *
 * @uses \SebastianBergmann\Comparator\Comparator
 * @uses \SebastianBergmann\Comparator\Factory
 * @uses \SebastianBergmann\Comparator\ComparisonFailure
 */
final class MockObjectComparatorTest extends TestCase
{
    /**
     * @var MockObjectComparator
     */
    private $comparator;

    protected function setUp(): void
    {
        $this->comparator = new MockObjectComparator;
        $this->comparator->setFactory(new Factory);
    }

    public function acceptsSucceedsProvider()
    {
        $testmock = $this->createMock(TestClass::class);
        $stdmock  = $this->createMock(stdClass::class);

        return [
            [$testmock, $testmock],
            [$stdmock, $stdmock],
            [$stdmock, $testmock]
        ];
    }

    public function acceptsFailsProvider()
    {
        $stdmock = $this->createMock(stdClass::class);

        return [
            [$stdmock, null],
            [null, $stdmock],
            [null, null]
        ];
    }

    public function assertEqualsSucceedsProvider()
    {
        // cyclic dependencies
        $book1                  = $this->getMockBuilder(Book::class)->setMethods(null)->getMock();
        $book1->author          = $this->getMockBuilder(Author::class)->setMethods(null)->setConstructorArgs(['Terry Pratchett'])->getMock();
        $book1->author->books[] = $book1;
        $book2                  = $this->getMockBuilder(Book::class)->setMethods(null)->getMock();
        $book2->author          = $this->getMockBuilder(Author::class)->setMethods(null)->setConstructorArgs(['Terry Pratchett'])->getMock();
        $book2->author->books[] = $book2;

        $object1 = $this->getMockBuilder(SampleClass::class)->setMethods(null)->setConstructorArgs([4, 8, 15])->getMock();
        $object2 = $this->getMockBuilder(SampleClass::class)->setMethods(null)->setConstructorArgs([4, 8, 15])->getMock();

        return [
            [$object1, $object1],
            [$object1, $object2],
            [$book1, $book1],
            [$book1, $book2],
            [
                $this->getMockBuilder(Struct::class)->setMethods(null)->setConstructorArgs([2.3])->getMock(),
                $this->getMockBuilder(Struct::class)->setMethods(null)->setConstructorArgs([2.5])->getMock(),
                0.5
            ]
        ];
    }

    public function assertEqualsFailsProvider()
    {
        $typeMessage  = 'is not instance of expected class';
        $equalMessage = 'Failed asserting that two objects are equal.';

        // cyclic dependencies
        $book1                  = $this->getMockBuilder(Book::class)->setMethods(null)->getMock();
        $book1->author          = $this->getMockBuilder(Author::class)->setMethods(null)->setConstructorArgs(['Terry Pratchett'])->getMock();
        $book1->author->books[] = $book1;
        $book2                  = $this->getMockBuilder(Book::class)->setMethods(null)->getMock();
        $book1->author          = $this->getMockBuilder(Author::class)->setMethods(null)->setConstructorArgs(['Terry Pratch'])->getMock();
        $book2->author->books[] = $book2;

        $book3         = $this->getMockBuilder(Book::class)->setMethods(null)->getMock();
        $book3->author = 'Terry Pratchett';
        $book4         = $this->createMock(stdClass::class);
        $book4->author = 'Terry Pratchett';

        $object1 = $this->getMockBuilder(SampleClass::class)->setMethods(null)->setConstructorArgs([4, 8, 15])->getMock();
        $object2 = $this->getMockBuilder(SampleClass::class)->setMethods(null)->setConstructorArgs([16, 23, 42])->getMock();

        return [
            [
                $this->getMockBuilder(SampleClass::class)->setMethods(null)->setConstructorArgs([4, 8, 15])->getMock(),
                $this->getMockBuilder(SampleClass::class)->setMethods(null)->setConstructorArgs([16, 23, 42])->getMock(),
                $equalMessage
            ],
            [$object1, $object2, $equalMessage],
            [$book1, $book2, $equalMessage],
            [$book3, $book4, $typeMessage],
            [
                $this->getMockBuilder(Struct::class)->setMethods(null)->setConstructorArgs([2.3])->getMock(),
                $this->getMockBuilder(Struct::class)->setMethods(null)->setConstructorArgs([4.2])->getMock(),
                $equalMessage,
                0.5
            ]
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
    public function testAssertEqualsFails($expected, $actual, $message, $delta = 0.0): void
    {
        $this->expectException(ComparisonFailure::class);
        $this->expectExceptionMessage($message);

        $this->comparator->assertEquals($expected, $actual, $delta);
    }
}
