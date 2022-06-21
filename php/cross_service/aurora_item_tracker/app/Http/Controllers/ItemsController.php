<?php

namespace App\Http\Controllers;

use App\Models\Item;
use Aws\SesV2\SesV2Client;
use Illuminate\Http\Request;
use Aws\Laravel\AwsFacade as AWS;

class ItemsController extends Controller
{

    protected Item $item;

    public function __construct(Item $item)
    {
        $this->item = $item;
        parent::__construct();
    }

    /**
     * Display a listing of the resource.
     *
     */
    public function index(string $state = null)
    {
        return $this->item->getItemsByState($state);
    }

    /**
     * Store a newly created resource in storage.
     *
     * @param Request $request
     * @return string JSON
     */
    public function store(Request $request): string
    {
        return $this->item->storeItem($request->input());
    }

    /**
     * Set a work item to the archived state.
     *
     * @param $itemId
     * @return string
     */
    public function archive($itemId)
    {
        return $this->item->archiveItem($itemId);
    }

    /**
     * Send a summary of the selected state to the email provided.
     *
     * @param Request $request
     * @return \Aws\Result
     */
    public function report(Request $request)
    {
        /** @var SesV2Client $sesClient */
        $sesClient = AWS::createClient('sesv2');

        $email = $request->input('email');

        $reportData = $this->item->getItemsByState($request->input('status'));
        return $sesClient->sendEmail([
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
