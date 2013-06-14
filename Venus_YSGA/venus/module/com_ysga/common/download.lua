-- -----------------------------------------------------------------------------
-- | WonderTek [ 网络无处不在，沟通及时到达 ]
-- -----------------------------------------------------------------------------
-- | Copyright (c) 2012, WonderTek, Inc. All Rights Reserved.
-- -----------------------------------------------------------------------------
-- | Author: wangweipeng <wangweipeng@mantis.com>
-- -----------------------------------------------------------------------------
-- | Desc: 下载相关
-- -----------------------------------------------------------------------------
-- @class Download
-- @note  下载相关
-- -----------------------------------------------------------------------------

Download = {}

Reg.g_download = 'g_download'

--[[
 ------------------------------------------------------------
 -- @table Download.status
 -- @brief 下载状态值
 -- @access public
 ------------------------------------------------------------
 --]]
Download.status = {
    Idle            = 0, -- 等待状态
    NotEnoughSpace  = 1, -- 空间不足
    Downloading     = 2, -- 正在下载
    Paused          = 3, -- 暂停状态
    Finished        = 4, -- 下载结束
    Failed          = 5  -- 下载失败
}

--[[
 ------------------------------------------------------------
 -- @function Download:getStatus(index, isSysList)
 ------------------------------------------------------------
 -- @brief 获取下载状态, 如果index为空或0则返回所有下载任务的
 -- @brief 状态
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param index: number, 下载任务索引号(从1开始，可为空)
 -- @param isSysList: boolean, 是否使用系统下载队列，默认为否
 ------------------------------------------------------------
 -- @return task: table, 下载任务状态表
 -- @return task.id: number, 下载任务ID号
 -- @return task.remote: string, 下载文件的url路径
 -- @return task.localfile: string, 下载文件保存的全路径
 -- @return task.title: string, 下载文件的标题
 -- @return task.maxsize: number, 下载文件的总大小
 -- @return task.size: number, 下载文件当前的下载的大小
 -- @return task.status: number, 当前下载状态
 -- @return task.lastStatus: number, 上次下载状态
 ------------------------------------------------------------
 --]]
function Download:getStatus(index, isSysList)
    local task={}
    local download = self:_getHandle()
    local reg = registerCreate(Reg.g_download)
    if index and index > 0 then
        _, task.id, task.remote, task.localfile, task.title, task.maxsize,
            task.size, task.status = pluginInvoke(download, isSysList and 'SystemGetItem' or 'GetItem', index - 1)
        task.lastStatus = registerGetInteger(reg, (isSysList and 'sysLastStatus' or 'lastStatus') .. (index - 1))
        registerSetInteger(reg, (isSysList and 'sysLastStatus' or 'lastStatus') .. (index - 1), task.status)
    else
        local count = self:getCount(isSysList)
        for i = 1, count do
            table.insert(task, {})
            _, task[i].id, task[i].remote, task[i].localfile, task[i].title,
                task[i].maxsize, task[i].size, task[i].status = pluginInvoke(download, isSysList and 'SystemGetItem' or 'GetItem', i - 1)
            task[i].lastStatus = registerGetInteger(reg, (isSysList and 'sysLastStatus' or 'lastStatus') .. (i - 1))
            registerSetInteger(reg, (isSysList and 'sysLastStatus' or 'lastStatus') .. (i - 1), task[i].status)
        end
    end
    return task
end

--[[
 ------------------------------------------------------------
 -- @function Download:getCount(isSysList)
 ------------------------------------------------------------
 -- @brief 获取下载任务个数
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param isSysList: boolean, 是否使用系统下载队列，默认为否
 ------------------------------------------------------------
 -- @return number
 ------------------------------------------------------------
 --]]
function Download:getCount(isSysList)
    return pluginInvoke(self:_getHandle(), isSysList and 'SystemCount' or 'Count')
end

--[[
 ------------------------------------------------------------
 -- @function Download:append(path, name, url, isSysList,isOffline)
 ------------------------------------------------------------
 -- @brief 添加下载任务
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param path: string, 文件要存储的全路径
 -- @param name: string, 文件名
 -- @param url: string, 下载路径
 -- @param isSysList: boolean, 是否使用系统下载队列，默认为否
 ------------------------------------------------------------
 -- @return number/nil: -1代表该任务已经存在，其它数字则代表该任务正常添加，nil表示参数错误
 ------------------------------------------------------------
 --]]
function Download:append(path, name, url, isSysList, paramUrl, downloadUrlFunc, sizeLimit, orderUrlFunc)
    if path and name and url then
        if isSysList then
            return pluginInvoke(self:_getHandle(), 'SystemAppend', url , path, name)
        else
            return pluginInvoke(self:_getHandle(), 'Append', url , path, name,
                paramUrl or '', downloadUrlFunc or '', sizeLimit or 0, orderUrlFunc or '')
        end
    end
end

--[[
 ------------------------------------------------------------
 -- @function Download:start(index, isSysList)
 ------------------------------------------------------------
 -- @brief 开始一个下载任务,如果index为空或0则开始所有下载项
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param index: number, 下载任务索引号
 -- @param isSysList: boolean, 是否使用系统下载队列，默认为否
 ------------------------------------------------------------
 -- @return nil
 ------------------------------------------------------------
 --]]
function Download:start(index, isSysList)
    local task={}
    local download = self:_getHandle()
    if index and index > 0 then
        local task = self:getStatus(index, isSysList)
        if task and task.id and task.status ~= self.status.Failed and task.status ~= self.status.Finished then
            pluginInvoke(download, isSysList and 'SystemResume' or 'Resume', task.id)
        end
    else
        local count = self:getCount(isSysList)
        local task = self:getStatus(nil, isSysList)
        for i = 1, count do
            if task[i] and task[i].id and task[i].status ~= self.status.Failed and task[i].status ~= self.status.Finished then
                pluginInvoke(download, isSysList and 'SystemResume' or 'Resume', task[i].id)
            end
        end
    end
end

--[[
 ------------------------------------------------------------
 -- @function Download:pause(index, isSysList)
 ------------------------------------------------------------
 -- @brief 暂停一个下载任务,如果index为空或0则暂停所有下载项
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param index: number, 下载任务索引号
 -- @param isSysList: boolean, 是否使用系统下载队列，默认为否
 ------------------------------------------------------------
 -- @return nil
 ------------------------------------------------------------
 --]]
function Download:pause(index, isSysList)
    local task = {}
    local download = self:_getHandle()
    if index and index > 0 then
        local task = self:getStatus(index, isSysList)
        if task and task.id and task.status ~= self.status.Failed and task.status ~= self.status.Finished then
            pluginInvoke(download, isSysList and 'SystemPause' or 'Pause', task.id)
        end
    else
        local count = self:getCount(isSysList)
        local task = self:getStatus(nil, isSysList)
        for i = 1, count do
            if task[i] and task[i].id and task[i].status ~= self.status.Failed and task[i].status ~= self.status.Finished then
                pluginInvoke(download, isSysList and 'SystemPause' or 'Pause', task[i].id)
                Log:write('action in Download:pause, note PauseAllDownloadTask')
            end
        end
    end
end

--[[
 ------------------------------------------------------------
 -- @function Download:delete(index, bDelete, isSysList)
 ------------------------------------------------------------
 -- @brief 删除一个下载任务
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param index: number, 下载任务索引号
 -- @param bDelete: boolean, 是否删除对应文件(可为空，默认为false)
 -- @param isSysList: boolean, 是否使用系统下载队列，默认为否
 ------------------------------------------------------------
 -- @return nil
 ------------------------------------------------------------
 --]]
function Download:delete(index, bDelete, isSysList)
    local download = self:_getHandle()
    if index and index > 0 then
        local task = self:getStatus(index, isSysList)
        if task and task.id then
            pluginInvoke(download, isSysList and 'SystemRemove' or 'Remove', task.id)
            if bDelete then
                os.remove(task.localfile .. '~')
                os.remove(task.localfile)
            end
        end
    end
end

--[[
 ------------------------------------------------------------
 -- @function Download:deleteByName(name, bDelete, isSysList)
 ------------------------------------------------------------
 -- @brief 根据文件名删除一个下载任务
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param name: string, 下载任务文件名
 -- @param bDelete: boolean, 是否删除对应文件(可为空，默认为false)
 -- @param isSysList: boolean, 是否使用系统下载队列，默认为否
 ------------------------------------------------------------
 -- @return nil
 ------------------------------------------------------------
 --]]
function Download:deleteByName(name, bDelete, isSysList)
    local download = self:_getHandle()
    local count = self:getCount(isSysList)
    local task = self:getStatus(nil, isSysList)
    if task then
        for i = 1, count do
            if task[i] and task[i].title == name then
                pluginInvoke(download, isSysList and 'SystemRemove' or 'Remove', task[i].id)
                if bDelete then
                    os.remove(task[i].localfile .. '~')
                    os.remove(task[i].localfile)
                end
                return
            end
        end
    end
end

--[[
 ------------------------------------------------------------
 -- @function Download:deleteByPath(path, bDelete, isSysList)
 ------------------------------------------------------------
 -- @brief 根据文件全路径删除一个下载任务
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param path: string, 下载任务全路径
 -- @param bDelete: boolean, 是否删除对应文件(可为空，默认为false)
 -- @param isSysList: boolean, 是否使用系统下载队列，默认为否
 ------------------------------------------------------------
 -- @return nil
 ------------------------------------------------------------
 --]]
function Download:deleteByPath(path, bDelete, isSysList)
    local download = self:_getHandle()
    local count = self:getCount(isSysList)
    local task = self:getStatus(nil, isSysList)
    if task then
        for i = 1, count do
            if task[i] and task[i].localfile == path then
                pluginInvoke(download, isSysList and 'SystemRemove' or 'Remove', task[i].id)
                if bDelete then
                    os.remove(task[i].localfile .. '~')
                    os.remove(task[i].localfile)
                end
                return
            end
        end
    end
end

--[[
 ------------------------------------------------------------
 -- @function Download:getIndexById(id)
 ------------------------------------------------------------
 -- @brief 通过下载id获得列表index
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param id: number, 下载任务id
 ------------------------------------------------------------
 -- @return number index: 下载任务序号(如果未找到则返回空)
 -- @return boolean isSysList: 是否为系统下载队列(如果未找到则返回空)
 ------------------------------------------------------------
 --]]
function Download:getIndexById(id)
    local download = self:_getHandle()
    local count = self:getCount()
    for i = 1, count do
        _, taskId = pluginInvoke(download, 'GetItem', i - 1)
        if taskId == id then
            return i, false
        end
    end
    count = self:getCount(true)
    for i = 1, count do
        _, taskId = pluginInvoke(download, 'SystemGetItem', i - 1)
        if taskId == id then
            return i, true
        end
    end
end

--[[
 ------------------------------------------------------------
 -- @function Download:_getHandle()
 ------------------------------------------------------------
 -- @brief 获取下载插件句柄
 ------------------------------------------------------------
 -- @access private
 ------------------------------------------------------------
 -- @param 无
 ------------------------------------------------------------
 -- @return number
 ------------------------------------------------------------
 --]]
function Download:_getHandle()
    local reg = registerCreate(Reg.g_download)
    local download = registerGetInteger(reg,'Download')
    if download == 0 then
        download = pluginCreate('Download')
        registerSetInteger(reg, 'Download', download)
    end
    return download
end

