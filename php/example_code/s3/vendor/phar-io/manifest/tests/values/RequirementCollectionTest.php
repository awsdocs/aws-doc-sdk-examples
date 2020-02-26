<?php
/*
 * This file is part of PharIo\Manifest.
 *
 * (c) Arne Blankerts <arne@blankerts.de>, Sebastian Heuer <sebastian@phpeople.de>, Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

namespace PharIo\Manifest;

use PharIo\Version\ExactVersionConstraint;
use PHPUnit\Framework\TestCase;

/**
 * @covers \PharIo\Manifest\RequirementCollection
 * @covers \PharIo\Manifest\RequirementCollectionIterator
 *
 * @uses \PharIo\Manifest\PhpVersionRequirement
 * @uses \PharIo\Version\VersionConstraint
 */
class RequirementCollectionTest extends TestCase {
    /**
     * @var RequirementCollection
     */
    private $collection;

    /**
     * @var Requirement
     */
    private $item;

    protected function setUp() {
        $this->collection = new RequirementCollection;
        $this->item       = new PhpVersionRequirement(new ExactVersionConstraint('7.1.0'));
    }

    public function testCanBeCreated() {
        $this->assertInstanceOf(RequirementCollection::class, $this->collection);
    }

    public function testCanBeCounted() {
        $this->collection->add($this->item);

        $this->assertCount(1, $this->collection);
    }

    public function testCanBeIterated() {
        $this->collection->add(new PhpVersionRequirement(new ExactVersionConstraint('5.6.0')));
        $this->collection->add($this->item);

        $this->assertContains($this->item, $this->collection);
    }

    public function testKeyPositionCanBeRetreived() {
        $this->collection->add($this->item);
        foreach($this->collection as $key => $item) {
            $this->assertEquals(0, $key);
        }
    }

}
