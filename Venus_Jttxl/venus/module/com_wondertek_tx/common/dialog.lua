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
    bgImg = {rect='30,240,419,247', src='file://image/book/dialog_bj.png', extendstyle='1111',style ='autosize'},
    iconImg = {rect='58,250,48,48', src='', extendstyle='1111',style ='autosize'},
    noticeLabel = {rect='50,252,300,35', color='#bcbcbc', ['font-size']='24', text='', ['v-align']='center', extendstyle='1111',style ='autosize'}, -- 对话框标题
    messageText = {rect='50,310,380,90', ['h-align']='center', ['v-align']='center', color='#bcbcbc', -- 对话框内容
        text='textarea 文本描述', frame='true', ['line-height']='28',['font-size']='22', ['font-family']='微软雅黑', loop='true', step='1', top='0', extendstyle='1111',style ='autosize'},
    okBtn = {rect='', OnSelect='_dialogClose', text='',['h-align']='center',['v-align']='center', ['font-size']='24',['font-family']='微软雅黑', color='#FFFFFF',
            normal = 'src:file://image/book/dialog_ok_d.png' ,
            sel = 'src:file://image/book/dialog_ok_s.png',style ='autosize',extendstyle='1111'},
    cancelBtn = {rect='', OnSelect='_dialogClose', text='',['h-align']='center', ['v-align']='center',['font-size']='24', ['font-family']='微软雅黑',color='#FFFFFF',
            normal = 'src:file://image/book/dialog_cancel_d.png',
            sel = 'src:file://image/book/dialog_cancel_s.png',style ='autosize',extendstyle='1111'},
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
        Log:write("ok")
        Dialog.propTable.okBtn.rect = "143,410,194,61"
        Dialog.propTable.okBtn.visible = 'true'
        Dialog.propTable.okBtn.enable = 'true'
        Dialog.propTable.cancelBtn.visible = 'false'
        Dialog.propTable.cancelBtn.enable = 'false'
        --Dialog.propTable.iconImg.src = "WONDER:\\framework\\image\\dialog_information.png"
    else
         Log:write("okc")
        Dialog.propTable.okBtn.rect = "38,410,194,61"
        Dialog.propTable.cancelBtn.rect = "248,410,194,61"
        Dialog.propTable.okBtn.visible = 'true'
        Dialog.propTable.okBtn.enable = 'true'
        Dialog.propTable.cancelBtn.visible = 'true'
        Dialog.propTable.cancelBtn.enable = 'true'
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

