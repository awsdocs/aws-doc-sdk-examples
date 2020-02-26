<?php
/*
 * This file is part of PharIo\Version.
 *
 * (c) Arne Blankerts <arne@blankerts.de>, Sebastian Heuer <sebastian@phpeople.de>, Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

namespace PharIo\Version;

use PHPUnit\Framework\TestCase;

/**
 * @covers \PharIo\Version\OrVersionConstraintGroup
 */
class OrVersionConstraintGroupTest extends TestCase {
    public function testReturnsTrueIfOneConstraintReturnsFalse() {
        $firstConstraint = $this->createMock(VersionConstraint::class);
        $secondConstraint = $this->createMock(VersionConstraint::class);

        $firstConstraint->expects($this->once())
            ->method('complies')
            ->will($this->returnValue(false));

        $secondConstraint->expects($this->once())
            ->method('complies')
            ->will($this->returnValue(true));

        $group = new OrVersionConstraintGroup('foo', [$firstConstraint, $secondConstraint]);

        $this->assertTrue($group->complies(new Version('1.0.0')));
    }

    public function testReturnsTrueIfAllConstraintsReturnsTrue() {
        $firstConstraint = $this->createMock(VersionConstraint::class);
        $secondConstraint = $this->createMock(VersionConstraint::class);

        $firstConstraint->expects($this->once())
            ->method('complies')
            ->will($this->returnValue(true));

        $group = new OrVersionConstraintGroup('foo', [$firstConstraint, $secondConstraint]);

        $this->assertTrue($group->complies(new Version('1.0.0')));
    }

    public function testReturnsFalseIfAllConstraintsReturnsFalse() {
        $firstConstraint = $this->createMock(VersionConstraint::class);
        $secondConstraint = $this->createMock(VersionConstraint::class);

        $firstConstraint->expects($this->once())
            ->method('complies')
            ->will($this->returnValue(false));

        $secondConstraint->expects($this->once())
            ->method('complies')
            ->will($this->returnValue(false));

        $group = new OrVersionConstraintGroup('foo', [$firstConstraint, $secondConstraint]);

        $this->assertFalse($group->complies(new Version('1.0.0')));
    }
}
