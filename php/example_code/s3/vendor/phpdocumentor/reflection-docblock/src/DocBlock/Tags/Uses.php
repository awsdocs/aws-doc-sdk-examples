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
 * Reflection class for a {@}uses tag in a Docblock.
 */
final class Uses extends BaseTag implements Factory\StaticMethod
{
    /** @var string */
    protected $name = 'uses';

    /** @var Fqsen */
    protected $refers;

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
        ?FqsenResolver $resolver = null,
        ?DescriptionFactory $descriptionFactory = null,
        ?TypeContext $context = null
    ) : self {
        Assert::notNull($resolver);
        Assert::notNull($descriptionFactory);

        $parts = preg_split('/\s+/Su', $body, 2);
        Assert::isArray($parts);
        Assert::allString($parts);

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
        return $this->refers . ' ' . (string) $this->description;
    }
}
