package com.wondertek.ocr;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wondertek.engine.OcrEngine;
import com.wondertek.vo.IDCard;

public class BroadBandActivity extends Activity {
	private static final String TAG = "MainActivity";
	private ImageButton m_takePhoto = null;
	private ImageButton m_selectAlbum = null;
	private EditText m_nameText = null;
	private TextView m_genderText = null;
	private Spinner m_bandSpinner = null;
	private TextView m_birthText = null;
	private TextView m_addressText = null;
	private TextView m_idText = null;
	private TextView m_year = null;
	private TextView m_month = null;
	private TextView m_day = null;
	private ImageView btn_back = null;
	private ImageView btn_next = null;
	private int LOCAL_IMAGE = 1;
	private int CAMERA_RESULT = 2;
	private static String filePath = null;
	private boolean isClear = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_broadband);
		// 设置按钮
		this.m_takePhoto = (ImageButton)findViewById(R.id.take_phone);
		this.m_selectAlbum = (ImageButton)findViewById(R.id.select_albumtake_phone);
		// 设置文本
		this.m_nameText = (EditText)findViewById(R.id.name);
		this.m_genderText = (TextView)findViewById(R.id.gender);
		this.m_bandSpinner = (Spinner)findViewById(R.id.race);
		this.m_birthText = (TextView)findViewById(R.id.birthday);
		this.m_year = (TextView)findViewById(R.id.year);
		this.m_month = (TextView)findViewById(R.id.month);
		this.m_day = (TextView)findViewById(R.id.day);
		this.m_addressText = (TextView)findViewById(R.id.address);
		this.m_idText = (TextView)findViewById(R.id.number);
		this.btn_back = (ImageView)findViewById(R.id.back);
		this.btn_next = (ImageView)findViewById(R.id.next);
		
		this.btn_back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		this.btn_next.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BroadBandActivity.this, correctionActivity.class);
				intent.putExtra("IDNo",m_idText.getText().toString());
				isClear = true;
				startActivity(intent);

				overridePendingTransition(R.anim.fade, R.anim.hold);

			}
		});
		
		// 设置拍照按钮消息处理函数
		m_takePhoto.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BroadBandActivity.this, SurfaceViewDraw.class);
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
		// 设置带宽选择
		ArrayAdapter<CharSequence> adapter =  ArrayAdapter.createFromResource(this, R.array.bands, 
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		m_bandSpinner.setAdapter(adapter);
	}
	
	@Override
	protected void onResume(){
		super.onResume();

		if(isClear){
			m_nameText.setText("");
			m_genderText.setText("");
			m_year.setText("");
			m_month.setText("");
			m_day.setText("");
			m_addressText.setText("");
			m_idText.setText("");
			
			isClear = false;
		}

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
					if(idCard.getBirth() != null){
						String birthday = idCard.getBirth().subSequence(0, 11).toString();
//						m_birthText.setText(idCard.getBirth().subSequence(0, 11));
						m_year.setText(birthday.subSequence(0, 4));
						m_month.setText(birthday.subSequence(5, 7));
						m_day.setText(birthday.subSequence(8, 10));
					}
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return true;
	}
}