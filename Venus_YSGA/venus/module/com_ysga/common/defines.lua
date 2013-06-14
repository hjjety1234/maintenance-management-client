-- | WonderTek [ 网络无处不在，沟通及时到达 ]
-- -----------------------------------------------------------------------------
-- | Copyright (c) 2012, WonderTek, Inc. All Rights Reserved.
-- -----------------------------------------------------------------------------
-- | Author: xxxx <xxxx@xxxx.com>
-- -----------------------------------------------------------------------------
-- @class Defines - Msg、 Reg、 Key
-- @note  常量注册列表
-- -----------------------------------------------------------------------------

Reg.com_ysga = {
    config = 'com_ysga_config', -- 配置文件数据仓库
    index = 'com_ysga_index', -- 入口场景数据仓库标识
    home = 'com_ysga_home', -- 首页场景数据仓库标识
}

Regs = {
    regName = 'com_ysga',
}

Alias = {
    login = 'MODULE:\\com_ysga\\login.wdml', -- 入口页
    home = 'MODULE:\\com_ysga\\home.wdml', -- 首页
    register = 'MODULE:\\com_ysga\\pqy_register.wdml',--用户注册界面
   -- urlServer='http://120.209.131.144:9091/mobileSale/',--外网
   -- imgServer='http://120.209.131.144:9091',
   
   -- 生产环境地址
   -- urlServer='http://120.209.131.143:9092/mobileSale/',
   -- imgServer='http://120.209.131.143:9092',
   
   -- 测试环境地址 
   urlServer='http://120.209.131.152:8080/mobileSale/', 
   imgServer='http://120.209.131.152:8080',
    
    -- urlServer="https://localhost:8282/mobileSale/",--外网新DB,https
    upadteSkin='MODULE:\\com_ysga\\m_updateSkin.wdml',
   --公共webview图片
   browserView = 'MODULE:\\com_ysga\\userApp.wdml', --webview通用页面

    --通知公告
    news = 'MODULE:\\com_ysga\\pqy_news.wdml', -- 公告列表
    newsDetail = 'MODULE:\\com_ysga\\pqy_newsDetail.wdml', -- 公告详情
    newsFav = 'MODULE:\\com_ysga\\pqy_newsFav.wdml', -- 公告收藏

     --到货管理
    daohuo='MODULE:\\com_ysga\\pqy_daohuo.wdml',--我的到货
    huodaoDetail='MODULE:\\com_ysga\\pqy_huodaoDetail.wdml',--到货签收
    --订货管理
    dinghuo='MODULE:\\com_ysga\\pqy_dinghuo.wdml',--我的定货
    dinghuoDetail='MODULE:\\com_ysga\\pqy_dinghuoDetail.wdml',--订货明细
    dinghuoApply='MODULE:\\com_ysga\\pqy_dinghuoApply.wdml',--订货申请
    
     dhgoodChoose='MODULE:\\com_ysga\\pqy_dhgoodChoose.wdml',--选择商品
     setup='MODULE:\\com_ysga\\pqy_setup.wdml',--选择商品
   --信息反馈
    xinxi = 'MODULE:\\com_ysga\\szl_xinxi.wdml', -- 信息反馈列表
    xinxiDetail = 'MODULE:\\com_ysga\\szl_xinxiDetail.wdml', -- 信息反馈详情
    addxinxi = 'MODULE:\\com_ysga\\szl_addxinxi.wdml', -- 增加信息反馈
    baogao = 'MODULE:\\com_ysga\\szl_baogao.wdml', -- 工作报告列表
    baogaoDetail = 'MODULE:\\com_ysga\\szl_baogaoDetail.wdml', -- 工作报告详情
    addbaogao = 'MODULE:\\com_ysga\\szl_addbaogao.wdml',
    kucunpandian = 'MODULE:\\com_ysga\\szl_kucunpandian.wdml', -- 库存盘点页面
    xzsp = 'MODULE:\\com_ysga\\szl_xzsp.wdml', -- 选择商品页面
    
    --丁兵
    test = 'MODULE:\\com_ysga\\test.wdml', -- 考勤列表
    kaoqinlist = 'MODULE:\\com_ysga\\db_kaoqinlist.wdml', -- 考勤列表
    kaoqinaction = 'MODULE:\\com_ysga\\db_kaoqinaction.wdml', -- 考勤
    dateDialog = 'MODULE:\\com_ysga\\dateDialog.wdml', -- 日期选择
    mendianlist = 'MODULE:\\com_ysga\\db_mendianlist.wdml', -- 门店列表
    mendianadd = 'MODULE:\\com_ysga\\db_mendianadd.wdml',
    mendianupd = 'MODULE:\\com_ysga\\db_mendianupdate.wdml',
    mendianlocal='MODULE:\\com_ysga\\db_mendianbiaoji.wdml',
    mendiandetail='MODULE:\\com_ysga\\db_mendiandetail.wdml',
    orignImg='MODULE:\\com_ysga\\db_yuantu.wdml',
    m_picRecord = 'MODULE:\\com_ysga\\m_picRecord.wdml', -- 图片提交
    m_picRecordUpdate = 'MODULE:\\com_ysga\\m_picRecordUpdate.wdml', -- 图片提交
    --徐刚强
    
    diaohuolist = 'MODULE:\\com_ysga\\xgq_diaohuolist.wdml', -- 调货管理首页
    diaohuodetail = 'MODULE:\\com_ysga\\xgq_diaohuodetail.wdml', -- 签收
    diaohuoshenqing = 'MODULE:\\com_ysga\\xgq_diaohuoshenqing.wdml', -- 新增
    diaohuoshangpin = 'MODULE:\\com_ysga\\xgq_shangpinchange.wdml', --商品
    
    kehuguanli = 'MODULE:\\com_ysga\\xgq_kehuguanli.wdml', -- 我的客户首页
    xiugaikehu = 'MODULE:\\com_ysga\\xgq_xiugaikehu.wdml', -- 修改客户
    xinzengkehu = 'MODULE:\\com_ysga\\xgq_xinzengkehu.wdml', -- 新增客户
    chakankehu = 'MODULE:\\com_ysga\\xgq_chakankehu.wdml', -- 新增客户
    
    
    --周本文
    baifangzhixing = 'MODULE:\\com_ysga\\zbw_baifangzhixing.wdml', --拜访执行
    baifangzhixingtijiao = 'MODULE:\\com_ysga\\zbw_baifangzhixingtijiao.wdml', --拜访执行提交
    jishibaifangzhixing = 'MODULE:\\com_ysga\\zbw_jishibaifangzhixing.wdml', --即时拜访
    clientSelect = 'MODULE:\\com_ysga\\bsq_clientSelect.wdml', -- 选择客户
    baifang = 'MODULE:\\com_ysga\\bsq_baifangplan.wdml', -- 拜访计划
    baifangjilu = 'MODULE:\\com_ysga\\zbw_baifangjilu.wdml', -- 拜访记录
    baifangjiluxiugai = 'MODULE:\\com_ysga\\zbw_baifangjiluxiugai.wdml', -- 拜访记录修改
    renyuannew = 'MODULE:\\com_ysga\\zbw_renyuannew.wdml', -- 人员入职登记
    renyuandelete = 'MODULE:\\com_ysga\\zbw_renyuandelete.wdml', -- 人员离职登记
    zbw_dateTimeDialog = 'MODULE:\\com_ysga\\zbw_dateTimeDialog.wdml', -- 人员离职登记
    cc_renyuanUpdata = 'MODULE:\\com_ysga\\zbw_renyuanupdate.wdml',
    weizhichaxun = 'MODULE:\\com_ysga\\wlq_weizhichaxun.wdml',
    renyuanNew='MODULE:\\com_ysga\\zbw_renyuannew.wdml',
    
    --汪珏斌
    wjb_myxiaoliang = 'MODULE:\\com_ysga\\wjb_myxiaoliang.wdml', --销售分析
    wjb_addxiaoliang = 'MODULE:\\com_ysga\\wjb_addxiaoliang.wdml',
    wjb_rixiaoliang = 'MODULE:\\com_ysga\\wjb_rixiaoliang.wdml',
    wjb_shangpinchange = 'MODULE:\\com_ysga\\wjb_shangpinchange.wdml',
    wjb_dateTimeDialog = 'MODULE:\\com_ysga\\wjb_dateTimeDialog.wdml',

    wjb_xundianjilu = 'MODULE:\\com_ysga\\wjb_xundianjilu.wdml',
    wjb_xundianjiluxiugai =  'MODULE:\\com_ysga\\wjb_xundianjiluxiugai.wdml',
    
    wjb_xundianjihua = 'MODULE:\\com_ysga\\wjb_xundianjihua.wdml',---巡店计划
    wjb_kehuxuanze = 'MODULE:\\com_ysga\\wjb_kehuxuanze.wdml',   -----------客户选择
    wjb_xundianzhixing = 'MODULE:\\com_ysga\\wjb_xundianzhixing.wdml',---------巡店执行
    wjb_xundianzhixingtijiao = 'MODULE:\\com_ysga\\wjb_xundianzhixingtijiao.wdml',---------巡店执行提交
    wjb_orignImg = 'MODULE:\\com_ysga\\wjb_orignImg.wdml',--------图像显示
    wjb_jishixundian = 'MODULE:\\com_ysga\\wjb_jishixundian.wdml',---------即时巡店
    
    cc_pepInfo = 'MODULE:\\com_ysga\\cc_pepInfo.wdml',
    cc_pepInfoDetail = 'MODULE:\\com_ysga\\cc_pepInfoDetail.wdml',
}