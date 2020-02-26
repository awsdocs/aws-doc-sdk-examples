<?php

declare(strict_types=1);

/**
 * This file is part of phpDocumentor.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @link http://phpdoc.org
 */

namespace phpDocumentor\Reflection\DocBlock\Tags;

use phpDocumentor\Reflection\DocBlock\Description;
use phpDocumentor\Reflection\DocBlock\DescriptionFactory;
use phpDocumentor\Reflection\Fqsen;
use phpDocumentor\Reflection\FqsenResolver;
use phpDocumentor\Reflection\Types\Context as TypeContext;
use Webmozart\Assert\Assert;
use function preg_split;

/**
 * Reflection class for a @covers tag in a Docblock.
 */
final class Covers extends BaseTag implements Factory\StaticMethod
{
    /** @var string */
    protected $name = 'covers';

    /** @var Fqsen */
    private $refers;

    /**
     * Initializes this tag.
     */
    public function __construct(Fqsen $refers, ?Description $description = null)
    {
        $this->refers      = $refers;
        $this->description = $description;
    }

    /**
     * {@inheritdoc}
     */
    public static function create(
        string $body,
        ?DescriptionFactory $descriptionFactory = null,
        ?FqsenResolver $resolver = null,
        ?TypeContext $context = null
    ) : self {
        Assert::notEmpty($body);
        Assert::notNull($descriptionFactory);
        Assert::notNull($resolver);

        $parts = preg_split('/\s+/Su', $body, 2);
        Assert::isArray($parts);

        return new static(
            $resolver->resolve($parts[0], $context),
            $descriptionFactory->create($parts[1] ?? '', $context)
        );
    }

    /**
     * Returns the structural element this tag refers to.
     */
    public function getReference() : Fqsen
    {
        return $this->refers;
    }

    /**
     * Returns a string representation of this tag.
     */
    public function __toString() : string
    {
        return $this->refers . ($this->description ? ' ' . $this->description->render() : '');
    }
}
