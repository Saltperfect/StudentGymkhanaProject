package in.ac.iiti.gymakhanaiiti.models;

import java.util.ArrayList;

import in.ac.iiti.gymakhanaiiti.other.Vars;

/**
 * Created by ankit on 9/2/17.
 */

public class Post {
    private int postId;
    private String title;
    private String body;
    private int authorId;
    private String authorName;
    private String authorEmail;
    private String authorprofileUrl;
    private String timeStamp;
    private int  clientVoteValue  = 0;//0 or 1;
    private int numVotes;
    private int numComments;
    private int numViews;
    private ArrayList<String> imageUrls = new ArrayList<String>();

    private static Post currentPost;


    /**
     *
     * @param postId every post has a unique id
     * @param title title of the post if any as it is to be shown in large fonts
     * @param body  body of the post containing normal, bold and italic text.
     * @param numVotes total number of the votes for this post;
     * @param clientVoteVaulue vote value to the this post by user viewing the post , value can be 0 and 1 respectively;
     * @param authorId  id of the student who posted this post;
     * @param authorName name of the author of the post
     * @param authorEmail
     * @param timeStamp timestamp showing the difference between current time and the time when it was posted
     */
    public Post(int postId, String title, String body, int numVotes, int clientVoteVaulue,int numComments,int numViews, int authorId, String authorName, String authorEmail, String authorprofileUrl, String timeStamp, ArrayList<String> imageUrls)
    {
        this.postId = postId;
        this.title = title;
        this.body = body;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorEmail = authorEmail;
        this.timeStamp = timeStamp;
        this.numVotes = numVotes;
        this.clientVoteValue = clientVoteVaulue;
        this.imageUrls = imageUrls;
        this.numComments = numComments;
        this.numViews = numViews;
        this.authorprofileUrl = authorprofileUrl;


        if(!authorprofileUrl.contains("http://")&&!authorprofileUrl.contains("www")&&!authorprofileUrl.contains("https://"))
        {
            //if profile url is hosted locally then it won't have host address in it so adding host address;
            this.authorprofileUrl = Vars.HOST + authorprofileUrl;
        }

    }

    public int getPostId() {
        return postId;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public int getAuthorId() {
        return authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public String getAuthorProfileUrl() {
        return authorprofileUrl;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public int getClientVoteValue()
    {
        return clientVoteValue;
    }

    public int getNumVotes() {
        return numVotes;
    }

    public ArrayList<String> getImageUrls() {
        return imageUrls;
    }

    public static Post getCurrentPost() {
        return currentPost;
    }

    public static void setCurrentPost(Post currentPost) {
        Post.currentPost = currentPost;
    }

    public int getNumComments() {
        return numComments;
    }

    public int getNumViews() {
        return numViews;
    }

    public boolean isUpvoted()
    {
        return clientVoteValue==1;
    }
    public void setUpvoteStatus(boolean isUpvotedBool)
    {
        if(isUpvotedBool) {
            if(!isUpvoted()){
                //already not upvoted
                numVotes++;
            }
            clientVoteValue = 1;
        }
        else {
            if(isUpvoted())
            {
                //if already upvoted
                numVotes--;
            }
            clientVoteValue = 0;
        }
        return;
    }
}
