package com.accherniakocich.android.masynbazar;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

public class MainActivity extends AppCompatActivity {

    private AVLoadingIndicatorView avi;

    private Context mContext;
    private Activity mActivity;

    private FrameLayout mRelativeLayout;
    private ImageButton mButtonBack;
    private ImageButton mButtonForward;
    //private ProgressBar progress_bar;
    static WebView mWebView;


    private String mUrl = "http://mashynbazar.biz/";
    private ValueCallback<Uri[]> mFilePathCallback;
    private int PICKFILE_REQUEST_CODE= 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Request window feature action bar
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the application context
        mContext = getApplicationContext();
        // Get the activity
        mActivity = MainActivity.this;

        // Change the action bar color
        /*getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#FFF91EC6"))
        );*/

        // Get the widgets reference from XML layout
        mRelativeLayout = (FrameLayout) findViewById(R.id.rl);
        mWebView = (WebView) findViewById(R.id.web_view);
        mButtonBack = (ImageButton) findViewById(R.id.btn_back);
        mButtonForward = (ImageButton) findViewById(R.id.btn_forward);
        //progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        avi = (AVLoadingIndicatorView)findViewById(R.id.avi);

        // Request to render the web page
        renderWebPage(mUrl);

        // Set a click listener for back button
        mButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                    public boolean canGoBack ()
                        Gets whether this WebView has a back history item.

                    Returns
                        true : iff this WebView has a back history item
                */
                if (mWebView.canGoBack()) {
                    /*
                        public void goBack ()
                            Goes back in the history of this WebView.
                    */
                    mWebView.goBack();

                    // Display the notification
                    //Toast.makeText(mContext,"Go To Back",Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set a click listener for forward button
        mButtonForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                    public boolean canGoForward ()
                        Gets whether this WebView has a forward history item.

                    Returns
                        true : iff this WebView has a forward history item
                */
                if (mWebView.canGoForward()) {
                    /*
                        public void goForward ()
                            Goes forward in the history of this WebView.
                    */
                    mWebView.goForward();

                    // Display the notification
                    //Toast.makeText(mContext,"Go To Forward",Toast.LENGTH_SHORT).show();
                }
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,FileChooserParams fileChooserParams) {
                mFilePathCallback = filePathCallback;
                // Launch Intent for picking file
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, PICKFILE_REQUEST_CODE);
                return true;
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == PICKFILE_REQUEST_CODE) {
            Uri result = intent == null || resultCode != RESULT_OK ? null
                    : intent.getData();
            Uri[] resultsArray = new Uri[1];
            resultsArray[0] = result;
            mFilePathCallback.onReceiveValue(resultsArray);
        }
    }

    // Custom method to render a web page
    protected void renderWebPage(String urlToRender) {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // Do something on page loading started
                mRelativeLayout.setAlpha(.3f);
                //progress_bar.setVisibility(View.VISIBLE);
                avi.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // Do something when page loading finished

                //progress_bar.setVisibility(View.INVISIBLE);
                avi.hide();
                mRelativeLayout.setAlpha(1);

                // Check web view back history availability
                if (mWebView.canGoBack()) {
                    mButtonBack.setEnabled(true);
                } else {
                    mButtonBack.setEnabled(false);
                }

                // Check web view forward history availability
                if (mWebView.canGoForward()) {
                    mButtonForward.setEnabled(true);
                } else {
                    mButtonForward.setEnabled(false);
                }
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int newProgress) {
            }
        });

        // Enable the javascript
        mWebView.getSettings().setJavaScriptEnabled(true);

        // Render the web page
        mWebView.loadUrl(urlToRender);
    }


    /*
        public void onBackPressed ()
            Called when the activity has detected the user's press of the back key. The default
            implementation simply finishes the current activity, but you can override this to
            do whatever you want.
    */
    @Override
    public void onBackPressed() {
        //Toast.makeText(mContext,"Back Key Pressed",Toast.LENGTH_SHORT).show();
        // We also allow to navigate back history by pressing device back key
        if (mWebView.canGoBack()) {
            //Toast.makeText(mContext,"Back History Available",Toast.LENGTH_SHORT).show();
            mWebView.goBack();
        } else {
            //Toast.makeText(mContext,"No Back History Found",Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }
    }
}
