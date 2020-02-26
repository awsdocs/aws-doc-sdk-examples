<?php
/*
 * This file is part of sebastian/code-unit-reverse-lookup.
 *
 * (c) Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
namespace SebastianBergmann\CodeUnitReverseLookup;

use PHPUnit\Framework\TestCase;

/**
 * @covers SebastianBergmann\CodeUnitReverseLookup\Wizard
 */
class WizardTest extends TestCase
{
    /**
     * @var Wizard
     */
    private $wizard;

    protected function setUp(): void
    {
        $this->wizard = new Wizard;
    }

    public function testMethodCanBeLookedUp(): Wizard
    {
        require __DIR__ . '/_fixture/Foo.php';

        $this->assertEquals(
            'Foo::method',
            $this->wizard->lookup(
                __DIR__ . '/_fixture/Foo.php',
                12
            )
        );

        return $this->wizard;
    }

    /**
     * @depends testMethodCanBeLookedUp
     */
    public function testMethodCanBeLookedUp2(Wizard $wizard): void
    {
        require __DIR__ . '/_fixture/Bar.php';

        $this->assertEquals(
            'Bar::method',
            $wizard->lookup(
                __DIR__ . '/_fixture/Bar.php',
                12
            )
        );
    }

    public function testReturnsFilenameAndLineNumberAsStringWhenNotInCodeUnit(): void
    {
        $this->assertEquals(
            'file.php:1',
            $this->wizard->lookup('file.php', 1)
        );
    }
}
