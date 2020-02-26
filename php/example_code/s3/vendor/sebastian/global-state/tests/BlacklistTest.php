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
use SebastianBergmann\GlobalState\TestFixture\BlacklistedChildClass;
use SebastianBergmann\GlobalState\TestFixture\BlacklistedClass;
use SebastianBergmann\GlobalState\TestFixture\BlacklistedImplementor;
use SebastianBergmann\GlobalState\TestFixture\BlacklistedInterface;

/**
 * @covers \SebastianBergmann\GlobalState\Blacklist
 */
final class BlacklistTest extends TestCase
{
    /**
     * @var \SebastianBergmann\GlobalState\Blacklist
     */
    private $blacklist;

    protected function setUp(): void
    {
        $this->blacklist = new Blacklist;
    }

    public function testGlobalVariableThatIsNotBlacklistedIsNotTreatedAsBlacklisted(): void
    {
        $this->assertFalse($this->blacklist->isGlobalVariableBlacklisted('variable'));
    }

    public function testGlobalVariableCanBeBlacklisted(): void
    {
        $this->blacklist->addGlobalVariable('variable');

        $this->assertTrue($this->blacklist->isGlobalVariableBlacklisted('variable'));
    }

    public function testStaticAttributeThatIsNotBlacklistedIsNotTreatedAsBlacklisted(): void
    {
        $this->assertFalse(
            $this->blacklist->isStaticAttributeBlacklisted(
                BlacklistedClass::class,
                'attribute'
            )
        );
    }

    public function testClassCanBeBlacklisted(): void
    {
        $this->blacklist->addClass(BlacklistedClass::class);

        $this->assertTrue(
            $this->blacklist->isStaticAttributeBlacklisted(
                BlacklistedClass::class,
                'attribute'
            )
        );
    }

    public function testSubclassesCanBeBlacklisted(): void
    {
        $this->blacklist->addSubclassesOf(BlacklistedClass::class);

        $this->assertTrue(
            $this->blacklist->isStaticAttributeBlacklisted(
                BlacklistedChildClass::class,
                'attribute'
            )
        );
    }

    public function testImplementorsCanBeBlacklisted(): void
    {
        $this->blacklist->addImplementorsOf(BlacklistedInterface::class);

        $this->assertTrue(
            $this->blacklist->isStaticAttributeBlacklisted(
                BlacklistedImplementor::class,
                'attribute'
            )
        );
    }

    public function testClassNamePrefixesCanBeBlacklisted(): void
    {
        $this->blacklist->addClassNamePrefix('SebastianBergmann\GlobalState');

        $this->assertTrue(
            $this->blacklist->isStaticAttributeBlacklisted(
                BlacklistedClass::class,
                'attribute'
            )
        );
    }

    public function testStaticAttributeCanBeBlacklisted(): void
    {
        $this->blacklist->addStaticAttribute(
            BlacklistedClass::class,
            'attribute'
        );

        $this->assertTrue(
            $this->blacklist->isStaticAttributeBlacklisted(
                BlacklistedClass::class,
                'attribute'
            )
        );
    }
}
