<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

use App\Http\Controllers\ItemsController;
use Illuminate\Support\Facades\Route;

Route::controller(ItemsController::class)->group(function () {
    Route::get('items/', 'index');
    Route::get('items/{state?}', 'index');
    Route::post('items/', 'store');
    Route::put('items/{itemId}', 'archive');
    Route::post('report/', 'report');
});
