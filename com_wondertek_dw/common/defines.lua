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
    login = 'MODULE:\\com_wondertek_dw\\login.wdml', -- 首页
    xunjianEdit = 'MODULE:\\com_wondertek_dw\\xunjianEdit.wdml', -- 首页
    xunjianDetail = 'MODULE:\\com_wondertek_dw\\xunjianDetail.wdml', -- 已巡检任务详情
    radioSample = 'MODULE:\\com_wondertek_dw\\radioSample.wdml', -- 首页 none
    yinhuanList = 'MODULE:\\com_wondertek_dw\\yinhuanList.wdml', -- 隐患列表
    yinhuanshangchuan = 'MODULE:\\com_wondertek_dw\\yinhuanshangchuan.wdml', -- 隐患上报
    daibandetail='MODULE:\\com_wondertek_dw\\daibandetail.wdml', -- 待办详情
    daibangongdan = 'MODULE:\\com_wondertek_dw\\daibangongdan.wdml', -- 待办列表
    xunjianrenwu = 'MODULE:\\com_wondertek_dw\\xunjianrenwu.wdml', -- 巡检列表
    search = 'MODULE:\\com_wondertek_dw\\search.wdml', -- 资料搜索
    searchDetail = 'MODULE:\\com_wondertek_dw\\searchDetail.wdml', -- 资料搜索详情 none
    xunjianEdit = 'MODULE:\\com_wondertek_dw\\xunjianEdit.wdml', -- 巡检上报
    notice = 'MODULE:\\com_wondertek_dw\\notice.wdml', -- 公告列表
    noticeDetail = 'MODULE:\\com_wondertek_dw\\noticeDetail.wdml', -- 公告详情
    noticeFav = 'MODULE:\\com_wondertek_dw\\noticeFav.wdml', -- 公告详情
    cheliangguanli = 'MODULE:\\com_wondertek_dw\\cheliangguanli.wdml', -- 车辆管理
    cheliangxiangqing = 'MODULE:\\com_wondertek_dw\\cheliangxiangqing.wdml', -- 车辆详情
    imageDetail = 'MODULE:\\com_wondertek_dw\\imageDetail.wdml', -- 图片详情
    tongjiliebiao = 'MODULE:\\com_wondertek_dw\\tongjiliebiao.wdml', -- 统计列表
    tongxunlu='MODULE:\\com_wondertek_dw\\tongxunlu.wdml', -- 通讯录
    tongxunluDetail='MODULE:\\com_wondertek_dw\\tongxunluDetail.wdml', -- 通讯录详情
    gongdandetail='MODULE:\\com_wondertek_dw\\gongdandetail.wdml', -- 待办详情
    gongdan = 'MODULE:\\com_wondertek_dw\\gongdan.wdml', -- 工单列表
    xunjianSubmit='MODULE:\\com_wondertek_dw\\xunjianEditEx.wdml', -- 工单列表
    setting = 'MODULE:\\com_wondertek_dw\\setting.wdml', -- 系统设置页面
    xunjianTask = 'MODULE:\\com_wondertek_dw\\xunjiantaskDetail.wdml', -- 巡检处理 none
    yinhuanDetail = 'MODULE:\\com_wondertek_dw\\yinhuanDetail.wdml', -- 系统设置页面
    cardispatch = 'MODULE:\\com_wondertek_dw\\carDispatch.wdml', -- 车辆申请
    dateDialog = 'MODULE:\\com_wondertek_dw\\dateDialog.wdml', -- 日期选择
    yibandetail = 'MODULE:\\com_wondertek_dw\\yibandetail.wdml', -- 待办详情
    jump = 'MODULE:\\com_wondertek_dw\\Jump.wdml', -- 跳转页
    xunjianjihua = 'MODULE:\\com_wondertek_dw\\xunjianjihua.wdml', -- 巡检计划
    xunjianzhandian = 'MODULE:\\com_wondertek_dw\\xunjianzhandian.wdml', -- 巡检站点
    xunjianEditEx = 'MODULE:\\com_wondertek_dw\\xunjianEditEx.wdml', -- 巡检子项列表
    xunjianupload = 'MODULE:\\com_wondertek_dw\\xunjianupload.wdml', -- 巡检上传
    xunjianzhandianDetail = 'MODULE:\\com_wondertek_dw\\xunjianzhandianDetail.wdml', -- 巡检站点详情页面
}