-- -----------------------------------------------------------------------------
-- | WonderTek [ 网络无处不在，沟通及时到达 ]
-- -----------------------------------------------------------------------------
-- | Copyright (c) 2012, WonderTek, Inc. All Rights Reserved.
-- -----------------------------------------------------------------------------
-- | Author: xxxx <xxxx@xxxx.com>
-- -----------------------------------------------------------------------------
-- | Desc: 对话框
-- -----------------------------------------------------------------------------

-- @brief dialog属性
Dialog.propTable = {
    shadow = {rect='0,0,480,800',alpha='128',color='#000000', extendstyle='1177'},
    extendable = {visible='0',rect='', extendstyle='1111'},
    bgImg = {rect='65,300,350,200', src='file://image/bg_dialog.png', style='sudoku-auto', sudoku='10,0,10,0', extendstyle='1111'},
    iconImg = {rect='58,230,48,48', src='', extendstyle='1111'},
    noticeLabel = {rect='65,300,350,48', color='#ffffff', ['font-size']='28', ['font-style']="bold", ['h-align']="center", ['v-align']="center", extendstyle='1111'}, -- 对话框标题
    messageText = {rect='75,360,330,70', ['h-align']='center', color='#ffffff', -- 对话框内容
        frame='true', ['line-height']='28',['font-size']='24', loop='true', extendstyle='1111'},
    okBtn = {rect='95,435,128,43', OnSelect='_dialogClose', extendstyle='1111'},
    cancelBtn = {rect='257,435,128,43', OnSelect='_dialogClose', extendstyle='1111'},
    okNormalImg = {rect='0,0,128,43', src="file://image/Dialog_ok_d.png", style="autosize", extendstyle='1111'},
    okFocusImg = {rect='0,0,128,43', src="file://image/Dialog_ok_s.png", style="autosize", extendstyle='1111'},
    cancelNormalImg = {rect='0,0,128,43', src="file://image/Dialog_cancel_d.png", style="autosize", extendstyle='1111'},
    cancelFocusImg = {rect='0,0,128,43', src="file://image/Dialog_cancel_s.png", style="autosize", extendstyle='1111'},
}

--[[
 ------------------------------------------------------------
 -- @function Dialog:show(title, message, typeStr, okCallback, cancelCallback)
 ------------------------------------------------------------
 -- @brief 显示对话框
 ------------------------------------------------------------
 -- @param string title 对话框标题
 -- @param string message 对话框内容
 -- @param string typeStr 对话框类型，'ok'、'cancle'、'ok_cancel'
 -- @param string okCallback 确定回调函数名，可空
 -- @param string cancelCallback 取消回调函数名，可空
 ------------------------------------------------------------
 -- @return void
 ------------------------------------------------------------
 --]] 
Dialog._show = Dialog.show
function Dialog:show(title, message, typeStr, okCallback, cancelCallback)
    Dialog.propTable.noticeLabel.text = title
    Dialog.propTable.messageText.text = message
    if okCallback then
        _okCallback = okCallback
        Dialog.propTable.okBtn.OnSelect = "_okProc" --okCallback
    else
        Dialog.propTable.okBtn.OnSelect = "_dialogClose"
    end
    if cancelCallback then
        _cancelCallback = cancelCallback
        Dialog.propTable.cancelBtn.OnSelect = "_cancelProc" --cancelCallback
    else
        Dialog.propTable.cancelBtn.OnSelect = "_dialogClose"
    end  
    if typeStr == "cancel" or typeStr == "ok" then
        Dialog.propTable.okBtn.rect = "176,435,128,43"
        Dialog.propTable.okNormalImg.src = 'file://image/Dialog_ok_d.png'
        Dialog.propTable.okFocusImg.src = 'file://image/Dialog_ok_s.png'
    elseif typeStr == "view_download" then 
        Dialog.propTable.okBtn.rect = "95,435,128,43"
        Dialog.propTable.cancelBtn.rect = "257,435,128,43"
        Dialog.propTable.okNormalImg.src = 'file://image/Dailog_view_d.png'
        Dialog.propTable.okFocusImg.src = 'file://image/Dailog_view_s.png'
        Dialog.propTable.cancelNormalImg.src = 'file://image/Dailog_download_d.png'
        Dialog.propTable.cancelFocusImg.src = 'file://image/Dailog_download_s.png'
    elseif typeStr == "download_cancel" then
        Dialog.propTable.okBtn.rect = "95,435,128,43"
        Dialog.propTable.cancelBtn.rect = "257,435,128,43"
        Dialog.propTable.okNormalImg.src = 'file://image/Dailog_download_d.png'
        Dialog.propTable.okFocusImg.src = 'file://image/Dailog_download_s.png'
        Dialog.propTable.cancelNormalImg.src = 'file://image/Dialog_cancel_d.png'
        Dialog.propTable.cancelFocusImg.src = 'file://image/Dialog_cancel_s.png'
    else
        Dialog.propTable.okBtn.rect = "95,435,128,43"
        Dialog.propTable.cancelBtn.rect = "257,435,128,43"
        Dialog.propTable.okNormalImg.src = 'file://image/Dialog_ok_d.png'
        Dialog.propTable.okFocusImg.src = 'file://image/Dialog_ok_s.png'
        Dialog.propTable.cancelNormalImg.src = 'file://image/Dialog_cancel_d.png'
        Dialog.propTable.cancelFocusImg.src = 'file://image/Dialog_cancel_s.png'
    end
    Dialog:_show(typeStr)
end

--[[
 ------------------------------------------------------------
 -- @function Dialog:isShow()
 ------------------------------------------------------------
 -- @brief Dialog框是否显示
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @return boolean
 ------------------------------------------------------------
 --]]
function Dialog:isShow()
    local rootSprite = Sprite:getCurScene()
    local dialogSprite = Sprite:findChild(rootSprite, 'dialogNode')
    if dialogSprite ~=0 and Sprite:isVisible(dialogSprite) == 1 then
        return true
    end
    return false
end

function _okProc()
    Dialog:close()
    if type(loadstring('return '.._okCallback)()) == 'function' then
        loadstring(_okCallback..'()')()
    end
end

function _cancelProc()
    Dialog:close()
    if type(loadstring('return '.._cancelCallback)()) == 'function' then
        loadstring(_cancelCallback..'()')()
    end
end


-- -----------------------------------------------------------------------------
-- @class Tips
-- @note  文字提示框 
-- -----------------------------------------------------------------------------

Tips = {}

Tips.layout = [[
<?xml version="1.0" encoding="utf-8"?>
<root>
    <header/>
    <body>
        <image name="tipsBgImg" rect="0,749,480,51" src="file://image/tips_bg.png" style="sudoku-auto" sudoku="23,20,23,20" extendstyle="1111" />
        <textarea name="tipsText" rect="0,749,480,51" h-align="center" v-align="center" extendstyle="1111" />
    </body>
</root>
]]

function Tips:show(text)
    local rootSprite = Sprite:getCurScene()
    local tipsNode = Sprite:findChild(rootSprite, 'tipsNode')
    if tipsNode ~= 0 then
        Sprite:setVisible(tipsNode, 1)
        Sprite:setEnable(tipsNode, 1)
        Sprite:setActive(tipsNode, 1)
    else
        tipsNode = Sprite:create('node', Sprite:findChild(rootSprite, 'mainNode'))
        Sprite:setProperty(tipsNode, 'name', 'tipsNode')
        Sprite:loadFromString(tipsNode, Tips.layout)
    end
    Sprite:setProperty(Sprite:findChild(tipsNode, 'tipsText'), 'text', text)
    Timer:set(1, 3000, '_closeTipsOnTimer')
end

function Tips:close()
    local tipsNode = Sprite:findChild(Sprite:getCurScene(), 'tipsNode')
    Sprite:setVisible(tipsNode, 0)
    Sprite:setEnable(tipsNode, 0)
    Sprite:setActive(tipsNode, 0)
end

function _closeTipsOnTimer()
    Tips:close()
end

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
