<?php declare(strict_types=1);
/*
 * This file is part of php-token-stream.
 *
 * (c) Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

final class PHP_Token_Util
{
    public static function getClass($object): string
    {
        $parts = explode('\\', get_class($object));

        return array_pop($parts);
    }
}