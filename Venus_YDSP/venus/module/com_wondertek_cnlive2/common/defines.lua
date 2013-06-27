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

Reg.com_wondertek_cnlive2 = {
    config = 'com_wondertek_cnlive2_config', -- 配置文件数据仓库
    wlan ='com_wondertek_cnlive2_wlan',  --wlan相关数据仓库
    index = 'com_wondertek_cnlive2_index', -- 入口场景数据仓库标识
    home = 'com_wondertek_cnlive2_home', -- 首页场景数据仓库标识
    detail = 'com_wondertek_cnlive2_detail', -- 详情页场景数据仓库标识
    homespread = 'com_wondertek_cnlive2_homespread',  -- 首页展开页场景数据仓库标识
    search = 'com_wondertek_cnlive2_search', -- 搜索页场景数据仓库标识
    history = 'com_wondertek_cnlive2_history', --播放历史记录数据仓库标识
    comment = 'com_wondertek_cnlive2_comment', --我的评论数据仓库标识
    booking = 'com_wondertek_cnlive2_booking', --我的预约数据仓库标识
    favorite = 'com_wondertek_cnlive2_favorite', --我的收藏数据仓库标识
    accountmgr = 'com_wondertek_cnlive2_accountmgr', --我的账号管理数据仓库标识
    play = 'com_wondertek_cnlive2_play', --播放页数据仓库标识
    list = 'com_wondertek_cnlive2_list', --节目列表页数据仓库标识
    weibo = 'com_wondertek_cnlive2_weibo',
    dianshitai_detail = 'com_wondertek_cnlive2_dianshitai_detail',
    tvspread = 'com_wondertek_cnlive2_tvspread',
    personalcenter = 'com_wondertek_cnlive2_personalcenter'
}
Alias = {
    index = 'MODULE:\\com_wondertek_cnlive2\\index.wdml', -- 入口页
    home = 'MODULE:\\com_wondertek_cnlive2\\home.wdml', -- 首页
    detail = 'MODULE:\\com_wondertek_cnlive2\\detail.wdml', -- 详情页
    live = 'MODULE:\\com_wondertek_cnlive2\\live.wdml', -- 直播页
    channel = 'MODULE:\\com_wondertek_cnlive2\\channel.wdml', -- 频道页
    ranking = 'MODULE:\\com_wondertek_cnlive2\\ranking.wdml', -- 排行榜页
    mytvstations = 'MODULE:\\com_wondertek_cnlive2\\mytvstations.wdml', -- 我的电视台页
    list = 'MODULE:\\com_wondertek_cnlive2\\list.wdml', -- 节目列表页
    personalcenter = 'MODULE:\\com_wondertek_cnlive2\\personalcenter.wdml', -- 个人中心页
    tvspread = 'MODULE:\\com_wondertek_cnlive2\\tvspread.wdml', -- 电视台页展开页
    homespread = 'MODULE:\\com_wondertek_cnlive2\\homespread.wdml', -- 首页展开页
    accountmgr = 'MODULE:\\com_wondertek_cnlive2\\accountmgr.wdml', -- 个人信息管理页
    dianshitai_detail = 'MODULE:\\com_wondertek_cnlive2\\dianshitai_detail.wdml',  -- 电视台详细页
    play = 'MODULE:\\com_wondertek_cnlive2\\play.wdml' -- 播放页
}

E1 = SCREEN_WIDTH / 640
E2 = math.min(SCREEN_WIDTH,SCREEN_HEIGHT) / math.min(640,960)


--一些约定：
--业务发请求收到的消息号 本场景内：三位数（101-999）；公共页面：四位数（1000以上）
--客户端发送请求时，所有http请求head上，必须加上uuid,clientType,ua 参数。
--WD_UUID：客户端根据手机生成的唯一标识
--WD_CLIENT_TYPE：客户端类型，01-iPad，02-aPad，03-iPhone，04-android
--WD_UA:手机类型ua


--用于定义各界面都有可能收到的请求消息号
Msg = {
    register       = 1001,  --用户注册请求的消息号
    login          = 1002,  --用户登录请求的消息号
    uploadHistory  = 1003,  --上传播放历史记录至服务器的请求的消息号
    getHistory     = 1004,  --从服务器获取播放历史记录的请求的消息号
    delHistory     = 1005,  --删除某条播放历史的请求的消息号
    hotKeyword     = 1006,  --从服务器获取热门关键字的请求的消息号
    weiboAuth      = 1007,  --从服务器获取微博授权地址请求的消息号
    weiboBanding   = 1008,  --从微博绑定的消息号
    loginout       = 1009,  --注销用户消息号
    bindUser       = 1010,  --微博绑定用户
    findPassword   = 1011,  --找密码
    searchResult   = 1012,  --搜索结果
}
