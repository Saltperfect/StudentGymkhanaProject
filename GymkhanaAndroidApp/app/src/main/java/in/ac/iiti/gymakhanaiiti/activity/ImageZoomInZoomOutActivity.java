package in.ac.iiti.gymakhanaiiti.activity;

import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import in.ac.iiti.gymakhanaiiti.R;
import in.ac.iiti.gymakhanaiiti.other.Vars;

public class ImageZoomInZoomOutActivity extends Activity implements OnTouchListener
{
    private static final String TAG = "Touch";
    @SuppressWarnings("unused")
    private static final float MIN_ZOOM = 1f,MAX_ZOOM = 1f;

    // These matrices will be used to scale points of the image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    Matrix imageDefaultMatrix = new Matrix();
    private float[] originalMatrixValue = new float[9];

    // The 3 states (events) which the user is trying to perform
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // these PointF objects are used to record the point(s) the user is touching
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    ImageView view;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_zoom_in_zoom_out);
        view  = (ImageView) findViewById(R.id.imageView);

        imageDefaultMatrix = view.getImageMatrix();
        imageDefaultMatrix.getValues(originalMatrixValue);

        if(getIntent().hasExtra("imageUrl"))
        {
            String url = getIntent().getExtras().getString("imageUrl");
            setImage(url);
        } else {
            finish();
        }
        view.setOnTouchListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void setImage(String url)
    {
        Glide.with(this).load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        if (e != null)
                            Log.d(TAG, e.toString());
                        Toast.makeText(ImageZoomInZoomOutActivity.this,"Failed to load image",Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(view);
    }
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if(view.getDrawable()==null)
        {
            return true;
        }

        ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;

        float[] matrixValues = new float[9];
        matrix.getValues(matrixValues);
        float yScale = matrixValues[Matrix.MSCALE_Y];
        float xScale = matrixValues[Matrix.MSCALE_X];
        float currentWidth = xScale*view.getDrawable().getIntrinsicWidth();
        float currentHeight = yScale*view.getDrawable().getIntrinsicHeight();
        float viewWidth = view.getWidth();
        float viewHeight = view.getHeight();
        float globalX = matrixValues[Matrix.MTRANS_X];
        float globalY = matrixValues[Matrix.MTRANS_Y];
        float minGlobalX = viewWidth - currentWidth;
        float minGlobalY = viewHeight - currentHeight;

        // Handle touch events here...

        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:   // first finger down only
                matrix.set(view.getImageMatrix());
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                if(yScale>1){
                mode = DRAG;
                }else{
                    mode =NONE;
                }
                break;

            case MotionEvent.ACTION_UP: // first finger lifted

            case MotionEvent.ACTION_POINTER_UP: // second finger lifted

                if(mode==ZOOM){
                if(yScale<1)
                {
                    RectF drawableRect = new RectF(0, 0, view.getDrawable().getIntrinsicWidth(), view.getDrawable().getIntrinsicHeight());
                    RectF viewRect = new RectF(0, 0, view.getWidth(), view.getHeight());
                    matrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);
                }
                }
                mode = NONE;
                Log.d(TAG, "mode=NONE");
                break;

            case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down

                oldDist = spacing(event);
                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 5f) {

                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM");
                }
                break;

            case MotionEvent.ACTION_MOVE:

                if (mode == DRAG)
                {
                    float offsetx = event.getX() - start.x;
                    float offsety = event.getY() - start.y;
                    matrix.set(savedMatrix);
                    matrix.postTranslate(offsetx,offsety ); // create the transformation in the matrix  of points
                }
                else if (mode == ZOOM)
                {
                    // pinch zooming
                    float newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 5f)
                    {
                        matrix.set(savedMatrix);
                        scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        view.setImageMatrix(matrix); // display the transformation on screen

        return true; // indicate event was handled
    }

    /*
     * --------------------------------------------------------------------------
     * Method: spacing Parameters: MotionEvent Returns: float Description:
     * checks the spacing between the two fingers on touch
     * ----------------------------------------------------
     */

    private float spacing(MotionEvent event)
    {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

    /*
     * --------------------------------------------------------------------------
     * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
     * Description: calculates the midpoint between the two fingers
     * ------------------------------------------------------------
     */

    private void midPoint(PointF point, MotionEvent event)
    {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

}