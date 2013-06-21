-- -----------------------------------------------------------------------------
-- | WonderTek [ 网络无处不在，沟通及时到达 ]
-- -----------------------------------------------------------------------------
-- | Copyright (c) 2013, WonderTek, Inc. All Rights Reserved.
-- -----------------------------------------------------------------------------
-- | Author: hewu <hewu@gmail.com>
-- -----------------------------------------------------------------------------
-- @class UmsAgent
-- @note 行为分析相关操作
-- -----------------------------------------------------------------------------

UmsAgent = {appkey=nil}

Reg.g_UmsAgent = 'g_UmsAgent'

--[[
 -------------------------------------------------------------------------------
 -- @function UmsAgent:_getHandle()
 -------------------------------------------------------------------------------
 -- @brief 获取UmsAgent插件句柄
 -------------------------------------------------------------------------------
 -- @access private
 -------------------------------------------------------------------------------
 -- @param 无
 -------------------------------------------------------------------------------
 -- @return number
 -------------------------------------------------------------------------------
 --]]
function UmsAgent:_getHandle()
    local reg = registerCreate(Reg.g_UmsAgent)
    local handle = registerGetInteger(reg, 'UmsAgent')
    if handle == 0 then
        handle = pluginCreate('UmsAgent')
        registerSetInteger(reg, 'UmsAgent', handle)
    end
    return handle
end


--[[
 -------------------------------------------------------------------------------
 -- @function UmsAgent:postClientData()
 -------------------------------------------------------------------------------
 -- @brief 发送客户端的统计数据，仅调用一次
 -------------------------------------------------------------------------------
 -- @access public
 -------------------------------------------------------------------------------
 -- @param 无
 -------------------------------------------------------------------------------
 -- @return 无
 -------------------------------------------------------------------------------
 --]]
function UmsAgent:postClientData()
    local reg = registerCreate(Reg.g_UmsAgent)
    pluginInvoke(self:_getHandle(), 'SetDefaultReportPolicy', 1)
    local f = io.open('MODULE:\\preUrl.ini')
    if f then
        local data = f:read('*all')
        f:close()
        UmsAgent.appkey = string.match(data, 'UMS_APPKEY%s*=%s*([^\r\n]*)')
        Log:write('postClientData: appkey=', UmsAgent.appkey)
        if UmsAgent.appkey then
            Reg:setInteger(reg, 'umsagentHasAppkey', 1)
        end
    end
    if Reg:getInteger(reg, 'umsagentHasAppkey') == 1 then 
      pluginInvoke(self:_getHandle(), "SendEvent", 'postClientData', 'postClientData')
    end
end

--[[
 -------------------------------------------------------------------------------
 -- @function UmsAgent:getAppKey()
 -------------------------------------------------------------------------------
 -- @brief 获取当前的appkey
 -------------------------------------------------------------------------------
 -- @access public
 -------------------------------------------------------------------------------
 -- @param 无
 -------------------------------------------------------------------------------
 -- @return string 
 -------------------------------------------------------------------------------
 --]]
function UmsAgent:getAppKey()
    local reg = registerCreate(Reg.g_UmsAgent)
    if Reg:getInteger(reg, 'umsagentHasAppkey') == 1 and UmsAgent.appkey ~= nil then 
        return UmsAgent.appkey
    else
      local f = io.open('MODULE:\\preUrl.ini')
      if f then
          local data = f:read('*all')
          f:close()
          UmsAgent.appkey = string.match(data, 'UMS_APPKEY%s*=%s*([^\r\n]*)')
      else
          UmsAgent.appkey = ""
      end
      Log:write('getAppKey: appkey=', UmsAgent.appkey)
      return UmsAgent.appkey
    end
end

--[[
 -------------------------------------------------------------------------------
 -- @function UmsAgent:OnActivate(activity)
 -------------------------------------------------------------------------------
 -- @brief 记录页面激活的时间点
 -------------------------------------------------------------------------------
 -- @access public
 -------------------------------------------------------------------------------
 -- @param activity: string 页面路径
 -------------------------------------------------------------------------------
 -- @return 无
 -------------------------------------------------------------------------------
 --]]
function UmsAgent:OnActivate(activity, tag)
    local reg = registerCreate(Reg.g_UmsAgent)
    if Reg:getInteger(reg, 'umsagentHasAppkey') == 1 then 
        Log:write('OnActivate: ', activity, tag)
        pluginInvoke(self:_getHandle(), "OnActivate", activity, tag)
    end
end

--[[
 -------------------------------------------------------------------------------
 -- @function UmsAgent:OnDeactivate()
 -------------------------------------------------------------------------------
 -- @brief 记录页面去激活的时间点
 -------------------------------------------------------------------------------
 -- @access public
 -------------------------------------------------------------------------------
 -- @param 无
 -------------------------------------------------------------------------------
 -- @return 无
 -------------------------------------------------------------------------------
 --]]
function UmsAgent:OnDeactivate()
    local reg = registerCreate(Reg.g_UmsAgent)
    if Reg:getInteger(reg, 'umsagentHasAppkey') == 1 then 
        Log:write('OnDeactivate:')
        pluginInvoke(self:_getHandle(), "OnDeactivate")
    end
end

--[[
 -------------------------------------------------------------------------------
 -- @function UmsAgent:onLoadStart(tag)
 -------------------------------------------------------------------------------
 -- @brief 记录页面开始加载的时间点
 -------------------------------------------------------------------------------
 -- @access public
 -------------------------------------------------------------------------------
 -- @param tag: string 对页面进行标注
 -------------------------------------------------------------------------------
 -- @return 无
 -------------------------------------------------------------------------------
 --]]
function UmsAgent:onLoadStart()
    local reg = registerCreate(Reg.g_UmsAgent)
    if Reg:getInteger(reg, 'umsagentHasAppkey') == 1 then 
        pluginInvoke(self:_getHandle(), "SendEvent", "onLoadStart")
    end
end

--[[
 -------------------------------------------------------------------------------
 -- @function UmsAgent:onLoadFinish()
 -------------------------------------------------------------------------------
 -- @brief 记录页面加载完成的时间点
 -------------------------------------------------------------------------------
 -- @access public
 -------------------------------------------------------------------------------
 -- @param 无
 -------------------------------------------------------------------------------
 -- @return 无
 -------------------------------------------------------------------------------
 --]]
function UmsAgent:onLoadFinish()
    local reg = registerCreate(Reg.g_UmsAgent)
    if Reg:getInteger(reg, 'umsagentHasAppkey') == 1 then 
        Log:write('onLoadFinish:')
        pluginInvoke(self:_getHandle(), "SendEvent", "onLoadFinish")
    end
end