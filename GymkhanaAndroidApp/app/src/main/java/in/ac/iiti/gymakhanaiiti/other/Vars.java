package in.ac.iiti.gymakhanaiiti.other;

/**
 * Created by Ankit Gaur on 1/3/2017.
 */

public class Vars {
    public static final String globalTag = "global";

    // ************** HOST NAME ***************************************
    public static final String HOST  = "http://www.frinkyapps.com";
    public static final String App_Main_Addresss = HOST+ "/StudentApp/v1";

    // ***************** REST service Urls *********************************
    public static final String CreatePostUrl = App_Main_Addresss +"/createpost";
    public static final String GetPostUrl = App_Main_Addresss +"/getpost/"; //this should be followed by postid;
    public static final String GetCommentUrl = App_Main_Addresss +"/getcomment/"; //this should be followed by commentId;
    public static final String GetPostIdsUrl = App_Main_Addresss +"/getpostids/"; //this should be followed by topic name;
    public static final String GetCommmentIdsUrl = App_Main_Addresss+"/getcommentids/"; // followed by post_id;
    public static final String AddCommentUrl = App_Main_Addresss +"/addcomment/"; //followed by post id
    public static final String UploadImageUrl = App_Main_Addresss+ "/uploadimage"; //upload image as bas64 string
    public static final String UploadImageFileUrl = App_Main_Addresss+"/uploadimagefile";
    public static final String AddVotePostUrl = App_Main_Addresss+"/addvote/";  // followed by post id;
    public static final String AddCommentVote = App_Main_Addresss+"/addcommentvote/"; //followed by comment_id;

    //TODO write all required urls here;

    //LogginIn
    public static final String name = "studentName";
    public static final String email = "studentEmail";
    public static final String picUrl = "picUrl";
    public static final String isLoggedIn = "isLoggedIn";
    public static final String isGuest = "isGuest";
    public static final String signOut = "signOut";
    public static final String singIn = "signIn";


    /**
     * Discussion Topic Names
     * These topics name should also be followed by web app developers;
     */
    public static final String TopicGeneralDiscussion = "General Discussion";
    public static final String TopicProgramming = "Programming";
    public static final String TopicRobotics = "Robotics";
    public static final String TopicElectronics  = "Electronics";
    public static final String TopicMyArtWork = "My Art Work";
    public static final String TopicTeachMe = "Teach Me";
    public static final String TopicShare = "Share";
    public static final String TopicMess = "Mess Discussion";
    public static final String TopicLostAndFound = "Lost And Found";

    /**
     * Action variables
     * It has to be same as defined in xml variable to apply switch case when menu is clicked;
     */
    public static final String ActionSavePost = "Save Post";
    public static final String ActionDeletePost = "Delete";
    public static final String ActionEditPost = "Edit Post";
    public static final String ActionReportPost = "Report";
    public static final String ActionLogout ="Logout";
    public static final String ActionRefresh = "Refresh";


    //server response keys , server should send the response with same response keys;
    public static final String KeyError = "error";  // key to know if request response has some error or not
    public static final String KeyMessage = "message";
    public static final String KeyImagePath = "image_path"; //path received after uploading a image;
    public static final String KeyAuthenticationVar = "Authentication-Key";
    public static final String KeyPostId = "post_id";
    public static final String KeyStudentId = "student_id";
    public static final String KeyStudentName = "student_name";
    public static final String KeyStudentMail  = "student_mail";
    public static final String KeyAuthorId = "author_id";
    public static final String KeyAuthorName  = "author_name";
    public static final String KeyAuthorMail = "author_mail";
    public static final String KeyAuthorProfileUrl = "author_profile_url";
    public static final String KeyStudentProfieUrl  = "student_profiel_url";
    public static final String KeyTimeStamp = "time_stamp";
    public static final String KeyPostTopic = "post_topic";
    public static final String KeyPostContent = "post_content";
    public static final String KeyCommentId = "comment_id";
    public static final String KeyCommentIds = "comment_ids";
    public static final String KeyCommentContent = "comment_content";
    public static final String KeyNumComments  = "num_of_comments";
    public static final String KeyNumViews = "num_of_views";
    public static final String KeyNumVotes = "num_of_votes";
    public static final String KeyCurrentUserVoteValue = "user_vote_value";
    public static final String KeyTitleVar = "title";
    public static final String KeyBodyVar = "body";
    public static final String KeyImagesVar = "images";
    public static final String KeyPostids ="postIds";
    public static final String KeyImageStr = "image_str";


    public static final String authKey = "5eb62a7de1043149aff35b8625aaf815";

}
