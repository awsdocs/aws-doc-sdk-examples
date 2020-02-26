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

final class IterableType extends Type
{
    /**
     * @var bool
     */
    private $allowsNull;

    public function __construct(bool $nullable)
    {
        $this->allowsNull = $nullable;
    }

    /**
     * @throws RuntimeException
     */
    public function isAssignable(Type $other): bool
    {
        if ($this->allowsNull && $other instanceof NullType) {
            return true;
        }

        if ($other instanceof self) {
            return true;
        }

        if ($other instanceof SimpleType) {
            return \is_iterable($other->value());
        }

        if ($other instanceof ObjectType) {
            $className = $other->className()->getQualifiedName();
            \assert(\class_exists($className));

            try {
                return (new \ReflectionClass($className))->isIterable();
                // @codeCoverageIgnoreStart
            } catch (\ReflectionException $e) {
                throw new RuntimeException(
                    $e->getMessage(),
                    (int) $e->getCode(),
                    $e
                );
                // @codeCoverageIgnoreEnd
            }
        }

        return false;
    }

    public function getReturnTypeDeclaration(): string
    {
        return ': ' . ($this->allowsNull ? '?' : '') . 'iterable';
    }

    public function allowsNull(): bool
    {
        return $this->allowsNull;
    }
}
