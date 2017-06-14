<?php

//including the required files
require_once '../include/DbOperation.php';
require '.././libs/Slim/Slim.php';

\Slim\Slim::registerAutoloader();
/* *
 * Thanks to simplifiedCoding.com for Rest API tutorial.
 * Created by Ankit Gaur on 03/02/17. 
 * send autherization key as 'Autherization' header
 * */
$app = new \Slim\Slim();
$student_id = null;
/**
 * authenticating user with api key for every request;
 */
function authenticate(\Slim\Route $route) {
    // Getting request headers
    $headers = apache_request_headers();
    $response = array();
    $app = \Slim\Slim::getInstance();

    // Verifying Authorization Header
    if (isset($headers['Authorization'])) {
        $db = new DbOperation();

        // get the api key
        $auth_key = $headers['Authorization'];
        // validating api key
        if (!$db->isValidStudent($auth_key)) {
            // api key is not present in users table
            $response["error"] = true;
            $response["message"] = "Access Denied. Invalid Authentication key";
            echoRespnse(401, $response);
            $app->stop();
        } else {
            global $student_id;
            // get user primary key id
            echo "authenticated"; //delete this *********;

            $student_id = $db->getStudentId($auth_key);
        }
    } else {
        // api key is missing in header
        $response["error"] = true;
        $response["message"] = "Why don't you try with Api key?!";
        echoRespnse(400, $response);
        $app->stop();
    }
}

/* *
 * URL: http://localhost/StudentGymkhanaApp/v1/addStudent
 * Params: name, email, password
 * Method: POST
 * */
$app->post('/addstudent', function () use ($app) {
    verifyRequiredParams(array('name', 'email', 'password'));
    $response = array();
    $name = $app->request->post('name');
    $email = $app->request->post('email');
    $password = $app->request->post('password');
    $db = new DbOperation();
    $res = $db->addStudent($name, $email, $password);
    if ($res == 0) {
        $response["error"] = false;
        $response["message"] = "You are successfully registered";
        echoResponse(201, $response);
    } else if ($res == 1) {
        $response["error"] = true;
        $response["message"] = "Oops! An error occurred while registereing";
        echoResponse(200, $response);
    } else if ($res == 2) {
        $response["error"] = true;
        $response["message"] = "Sorry, this student  already existed";
        echoResponse(200, $response);
    }
});

/* *
 * URL: http://localhost/StudentGymkhanaApp/v1/studentlogin
 * Params: email, password
 * Method: POST
 * */
$app->post('/studentlogin', function () use ($app) {
    verifyRequiredParams(array('email', 'password'));
    $email = $app->request->post('email');
    $password = $app->request->post('password');
    $db = new DbOperation();
    $response = array();
    if ($db->studentLogin($email, $password)) {
        $student = $db->getStudent($email);
        $response['error'] = false;
        $response['id'] = $student['student_id'];
        $response['name'] = $student['name'];
        $response['email'] = $student['email'];
        $response['authentication_key'] = $student['authentication_token'];
    } else {
        $response['error'] = true;
        $response['message'] = "Invalid email or password";
    }
    echoResponse(200, $response);
});

/**
* URL : htttp://localhost/StudentGymkhanaApp/v1/createpost
* Header : auth_token
* Params : email, post_topic, post_content,image
* post_topic can be 'general' , 'mess' , 'prog_club' etc handle accordingly
*/
$app->post('/createpost','authenticate', function() use ($app){
 verifyRequiredParams(array('post_topic','post_content','post_image_id'));

 $post_topic = $app->request->post('post_topic');
 $post_content = $app->request->post('post_content');
 $post_image_id = $app->request->post('post_image_id');

 $db = new DbOperation();

 $response = array();

 global $student_id;

 if($db->createPost($student_id,$post_topic,$post_content,$post_image_id)){
    $response['error'] = false;
    $response['message'] = "Post created successfully";
}else{
    $response['error'] = true;
    $response['message'] = "Could not create post";
}
echoResponse(200,$response);
});




/**
 * URL :/updatepost
 * METHDO : POST
 * params : post_id, new_content, new_image,
 * will create new post and will add a reference in original post and set has_update of original post = 1 show that will not be show in future but its good to store all the logs;
 */
//design architecture to add images;
$app->post('/updatepost','authenticate',function() use($app){
    verifyRequiredParams(array('post_id','new_content'));
    
    global $student_id;

    $db = new DbOperation;

    $response = array();
    
    $postId = $app->request->post('post_id');
    $post_content = $app->request->post('new_content');

    $orginalPost = $db->getPost($postId);
    //checking if post exists or not.
    if($orginalPost==null)
    {
        $response['error'] = true; 
        $response['message'] = "Post doesn't exist in database";
        echoResponse(404,$response);
        return;
    }    

    $autherId = $orginalPost['student_id'];

    //if someone else tries to update the post using his authentication key
    if($autherId!=$student_id){
        $response['error'] = true; 
        $response['message'] = "Don't update other's post. Take care of your own a**.";
        echoResponse(403,$response);
        return; 
    }

    // finally updating the original post
    if($db->updatePost($postId,$post_content))
    {
        $response['error'] = false;
        $response['message'] = "Post Updated Successfully";
        echoResponse(200,$response);
    }else{
     $response['error'] = true;
     $response['message'] = "Post couldn't be updated";
     echoResponse(304,$response);
 }   
});



/** 
 * URL : /getpostids
 * method GET
 * params: none
 * will return all post ids in descending order belonging to a particular topic and then client will download all posts in an asynchronous thread one by one;
 * as reading all the posts at once on server side will take a lot of time;
 * 'postIds' arrray contains the ids
 **/
$app->get('/getpostids/:topic','authenticate',function($topic) use ($app){
    global $student_id;
    
    $response = array();

    $db = new DbOperation();
    
    date_default_timezone_set('Asia/Kolkata');

    //fetch ids;
    $ids = $db->getAllPostIds($topic);
    if($ids!=null)
    {
        $response['error'] = false;
        $response['postIds'] = array();
        while($id = $ids->fetch_assoc())
        { 
         $post_id = $id['post_id'];
         array_push($response['postIds'],$post_id);
     }    
     echoResponse(200,$response); 
 } else{
    $response['error'] = true;
    $response['message'] = "No post for topic ".$topic;
    echoResponse(404,$response);
}    

});

/**
 * URL : /getpost/:id  ,  get a particular post
 * method : GET 
 * params : none
 */
$app->get('/getpost/:id','authenticate',function($id) {

 $db  = new DbOperation();

 $response = array();

     //fetch post using id;

 $post = $db->getPost($id);
 if($post!=null)
 {
    $student_id = $post['student_id'];
    $student  = $db->getStudentById($student_id);
    $student_name  =  $student['name'];

    $response['error'] = false;
    $response['message'] = "Got the post for ya.";
    $response['post_id'] = $id;
    $response['student_id'] = $student_id;
    $response['student_name'] = $student_name;
    $response['post_content'] = $post['post_content'];
        //do something for images;

    echoResponse(200,$response); 
}else{
    $response['error'] = true;
    $response['message'] = "Cannot get the post from db with post id ".$id;
    echoResponse(200,$response);
}   

});

/**
 * URL : /deletepost/:id
 * method : delete
 * param : none 
 * post will be deleted from the post database but it will be put in another table which may be useful in some case;
 */
$app->delete('/deletepost/:id','authenticate',function($id){

    $db  = new DbOperation();

    $response = array();

        global $student_id;  // to confirm that request client is the auther of the post;

        $post  = $db->getPost($id);
        if($post!=null){
          $auther = $post['student_id'];
          if($student_id==$auther) //add a method so that admin can delete any post if found illegal or something in case
          {  
           if($db->deletePost($id,time()))
           {
            $response['error'] = false;
            $response['message'] = "Post deleted successfully with id ".$id;
            echoResponse(200,$response);
        }else{
         $response['error'] = true;
         $response['message'] = "Error, cannot delete the post";
         echoResponse(500,$response);
     }
 }
 else{
   $response['error'] = true;
   $response['message'] = "You are not the auther of this post";
   echoResponse(403,$response);
}  

}else{
    $response['error'] = true;
    $response['message'] = "Post doesn't exit with id ".$id;
    echoResponse(404,$response);
}    
});

/**
 * url: /addaction  ................ action can be comment or like
 * method : post
 * params : post_id, action_type , action_content;
 */
$app->post('/addaction','authenticate', function() use($app){
    verifyRequiredParams(array('post_id','action_type','action_content'));
    global $student_id;

    $db = new DbOperation();

    $response = array();

    $post_id = $app->request->post('post_id');
    $action_type = $app->request->post('action_type');
    $action_content = $app->request->post('action_content');
    $post = $db->getPost($post_id);
    if($post!=null){
        $db->addAction($post_id,$student_id,$action_type,$action_content,time());
        $response['error'] = true;
        $response['message'] = 'Action added successfully.';
        echoResponse(200,$response);
    }
    else{
        $response['error'] = true;
        $response['message'] = "Post doesn't exit with id ".$post_id;
        echoResponse(404,$response);
    }    

});

/**
 * method to post image id;
 *url : /uploadimage
 * method: post
 * params : 'image_str' , 'image_name'
 * image name is first generated on web then sent to client then client updloads it to web
 */
$app->post('/uploadimage','authenticate' , function() use($app){ 
  
  verifyRequiredParams(array('image_str','image_name'));

  global $student_id;
   
  $stringImage = $app->request->post('image_str');

  $imageuniquename = uniqid();//$app->request->post('image_name');

  $response = array();

  $db = new DbOperation();
  
  if(isImage($stringImage))
  {
    $extension = getImageExtension($stringImage); 
    $fname = $imageuniquename;
    $fname = $fname.".".$extension;
    $fpath = "../data/images/".$fname;
    $db->saveImage($student_id,$stringImage,$fpath,time()); // user id, base64 string of image and path and time; 
    $ifp = fopen($fpath, "wb"); 
    $data = explode(',', $stringImage);
    fwrite($ifp, base64_decode($data[1])); 
    fclose($ifp); 

    $response['error'] = false;
    $response['message'] = "Image Uploaded Successfully.";
    echoResponse(200,$response);
  }else{
    $response['error'] = true;
    $response['message'] = "Image is not valid";
    echoResponse(403,$response);
  }

  echo "<image src= $stringImage />"; 
 });



function echoResponse($status_code, $response)
{
    $app = \Slim\Slim::getInstance();
    $app->status($status_code);
    $app->contentType('application/json');
    echo json_encode($response);
}


function verifyRequiredParams($required_fields)
{
    $error = false;
    $error_fields = "";
    $request_params = $_REQUEST;

    if ($_SERVER['REQUEST_METHOD'] == 'PUT') {
        $app = \Slim\Slim::getInstance();
        parse_str($app->request()->getBody(), $request_params);
    }

    foreach ($required_fields as $field) {
        if (!isset($request_params[$field]) || strlen(trim($request_params[$field])) <= 0) {
            $error = true;
            $error_fields .= $field . ', ';
        }
    }

    if ($error) {
        $response = array();
        $app = \Slim\Slim::getInstance();
        $response["error"] = true;
        $response["message"] = 'Required field(s) ' . substr($error_fields, 0, -2) . ' is missing or empty';
        echoResponse(400, $response);
        $app->stop();
    }
}

function isImage($base64)
{
    $mimetype = substr($base64, 5, strpos($base64, ';')-5);
    $first = substr($mimetype, 0,5); // getting first 5 chars if image/extension
    return $first=='image';
}
function getImageExtension($base64)
{
    $mimetype = substr($base64, 5, strpos($base64, ';')-5);
    $second = substr($mimetype, 6,strlen($mimetype)-5); // getting chars after / in  image/extension
    return $second;
}


$app->run();