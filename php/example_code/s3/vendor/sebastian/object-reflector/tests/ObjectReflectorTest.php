<?php
/*
 * This file is part of object-reflector.
 *
 * (c) Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

declare(strict_types=1);

namespace SebastianBergmann\ObjectReflector;

use PHPUnit\Framework\TestCase;
use SebastianBergmann\ObjectReflector\TestFixture\ChildClass;
use SebastianBergmann\ObjectReflector\TestFixture\ClassWithIntegerAttributeName;

/**
 * @covers SebastianBergmann\ObjectReflector\ObjectReflector
 */
class ObjectReflectorTest extends TestCase
{
    /**
     * @var ObjectReflector
     */
    private $objectReflector;

    protected function setUp(): void
    {
        $this->objectReflector = new ObjectReflector;
    }

    public function testReflectsAttributesOfObject(): void
    {
        $o = new ChildClass;

        $this->assertEquals(
            [
                'privateInChild' => 'private',
                'protectedInChild' => 'protected',
                'publicInChild' => 'public',
                'undeclared' => 'undeclared',
                'SebastianBergmann\ObjectReflector\TestFixture\ParentClass::privateInParent' => 'private',
                'SebastianBergmann\ObjectReflector\TestFixture\ParentClass::protectedInParent' => 'protected',
                'SebastianBergmann\ObjectReflector\TestFixture\ParentClass::publicInParent' => 'public',
            ],
            $this->objectReflector->getAttributes($o)
        );
    }

    public function testReflectsAttributeWithIntegerName(): void
    {
        $o = new ClassWithIntegerAttributeName;

        $this->assertEquals(
            [
                1 => 2
            ],
            $this->objectReflector->getAttributes($o)
        );
    }

    public function testRaisesExceptionWhenPassedArgumentIsNotAnObject(): void
    {
        $this->expectException(InvalidArgumentException::class);

        $this->objectReflector->getAttributes(null);
    }
}
