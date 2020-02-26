<?php declare(strict_types=1);
/*
 * This file is part of sebastian/type.
 *
 * (c) Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
namespace SebastianBergmann\Type;

use PHPUnit\Framework\TestCase;
use SebastianBergmann\Type\TestFixture\ClassWithCallbackMethods;
use SebastianBergmann\Type\TestFixture\ClassWithInvokeMethod;

/**
 * @covers \SebastianBergmann\Type\CallableType
 *
 * @uses \SebastianBergmann\Type\Type
 * @uses \SebastianBergmann\Type\ObjectType
 * @uses \SebastianBergmann\Type\SimpleType
 * @uses \SebastianBergmann\Type\TypeName
 */
final class CallableTypeTest extends TestCase
{
    /**
     * @var CallableType
     */
    private $type;

    protected function setUp(): void
    {
        $this->type = new CallableType(false);
    }

    public function testMayDisallowNull(): void
    {
        $this->assertFalse($this->type->allowsNull());
    }

    public function testCanGenerateReturnTypeDeclaration(): void
    {
        $this->assertEquals(': callable', $this->type->getReturnTypeDeclaration());
    }

    public function testMayAllowNull(): void
    {
        $type = new CallableType(true);

        $this->assertTrue($type->allowsNull());
    }

    public function testCanGenerateNullableReturnTypeDeclaration(): void
    {
        $type = new CallableType(true);

        $this->assertEquals(': ?callable', $type->getReturnTypeDeclaration());
    }

    public function testNullCanBeAssignedToNullableCallable(): void
    {
        $type = new CallableType(true);

        $this->assertTrue($type->isAssignable(new NullType));
    }

    public function testCallableCanBeAssignedToCallable(): void
    {
        $this->assertTrue($this->type->isAssignable(new CallableType(false)));
    }

    public function testClosureCanBeAssignedToCallable(): void
    {
        $this->assertTrue(
            $this->type->isAssignable(
                new ObjectType(
                    TypeName::fromQualifiedName(\Closure::class),
                    false
                )
            )
        );
    }

    public function testInvokableCanBeAssignedToCallable(): void
    {
        $this->assertTrue(
            $this->type->isAssignable(
                new ObjectType(
                    TypeName::fromQualifiedName(ClassWithInvokeMethod::class),
                    false
                )
            )
        );
    }

    public function testStringWithFunctionNameCanBeAssignedToCallable(): void
    {
        $this->assertTrue(
            $this->type->isAssignable(
                Type::fromValue('SebastianBergmann\Type\TestFixture\callback_function', false)
            )
        );
    }

    public function testStringWithClassNameAndStaticMethodNameCanBeAssignedToCallable(): void
    {
        $this->assertTrue(
            $this->type->isAssignable(
                Type::fromValue(ClassWithCallbackMethods::class . '::staticCallback', false)
            )
        );
    }

    public function testArrayWithClassNameAndStaticMethodNameCanBeAssignedToCallable(): void
    {
        $this->assertTrue(
            $this->type->isAssignable(
                Type::fromValue([ClassWithCallbackMethods::class, 'staticCallback'], false)
            )
        );
    }

    public function testArrayWithClassNameAndInstanceMethodNameCanBeAssignedToCallable(): void
    {
        $this->assertTrue(
            $this->type->isAssignable(
                Type::fromValue([new ClassWithCallbackMethods, 'nonStaticCallback'], false)
            )
        );
    }

    public function testSomethingThatIsNotCallableCannotBeAssignedToCallable(): void
    {
        $this->assertFalse(
            $this->type->isAssignable(
                Type::fromValue(null, false)
            )
        );
    }

    public function testObjectWithoutInvokeMethodCannotBeAssignedToCallable(): void
    {
        $this->assertFalse(
            $this->type->isAssignable(
                Type::fromValue(new class {
                }, false)
            )
        );
    }
}
