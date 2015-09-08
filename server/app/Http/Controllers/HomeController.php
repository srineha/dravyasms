<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;

use App\Http\Requests;
use App\Http\Controllers\Controller;
use App\User;
use Input;
class HomeController extends Controller
{
    public function transfer(){
    	if(!Input::has('from')){
    		return $this->error('No User to send money');
    	}
    	$user = User::find(Input::get('from'));
    	if(is_null($user))
    		return $this->error('Wrong sender ID');

    	if(!Input::has('to'))
    	{
    		return $this->error('No Sender phone Number');
    	}
    	$to = User::where('phone','=',Input::get('to'))->first();
    	if(is_null($to))
    		return $this->error('Invalid phone number');

    	if(!Input::has('money'))
    	{
    		return $this->error('No Amount specified');
    	}
    	$money = intval(Input::get('money'));

    	if($user->balance < $money)
    		return $this->error('Invalid balance');

    	$to->balance = $to->balance + $money;
    	$to->push();

    	$user->balance = $user->balance - $money;
    	$user->push();

    	return $this->success('Successfully transferred');

    }


    public function error($message){
    	return array('error'=>1,'message'=>$message);
    }

    public function success($message,$data=array()){
    	return array_merge(array('error'=>0,'message'=>$message),$data);
    }
}
