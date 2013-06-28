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

Reg.com_wondertek_ydsx = {
    config = 'com_wondertek_ydsx_config', -- 配置文件数据仓库
    index = 'com_wondertek_ydsx_index', -- 入口场景数据仓库标识
    home = 'com_wondertek_ydsx_home', -- 首页场景数据仓库标识
    help = 'com_wondertek_ydsx_help', -- 帮主页场景数据仓库标识
    detail = 'com_wondertek_ydsx_detail', -- 详情页场景数据仓库标识
}
Alias = {
    index = 'MODULE:\\com_wondertek_ydsx\\index.wdml', -- 入口页
    home = 'MODULE:\\com_wondertek_ydsx\\home.wdml', -- 首页
    help = 'MODULE:\\com_wondertek_ydsx\\help.wdml', -- 帮助页
    detail = 'MODULE:\\com_wondertek_ydsx\\detail.wdml', -- 详情页
    voiceinput = 'MODULE:\\com_wondertek_ydsx\\voiceinput.wdml', -- 语音输入页面
}