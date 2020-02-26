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

/**
 * @covers \SebastianBergmann\Type\SimpleType
 *
 * @uses \SebastianBergmann\Type\Type
 */
final class SimpleTypeTest extends TestCase
{
    public function testCanBeBool(): void
    {
        $type = new SimpleType('bool', false);

        $this->assertSame(': bool', $type->getReturnTypeDeclaration());
    }

    public function testCanBeBoolean(): void
    {
        $type = new SimpleType('boolean', false);

        $this->assertSame(': bool', $type->getReturnTypeDeclaration());
    }

    public function testCanBeDouble(): void
    {
        $type = new SimpleType('double', false);

        $this->assertSame(': float', $type->getReturnTypeDeclaration());
    }

    public function testCanBeFloat(): void
    {
        $type = new SimpleType('float', false);

        $this->assertSame(': float', $type->getReturnTypeDeclaration());
    }

    public function testCanBeReal(): void
    {
        $type = new SimpleType('real', false);

        $this->assertSame(': float', $type->getReturnTypeDeclaration());
    }

    public function testCanBeInt(): void
    {
        $type = new SimpleType('int', false);

        $this->assertSame(': int', $type->getReturnTypeDeclaration());
    }

    public function testCanBeInteger(): void
    {
        $type = new SimpleType('integer', false);

        $this->assertSame(': int', $type->getReturnTypeDeclaration());
    }

    public function testCanBeArray(): void
    {
        $type = new SimpleType('array', false);

        $this->assertSame(': array', $type->getReturnTypeDeclaration());
    }

    public function testCanBeArray2(): void
    {
        $type = new SimpleType('[]', false);

        $this->assertSame(': array', $type->getReturnTypeDeclaration());
    }

    public function testMayAllowNull(): void
    {
        $type = new SimpleType('bool', true);

        $this->assertTrue($type->allowsNull());
        $this->assertSame(': ?bool', $type->getReturnTypeDeclaration());
    }

    public function testMayNotAllowNull(): void
    {
        $type = new SimpleType('bool', false);

        $this->assertFalse($type->allowsNull());
    }

    /**
     * @dataProvider assignablePairs
     */
    public function testIsAssignable(Type $assignTo, Type $assignedType): void
    {
        $this->assertTrue($assignTo->isAssignable($assignedType));
    }

    public function assignablePairs(): array
    {
        return [
            'nullable to not nullable'     => [new SimpleType('int', false), new SimpleType('int', true)],
            'not nullable to nullable'     => [new SimpleType('int', true), new SimpleType('int', false)],
            'nullable to nullable'         => [new SimpleType('int', true), new SimpleType('int', true)],
            'not nullable to not nullable' => [new SimpleType('int', false), new SimpleType('int', false)],
            'null to not nullable'         => [new SimpleType('int', true), new NullType],
        ];
    }

    /**
     * @dataProvider notAssignablePairs
     */
    public function testIsNotAssignable(Type $assignTo, Type $assignedType): void
    {
        $this->assertFalse($assignTo->isAssignable($assignedType));
    }

    public function notAssignablePairs(): array
    {
        return [
            'null to not nullable' => [new SimpleType('int', false), new NullType],
            'int to boolean'       => [new SimpleType('boolean', false), new SimpleType('int', false)],
            'object'               => [new SimpleType('boolean', false), new ObjectType(TypeName::fromQualifiedName(\stdClass::class), true)],
            'unknown type'         => [new SimpleType('boolean', false), new UnknownType],
            'void'                 => [new SimpleType('boolean', false), new VoidType],
        ];
    }

    /**
     * @dataProvider returnTypes
     */
    public function testReturnTypeDeclaration(Type $type, string $returnType): void
    {
        $this->assertEquals($type->getReturnTypeDeclaration(), $returnType);
    }

    public function returnTypes(): array
    {
        return [
            '[]'      => [new SimpleType('[]', false), ': array'],
            'array'   => [new SimpleType('array', false), ': array'],
            '?array'  => [new SimpleType('array', true), ': ?array'],
            'boolean' => [new SimpleType('boolean', false), ': bool'],
            'real'    => [new SimpleType('real', false), ': float'],
            'double'  => [new SimpleType('double', false), ': float'],
            'integer' => [new SimpleType('integer', false), ': int'],
        ];
    }

    public function testCanHaveValue(): void
    {
        $this->assertSame('string', Type::fromValue('string', false)->value());
    }
}
