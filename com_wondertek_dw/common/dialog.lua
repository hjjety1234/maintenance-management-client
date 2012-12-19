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
    shadow = {rect='0,0,480,800',alpha='128',color='#000000', extendstyle='1111',style ='autosize'},
    extendable = {visible='0',rect='', extendstyle='1111',style ='autosize'},
    bgImg = {rect='50,285,380,200', src='file://image/dialog.png', extendstyle='1111',style ='autosize'},
    iconImg = {rect='58,230,48,48', src='', extendstyle='1111',style ='autosize'},
    noticeLabel = {rect='128,335,148,48', color='#ffffff', text='', extendstyle='1111',style ='autosize'}, -- 对话框标题
    messageText = {rect='50,340,380,55', ['h-align']='center', color='#000000', -- 对话框内容
        text='textarea 文本描述', frame='true', ['line-height']='28',['font-size']='28', ['font-family']='微软雅黑', loop='true', step='1', top='0', extendstyle='1111',style ='autosize'},
    okBtn = {rect='', OnSelect='_dialogClose', text='确定',['h-align']='center',['v-align']='center', ['font-size']='28',['font-family']='微软雅黑', color='#FFFFFF',
            normal = 'src:file://image/btn_yes.png' ,
            sel = 'src:file://image/btn_yes.png',style ='autosize',extendstyle='1111'},
    cancelBtn = {rect='', OnSelect='_dialogClose', text='取消',['h-align']='center', ['v-align']='center',['font-size']='28', ['font-family']='微软雅黑',color='#FFFFFF',
            normal = 'src:file://image/btn_no.png',
            sel = 'src:file://image/btn_no.png',style ='autosize',extendstyle='1111'},
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
        Dialog.propTable.okBtn.rect = "175,407,141,57"
        --Dialog.propTable.iconImg.src = "WONDER:\\framework\\image\\dialog_information.png"
    else
        Dialog.propTable.okBtn.rect = "85,407,141,57"
        Dialog.propTable.cancelBtn.rect = "255,407,141,57"
        --Dialog.propTable.iconImg.src = "WONDER:\\framework\\image\\dialog_question.png"
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

