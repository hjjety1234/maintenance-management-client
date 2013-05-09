package com.wondertek.video.contacts;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts.People;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract;
import android.util.Log;

import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;

@SuppressWarnings("deprecation")
public class ContactsObserver {
	public static final String TAG = "Contacts";
	public String url;
	public VenusActivity venusHandle;
	private static ContactsObserver instance = null;

	private HashMap<String, String> Contact_Map ;
	
	static String[] pinyin = { "a", "ai",  "an", "ang", "ao", "ba", "bai", "ban", "bang",   
		"bao", "bei", "ben", "beng", "bi", "bian", "biao", "bie", "bin",   
		"bing", "bo", "bu", "ca", "cai", "can", "cang", "cao",  "ce",   
		"ceng", "cha", "chai", "chan", "chang", "chao", "che",  "chen",   
		"cheng", "chi", "chong", "chou", "chu", "chuai", "chuan",
		"chuang", "chui", "chun", "chuo", "ci", "cong", "cou", "cu", 
		"cuan", "cui", "cun", "cuo", "da", "dai", "dan", "dang", "dao",   
		"de", "deng", "di", "dian", "diao", "die", "ding", "diu", "dong",   
		"dou", "du", "duan", "dui", "dun", "duo", "e", "en", "er",  "fa",   
		"fan", "fang", "fei", "fen", "feng", "fo", "fou", "fu", "ga", 
		"gai", "gan", "gang", "gao", "ge", "gei", "gen", "geng",  "gong",   
		"gou", "gu", "gua", "guai", "guan", "guang", "gui", "gun", "guo",   
		"ha", "hai", "han", "hang", "hao", "he", "hei", "hen", "heng",   
		"hong", "hou", "hu", "hua", "huai", "huan", "huang", "hui", "hun",  
		"huo", "ji", "jia", "jian", "jiang", "jiao", "jie", "jin", "jing",   
		"jiong", "jiu", "ju", "juan", "jue", "jun", "ka", "kai", "kan",   
		"kang", "kao", "ke", "ken", "keng", "kong", "kou", "ku", "kua",    
		"kuai", "kuan", "kuang", "kui", "kun", "kuo", "la", "lai", "lan",    
		"lang", "lao", "le", "lei", "leng", "li", "lia", "lian", "liang",   
		"liao", "lie", "lin", "ling", "liu", "long", "lou", "lu", "lv",   
		"luan", "lue", "lun", "luo", "ma", "mai", "man", "mang", "mao",   
		"me", "mei", "men", "meng", "mi", "mian", "miao", "mie", "min",    
		"ming", "miu", "mo", "mou", "mu", "na", "nai", "nan", "nang",   
		"nao", "ne", "nei", "nen", "neng", "ni", "nian", "niang", "niao",   
		"nie", "nin", "ning", "niu", "nong", "nu", "nv", "nuan", "nue",   
		"nuo", "o", "ou", "pa", "pai", "pan", "pang", "pao", "pei", "pen",   
		"peng", "pi", "pian", "piao", "pie", "pin", "ping", "po", "pu",
		"qi", "qia", "qian", "qiang", "qiao", "qie", "qin", "qing", 
		"qiong", "qiu", "qu", "quan", "que", "qun", "ran", "rang", "rao",    
		"re", "ren", "reng", "ri", "rong", "rou", "ru", "ruan", "rui",   
		"run", "ruo", "sa", "sai", "san", "sang", "sao", "se", "sen",    
		"seng", "sha", "shai", "shan", "shang", "shao", "she", "shen",    
		"sheng", "shi", "shou", "shu", "shua", "shuai", "shuan", "shuang",   
		"shui", "shun", "shuo", "si", "song", "sou", "su", "suan", "sui",    
		"sun", "suo", "ta", "tai", "tan", "tang", "tao", "te", "teng",
		"ti", "tian", "tiao", "tie", "ting", "tong", "tou",  "tu", "tuan",   
		"tui", "tun", "tuo", "wa", "wai", "wan", "wang", "wei", "wen",   
		"weng", "wo", "wu", "xi", "xia", "xian", "xiang", "xiao", "xie",   
		"xin", "xing", "xiong", "xiu", "xu", "xuan", "xue", "xun", "ya",   
		"yan", "yang", "yao", "ye", "yi", "yin", "ying", "yo", "yong",   
		"you", "yu", "yuan", "yue", "yun", "za", "zai", "zan", "zang",   
		"zao", "ze", "zei", "zen", "zeng", "zha", "zhai", "zhan",   
		"zhang", "zhao", "zhe", "zhen", "zheng", "zhi", "zhong", "zhou",    
		"zhu", "zhua", "zhuai", "zhuan", "zhuang", "zhui", "zhun", "zhuo",   
		"zi", "zong", "zou", "zu", "zuan", "zui", "zun", "zuo" };
	
	private ContactsObserver(VenusActivity va) {
		venusHandle = va;
	}
	
	public static ContactsObserver getInstance(VenusActivity va) {
		if (instance == null) {
			instance = new ContactsObserver(va);
		}
		return instance;
	}
	
	public String getContacts()
	{
		int sdk = Util.GetSDK();
		if(sdk == Util.SDK_ANDROID_15 || sdk == Util.SDK_ANDROID_16 || sdk == Util.SDK_OMS_15 || sdk == Util.SDK_OMS_16)
		{
			return getContactsLowSDK();
		}
		else
		{
			return getContactsHighSDK();
		}
	}

	public String getSearchContacts(String condition)
	{
		String contactsList = "";
		if (condition == null || condition.equals(""))
			return contactsList;
		String[] projection= {Phone.DISPLAY_NAME, Phone.NUMBER, Phone.PHOTO_ID, "sort_key"};
		String selection = Phone.NUMBER + " like '%" + condition + "%' or " 
				 		 + Phone.DISPLAY_NAME + " like '%" + condition + "%' or "
				 		 + "sort_key" + " like '%" + getPYSearchRegExp(condition, "%") + "%'";
		Cursor cur = venusHandle.appActivity.getContentResolver().query(Phone.CONTENT_URI, projection, selection, null, Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
		cur.moveToFirst();
		while(cur.getCount() > cur.getPosition()) {
			String number = cur.getString(cur.getColumnIndex(Phone.NUMBER));
			String name = cur.getString(cur.getColumnIndex(Phone.DISPLAY_NAME));
			String photo_id = cur.getString(cur.getColumnIndex(Phone.PHOTO_ID));
			String sort_key = cur.getString(cur.getColumnIndex("sort_key"));
		
			Util.Trace("contacts>>> name:" + name + "number:" + number + "photo:"+photo_id + "sort_key" + sort_key);
			boolean show = true;
			if (isPinYin(condition) ) {
				if(containCn(sort_key)) {
					show = pyMatches(sort_key, condition.replaceAll(" ", ""));
				} else {
					if (name != null && name.startsWith(condition)) //如果sort_key 不包含中文 则需要用display_name匹配 英文匹配采取前缀匹配
						show = true;
					else
						show = false;
				}
			}
			Util.Trace("is show " + show);
			
			if (show) {
				contactsList = contactsList + name + "\n" + number + "\n";
			}
			cur.moveToNext();
		}
		cur.close();
		Util.Trace("is contactsList= " + contactsList);
		return contactsList;
	}
	
	/**
     * 
     * @param str 搜索字符串
     * @param exp 追加的正则表达式
     * @return 拼音搜索正则表达式
     */
    public String getPYSearchRegExp(String str, String exp) {
		int start = 0;
		String regExp = "";
		str = str.toLowerCase();
		boolean isFirstSpell = true;
		for (int i = 0; i < str.length(); ++i) {
			String tmp = str.substring(start, i + 1);
			isFirstSpell = binSearch(tmp) ? false : true;
			
			if (isFirstSpell) {
				regExp += str.substring(start, i) + exp;
				start = i;
			} else {
				isFirstSpell = true;
			}
			
			if (i == str.length() - 1)
				regExp += str.substring(start, i + 1) + exp;		
		}
		return regExp;
	}
    
    /**
     * 2分法查找拼音列表
     * @param str 拼音字符串
     * @return 是否是存在于拼音列表
     */
	public boolean binSearch(String str) {
		int mid = 0;
		int start = 0;
		int end = pinyin.length - 1;
		
		while (start < end) {
			mid = start + ((end - start) / 2 );
			if (pinyin[mid].matches(str + "[a-zA-Z]*"))
				return true;
			
			if (pinyin[mid].compareTo(str) < 0) 
				start = mid + 1;
			else 
				end = mid - 1;
		}
		return false;
	}

	/**
	 * 拼音匹配
	 * @param src 含有中文的字符串  
	 * @param des 查询的拼音
	 * @return 是否能匹配拼音
	 */
	public boolean pyMatches(String src, String des) {
		if (src != null) {
			src = src.replaceAll("[^ a-zA-Z]", "").toLowerCase();
			src = src.replaceAll("[ ]+", " ");
			String condition = getPYSearchRegExp(des, "[a-zA-Z]* ");
			
			/*
			Pattern pattern = Pattern.compile(condition);
			Matcher m = pattern.matcher(src);  
			return m.find(); 
			*/
			String[] tmp = condition.split("[ ]");
			String[] tmp1 = src.split("[ ]");
			
			for(int i = 0; i + tmp.length <= tmp1.length; ++i) {
				String str = "";
				for (int j = 0; j < tmp.length; j++)
					str += tmp1[i+j] + " ";
				if (str.matches(condition))
					return true;
			}
		} 
		return false;
	}
	
	public boolean isNumeric(String str) {
	    Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
	}
	
	public boolean isPinYin(String str) {
		Pattern pattern = Pattern.compile("[ a-zA-Z]*");
        return pattern.matcher(str).matches();
	}
	
	public boolean containCn(String str) {        
	    Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]");          
	    return pattern.matcher(str).find();
	    /*
	    while (m.find()) {      
	           for (int i = 0; i <= m.groupCount(); i++) {      
	                count = count + 1;      
	            }      
	        }
	   System.out.println("共有 " + count + "个 ");   
	   */
	}
	private final Uri CONTACT_PHONE_URI = Uri.parse("content://contacts/people"); // People.CONTENT_URI;
	private final Uri CONTACT_SIM_URI = Uri.parse("content://icc/adn"); // sim
	private String getContactsLowSDK()
	{
		Uri uri = null;
		String contactsList = "";
		ContentResolver resolver = venusHandle.appActivity.getContentResolver();
		String columns[] = new String[] { People._ID, People.NAME, People.NUMBER };
		Cursor cur = null;

		if(Contact_Map == null) Contact_Map = new HashMap<String, String>();
		Contact_Map.clear();

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
						String temp = cur.getString(cur.getColumnIndex(People.NUMBER));
						//phoneNumberfilter(name, temp);
						Contact_Map.put(name, temp);
					} while (cur.moveToNext());
				}
			} catch (Exception e) {
			} finally {
				if (cur != null) {
					cur.close();
					cur = null;
				}
			}
		}

		//Build the contacts list
		int elementN = Contact_Map.size();
		int j = 0;
		String[] nameArray = new String[elementN];
		Set<String> set = Contact_Map.keySet();
		for(String key : set)
		{
			nameArray[j++] = key;
		}
		for(j=0; j<elementN; j++)
		{
			contactsList = contactsList + nameArray[j] + "\n" + Contact_Map.get(nameArray[j]) + "\n";
		}
		return contactsList;
	}

	@SuppressWarnings("unchecked")
	private String getContactsHighSDK()
	{
		String contactsList = "";
		String oldname = "";
		String oldphone = "";
		if(Contact_Map == null) Contact_Map = new HashMap<String, String>();
		Contact_Map.clear();
		Cursor phones = venusHandle.appActivity.getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				null,
				null,
				null,
				Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
		int c = phones.getCount();
		if(c > 0)
		{
			for(int i = 0; i<c; i++)
			{
				phones.moveToNext();
				String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				if(!oldname.equals("") && !name.equals("") && !oldname.equals(name))
				{
					Contact_Map.put(oldname+"###" + i, oldphone);
					oldname = "";
					oldphone = "";
				}

				oldname = name; 
				if(!oldphone.equals(""))
					oldphone += "," + phone;
				else
					oldphone += phone; 
			}
		}
		if(!oldname.equals(""))
		{
			Contact_Map.put(oldname+"###" + c, oldphone);
		}

		//Sort the contacts by name
		Comparator cmp = Collator.getInstance(java.util.Locale.CHINA);
		int elementN = Contact_Map.size();
		int j = 0;
		String[] nameArray = new String[elementN];
		Set<String> set = Contact_Map.keySet();
		for(String key : set)
		{
			nameArray[j++] = key;
		}
		Arrays.sort(nameArray, cmp);

		//Build the contacts list
		for(j=0; j<elementN; j++)
		{
			contactsList = contactsList + nameArray[j].substring(0,nameArray[j].lastIndexOf("###")) + "\n" + Contact_Map.get(nameArray[j]) + "\n";
		}
		return contactsList;
	}

}
