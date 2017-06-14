package in.ac.iiti.gymakhanaiiti.activity;

import android.app.ProgressDialog;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.ac.iiti.gymakhanaiiti.AppController;
import in.ac.iiti.gymakhanaiiti.R;
import in.ac.iiti.gymakhanaiiti.User.AuthenticationHeader;
import in.ac.iiti.gymakhanaiiti.models.Comment;
import in.ac.iiti.gymakhanaiiti.models.Post;
import in.ac.iiti.gymakhanaiiti.other.Methods;
import in.ac.iiti.gymakhanaiiti.other.Vars;

public class CommentActivity extends AppCompatActivity {

    String TAG = Vars.globalTag;
    private TextView tootlBarTitleTv;
    private TextView commentContextTv;
    private EditText commentBodyET;

    private String commentContext = "";

    private int postId;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        if(getIntent().hasExtra("todo"))
        {
            commentContext  = "";

            if(getIntent().getExtras().getString("todo").equals("commentOnPost"))
            {
                Post post = Post.getCurrentPost();
                postId = post.getPostId();
                // making context for the comment which would look like  "Ankit Gaur.... Gymkhana  App finally launched"
                commentContext = "<b><i>"+post.getAuthorName()+"</i></b>....";
                if(post.getTitle()!=null)
                {
                    commentContext  =  commentContext+post.getTitle();
                }
                if(post.getBody()!=null)
                {
                    commentContext = commentContext+ ".."+post.getBody();
                }

            }else if(getIntent().getExtras().getString("todo").equals("replyOnComment"))
            {
                tootlBarTitleTv.setText("Reply");
                Comment comment = Comment.getCurrentComment();
                postId = comment.getCommentId();
                commentContext = "<b><i>"+comment.getAuthorName()+"</i></b>..."+comment.getCommentContent();
            }else{
                finish();
            }


        }else{
            //if nothing found in extras then finish;
            finish();
        }

        initViews();

    }
    private void initViews()
    {
        progressDialog  = new ProgressDialog(this);

        tootlBarTitleTv = (TextView)findViewById(R.id.comment_reply_toolbar_TV);
        commentContextTv = (TextView)findViewById(R.id.context_content_tv);
        commentBodyET = (EditText)findViewById(R.id.comment_body_ET);

        Log.d(TAG,"context comment "+commentContext);
        commentContextTv.setText(Methods.fromHtml(commentContext));

    }
    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.return_back_button:
                finish();
                break;
            case R.id.send_comment_button:
                 uploadComment();
                break;
        }
    }
    public void uploadComment()
    {
        if(commentBodyET.getText()==null||commentBodyET.getText().length()==0)
        {
            return;
        }

        progressDialog.setMessage("Sending Comment...");
        final String commentContent = commentBodyET.getText().toString();

        progressDialog.show();
        String url = Vars.AddCommentUrl+postId;
        StringRequest addCommentRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.hide();
                Log.d(TAG,"Comment upload response :"+response);
                try{
                    JSONObject jsonresponse = new JSONObject(response);
                    boolean isError = jsonresponse.getBoolean(Vars.KeyError);
                    if(isError)
                    {
                        Toast.makeText(CommentActivity.this,"Comment couldn't be sent.",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(CommentActivity.this,"Comment Uploaded Successfully.",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }catch (JSONException e)
                {
                    Toast.makeText(CommentActivity.this,"Comment couldn't be sent.",Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"comment uploaded");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                   progressDialog.hide();
                   Toast.makeText(CommentActivity.this,"Comment couldn't be sent.",Toast.LENGTH_SHORT).show();
                   String errorLog = "Comment Upload Error :";
                   if(error.getMessage()!=null)
                   {
                       errorLog = errorLog + error.getMessage();
                   }
                errorLog = errorLog + error.networkResponse.statusCode;
                   Log.d(TAG,errorLog);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String,String>();
                params.put(Vars.KeyCommentContent,commentContent);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthenticationHeader.getAuthenticationHeader(Vars.authKey);
            }
        };
        AppController.getInstance().addToRequestQueue(addCommentRequest,"addComment");
    }
}
