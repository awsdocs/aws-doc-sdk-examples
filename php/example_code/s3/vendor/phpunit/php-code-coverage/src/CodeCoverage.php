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

use PHPUnit\Framework\TestCase;
use PHPUnit\Runner\PhptTestCase;
use PHPUnit\Util\Test;
use SebastianBergmann\CodeCoverage\Driver\Driver;
use SebastianBergmann\CodeCoverage\Driver\PCOV;
use SebastianBergmann\CodeCoverage\Driver\PHPDBG;
use SebastianBergmann\CodeCoverage\Driver\Xdebug;
use SebastianBergmann\CodeCoverage\Node\Builder;
use SebastianBergmann\CodeCoverage\Node\Directory;
use SebastianBergmann\CodeUnitReverseLookup\Wizard;
use SebastianBergmann\Environment\Runtime;

/**
 * Provides collection functionality for PHP code coverage information.
 */
final class CodeCoverage
{
    /**
     * @var Driver
     */
    private $driver;

    /**
     * @var Filter
     */
    private $filter;

    /**
     * @var Wizard
     */
    private $wizard;

    /**
     * @var bool
     */
    private $cacheTokens = false;

    /**
     * @var bool
     */
    private $checkForUnintentionallyCoveredCode = false;

    /**
     * @var bool
     */
    private $forceCoversAnnotation = false;

    /**
     * @var bool
     */
    private $checkForUnexecutedCoveredCode = false;

    /**
     * @var bool
     */
    private $checkForMissingCoversAnnotation = false;

    /**
     * @var bool
     */
    private $addUncoveredFilesFromWhitelist = true;

    /**
     * @var bool
     */
    private $processUncoveredFilesFromWhitelist = false;

    /**
     * @var bool
     */
    private $ignoreDeprecatedCode = false;

    /**
     * @var PhptTestCase|string|TestCase
     */
    private $currentId;

    /**
     * Code coverage data.
     *
     * @var array
     */
    private $data = [];

    /**
     * @var array
     */
    private $ignoredLines = [];

    /**
     * @var bool
     */
    private $disableIgnoredLines = false;

    /**
     * Test data.
     *
     * @var array
     */
    private $tests = [];

    /**
     * @var string[]
     */
    private $unintentionallyCoveredSubclassesWhitelist = [];

    /**
     * Determine if the data has been initialized or not
     *
     * @var bool
     */
    private $isInitialized = false;

    /**
     * Determine whether we need to check for dead and unused code on each test
     *
     * @var bool
     */
    private $shouldCheckForDeadAndUnused = true;

    /**
     * @var Directory
     */
    private $report;

    /**
     * @throws RuntimeException
     */
    public function __construct(Driver $driver = null, Filter $filter = null)
    {
        if ($filter === null) {
            $filter = new Filter;
        }

        if ($driver === null) {
            $driver = $this->selectDriver($filter);
        }

        $this->driver = $driver;
        $this->filter = $filter;

        $this->wizard = new Wizard;
    }

    /**
     * Returns the code coverage information as a graph of node objects.
     */
    public function getReport(): Directory
    {
        if ($this->report === null) {
            $this->report = (new Builder)->build($this);
        }

        return $this->report;
    }

    /**
     * Clears collected code coverage data.
     */
    public function clear(): void
    {
        $this->isInitialized = false;
        $this->currentId     = null;
        $this->data          = [];
        $this->tests         = [];
        $this->report        = null;
    }

    /**
     * Returns the filter object used.
     */
    public function filter(): Filter
    {
        return $this->filter;
    }

    /**
     * Returns the collected code coverage data.
     */
    public function getData(bool $raw = false): array
    {
        if (!$raw && $this->addUncoveredFilesFromWhitelist) {
            $this->addUncoveredFilesFromWhitelist();
        }

        return $this->data;
    }

    /**
     * Sets the coverage data.
     */
    public function setData(array $data): void
    {
        $this->data   = $data;
        $this->report = null;
    }

    /**
     * Returns the test data.
     */
    public function getTests(): array
    {
        return $this->tests;
    }

    /**
     * Sets the test data.
     */
    public function setTests(array $tests): void
    {
        $this->tests = $tests;
    }

    /**
     * Start collection of code coverage information.
     *
     * @param PhptTestCase|string|TestCase $id
     *
     * @throws RuntimeException
     */
    public function start($id, bool $clear = false): void
    {
        if ($clear) {
            $this->clear();
        }

        if ($this->isInitialized === false) {
            $this->initializeData();
        }

        $this->currentId = $id;

        $this->driver->start($this->shouldCheckForDeadAndUnused);
    }

    /**
     * Stop collection of code coverage information.
     *
     * @param array|false $linesToBeCovered
     *
     * @throws MissingCoversAnnotationException
     * @throws CoveredCodeNotExecutedException
     * @throws RuntimeException
     * @throws InvalidArgumentException
     * @throws \ReflectionException
     */
    public function stop(bool $append = true, $linesToBeCovered = [], array $linesToBeUsed = [], bool $ignoreForceCoversAnnotation = false): array
    {
        if (!\is_array($linesToBeCovered) && $linesToBeCovered !== false) {
            throw InvalidArgumentException::create(
                2,
                'array or false'
            );
        }

        $data = $this->driver->stop();
        $this->append($data, null, $append, $linesToBeCovered, $linesToBeUsed, $ignoreForceCoversAnnotation);

        $this->currentId = null;

        return $data;
    }

    /**
     * Appends code coverage data.
     *
     * @param PhptTestCase|string|TestCase $id
     * @param array|false                  $linesToBeCovered
     *
     * @throws \SebastianBergmann\CodeCoverage\UnintentionallyCoveredCodeException
     * @throws \SebastianBergmann\CodeCoverage\MissingCoversAnnotationException
     * @throws \SebastianBergmann\CodeCoverage\CoveredCodeNotExecutedException
     * @throws \ReflectionException
     * @throws \SebastianBergmann\CodeCoverage\InvalidArgumentException
     * @throws RuntimeException
     */
    public function append(array $data, $id = null, bool $append = true, $linesToBeCovered = [], array $linesToBeUsed = [], bool $ignoreForceCoversAnnotation = false): void
    {
        if ($id === null) {
            $id = $this->currentId;
        }

        if ($id === null) {
            throw new RuntimeException;
        }

        $this->applyWhitelistFilter($data);
        $this->applyIgnoredLinesFilter($data);
        $this->initializeFilesThatAreSeenTheFirstTime($data);

        if (!$append) {
            return;
        }

        if ($id !== 'UNCOVERED_FILES_FROM_WHITELIST') {
            $this->applyCoversAnnotationFilter(
                $data,
                $linesToBeCovered,
                $linesToBeUsed,
                $ignoreForceCoversAnnotation
            );
        }

        if (empty($data)) {
            return;
        }

        $size   = 'unknown';
        $status = -1;

        if ($id instanceof TestCase) {
            $_size = $id->getSize();

            if ($_size === Test::SMALL) {
                $size = 'small';
            } elseif ($_size === Test::MEDIUM) {
                $size = 'medium';
            } elseif ($_size === Test::LARGE) {
                $size = 'large';
            }

            $status = $id->getStatus();
            $id     = \get_class($id) . '::' . $id->getName();
        } elseif ($id instanceof PhptTestCase) {
            $size = 'large';
            $id   = $id->getName();
        }

        $this->tests[$id] = ['size' => $size, 'status' => $status];

        foreach ($data as $file => $lines) {
            if (!$this->filter->isFile($file)) {
                continue;
            }

            foreach ($lines as $k => $v) {
                if ($v === Driver::LINE_EXECUTED) {
                    if (empty($this->data[$file][$k]) || !\in_array($id, $this->data[$file][$k])) {
                        $this->data[$file][$k][] = $id;
                    }
                }
            }
        }

        $this->report = null;
    }

    /**
     * Merges the data from another instance.
     *
     * @param CodeCoverage $that
     */
    public function merge(self $that): void
    {
        $this->filter->setWhitelistedFiles(
            \array_merge($this->filter->getWhitelistedFiles(), $that->filter()->getWhitelistedFiles())
        );

        foreach ($that->data as $file => $lines) {
            if (!isset($this->data[$file])) {
                if (!$this->filter->isFiltered($file)) {
                    $this->data[$file] = $lines;
                }

                continue;
            }

            // we should compare the lines if any of two contains data
            $compareLineNumbers = \array_unique(
                \array_merge(
                    \array_keys($this->data[$file]),
                    \array_keys($that->data[$file])
                )
            );

            foreach ($compareLineNumbers as $line) {
                $thatPriority = $this->getLinePriority($that->data[$file], $line);
                $thisPriority = $this->getLinePriority($this->data[$file], $line);

                if ($thatPriority > $thisPriority) {
                    $this->data[$file][$line] = $that->data[$file][$line];
                } elseif ($thatPriority === $thisPriority && \is_array($this->data[$file][$line])) {
                    $this->data[$file][$line] = \array_unique(
                        \array_merge($this->data[$file][$line], $that->data[$file][$line])
                    );
                }
            }
        }

        $this->tests  = \array_merge($this->tests, $that->getTests());
        $this->report = null;
    }

    public function setCacheTokens(bool $flag): void
    {
        $this->cacheTokens = $flag;
    }

    public function getCacheTokens(): bool
    {
        return $this->cacheTokens;
    }

    public function setCheckForUnintentionallyCoveredCode(bool $flag): void
    {
        $this->checkForUnintentionallyCoveredCode = $flag;
    }

    public function setForceCoversAnnotation(bool $flag): void
    {
        $this->forceCoversAnnotation = $flag;
    }

    public function setCheckForMissingCoversAnnotation(bool $flag): void
    {
        $this->checkForMissingCoversAnnotation = $flag;
    }

    public function setCheckForUnexecutedCoveredCode(bool $flag): void
    {
        $this->checkForUnexecutedCoveredCode = $flag;
    }

    public function setAddUncoveredFilesFromWhitelist(bool $flag): void
    {
        $this->addUncoveredFilesFromWhitelist = $flag;
    }

    public function setProcessUncoveredFilesFromWhitelist(bool $flag): void
    {
        $this->processUncoveredFilesFromWhitelist = $flag;
    }

    public function setDisableIgnoredLines(bool $flag): void
    {
        $this->disableIgnoredLines = $flag;
    }

    public function setIgnoreDeprecatedCode(bool $flag): void
    {
        $this->ignoreDeprecatedCode = $flag;
    }

    public function setUnintentionallyCoveredSubclassesWhitelist(array $whitelist): void
    {
        $this->unintentionallyCoveredSubclassesWhitelist = $whitelist;
    }

    /**
     * Determine the priority for a line
     *
     * 1 = the line is not set
     * 2 = the line has not been tested
     * 3 = the line is dead code
     * 4 = the line has been tested
     *
     * During a merge, a higher number is better.
     *
     * @param array $data
     * @param int   $line
     *
     * @return int
     */
    private function getLinePriority($data, $line)
    {
        if (!\array_key_exists($line, $data)) {
            return 1;
        }

        if (\is_array($data[$line]) && \count($data[$line]) === 0) {
            return 2;
        }

        if ($data[$line] === null) {
            return 3;
        }

        return 4;
    }

    /**
     * Applies the @covers annotation filtering.
     *
     * @param array|false $linesToBeCovered
     *
     * @throws \SebastianBergmann\CodeCoverage\CoveredCodeNotExecutedException
     * @throws \ReflectionException
     * @throws MissingCoversAnnotationException
     * @throws UnintentionallyCoveredCodeException
     */
    private function applyCoversAnnotationFilter(array &$data, $linesToBeCovered, array $linesToBeUsed, bool $ignoreForceCoversAnnotation): void
    {
        if ($linesToBeCovered === false ||
            ($this->forceCoversAnnotation && empty($linesToBeCovered) && !$ignoreForceCoversAnnotation)) {
            if ($this->checkForMissingCoversAnnotation) {
                throw new MissingCoversAnnotationException;
            }

            $data = [];

            return;
        }

        if (empty($linesToBeCovered)) {
            return;
        }

        if ($this->checkForUnintentionallyCoveredCode &&
            (!$this->currentId instanceof TestCase ||
            (!$this->currentId->isMedium() && !$this->currentId->isLarge()))) {
            $this->performUnintentionallyCoveredCodeCheck($data, $linesToBeCovered, $linesToBeUsed);
        }

        if ($this->checkForUnexecutedCoveredCode) {
            $this->performUnexecutedCoveredCodeCheck($data, $linesToBeCovered, $linesToBeUsed);
        }

        $data = \array_intersect_key($data, $linesToBeCovered);

        foreach (\array_keys($data) as $filename) {
            $_linesToBeCovered = \array_flip($linesToBeCovered[$filename]);
            $data[$filename]   = \array_intersect_key($data[$filename], $_linesToBeCovered);
        }
    }

    private function applyWhitelistFilter(array &$data): void
    {
        foreach (\array_keys($data) as $filename) {
            if ($this->filter->isFiltered($filename)) {
                unset($data[$filename]);
            }
        }
    }

    /**
     * @throws \SebastianBergmann\CodeCoverage\InvalidArgumentException
     */
    private function applyIgnoredLinesFilter(array &$data): void
    {
        foreach (\array_keys($data) as $filename) {
            if (!$this->filter->isFile($filename)) {
                continue;
            }

            foreach ($this->getLinesToBeIgnored($filename) as $line) {
                unset($data[$filename][$line]);
            }
        }
    }

    private function initializeFilesThatAreSeenTheFirstTime(array $data): void
    {
        foreach ($data as $file => $lines) {
            if (!isset($this->data[$file]) && $this->filter->isFile($file)) {
                $this->data[$file] = [];

                foreach ($lines as $k => $v) {
                    $this->data[$file][$k] = $v === -2 ? null : [];
                }
            }
        }
    }

    /**
     * @throws CoveredCodeNotExecutedException
     * @throws InvalidArgumentException
     * @throws MissingCoversAnnotationException
     * @throws RuntimeException
     * @throws UnintentionallyCoveredCodeException
     * @throws \ReflectionException
     */
    private function addUncoveredFilesFromWhitelist(): void
    {
        $data           = [];
        $uncoveredFiles = \array_diff(
            $this->filter->getWhitelist(),
            \array_keys($this->data)
        );

        foreach ($uncoveredFiles as $uncoveredFile) {
            if (!\file_exists($uncoveredFile)) {
                continue;
            }

            $data[$uncoveredFile] = [];

            $lines = \count(\file($uncoveredFile));

            for ($i = 1; $i <= $lines; $i++) {
                $data[$uncoveredFile][$i] = Driver::LINE_NOT_EXECUTED;
            }
        }

        $this->append($data, 'UNCOVERED_FILES_FROM_WHITELIST');
    }

    private function getLinesToBeIgnored(string $fileName): array
    {
        if (isset($this->ignoredLines[$fileName])) {
            return $this->ignoredLines[$fileName];
        }

        try {
            return $this->getLinesToBeIgnoredInner($fileName);
        } catch (\OutOfBoundsException $e) {
            // This can happen with PHP_Token_Stream if the file is syntactically invalid,
            // and probably affects a file that wasn't executed.
            return [];
        }
    }

    private function getLinesToBeIgnoredInner(string $fileName): array
    {
        $this->ignoredLines[$fileName] = [];

        $lines = \file($fileName);

        foreach ($lines as $index => $line) {
            if (!\trim($line)) {
                $this->ignoredLines[$fileName][] = $index + 1;
            }
        }

        if ($this->cacheTokens) {
            $tokens = \PHP_Token_Stream_CachingFactory::get($fileName);
        } else {
            $tokens = new \PHP_Token_Stream($fileName);
        }

        foreach ($tokens->getInterfaces() as $interface) {
            $interfaceStartLine = $interface['startLine'];
            $interfaceEndLine   = $interface['endLine'];

            foreach (\range($interfaceStartLine, $interfaceEndLine) as $line) {
                $this->ignoredLines[$fileName][] = $line;
            }
        }

        foreach (\array_merge($tokens->getClasses(), $tokens->getTraits()) as $classOrTrait) {
            $classOrTraitStartLine = $classOrTrait['startLine'];
            $classOrTraitEndLine   = $classOrTrait['endLine'];

            if (empty($classOrTrait['methods'])) {
                foreach (\range($classOrTraitStartLine, $classOrTraitEndLine) as $line) {
                    $this->ignoredLines[$fileName][] = $line;
                }

                continue;
            }

            $firstMethod          = \array_shift($classOrTrait['methods']);
            $firstMethodStartLine = $firstMethod['startLine'];
            $lastMethodEndLine    = $firstMethod['endLine'];

            do {
                $lastMethod = \array_pop($classOrTrait['methods']);
            } while ($lastMethod !== null && 0 === \strpos($lastMethod['signature'], 'anonymousFunction'));

            if ($lastMethod !== null) {
                $lastMethodEndLine = $lastMethod['endLine'];
            }

            foreach (\range($classOrTraitStartLine, $firstMethodStartLine) as $line) {
                $this->ignoredLines[$fileName][] = $line;
            }

            foreach (\range($lastMethodEndLine + 1, $classOrTraitEndLine) as $line) {
                $this->ignoredLines[$fileName][] = $line;
            }
        }

        if ($this->disableIgnoredLines) {
            $this->ignoredLines[$fileName] = \array_unique($this->ignoredLines[$fileName]);
            \sort($this->ignoredLines[$fileName]);

            return $this->ignoredLines[$fileName];
        }

        $ignore = false;
        $stop   = false;

        foreach ($tokens->tokens() as $token) {
            switch (\get_class($token)) {
                case \PHP_Token_COMMENT::class:
                case \PHP_Token_DOC_COMMENT::class:
                    $_token = \trim((string) $token);
                    $_line  = \trim($lines[$token->getLine() - 1]);

                    if ($_token === '// @codeCoverageIgnore' ||
                        $_token === '//@codeCoverageIgnore') {
                        $ignore = true;
                        $stop   = true;
                    } elseif ($_token === '// @codeCoverageIgnoreStart' ||
                        $_token === '//@codeCoverageIgnoreStart') {
                        $ignore = true;
                    } elseif ($_token === '// @codeCoverageIgnoreEnd' ||
                        $_token === '//@codeCoverageIgnoreEnd') {
                        $stop = true;
                    }

                    if (!$ignore) {
                        $start = $token->getLine();
                        $end   = $start + \substr_count((string) $token, "\n");

                        // Do not ignore the first line when there is a token
                        // before the comment
                        if (0 !== \strpos($_token, $_line)) {
                            $start++;
                        }

                        for ($i = $start; $i < $end; $i++) {
                            $this->ignoredLines[$fileName][] = $i;
                        }

                        // A DOC_COMMENT token or a COMMENT token starting with "/*"
                        // does not contain the final \n character in its text
                        if (isset($lines[$i - 1]) && 0 === \strpos($_token, '/*') && '*/' === \substr(\trim($lines[$i - 1]), -2)) {
                            $this->ignoredLines[$fileName][] = $i;
                        }
                    }

                    break;

                case \PHP_Token_INTERFACE::class:
                case \PHP_Token_TRAIT::class:
                case \PHP_Token_CLASS::class:
                case \PHP_Token_FUNCTION::class:
                    /* @var \PHP_Token_Interface $token */

                    $docblock = (string) $token->getDocblock();

                    $this->ignoredLines[$fileName][] = $token->getLine();

                    if (\strpos($docblock, '@codeCoverageIgnore') || ($this->ignoreDeprecatedCode && \strpos($docblock, '@deprecated'))) {
                        $endLine = $token->getEndLine();

                        for ($i = $token->getLine(); $i <= $endLine; $i++) {
                            $this->ignoredLines[$fileName][] = $i;
                        }
                    }

                    break;

                /* @noinspection PhpMissingBreakStatementInspection */
                case \PHP_Token_NAMESPACE::class:
                    $this->ignoredLines[$fileName][] = $token->getEndLine();

                // Intentional fallthrough
                case \PHP_Token_DECLARE::class:
                case \PHP_Token_OPEN_TAG::class:
                case \PHP_Token_CLOSE_TAG::class:
                case \PHP_Token_USE::class:
                case \PHP_Token_USE_FUNCTION::class:
                    $this->ignoredLines[$fileName][] = $token->getLine();

                    break;
            }

            if ($ignore) {
                $this->ignoredLines[$fileName][] = $token->getLine();

                if ($stop) {
                    $ignore = false;
                    $stop   = false;
                }
            }
        }

        $this->ignoredLines[$fileName][] = \count($lines) + 1;

        $this->ignoredLines[$fileName] = \array_unique(
            $this->ignoredLines[$fileName]
        );

        $this->ignoredLines[$fileName] = \array_unique($this->ignoredLines[$fileName]);
        \sort($this->ignoredLines[$fileName]);

        return $this->ignoredLines[$fileName];
    }

    /**
     * @throws \ReflectionException
     * @throws UnintentionallyCoveredCodeException
     */
    private function performUnintentionallyCoveredCodeCheck(array &$data, array $linesToBeCovered, array $linesToBeUsed): void
    {
        $allowedLines = $this->getAllowedLines(
            $linesToBeCovered,
            $linesToBeUsed
        );

        $unintentionallyCoveredUnits = [];

        foreach ($data as $file => $_data) {
            foreach ($_data as $line => $flag) {
                if ($flag === 1 && !isset($allowedLines[$file][$line])) {
                    $unintentionallyCoveredUnits[] = $this->wizard->lookup($file, $line);
                }
            }
        }

        $unintentionallyCoveredUnits = $this->processUnintentionallyCoveredUnits($unintentionallyCoveredUnits);

        if (!empty($unintentionallyCoveredUnits)) {
            throw new UnintentionallyCoveredCodeException(
                $unintentionallyCoveredUnits
            );
        }
    }

    /**
     * @throws CoveredCodeNotExecutedException
     */
    private function performUnexecutedCoveredCodeCheck(array &$data, array $linesToBeCovered, array $linesToBeUsed): void
    {
        $executedCodeUnits = $this->coverageToCodeUnits($data);
        $message           = '';

        foreach ($this->linesToCodeUnits($linesToBeCovered) as $codeUnit) {
            if (!\in_array($codeUnit, $executedCodeUnits)) {
                $message .= \sprintf(
                    '- %s is expected to be executed (@covers) but was not executed' . "\n",
                    $codeUnit
                );
            }
        }

        foreach ($this->linesToCodeUnits($linesToBeUsed) as $codeUnit) {
            if (!\in_array($codeUnit, $executedCodeUnits)) {
                $message .= \sprintf(
                    '- %s is expected to be executed (@uses) but was not executed' . "\n",
                    $codeUnit
                );
            }
        }

        if (!empty($message)) {
            throw new CoveredCodeNotExecutedException($message);
        }
    }

    private function getAllowedLines(array $linesToBeCovered, array $linesToBeUsed): array
    {
        $allowedLines = [];

        foreach (\array_keys($linesToBeCovered) as $file) {
            if (!isset($allowedLines[$file])) {
                $allowedLines[$file] = [];
            }

            $allowedLines[$file] = \array_merge(
                $allowedLines[$file],
                $linesToBeCovered[$file]
            );
        }

        foreach (\array_keys($linesToBeUsed) as $file) {
            if (!isset($allowedLines[$file])) {
                $allowedLines[$file] = [];
            }

            $allowedLines[$file] = \array_merge(
                $allowedLines[$file],
                $linesToBeUsed[$file]
            );
        }

        foreach (\array_keys($allowedLines) as $file) {
            $allowedLines[$file] = \array_flip(
                \array_unique($allowedLines[$file])
            );
        }

        return $allowedLines;
    }

    /**
     * @throws RuntimeException
     */
    private function selectDriver(Filter $filter): Driver
    {
        $runtime = new Runtime;

        if ($runtime->hasPHPDBGCodeCoverage()) {
            return new PHPDBG;
        }

        if ($runtime->hasPCOV()) {
            return new PCOV;
        }

        if ($runtime->hasXdebug()) {
            return new Xdebug($filter);
        }

        throw new RuntimeException('No code coverage driver available');
    }

    private function processUnintentionallyCoveredUnits(array $unintentionallyCoveredUnits): array
    {
        $unintentionallyCoveredUnits = \array_unique($unintentionallyCoveredUnits);
        \sort($unintentionallyCoveredUnits);

        foreach (\array_keys($unintentionallyCoveredUnits) as $k => $v) {
            $unit = \explode('::', $unintentionallyCoveredUnits[$k]);

            if (\count($unit) !== 2) {
                continue;
            }

            $class = new \ReflectionClass($unit[0]);

            foreach ($this->unintentionallyCoveredSubclassesWhitelist as $whitelisted) {
                if ($class->isSubclassOf($whitelisted)) {
                    unset($unintentionallyCoveredUnits[$k]);

                    break;
                }
            }
        }

        return \array_values($unintentionallyCoveredUnits);
    }

    /**
     * @throws CoveredCodeNotExecutedException
     * @throws InvalidArgumentException
     * @throws MissingCoversAnnotationException
     * @throws RuntimeException
     * @throws UnintentionallyCoveredCodeException
     * @throws \ReflectionException
     */
    private function initializeData(): void
    {
        $this->isInitialized = true;

        if ($this->processUncoveredFilesFromWhitelist) {
            $this->shouldCheckForDeadAndUnused = false;

            $this->driver->start();

            foreach ($this->filter->getWhitelist() as $file) {
                if ($this->filter->isFile($file)) {
                    include_once $file;
                }
            }

            $data = [];

            foreach ($this->driver->stop() as $file => $fileCoverage) {
                if ($this->filter->isFiltered($file)) {
                    continue;
                }

                foreach (\array_keys($fileCoverage) as $key) {
                    if ($fileCoverage[$key] === Driver::LINE_EXECUTED) {
                        $fileCoverage[$key] = Driver::LINE_NOT_EXECUTED;
                    }
                }

                $data[$file] = $fileCoverage;
            }

            $this->append($data, 'UNCOVERED_FILES_FROM_WHITELIST');
        }
    }

    private function coverageToCodeUnits(array $data): array
    {
        $codeUnits = [];

        foreach ($data as $filename => $lines) {
            foreach ($lines as $line => $flag) {
                if ($flag === 1) {
                    $codeUnits[] = $this->wizard->lookup($filename, $line);
                }
            }
        }

        return \array_unique($codeUnits);
    }

    private function linesToCodeUnits(array $data): array
    {
        $codeUnits = [];

        foreach ($data as $filename => $lines) {
            foreach ($lines as $line) {
                $codeUnits[] = $this->wizard->lookup($filename, $line);
            }
        }

        return \array_unique($codeUnits);
    }
}
