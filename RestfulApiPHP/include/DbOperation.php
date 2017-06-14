<?php

class DbOperation
{
    private $con;

    function __construct()
    {
        require_once dirname(__FILE__) . '/DbConnect.php';
        $db = new DbConnect();
        $this->con = $db->connect();
    }

    //Method to register a new student
    public function addStudent($name,$email,$pass){
        if (!$this->isStudentExists($email)) {
            $password = md5($pass);
            $apikey = $this->generateAuthenticationToken();
            $stmt = $this->con->prepare("INSERT INTO students(name, email, password, authentication_token) values(?, ?, ?, ?)");
            if($stmt){  
                $stmt->bind_param("ssss", $name, $email, $password, $apikey);
                $result = $stmt->execute();
                $stmt->close();
                if ($result) {
                    return 0;
                } else {
                    return 1;
                }
            }else{
               echo mysqli_error($this->con);
               return 4; 
           }
       } else {
        return 2;
    }
}

    //Method to let a student log in
public function studentLogin($email,$pass){
    $password = md5($pass);
    $stmt = $this->con->prepare("SELECT * FROM students WHERE email=? and password=?");
    if($stmt){
        $stmt->bind_param("ss",$email,$password);
        $stmt->execute();
        $stmt->store_result();
        $num_rows = $stmt->num_rows;
        $stmt->close();
        return $num_rows>0;
    }else{
        //mysql error
        echo mysqli_error($this->con);
        return false;
    }
}


    //Method to get student details by email
public function getStudent($email){
    $stmt = $this->con->prepare("SELECT * FROM students WHERE email = ?");
    $stmt->bind_param("s",$email);
    $stmt->execute();
    $student = $stmt->get_result()->fetch_assoc();
    $stmt->close();
    return $student;
}
    //get student by student_id;
public function getStudentById($id){
    $stmt = $this->con->prepare("Select * from students where student_id = ?");
    $stmt->bind_param("i",$id);
    $stmt->execute();
    $student = $stmt->get_result()->fetch_assoc();
    $stmt->close();
    return $student;
}
    //Method to get student Id by authentication token;
public function getStudentId($auth_key){
    $stmt = $this->con->prepare("SELECT * FROM students WHERE authentication_token = ?");
    $stmt->bind_param("s",$auth_key);
    $stmt->execute();
    $student = $stmt->get_result()->fetch_assoc();
    $stmt->close();
    return $student['student_id'];
}

    //Method to get all students from database
public function getAllStudents(){
    $stmt = $this->con->prepare("SELECT * FROM students");
    $stmt->execute();
    $students = $stmt->get_result();
    $stmt->close();
    return $students;
}

    //Method to create a post 
public function createPost($studentid,$post_topic,$post_content,$post_image_id){

    $stmt = $this->con->prepare("INSERT INTO posts (student_id, post_topic, post_content, post_image_id,time_stamp) VALUES (?,?,?,?,?)");
    $timeInSecond = time();
    $stmt->bind_param("issii",$studentid,$post_topic,$post_content,$post_image_id,$timeInSecond);
    $result = $stmt->execute();
    $stmt->close();
    if($result){
        return true;
    }
    return false;
}

     //Method to update post
    /**
     * we will create a table to store the previous versions of our post 
     * in case we need it at some point of time.
     */
    public function updatePost($postId,$newContent)
    {  

         //first moving the original post to post_bin before updating
        copyPost($postId);
        //now update the post;
        $query = "UPDATE posts SET post_content = ? WHERE post_id = ?";
        $stmt = $this->con->prepare($query);
        $stmt->bind_param("si",$newContent,$postId);
        $result = $stmt->execute();
        $stmt->close();
        return $result;
    }

    

    //Delete a post ,  also move the post in post_bin in case needed
    public function deletePost($postId,$time)
    {
        //first move the post in post_bin
       $this->copyPost($postId,$time);
        //now delete the row
        $query = "DELETE FROM posts WHERE post_id = ?";
        $stmt = $this->con->prepare($query);
        echo $this->con->error;
        $stmt->bind_param("i",$postId);
        echo $this->con->error;
        $result = $stmt->execute();
        echo $stmt->error;
        $stmt -> close();
        return $result;
    }
    
    //method to copy post from posts to post_bin
    public function copyPost($id,$time)
    {

       $post = $this->getPost($id);
       //extract($post);
       $post_id = $post['post_id'];
       $auther = $post['student_id'];
       $post_topic = $post['post_topic'];
       $post_content = $post['post_content'];
    

       $query = "INSERT INTO post_bin (student_id, post_topic , post_content, original_post_id,update_time_stamp) VALUES (?,?,?,?,?)";
       $stmt = $this->con->prepare($query);
       $stmt->bind_param('issii',$auther,$post_topic,$post_content,$post_id,$time);
       $result = $stmt->execute();
       $stmt->close();
       return $result;
       //complete this function
    }

    //Method to get all posts ids of particular topic
    public function getAllPostIds($topic)
    {
        $query = "SELECT post_id from posts where post_topic = ? ORDER BY post_id DESC";
        $stmt = $this->con->prepare($query);
        $stmt->bind_param("s",$topic);
        $stmt->execute();
        $ids = $stmt->get_result();
        $stmt->close();
        return $ids;
    }

     //Method to get a post using its id
    public function getPost($id)
    {
        $query = "SELECT * from posts where post_id = ? ";
        $stmt = $this->con->prepare($query);
        $stmt->bind_param("i",$id);
        $stmt->execute();
        $post = $stmt->get_result()->fetch_assoc();
        $stmt->close();
        return $post;
    }
      //add an action with action type as comment or like
     public function addAction($post_id,$student_id,$action_type,$action_content,$time_stamp)
     {
         $stmt = $this->con->prepare("INSERT INTO actions (post_id,student_id,action_type,action_content,time_stamp) VALUES (?,?,?,?,?)");
         $stmt->bind_param("iissi",$post_id,$student_id,$action_type,$action_content,$time_stamp);
         $stmt->execute();
         $stmt->close();
     } 

    // method to save image 
    public function saveImage($studentId,$stringImage,$filepath,$timeStamp)
    {
        $stmt = $this->con->prepare("INSERT INTO images ( student_id, string_image, image_path, time_stamp) VALUES (?,?,?,?)");
        echo $this->con->error;
        $stmt->bind_param("isss",$studentId,$stringImage,$filepath,$timeStamp);
        $stmt->execute();
      //  echo $stmt->error;
        $stmt->close();

    }

    //Method to check the student email already exist or not
    private function isStudentExists($email) {
        $stmt = $this->con->prepare("SELECT student_id from students WHERE email = ?");
        if($stmt){
            $stmt->bind_param("s", $email);
            $stmt->execute();
            $stmt->store_result();
            $num_rows = $stmt->num_rows;
            $stmt->close();
            return $num_rows > 0;
        }else {
         echo mysqli_error($this->con); echo "cant process query";
         return true;
     } 

 }


    //Checking the student is valid or not by authentication token
 public function isValidStudent($auth_key) {
    $stmt = $this->con->prepare("SELECT student_id from students WHERE authentication_token = ?");
    $stmt->bind_param("s", $auth_key);
    $stmt->execute();
    $stmt->store_result();
    $num_rows = $stmt->num_rows;
    $stmt->close();
    return $num_rows > 0;
}

    //Method to generate a unique authentication token every time
private function generateAuthenticationToken(){
    return md5(uniqid(rand(), true));
}
}