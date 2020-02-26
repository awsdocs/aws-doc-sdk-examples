<?php declare(strict_types=1);
/*
 * This file is part of phpunit/php-code-coverage.
 *
 * (c) Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
namespace SebastianBergmann\CodeCoverage\Report;

use SebastianBergmann\CodeCoverage\CodeCoverage;
use SebastianBergmann\CodeCoverage\RuntimeException;

/**
 * Uses var_export() to write a SebastianBergmann\CodeCoverage\CodeCoverage object to a file.
 */
final class PHP
{
    /**
     * @throws \SebastianBergmann\CodeCoverage\RuntimeException
     */
    public function process(CodeCoverage $coverage, ?string $target = null): string
    {
        $filter = $coverage->filter();

        $buffer = \sprintf(
            '<?php
$coverage = new SebastianBergmann\CodeCoverage\CodeCoverage;
$coverage->setData(%s);
$coverage->setTests(%s);

$filter = $coverage->filter();
$filter->setWhitelistedFiles(%s);

return $coverage;',
            \var_export($coverage->getData(true), true),
            \var_export($coverage->getTests(), true),
            \var_export($filter->getWhitelistedFiles(), true)
        );

        if ($target !== null) {
            if (!$this->createDirectory(\dirname($target))) {
                throw new \RuntimeException(\sprintf('Directory "%s" was not created', \dirname($target)));
            }

            if (@\file_put_contents($target, $buffer) === false) {
                throw new RuntimeException(
                    \sprintf(
                        'Could not write to "%s',
                        $target
                    )
                );
            }
        }

        return $buffer;
    }

    private function createDirectory(string $directory): bool
    {
        return !(!\is_dir($directory) && !@\mkdir($directory, 0777, true) && !\is_dir($directory));
    }
}
