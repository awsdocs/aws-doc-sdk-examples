<?php

use Illuminate\Support\Facades\Route;

Route::controller(\App\Http\Controllers\ItemsController::class)->group(function(){
    Route::get('items/', 'index');
    Route::get('items/{state?}', 'index');
    Route::post('items/', 'store');
    Route::put('items/{itemId}', 'archive');
    Route::post('report/', 'report');
});
