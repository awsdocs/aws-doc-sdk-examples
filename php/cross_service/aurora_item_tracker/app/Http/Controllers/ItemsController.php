<?php

namespace App\Http\Controllers;

use App\Models\Item;
use Aws\Result;
use Aws\SesV2\SesV2Client;
use Illuminate\Http\Request;

class ItemsController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     */
    public function index(Item $item, string $state = null)
    {
        return $item->getItemsByState($state);
    }

    /**
     * Store a newly created resource in storage.
     *
     * @param Request $request
     * @param Item $item
     * @return string JSON
     */
    public function store(Request $request, Item $item): string
    {
        return $item->storeItem($request->input());
    }

    /**
     * Set a work item to the archived state.
     *
     * @param Item $item
     * @param $itemId
     * @return string
     */
    public function archive(Item $item, $itemId)
    {
        return $item->archiveItem($itemId);
    }

    /**
     * Send a summary of the selected state to the email provided.
     *
     * @param Request $request
     * @param SesV2Client $sesV2Client
     * @param Item $item
     * @return Result
     */
    public function report(Request $request, SesV2Client $sesV2Client, Item $item)
    {
        $email = $request->input('email');

        $reportData = $item->getItemsByState($request->input('status'));
        return $sesV2Client->sendEmail([
            'Content' => [
                'Simple' => [
                    'Body' => [
                        'Text' => [
                            'Data' => $reportData,
                        ]
                    ],
                    'Subject' => [
                        'Data' => "Work Items report for $email.",
                    ],
                ],
            ],
            'Destination' => [
                'ToAddresses' => [$email],
            ],
            'FromEmailAddress' => env('EMAIL'),
        ]);
    }
}
