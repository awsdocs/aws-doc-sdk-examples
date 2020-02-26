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

use PHPUnit\Framework\TestCase;
use Symfony\Component\Process\Process;

/**
 * @requires OS Linux
 *
 * @coversNothing
 */
final class UnifiedDiffAssertTraitIntegrationTest extends TestCase
{
    use UnifiedDiffAssertTrait;

    private $filePatch;

    protected function setUp(): void
    {
        $this->filePatch = __DIR__ . '/../fixtures/out/patch.txt';

        $this->cleanUpTempFiles();
    }

    protected function tearDown(): void
    {
        $this->cleanUpTempFiles();
    }

    /**
     * @param string $fileFrom
     * @param string $fileTo
     *
     * @dataProvider provideFilePairsCases
     */
    public function testValidPatches(string $fileFrom, string $fileTo): void
    {
        $p = Process::fromShellCommandline('diff -u $from $to > $patch');
        $p->run(
            null,
            [
                'from'  => \realpath($fileFrom),
                'to'    => \realpath($fileTo),
                'patch' => $this->filePatch,
            ]
        );

        $exitCode = $p->getExitCode();

        if (0 === $exitCode) {
            // odd case when two files have the same content. Test after executing as it is more efficient than to read the files and check the contents every time.
            $this->addToAssertionCount(1);

            return;
        }

        $this->assertSame(
            1, // means `diff` found a diff between the files we gave it
            $exitCode,
            \sprintf(
                "Command exec. was not successful:\n\"%s\"\nOutput:\n\"%s\"\nStdErr:\n\"%s\"\nExit code %d.\n",
                $p->getCommandLine(),
                $p->getOutput(),
                $p->getErrorOutput(),
                $p->getExitCode()
            )
        );

        $this->assertValidUnifiedDiffFormat(FileUtils::getFileContent($this->filePatch));
    }

    /**
     * @return array<string, array<string, string>>
     */
    public function provideFilePairsCases(): array
    {
        $cases = [];

        // created cases based on dedicated fixtures
        $dir       = \realpath(__DIR__ . '/../fixtures/UnifiedDiffAssertTraitIntegrationTest');
        $dirLength = \strlen($dir);

        for ($i = 1;; ++$i) {
            $fromFile = \sprintf('%s/%d_a.txt', $dir, $i);
            $toFile   = \sprintf('%s/%d_b.txt', $dir, $i);

            if (!\file_exists($fromFile)) {
                break;
            }

            $this->assertFileExists($toFile);
            $cases[\sprintf("Diff file:\n\"%s\"\nvs.\n\"%s\"\n", \substr(\realpath($fromFile), $dirLength), \substr(\realpath($toFile), $dirLength))] = [$fromFile, $toFile];
        }

        // create cases based on PHP files within the vendor directory for integration testing
        $dir       = \realpath(__DIR__ . '/../../vendor');
        $dirLength = \strlen($dir);

        $fileIterator = new \RecursiveIteratorIterator(new \RecursiveDirectoryIterator($dir, \RecursiveDirectoryIterator::SKIP_DOTS));
        $fromFile     = __FILE__;

        /** @var \SplFileInfo $file */
        foreach ($fileIterator as $file) {
            if ('php' !== $file->getExtension()) {
                continue;
            }

            $toFile                                                                                                                                   = $file->getPathname();
            $cases[\sprintf("Diff file:\n\"%s\"\nvs.\n\"%s\"\n", \substr(\realpath($fromFile), $dirLength), \substr(\realpath($toFile), $dirLength))] = [$fromFile, $toFile];
            $fromFile                                                                                                                                 = $toFile;
        }

        return $cases;
    }

    private function cleanUpTempFiles(): void
    {
        @\unlink($this->filePatch);
    }
}
