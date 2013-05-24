-- -----------------------------------------------------------------------------
-- | WonderTek [ 网络无处不在，沟通及时到达 ]
-- -----------------------------------------------------------------------------
-- | Copyright (c) 2012, WonderTek, Inc. All Rights Reserved.
-- -----------------------------------------------------------------------------
-- | Author: yeyu <yeyu@mantis.com>
-- -----------------------------------------------------------------------------
-- @class AppManager
-- @note  操作系统平台（非WRP）程序的相关方法
-- -----------------------------------------------------------------------------

AppManager = {}

Reg.g_appmanager = 'g_appmanager'

--[[
 ------------------------------------------------------------
 -- @function AppManager:_getHandle()
 ------------------------------------------------------------
 -- @brief 获取应用管理插件句柄
 ------------------------------------------------------------
 -- @access private
 ------------------------------------------------------------
 -- @param 无
 ------------------------------------------------------------
 -- @return number
 ------------------------------------------------------------
 --]]
function AppManager:_getHandle()
    local reg = registerCreate(Reg.g_appmanager)
    local appmgrHandle = registerGetInteger(reg, 'AppManager')
    if appmgrHandle == 0 then
        appmgrHandle = pluginCreate('AppManager')
        registerSetInteger(reg, 'AppManager', appmgrHandle)
    end
    return appmgrHandle
end

--[[
 ------------------------------------------------------------
 -- @function AppManager:installApp(packagePath, bSilent)
 ------------------------------------------------------------
 -- @brief 安装原生应用，安装完成以后，系统会发送MSG_APPINSTALL消息给脚本(1为成功，0为失败)
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param packagePath: string 安装包路径，如MODULE:\\com_wondertek_demo\\demo.apk
 -- @param bSilent: boolean 是否静默安装, 一般需要经过手机厂商签名的安装包才能进行静默安装
 ------------------------------------------------------------
 -- @sample function bodyOnPluginEvent(msg, param)
 -- @sample     if msg == MSG_APPINSTALL then -- 安装完毕的消息
 -- @sample         local result = Param:getInteger(param, 0)
 -- @sample         if result == 1 then -- 成功
 -- @sample             local saveResult = AppManager:saveInfo('/data/data/com.wondertek.demo', '酷族音乐', 'com.wondertek.demo', '1.0.0', '音乐客户端', 'MODULE:\\com_wondertek_demo\\data\\demo.png')
 -- @sample         else -- 失败
 -- @sample         end
 -- @sample     elseif msg == MSG_APPUNINSTALL then -- 卸载完毕的消息
 -- @sample         local result = Param:getInteger(param, 0)
 -- @sample         if result == 1 then -- 成功
 -- @sample             local deteleResult = AppManager:deleteById('com.wondertek.demo')
 -- @sample         else -- 失败
 -- @sample         end
 -- @sample     end
 -- @sample end
 ------------------------------------------------------------
 -- @return nil
 ------------------------------------------------------------
 --]]
function AppManager:installApp(packagePath, bSilent)
    if bSilent then
        pluginInvoke(AppManager:_getHandle(), 'AppManager_InstallAppSilent', packagePath)
    else
        pluginInvoke(AppManager:_getHandle(), 'AppManager_InstallAppEx', packagePath)
    end
end

--[[
 ------------------------------------------------------------
 -- @function AppManager:uninstallApp(appId)
 ------------------------------------------------------------
 -- @brief 卸载非系统应用，卸载完成以后，系统会发送MSG_APPUNINSTALL消息给脚本(1为成功，0为失败)
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param appId: string 应用在系统中的唯一标识id，如android上的com.wondertek.demo
 ------------------------------------------------------------
 -- @return nil
 ------------------------------------------------------------
 --]]
function AppManager:uninstallApp(appId)
    pluginInvoke(AppManager:_getHandle(), 'AppManager_UnInstallApp', appId)
end

--[[
 ------------------------------------------------------------
 -- @function AppManager:runApp(appId)
 ------------------------------------------------------------
 -- @brief 运行原生应用
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param appId: string 应用包名或者应用包名加参数列表，格式为"com.wondertek.demo?key1=value&key2=value"
 ------------------------------------------------------------
 -- @return number: 运行是否成功(1为成功,0为失败)
 ------------------------------------------------------------
 --]]
function AppManager:runApp(appId)
    return pluginInvoke(AppManager:_getHandle(), 'AppManager_RunApp', appId) 
end

--[[
 ------------------------------------------------------------
 -- @function AppManager:createShortcut(appId)
 ------------------------------------------------------------
 -- @brief 发送应用程序快捷方式到桌面（如果桌面是厂商二次开发过的，可能不支持）
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param appId: string 应用在系统中的唯一标识id，如android上的com.wondertek.demo
 ------------------------------------------------------------
 -- @return number: 创建应用程序桌面快捷方式是否成功(1为成功,0为失败)
 ------------------------------------------------------------
 --]]
function AppManager:createShortcut(appId)
    return pluginInvoke(AppManager:_getHandle(), 'AppManager_CreateShortcut', appId)
end

--[[
 ------------------------------------------------------------
 -- @function AppManager:removeShortcut(appId)
 ------------------------------------------------------------
 -- @brief 删除应用程序桌面快捷方式（如果桌面是厂商二次开发过的，可能不支持）
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param appId: string 应用在系统中的唯一标识id，如android上的com.wondertek.demo
 ------------------------------------------------------------
 -- @return number: 删除应用程序桌面快捷方式是否成功(1为成功,0为失败)
 ------------------------------------------------------------
 --]]
function AppManager:removeShortcut(appId)
    return pluginInvoke(AppManager:_getHandle(), 'AppManager_RemoveShortcut', appId)
end

--[[
 ------------------------------------------------------------
 -- @function AppManager:isShortcutExist(appId)
 ------------------------------------------------------------
 -- @brief 查询应用程序桌面快捷方式是否存在（如果桌面是厂商二次开发过的，可能不支持）
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param appId: string 应用在系统中的唯一标识id，如android上的com.wondertek.demo
 ------------------------------------------------------------
 -- @return number: 表示桌面快捷方式已经存在(1为存在,0为不存在)
 ------------------------------------------------------------
 --]]
function AppManager:isShortcutExist(appId)
    return pluginInvoke(AppManager:_getHandle(), 'AppManager_ShortcutExists', appId)
end

--[[
 ------------------------------------------------------------
 -- @function AppManager:getInstalledAppInfo()
 ------------------------------------------------------------
 -- @brief 获取非系统应用安装信息
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param 无
 ------------------------------------------------------------
 -- @return table: 以table格式返回所有已安装的非系统应用信息
 -- @return {
 -- @return   [1] = {name='demo1', appid='com.wondertek.demo1', ver='1.0.0', path='/data/data/com.wondertek.demo1/', icon='', size=''},
 -- @return   [2] = {name='demo2', appid='com.wondertek.demo2', ver='1.0.0', path='/data/data/com.wondertek.demo2/', icon='', size=''},
 -- @return   [3] = {name='demo3', appid='com.wondertek.demo3', ver='1.0.0', path='/data/data/com.wondertek.demo3/', icon='', size=''},
 -- @return }
 ------------------------------------------------------------
 --]]
function AppManager:getInstalledAppInfo()
    local appTable = {}
    local appInfo = pluginInvoke(AppManager:_getHandle(), 'AppManager_GetInstalledAppInfo')
    local apps = Util:split(appInfo, ';')
    for i = 1, #apps - 1 do
        table.insert(appTable, {})
        local info = Util:split(apps[i], ',')
        appTable[i].name = info[1]
        appTable[i].appid = info[2]
        appTable[i].ver = info[3]
        appTable[i].path = info[4]
        appTable[i].icon = info[5]
        appTable[i].size = info[6]
    end
    return appTable
end

--[[
 ------------------------------------------------------------
 -- @function AppManager:getInfoById(appId)
 ------------------------------------------------------------
 -- @brief 根据非系统应用id，获取应用信息
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param appId: string 应用在系统中的唯一标识id，如android上的com.wondertek.demo
 ------------------------------------------------------------
 -- @return string: 以字符串的形式返回应用信息
 ------------------------------------------------------------
 --]]
function AppManager:getInfoById(appId)
    return pluginInvoke(AppManager:_getHandle(), 'AppManager_GetInstalledAppInfoById', appId)
end

--[[
 ------------------------------------------------------------
 -- @function AppManager:applyPatchToOldApk(oldApk, newApk, patch)
 ------------------------------------------------------------
 -- @brief 生成apk差分包
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param oldApk: string 旧包全路径
 -- @param newApk: string 新包全路径
 -- @param patch: string 补丁文件全路径
 ------------------------------------------------------------
 -- @return number: 0成功，非0失败.
 ------------------------------------------------------------
 --]]
function AppManager:applyPatchToOldApk(oldApk, newApk, patch)
    return pluginInvoke(AppManager:_getHandle(), 'AppManager_applyPatchToOldApk', oldApk, newApk, patch)
end

