-- -----------------------------------------------------------------------------
-- | WonderTek [ 网络无处不在，沟通及时到达 ]
-- -----------------------------------------------------------------------------
-- | Copyright (c) 2012, WonderTek, Inc. All Rights Reserved.
-- -----------------------------------------------------------------------------
-- | Author: xxxx <xxxx@xxxx.com>
-- -----------------------------------------------------------------------------
-- | Desc: 常用功能函数
-- -----------------------------------------------------------------------------

function getWholeUrl(urlContent , params)
    local port
    if Config:get('server_port') == nil or Config:get('server_port') == '' then
        Config:set('server_port', '7011')
    end
    if Config:get('server_port') ~= nil and Config:get('server_port') ~= '' then
        port = ':' .. Config:get('server_port')
    end

    if Config:get('server_url') == nil or Config:get('server_url') == '' then
        Config:set('server_url', '61.191.25.238')
    end

    local nFindLastIndex = string.find(Config:get('server_url'), 'http://')
    if not nFindLastIndex then
        url = 'http://' .. Config:get('server_url') .. port .. '/'
    else
        url = Config:get('server_url') .. port .. '/'
    end
    local lines = '10'
    if Config:get('lines') ~= nil and Config:get('lines') ~= '' then
        lines = Config:get('lines')
    end
    url = url .. urlContent .. '?' .. 'usercode=' .. Config:get('username') .. '&pagesize=' .. lines.. '&' .. params
    return url
end
function setAllShoworHide(sprite, isShow)
    Sprite:setVisible(sprite, isShow)
    Sprite:setActive(sprite, isShow)
    Sprite:setEnable(sprite, isShow)
end
function getJsonArrayCount(data)
    local count = 0
    if data then
        if  #data == 0 and type(data[0]) == 'table' then
            count = 1
        else
            count = #data + 1
        end
    else
    return 0
    end
    return count
end
function isStrAllAlphAndNum(str)
    local len = string.len(str)
    for i = 1, len do
        local ch = string.sub(str, i, i)
        if not ((ch >= 'a' and ch <= 'z') or (ch >= 'A' and ch <= 'Z')) then
            if isStrAllNum(ch) == false then
                return false
            end
        end
    end
    return true
end
function isStrAllNum(str)
    local len = string.len(str)
    for i = 1, len do
        local ch = string.sub(str, i, i)
        if ch < '0' or ch > '9' then
            return false
        end
    end
    return true
end

function getCurDateAndTime()
    local year = os.date("*t")["year"]
    local month = os.date("*t")["month"]
    local day = os.date("*t")["day"]
    local hour = os.date("*t")["hour"]
    local minute = os.date("*t")["min"]
    local sec = os.date("*t")["sec"]
    return string.format('%04s%02s%02s%02s%02s%02s', year, month, day, hour, minute, sec)
end
function getCurDate()
    local year = os.date("*t")["year"]
    local month = os.date("*t")["month"]
    local day = os.date("*t")["day"]
    return string.format('%04s-%02s-%02s', year, month, day)
end

function SplitWithBlank(szFullString, szSeparator)
    local nFindStartIndex = 1
    local nSplitIndex = 1
    local nSplitArray = {}
    while true do
        local nFindLastIndex = string.find(szFullString, szSeparator, nFindStartIndex)
        if not nFindLastIndex then
            local stringData = string.sub(szFullString, nFindStartIndex, string.len(szFullString))
            nSplitArray[nSplitIndex] = stringData
            break
        end
        local stringData = string.sub(szFullString, nFindStartIndex, nFindLastIndex - 1)
        nSplitArray[nSplitIndex] = stringData
        nFindStartIndex = nFindLastIndex + string.len(szSeparator)
        nSplitIndex = nSplitIndex + 1
    end
    return nSplitArray
end

function Split(szFullString, szSeparator)
    local nFindStartIndex = 1
    local nSplitIndex = 1
    local nSplitArray = {}
    while true do
        local nFindLastIndex = string.find(szFullString, szSeparator, nFindStartIndex)
        if not nFindLastIndex then
            local stringData = string.sub(szFullString, nFindStartIndex, string.len(szFullString))
            
            if string.len(stringData) > 0 then
                nSplitArray[nSplitIndex] = stringData
            end
            break
        end
        local stringData = string.sub(szFullString, nFindStartIndex, nFindLastIndex - 1)
        if string.len(stringData) > 0 then
            nSplitArray[nSplitIndex] = stringData
        end
        nFindStartIndex = nFindLastIndex + string.len(szSeparator)
        nSplitIndex = nSplitIndex + 1
    end
    return nSplitArray
end

function getErrorCode(code)
    -- body
    if code == '0' then
        return '成功'
        elseif code == '-1' then
        return '接口内部发生异常'
        elseif code == '10' then
        return '用户不存在'
        elseif code == '11' then
        return '用户密码错误'
        elseif code == '12' then
        return '用户被禁用'
        elseif code == '13' then
        return '用户手机号码不正确（需做手机绑定）'
        elseif code == '14' then
        return '用户暂无权限'
        elseif code == '20' then
        return '没有待办事项'
        elseif code == '21' then
        return '待办处理失败'
        elseif code == '22' then
        return '巡检组不存在'
        elseif code == '23' then
        return '待办转派失败'
        elseif code == '24' then
        return '转派人不存在'
        elseif code == '30' then
        return '图片上传失败'
        elseif code == '40' then
        return '更新失败'
        elseif code == '50' then
        return '数据不存在'
        elseif code == '80' then
        return 'Cmd请求不合法（未知请求）'
        elseif code == '81' then
        return '请求参数有误'
        elseif code == '90' then
        return 'Xml解析异常'
    else
        return '未知错误'
    end
end