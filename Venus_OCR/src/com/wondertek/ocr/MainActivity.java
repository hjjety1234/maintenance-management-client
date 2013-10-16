package com.wondertek.ocr;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import org.apache.commons.io.FileUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.wondertek.engine.OcrEngine;
import com.wondertek.vo.IDCard;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	private ImageButton m_takePhoto = null;
	private ImageButton m_selectAlbum = null;
	private TextView m_nameText = null;
	private TextView m_genderText = null;
	private TextView m_raceText = null;
	private TextView m_birthText = null;
	private TextView m_addressText = null;
	private TextView m_idText = null;
	private int LOCAL_IMAGE = 1;
	private int CAMERA_RESULT = 2;
	private static String filePath = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		// 设置按钮
		this.m_takePhoto = (ImageButton)findViewById(R.id.take_phone);
		this.m_selectAlbum = (ImageButton)findViewById(R.id.select_album);
		// 设置文本
		this.m_nameText = (TextView)findViewById(R.id.name);
		this.m_genderText = (TextView)findViewById(R.id.gender);
		this.m_raceText = (TextView)findViewById(R.id.race);
		this.m_birthText = (TextView)findViewById(R.id.birthday);
		this.m_addressText = (TextView)findViewById(R.id.address);
		this.m_idText = (TextView)findViewById(R.id.id_no);
		// 设置拍照按钮消息处理函数
		m_takePhoto.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, SurfaceViewDraw.class);
	            new DateFormat();
	            String name = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA))+".jpg";
	            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/ocr/");
	            file.mkdirs();
	            filePath = file.getPath() + "/" + name;
   	         	intent.putExtra("image_path", filePath);
				startActivityForResult(intent, CAMERA_RESULT);
			}
		});
		// 设置相册选择按钮消息处理函数
		m_selectAlbum.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
				getImage.addCategory(Intent.CATEGORY_OPENABLE);
				getImage.setType("image/*");
				startActivityForResult(getImage, LOCAL_IMAGE);
			}
		});
	}
	
	
	private static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
		}

		return inSampleSize;
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            if (requestCode == LOCAL_IMAGE) {
            	Log.d(TAG, "[onActivityResult] request code: LOCAL IMAGE" );
                Uri originalUri = intent.getData();
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = managedQuery(originalUri, proj, null, null, null); 
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                filePath = cursor.getString(column_index);
	        }else if (requestCode == CAMERA_RESULT){
	            String sd_state = Environment.getExternalStorageState();
	            if(!sd_state.equals(Environment.MEDIA_MOUNTED)){
	            	return;
	            }
	        }
            Log.d(TAG, "file path : " + filePath);
            if (filePath != null) {
	        	// 获取图片的原始数据
		        byte[] imageData = null;
		        Bitmap bitmap = null;
				try {
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inJustDecodeBounds = true;
					BitmapFactory.decodeFile(filePath, options);
					options.inSampleSize = calculateInSampleSize(options, 1024, 720);
					options.inJustDecodeBounds = false;
					bitmap = BitmapFactory.decodeFile(filePath, options);
					// 转换为字符数组
					ByteArrayOutputStream fout = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
					imageData = fout.toByteArray();
					fout.close();
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
					return;
				}finally {
					if(bitmap != null && bitmap.isRecycled() == false){
						bitmap.recycle();
					}
				}
				// 进行识别操作
				OcrEngine localOcrEngine = new OcrEngine();
				try {
					IDCard idCard = localOcrEngine.recognize(this, imageData);
					imageData = null;
					if(idCard.getName() != null)
						m_nameText.setText(idCard.getName().substring(0, 3));
					if(idCard.getSex() != null)
						m_genderText.setText(idCard.getSex().subSequence(0, 2));
					if(idCard.getEthnicity() != null)
						m_raceText.setText(idCard.getEthnicity().subSequence(0, 2));
					if(idCard.getBirth() != null)
						m_birthText.setText(idCard.getBirth().subSequence(0, 11));
					if(idCard.getAddress() != null)
						m_addressText.setText(idCard.getAddress().subSequence(0, 30));
					if(idCard.getCardNo() != null)
						m_idText.setText(idCard.getCardNo().subSequence(0, 20));
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
				}finally {
					imageData = null;
					System.gc();
				}
            }
        }
	}
}