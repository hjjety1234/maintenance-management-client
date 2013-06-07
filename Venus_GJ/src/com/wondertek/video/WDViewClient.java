package com.wondertek.video;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WDViewClient extends WebViewClient {


        /**
		 * 
		 */
		private final VenusActivity WDViewClient;

		/**
		 * @param venusActivity
		 */
		WDViewClient(VenusActivity venusActivity) {
			WDViewClient = venusActivity;
		}

		/**
         * Give the host application a chance to take over the control when a new url
         * is about to be loaded in the current WebView.
         *
         * @param view          The WebView that is initiating the callback.
         * @param url           The url to be loaded.
         * @return              true to override, false for default behavior
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
        	Log.d(VenusActivity.TAG, "shouldOverrideUrlLoading url=" + url);
            
            // If dialing phone (tel:5551212)
            if (url.startsWith(WebView.SCHEME_TEL)) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(url));
                    VenusActivity.appActivity.startActivity(intent);
                } catch (android.content.ActivityNotFoundException e) {
                    
                }
            }

            // If displaying map (geo:0,0?q=address)
            else if (url.startsWith("geo:")) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    VenusActivity.appActivity.startActivity(intent);
                } catch (android.content.ActivityNotFoundException e) {
                    
                }
            }

            // If sending email (mailto:abc@corp.com)
            else if (url.startsWith(WebView.SCHEME_MAILTO)) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    VenusActivity.appActivity.startActivity(intent);
                } catch (android.content.ActivityNotFoundException e) {
                    
                }
            }

            // If sms:5551212?body=This is the message
            else if (url.startsWith("sms:")) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);

                    // Get address
                    String address = null;
                    int parmIndex = url.indexOf('?');
                    if (parmIndex == -1) {
                        address = url.substring(4);
                    }
                    else {
                        address = url.substring(4, parmIndex);

                        // If body, then set sms body
                        Uri uri = Uri.parse(url);
                        String query = uri.getQuery();
                        if (query != null) {
                            if (query.startsWith("body=")) {
                                intent.putExtra("sms_body", query.substring(5));
                            }
                        }
                    }
                    intent.setData(Uri.parse("sms:"+address));
                    intent.putExtra("address", address);
                    intent.setType("vnd.android-dir/mms-sms");
                    VenusActivity.appActivity.startActivity(intent);
                } catch (android.content.ActivityNotFoundException e) {
                    
                }
            }
            else if(url.indexOf(".3gp") != -1||url.indexOf(".mp4") != -1||url.indexOf(".flv") != -1)
            {
            	try {
	            	Intent intent = new Intent("android.intent.action.VIEW",Uri.parse(url));
	            	VenusActivity.appActivity.startActivity(intent);
            	} catch (android.content.ActivityNotFoundException e) {
                    
                }
            }
            // All else
            else {
/*
                // If our app or file:, then load into a new phonegap webview container by starting a new instance of our activity.
                // Our app continues to run.  When BACK is pressed, our app is redisplayed.
                if (url.startsWith("file://") || url.indexOf(this.ctx.baseUrl) == 0 || isUrlWhiteListed(url)) {
                    this.ctx.loadUrl(url);
                }

                // If not our application, let default viewer handle
                else {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    } catch (android.content.ActivityNotFoundException e) {
                        LOG.e(TAG, "Error loading url "+url, e);
                    }
                }*/
            	if(url.startsWith("http://")||url.startsWith("https://"))
            		view.loadUrl(url);
            	else
            	{
            		try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        VenusActivity.appActivity.startActivity(intent);
                    } catch (android.content.ActivityNotFoundException e) {
                    }
            	}
            }
            
            WDViewClient.nativebrowserreturn(url, 0);
            
            return true;
        	/*
        	view.loadUrl(url);
        	imageView.setVisibility(View.VISIBLE);
        	animation.setDuration(20000);
        	imageView.setAnimation(animation);
        	animation.start();	
            return true;*/
        }

        @Override
		public void doUpdateVisitedHistory(WebView view, String url,
				boolean isReload) {
			// TODO Auto-generated method stub
			super.doUpdateVisitedHistory(view, url, isReload);
			Log.d(VenusActivity.TAG,"doUpdateVisitedHistory url=" + url +",isReload=" + isReload);
			if(WDViewClient.bCleanHistory)
			{
				WDViewClient.bCleanHistory = false;
				WDViewClient.webView.clearHistory();
			}
		}

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            // Clear history so history.back() doesn't do anything.
            // So we can reinit() native side CallbackServer & PluginManager.
        	Log.d(VenusActivity.TAG,"onPageStarted url=" + url+"");
        	for (int i=0;i<WDViewClient.oldurls.size();i++) {
                String url1 = WDViewClient.oldurls.get(i);
                Log.d(VenusActivity.TAG,"======url=" +url1);
            }
        	WDViewClient.imageView.setVisibility(View.VISIBLE);
        	WDViewClient.animation.setDuration(20000);
        	WDViewClient.imageView.setAnimation(WDViewClient.animation);
    		
            //view.clearHistory();
        }

        /**
         * Notify the host application that a page has finished loading.
         *
         * @param view          The webview initiating the callback.
         * @param url           The url of the page.
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            WDViewClient.oldurls.add(url);
            Log.d(VenusActivity.TAG,"onPageFinished url=" + url);
            WDViewClient.imageView.setVisibility(View.INVISIBLE);
            WDViewClient.animation.setDuration(0);
            view.requestFocus();
			//pj382 add fgx
            Util.Trace("@@@@@@@@@@@onPageFinished:" + url);
            WDViewClient.nativebrowserreturn(url, 0);
        }

        /**
         * Report an error to the host application. These errors are unrecoverable (i.e. the main resource is unavailable).
         * The errorCode parameter corresponds to one of the ERROR_* constants.
         *
         * @param view          The WebView that is initiating the callback.
         * @param errorCode     The error code corresponding to an ERROR_* value.
         * @param description   A String describing the error.
         * @param failingUrl    The url that failed to load.
         */
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        	Log.d(VenusActivity.TAG,"onReceivedError errorCode=" + errorCode);
        	WDViewClient.imageView.setVisibility(View.INVISIBLE);
        	WDViewClient.animation.setDuration(0);
        }
    }