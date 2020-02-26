<?php declare(strict_types=1);
/*
 * This file is part of sebastian/diff.
 *
 * (c) Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

namespace SebastianBergmann\Diff\Utils;

final class FileUtils
{
    public static function getFileContent(string $file): string
    {
        $content = @\file_get_contents($file);

        if (false === $content) {
            $error = \error_get_last();

            throw new \RuntimeException(\sprintf(
                'Failed to read content of file "%s".%s',
                $file,
                $error ? ' ' . $error['message'] : ''
            ));
        }

        return $content;
    }
}
