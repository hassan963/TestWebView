package hassan.testwebview;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private ImageView imageView;

    FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView  =  findViewById(R.id.webView);

        initRemoteConfig();

        initWebView();

    }

    private void initRemoteConfig(){

        imageView = findViewById(R.id.imageView);
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_default);

        // cacheExpirationSeconds is set to cacheExpiration here, indicating the next fetch request
        // will use fetch data from the Remote Config service, rather than cached parameter values,
        // if cached parameter values are more than cacheExpiration seconds old.
        // See Best Practices in the README for more information.
        long cacheExpiration = 0;
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Fetch Succeeded",
                                    Toast.LENGTH_SHORT).show();

                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            Toast.makeText(MainActivity.this, "Fetch Failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                        displayImage();
                    }
                });



    }

    private void displayImage() {
        String url = mFirebaseRemoteConfig.getString("image");
        Glide.with(this)
                .load(url)
                .into(imageView);
    }

    private void initWebView(){
        String url = "https://coderwall.com/p/gv4kpg/javascript-to-java-bridge";
        final String js = "javascript:"
                + "function () {"
                + "   var element = document.getElementsByClassName('mt4 mb2 center');"
                + "   element[0].parentNode.remove(element[0]);"
                + "} ();";
        // Enable Javascript

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());

        //set the WebViewClient before calling loadUrl
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                //webView.loadUrl("javascript:var con = document.getElementsByClassName('mt4 mb2 center'); " +
                //   "con.parentNode.removeChild(con); ");

                webView.loadUrl("javascript:(function() { " +
                        "document.getElementsByClassName('mt4 mb2 center')[0].style.display=\"none\"; " +
                        "})()");

            }

        });

        webView.loadUrl(url);

        //"webView.loadUrl(\"javascript:var con = document.getElementById('a'); \" +\n" +
        //                        "                \"con.parentNode.removeChild(con); \");"
    }

    //https://inducesmile.com/android/android-firebase-remote-config-example/

    //https://medium.com/mindorks/firebase-remote-config-on-android-55a27f578505
}
