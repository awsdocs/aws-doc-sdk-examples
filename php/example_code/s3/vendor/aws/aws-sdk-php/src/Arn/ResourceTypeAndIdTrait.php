<?php
namespace Aws\Arn;

/**
 * @internal
 */
trait ResourceTypeAndIdTrait
{
    public function getResourceType()
    {
        return $this->data['resource_type'];
    }

    public function getResourceId()
    {
        return $this->data['resource_id'];
    }

    private static function parseResourceTypeAndId(array $data)
    {
        $data['resource_type'] = null;
        $data['resource_id'] = null;
        $length = strlen($data['resource']);
        for ($i = 0; $i < $length; $i++) {
            if (in_array($data['resource'][$i], ['/', ':'])) {
                $data['resource_type'] = substr($data['resource'], 0, $i);
                $data['resource_id'] = substr($data['resource'], $i + 1);
                break;
            }
        }

        return $data;
    }
}