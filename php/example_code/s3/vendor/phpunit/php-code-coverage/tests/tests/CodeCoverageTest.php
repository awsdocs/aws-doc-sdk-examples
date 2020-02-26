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

use SebastianBergmann\CodeCoverage\Driver\Driver;
use SebastianBergmann\Environment\Runtime;

/**
 * @covers SebastianBergmann\CodeCoverage\CodeCoverage
 */
class CodeCoverageTest extends TestCase
{
    /**
     * @var CodeCoverage
     */
    private $coverage;

    protected function setUp(): void
    {
        $runtime = new Runtime;

        if (!$runtime->canCollectCodeCoverage()) {
            $this->markTestSkipped('No code coverage driver available');
        }

        $this->coverage = new CodeCoverage;
    }

    public function testCannotStopWithInvalidSecondArgument(): void
    {
        $this->expectException(Exception::class);

        $this->coverage->stop(true, null);
    }

    public function testCannotAppendWithInvalidArgument(): void
    {
        $this->expectException(Exception::class);

        $this->coverage->append([], null);
    }

    public function testCollect(): void
    {
        $coverage = $this->getCoverageForBankAccount();

        $this->assertEquals(
            $this->getExpectedDataArrayForBankAccount(),
            $coverage->getData()
        );

        $this->assertEquals(
            [
                'BankAccountTest::testBalanceIsInitiallyZero'       => ['size' => 'unknown', 'status' => -1],
                'BankAccountTest::testBalanceCannotBecomeNegative'  => ['size' => 'unknown', 'status' => -1],
                'BankAccountTest::testBalanceCannotBecomeNegative2' => ['size' => 'unknown', 'status' => -1],
                'BankAccountTest::testDepositWithdrawMoney'         => ['size' => 'unknown', 'status' => -1],
            ],
            $coverage->getTests()
        );
    }

    public function testMerge(): void
    {
        $coverage = $this->getCoverageForBankAccountForFirstTwoTests();
        $coverage->merge($this->getCoverageForBankAccountForLastTwoTests());

        $this->assertEquals(
            $this->getExpectedDataArrayForBankAccount(),
            $coverage->getData()
        );
    }

    public function testMergeReverseOrder(): void
    {
        $coverage = $this->getCoverageForBankAccountForLastTwoTests();
        $coverage->merge($this->getCoverageForBankAccountForFirstTwoTests());

        $this->assertEquals(
            $this->getExpectedDataArrayForBankAccountInReverseOrder(),
            $coverage->getData()
        );
    }

    public function testMerge2(): void
    {
        $coverage = new CodeCoverage(
            $this->createMock(Driver::class),
            new Filter
        );

        $coverage->merge($this->getCoverageForBankAccount());

        $this->assertEquals(
            $this->getExpectedDataArrayForBankAccount(),
            $coverage->getData()
        );
    }

    public function testGetLinesToBeIgnored(): void
    {
        $this->assertEquals(
            [
                1,
                3,
                4,
                5,
                7,
                8,
                9,
                10,
                11,
                12,
                13,
                14,
                15,
                16,
                17,
                18,
                19,
                20,
                21,
                22,
                23,
                24,
                25,
                26,
                27,
                28,
                30,
                32,
                33,
                34,
                35,
                36,
                37,
                38,
            ],
            $this->getLinesToBeIgnored()->invoke(
                $this->coverage,
                TEST_FILES_PATH . 'source_with_ignore.php'
            )
        );
    }

    public function testGetLinesToBeIgnored2(): void
    {
        $this->assertEquals(
            [1, 5],
            $this->getLinesToBeIgnored()->invoke(
                $this->coverage,
                TEST_FILES_PATH . 'source_without_ignore.php'
            )
        );
    }

    public function testGetLinesToBeIgnored3(): void
    {
        $this->assertEquals(
            [
                1,
                2,
                3,
                4,
                5,
                8,
                11,
                15,
                16,
                19,
                20,
            ],
            $this->getLinesToBeIgnored()->invoke(
                $this->coverage,
                TEST_FILES_PATH . 'source_with_class_and_anonymous_function.php'
            )
        );
    }

    public function testGetLinesToBeIgnoredOneLineAnnotations(): void
    {
        $this->assertEquals(
            [
                1,
                2,
                3,
                4,
                5,
                6,
                7,
                8,
                9,
                10,
                11,
                14,
                15,
                16,
                18,
                20,
                21,
                23,
                24,
                25,
                27,
                28,
                29,
                30,
                31,
                32,
                33,
                34,
                37,
            ],
            $this->getLinesToBeIgnored()->invoke(
                $this->coverage,
                TEST_FILES_PATH . 'source_with_oneline_annotations.php'
            )
        );
    }

    public function testGetLinesToBeIgnoredWhenIgnoreIsDisabled(): void
    {
        $this->coverage->setDisableIgnoredLines(true);

        $this->assertEquals(
            [
                7,
                11,
                12,
                13,
                16,
                17,
                18,
                19,
                20,
                21,
                22,
                23,
                26,
                27,
                32,
                33,
                34,
                35,
                36,
                37,
            ],
            $this->getLinesToBeIgnored()->invoke(
                $this->coverage,
                TEST_FILES_PATH . 'source_with_ignore.php'
            )
        );
    }

    public function testUseStatementsAreIgnored(): void
    {
        $this->assertEquals(
            [
                1,
                2,
                3,
                4,
                5,
                6,
                7,
                8,
                9,
                10,
                13,
                16,
                23,
                24,
            ],
            $this->getLinesToBeIgnored()->invoke(
                $this->coverage,
                TEST_FILES_PATH . 'source_with_use_statements.php'
            )
        );
    }

    public function testAppendThrowsExceptionIfCoveredCodeWasNotExecuted(): void
    {
        $this->coverage->filter()->addDirectoryToWhitelist(TEST_FILES_PATH);
        $this->coverage->setCheckForUnexecutedCoveredCode(true);

        $data = [
            TEST_FILES_PATH . 'BankAccount.php' => [
                29 => -1,
                31 => -1,
            ],
        ];

        $linesToBeCovered = [
            TEST_FILES_PATH . 'BankAccount.php' => [
                22,
                24,
            ],
        ];

        $linesToBeUsed = [];

        $this->expectException(CoveredCodeNotExecutedException::class);

        $this->coverage->append($data, 'File1.php', true, $linesToBeCovered, $linesToBeUsed);
    }

    public function testAppendThrowsExceptionIfUsedCodeWasNotExecuted(): void
    {
        $this->coverage->filter()->addDirectoryToWhitelist(TEST_FILES_PATH);
        $this->coverage->setCheckForUnexecutedCoveredCode(true);

        $data = [
            TEST_FILES_PATH . 'BankAccount.php' => [
                29 => -1,
                31 => -1,
            ],
        ];

        $linesToBeCovered = [
            TEST_FILES_PATH . 'BankAccount.php' => [
                29,
                31,
            ],
        ];

        $linesToBeUsed = [
            TEST_FILES_PATH . 'BankAccount.php' => [
                22,
                24,
            ],
        ];

        $this->expectException(CoveredCodeNotExecutedException::class);

        $this->coverage->append($data, 'File1.php', true, $linesToBeCovered, $linesToBeUsed);
    }

    /**
     * @return \ReflectionMethod
     */
    private function getLinesToBeIgnored()
    {
        $getLinesToBeIgnored = new \ReflectionMethod(
            'SebastianBergmann\CodeCoverage\CodeCoverage',
            'getLinesToBeIgnored'
        );

        $getLinesToBeIgnored->setAccessible(true);

        return $getLinesToBeIgnored;
    }
}
