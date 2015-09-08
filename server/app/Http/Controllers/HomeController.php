<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;

use App\Http\Requests;
use App\Http\Controllers\Controller;
use App\User;
use Input;
use Hash;
use DB;
class HomeController extends Controller
{
	// API to transfer money
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

    	DB::table('transfers')->insert(
		    ['from' => $user->id, 'to' => $to->id,'amount'=>$money]
		);
    	return $this->success('Successfully transferred');

    }

    // API to register gcm id
     public function gcm(){
    	if(!Input::has('from')){
    		return $this->error('No User ID');
    	}
    	$user = User::find(Input::get('from'));
    	if(is_null($user))
    		return $this->error('Wrong sender ID');

    	if(!Input::has('gcm')){
    		return $this->error('no gcm');
    	}

    	$user->gcm_id = Input::get('gcm');
    	$user->save();
    	return $this->success('GCM Added');

    }

    // API for login
    public function login(){
    	if(!Input::has('phone')){
    		return $this->error('Phone Number required');
    	}
    	$user = User::where('phone','=',Input::get('phone'))->first();
    	if(is_null($user))
    		return $this->error('Invalid phone number');

    	if(!Input::has('password')){
    		return $this->error('Password required');
    	}

    	if(Hash::check(Input::get('password'),$user->password)){
    		return $this->success('Successfully logged in',$user);
    	}
    	else{
    		return $this->error('Invalid Password');
    	}
    }

    public function user($id){

    	$user = User::find($id);
    	if(is_null($user))
    		return $this->error('Wrong user ID');
    	return $this->success('User data',$user);

    }
    // api to send error
    public function error($message){
    	return array('error'=>1,'message'=>$message);
    }

    // api to success
    public function success($message,$data=array()){
    	return array('error'=>0,'message'=>$message,'data'=>$data);
    }


}
