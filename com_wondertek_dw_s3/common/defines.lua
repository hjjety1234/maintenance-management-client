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

Reg.com_wondertek_dw_s3 = {
    config = 'com_wondertek_dw_s3_config', -- 配置文件数据仓库
    index = 'com_wondertek_dw_s3_index', -- 入口场景数据仓库标识
    home = 'com_wondertek_dw_s3_home', -- 首页场景数据仓库标识
    xunjianzhandianDetail = 'com_wondertek_dw_s3_xunjianzhandianDetail' -- 巡检站点页面数据仓库
}

Regs = {
    regName = 'com_wondertek_dw_s3',
}

Alias = {
    index = 'MODULE:\\com_wondertek_dw_s3\\index.wdml', -- 入口页
    home = 'MODULE:\\com_wondertek_dw_s3\\home.wdml', -- 首页
    login = 'MODULE:\\com_wondertek_dw_s3\\login.wdml', -- 登录
    m_login = 'MODULE:\\com_wondertek_dw_s3\\m_login.wdml', -- 登录
    xunjianEdit = 'MODULE:\\com_wondertek_dw_s3\\xunjianEdit.wdml', -- 首页
    xunjianDetail = 'MODULE:\\com_wondertek_dw_s3\\xunjianDetail.wdml', -- 已巡检任务详情
    radioSample = 'MODULE:\\com_wondertek_dw_s3\\radioSample.wdml', -- 首页 none
    yinhuanList = 'MODULE:\\com_wondertek_dw_s3\\yinhuanList.wdml', -- 隐患列表
    yinhuanshangchuan = 'MODULE:\\com_wondertek_dw_s3\\yinhuanshangchuan.wdml', -- 隐患上报
    m_daibandetail='MODULE:\\com_wondertek_dw_s3\\m_daibandetail.wdml', -- 待办详情
    m_daibangongdan = 'MODULE:\\com_wondertek_dw_s3\\m_daibangongdan.wdml', -- 待办列表
    xunjianrenwu = 'MODULE:\\com_wondertek_dw_s3\\xunjianrenwu.wdml', -- 巡检列表
    search = 'MODULE:\\com_wondertek_dw_s3\\search.wdml', -- 资料搜索
    searchDetail = 'MODULE:\\com_wondertek_dw_s3\\searchDetail.wdml', -- 资料搜索详情 none
    xunjianEdit = 'MODULE:\\com_wondertek_dw_s3\\xunjianEdit.wdml', -- 巡检上报
    notice = 'MODULE:\\com_wondertek_dw_s3\\notice.wdml', -- 公告列表
    noticeDetail = 'MODULE:\\com_wondertek_dw_s3\\noticeDetail.wdml', -- 公告详情
    noticeFav = 'MODULE:\\com_wondertek_dw_s3\\noticeFav.wdml', -- 公告详情
    cheliangguanli = 'MODULE:\\com_wondertek_dw_s3\\m_cheliangguanli.wdml', -- 车辆管理
    cheliangxiangqing = 'MODULE:\\com_wondertek_dw_s3\\m_cheliangxiangqing.wdml', -- 车辆详情
    imageDetail = 'MODULE:\\com_wondertek_dw_s3\\imageDetail.wdml', -- 图片详情
    orignImg = 'MODULE:\\com_wondertek_dw_s3\\orignImg.wdml', -- 查看原图（隐患上传）
    tongjiliebiao = 'MODULE:\\com_wondertek_dw_s3\\tongjiliebiao.wdml', -- 统计列表
    tongxunlu='MODULE:\\com_wondertek_dw_s3\\tongxunlu.wdml', -- 通讯录
    tongxunluDetail='MODULE:\\com_wondertek_dw_s3\\tongxunluDetail.wdml', -- 通讯录详情
    gongdandetail='MODULE:\\com_wondertek_dw_s3\\gongdandetail.wdml', -- 待办详情
    gongdan = 'MODULE:\\com_wondertek_dw_s3\\gongdan.wdml', -- 工单列表
    xunjianSubmit='MODULE:\\com_wondertek_dw_s3\\xunjianEditEx.wdml', -- 工单列表
    setting = 'MODULE:\\com_wondertek_dw_s3\\setting.wdml', -- 系统设置页面
    xunjianTask = 'MODULE:\\com_wondertek_dw_s3\\xunjiantaskDetail.wdml', -- 巡检处理 none
    yinhuanDetail = 'MODULE:\\com_wondertek_dw_s3\\yinhuanDetail.wdml', -- 系统设置页面
    cardispatch = 'MODULE:\\com_wondertek_dw_s3\\m_carDispatch.wdml', -- 车辆申请
    dateDialog = 'MODULE:\\com_wondertek_dw_s3\\dateDialog.wdml', -- 日期选择
    m_yibandetail = 'MODULE:\\com_wondertek_dw_s3\\m_yibandetail.wdml', -- 已办详情
    jump = 'MODULE:\\com_wondertek_dw_s3\\Jump.wdml', -- 跳转页
    xunjianjihua = 'MODULE:\\com_wondertek_dw_s3\\xunjianjihua.wdml', -- 巡检计划
    xunjianzhandian = 'MODULE:\\com_wondertek_dw_s3\\xunjianzhandian.wdml', -- 巡检站点
    xunjianEditEx = 'MODULE:\\com_wondertek_dw_s3\\xunjianEditEx.wdml', -- 巡检子项列表
    xunjianupload = 'MODULE:\\com_wondertek_dw_s3\\xunjianupload.wdml', -- 巡检上传
    xunjianzhandianDetail = 'MODULE:\\com_wondertek_dw_s3\\xunjianzhandianDetail.wdml', -- 巡检站点详情页面
    xunjiantijiao = 'MODULE:\\com_wondertek_dw_s3\\xunjiantijiao.wdml', -- 巡检项提交页面
    chart = 'MODULE:\\com_wondertek_dw_s3\\chart.wdml', -- 统计报表demo
    
    ------------------------------Metro Style ------------------------------------------
    home_new = 'MODULE:\\com_wondertek_dw_s3\\home_new.wdml',  -- 首页
    m_login = 'MODULE:\\com_wondertek_dw_s3\\m_login.wdml',    -- 登录
    m_daibangongdan = 'MODULE:\\com_wondertek_dw_s3\\m_daibangongdan.wdml', -- 待办列表
    m_xunjianjihua = 'MODULE:\\com_wondertek_dw_s3\\m_xunjianjihua.wdml', -- 巡检计划
    m_yinhuanList = 'MODULE:\\com_wondertek_dw_s3\\m_yinhuanList.wdml', -- 隐患列表
    m_yinhuanshangchuan = 'MODULE:\\com_wondertek_dw_s3\\m_yinhuanshangchuan.wdml', -- 隐患上报
    m_yinhuanDetail = 'MODULE:\\com_wondertek_dw_s3\\m_yinhuanDetail.wdml', -- 隐患详情
    m_cardispatch = 'MODULE:\\com_wondertek_dw_s3\\m_carDispatch.wdml', -- 车辆申请
    m_cheliangguanli = 'MODULE:\\com_wondertek_dw_s3\\m_cheliangguanli.wdml', -- 车辆管理
    m_daibangongdan = 'MODULE:\\com_wondertek_dw_s3\\m_daibangongdan.wdml', -- 待办列表
    m_xunjianzhandian = 'MODULE:\\com_wondertek_dw_s3\\m_xunjianzhandian.wdml', -- 巡检站点
    m_xunjianEditEx = 'MODULE:\\com_wondertek_dw_s3\\m_xunjianEditEx.wdml', -- 巡检子项列表
    m_xunjiantijiao = 'MODULE:\\com_wondertek_dw_s3\\m_xunjiantijiao.wdml', -- 巡检项提交页面
    m_xunjianupload = 'MODULE:\\com_wondertek_dw_s3\\m_xunjianupload.wdml', -- 巡检上传
    m_notice = 'MODULE:\\com_wondertek_dw_s3\\m_notice.wdml', -- 公告列表(江峰)
    m_noticeDetail = 'MODULE:\\com_wondertek_dw_s3\\m_noticeDetail.wdml', -- 公告详情（江峰）
    m_noticeFav = 'MODULE:\\com_wondertek_dw_s3\\m_noticeFav.wdml', -- 收藏列表（江峰）
    m_daibandetail='MODULE:\\com_wondertek_dw_s3\\m_daibandetail.wdml', -- 待办详情
    m_yibandetail = 'MODULE:\\com_wondertek_dw_s3\\m_yibandetail.wdml', -- 待办详情
    m_gongdanliucheng = 'MODULE:\\com_wondertek_dw_s3\\m_gongdanliucheng.wdml', -- 工单流程
    m_tongxunlu = 'MODULE:\\com_wondertek_dw_s3\\m_tongxunlu.wdml', -- 通讯录（江峰）
    m_tongxunluDetail = 'MODULE:\\com_wondertek_dw_s3\\m_tongxunluDetail.wdml', -- 通讯录（江峰）
    
    home_sheng = 'MODULE:\\com_wondertek_dw_s3\\home_sheng.wdml',   -- 省公司首页
    m_kaohetongji = 'MODULE:\\com_wondertek_dw_s3\\m_kaohetongji.wdml',  -- 考核统计
    m_kaohebaobiao = 'MODULE:\\com_wondertek_dw_s3\\m_kaohebaobiao.wdml', -- 考核报表
    m_staff_statistical = 'MODULE:\\com_wondertek_dw_s3\\m_staff_statistical.wdml', -- 人员资质统计
    m_search = 'MODULE:\\com_wondertek_dw_s3\\m_search.wdml',--资源查询
    
    m_xianluxunjian = 'MODULE:\\com_wondertek_dw_s3\\m_xianluxunjian.wdml',--线路巡检
    
    m_setting = 'MODULE:\\com_wondertek_dw_s3\\m_setting.wdml', -- 系统设置页面(江峰)
}

