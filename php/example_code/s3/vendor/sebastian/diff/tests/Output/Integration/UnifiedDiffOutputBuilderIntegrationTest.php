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
use SebastianBergmann\Diff\Utils\UnifiedDiffAssertTrait;
use Symfony\Component\Process\Process;

/**
 * @covers SebastianBergmann\Diff\Output\UnifiedDiffOutputBuilder
 *
 * @uses SebastianBergmann\Diff\Differ
 * @uses SebastianBergmann\Diff\TimeEfficientLongestCommonSubsequenceCalculator
 *
 * @requires OS Linux
 */
final class UnifiedDiffOutputBuilderIntegrationTest extends TestCase
{
    use UnifiedDiffAssertTrait;

    private $dir;

    private $fileFrom;

    private $filePatch;

    protected function setUp(): void
    {
        $this->dir       = \realpath(__DIR__ . '/../../fixtures/out/') . '/';
        $this->fileFrom  = $this->dir . 'from.txt';
        $this->filePatch = $this->dir . 'patch.txt';

        $this->cleanUpTempFiles();
    }

    protected function tearDown(): void
    {
        $this->cleanUpTempFiles();
    }

    /**
     * @dataProvider provideDiffWithLineNumbers
     *
     * @param mixed $expected
     * @param mixed $from
     * @param mixed $to
     */
    public function testDiffWithLineNumbersPath($expected, $from, $to): void
    {
        $this->doIntegrationTestPatch($expected, $from, $to);
    }

    /**
     * @dataProvider provideDiffWithLineNumbers
     *
     * @param mixed $expected
     * @param mixed $from
     * @param mixed $to
     */
    public function testDiffWithLineNumbersGitApply($expected, $from, $to): void
    {
        $this->doIntegrationTestGitApply($expected, $from, $to);
    }

    public function provideDiffWithLineNumbers()
    {
        return \array_filter(
            UnifiedDiffOutputBuilderDataProvider::provideDiffWithLineNumbers(),
            static function ($key) {
                return !\is_string($key) || false === \strpos($key, 'non_patch_compat');
            },
            ARRAY_FILTER_USE_KEY
        );
    }

    private function doIntegrationTestPatch(string $diff, string $from, string $to): void
    {
        $this->assertNotSame('', $diff);
        $this->assertValidUnifiedDiffFormat($diff);

        $diff = self::setDiffFileHeader($diff, $this->fileFrom);

        $this->assertNotFalse(\file_put_contents($this->fileFrom, $from));
        $this->assertNotFalse(\file_put_contents($this->filePatch, $diff));

        $p = Process::fromShellCommandline('patch -u --verbose --posix $from < $patch'); // --posix
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
        @\unlink($this->fileFrom);
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
