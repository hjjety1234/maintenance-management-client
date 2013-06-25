-- | WonderTek [ 网络无处不在，沟通及时到达 ]
-- -----------------------------------------------------------------------------
-- | Copyright (c) 2012, WonderTek, Inc. All Rights Reserved.
-- -----------------------------------------------------------------------------
-- | Author: xxxx <xxxx@xxxx.com>
-- -----------------------------------------------------------------------------
-- @class Defines - Msg、 Reg、 Key
-- @note  常量注册列表
-- -----------------------------------------------------------------------------

Reg.com_xsgj = {
    config = 'com_xsgj_config', -- 配置文件数据仓库
    index = 'com_xsgj_index', -- 入口场景数据仓库标识
    home = 'com_xsgj_home', -- 首页场景数据仓库标识
}

Regs = {
    regName = 'com_xsgj',
}

Alias = {
    login = 'MODULE:\\com_xsgj\\login.wdml', -- 入口页
    home = 'MODULE:\\com_xsgj\\home_bak.wdml', -- 首页
    register = 'MODULE:\\com_xsgj\\pqy_register.wdml',--用户注册界面
    
    urlServer = 'http://120.209.131.152:8080/mobileSale/', --外网
    imgServer = 'http://120.209.131.152:8080',
   
    -- urlServer='http://120.209.131.144:9091/mobileSale/',  --外网
    -- imgServer='http://120.209.131.144:9091',
    
    -- urlServer = 'http://120.209.131.143:9092/mobileSale/', --外网
    -- imgServer = 'http://120.209.131.143:9092',
	
	-- urlServer = 'http://localhost:8282/mobileSale/',--内网新DB
	-- imgServer = 'http://localhost:8282',
    
    -- urlServer="https://localhost:8282/mobileSale/",--外网新DB,https
    --公共webview图片
    
    browserView = 'MODULE:\\com_xsgj\\userApp.wdml', --webview通用页面
	--通知公告
    news = 'MODULE:\\com_xsgj\\pqy_news.wdml', -- 公告列表
    newsDetail = 'MODULE:\\com_xsgj\\pqy_newsDetail.wdml', -- 公告详情
    newsFav = 'MODULE:\\com_xsgj\\pqy_newsFav.wdml', -- 公告收藏

     --到货管理
    daohuo='MODULE:\\com_xsgj\\pqy_daohuo.wdml',--我的到货
    huodaoDetail='MODULE:\\com_xsgj\\pqy_huodaoDetail.wdml',--到货签收
    
      --订货管理
    dinghuoApply='MODULE:\\com_xsgj\\m_dinghuoshenqing_New.wdml',--我的定货
    dhgoodChoose='MODULE:\\com_xsgj\\m_selectGoods.wdml',--选择商品
    selectMendian='MODULE:\\com_xsgj\\m_selectMendian.wdml',--选择门店
    shouhuoAddr='MODULE:\\com_xsgj\\m_shouhuodizhi.wdml',--收货地址
    --订货查询
   
     dinghuolist='MODULE:\\com_xsgj\\m_dinghuolist.wdml',--我的定货
     dinghuoDetail='MODULE:\\com_xsgj\\m_dinghuoDetail.wdml',--订货明细
    --订货管理
   -- dinghuo='MODULE:\\com_xsgj\\pqy_dinghuo.wdml',--我的定货
   -- dinghuoDetail='MODULE:\\com_xsgj\\pqy_dinghuoDetail.wdml',--订货明细
   -- dinghuoApply='MODULE:\\com_xsgj\\pqy_dinghuoApply.wdml',--订货申请
     --  dinghuoApply='MODULE:\\com_xsgj\\m_dinghuoDetail.wdml',--订货申请
   --  dhgoodChoose='MODULE:\\com_xsgj\\pqy_dhgoodChoose.wdml',--选择商品
  --   setup='MODULE:\\com_xsgj\\pqy_setup.wdml',--选择商品
   --信息反馈
    xinxi = 'MODULE:\\com_xsgj\\szl_xinxi.wdml', -- 信息反馈列表
    xinxiDetail = 'MODULE:\\com_xsgj\\szl_xinxiDetail.wdml', -- 信息反馈详情
    addxinxi = 'MODULE:\\com_xsgj\\szl_addxinxi.wdml', -- 增加信息反馈
    baogao = 'MODULE:\\com_xsgj\\szl_baogao.wdml', -- 工作报告列表
    baogaoDetail = 'MODULE:\\com_xsgj\\szl_baogaoDetail.wdml', -- 工作报告详情
    addbaogao = 'MODULE:\\com_xsgj\\szl_addbaogao.wdml',
    kucunpandian = 'MODULE:\\com_xsgj\\szl_kucunpandian.wdml', -- 库存盘点页面
    xzsp = 'MODULE:\\com_xsgj\\szl_xzsp.wdml', -- 选择商品页面
    
    --丁兵
	test = 'MODULE:\\com_xsgj\\test.wdml', -- 考勤列表
	kaoqinlist = 'MODULE:\\com_xsgj\\db_kaoqinlist.wdml', -- 考勤列表
	kaoqinaction = 'MODULE:\\com_xsgj\\db_kaoqinaction.wdml', -- 考勤
	dateDialog = 'MODULE:\\com_xsgj\\dateDialog.wdml', -- 日期选择
	mendianlist = 'MODULE:\\com_xsgj\\db_mendianlist.wdml', -- 门店列表
	mendianadd = 'MODULE:\\com_xsgj\\db_mendianadd.wdml',
	mendianupd = 'MODULE:\\com_xsgj\\db_mendianupdate.wdml',
	mendianlocal='MODULE:\\com_xsgj\\db_mendianbiaoji.wdml',
	mendiandetail='MODULE:\\com_xsgj\\db_mendiandetail.wdml',
	orignImg='MODULE:\\com_xsgj\\db_yuantu.wdml',
	m_picRecord = 'MODULE:\\com_xsgj\\m_picRecord.wdml', -- 图片提交
	m_picRecordUpdate = 'MODULE:\\com_xsgj\\m_picRecordUpdate.wdml', -- 图片提交
    --徐刚强
    
    diaohuolist = 'MODULE:\\com_xsgj\\xgq_diaohuolist.wdml', -- 调货管理首页
    diaohuodetail = 'MODULE:\\com_xsgj\\xgq_diaohuodetail.wdml', -- 签收
    diaohuoshenqing = 'MODULE:\\com_xsgj\\xgq_diaohuoshenqing.wdml', -- 新增
    diaohuoshangpin = 'MODULE:\\com_xsgj\\xgq_shangpinchange.wdml', --商品
    
    kehuguanli = 'MODULE:\\com_xsgj\\xgq_kehuguanli.wdml', -- 我的客户首页
    xiugaikehu = 'MODULE:\\com_xsgj\\xgq_xiugaikehu.wdml', -- 修改客户
    xinzengkehu = 'MODULE:\\com_xsgj\\xgq_xinzengkehu.wdml', -- 新增客户
    chakankehu = 'MODULE:\\com_xsgj\\xgq_chakankehu.wdml', -- 新增客户
    
	
	--周本文
	baifangzhixing = 'MODULE:\\com_xsgj\\zbw_baifangzhixing.wdml', --拜访执行
  	baifangzhixingtijiao = 'MODULE:\\com_xsgj\\zbw_baifangzhixingtijiao.wdml', --拜访执行提交
  	jishibaifangzhixing = 'MODULE:\\com_xsgj\\zbw_jishibaifangzhixing.wdml', --即时拜访
  	clientSelect = 'MODULE:\\com_xsgj\\bsq_clientSelect.wdml', -- 选择客户
    baifang = 'MODULE:\\com_xsgj\\bsq_baifangplan.wdml', -- 拜访计划
    baifangjilu = 'MODULE:\\com_xsgj\\zbw_baifangjilu.wdml', -- 拜访记录
    baifangjiluxiugai = 'MODULE:\\com_xsgj\\zbw_baifangjiluxiugai.wdml', -- 拜访记录修改
    renyuannew = 'MODULE:\\com_xsgj\\zbw_renyuannew.wdml', -- 人员入职登记
    renyuandelete = 'MODULE:\\com_xsgj\\zbw_renyuandelete.wdml', -- 人员离职登记
    zbw_dateTimeDialog = 'MODULE:\\com_xsgj\\zbw_dateTimeDialog.wdml', -- 人员离职登记
	cc_renyuanUpdata = 'MODULE:\\com_xsgj\\zbw_renyuanupdate.wdml',
	weizhichaxun = 'MODULE:\\com_xsgj\\wlq_weizhichaxun.wdml',
    renyuanNew='MODULE:\\com_xsgj\\zbw_renyuannew.wdml',
	
	--汪珏斌
	wjb_myxiaoliang = 'MODULE:\\com_xsgj\\wjb_myxiaoliang.wdml', --销售分析
	wjb_addxiaoliang = 'MODULE:\\com_xsgj\\wjb_addxiaoliang.wdml',
	wjb_rixiaoliang = 'MODULE:\\com_xsgj\\wjb_rixiaoliang.wdml',
	wjb_shangpinchange = 'MODULE:\\com_xsgj\\wjb_shangpinchange.wdml',
	wjb_dateTimeDialog = 'MODULE:\\com_xsgj\\wjb_dateTimeDialog.wdml',

    wjb_xundianjilu = 'MODULE:\\com_xsgj\\wjb_xundianjilu.wdml',
    wjb_xundianjiluxiugai =  'MODULE:\\com_xsgj\\wjb_xundianjiluxiugai.wdml',
	
	wjb_xundianjihua = 'MODULE:\\com_xsgj\\wjb_xundianjihua.wdml',---巡店计划
    wjb_kehuxuanze = 'MODULE:\\com_xsgj\\wjb_kehuxuanze.wdml',   -----------客户选择
    wjb_xundianzhixing = 'MODULE:\\com_xsgj\\wjb_xundianzhixing.wdml',---------巡店执行
    wjb_xundianzhixingtijiao = 'MODULE:\\com_xsgj\\wjb_xundianzhixingtijiao.wdml',---------巡店执行提交
    wjb_orignImg = 'MODULE:\\com_xsgj\\wjb_orignImg.wdml',--------图像显示
    wjb_jishixundian = 'MODULE:\\com_xsgj\\wjb_jishixundian.wdml',---------即时巡店
    
    cc_pepInfo = 'MODULE:\\com_xsgj\\cc_pepInfo.wdml',
    cc_pepInfoDetail = 'MODULE:\\com_xsgj\\cc_pepInfoDetail.wdml',
}