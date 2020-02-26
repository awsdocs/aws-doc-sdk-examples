<?php declare(strict_types=1);
/*
 * This file is part of phpunit/php-code-coverage.
 *
 * (c) Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
namespace SebastianBergmann\CodeCoverage\Driver;

use SebastianBergmann\CodeCoverage\Filter;
use SebastianBergmann\CodeCoverage\RuntimeException;

/**
 * Driver for Xdebug's code coverage functionality.
 *
 * @codeCoverageIgnore
 */
final class Xdebug implements Driver
{
    /**
     * @var array
     */
    private $cacheNumLines = [];

    /**
     * @var Filter
     */
    private $filter;

    /**
     * @throws RuntimeException
     */
    public function __construct(Filter $filter = null)
    {
        if (!\extension_loaded('xdebug')) {
            throw new RuntimeException('This driver requires Xdebug');
        }

        if (!\ini_get('xdebug.coverage_enable')) {
            throw new RuntimeException('xdebug.coverage_enable=On has to be set in php.ini');
        }

        if ($filter === null) {
            $filter = new Filter;
        }

        $this->filter = $filter;
    }

    /**
     * Start collection of code coverage information.
     */
    public function start(bool $determineUnusedAndDead = true): void
    {
        if ($determineUnusedAndDead) {
            \xdebug_start_code_coverage(XDEBUG_CC_UNUSED | XDEBUG_CC_DEAD_CODE);
        } else {
            \xdebug_start_code_coverage();
        }
    }

    /**
     * Stop collection of code coverage information.
     */
    public function stop(): array
    {
        $data = \xdebug_get_code_coverage();

        \xdebug_stop_code_coverage();

        return $this->cleanup($data);
    }

    private function cleanup(array $data): array
    {
        foreach (\array_keys($data) as $file) {
            unset($data[$file][0]);

            if (!$this->filter->isFile($file)) {
                continue;
            }

            $numLines = $this->getNumberOfLinesInFile($file);

            foreach (\array_keys($data[$file]) as $line) {
                if ($line > $numLines) {
                    unset($data[$file][$line]);
                }
            }
        }

        return $data;
    }

    private function getNumberOfLinesInFile(string $fileName): int
    {
        if (!isset($this->cacheNumLines[$fileName])) {
            $buffer = \file_get_contents($fileName);
            $lines  = \substr_count($buffer, "\n");

            if (\substr($buffer, -1) !== "\n") {
                $lines++;
            }

            $this->cacheNumLines[$fileName] = $lines;
        }

        return $this->cacheNumLines[$fileName];
    }
}
