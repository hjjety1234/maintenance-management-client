-- -----------------------------------------------------------------------------
-- | WonderTek [ 网络无处不在，沟通及时到达 ]
-- -----------------------------------------------------------------------------
-- | Copyright (c) 2012, WonderTek, Inc. All Rights Reserved.
-- -----------------------------------------------------------------------------
-- | Author: xxxx <xxxx@xxxx.com>
-- -----------------------------------------------------------------------------
-- @class Defines - Msg、 Reg、 Key
-- @note  常量注册列表
-- -----------------------------------------------------------------------------

Reg.com_wondertek_dw = {
    config = 'com_wondertek_dw_config', -- 配置文件数据仓库
    index = 'com_wondertek_dw_index', -- 入口场景数据仓库标识
    home = 'com_wondertek_dw_home', -- 首页场景数据仓库标识
    xunjianzhandianDetail = 'com_wondertek_dw_xunjianzhandianDetail' -- 巡检站点页面数据仓库
}

Regs = {
    regName = 'com_wondertek_dw',
}

Alias = {
    index = 'MODULE:\\com_wondertek_dw\\index.wdml', -- 入口页
    home = 'MODULE:\\com_wondertek_dw\\home.wdml', -- 首页
    login = 'MODULE:\\com_wondertek_dw\\login.wdml', -- 登录
    m_login = 'MODULE:\\com_wondertek_dw\\m_login.wdml', -- 登录
    home_xianlu = 'MODULE:\\com_wondertek_dw\\home_xianlu.wdml',--巡检员首页
    home_jzgly = 'MODULE:\\com_wondertek_dw\\home_jzgly.wdml', --基站管理员首页
    home_xlgly = 'MODULE:\\com_wondertek_dw\\home_xlgly.wdml', --线路管理员首页
    home_jkgly = 'MODULE:\\com_wondertek_dw\\home_jkgly.wdml', --集客管理员首页
    
    ------------------------------Metro Style ------------------------------------------
    imageDetail = 'MODULE:\\com_wondertek_dw\\imageDetail.wdml', -- 图片详情
    orignImg = 'MODULE:\\com_wondertek_dw\\orignImg.wdml', -- 查看原图（隐患上传）
    m_picRecord = 'MODULE:\\com_wondertek_dw\\m_picRecord.wdml', -- 进入拍照页面进行录音
    home_new = 'MODULE:\\com_wondertek_dw\\home_new.wdml',  -- 首页
    m_login = 'MODULE:\\com_wondertek_dw\\m_login.wdml',    -- 登录
    m_daibangongdan = 'MODULE:\\com_wondertek_dw\\m_daibangongdan.wdml', -- 待办列表
    m_xunjianjihua = 'MODULE:\\com_wondertek_dw\\m_xunjianjihua.wdml', -- 巡检计划
    m_yinhuanList = 'MODULE:\\com_wondertek_dw\\m_yinhuanList.wdml', -- 隐患列表
    m_yinhuanshangchuan = 'MODULE:\\com_wondertek_dw\\m_yinhuanshangchuan.wdml', -- 隐患上报
    m_yinhuanDetail = 'MODULE:\\com_wondertek_dw\\m_yinhuanDetail.wdml', -- 隐患详情
    m_cardispatch = 'MODULE:\\com_wondertek_dw\\m_carDispatch.wdml', -- 车辆申请
    m_cheliangxiangqing = 'MODULE:\\com_wondertek_dw\\m_cheliangxiangqing.wdml',--车辆详情
    m_cheliangguanli = 'MODULE:\\com_wondertek_dw\\m_cheliangguanli.wdml', -- 车辆管理
    m_dateDialogTime = 'MODULE:\\com_wondertek_dw\\m_dateDialogTime.wdml', ---车辆申请时间选择器
    m_daibangongdan = 'MODULE:\\com_wondertek_dw\\m_daibangongdan.wdml', -- 待办列表
    m_xunjianzhandian = 'MODULE:\\com_wondertek_dw\\m_xunjianzhandian.wdml', -- 巡检站点
    m_xunjianEditEx = 'MODULE:\\com_wondertek_dw\\m_xunjianEditEx.wdml', -- 巡检子项列表
    m_xunjiantijiao = 'MODULE:\\com_wondertek_dw\\m_xunjiantijiao.wdml', -- 巡检项提交页面
    m_xunjianupload = 'MODULE:\\com_wondertek_dw\\m_xunjianupload.wdml', -- 巡检上传
    m_notice = 'MODULE:\\com_wondertek_dw\\m_notice.wdml', -- 公告列表(江峰)
    m_noticeDetail = 'MODULE:\\com_wondertek_dw\\m_noticeDetail.wdml', -- 公告详情（江峰）
    m_noticeFav = 'MODULE:\\com_wondertek_dw\\m_noticeFav.wdml', -- 收藏列表（江峰）
    m_daibandetail='MODULE:\\com_wondertek_dw\\m_daibandetail.wdml', -- 待办详情
    m_yibandetail = 'MODULE:\\com_wondertek_dw\\m_yibandetail.wdml', -- 待办详情
    m_gongdanliucheng = 'MODULE:\\com_wondertek_dw\\m_gongdanliucheng.wdml', -- 工单流程
    m_tongxunlu = 'MODULE:\\com_wondertek_dw\\m_tongxunlu.wdml', -- 通讯录（江峰）
    m_tongxunluDetail = 'MODULE:\\com_wondertek_dw\\m_tongxunluDetail.wdml', -- 通讯录（江峰）
    
    home_sheng = 'MODULE:\\com_wondertek_dw\\home_sheng.wdml',   -- 省公司首页
    m_kaohetongji = 'MODULE:\\com_wondertek_dw\\m_kaohetongji.wdml',  -- 考核统计
    m_kaohebaobiao = 'MODULE:\\com_wondertek_dw\\m_kaohebaobiao.wdml', -- 考核报表
    m_staff_statistical = 'MODULE:\\com_wondertek_dw\\m_staff_statistical.wdml', -- 人员资质统计
    m_search = 'MODULE:\\com_wondertek_dw\\m_search.wdml',--资源查询
	m_tieta = 'MODULE:\\com_wondertek_dw\\m_tieta.wdml',--铁塔查询
	m_tietadetail = 'MODULE:\\com_wondertek_dw\\m_tietadetail.wdml',--铁塔详情
    m_zonghefugai = 'MODULE:\\com_wondertek_dw\\m_zonghefugai.wdml',--综合覆盖
    m_wlandetail = 'MODULE:\\com_wondertek_dw\\m_wlandetail.wdml',--WLAN详情
    m_zhifangdetail = 'MODULE:\\com_wondertek_dw\\m_zhifangdetail.wdml',--直放站详情
    m_dateDialog = 'MODULE:\\com_wondertek_dw\\m_dateDialog.wdml',--日期下拉框
    m_jikejiake_search = 'MODULE:\\com_wondertek_dw\\m_jikejiake_search.wdml',--集客家客搜索
    m_jikejiake_detail = 'MODULE:\\com_wondertek_dw\\m_jikejiakedetail.wdml',--集客家客详情
    m_resourcereport = 'MODULE:\\com_wondertek_dw\\m_resourcereport.wdml',--资源统计
    
    m_xianluxunjian = 'MODULE:\\com_wondertek_dw\\m_xianluxunjian.wdml',--线路巡检
    
    m_setting = 'MODULE:\\com_wondertek_dw\\m_setting.wdml', -- 系统设置页面(江峰)
    m_zhandiandaohang = 'MODULE:\\com_wondertek_dw\\m_zhandiandaohang.wdml', -- 站点导航
    m_xunjiandaohang = 'MODULE:\\com_wondertek_dw\\m_xunjiandaohang.wdml', -- 巡检导航
    m_zhandiansousuo = 'MODULE:\\com_wondertek_dw\\m_zhandiansousuo.wdml', -- 站点搜索
    m_daohangsousuo = 'MODULE:\\com_wondertek_dw\\m_daohangsousuo.wdml', -- 导航搜索
    m_renyuantongji = 'MODULE:\\com_wondertek_dw\\m_renyuantongji.wdml', -- 人员统计
    m_xunjiantongji = 'MODULE:\\com_wondertek_dw\\m_xunjiantongji.wdml', -- 巡检统计
    monthDialog = 'MODULE:\\com_wondertek_dw\\monthDialog.wdml', -- 人员统计
    m_basestation='MODULE:\\com_wondertek_dw\\m_basestation.wdml',
    m_zonghefugai='MODULE:\\com_wondertek_dw\\m_zonghefugai.wdml',
    m_tieta='MODULE:\\com_wondertek_dw\\m_tieta.wdml',
    m_stationdetail='MODULE:\\com_wondertek_dw\\m_stationdetail.wdml',

    m_sitecheck='MODULE:\\com_wondertek_dw\\m_sitecheck.wdml',
    m_checkzhandian='MODULE:\\com_wondertek_dw\\m_checkzhandian.wdml',
    m_sitemajor='MODULE:\\com_wondertek_dw\\m_sitemajor.wdml',
    m_checkhistory='MODULE:\\com_wondertek_dw\\m_checkhistory.wdml',
    m_sitecheckdetail='MODULE:\\com_wondertek_dw\\m_sitecheckdetail.wdml',
    m_jiucuolishi='MODULE:\\com_wondertek_dw\\m_jiucuolishi.wdml', -- 纠错历史记录
    m_jizhanzhuanye='MODULE:\\com_wondertek_dw\\m_jizhanzhuanye.wdml',

    m_xiaoqucount = 'MODULE:\\com_wondertek_dw\\m_xiaoqucount.wdml',  --小区完好率统计
    m_electricity = 'MODULE:\\com_wondertek_dw\\m_electricity.wdml',  --小区完好率统计


    
}

