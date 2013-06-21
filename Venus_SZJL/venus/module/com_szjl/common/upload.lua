-- -----------------------------------------------------------------------------
-- | WonderTek [ 网络无处不在，沟通及时到达 ]
-- -----------------------------------------------------------------------------
-- | Copyright (c) 2012, WonderTek, Inc. All Rights Reserved.
-- -----------------------------------------------------------------------------
-- | Author: wangweipeng <wangweipeng@mantis.com>
-- -----------------------------------------------------------------------------
-- | Desc: 上传相关
-- -----------------------------------------------------------------------------
-- @class Upload
-- @note  上传相关
-- -----------------------------------------------------------------------------

Upload = {}

--[[
 ------------------------------------------------------------
 -- @table Upload.status
 -- @brief 上传状态值
 -- @access public
 ------------------------------------------------------------
 --]]
Upload.status = {
    Idle        = 0, -- 等待状态
    LoadFailed  = 1, -- 加载失败
    Seeking     = 2, -- 查找状态
    Uploading   = 3, -- 正在上传
    Paused      = 4, -- 暂停状态
    Finished    = 5, -- 上传结束
    Failed      = 6  -- 上传失败
}

--[[
 ------------------------------------------------------------
 -- @function Upload:getStatus([index])
 ------------------------------------------------------------
 -- @brief 获取上传状态, 如果index为空或0则返回所有上传任务的状态
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param index: number, 上传任务索引号(可为空)
 ------------------------------------------------------------
 -- @return table task: 上传任务状态表
 -- @return       number task.id: 上传任务ID号
 -- @return       string task.remote: 上传文件的url路径
 -- @return       string task.remoteparam: 客户端上传请求所必需的参数信息，这个PARAM需要保留在客户端，
 -- @return       用于客户端上传线程向UGC上传部件请求视频上传时提供的参数信息
 -- @return       string task.localfile: 上传文件保存的全路径
 -- @return       string task.title: 上传文件的标题
 -- @return       string task.desc: 上传文件的描述
 -- @return       string task.type: 上传文件的类型
 -- @return       number task.maxsize: 上传文件的总大小
 -- @return       number task.size: 上传文件当前的上传的大小
 -- @return       string task.status: 当前上传状态
 ------------------------------------------------------------
 --]]
function Upload:getStatus(index)
    local task={}
    local upload = self:_getHandle()
    if index and index > 0 then
        _, task.id, task.remote, task.remoteparam, task.localfile,
            task.title, task.desc, task.type, task.maxsize, task.size,
            task.status = pluginInvoke(upload, 'GetItem', index - 1)
    else
        local count = self:getCount()
        for i = 1, count do
            table.insert(task, {})
            _, task[i].id, task[i].remote, task[i].remoteparam, task[i].localfile,
                task[i].title, task[i].desc, task[i].type, task[i].maxsize,
                task[i].size, task[i].status = pluginInvoke(upload, 'GetItem', i - 1)
        end
    end
    return task
end

--[[
 ------------------------------------------------------------
 -- @function Upload:getCount()
 ------------------------------------------------------------
 -- @brief 获取上传任务个数
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @return number
 ------------------------------------------------------------
 --]]
function Upload:getCount()
    return pluginInvoke(self:_getHandle(), 'Count')
end

--[[
 ------------------------------------------------------------
 -- @function Upload:append(urlpath, param, filePath, name, desc, fileExt)
 ------------------------------------------------------------
 -- @brief 添加上传任务
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param urlpath: string, 上传地址
 -- @param param: string, 客户端上传请求所必需的参数信息，这个PARAM需要保留在客户端，
 -- @param                用于客户端上传线程向UGC上传部件请求视频上传时提供的参数信息
 -- @param filePath: string, 上传文件路径
 -- @param name: string, 上传文件名
 -- @param desc: string, 上传文件描述
 -- @param fileExt: string, 上传文件后缀名
 ------------------------------------------------------------
 -- @return number/nil: -1代表该任务已经存在，其它数字则代表该任务正常添加，nil表示参数错误
 ------------------------------------------------------------
 --]]
function Upload:append(urlpath, param, filePath, name, desc, fileExt)
    if urlpath and name and filePath then
        return pluginInvoke(self:_getHandle(), "Append", urlpath , param, filePath, name, desc, fileExt)
    end
end

--[[
 ------------------------------------------------------------
 -- @function Upload:pause([index])
 ------------------------------------------------------------
 -- @brief 暂停一个上传任务,如果index为空或0则暂停所有上传
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param index: number, 上传任务索引号
 ------------------------------------------------------------
 -- @return nil
 ------------------------------------------------------------
 --]]
function Upload:pause(index)
    local task = {}
    local upload = self:_getHandle()
    if index and index > 0 then
        local task = self:getStatus(index, upload)
        if task and task.id and task.status ~= self.status.Failed and task.status ~= self.status.Finished then
            pluginInvoke(upload, "Pause", task.id)
        end
    else
        local count = self:getCount(upload)
        local task = self:getStatus(nil, upload, count)
        for i = 1, count do
            if task[i] and task[i].id and task[i].status ~= self.status.Failed and task[i].status ~= self.status.Finished then
                pluginInvoke(upload, "Pause", task[i].id)
            end
        end
    end
end

--[[
 ------------------------------------------------------------
 -- @function Upload:start(index)
 ------------------------------------------------------------
 -- @brief 开始一个上传任务
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param index: number, 上传任务索引号
 ------------------------------------------------------------
 -- @return nil
 ------------------------------------------------------------
 --]]
function Upload:start(index)
    local task={}
    local upload = self:_getHandle()
    if index and index > 0 then
        local task = self:getStatus(index, upload)
        if task and task.id then
            pluginInvoke(upload, "Resume", task.id)
        end
    end
end

--[[
 ------------------------------------------------------------
 -- @function Upload:delete(index)
 ------------------------------------------------------------
 -- @brief 删除一个上传任务
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param index: number, 上传任务索引号
 ------------------------------------------------------------
 -- @return nil
 ------------------------------------------------------------
 --]]
function Upload:delete(index)
    local upload = self:_getHandle()
    if index and index > 0 then
        local task = self:getStatus(index, upload)
        if task and task.id then
            pluginInvoke(upload, "Remove", task.id)
        end
    end
end

--[[
 ------------------------------------------------------------
 -- @function Upload:_getHandle()
 ------------------------------------------------------------
 -- @brief 获取上传插件句柄
 ------------------------------------------------------------
 -- @access private
 ------------------------------------------------------------
 -- @param 无
 ------------------------------------------------------------
 -- @return number
 ------------------------------------------------------------
 --]]
function Upload:_getHandle()
    local reg = registerCreate('upload')
    local upload = registerGetInteger(reg, 'Upload')
    if  upload == 0 then
         upload = pluginCreate('Upload')
         registerSetInteger(reg, 'Upload', upload)
    end
    return upload
end

