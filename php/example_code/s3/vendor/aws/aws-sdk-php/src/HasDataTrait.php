<?php
namespace Aws;

/**
 * Trait implementing ToArrayInterface, \ArrayAccess, \Countable, and
 * \IteratorAggregate
 */
trait HasDataTrait
{
    /** @var array */
    private $data = [];

    public function getIterator()
    {
        return new \ArrayIterator($this->data);
    }

    /**
     * This method returns a reference to the variable to allow for indirect
     * array modification (e.g., $foo['bar']['baz'] = 'qux').
     *
     * @param $offset
     *
     * @return mixed|null
     */
    public function & offsetGet($offset)
    {
        if (isset($this->data[$offset])) {
            return $this->data[$offset];
        }

        $value = null;
        return $value;
    }

    public function offsetSet($offset, $value)
    {
        $this->data[$offset] = $value;
    }

    public function offsetExists($offset)
    {
        return isset($this->data[$offset]);
    }

    public function offsetUnset($offset)
    {
        unset($this->data[$offset]);
    }

    public function toArray()
    {
        return $this->data;
    }

    public function count()
    {
        return count($this->data);
    }
}
