package com.wondertek.video.chinanetwifi;

import android.content.Context;

/**
 * 
 * 对会员的注册订购状态进行管理的工具类
 * 
 * @date 2013-2-22
 */
public class MemberOrderManager
{
    private static String MEMBER_STORE_FILE_NAME = "demo_member_wifi_sdk_info";

    private static String TYPE_REGISTER = "register";

    private static String TYPE_ORDER = "order";

    private static MemberOrderManager instance = new MemberOrderManager();

    private MemberOrderManager()
    {

    }

    public static MemberOrderManager getInstance()
    {
        return instance;
    }

    /**
     * 获得上次退出时使用会员号
     * 
     * @param context
     * @return
     */
    public String getLastMemberId(Context context)
    {
        return context.getSharedPreferences(MEMBER_STORE_FILE_NAME, Context.MODE_PRIVATE).getString("last_member_id", "");
    }

    /**
     * 保存当前会员号
     * 
     * @param context
     * @param memberId
     */
    public void storeLastMemberId(Context context, String memberId)
    {
        context.getSharedPreferences(MEMBER_STORE_FILE_NAME, Context.MODE_PRIVATE).edit().putString("last_member_id", memberId).commit();
    }

    /**
     * 查看该会员是否已经注册
     * 
     * @param memberId
     * @return
     */
    public boolean isRegistered(Context context, String memberId)
    {
        return get(context, memberId, TYPE_REGISTER);
    }

    /**
     * 查看该会员是否已经订购
     * 
     * @param memberId
     * @return
     */
    public boolean isOrdered(Context context, String memberId)
    {
        return get(context, memberId, TYPE_ORDER);
    }

    /**
     * 标记该会员的订购状态
     * 
     * @param memberId
     */
    public void markRegistered(Context context, String memberId, boolean isRegistered)
    {
        save(context, memberId, TYPE_REGISTER, isRegistered);
    }

    /**
     * 标记该会员的订购状态
     * 
     * @param context
     * @param memberId
     * @param isOrdered
     */
    public void markOrdered(Context context, String memberId, boolean isOrdered)
    {
        save(context, memberId, TYPE_ORDER, isOrdered);
    }

    private void save(Context context, String member, String type, boolean val)
    {
        String key = member + "_" + type;
        context.getSharedPreferences(MEMBER_STORE_FILE_NAME, Context.MODE_PRIVATE).edit().putBoolean(key, val).commit();
    }

    private boolean get(Context context, String member, String type)
    {
        String key = member + "_" + type;
        return context.getSharedPreferences(MEMBER_STORE_FILE_NAME, Context.MODE_PRIVATE).getBoolean(key, false);
    }

}
