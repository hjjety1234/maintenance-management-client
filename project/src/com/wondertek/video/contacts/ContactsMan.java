package com.wondertek.video.contacts;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;
import com.wondertek.video.VenusApplication;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Contacts.People;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsoluteLayout;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

/**
 * Sample:
 * 		if(ContactsMan.getInstance().ShowContactsView("Choose Your Contacts"))
		{
			if(al.findViewWithTag(ContactsMan.getInstance().GetContactsViewID()) == null)
				al.addView(ContactsMan.getInstance().GetContactsView());
			venusview.setVisibility(View.GONE);
		}
		
 * 
 * */
public class ContactsMan {
	
	private static ContactsMan instance = null;
	
	private LinearLayout 		mView = null;
	private RelativeLayout		mButtonLayout = null;
	private Button				mButtonYes = null;
	private Button				mButtonNo = null;
	private TextView 			mViewTitle = null;
	private ContactListView		mViewList = null;
	private ProgressDialog 		mDialogBar = null; 
	
	private ArrayList<DataHolder>	mArrayDataSet = null;
	private ArrayList<DataHolder>	mArraySelected = null;
	private ListViewAdapter			mListViewAdapter = null;
	private int						mArraySelectedDirtySize = 0;
	private ArrayList<Message> 		mMessageArray = null;
	
	private Cursor mContactCursor;
	private boolean mbContactChange	= true;
	private String mViewTitleString = "";
	private ContactsAsynchTask mTask = null;
	
	private static final Object criticalLock = new Object();
	
	private ContactsMan()
	{
		mTask = new ContactsAsynchTask();
	}
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Public Data
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public synchronized static ContactsMan getInstance()
	{
		if(instance == null)
		{
			instance = new ContactsMan();
		}
		return instance;
	}
	
	public void Init(String title)
	{
		if(mView == null)
		{
			mView = new LinearLayout(VenusActivity.appActivity);
			mView.setOrientation(LinearLayout.VERTICAL);
			mView.setLayoutParams(new AbsoluteLayout.LayoutParams(VenusActivity.getInstance().getScreenWidth(), VenusActivity.getInstance().getScreenHeight(), 0, 0));
			mView.setBackgroundColor(Color.parseColor("#000000"));

			mButtonLayout = new RelativeLayout(VenusActivity.appActivity);
			mButtonLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			RelativeLayout.LayoutParams alignLeft = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			RelativeLayout.LayoutParams alignRight = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			alignLeft.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
			alignRight.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);

			mButtonYes = new Button(VenusActivity.appActivity);
			mButtonYes.setText(VenusApplication.getInstance().getResString("common_ok"));
			mButtonYes.setOnClickListener(mClickListener);
			mButtonYes.setLayoutParams(alignLeft);

			mButtonNo = new Button(VenusActivity.appActivity);
			mButtonNo.setText(VenusApplication.getInstance().getResString("common_cancel"));
			mButtonNo.setOnClickListener(mClickListener);
			mButtonNo.setLayoutParams(alignRight);

			mButtonLayout.addView(mButtonYes);
			mButtonLayout.addView(mButtonNo);
			
			mViewTitle = new TextView(VenusActivity.appActivity);
			
			mViewList = new ContactListView(VenusActivity.appActivity);
			mViewList.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			
			mArrayDataSet	= new ArrayList<DataHolder>();
			mArraySelected	= new ArrayList<DataHolder>();
			mMessageArray	= new ArrayList<Message>();
			
			mListViewAdapter = new ListViewAdapter();
			mViewList.setAdapter(mListViewAdapter);
			
			mDialogBar=new ProgressDialog(VenusActivity.appActivity);
			mDialogBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mDialogBar.setIndeterminate(false);
			
			mView.addView(mButtonLayout);
			mView.addView(mViewTitle);
			mView.addView(mViewList);
			mView.setTag(this.GetContactsViewID());
			mView.setVisibility(View.GONE);
		}
		mViewTitleString = title;
		mViewTitle.setText(mViewTitleString);
	}

	public void Reset()
	{
		mbContactChange = true;
		mViewList.Reset();
		mListViewAdapter.Reset();
		mArrayDataSet.clear();
		mMessageArray.clear();
		mArraySelectedDirtySize = mArraySelected.size();
		Util.Trace("-------Reset---------");
	}
	
	public void DeInit()
	{
		
	}

	public View GetContactsView()
	{
		return mView;
	}
	
	public String GetContactsViewID()
	{
		return "ContactListView";
	}

	public boolean ShowContactsView(String title)
	{
		Init(title);
		if(HaveContacts())
		{
			mView.setVisibility(View.VISIBLE);
			InitData();
			return true;
		}
		
		return false;
	}

	public static final int EVENT_ID_CONTACT_CHANGE			= 0;
	
	public void PostEvent(int event, Bundle bundle)
	{
		switch(event)
		{
		case EVENT_ID_CONTACT_CHANGE :
			SendMessage(MSG_ID_CONTACT_CHANGE, 0);
			break;
		default :
			break;
		}
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Private Data
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private class ViewHolder {
		TextView TVname;
		TextView TVnumber;
		CheckBox CBcheck;
	}
	
	private class DataHolder
	{
		String name;
		String number;
		boolean selected;
	}

	private class ContactListView extends ListView implements OnScrollListener {
		private boolean mScrollAvailable = false;
		private int mLastVisibleIndex = -1;
		
		public ContactListView(Context context) {
			super(context);
			setOnScrollListener(this);
		}
		
		public int getLastVisibleIndex()
		{
			return mLastVisibleIndex;
		}
		
		public boolean isScrollAvailable()
		{
			if(mScrollAvailable == false)
			{

				int index0 = this.getLastVisibleIndex();
				if(index0>=0)
				{
					int bottom0 = 0;
					View item0 = this.getChildAt(index0);
					int XY0[] = new int[2];
					if(item0 != null)
					{
						item0.getLocationInWindow(XY0);
						bottom0 = XY0[1] + item0.getHeight();
					}
					
					View item1 = this.getChildAt(index0+1);
					int bottom1 = 0;
					if(item1 != null)
					{
						int XY1[] = new int[2];
						item1.getLocationInWindow(XY1);
						bottom1 = XY1[1] + item1.getHeight();
					}
					
					int XY2[] = new int[2];
					this.getLocationInWindow(XY2);
					int parentY = XY2[1] + this.getHeight();
					
					if(bottom0 >= parentY || (bottom0<parentY && bottom1>parentY))
					{
						mScrollAvailable = true;
					}
					Util.Trace("bottom0="+bottom0+", bottom1="+bottom1+", parentY="+parentY);
				}
			}
			return mScrollAvailable;
		}
		
		public void RemoveItem(int index)
		{
			if(index >= 0 && (index < mViewList.getCount()))
			{
				this.removeViewAt(index);
			}
		}

		public void RemoveItem(ViewHolder h)
		{
			int n = this.getCount();
			for(int i=0; i<n; i++)
			{
				View item = (View)this.getItemAtPosition(i);
				if(((ViewHolder)item.getTag()) == h)
				{
					this.removeViewAt(i);
					break;
				}
			}
		}

		public void RemoveItemAll()
		{
			//mViewList.removeAllViews();
		}

		private boolean SelListHaveSpec(DataHolder h)
		{
			Util.Trace("!!! Have " + h.hashCode());
			int n = mArraySelected.size();
			for(int i=0; i<n; i++)
			{
				if(mArraySelected.get(i) == h)
				{
					Util.Trace("!!! :)");
					return true;
				}
			}
			Util.Trace("!!! :(");
			return false;
		}
		
		private boolean SelListClearByIndex(int index)
		{
			if(mArraySelected != null && (index>=0 && index<mArraySelected.size()))
			{
				mArraySelected.remove(index);
				return true;
			}
			return false;
		}

		private boolean SelListClearByHandle(DataHolder h)
		{
			Util.Trace("!!! Clear " + h.hashCode());
			if(mArraySelected != null)
			{
				int n = mArraySelected.size();
				for(int i=0; i<n; i++)
				{
					if(mArraySelected.get(i) == h)
					{
						mArraySelected.remove(i);
						return true;
					}
				}
			}
			return false;
		}

		private void SelListAppend(DataHolder h)
		{
			if(mArraySelected != null)
			{
				Util.Trace("!!! Append " + h.hashCode());
				mArraySelected.add(h);
			}
		}
		
		public void Reset()
		{
			RemoveItemAll();
			mScrollAvailable = false;
			mLastVisibleIndex = -1;
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			mLastVisibleIndex = firstVisibleItem + visibleItemCount -1;
			mViewTitle.setText(mViewTitleString+":("+this.getCount()+")");
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if( ((scrollState == OnScrollListener.SCROLL_STATE_FLING) || (scrollState == OnScrollListener.SCROLL_STATE_IDLE))
				&& (mLastVisibleIndex == mListViewAdapter.getCount() - 1)
				&& (mListViewAdapter.getCount() < mArrayDataSet.size()))
			{
				Util.Trace("---onScrollStateChanged---2");
				SendMessage(MSG_ID_GET_CONTACT, 0);
			}
		}
	}
	
	private View.OnClickListener mClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.equals(mButtonYes)) {
				instance.SendMessage(MSG_ID_RETURN_OK, 0);
			} else if (v.equals(mButtonNo)) {
				instance.SendMessage(MSG_ID_RETURN_CANCEL, 0);
			}
			mView.setVisibility(View.GONE);
			Message msg = new Message();
			msg.what = VenusActivity.MSG_ID_CONTACT_VIEW_BACK;
			VenusActivity.getInstance().sendVenusEvent(msg);
		}
	};
	
	private class ListViewAdapter extends BaseAdapter {

		private int		mCount = 0;
		
		public ListViewAdapter()
		{
		}
		
		public void Reset()
		{
			mCount = 0;
		}
		
		public void setCount(int count)
		{
			mCount = count;
		}
		
		@Override
		public int getCount() {
			return mCount;		//Be sure that the 'count' value won't be changed when the 'getView' is running.
		}

		@Override
		public Object getItem(int position) {
			return mArrayDataSet.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			DataHolder dataHolder = mArrayDataSet.get(position);
			if(convertView == null)
			{
				convertView = instance.BuildItemView(dataHolder, dataHolder.name, dataHolder.number);
				holder = (ViewHolder)convertView.getTag();
			}
			else
			{
				holder = (ViewHolder)convertView.getTag();
				holder.TVname.setText(dataHolder.name);
				holder.TVnumber.setText(dataHolder.number);
				holder.CBcheck.setTag(dataHolder);
				if(dataHolder.selected)
				{
					holder.CBcheck.setChecked(true);
				}
				else
				{
					holder.CBcheck.setChecked(false);
				}
			}
			if(position == mCount-1)
			{
				instance.SendMessage(MSG_ID_ADD_CONTACT_SUCCESS, 10);
			}
			Util.Trace("!!!                       position = " + position);
			Util.Trace("!!! [" + dataHolder.name + ", " + dataHolder.number + ", " + (dataHolder.selected?"1":"0") + "]");
			Util.Trace("!!! -----------------------------------------------------------------");
			return convertView;
		}
	}
	
	/**
	 * 
	 * android.os.AsyncTask<Params, Progress, Result>
	 * 
	 * The three types used by an asynchronous task are the following:
	 * 		1. Params, the type of the parameters sent to the task upon execution.
	 * 		2. Progress, the type of the progress units published during the background computation.
	 * 		3. Result, the type of the result of the background computation.
	 * 
	 */
	private class ContactsAsynchTask extends AsyncTask<String, String, Void>
	{
		//Runs on the UI thread before doInBackground(Params...)
		@Override
        protected void onPreExecute() {
        }
        
        @Override
		protected Void doInBackground(String... params) {
        	GetContacts(this);
			return null;
		}
		
		//Runs on the UI thread after publishProgress(Progress...) is invoked.
		@Override 
        protected void onProgressUpdate(String... progress) {
			if(!mViewList.isScrollAvailable())
			{
				SendMessage(MSG_ID_GET_CONTACT, 0);
			}
        }
		
		//Runs on the UI thread after doInBackground(Params...)
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		} 
		
		//Runs on the UI thread after cancel(boolean) is invoked.
		@Override 
        protected void onCancelled() {
            super.onCancelled(); 
        }
		
		public void onCallBack(String...param)
		{
			publishProgress(param[0], param[1], param[2]);
		}
	}
	
//	private class ContactsAsynchTask_1 extends Thread {
//		private final static int THREAD_STATE_IDLE 		= 0;
//		private final static int THREAD_STATE_RUNNING	= 1;
//		private final static int THREAD_STATE_PENDING	= 2;
//		private int mState = THREAD_STATE_IDLE;
//		
//		private final static int THREAD_OPR_NONE		= 0;
//		private final static int THREAD_OPR_PAUSE		= 1;
//		private final static int THREAD_OPR_RESUME		= 2;
//		private final static int THREAD_OPR_STOP		= 1;
//		private int mOperation	= THREAD_OPR_NONE;
//		
//		public ContactsAsynchTask_1()
//		{
//			super();
//		}
//		
//		public void run()
//		{
//			mState = THREAD_STATE_RUNNING;
//			GetContacts(this);
//			mState = THREAD_STATE_IDLE;
//		}
//		
//		public void onCallBack(String...param)
//		{
//			if(!mViewList.isScrollAvailable())
//			{
//				Util.Trace("123");
//				//mFirstTime = false;
//				SendMessage(MSG_ID_GET_CONTACT, 0);
//			}
//		}
//		
//		public boolean cancel(boolean mayInterruptIfRunning)
//		{
//			mOperation = THREAD_OPR_STOP;
//			return true;
//		}
//		
//		public boolean isCancelled()
//		{
//			if(mState == THREAD_STATE_IDLE || mOperation == THREAD_OPR_STOP)
//			{
//				return true;
//			}
//			return false;
//		}
//		
//		public void Pause()
//		{
//			try {
//				this.wait();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//		
//		public void Resume()
//		{
//			this.notify();
//		}
//		
//		public void Reset()
//		{
//			mState = THREAD_STATE_IDLE;
//			mOperation	= THREAD_OPR_NONE;
//		}
//		
//		public AsyncTask.Status getStatus()
//		{
//			AsyncTask.Status status;
//			switch(mState)
//			{
//			case THREAD_STATE_IDLE :
//				status = AsyncTask.Status.FINISHED;
//				break;
//			case THREAD_STATE_RUNNING :
//				status = AsyncTask.Status.RUNNING;
//				break;
//			case THREAD_STATE_PENDING :
//				status = AsyncTask.Status.PENDING;
//				break;
//			default :
//				status = AsyncTask.Status.FINISHED;
//				break;
//			}
//			return status;
//		}
//		
//		public void execute(String...params)
//		{
//			this.start();
//		}
//	}
	
	private static final int MSG_ID_GET_CONTACT				= 0;
	private static final int MSG_ID_ADD_CONTACT_SUCCESS		= 1;
	private static final int MSG_ID_CONTACT_CHANGE			= 2;
	private static final int MSG_ID_RETURN_OK				= 3;
	private static final int MSG_ID_RETURN_CANCEL			= 4;
	
	private Handler mHandler = new Handler(){
		private static final int STATE_IDLE		= 0;
		private static final int STATE_BUSY		= 1;
		private int mState = STATE_IDLE;
		
		public void handleMessage(Message msg) {
			switch(msg.what)
			{
			case MSG_ID_GET_CONTACT :
			{
				Util.Trace("mState =  " + mState);
				if(mState == STATE_IDLE)
				{
					mState = STATE_BUSY;
					mListViewAdapter.setCount(mArrayDataSet.size());
					mListViewAdapter.notifyDataSetChanged();
				}
				else if(mState == STATE_BUSY)
				{
					
				}
				break;
			}
			case MSG_ID_ADD_CONTACT_SUCCESS :
			{
				Util.Trace("MSG_ID_ADD_CONTACT_SUCCESS");
				mState = STATE_IDLE;
				break;
			}
			case MSG_ID_CONTACT_CHANGE :
			{
				Util.Trace("------MSG_ID_CONTACT_CHANGE------");
				AsyncTask.Status status = mTask.getStatus();
				if(status == AsyncTask.Status.FINISHED || status == AsyncTask.Status.PENDING)
				{
					Reset();
					if(HaveContacts())
					{
						InitData();
					}
				}
				else
				{
					SendMessage(MSG_ID_CONTACT_CHANGE, 1000);
				}
				break;
			}
			case MSG_ID_RETURN_OK :
			{
				String contacts = GetSelectedContacts();
				Util.Trace("###########");
				Util.Trace(contacts);
				Util.Trace("###########");
				//VenusActivity.getInstance().nativecontactreturn(contacts, true);
				break;
			}
			case MSG_ID_RETURN_CANCEL :
			{
				//VenusActivity.getInstance().nativecontactreturn("", false);
				break;
			}
			default :
				break;
			}
		}
	};
	
	
	private void SendMessage(int id, int delay)
	{
		Message msg = new Message();
		msg.what = id;
		if(delay <= 0)
		{
			mHandler.sendMessage(msg);
		}
		else
		{
			mMessageArray.add(msg);
			mHandler.postDelayed(new Runnable(){
				public void run() {
					if(mMessageArray.size() > 0)
					{
						mHandler.sendMessage( mMessageArray.get(0) );
						mMessageArray.remove(0);
					}
				}}, delay);
		}
	}
	private void InitData()
	{
		AsyncTask.Status status = mTask.getStatus();
		if(mbContactChange == true)
		{
			if(status == AsyncTask.Status.FINISHED)
			{
				mTask = new ContactsAsynchTask();
				mTask.execute("");
			}
			else if(status == AsyncTask.Status.PENDING)
			{
				mTask = new ContactsAsynchTask();
				mTask.execute("");
			}
			else if(status == AsyncTask.Status.RUNNING)
			{
				
			}
			else
			{
				mTask = new ContactsAsynchTask();
				mTask.execute("");
			}
		}
	}
	
	private String GetSelectedContacts()
	{
		String result = "";
		synchronized(criticalLock)
		{
			int n = mArraySelected.size();
			for(int i=mArraySelectedDirtySize; i<n; i++)
			{
				if(i != n-1)
				{
					result += mArraySelected.get(i).name + "\n" + mArraySelected.get(i).number + "\n";
				}
				else
				{
					result += mArraySelected.get(i).name + "\n" + mArraySelected.get(i).number;
				}
			}
		}
		return result;
	}
	private View BuildItemView(DataHolder dataHolder, String sName, String sNumber)
	{

		View item = new LinearLayout(VenusActivity.appActivity);
		ViewHolder holder = new ViewHolder();
		item.setLayoutParams(new AbsListView.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		
		RelativeLayout relative = new RelativeLayout(VenusActivity.appActivity);
		relative.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		
		TextView name = new TextView(VenusActivity.appActivity);
		RelativeLayout.LayoutParams pName = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		pName.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
				RelativeLayout.TRUE);
		pName.leftMargin = 4;
		name.setLayoutParams(pName);
		name.setTextSize(20);
		name.setText(sName);
		name.setTextColor(Color.parseColor("#FFFFFF"));

		TextView number = new TextView(VenusActivity.appActivity);
		RelativeLayout.LayoutParams pNumber = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		pNumber.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
				RelativeLayout.TRUE);
		pNumber.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
				RelativeLayout.TRUE);
		pNumber.leftMargin = 4;
		pNumber.topMargin = 1;
		number.setLayoutParams(pNumber);
		number.setTextSize(14);
		number.setText(sNumber);
		number.setTextColor(Color.parseColor("#FFFFFF"));

		CheckBox check = new CheckBox(VenusActivity.appActivity);
		RelativeLayout.LayoutParams pCheck = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		pCheck.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
				RelativeLayout.TRUE);
		pCheck.addRule(RelativeLayout.ALIGN_PARENT_TOP,
				RelativeLayout.TRUE);
		check.setLayoutParams(pCheck);
		check.setTag(dataHolder);					//Bind the handle of a item.
		check.setChecked(false);
		check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
				Util.Trace("!!! check = " + (isChecked==true?"1":"0"));
				if(isChecked)
				{
					DataHolder h = (DataHolder)buttonView.getTag();
					h.selected = true;
					mViewList.SelListAppend(h);
				}
				else
				{
					DataHolder h = (DataHolder)buttonView.getTag();
					h.selected =false;
					mViewList.SelListClearByHandle(h);
				}
			}
		});

		relative.addView(name);
		relative.addView(number);
		relative.addView(check);

		holder.TVname = name;
		holder.TVnumber = number;
		holder.CBcheck = check;

		((LinearLayout) item).addView(relative);
		item.setTag(holder);
		
		return item;
	}
	
	private void AddContactData(String sName, String sNumber, boolean finish)
	{
		synchronized(criticalLock)
		{
			if(finish == false)
			{
				DataHolder holder = new DataHolder();
				holder.name = sName;
				holder.number = sNumber;
				holder.selected = false;
				
				int n = mArraySelectedDirtySize;
				for(int i=0; i<n; i++)
				{
					DataHolder data = mArraySelected.get(i);
					if(sName.equals(data.name) && sNumber.equals(data.number))
					{
						holder.selected = true;
						mArraySelected.add(holder);
						break;
					}
				}
				mArrayDataSet.add(holder);
			}
			else
			{
				int n = mArraySelectedDirtySize;
				for(int i=0; i<n; i++)
				{
					mArraySelected.remove(0);
				}
				mArraySelectedDirtySize = 0;
			}
		}
	}

	private Uri contactsUri = null;
	private Uri phoneUri = null;
	private Cursor ContactRefresh()
	{
		if(contactsUri == null || phoneUri == null)
		{
			try {
				Class<?> clazz = Class.forName("android.provider.ContactsContract$Contacts");
				Field f1 = clazz.getField("CONTENT_URI");
				contactsUri = (Uri) (f1.get(null));

				Class<?> clazz2 = Class.forName("android.provider.ContactsContract$CommonDataKinds$Phone");
				Field f2 = clazz2.getField("CONTENT_URI");
				phoneUri = (Uri) (f2.get(null));
			} catch (Exception e) {
				Util.Trace(e.toString());
			}
		}
		Cursor c = VenusActivity.appActivity.getContentResolver().query(contactsUri/*Phones.CONTENT_URI*/, null, null, null, null);
		return c;
	}
	
	private String GetContactsHighSDK(Object observer)
	{
		ContactsAsynchTask task = (ContactsAsynchTask)observer;
		mContactCursor = ContactRefresh();
		String contactsList = "";
		
		int i = 0;
		int c = 0;
		if(mContactCursor != null)
			c = mContactCursor.getCount();
		
		if(c > 0)
		{
			mContactCursor.moveToFirst();
			for(; task.isCancelled()==false && i<c; i++)
			{
				String name = mContactCursor.getString(mContactCursor.getColumnIndex("display_name"));
				String id = mContactCursor.getString(mContactCursor.getColumnIndex("_id"));
				String hasPhone = mContactCursor.getString( mContactCursor.getColumnIndex("has_phone_number") );
				if( hasPhone.equals("1") )
				{
					Cursor phones = VenusActivity.appActivity.getContentResolver().query(
							phoneUri,
							null,
							"contact_id" +" = ?",
							new String[] { id },
							null);
					
					if(phones.getCount() == 1)
					{
						phones.moveToNext();
						String phone = phones.getString(phones.getColumnIndex("data1"));
						phone = phone.replaceAll("-|\\s|_", "");
						AddContactData(name, phone, false);
						((ContactsAsynchTask)observer).onCallBack(name, phone, "0");
					}
					else
					{
						while(task.isCancelled()==false && phones != null && phones.moveToNext())
						{
							String phone = phones.getString(phones.getColumnIndex("data1"));
							phone = phone.replaceAll("-|\\s|_", "");
							AddContactData(name, phone, false);
							((ContactsAsynchTask)observer).onCallBack(name, phone, "0");
						}
					}
					phones.close();
				}
				mContactCursor.moveToNext();	
			}
		}
		AddContactData(null, null, true);
		mbContactChange = false;
		CursorClose(mContactCursor);
		Util.Trace("---------GetContactsHighSDK Over-----------");
		return contactsList; 
	}
	
	private final Uri CONTACT_PHONE_URI = Uri.parse("content://contacts/people"); // People.CONTENT_URI;
	private final Uri CONTACT_SIM_URI = Uri.parse("content://icc/adn"); // sim
	private String GetContactsLowSDK(Object observer)
	{
		ContactsAsynchTask task = (ContactsAsynchTask)observer;
		Uri uri = null;
		String contactsList = "";
		ContentResolver resolver = VenusActivity.appActivity.getContentResolver();
		String columns[] = new String[] { People._ID, People.NAME, People.NUMBER };
		Cursor cur = null;
		
		for(int i=0; i<2; i++)
		{
			if(i == 0) 
				uri = CONTACT_PHONE_URI;
			else if(i == 1)
				uri = CONTACT_SIM_URI;
			else
				break;
			
			try {
				cur = resolver.query(uri, columns, null, null, People.NAME);
				if (cur.moveToFirst()) {
					do {
						String name = cur.getString(cur.getColumnIndex(People.NAME));
						String phone = cur.getString(cur.getColumnIndex(People.NUMBER));
						phone = phone.replaceAll("-|\\s|_", "");
						AddContactData(name, phone, false);
						((ContactsAsynchTask)observer).onCallBack(name, phone, "0");
					} while (task.isCancelled()==false && cur.moveToNext());
				}
			} catch (Exception e) {
			} finally {
				CursorClose(cur);
				cur = null;
			}
		}
		AddContactData(null, null, true);
		mbContactChange = false;
		Util.Trace("---------GetContactsLowSDK Over-----------");
		return contactsList;
	}
	
	private void CursorClose(Cursor c)
	{
		if(c != null)
		{
			c.deactivate();
			c.close();	
		}
	}
	
	private boolean checkMobilePhone(String phone) {
		/*
		 * Matcher China Mobile "^1(3[4-9]|5[012789]|8[78])\d{8}$"以代码为准
		 * 
		 * Matcher China Telecom "^18[09]\d{8}$"
		 * 
		 * Matcher China Unicom "^1(3[0-2]|5[56]|8[56])\d{8}$"
		 * 
		 * Matcher CDMA "^1[35]3\d{8}$"
		 */
		Pattern pattern = Pattern
				.compile("^(\\+86)?1(3[4-9]|5[012789]|8[278])\\d{8}$");
		Matcher matcher = pattern.matcher(phone);

		if (matcher.matches()) {
			return true;
		}
		return false;
	}
	
	private void phoneNumberfilter(String name, String temp) {
		if (temp != null) {
			String phoneNo = temp;
			if (temp.contains(" ") || temp.contains("-") || temp.contains("_")) {
				phoneNo = temp.replaceAll("-|\\s|_", "");
			}
			if (checkMobilePhone(phoneNo)) {
				Pattern pattern = Pattern.compile("1(3[4-9]|5[012789]|8[278])\\d{8}$");
				Matcher matcher = pattern.matcher(phoneNo);
				StringBuffer phoneNoMached = new StringBuffer();
				while (matcher.find()) {
					phoneNoMached.append(matcher.group());
				}
				String phoneNoMachedString = phoneNoMached.toString();
//				if (!phoneMap.containsKey(phoneNoMachedString)) {
//					if (name != null && !"".equals(name)) {
//						personsArrayList.add(phoneNoMachedString.concat("[")
//								.concat(name).concat("]"));
//
//					} else {
//						personsArrayList.add(phoneNoMachedString);
//					}
//					phoneMap.put(phoneNoMachedString, 1);
//				}
			}
		}
	}
	
	private void GetContacts(Object observer)
	{
		int sdk = Util.GetSDK();
		if(sdk == Util.SDK_ANDROID_15 || sdk == Util.SDK_ANDROID_16 || sdk == Util.SDK_OMS_15 || sdk == Util.SDK_OMS_16)
		{
			GetContactsLowSDK(observer);
		}
		else
		{
			GetContactsHighSDK(observer);
		}
	}
	
	private boolean HaveContacts()
	{
		if(mArrayDataSet.size() > 0)
		{
			return true;
		}
		
		int c = 0;
		int sdk = Util.GetSDK();
		if(sdk == Util.SDK_ANDROID_15 || sdk == Util.SDK_ANDROID_16 || sdk == Util.SDK_OMS_15 || sdk == Util.SDK_OMS_16)
		{
			Uri uri = null;
			ContentResolver resolver = VenusActivity.appActivity.getContentResolver();
			String columns[] = new String[] { People._ID, People.NAME, People.NUMBER };
			Cursor cur = null;
			
			for(int i=0; i<2; i++)
			{
				if(i == 0) 
					uri = CONTACT_PHONE_URI;
				else if(i == 1)
					uri = CONTACT_SIM_URI;
				else
					break;
				
				try {
					cur = resolver.query(uri, columns, null, null, People.NAME);
					if(cur != null)
						c += cur.getCount();
				} catch (Exception e) {
				} finally {
					if (cur != null) {
						cur.close();
						cur = null;
					}
				}
			}
		}
		else
		{
			mContactCursor = ContactRefresh();
			if(mContactCursor != null)
			{
				c = mContactCursor.getCount();
			}
		}
		return (c > 0)?true:false;
	}
}
