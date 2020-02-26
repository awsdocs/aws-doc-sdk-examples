<?php
/*
 * This file is part of sebastian/comparator.
 *
 * (c) Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
namespace SebastianBergmann\Comparator;

/**
 * Factory for comparators which compare values for equality.
 */
class Factory
{
    /**
     * @var Factory
     */
    private static $instance;

    /**
     * @var Comparator[]
     */
    private $customComparators = [];

    /**
     * @var Comparator[]
     */
    private $defaultComparators = [];

    /**
     * @return Factory
     */
    public static function getInstance()
    {
        if (self::$instance === null) {
            self::$instance = new self; // @codeCoverageIgnore
        }

        return self::$instance;
    }

    /**
     * Constructs a new factory.
     */
    public function __construct()
    {
        $this->registerDefaultComparators();
    }

    /**
     * Returns the correct comparator for comparing two values.
     *
     * @param mixed $expected The first value to compare
     * @param mixed $actual   The second value to compare
     *
     * @return Comparator
     */
    public function getComparatorFor($expected, $actual)
    {
        foreach ($this->customComparators as $comparator) {
            if ($comparator->accepts($expected, $actual)) {
                return $comparator;
            }
        }

        foreach ($this->defaultComparators as $comparator) {
            if ($comparator->accepts($expected, $actual)) {
                return $comparator;
            }
        }
    }

    /**
     * Registers a new comparator.
     *
     * This comparator will be returned by getComparatorFor() if its accept() method
     * returns TRUE for the compared values. It has higher priority than the
     * existing comparators, meaning that its accept() method will be invoked
     * before those of the other comparators.
     *
     * @param Comparator $comparator The comparator to be registered
     */
    public function register(Comparator $comparator)
    {
        \array_unshift($this->customComparators, $comparator);

        $comparator->setFactory($this);
    }

    /**
     * Unregisters a comparator.
     *
     * This comparator will no longer be considered by getComparatorFor().
     *
     * @param Comparator $comparator The comparator to be unregistered
     */
    public function unregister(Comparator $comparator)
    {
        foreach ($this->customComparators as $key => $_comparator) {
            if ($comparator === $_comparator) {
                unset($this->customComparators[$key]);
            }
        }
    }

    /**
     * Unregisters all non-default comparators.
     */
    public function reset()
    {
        $this->customComparators = [];
    }

    private function registerDefaultComparators()
    {
        $this->registerDefaultComparator(new MockObjectComparator);
        $this->registerDefaultComparator(new DateTimeComparator);
        $this->registerDefaultComparator(new DOMNodeComparator);
        $this->registerDefaultComparator(new SplObjectStorageComparator);
        $this->registerDefaultComparator(new ExceptionComparator);
        $this->registerDefaultComparator(new ObjectComparator);
        $this->registerDefaultComparator(new ResourceComparator);
        $this->registerDefaultComparator(new ArrayComparator);
        $this->registerDefaultComparator(new DoubleComparator);
        $this->registerDefaultComparator(new NumericComparator);
        $this->registerDefaultComparator(new ScalarComparator);
        $this->registerDefaultComparator(new TypeComparator);
    }

    private function registerDefaultComparator(Comparator $comparator)
    {
        $this->defaultComparators[] = $comparator;

        $comparator->setFactory($this);
    }
}
