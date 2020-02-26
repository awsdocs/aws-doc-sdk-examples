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

class RequirementCollection implements \Countable, \IteratorAggregate {
    /**
     * @var Requirement[]
     */
    private $requirements = [];

    public function add(Requirement $requirement) {
        $this->requirements[] = $requirement;
    }

    /**
     * @return Requirement[]
     */
    public function getRequirements() {
        return $this->requirements;
    }

    /**
     * @return int
     */
    public function count() {
        return count($this->requirements);
    }

    /**
     * @return RequirementCollectionIterator
     */
    public function getIterator() {
        return new RequirementCollectionIterator($this);
    }
}
