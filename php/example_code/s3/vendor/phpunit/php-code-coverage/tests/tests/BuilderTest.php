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

use SebastianBergmann\CodeCoverage\Node\Builder;
use SebastianBergmann\CodeCoverage\TestCase;

class BuilderTest extends TestCase
{
    protected $factory;

    protected function setUp(): void
    {
        $this->factory = new Builder;
    }

    public function testSomething(): void
    {
        $root = $this->getCoverageForBankAccount()->getReport();

        $expectedPath = \rtrim(TEST_FILES_PATH, \DIRECTORY_SEPARATOR);
        $this->assertEquals($expectedPath, $root->getName());
        $this->assertEquals($expectedPath, $root->getPath());
        $this->assertEquals(10, $root->getNumExecutableLines());
        $this->assertEquals(5, $root->getNumExecutedLines());
        $this->assertEquals(1, $root->getNumClasses());
        $this->assertEquals(0, $root->getNumTestedClasses());
        $this->assertEquals(4, $root->getNumMethods());
        $this->assertEquals(3, $root->getNumTestedMethods());
        $this->assertEquals('0.00%', $root->getTestedClassesPercent());
        $this->assertEquals('75.00%', $root->getTestedMethodsPercent());
        $this->assertEquals('50.00%', $root->getLineExecutedPercent());
        $this->assertEquals(0, $root->getNumFunctions());
        $this->assertEquals(0, $root->getNumTestedFunctions());
        $this->assertNull($root->getParent());
        $this->assertEquals([], $root->getDirectories());
        #$this->assertEquals(array(), $root->getFiles());
        #$this->assertEquals(array(), $root->getChildNodes());

        $this->assertEquals(
            [
                'BankAccount' => [
                    'methods' => [
                        'getBalance' => [
                            'signature'       => 'getBalance()',
                            'startLine'       => 6,
                            'endLine'         => 9,
                            'executableLines' => 1,
                            'executedLines'   => 1,
                            'ccn'             => 1,
                            'coverage'        => 100,
                            'crap'            => '1',
                            'link'            => 'BankAccount.php.html#6',
                            'methodName'      => 'getBalance',
                            'visibility'      => 'public',
                        ],
                        'setBalance' => [
                            'signature'       => 'setBalance($balance)',
                            'startLine'       => 11,
                            'endLine'         => 18,
                            'executableLines' => 5,
                            'executedLines'   => 0,
                            'ccn'             => 2,
                            'coverage'        => 0,
                            'crap'            => 6,
                            'link'            => 'BankAccount.php.html#11',
                            'methodName'      => 'setBalance',
                            'visibility'      => 'protected',
                        ],
                        'depositMoney' => [
                            'signature'       => 'depositMoney($balance)',
                            'startLine'       => 20,
                            'endLine'         => 25,
                            'executableLines' => 2,
                            'executedLines'   => 2,
                            'ccn'             => 1,
                            'coverage'        => 100,
                            'crap'            => '1',
                            'link'            => 'BankAccount.php.html#20',
                            'methodName'      => 'depositMoney',
                            'visibility'      => 'public',
                        ],
                        'withdrawMoney' => [
                            'signature'       => 'withdrawMoney($balance)',
                            'startLine'       => 27,
                            'endLine'         => 32,
                            'executableLines' => 2,
                            'executedLines'   => 2,
                            'ccn'             => 1,
                            'coverage'        => 100,
                            'crap'            => '1',
                            'link'            => 'BankAccount.php.html#27',
                            'methodName'      => 'withdrawMoney',
                            'visibility'      => 'public',
                        ],
                    ],
                    'startLine'       => 2,
                    'executableLines' => 10,
                    'executedLines'   => 5,
                    'ccn'             => 5,
                    'coverage'        => 50,
                    'crap'            => '8.12',
                    'package'         => [
                        'namespace'   => '',
                        'fullPackage' => '',
                        'category'    => '',
                        'package'     => '',
                        'subpackage'  => '',
                    ],
                    'link'      => 'BankAccount.php.html#2',
                    'className' => 'BankAccount',
                ],
            ],
            $root->getClasses()
        );

        $this->assertEquals([], $root->getFunctions());
    }

    public function testNotCrashParsing(): void
    {
        $coverage = $this->getCoverageForCrashParsing();
        $root     = $coverage->getReport();

        $expectedPath = \rtrim(TEST_FILES_PATH, \DIRECTORY_SEPARATOR);
        $this->assertEquals($expectedPath, $root->getName());
        $this->assertEquals($expectedPath, $root->getPath());
        $this->assertEquals(2, $root->getNumExecutableLines());
        $this->assertEquals(0, $root->getNumExecutedLines());
        $data         = $coverage->getData();
        $expectedFile = $expectedPath . \DIRECTORY_SEPARATOR . 'Crash.php';
        $this->assertSame([$expectedFile => [1 => [], 2 => []]], $data);
    }

    public function testBuildDirectoryStructure(): void
    {
        $s = \DIRECTORY_SEPARATOR;

        $method = new \ReflectionMethod(
            Builder::class,
            'buildDirectoryStructure'
        );

        $method->setAccessible(true);

        $this->assertEquals(
            [
                'src' => [
                    'Money.php/f'    => [],
                    'MoneyBag.php/f' => [],
                    'Foo'            => [
                        'Bar' => [
                            'Baz' => [
                                'Foo.php/f' => [],
                            ],
                        ],
                    ],
                ],
            ],
            $method->invoke(
                $this->factory,
                [
                    "src{$s}Money.php"                    => [],
                    "src{$s}MoneyBag.php"                 => [],
                    "src{$s}Foo{$s}Bar{$s}Baz{$s}Foo.php" => [],
                ]
            )
        );
    }

    /**
     * @dataProvider reducePathsProvider
     */
    public function testReducePaths($reducedPaths, $commonPath, $paths): void
    {
        $method = new \ReflectionMethod(
            Builder::class,
            'reducePaths'
        );

        $method->setAccessible(true);

        $_commonPath = $method->invokeArgs($this->factory, [&$paths]);

        $this->assertEquals($reducedPaths, $paths);
        $this->assertEquals($commonPath, $_commonPath);
    }

    public function reducePathsProvider()
    {
        $s = \DIRECTORY_SEPARATOR;

        yield [
            [],
            '.',
            [],
        ];

        $prefixes = ["C:$s", "$s"];

        foreach ($prefixes as $p) {
            yield [
                [
                    'Money.php' => [],
                ],
                "{$p}home{$s}sb{$s}Money{$s}",
                [
                    "{$p}home{$s}sb{$s}Money{$s}Money.php" => [],
                ],
            ];

            yield [
                [
                    'Money.php'    => [],
                    'MoneyBag.php' => [],
                ],
                "{$p}home{$s}sb{$s}Money",
                [
                    "{$p}home{$s}sb{$s}Money{$s}Money.php"    => [],
                    "{$p}home{$s}sb{$s}Money{$s}MoneyBag.php" => [],
                ],
            ];

            yield [
                [
                    'Money.php'             => [],
                    'MoneyBag.php'          => [],
                    "Cash.phar{$s}Cash.php" => [],
                ],
                "{$p}home{$s}sb{$s}Money",
                [
                    "{$p}home{$s}sb{$s}Money{$s}Money.php"                    => [],
                    "{$p}home{$s}sb{$s}Money{$s}MoneyBag.php"                 => [],
                    "phar://{$p}home{$s}sb{$s}Money{$s}Cash.phar{$s}Cash.php" => [],
                ],
            ];
        }
    }
}
