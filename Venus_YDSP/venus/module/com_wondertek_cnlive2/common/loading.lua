-- -----------------------------------------------------------------------------
-- | WonderTek [ 网络无处不在，沟通及时到达 ]
-- -----------------------------------------------------------------------------
-- | Copyright (c) 2012, WonderTek, Inc. All Rights Reserved.
-- -----------------------------------------------------------------------------
-- | Author: xxxx <xxxx@xxxx.com>
-- -----------------------------------------------------------------------------
-- | Desc: 等待提示
-- -----------------------------------------------------------------------------

 -- @brief Loading所有属性
Loading.propTable = {
    shadow = {rect='0,70,640,820', alpha='0', color='#000000', extendstyle='1111'},
    loadingNullBtn = {rect='0,70,640,820', OnSelect='_loadingNullBtnOnSelect', extendstyle='1111'},
    loadingRect ={ rect='302,462,35,35', delay='12', loop='true', extendstyle='1111'},
    frame0 = {rect='0,0,35,35', src='MODULE:\\com_wondertek_cnlive2\\image\\loading0.png', style='maxsize', extendstyle='1111'},
    frame1 = {rect='0,0,35,35', src='MODULE:\\com_wondertek_cnlive2\\image\\loading1.png', style='maxsize', extendstyle='1111'},
    frame2 = {rect='0,0,35,35', src='MODULE:\\com_wondertek_cnlive2\\image\\loading2.png', style='maxsize', extendstyle='1111'},
    frame3 = {rect='0,0,35,35', src='MODULE:\\com_wondertek_cnlive2\\image\\loading3.png', style='maxsize', extendstyle='1111'},
    frame4 = {rect='0,0,35,35', src='MODULE:\\com_wondertek_cnlive2\\image\\loading4.png', style='maxsize', extendstyle='1111'},
    frame5 = {rect='0,0,35,35', src='MODULE:\\com_wondertek_cnlive2\\image\\loading5.png', style='maxsize', extendstyle='1111'},
    frame6 = {rect='0,0,35,35', src='MODULE:\\com_wondertek_cnlive2\\image\\loading6.png', style='maxsize', extendstyle='1111'},
    frame7 = {rect='0,0,35,35', src='MODULE:\\com_wondertek_cnlive2\\image\\loading7.png', style='maxsize', extendstyle='1111'},
    frame8 = {rect='0,0,35,35', src='MODULE:\\com_wondertek_cnlive2\\image\\loading8.png', style='maxsize', extendstyle='1111'},
    frame9 = {rect='0,0,35,35', src='MODULE:\\com_wondertek_cnlive2\\image\\loading9.png', style='maxsize', extendstyle='1111'},
    frame10 = {rect='0,0,35,35', src='MODULE:\\com_wondertek_cnlive2\\image\\loading10.png', style='maxsize', extendstyle='1111'},
    frame11 = {rect='0,0,35,35', src='MODULE:\\com_wondertek_cnlive2\\image\\loading11.png', style='maxsize', extendstyle='1111'},
    clickBtn = {rect='430,750,49,49', OnSelect='_loadingClose', text='取消', color="#FFFFFF",
                normal = 'src:WONDER:\\framework\\image\\button.png;style:sudoku-auto;sudoku:4,4,4,4',
                focus = 'src:WONDER:\\framework\\image\\button_f.png;style:sudoku-auto;sudoku:4,4,4,4',
                border='1', enable='false', visible='false', extendstyle='1111'}
}

-- Loading:show(rootSprite) 显示loading
-- Loading:isShow() 判断是否show（显示），已显示返回true否则false
-- Loading:close() 关闭Loading


