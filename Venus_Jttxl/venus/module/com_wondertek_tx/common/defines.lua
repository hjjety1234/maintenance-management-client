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

Reg.com_wondertek_tx = {
    config = 'com_wondertek_tx_config', -- 配置文件数据仓库
    index = 'com_wondertek_tx_index', -- 入口场景数据仓库标识
    home = 'com_wondertek_tx_home', -- 首页场景数据仓库标识
    xunjianzhandianDetail = 'com_wondertek_tx_xunjianzhandianDetail' -- 巡检站点页面数据仓库
}

Regs = {
    regName = 'com_wondertek_tx',
}

Alias = {
    index = 'MODULE:\\com_wondertek_tx\\index.wdml', -- 入口页

    m_newUserLeader = 'MODULE:\\com_wondertek_tx\\m_newUserLeader.wdml', -- 首次安装软件进入新手引导页面

    genxin = 'MODULE:\\com_wondertek_tx\\genxin.wdml', -- 设置页面中的新手引导页面

    m_login = 'MODULE:\\com_wondertek_tx\\m_login.wdml', -- 登录
  
    m_dateDialog = 'MODULE:\\com_wondertek_tx\\m_dateDialog.wdml',--日期下拉框
    
    
    m_renyuantongji = 'MODULE:\\com_wondertek_tx\\m_renyuantongji.wdml', -- 系统首页

   
    m_system = 'MODULE:\\com_wondertek_tx\\m_system.wdml',     --新系统设置
    
    m_zuzhijiegou = 'MODULE:\\com_wondertek_tx\\m_zuzhijiegou.wdml',     --组织机构列表

	m_xiugaimima = 'MODULE:\\com_wondertek_tx\\m_xiugaimima.wdml',     --新修改密码

    m_lianxirenlist = 'MODULE:\\com_wondertek_tx\\m_lianxirenlist.wdml', --联系人列表

    m_searchContacter = 'MODULE:\\com_wondertek_tx\\m_searchContacter.wdml', --联系人搜索列表
    
    m_personalInfo = 'MODULE:\\com_wondertek_tx\\m_personalInfo.wdml', --联系人信息
    
    m_myInfo = 'MODULE:\\com_wondertek_tx\\m_myInfo.wdml', --个人信息
    
    m_editInfo = 'MODULE:\\com_wondertek_tx\\m_editInfo.wdml', --编辑个人信息

    m_picRecord = 'MODULE:\\com_wondertek_tx\\m_picRecord.wdml', -- 图片提交
    
    m_aboutus = 'MODULE:\\com_wondertek_tx\\m_aboutus.wdml', --关于
    
    m_register = 'MODULE:\\com_wondertek_tx\\m_register.wdml', --关于

    m_suggest = 'MODULE:\\com_wondertek_tx\\m_suggest.wdml', --反馈意见

    m_recentContacter = 'MODULE:\\com_wondertek_tx\\m_recentContacter.wdml', -- 最近联系人
    
    m_local = 'MODULE:\\com_wondertek_tx\\m_local.wdml', -- 本机通讯录
    
    m_localsetup = 'MODULE:\\com_wondertek_tx\\m_localsetup.wdml', -- 本机通讯录设置
    
    url_server = 'http://120.209.138.173:8080/',     --测试服务器地址
    m_update = 'MODULE:\\com_wondertek_tx\\m_update.wdml', -- 入口页

    --url_server = 'http://120.209.131.147:8088/'  --生产服务器地址
}

