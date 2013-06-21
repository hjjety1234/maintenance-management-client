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
    bgImg = {rect='65,280,350,200', src="file://png/kq_icons_arrow_bg.png", style="sudoku-auto", sudoku="11,13,11,13", extendstyle='1111'},
    iconImg = {rect='275,315,16,16', src='file://png/sz_warn.png', extendstyle='1111'},
    noticeLabel = {rect='65,300,350,48', color='#0', ['font-size']='28', ['font-style']="bold", ['h-align']="center", ['v-align']="center", extendstyle='1111'}, -- 对话框标题
    messageText = {rect='75,360,330,70', ['h-align']='center', color='#0', -- 对话框内容
        frame='true', ['line-height']='24',['font-size']='24', loop='true', extendstyle='1111'},
    okBtn = {rect='95,400,116,56', OnSelect='_dialogClose', extendstyle='1111'},
    cancelBtn = {rect='257,400,116,56', OnSelect='_dialogClose', extendstyle='1111'},
    okNormalImg = {rect='0,0,116,56', src="file://png/sz_bottom_qd.png", style="autosize", extendstyle='1111'},
    okFocusImg = {rect='0,0,116,56', src="file://png/sz_bottom_qd_a.png", style="autosize", extendstyle='1111'},
    cancelNormalImg = {rect='0,0,116,56', src="file://png/sz_bottom_qx.png", style="autosize", extendstyle='1111'},
    cancelFocusImg = {rect='0,0,116,56', src="file://png/sz_bottom_qx_a.png", style="autosize", extendstyle='1111'},
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
        Dialog.propTable.okBtn.rect = "176,400,116,56"
        Dialog.propTable.okNormalImg.src = 'file://png/sz_bottom_qd.png'
        Dialog.propTable.okFocusImg.src = 'file://png/sz_bottom_qd_a.png'
		Dialog.propTable.okBtn.visible = 'true'
        Dialog.propTable.okBtn.enable = 'true'
        Dialog.propTable.cancelBtn.visible = 'false'
        Dialog.propTable.cancelBtn.enable = 'false'
    elseif typeStr == "zancun_tijiao" then
        Dialog.propTable.okBtn.rect = "95,400,116,56"
        Dialog.propTable.cancelBtn.rect = "257,400,116,56"
        Dialog.propTable.okNormalImg.src = 'file://png/jccl_bottom_zc.png'
        Dialog.propTable.okFocusImg.src = 'file://png/jccl_bottom_zc_a.png'
        Dialog.propTable.cancelNormalImg.src = 'file://png/jccl_bottom_tj.png'
        Dialog.propTable.cancelFocusImg.src = 'file://png/jccl_bottom_tj_a.png'
        Dialog.propTable.okBtn.visible = 'true'
        Dialog.propTable.okBtn.enable = 'true'
        Dialog.propTable.cancelBtn.visible = 'true'
        Dialog.propTable.cancelBtn.enable = 'true'
    elseif typeStr == "ok_cancel" then
        Dialog.propTable.okBtn.rect = "95,400,116,56"
        Dialog.propTable.cancelBtn.rect = "257,400,116,56"
		Dialog.propTable.okNormalImg.src = 'file://png/sz_bottom_qd.png'
        Dialog.propTable.okFocusImg.src = 'file://png/sz_bottom_qd_a.png'
        Dialog.propTable.cancelNormalImg.src = 'file://png/sz_bottom_qx.png'
        Dialog.propTable.cancelFocusImg.src = 'file://png/sz_bottom_qx_a.png'
        Dialog.propTable.okBtn.visible = 'true'
        Dialog.propTable.okBtn.enable = 'true'
        Dialog.propTable.cancelBtn.visible = 'true'
        Dialog.propTable.cancelBtn.enable = 'true'
    else
        Dialog.propTable.okBtn.rect = "95,400,116,56"
        Dialog.propTable.cancelBtn.rect = "257,400,116,56"
		Dialog.propTable.okNormalImg.src = 'file://png/sz_bottom_qd.png'
        Dialog.propTable.okFocusImg.src = 'file://png/sz_bottom_qd_a.png'
        Dialog.propTable.cancelNormalImg.src = 'file://png/sz_bottom_qx.png'
        Dialog.propTable.cancelFocusImg.src = 'file://png/sz_bottom_qx_a.png'
        Dialog.propTable.okBtn.visible = 'true'
        Dialog.propTable.okBtn.enable = 'true'
        Dialog.propTable.cancelBtn.visible = 'true'
        Dialog.propTable.cancelBtn.enable = 'true'
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
