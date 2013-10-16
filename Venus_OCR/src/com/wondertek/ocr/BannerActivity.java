package com.wondertek.ocr;

import com.wondertek.banner.BannerLayout;
import com.wondertek.banner.BannerLayout.OnItemClickListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class BannerActivity extends Activity {

	BannerLayout bl;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        bl=(BannerLayout) findViewById(R.id.banner);
        bl.setOnItemClickListener(new OnItemClickListener() {
			
			public void onClick(int index, View childview) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "µã»÷ÁËindex£º"+index,Toast.LENGTH_SHORT).show();
			}
		});
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.activity_main, menu);
//        return true;
//    }
    
    @Override
	protected void onPause() {
		bl.stopScroll();
		super.onPause();
	}

	@Override
	protected void onRestart() {
		if(!bl.isScrolling())
			bl.startScroll();
		super.onRestart();
	}

	@Override
	protected void onResume() {
		if(!bl.isScrolling())
			bl.startScroll();
		super.onResume();
	}


	@Override
	protected void onStop() {
		bl.stopScroll();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		bl.stopScroll();
		super.onDestroy();
	}
}
