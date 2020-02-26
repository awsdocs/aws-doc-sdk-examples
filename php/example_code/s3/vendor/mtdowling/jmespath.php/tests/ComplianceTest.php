<?php
namespace JmesPath\Tests;

use JmesPath\AstRuntime;
use JmesPath\CompilerRuntime;
use JmesPath\SyntaxErrorException;
use PHPUnit\Framework\TestCase;

class ComplianceTest extends TestCase
{
    private static $path;

    public static function setUpBeforeClass()
    {
        self::$path = __DIR__ . '/../../compiled';
        array_map('unlink', glob(self::$path . '/jmespath_*.php'));
    }

    public static function tearDownAfterClass()
    {
        array_map('unlink', glob(self::$path . '/jmespath_*.php'));
    }

    /**
     * @dataProvider complianceProvider
     */
    public function testPassesCompliance(
        $data,
        $expression,
        $result,
        $error,
        $file,
        $suite,
        $case,
        $compiled,
        $asAssoc
    ) {
        $evalResult = null;
        $failed = false;
        $failureMsg = '';
        $failure = '';
        $compiledStr = '';

        try {
            if ($compiled) {
                $compiledStr = \JmesPath\Env::COMPILE_DIR . '=on ';
                $runtime = new CompilerRuntime(self::$path);
            } else {
                $runtime = new AstRuntime();
            }
            $evalResult = $runtime($expression, $data);
        } catch (\Exception $e) {
            $failed = $e instanceof SyntaxErrorException ? 'syntax' : 'runtime';
            $failureMsg = sprintf(
                '%s (%s line %d)',
                $e->getMessage(),
                $e->getFile(),
                $e->getLine()
            );
        }

        $file = __DIR__ . '/compliance/' . $file . '.json';
        $failure .= "\n{$compiledStr}php bin/jp.php --file {$file} --suite {$suite} --case {$case}\n\n"
            . "Result: " . $this->prettyJson($evalResult) . "\n\n"
            . "Expected: " . $this->prettyJson($result) . "\n\n";
        $failure .= 'Associative? ' . var_export($asAssoc, true) . "\n\n";

        if (!$error && $failed) {
            $this->fail("Should not have failed\n{$failure}=> {$failed} {$failureMsg}");
        } elseif ($error && !$failed) {
            $this->fail("Should have failed\n{$failure}");
        }

        $this->assertEquals(
            $this->convertAssoc($result),
            $this->convertAssoc($evalResult),
            $failure
        );
    }

    public function complianceProvider()
    {
        $cases = [];

        $files = array_map(function ($f) {
            return basename($f, '.json');
        }, glob(__DIR__ . '/compliance/*.json'));

        foreach ($files as $name) {
            $contents = file_get_contents(__DIR__ . "/compliance/{$name}.json");
            foreach ([true, false] as $asAssoc) {
                $json = json_decode($contents, true);
                $jsonObj = json_decode($contents);
                foreach ($json as $suiteNumber => $suite) {
                    $given = $asAssoc ? $suite['given'] : $jsonObj[$suiteNumber]->given;
                    foreach ($suite['cases'] as $caseNumber => $case) {
                        $caseData = [
                            $given,
                            $case['expression'],
                            isset($case['result']) ? $case['result'] : null,
                            isset($case['error']) ? $case['error'] : false,
                            $name,
                            $suiteNumber,
                            $caseNumber,
                            false,
                            $asAssoc
                        ];
                        $cases[] = $caseData;
                        $caseData[7] = true;
                        $cases[] = $caseData;
                    }
                }
            }
        }

        return $cases;
    }

    private function convertAssoc($data)
    {
        if ($data instanceof \stdClass) {
            return $this->convertAssoc((array) $data);
        } elseif (is_array($data)) {
            return array_map([$this, 'convertAssoc'], $data);
        } else {
            return $data;
        }
    }

    private function prettyJson($json)
    {
        if (defined('JSON_PRETTY_PRINT')) {
            return json_encode($json, JSON_PRETTY_PRINT);
        }

        return json_encode($json);
    }
}
