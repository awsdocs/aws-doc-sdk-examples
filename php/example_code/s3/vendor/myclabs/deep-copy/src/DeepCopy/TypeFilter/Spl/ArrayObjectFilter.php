<?php
namespace DeepCopy\TypeFilter\Spl;

use ArrayObject;
use DeepCopy\DeepCopy;
use DeepCopy\TypeFilter\TypeFilter;

/**
 * In PHP 7.4 the storage of an ArrayObject isn't returned as
 * ReflectionProperty. So we deep copy its array copy.
 */
final class ArrayObjectFilter implements TypeFilter
{
    /**
     * @var DeepCopy
     */
    private $copier;

    public function __construct(DeepCopy $copier)
    {
        $this->copier = $copier;
    }

    /**
     * {@inheritdoc}
     */
    public function apply($arrayObject)
    {
        return new ArrayObject(
            $this->copier->copy($arrayObject->getArrayCopy()),
            $arrayObject->getFlags(),
            $arrayObject->getIteratorClass()
        );
    }
}

