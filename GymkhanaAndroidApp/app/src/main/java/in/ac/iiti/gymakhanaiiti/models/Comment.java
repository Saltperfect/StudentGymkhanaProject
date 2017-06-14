package in.ac.iiti.gymakhanaiiti.models;

/**
 * Created by ankit on 18/2/17.
 */

public class Comment {

    private int authorId;
    private int commentId;
    private int numVotes;
    private String authorName;
    private String commentTime;
    private String commentContent;
    private int currentUserVoteValue = 0; //0 or 1 for neutral or upvoted by the current user;
    private static Comment currentComment;

    public Comment(int authorId, int commentId, String authorName, int numVotes, String commentTime, String commentContent, int currentUserVoteValue) {
        this.authorId = authorId;
        this.commentId = commentId;
        this.authorName = authorName;
        this.numVotes = numVotes;
        this.commentTime = commentTime;
        this.commentContent = commentContent;
        this.currentUserVoteValue = currentUserVoteValue;
    }

    public int getAuthorId() {
        return authorId;
    }

    public int getCommentId() {
        return commentId;
    }

    public int getNumUpvotes() {
        return numVotes;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public int getCurrentUserVoteValue() {
        return currentUserVoteValue;
    }

    public void setUpvoteStatus(boolean isUpvotedBool)
    {
        if(isUpvotedBool) {
            if(!isUpvoted()){
                //already not upvoted
                numVotes++;
            }
          currentUserVoteValue = 1;
        }
        else {
            if(isUpvoted())
            {
                //if already upvoted
                numVotes--;
            }
            currentUserVoteValue = 0;
        }
        return;
    }
    public boolean isUpvoted()
    {
        return currentUserVoteValue ==1;
    }

    public static Comment getCurrentComment() {
        return currentComment;
    }

    public static void setCurrentComment(Comment currentComment) {
        Comment.currentComment = currentComment;
    }
}
