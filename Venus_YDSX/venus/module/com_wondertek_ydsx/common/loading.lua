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
    shadow = {rect='0,0,480,800', alpha='128', color='#000000', extendstyle='1111'},
    loadingNullBtn = {rect='0,0,480,800', OnSelect='_loadingNullBtnOnSelect', extendstyle='1111'},
    loadingRect ={ rect='200,368,81,81', delay='12', loop='true', extendstyle='1111'},
    frame0 = {rect='0,0,81,81', src='WONDER:\\framework\\image\\loading.png', style='autosize', rotate='360', extendstyle='1111'},
    frame1 = {rect='0,0,81,81', src='WONDER:\\framework\\image\\loading.png', style='autosize', rotate='330', extendstyle='1111'},
    frame2 = {rect='0,0,81,81', src='WONDER:\\framework\\image\\loading.png', style='autosize', rotate='300', extendstyle='1111'},
    frame3 = {rect='0,0,81,81', src='WONDER:\\framework\\image\\loading.png', style='autosize', rotate='270', extendstyle='1111'},
    frame4 = {rect='0,0,81,81', src='WONDER:\\framework\\image\\loading.png', style='autosize', rotate='240', extendstyle='1111'},
    frame5 = {rect='0,0,81,81', src='WONDER:\\framework\\image\\loading.png', style='autosize', rotate='210', extendstyle='1111'},
    frame6 = {rect='0,0,81,81', src='WONDER:\\framework\\image\\loading.png', style='autosize', rotate='180', extendstyle='1111'},
    frame7 = {rect='0,0,81,81', src='WONDER:\\framework\\image\\loading.png', style='autosize', rotate='150', extendstyle='1111'},
    frame8 = {rect='0,0,81,81', src='WONDER:\\framework\\image\\loading.png', style='autosize', rotate='120', extendstyle='1111'},
    frame9 = {rect='0,0,81,81', src='WONDER:\\framework\\image\\loading.png', style='autosize', rotate='90', extendstyle='1111'},
    frame10 = {rect='0,0,81,81', src='WONDER:\\framework\\image\\loading.png', style='autosize', rotate='60', extendstyle='1111'},
    frame11 = {rect='0,0,81,81', src='WONDER:\\framework\\image\\loading.png', style='autosize', rotate='30', extendstyle='1111'},
    clickBtn = {rect='430,750,49,49', OnSelect='_loadingClose', text='取消', color="#FFFFFF",
                normal = 'src:WONDER:\\framework\\image\\button.png;style:sudoku-auto;sudoku:4,4,4,4',
                focus = 'src:WONDER:\\framework\\image\\button_f.png;style:sudoku-auto;sudoku:4,4,4,4',
                border='1', enable='false', visible='false', extendstyle='1111'}
}

-- Loading:show(rootSprite) 显示loading
-- Loading:isShow() 判断是否show（显示），已显示返回true否则false
-- Loading:close() 关闭Loading


