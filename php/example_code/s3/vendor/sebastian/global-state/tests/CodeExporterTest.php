<?php declare(strict_types=1);
/*
 * This file is part of sebastian/global-state.
 *
 * (c) Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
namespace SebastianBergmann\GlobalState;

use PHPUnit\Framework\TestCase;

/**
 * @covers \SebastianBergmann\GlobalState\CodeExporter
 */
final class CodeExporterTest extends TestCase
{
    /**
     * @runInSeparateProcess
     */
    public function testCanExportGlobalVariablesToCode(): void
    {
        $GLOBALS = ['foo' => 'bar'];

        $snapshot = new Snapshot(null, true, false, false, false, false, false, false, false, false);

        $exporter = new CodeExporter;

        $this->assertEquals(
            '$GLOBALS = [];' . \PHP_EOL . '$GLOBALS[\'foo\'] = \'bar\';' . \PHP_EOL,
            $exporter->globalVariables($snapshot)
        );
    }

    /**
     * @runInSeparateProcess
     */
    public function testCanExportIniSettingsToCode(): void
    {
        $iniSettingName = 'display_errors';
        ini_set($iniSettingName, '1');
        $iniValue = ini_get($iniSettingName);

        $snapshot = new Snapshot(null, false, false, false, false, false, false, false, true, false);

        $exporter = new CodeExporter;
        $export = $exporter->iniSettings($snapshot);

        $pattern = "/@ini_set\(\'$iniSettingName\', \'$iniValue\'\);/";

        $this->assertRegExp(
            $pattern,
            $export
        );
    }

    /**
     * @runInSeparateProcess
     */
    public function testCanExportConstantsToCode(): void
    {
        define('FOO', 'BAR');

        $snapshot = new Snapshot(null, false, false, true, false, false, false, false, false, false);

        $exporter = new CodeExporter;

        $this->assertStringContainsString(
            "if (!defined('FOO')) define('FOO', 'BAR');",
            $exporter->constants($snapshot)
        );
    }
}
