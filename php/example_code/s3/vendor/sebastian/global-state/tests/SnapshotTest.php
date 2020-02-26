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
use SebastianBergmann\GlobalState\TestFixture\BlacklistedInterface;
use SebastianBergmann\GlobalState\TestFixture\SnapshotClass;
use SebastianBergmann\GlobalState\TestFixture\SnapshotTrait;

/**
 * @covers \SebastianBergmann\GlobalState\Snapshot
 *
 * @uses \SebastianBergmann\GlobalState\Blacklist
 */
final class SnapshotTest extends TestCase
{
    /**
     * @var Blacklist
     */
    private $blacklist;

    protected function setUp(): void
    {
        $this->blacklist = new Blacklist;
    }

    public function testStaticAttributes(): void
    {
        SnapshotClass::init();

        $this->blacklistAllLoadedClassesExceptSnapshotClass();

        $snapshot = new Snapshot($this->blacklist, false, true, false, false, false, false, false, false, false);

        $expected = [
            SnapshotClass::class => [
                'string'  => 'string',
                'objects' => [new \stdClass],
            ],
        ];

        $this->assertEquals($expected, $snapshot->staticAttributes());
    }

    public function testConstructorExcludesAspectsWhenTheyShouldNotBeIncluded(): void
    {
        $snapshot = new Snapshot(
            $this->blacklist,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false
        );

        $this->assertEmpty($snapshot->constants());
        $this->assertEmpty($snapshot->functions());
        $this->assertEmpty($snapshot->globalVariables());
        $this->assertEmpty($snapshot->includedFiles());
        $this->assertEmpty($snapshot->iniSettings());
        $this->assertEmpty($snapshot->interfaces());
        $this->assertEmpty($snapshot->staticAttributes());
        $this->assertEmpty($snapshot->superGlobalArrays());
        $this->assertEmpty($snapshot->superGlobalVariables());
        $this->assertEmpty($snapshot->traits());
    }

    public function testBlacklistCanBeAccessed(): void
    {
        $snapshot = new Snapshot(
            $this->blacklist,
            false,
            false,
            true,
            false,
            false,
            false,
            false,
            false,
            false
        );

        $this->assertSame($this->blacklist, $snapshot->blacklist());
    }

    public function testConstants(): void
    {
        $snapshot = new Snapshot($this->blacklist, false, false, true, false, false, false, false, false, false);

        $this->assertArrayHasKey('GLOBALSTATE_TESTSUITE', $snapshot->constants());
    }

    public function testFunctions(): void
    {
        $snapshot  = new Snapshot($this->blacklist, false, false, false, true, false, false, false, false, false);
        $functions = $snapshot->functions();

        $this->assertContains('sebastianbergmann\globalstate\testfixture\snapshotfunction', $functions);
        $this->assertNotContains('assert', $functions);
    }

    public function testClasses(): void
    {
        $snapshot = new Snapshot($this->blacklist, false, false, false, false, true, false, false, false, false);
        $classes  = $snapshot->classes();

        $this->assertContains(TestCase::class, $classes);
        $this->assertNotContains(Exception::class, $classes);
    }

    public function testInterfaces(): void
    {
        $this->blacklist->addClass(BlacklistedInterface::class);

        $snapshot   = new Snapshot($this->blacklist, false, false, false, false, false, true, false, false, false);
        $interfaces = $snapshot->interfaces();

        $this->assertContains(BlacklistedInterface::class, $interfaces);
        $this->assertNotContains(\Countable::class, $interfaces);
    }

    public function testTraits(): void
    {
        \spl_autoload_call('SebastianBergmann\GlobalState\TestFixture\SnapshotTrait');

        $snapshot = new Snapshot($this->blacklist, false, false, false, false, false, false, true, false, false);

        $this->assertContains(SnapshotTrait::class, $snapshot->traits());
    }

    public function testIniSettings(): void
    {
        $snapshot    = new Snapshot($this->blacklist, false, false, false, false, false, false, false, true, false);
        $iniSettings = $snapshot->iniSettings();

        $this->assertArrayHasKey('date.timezone', $iniSettings);
        $this->assertEquals('Etc/UTC', $iniSettings['date.timezone']);
    }

    public function testIncludedFiles(): void
    {
        $snapshot = new Snapshot($this->blacklist, false, false, false, false, false, false, false, false, true);
        $this->assertContains(__FILE__, $snapshot->includedFiles());
    }

    private function blacklistAllLoadedClassesExceptSnapshotClass(): void
    {
        foreach (\get_declared_classes() as $class) {
            if ($class === SnapshotClass::class) {
                continue;
            }

            $this->blacklist->addClass($class);
        }
    }
}
