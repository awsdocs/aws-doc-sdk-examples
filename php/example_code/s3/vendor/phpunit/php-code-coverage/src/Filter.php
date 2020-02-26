<?php declare(strict_types=1);
/*
 * This file is part of phpunit/php-code-coverage.
 *
 * (c) Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
namespace SebastianBergmann\CodeCoverage;

use SebastianBergmann\FileIterator\Facade as FileIteratorFacade;

/**
 * Filter for whitelisting of code coverage information.
 */
final class Filter
{
    /**
     * Source files that are whitelisted.
     *
     * @var array
     */
    private $whitelistedFiles = [];

    /**
     * Remembers the result of the `is_file()` calls.
     *
     * @var bool[]
     */
    private $isFileCallsCache = [];

    /**
     * Adds a directory to the whitelist (recursively).
     */
    public function addDirectoryToWhitelist(string $directory, string $suffix = '.php', string $prefix = ''): void
    {
        $facade = new FileIteratorFacade;
        $files  = $facade->getFilesAsArray($directory, $suffix, $prefix);

        foreach ($files as $file) {
            $this->addFileToWhitelist($file);
        }
    }

    /**
     * Adds a file to the whitelist.
     */
    public function addFileToWhitelist(string $filename): void
    {
        $filename = \realpath($filename);

        if (!$filename) {
            return;
        }

        $this->whitelistedFiles[$filename] = true;
    }

    /**
     * Adds files to the whitelist.
     *
     * @param string[] $files
     */
    public function addFilesToWhitelist(array $files): void
    {
        foreach ($files as $file) {
            $this->addFileToWhitelist($file);
        }
    }

    /**
     * Removes a directory from the whitelist (recursively).
     */
    public function removeDirectoryFromWhitelist(string $directory, string $suffix = '.php', string $prefix = ''): void
    {
        $facade = new FileIteratorFacade;
        $files  = $facade->getFilesAsArray($directory, $suffix, $prefix);

        foreach ($files as $file) {
            $this->removeFileFromWhitelist($file);
        }
    }

    /**
     * Removes a file from the whitelist.
     */
    public function removeFileFromWhitelist(string $filename): void
    {
        $filename = \realpath($filename);

        if (!$filename || !isset($this->whitelistedFiles[$filename])) {
            return;
        }

        unset($this->whitelistedFiles[$filename]);
    }

    /**
     * Checks whether a filename is a real filename.
     */
    public function isFile(string $filename): bool
    {
        if (isset($this->isFileCallsCache[$filename])) {
            return $this->isFileCallsCache[$filename];
        }

        if ($filename === '-' ||
            \strpos($filename, 'vfs://') === 0 ||
            \strpos($filename, 'xdebug://debug-eval') !== false ||
            \strpos($filename, 'eval()\'d code') !== false ||
            \strpos($filename, 'runtime-created function') !== false ||
            \strpos($filename, 'runkit created function') !== false ||
            \strpos($filename, 'assert code') !== false ||
            \strpos($filename, 'regexp code') !== false ||
            \strpos($filename, 'Standard input code') !== false) {
            $isFile = false;
        } else {
            $isFile = \file_exists($filename);
        }

        $this->isFileCallsCache[$filename] = $isFile;

        return $isFile;
    }

    /**
     * Checks whether or not a file is filtered.
     */
    public function isFiltered(string $filename): bool
    {
        if (!$this->isFile($filename)) {
            return true;
        }

        return !isset($this->whitelistedFiles[$filename]);
    }

    /**
     * Returns the list of whitelisted files.
     *
     * @return string[]
     */
    public function getWhitelist(): array
    {
        return \array_keys($this->whitelistedFiles);
    }

    /**
     * Returns whether this filter has a whitelist.
     */
    public function hasWhitelist(): bool
    {
        return !empty($this->whitelistedFiles);
    }

    /**
     * Returns the whitelisted files.
     *
     * @return string[]
     */
    public function getWhitelistedFiles(): array
    {
        return $this->whitelistedFiles;
    }

    /**
     * Sets the whitelisted files.
     */
    public function setWhitelistedFiles(array $whitelistedFiles): void
    {
        $this->whitelistedFiles = $whitelistedFiles;
    }
}
