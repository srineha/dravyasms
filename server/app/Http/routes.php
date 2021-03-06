<?php

/*
|--------------------------------------------------------------------------
| Application Routes
|--------------------------------------------------------------------------
|
| Here is where you can register all of the routes for an application.
| It's a breeze. Simply tell Laravel the URIs it should respond to
| and give it the controller to call when that URI is requested.
|
*/

Route::group(['prefix'=>'api'],function(){
	Route::post('transfer','HomeController@transfer');
	Route::post('login','HomeController@login');
	Route::post('gcm','HomeController@gcm');
	Route::any('user/{id}','HomeController@user');
});
