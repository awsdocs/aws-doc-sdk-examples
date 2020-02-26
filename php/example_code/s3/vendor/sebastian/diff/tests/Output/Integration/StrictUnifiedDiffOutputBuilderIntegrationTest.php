<?php declare(strict_types=1);
/*
 * This file is part of sebastian/diff.
 *
 * (c) Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

namespace SebastianBergmann\Diff\Output;

use PHPUnit\Framework\TestCase;
use SebastianBergmann\Diff\Differ;
use SebastianBergmann\Diff\Utils\FileUtils;
use SebastianBergmann\Diff\Utils\UnifiedDiffAssertTrait;
use Symfony\Component\Process\Process;

/**
 * @covers SebastianBergmann\Diff\Output\StrictUnifiedDiffOutputBuilder
 *
 * @uses SebastianBergmann\Diff\Differ
 * @uses SebastianBergmann\Diff\TimeEfficientLongestCommonSubsequenceCalculator
 * @uses SebastianBergmann\Diff\MemoryEfficientLongestCommonSubsequenceCalculator
 *
 * @requires OS Linux
 */
final class StrictUnifiedDiffOutputBuilderIntegrationTest extends TestCase
{
    use UnifiedDiffAssertTrait;

    private $dir;

    private $fileFrom;

    private $fileTo;

    private $filePatch;

    protected function setUp(): void
    {
        $this->dir       = \realpath(__DIR__ . '/../../fixtures/out') . '/';
        $this->fileFrom  = $this->dir . 'from.txt';
        $this->fileTo    = $this->dir . 'to.txt';
        $this->filePatch = $this->dir . 'diff.patch';

        if (!\is_dir($this->dir)) {
            throw new \RuntimeException('Integration test working directory not found.');
        }

        $this->cleanUpTempFiles();
    }

    protected function tearDown(): void
    {
        $this->cleanUpTempFiles();
    }

    /**
     * Integration test
     *
     * - get a file pair
     * - create a `diff` between the files
     * - test applying the diff using `git apply`
     * - test applying the diff using `patch`
     *
     * @param string $fileFrom
     * @param string $fileTo
     *
     * @dataProvider provideFilePairs
     */
    public function testIntegrationUsingPHPFileInVendorGitApply(string $fileFrom, string $fileTo): void
    {
        $from = FileUtils::getFileContent($fileFrom);
        $to   = FileUtils::getFileContent($fileTo);

        $diff = (new Differ(new StrictUnifiedDiffOutputBuilder(['fromFile' => 'Original', 'toFile' => 'New'])))->diff($from, $to);

        if ('' === $diff && $from === $to) {
            // odd case: test after executing as it is more efficient than to read the files and check the contents every time
            $this->addToAssertionCount(1);

            return;
        }

        $this->doIntegrationTestGitApply($diff, $from, $to);
    }

    /**
     * Integration test
     *
     * - get a file pair
     * - create a `diff` between the files
     * - test applying the diff using `git apply`
     * - test applying the diff using `patch`
     *
     * @param string $fileFrom
     * @param string $fileTo
     *
     * @dataProvider provideFilePairs
     */
    public function testIntegrationUsingPHPFileInVendorPatch(string $fileFrom, string $fileTo): void
    {
        $from = FileUtils::getFileContent($fileFrom);
        $to   = FileUtils::getFileContent($fileTo);

        $diff = (new Differ(new StrictUnifiedDiffOutputBuilder(['fromFile' => 'Original', 'toFile' => 'New'])))->diff($from, $to);

        if ('' === $diff && $from === $to) {
            // odd case: test after executing as it is more efficient than to read the files and check the contents every time
            $this->addToAssertionCount(1);

            return;
        }

        $this->doIntegrationTestPatch($diff, $from, $to);
    }

    /**
     * @param string $expected
     * @param string $from
     * @param string $to
     *
     * @dataProvider provideOutputBuildingCases
     * @dataProvider provideSample
     * @dataProvider provideBasicDiffGeneration
     */
    public function testIntegrationOfUnitTestCasesGitApply(string $expected, string $from, string $to): void
    {
        $this->doIntegrationTestGitApply($expected, $from, $to);
    }

    /**
     * @param string $expected
     * @param string $from
     * @param string $to
     *
     * @dataProvider provideOutputBuildingCases
     * @dataProvider provideSample
     * @dataProvider provideBasicDiffGeneration
     */
    public function testIntegrationOfUnitTestCasesPatch(string $expected, string $from, string $to): void
    {
        $this->doIntegrationTestPatch($expected, $from, $to);
    }

    public function provideOutputBuildingCases(): array
    {
        return StrictUnifiedDiffOutputBuilderDataProvider::provideOutputBuildingCases();
    }

    public function provideSample(): array
    {
        return StrictUnifiedDiffOutputBuilderDataProvider::provideSample();
    }

    public function provideBasicDiffGeneration(): array
    {
        return StrictUnifiedDiffOutputBuilderDataProvider::provideBasicDiffGeneration();
    }

    public function provideFilePairs(): array
    {
        $cases     = [];
        $fromFile  = __FILE__;
        $vendorDir = \realpath(__DIR__ . '/../../../vendor');

        $fileIterator = new \RecursiveIteratorIterator(new \RecursiveDirectoryIterator($vendorDir, \RecursiveDirectoryIterator::SKIP_DOTS));

        /** @var \SplFileInfo $file */
        foreach ($fileIterator as $file) {
            if ('php' !== $file->getExtension()) {
                continue;
            }

            $toFile                                                                                         = $file->getPathname();
            $cases[\sprintf("Diff file:\n\"%s\"\nvs.\n\"%s\"\n", \realpath($fromFile), \realpath($toFile))] = [$fromFile, $toFile];
            $fromFile                                                                                       = $toFile;
        }

        return $cases;
    }

    /**
     * Compare diff create by builder and against one create by `diff` command.
     *
     * @param string $diff
     * @param string $from
     * @param string $to
     *
     * @dataProvider provideBasicDiffGeneration
     */
    public function testIntegrationDiffOutputBuilderVersusDiffCommand(string $diff, string $from, string $to): void
    {
        $this->assertNotSame('', $diff);
        $this->assertValidUnifiedDiffFormat($diff);

        $this->assertNotFalse(\file_put_contents($this->fileFrom, $from));
        $this->assertNotFalse(\file_put_contents($this->fileTo, $to));

        $p = Process::fromShellCommandline('diff -u $from $to');
        $p->run(
            null,
            [
                'from' => $this->fileFrom,
                'to'   => $this->fileTo,
            ]
        );

        $this->assertSame(1, $p->getExitCode()); // note: Process assumes exit code 0 for `isSuccessful`, however `diff` uses the exit code `1` for success with diff

        $output = $p->getOutput();

        $diffLines    = \preg_split('/(.*\R)/', $diff, -1, PREG_SPLIT_DELIM_CAPTURE | PREG_SPLIT_NO_EMPTY);
        $diffLines[0] = \preg_replace('#^\-\-\- .*#', '--- /' . $this->fileFrom, $diffLines[0], 1);
        $diffLines[1] = \preg_replace('#^\+\+\+ .*#', '+++ /' . $this->fileFrom, $diffLines[1], 1);
        $diff         = \implode('', $diffLines);

        $outputLines    = \preg_split('/(.*\R)/', $output, -1, PREG_SPLIT_DELIM_CAPTURE | PREG_SPLIT_NO_EMPTY);
        $outputLines[0] = \preg_replace('#^\-\-\- .*#', '--- /' . $this->fileFrom, $outputLines[0], 1);
        $outputLines[1] = \preg_replace('#^\+\+\+ .*#', '+++ /' . $this->fileFrom, $outputLines[1], 1);
        $output         = \implode('', $outputLines);

        $this->assertSame($diff, $output);
    }

    private function doIntegrationTestGitApply(string $diff, string $from, string $to): void
    {
        $this->assertNotSame('', $diff);
        $this->assertValidUnifiedDiffFormat($diff);

        $diff = self::setDiffFileHeader($diff, $this->fileFrom);

        $this->assertNotFalse(\file_put_contents($this->fileFrom, $from));
        $this->assertNotFalse(\file_put_contents($this->filePatch, $diff));

        $p = Process::fromShellCommandline('git --git-dir $dir apply --check -v --unsafe-paths --ignore-whitespace $patch');
        $p->run(
            null,
            [
                'dir'   => $this->dir,
                'patch' => $this->filePatch,
            ]
        );

        $this->assertProcessSuccessful($p);
    }

    private function doIntegrationTestPatch(string $diff, string $from, string $to): void
    {
        $this->assertNotSame('', $diff);
        $this->assertValidUnifiedDiffFormat($diff);

        $diff = self::setDiffFileHeader($diff, $this->fileFrom);

        $this->assertNotFalse(\file_put_contents($this->fileFrom, $from));
        $this->assertNotFalse(\file_put_contents($this->filePatch, $diff));

        $p = Process::fromShellCommandline('patch -u --verbose --posix $from < $patch');
        $p->run(
            null,
            [
                'from'  => $this->fileFrom,
                'patch' => $this->filePatch,
            ]
        );

        $this->assertProcessSuccessful($p);

        $this->assertStringEqualsFile(
            $this->fileFrom,
            $to,
            \sprintf('Patch command "%s".', $p->getCommandLine())
        );
    }

    private function assertProcessSuccessful(Process $p): void
    {
        $this->assertTrue(
            $p->isSuccessful(),
            \sprintf(
                "Command exec. was not successful:\n\"%s\"\nOutput:\n\"%s\"\nStdErr:\n\"%s\"\nExit code %d.\n",
                $p->getCommandLine(),
                $p->getOutput(),
                $p->getErrorOutput(),
                $p->getExitCode()
            )
        );
    }

    private function cleanUpTempFiles(): void
    {
        @\unlink($this->fileFrom . '.orig');
        @\unlink($this->fileFrom . '.rej');
        @\unlink($this->fileFrom);
        @\unlink($this->fileTo);
        @\unlink($this->filePatch);
    }

    private static function setDiffFileHeader(string $diff, string $file): string
    {
        $diffLines    = \preg_split('/(.*\R)/', $diff, -1, PREG_SPLIT_DELIM_CAPTURE | PREG_SPLIT_NO_EMPTY);
        $diffLines[0] = \preg_replace('#^\-\-\- .*#', '--- /' . $file, $diffLines[0], 1);
        $diffLines[1] = \preg_replace('#^\+\+\+ .*#', '+++ /' . $file, $diffLines[1], 1);

        return \implode('', $diffLines);
    }
}
