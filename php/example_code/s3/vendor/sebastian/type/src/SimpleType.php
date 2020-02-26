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

final class SimpleType extends Type
{
    /**
     * @var string
     */
    private $name;

    /**
     * @var bool
     */
    private $allowsNull;

    /**
     * @var mixed
     */
    private $value;

    public function __construct(string $name, bool $nullable, $value = null)
    {
        $this->name       = $this->normalize($name);
        $this->allowsNull = $nullable;
        $this->value      = $value;
    }

    public function isAssignable(Type $other): bool
    {
        if ($this->allowsNull && $other instanceof NullType) {
            return true;
        }

        if ($other instanceof self) {
            return $this->name === $other->name;
        }

        return false;
    }

    public function getReturnTypeDeclaration(): string
    {
        return ': ' . ($this->allowsNull ? '?' : '') . $this->name;
    }

    public function allowsNull(): bool
    {
        return $this->allowsNull;
    }

    public function value()
    {
        return $this->value;
    }

    private function normalize(string $name): string
    {
        $name = \strtolower($name);

        switch ($name) {
            case 'boolean':
                return 'bool';

            case 'real':
            case 'double':
                return 'float';

            case 'integer':
                return 'int';

            case '[]':
                return 'array';

            default:
                return $name;
        }
    }
}
