<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;

use App\Http\Requests;
use App\Http\Controllers\Controller;
use User;
use Input;
class HomeController extends Controller
{
    public function transfer(){
    	if(!Input::has('from')){
    		return $this->error('No User to send money');
    	}
    	$user = User::find(Input::get)
    }


    public function error($message){
    	return array('error'=>1,'message'=>$message);
    }
}
