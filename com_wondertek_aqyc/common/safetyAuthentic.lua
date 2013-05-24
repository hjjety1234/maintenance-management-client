-- -----------------------------------------------------------------------------
-- | WonderTek [ 网络无处不在，沟通及时到达 ]
-- -----------------------------------------------------------------------------
-- | Copyright (c) 2013, WonderTek, Inc. All Rights Reserved.
-- -----------------------------------------------------------------------------
-- | Author: hewu <heu2008@gmail.com>
-- -----------------------------------------------------------------------------
-- @class SafetyAuthentic
-- @note  CA认证相关操作
-- -----------------------------------------------------------------------------

SafetyAuthentic = {}

Reg.g_safetyAuthentic = 'g_safetyAuthentic'


--[[
 -------------------------------------------------------------------------------
 -- @function SafetyAuthentic:_getHandle()
 -------------------------------------------------------------------------------
 -- @brief 获取CA认证插件句柄
 -------------------------------------------------------------------------------
 -- @access private
 -------------------------------------------------------------------------------
 -- @param 无
 -------------------------------------------------------------------------------
 -- @return number
 -------------------------------------------------------------------------------
 --]]
function SafetyAuthentic:_getHandle()
    local reg = registerCreate(Reg.g_safetyAuthentic)
    local handle = registerGetInteger(reg, 'SafetyAuthentic')
    if handle == 0 then
        handle = pluginCreate('SafetyAuthentic')
        registerSetInteger(reg, 'SafetyAuthentic', handle)
    end
    return handle
end

--[[
  ------------------------------------------------------------------------------
  --@function SafetyAuthentic:init(nLeft, nTop, nWidth, nHeight)
  ------------------------------------------------------------------------------
  --@brief 初始化服务
  ------------------------------------------------------------------------------
  --@access public
  ------------------------------------------------------------------------------
  --@return 无
  ------------------------------------------------------------------------------
  --]]
function SafetyAuthentic:init()
    return pluginInvoke(self:_getHandle(), 'Init')
end

--[[
  ------------------------------------------------------------------------------
  --@function SafetyAuthentic:start()
  ------------------------------------------------------------------------------
  --@brief 启动服务。
  ------------------------------------------------------------------------------
  --@access public
  ------------------------------------------------------------------------------
  --@return 无
  ------------------------------------------------------------------------------
  --]]
function SafetyAuthentic:start()
    return pluginInvoke(self:_getHandle(), 'Start')
end
 
--[[
  ------------------------------------------------------------------------------
  --@function SafetyAuthentic:stop()
  ------------------------------------------------------------------------------
  --@brief 启动服务。
  ------------------------------------------------------------------------------
  --@access public
  ------------------------------------------------------------------------------
  --@return 无
  ------------------------------------------------------------------------------
  --]]
function SafetyAuthentic:stop()
    return pluginInvoke(self:_getHandle(), 'Stop')
end
 

 --[[
  ------------------------------------------------------------------------------
  --@function SafetyAuthentic:GetApps()
  ------------------------------------------------------------------------------
  --@brief 获取应用。
  ------------------------------------------------------------------------------
  --@access public
  ------------------------------------------------------------------------------
  --@return result, apps
  ------------------------------------------------------------------------------
  --]]
function SafetyAuthentic:GetApps()
    return pluginInvoke(self:_getHandle(), 'GetApps')
end
 
 --[[
  ------------------------------------------------------------------------------
  --@function SafetyAuthentic:GetCertInfo()
  ------------------------------------------------------------------------------
  --@brief 获取证书项信息（成功启动服务后才能获取）。
  ------------------------------------------------------------------------------
  --@access public
  ------------------------------------------------------------------------------
  --@return result, apps
  ------------------------------------------------------------------------------
  --]]
function SafetyAuthentic:GetCertInfo()
    return pluginInvoke(self:_getHandle(), 'GetCertInfo', 1)
end

 --[[
  ------------------------------------------------------------------------------
  --@function SafetyAuthentic:IsStarted()
  ------------------------------------------------------------------------------
  --@brief 服务是否已启动。
  ------------------------------------------------------------------------------
  --@access public
  ------------------------------------------------------------------------------
  --@return 无
  ------------------------------------------------------------------------------
  --]]
function SafetyAuthentic:IsStarted()
    return pluginInvoke(self:_getHandle(), 'IsStarted')
end
 
 --[[
  ------------------------------------------------------------------------------
  --@function SafetyAuthentic:SetServerAddr()
  ------------------------------------------------------------------------------
  --@brief 设置服务器地址。
  ------------------------------------------------------------------------------
  --@access public
  ------------------------------------------------------------------------------
  --@return 无
  ------------------------------------------------------------------------------
  --]]
function SafetyAuthentic:SetServerAddr(address, port)
    return pluginInvoke(self:_getHandle(), 'SetServerAddr', address, port)
end

 --[[
  ------------------------------------------------------------------------------
  --@function SafetyAuthentic:Upgrade()
  ------------------------------------------------------------------------------
  --@brief 自动升级。
  ------------------------------------------------------------------------------
  --@access public
  ------------------------------------------------------------------------------
  --@return 无
  ------------------------------------------------------------------------------
  --]]
function SafetyAuthentic:Upgrade()
    return pluginInvoke(self:_getHandle(), 'Upgrade')
end