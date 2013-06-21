-- | WonderTek [ 网络无处不在，沟通及时到达 ]
-- -----------------------------------------------------------------------------
-- | Copyright (c) 2012, WonderTek, Inc. All Rights Reserved.
-- -----------------------------------------------------------------------------
-- | Author: xxxx <xxxx@xxxx.com>
-- -----------------------------------------------------------------------------
-- @class Defines - Msg、 Reg、 Key
-- @note  常量注册列表
-- -----------------------------------------------------------------------------

Reg.com_szjl = {
    config = 'com_szjl_config', -- 配置文件数据仓库
    index = 'com_szjl_index', -- 入口场景数据仓库标识
    home = 'com_szjl_home', -- 首页场景数据仓库标识
}

Regs = {
    regName = 'com_szjl',
}

Alias = {  
    home = 'MODULE:\\com_szjl\\home.wdml', -- 首页
    urlServer='http://120.209.131.152:8081/szjl/',
    --urlServer='http://120.209.131.144:9092/szjl/',
    index = 'MODULE:\\com_szjl\\index.wdml',
    wjb_kaoqindaka = 'MODULE:\\com_szjl\\wjb_kaoqindaka.wdml',
    wjb_kaoqinjilu = 'MODULE:\\com_szjl\\wjb_kaoqinjilu.wdml',
    dateDialog = 'MODULE:\\com_szjl\\dateDialog.wdml',
    newslist = 'MODULE:\\com_szjl\\xgq_newslist.wdml',
    newsdetail = 'MODULE:\\com_szjl\\xgq_newsdetail.wdml',
    wodejianli = 'MODULE:\\com_szjl\\xgq_wodejianli.wdml',
    xgq_kemulist = 'MODULE:\\com_szjl\\xgq_kemulist.wdml',
	xgq_shenheyijian = 'MODULE:\\com_szjl\\xgq_shenheyijian.wdml',
    login = 'MODULE:\\com_szjl\\login.wdml',
    wjb_bbxs = 'MODULE:\\com_szjl\\wjb_bbxs.wdml',
    wjb_bb = 'MODULE:\\com_szjl\\wjb_bb.wdml',
    wjb_orignImg = 'MODULE:\\com_szjl\\wjb_orignImg.wdml',
    wjb_xianlu = 'MODULE:\\com_szjl\\wjb_xianlu.wdml',
	wjb_jizhanshebei = 'MODULE:\\com_szjl\\wjb_jizhanshebei.wdml',
	wjb_jizhantujian = 'MODULE:\\com_szjl\\wjb_jizhantujian.wdml',
	
	gdgc_jccl = 'MODULE:\\com_szjl\\xgq_gdgc_jccl.wdml',
	gdgc_lyfc = 'MODULE:\\com_szjl\\xgq_gdgc_lyfc.wdml',
    gdgc_kwgc = 'MODULE:\\com_szjl\\xgq_gdgc_kwgc.wdml',
    gdgc_gdps = 'MODULE:\\com_szjl\\xgq_gdgc_gdps.wdml',
    gdgc_gdys = 'MODULE:\\com_szjl\\xgq_gdgc_gdys.wdml',
    gdgc_rsk = 'MODULE:\\com_szjl\\xgq_gdgc_rsk.wdml',

    wjb_jiaoliuyinru = "MODULE:\\com_szjl\\wjb_jiaoliuyinru.wdml",
    wjb_jifangweiqiangbufen = "MODULE:\\com_szjl\\wjb_jifangweiqiangbufen.wdml",
    wjb_peitaobufen = "MODULE:\\com_szjl\\wjb_peitaobufen.wdml",
    wjb_qianqizhunbei = "MODULE:\\com_szjl\\wjb_qianqizhunbei.wdml",
    wjb_shebeibufen = "MODULE:\\com_szjl\\wjb_shebeibufen.wdml",
    wjb_tajibufen = "MODULE:\\com_szjl\\wjb_tajibufen.wdml",
    wjb_tietaanzhuang = "MODULE:\\com_szjl\\wjb_tietaanzhuang.wdml",
    wjb_yunweijiaojieyanshou = "MODULE:\\com_szjl\\wjb_yunweijiaojieyanshou.wdml",
}