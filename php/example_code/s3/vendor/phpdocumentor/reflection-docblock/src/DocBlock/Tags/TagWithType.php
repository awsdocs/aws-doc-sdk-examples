<?php

declare(strict_types=1);

/**
 * This file is part of phpDocumentor.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @link      http://phpdoc.org
 */

namespace phpDocumentor\Reflection\DocBlock\Tags;

use phpDocumentor\Reflection\Type;
use function in_array;
use function strlen;
use function substr;
use function trim;

abstract class TagWithType extends BaseTag
{
    /** @var ?Type */
    protected $type;

    /**
     * Returns the type section of the variable.
     */
    public function getType() : ?Type
    {
        return $this->type;
    }

    /**
     * @return string[]
     */
    protected static function extractTypeFromBody(string $body) : array
    {
        $type         = '';
        $nestingLevel = 0;
        for ($i = 0; $i < strlen($body); $i++) {
            $character = $body[$i];

            if (trim($character) === '' && $nestingLevel === 0) {
                break;
            }

            $type .= $character;
            if (in_array($character, ['<', '(', '[', '{'])) {
                $nestingLevel++;
                continue;
            }

            if (in_array($character, ['>', ')', ']', '}'])) {
                $nestingLevel--;
                continue;
            }
        }

        $description = trim(substr($body, strlen($type)));

        return [$type, $description];
    }
}
