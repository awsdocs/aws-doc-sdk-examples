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

class BundledComponentCollection implements \Countable, \IteratorAggregate {
    /**
     * @var BundledComponent[]
     */
    private $bundledComponents = [];

    public function add(BundledComponent $bundledComponent) {
        $this->bundledComponents[] = $bundledComponent;
    }

    /**
     * @return BundledComponent[]
     */
    public function getBundledComponents() {
        return $this->bundledComponents;
    }

    /**
     * @return int
     */
    public function count() {
        return count($this->bundledComponents);
    }

    /**
     * @return BundledComponentCollectionIterator
     */
    public function getIterator() {
        return new BundledComponentCollectionIterator($this);
    }
}
