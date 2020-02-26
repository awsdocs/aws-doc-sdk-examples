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
use phpDocumentor\Reflection\Types\Context as TypeContext;
use Webmozart\Assert\Assert;
use function preg_split;

/**
 * Reflection class for a @link tag in a Docblock.
 */
final class Link extends BaseTag implements Factory\StaticMethod
{
    /** @var string */
    protected $name = 'link';

    /** @var string */
    private $link;

    /**
     * Initializes a link to a URL.
     */
    public function __construct(string $link, ?Description $description = null)
    {
        $this->link        = $link;
        $this->description = $description;
    }

    /**
     * {@inheritdoc}
     */
    public static function create(
        string $body,
        ?DescriptionFactory $descriptionFactory = null,
        ?TypeContext $context = null
    ) : self {
        Assert::notNull($descriptionFactory);

        $parts = preg_split('/\s+/Su', $body, 2);
        Assert::isArray($parts);
        $description = isset($parts[1]) ? $descriptionFactory->create($parts[1], $context) : null;

        return new static($parts[0], $description);
    }

    /**
     * Gets the link
     */
    public function getLink() : string
    {
        return $this->link;
    }

    /**
     * Returns a string representation for this tag.
     */
    public function __toString() : string
    {
        return $this->link . ($this->description ? ' ' . $this->description->render() : '');
    }
}
