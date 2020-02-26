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
use SebastianBergmann\Diff\ConfigurationException;
use SebastianBergmann\Diff\Differ;
use SebastianBergmann\Diff\Utils\UnifiedDiffAssertTrait;

/**
 * @covers SebastianBergmann\Diff\Output\StrictUnifiedDiffOutputBuilder
 *
 * @uses SebastianBergmann\Diff\Differ
 * @uses SebastianBergmann\Diff\TimeEfficientLongestCommonSubsequenceCalculator
 * @uses SebastianBergmann\Diff\ConfigurationException
 */
final class StrictUnifiedDiffOutputBuilderTest extends TestCase
{
    use UnifiedDiffAssertTrait;

    /**
     * @param string $expected
     * @param string $from
     * @param string $to
     * @param array  $options
     *
     * @dataProvider provideOutputBuildingCases
     */
    public function testOutputBuilding(string $expected, string $from, string $to, array $options): void
    {
        $diff = $this->getDiffer($options)->diff($from, $to);

        $this->assertValidDiffFormat($diff);
        $this->assertSame($expected, $diff);
    }

    /**
     * @param string $expected
     * @param string $from
     * @param string $to
     * @param array  $options
     *
     * @dataProvider provideSample
     */
    public function testSample(string $expected, string $from, string $to, array $options): void
    {
        $diff = $this->getDiffer($options)->diff($from, $to);

        $this->assertValidDiffFormat($diff);
        $this->assertSame($expected, $diff);
    }

    /**
     * {@inheritdoc}
     */
    public function assertValidDiffFormat(string $diff): void
    {
        $this->assertValidUnifiedDiffFormat($diff);
    }

    /**
     * {@inheritdoc}
     */
    public function provideOutputBuildingCases(): array
    {
        return StrictUnifiedDiffOutputBuilderDataProvider::provideOutputBuildingCases();
    }

    /**
     * {@inheritdoc}
     */
    public function provideSample(): array
    {
        return StrictUnifiedDiffOutputBuilderDataProvider::provideSample();
    }

    /**
     * @param string $expected
     * @param string $from
     * @param string $to
     *
     * @dataProvider provideBasicDiffGeneration
     */
    public function testBasicDiffGeneration(string $expected, string $from, string $to): void
    {
        $diff = $this->getDiffer([
            'fromFile' => 'input.txt',
            'toFile'   => 'output.txt',
        ])->diff($from, $to);

        $this->assertValidDiffFormat($diff);
        $this->assertSame($expected, $diff);
    }

    public function provideBasicDiffGeneration(): array
    {
        return StrictUnifiedDiffOutputBuilderDataProvider::provideBasicDiffGeneration();
    }

    /**
     * @param string $expected
     * @param string $from
     * @param string $to
     * @param array  $config
     *
     * @dataProvider provideConfiguredDiffGeneration
     */
    public function testConfiguredDiffGeneration(string $expected, string $from, string $to, array $config = []): void
    {
        $diff = $this->getDiffer(\array_merge([
            'fromFile' => 'input.txt',
            'toFile'   => 'output.txt',
        ], $config))->diff($from, $to);

        $this->assertValidDiffFormat($diff);
        $this->assertSame($expected, $diff);
    }

    public function provideConfiguredDiffGeneration(): array
    {
        return [
            [
                '--- input.txt
+++ output.txt
@@ -1 +1 @@
-a
\ No newline at end of file
+b
\ No newline at end of file
',
                'a',
                'b',
            ],
            [
                '',
                "1\n2",
                "1\n2",
            ],
            [
                '',
                "1\n",
                "1\n",
            ],
            [
'--- input.txt
+++ output.txt
@@ -4 +4 @@
-X
+4
',
                "1\n2\n3\nX\n5\n6\n7\n8\n9\n0\n",
                "1\n2\n3\n4\n5\n6\n7\n8\n9\n0\n",
                [
                    'contextLines' => 0,
                ],
            ],
            [
'--- input.txt
+++ output.txt
@@ -3,3 +3,3 @@
 3
-X
+4
 5
',
                "1\n2\n3\nX\n5\n6\n7\n8\n9\n0\n",
                "1\n2\n3\n4\n5\n6\n7\n8\n9\n0\n",
                [
                    'contextLines' => 1,
                ],
            ],
            [
'--- input.txt
+++ output.txt
@@ -1,10 +1,10 @@
 1
 2
 3
-X
+4
 5
 6
 7
 8
 9
 0
',
                "1\n2\n3\nX\n5\n6\n7\n8\n9\n0\n",
                "1\n2\n3\n4\n5\n6\n7\n8\n9\n0\n",
                [
                    'contextLines' => 999,
                ],
            ],
            [
'--- input.txt
+++ output.txt
@@ -1,0 +1,2 @@
+
+A
',
                '',
                "\nA\n",
            ],
            [
'--- input.txt
+++ output.txt
@@ -1,2 +1,0 @@
-
-A
',
                "\nA\n",
                '',
            ],
            [
                '--- input.txt
+++ output.txt
@@ -1,5 +1,5 @@
 1
-X
+2
 3
-Y
+4
 5
@@ -8,3 +8,3 @@
 8
-X
+9
 0
',
                "1\nX\n3\nY\n5\n6\n7\n8\nX\n0\n",
                "1\n2\n3\n4\n5\n6\n7\n8\n9\n0\n",
                [
                    'commonLineThreshold' => 2,
                    'contextLines'        => 1,
                ],
            ],
            [
                '--- input.txt
+++ output.txt
@@ -2 +2 @@
-X
+2
@@ -4 +4 @@
-Y
+4
@@ -9 +9 @@
-X
+9
',
                "1\nX\n3\nY\n5\n6\n7\n8\nX\n0\n",
                "1\n2\n3\n4\n5\n6\n7\n8\n9\n0\n",
                [
                    'commonLineThreshold' => 1,
                    'contextLines'        => 0,
                ],
            ],
        ];
    }

    public function testReUseBuilder(): void
    {
        $differ = $this->getDiffer([
            'fromFile' => 'input.txt',
            'toFile'   => 'output.txt',
        ]);

        $diff = $differ->diff("A\nB\n", "A\nX\n");
        $this->assertSame(
            '--- input.txt
+++ output.txt
@@ -1,2 +1,2 @@
 A
-B
+X
',
            $diff
        );

        $diff = $differ->diff("A\n", "A\n");
        $this->assertSame(
            '',
            $diff
        );
    }

    public function testEmptyDiff(): void
    {
        $builder = new StrictUnifiedDiffOutputBuilder([
            'fromFile' => 'input.txt',
            'toFile'   => 'output.txt',
        ]);

        $this->assertSame(
            '',
            $builder->getDiff([])
        );
    }

    /**
     * @param array  $options
     * @param string $message
     *
     * @dataProvider provideInvalidConfiguration
     */
    public function testInvalidConfiguration(array $options, string $message): void
    {
        $this->expectException(ConfigurationException::class);
        $this->expectExceptionMessageMatches(\sprintf('#^%s$#', \preg_quote($message, '#')));

        new StrictUnifiedDiffOutputBuilder($options);
    }

    public function provideInvalidConfiguration(): array
    {
        $time = \time();

        return [
            [
                ['collapseRanges' => 1],
                'Option "collapseRanges" must be a bool, got "integer#1".',
            ],
            [
                ['contextLines' => 'a'],
                'Option "contextLines" must be an int >= 0, got "string#a".',
            ],
            [
                ['commonLineThreshold' => -2],
                'Option "commonLineThreshold" must be an int > 0, got "integer#-2".',
            ],
            [
                ['commonLineThreshold' => 0],
                'Option "commonLineThreshold" must be an int > 0, got "integer#0".',
            ],
            [
                ['fromFile' => new \SplFileInfo(__FILE__)],
                'Option "fromFile" must be a string, got "SplFileInfo".',
            ],
            [
                ['fromFile' => null],
                'Option "fromFile" must be a string, got "<null>".',
            ],
            [
                [
                    'fromFile' => __FILE__,
                    'toFile'   => 1,
                ],
                'Option "toFile" must be a string, got "integer#1".',
            ],
            [
                [
                    'fromFile'   => __FILE__,
                    'toFile'     => __FILE__,
                    'toFileDate' => $time,
                ],
                'Option "toFileDate" must be a string or <null>, got "integer#' . $time . '".',
            ],
            [
                [],
                'Option "fromFile" must be a string, got "<null>".',
            ],
        ];
    }

    /**
     * @param string $expected
     * @param string $from
     * @param string $to
     * @param int    $threshold
     *
     * @dataProvider provideCommonLineThresholdCases
     */
    public function testCommonLineThreshold(string $expected, string $from, string $to, int $threshold): void
    {
        $diff = $this->getDiffer([
            'fromFile'            => 'input.txt',
            'toFile'              => 'output.txt',
            'commonLineThreshold' => $threshold,
            'contextLines'        => 0,
        ])->diff($from, $to);

        $this->assertValidDiffFormat($diff);
        $this->assertSame($expected, $diff);
    }

    public function provideCommonLineThresholdCases(): array
    {
        return [
            [
'--- input.txt
+++ output.txt
@@ -2,3 +2,3 @@
-X
+B
 C12
-Y
+D
@@ -7 +7 @@
-X
+Z
',
                "A\nX\nC12\nY\nA\nA\nX\n",
                "A\nB\nC12\nD\nA\nA\nZ\n",
                2,
            ],
            [
'--- input.txt
+++ output.txt
@@ -2 +2 @@
-X
+B
@@ -4 +4 @@
-Y
+D
',
                "A\nX\nV\nY\n",
                "A\nB\nV\nD\n",
                1,
            ],
        ];
    }

    /**
     * @param string $expected
     * @param string $from
     * @param string $to
     * @param int    $contextLines
     * @param int    $commonLineThreshold
     *
     * @dataProvider provideContextLineConfigurationCases
     */
    public function testContextLineConfiguration(string $expected, string $from, string $to, int $contextLines, int $commonLineThreshold = 6): void
    {
        $diff = $this->getDiffer([
            'fromFile'            => 'input.txt',
            'toFile'              => 'output.txt',
            'contextLines'        => $contextLines,
            'commonLineThreshold' => $commonLineThreshold,
        ])->diff($from, $to);

        $this->assertValidDiffFormat($diff);
        $this->assertSame($expected, $diff);
    }

    public function provideContextLineConfigurationCases(): array
    {
        $from = "A\nB\nC\nD\nE\nF\nX\nG\nH\nI\nJ\nK\nL\nM\n";
        $to   = "A\nB\nC\nD\nE\nF\nY\nG\nH\nI\nJ\nK\nL\nM\n";

        return [
            'EOF 0' => [
                "--- input.txt\n+++ output.txt\n@@ -3 +3 @@
-X
\\ No newline at end of file
+Y
\\ No newline at end of file
",
                "A\nB\nX",
                "A\nB\nY",
                0,
            ],
            'EOF 1' => [
                "--- input.txt\n+++ output.txt\n@@ -2,2 +2,2 @@
 B
-X
\\ No newline at end of file
+Y
\\ No newline at end of file
",
                "A\nB\nX",
                "A\nB\nY",
                1,
],
            'EOF 2' => [
                "--- input.txt\n+++ output.txt\n@@ -1,3 +1,3 @@
 A
 B
-X
\\ No newline at end of file
+Y
\\ No newline at end of file
",
                "A\nB\nX",
                "A\nB\nY",
                2,
            ],
            'EOF 200' => [
                "--- input.txt\n+++ output.txt\n@@ -1,3 +1,3 @@
 A
 B
-X
\\ No newline at end of file
+Y
\\ No newline at end of file
",
                "A\nB\nX",
                "A\nB\nY",
                200,
            ],
            'n/a 0' => [
                "--- input.txt\n+++ output.txt\n@@ -7 +7 @@\n-X\n+Y\n",
                $from,
                $to,
                0,
            ],
            'G' => [
                "--- input.txt\n+++ output.txt\n@@ -6,3 +6,3 @@\n F\n-X\n+Y\n G\n",
                $from,
                $to,
                1,
            ],
            'H' => [
                "--- input.txt\n+++ output.txt\n@@ -5,5 +5,5 @@\n E\n F\n-X\n+Y\n G\n H\n",
                $from,
                $to,
                2,
            ],
            'I' => [
                "--- input.txt\n+++ output.txt\n@@ -4,7 +4,7 @@\n D\n E\n F\n-X\n+Y\n G\n H\n I\n",
                $from,
                $to,
                3,
            ],
            'J' => [
                "--- input.txt\n+++ output.txt\n@@ -3,9 +3,9 @@\n C\n D\n E\n F\n-X\n+Y\n G\n H\n I\n J\n",
                $from,
                $to,
                4,
            ],
            'K' => [
                "--- input.txt\n+++ output.txt\n@@ -2,11 +2,11 @@\n B\n C\n D\n E\n F\n-X\n+Y\n G\n H\n I\n J\n K\n",
                $from,
                $to,
                5,
            ],
            'L' => [
                "--- input.txt\n+++ output.txt\n@@ -1,13 +1,13 @@\n A\n B\n C\n D\n E\n F\n-X\n+Y\n G\n H\n I\n J\n K\n L\n",
                $from,
                $to,
                6,
            ],
            'M' => [
                "--- input.txt\n+++ output.txt\n@@ -1,14 +1,14 @@\n A\n B\n C\n D\n E\n F\n-X\n+Y\n G\n H\n I\n J\n K\n L\n M\n",
                $from,
                $to,
                7,
            ],
            'M no linebreak EOF .1' => [
                "--- input.txt\n+++ output.txt\n@@ -1,14 +1,14 @@\n A\n B\n C\n D\n E\n F\n-X\n+Y\n G\n H\n I\n J\n K\n L\n-M\n+M\n\\ No newline at end of file\n",
                $from,
                \substr($to, 0, -1),
                7,
            ],
            'M no linebreak EOF .2' => [
                "--- input.txt\n+++ output.txt\n@@ -1,14 +1,14 @@\n A\n B\n C\n D\n E\n F\n-X\n+Y\n G\n H\n I\n J\n K\n L\n-M\n\\ No newline at end of file\n+M\n",
                \substr($from, 0, -1),
                $to,
                7,
            ],
            'M no linebreak EOF .3' => [
                "--- input.txt\n+++ output.txt\n@@ -1,14 +1,14 @@\n A\n B\n C\n D\n E\n F\n-X\n+Y\n G\n H\n I\n J\n K\n L\n M\n",
                \substr($from, 0, -1),
                \substr($to, 0, -1),
                7,
            ],
            'M no linebreak EOF .4' => [
                "--- input.txt\n+++ output.txt\n@@ -1,14 +1,14 @@\n A\n B\n C\n D\n E\n F\n-X\n+Y\n G\n H\n I\n J\n K\n L\n M\n\\ No newline at end of file\n",
                \substr($from, 0, -1),
                \substr($to, 0, -1),
                10000,
                10000,
            ],
            'M+1' => [
                "--- input.txt\n+++ output.txt\n@@ -1,14 +1,14 @@\n A\n B\n C\n D\n E\n F\n-X\n+Y\n G\n H\n I\n J\n K\n L\n M\n",
                $from,
                $to,
                8,
            ],
            'M+100' => [
                "--- input.txt\n+++ output.txt\n@@ -1,14 +1,14 @@\n A\n B\n C\n D\n E\n F\n-X\n+Y\n G\n H\n I\n J\n K\n L\n M\n",
                $from,
                $to,
                107,
            ],
            '0 II' => [
                "--- input.txt\n+++ output.txt\n@@ -12 +12 @@\n-X\n+Y\n",
                "A\nB\nC\nD\nE\nF\nG\nH\nI\nJ\nK\nX\nM\n",
                "A\nB\nC\nD\nE\nF\nG\nH\nI\nJ\nK\nY\nM\n",
                0,
                999,
            ],
            '0\' II' => [
                "--- input.txt\n+++ output.txt\n@@ -12 +12 @@\n-X\n+Y\n",
                "A\nB\nC\nD\nE\nF\nG\nH\nI\nJ\nK\nX\nM\nA\nA\nA\nA\nA\n",
                "A\nB\nC\nD\nE\nF\nG\nH\nI\nJ\nK\nY\nM\nA\nA\nA\nA\nA\n",
                0,
                999,
            ],
            '0\'\' II' => [
                "--- input.txt\n+++ output.txt\n@@ -12,2 +12,2 @@\n-X\n-M\n\\ No newline at end of file\n+Y\n+M\n",
                "A\nB\nC\nD\nE\nF\nG\nH\nI\nJ\nK\nX\nM",
                "A\nB\nC\nD\nE\nF\nG\nH\nI\nJ\nK\nY\nM\n",
                0,
            ],
            '0\'\'\' II' => [
                "--- input.txt\n+++ output.txt\n@@ -12,2 +12,2 @@\n-X\n-X1\n+Y\n+Y2\n",
                "A\nB\nC\nD\nE\nF\nG\nH\nI\nJ\nK\nX\nX1\nM\nA\nA\nA\nA\nA\n",
                "A\nB\nC\nD\nE\nF\nG\nH\nI\nJ\nK\nY\nY2\nM\nA\nA\nA\nA\nA\n",
                0,
                999,
            ],
            '1 II' => [
                "--- input.txt\n+++ output.txt\n@@ -11,3 +11,3 @@\n K\n-X\n+Y\n M\n",
                "A\nB\nC\nD\nE\nF\nG\nH\nI\nJ\nK\nX\nM\n",
                "A\nB\nC\nD\nE\nF\nG\nH\nI\nJ\nK\nY\nM\n",
                1,
            ],
            '5 II' => [
                "--- input.txt\n+++ output.txt\n@@ -7,7 +7,7 @@\n G\n H\n I\n J\n K\n-X\n+Y\n M\n",
                "A\nB\nC\nD\nE\nF\nG\nH\nI\nJ\nK\nX\nM\n",
                "A\nB\nC\nD\nE\nF\nG\nH\nI\nJ\nK\nY\nM\n",
                5,
            ],
            [
                '--- input.txt
+++ output.txt
@@ -1,28 +1,28 @@
 A
-X
+B
 V
-Y
+D
 1
 A
 2
 A
 3
 A
 4
 A
 8
 A
 9
 A
 5
 A
 A
 A
 A
 A
 A
 A
 A
 A
 A
 A
',
                "A\nX\nV\nY\n1\nA\n2\nA\n3\nA\n4\nA\n8\nA\n9\nA\n5\nA\nA\nA\nA\nA\nA\nA\nA\nA\nA\nA\n",
                "A\nB\nV\nD\n1\nA\n2\nA\n3\nA\n4\nA\n8\nA\n9\nA\n5\nA\nA\nA\nA\nA\nA\nA\nA\nA\nA\nA\n",
                9999,
                99999,
            ],
        ];
    }

    /**
     * Returns a new instance of a Differ with a new instance of the class (DiffOutputBuilderInterface) under test.
     *
     * @param array $options
     *
     * @return Differ
     */
    private function getDiffer(array $options = []): Differ
    {
        return new Differ(new StrictUnifiedDiffOutputBuilder($options));
    }
}
