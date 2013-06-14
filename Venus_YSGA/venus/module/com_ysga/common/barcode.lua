-- -----------------------------------------------------------------------------
-- | WonderTek [ 网络无处不在，沟通及时到达 ]
-- -----------------------------------------------------------------------------
-- | Copyright (c) 2012, WonderTek, Inc. All Rights Reserved.
-- -----------------------------------------------------------------------------
-- | Author: yuhongwei <yuhongwei@mantis.com>
-- -----------------------------------------------------------------------------
-- @class BarCode
-- @note 二维码相关操作
-- -----------------------------------------------------------------------------

BarCode = {}

Reg.g_BarCode = 'g_BarCode'

--[[
 -------------------------------------------------------------------------------
 -- @function BarCode:_getHandle()
 -------------------------------------------------------------------------------
 -- @brief 获取BarCode插件句柄
 -------------------------------------------------------------------------------
 -- @access private
 -------------------------------------------------------------------------------
 -- @param 无
 -------------------------------------------------------------------------------
 -- @return number
 -------------------------------------------------------------------------------
 --]]
function BarCode:_getHandle()
    local reg = registerCreate(Reg.g_BarCode)
    local handle = registerGetInteger(reg, 'BarCodePlugin')
    if handle == 0 then
        handle = pluginCreate('BarcodeScanner')
        registerSetInteger(reg, 'MapPlugin', handle)
    end
    return handle
end

--[[
  ------------------------------------------------------------------------------
  --@function BarCode:startScanner(observer, msgId)
  --------------------------------------------------------------------------------
  --@breif 启动BarcodeCanner, 得到结果后发送插件消息
  ------------------------------------------------------------------------------
  --@access public
  ------------------------------------------------------------------------------
  --@param observer: number, 场景监听器
  --@param msgId: number, 插件消息号,业务自定
  ------------------------------------------------------------------------------
  --@sample 参考用法如下：
  --@sample local observer = Plugin:getObserver()
  --@sample local ret = BarCode:startScanner(observer, 1000)
  --@sample 插件消息返回数据
  --@sample function bodyOnPluginEvent(msg, param)
  --@sample     if msg == 1000 then
  --@sample         local path = Param:getString(param, 0) -- 条码图片路径
  --@sample         local code = Param:getString(param, 1) -- 解码后的文本
  --@sample         -- TODO
  --@sample     end
  --@sample end
  ------------------------------------------------------------------------------
  --@return number, 1表示启动成功，0代表失败
  ------------------------------------------------------------------------------
  --]]
function BarCode:startCanner(observer, msgId)
    return pluginInvoke(self:_getHandle(), 'StartBarcodeScanner', observer, msgId)
end

--[[
  ------------------------------------------------------------------------------
  --@function BarCode:setPreferences(key, value)
  --------------------------------------------------------------------------------
  --@breif 设置扫描参数
  ------------------------------------------------------------------------------
  --@access public
  ------------------------------------------------------------------------------
  --@param key: string, 属性名: front_light(闪光灯)、play_beep(提示音)、vibrate(震动)
  --@param value: string, 属性值: true、false;注意该参数应该传字串, 默认false 
  ------------------------------------------------------------------------------
  --@sample 参考用法如下：
  --@sample local ret1 = BarCode:setPreferences('front_light', 'true') -- 开启闪光灯
  --@sample if 0 == ret1 then
  --@sample     Dialog:show('开启闪光灯失败', 'ok')
  --@sample end
  --@sample local ret2 = BarCode:setPreferences('play_beep', 'true') -- 开启提示音
  --@sample if 0 == ret2 then
  --@sample     Dialog:show('开启提示音失败', 'ok')
  --@sample end
  --@sample local ret3 = BarCode:setPreferences('vibrate', 'true') -- 开启震动
  --@sample if 0 == ret3 then
  --@sample     Dialog:show('开启震动失败', 'ok')
  --@sample end
  ------------------------------------------------------------------------------
  --@return number, 1表示设置成功，0代表设置失败
  ------------------------------------------------------------------------------
  --]]
function BarCode:setPreferences(key, value)
    return pluginInvoke(self:_getHandle(), 'SetPreferences', key, value or 'false')
end

--[[
  ------------------------------------------------------------------------------
  --@function BarCode:getPreferences(key)
  --------------------------------------------------------------------------------
  --@breif 获取某项属性值
  ------------------------------------------------------------------------------
  --@access public
  ------------------------------------------------------------------------------
  --@param key: string, 属性名: front_light(闪光灯)、play_beep(提示音)、vibrate(震动)
  ------------------------------------------------------------------------------
  --@sample 参考用法如下：
  --@sample local light = BarCode:getPreferences('front_light') -- 获取闪光灯属性值
  --@sample if 'true' == light then
  --@sample     local ret = BarCode:setPreferences('front_light', 'false') -- 关闭闪光灯
  --@sample     if ret == 0 then
  --@sample         Dialog:show('关闭闪光灯失败', 'ok')
  --@sample     end
  --@sample end
  ------------------------------------------------------------------------------
  --@return string, 'true'或'false', 失败返回空字串
  ------------------------------------------------------------------------------
  --]]
function BarCode:getPreferences(key)
    return pluginInvoke(self:_getHandle(), 'GetPreferences', key)
end
