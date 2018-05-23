package com.AppDriver.floatingService;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.AppDriver.Activity.MainActivity;
import com.AppDriver.R;


public class FloatingService extends Service {
    private WindowManager mWindowManager;
    private View mFloatingView;

    public FloatingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        int LAYOUT_FLAG;

        //Inflate the floating view layout we created
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.floatingview, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                PixelFormat.TRANSLUCENT);

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 800;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

//The root element of the collapsed view layout
//        final View collapsedView = mFloatingView.findViewById(R.id.collapse_view);
//        //The root element of the expanded view layout
//        final View expandedView = mFloatingView.findViewById(R.id.expanded_container);

        //Set the close button on floating widget
//        ImageView closeButtonCollapsed = (ImageView) mFloatingView.findViewById(R.id.close_btn);
//        closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //close the service and remove the from from the window
//                stopSelf();
//            }
//        });
        //display typed message.
//        final TextView displaytext = (TextView) mFloatingView.findViewById(R.id.displaytext);
//
//        // while floating view is expanded.
//        //type message.
//        final EditText typetext = (EditText) mFloatingView.findViewById(R.id.edittext);
//        // Request focus and show soft keyboard automatically


//        ImageView senttext = (ImageView) mFloatingView.findViewById(R.id.sendText);
//        senttext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.e("float ", " image send click");
//                displaytext.setText(typetext.getText().toString());
//                displaytext.setVisibility(View.VISIBLE);
//            }
//        });


        //Set the close button on expanded view
//        ImageView closeButton = (ImageView) mFloatingView.findViewById(R.id.close_exp_button);
//        closeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                collapsedView.setVisibility(View.VISIBLE);
//                expandedView.setVisibility(View.GONE);
//            }
//        });


        //Open the application on open button click on expanded view
//        ImageView openButton = (ImageView) mFloatingView.findViewById(R.id.open_exp_button);
//        openButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // open main activity
////                Intent intent = new Intent(FloatingService.this, MainActivity.class);
////                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                startActivity(intent);
//
//
//                //close the service and remove view from the view hierarchy
//                stopSelf();
//            }
//        });

        //Drag and move floating view using user's touch action.
        mFloatingView.findViewById(R.id.root_container).
                setOnTouchListener(new View.OnTouchListener() {
                    private int initialX;
                    private int initialY;
                    private float initialTouchX;
                    private float initialTouchY;


                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);
                            }
                        });
//                switch (event.getAction()) {
//                    case v.:

//                        //remember the initial position.
//                        initialX = params.x;
//                        initialY = params.y;
//
//                        //get the touch location
//                        initialTouchX = event.getRawX();
//                        initialTouchY = event.getRawY();
//                        return true;
//                    case MotionEvent.ACTION_UP:
//                        int Xdiff = (int) (event.getRawX() - initialTouchX);
//                        int Ydiff = (int) (event.getRawY() - initialTouchY);
//
//
//                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
//                        //So that is click event.
//                        if (Xdiff < 10 && Ydiff < 10) {
//                            if (isViewCollapsed()) {
//                                //When user clicks on the image view of the collapsed layout,
//                                //visibility of the collapsed layout will be changed to "View.GONE"
//                                //and expanded view will become visible.
////                                collapsedView.setVisibility(View.GONE);
////                                expandedView.setVisibility(View.VISIBLE);
////                                typetext.requestFocus();

//                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
////                                Intent intent = getPackageManager().getLaunchIntentForPackage("mersattech.bargexp");
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                        startActivity(intent);
                        // attach keypad with edittext
//                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                                imm.showSoftInput(typetext, InputMethodManager.SHOW_IMPLICIT);

//                            }
//                        }
//                        return true;
//                    case MotionEvent.ACTION_MOVE:
//                        //Calculate the X and Y coordinates of the view.
//                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
//                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
//
//
//                        //Update the layout with new X & Y coordinate
//                        mWindowManager.updateViewLayout(mFloatingView, params);
//                        return true;
////                }
//                } return false;
//            }
//        });
                        return false;
                    }
                });
    }

    /**
     * Detect if the floating view is collapsed or expanded.
     *
     * @return true if the floating view is collapsed.
     */
    private boolean isViewCollapsed() {
        return mFloatingView == null || mFloatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
        stopSelf();
    }
}