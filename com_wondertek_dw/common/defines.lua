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
    home_jiakgly = 'MODULE:\\com_wondertek_dw\\home_jiakgly.wdml', --家客管理员首页
    
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
    m_yinhuangongdanlist = 'MODULE:\\com_wondertek_dw\\m_yinhuangongdanlist.wdml', -- 隐患工单下隐患列表 
    m_dangerOrderDetail = 'MODULE:\\com_wondertek_dw\\m_dangerOrderDetail.wdml', -- 隐患工单详情界面
    m_dangerdeal = 'MODULE:\\com_wondertek_dw\\m_dangerdeal.wdml', -- 隐患处理界面
    m_dangerorignImg = 'MODULE:\\com_wondertek_dw\\m_dangerorignImg.wdml', -- 隐患工单拍照查看原图
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
    m_xianluomit = 'MODULE:\\com_wondertek_dw\\m_xianluomit.wdml',--线路漏检提醒 
    m_xianluxunjianzancun = 'MODULE:\\com_wondertek_dw\\m_xianluxunjianzancun.wdml',--线路巡检暂存  
    
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
    m_electricity = 'MODULE:\\com_wondertek_dw\\m_electricity.wdml',  --小区发电率统计
    m_linecount =  'MODULE:\\com_wondertek_dw\\m_linecount.wdml',     --线路障碍数 
    m_linecounttime = 'MODULE:\\com_wondertek_dw\\m_linecounttime.wdml',     --线路障碍时长
    m_jikezhuanye= 'MODULE:\\com_wondertek_dw\\m_jikezhuanye.wdml',     --集客障碍率
    m_grouptime= 'MODULE:\\com_wondertek_dw\\m_grouptime.wdml',     --集客障碍处理及时率
    m_jiakezhuanye = 'MODULE:\\com_wondertek_dw\\m_jiakezhuanye.wdml',     --家客业务咨询投诉比
    m_about = 'MODULE:\\com_wondertek_dw\\m_about.wdml',     --关于
    m_password = 'MODULE:\\com_wondertek_dw\\m_password.wdml',     --修改密码
    m_system = 'MODULE:\\com_wondertek_dw\\m_system.wdml',     --新系统设置
    
    m_xunjianewmlist = "MODULE:\\com_wondertek_dw\\m_xunjianewmlist.wdml",      -- 综合覆盖巡检二维码列表
    m_xunjianewmtijiao = "MODULE:\\com_wondertek_dw\\m_xunjianewmtijiao.wdml",  -- 综合覆盖巡检巡检项提交

    m_peocar = "MODULE:\\com_wondertek_dw\\m_peocar.wdml",  -- 人车统计
    m_resource = "MODULE:\\com_wondertek_dw\\m_resource.wdml",  -- 资源统计
    m_resource_line = "MODULE:\\com_wondertek_dw\\m_resource_line.wdml",  -- 传输线路统计
    m_resource_repeater = "MODULE:\\com_wondertek_dw\\m_resource_repeater.wdml",  -- 直放站统计
    m_resource_wlan = "MODULE:\\com_wondertek_dw\\m_resource_wlan.wdml",  -- WLAN统计
    m_resource_tower = "MODULE:\\com_wondertek_dw\\m_resource_tower.wdml",  -- 铁塔统计
    m_resource_group = "MODULE:\\com_wondertek_dw\\m_resource_group.wdml",  -- 集客统计
    m_resource_home = "MODULE:\\com_wondertek_dw\\m_resource_home.wdml",  -- 家庭宽带统计
    m_quality = "MODULE:\\com_wondertek_dw\\m_quality.wdml",  -- 质量指标（基站）统计
    m_qualityLine = "MODULE:\\com_wondertek_dw\\m_qualityLine.wdml",  -- 质量指标（线路）统计 
    m_qualityZonghefugai = "MODULE:\\com_wondertek_dw\\m_qualityZonghefugai.wdml",  -- 质量指标（综合覆盖）统计 
    m_qualityJike = "MODULE:\\com_wondertek_dw\\m_qualityJike.wdml",  -- 质量指标（集客）统计
    m_qualityJiake = "MODULE:\\com_wondertek_dw\\m_qualityJiake.wdml",  -- 质量指标（家客）统计
    m_qualityTower = "MODULE:\\com_wondertek_dw\\m_qualityTower.wdml",  -- 质量指标（铁塔）统计
    m_cost = "MODULE:\\com_wondertek_dw\\m_cost.wdml",  -- 费用统计
    m_kaohetongji_new = "MODULE:\\com_wondertek_dw\\m_kaohetongji_new.wdml",  -- 考核统计 
    m_xunjiantongji_new = "MODULE:\\com_wondertek_dw\\m_xunjiantongji_new.wdml",  -- 巡检统计
    home_sheng_manager = 'MODULE:\\com_wondertek_dw\\home_sheng_manager.wdml',   -- 省公司管理员首页
    home_city = 'MODULE:\\com_wondertek_dw\\home_city.wdml',   -- 地市领导首页 
    home_city_manager = 'MODULE:\\com_wondertek_dw\\home_city_manager.wdml',   -- 地市管理员首页
--省公司领导资源查询页面
    m_resource1 = "MODULE:\\com_wondertek_dw\\m_resource1.wdml",  -- 资源统计
    m_resource_line1 = "MODULE:\\com_wondertek_dw\\m_resource_line1.wdml",  -- 传输线路统计
    m_resource_repeater1 = "MODULE:\\com_wondertek_dw\\m_resource_repeater1.wdml",  -- 直放站统计
    m_resource_wlan1 = "MODULE:\\com_wondertek_dw\\m_resource_wlan1.wdml",  -- WLAN统计
    m_resource_tower1 = "MODULE:\\com_wondertek_dw\\m_resource_tower1.wdml",  -- 铁塔统计
    m_resource_group1 = "MODULE:\\com_wondertek_dw\\m_resource_group1.wdml",  -- 集客统计
    m_resource_home1 = "MODULE:\\com_wondertek_dw\\m_resource_home1.wdml",  -- 家庭宽带统计          
    m_yinhuantongji = "MODULE:\\com_wondertek_dw\\m_yinhuantongji.wdml",  -- 隐患统计   
    m_renchepeizhi = "MODULE:\\com_wondertek_dw\\m_renchepeizhi.wdml",  -- 人车配置   


    m_biaoganGuanLi =  "MODULE:\\com_wondertek_dw\\m_biaoganGuanLi.wdml",  -- 标杆管理
    m_biaoganGuanLizq =  "MODULE:\\com_wondertek_dw\\m_biaoganGuanLizq.wdml",  -- 标杆管理

    home_sheng_leader = "MODULE:\\com_wondertek_dw\\home_sheng_leader.wdml",
}

