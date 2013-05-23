-- -----------------------------------------------------------------------------
-- | WonderTek [ 网络无处不在，沟通及时到达 ]
-- -----------------------------------------------------------------------------
-- | Copyright (c) 2012, WonderTek, Inc. All Rights Reserved.
-- -----------------------------------------------------------------------------
-- | Author: xxxx <xxxx@xxxx.com>
-- -----------------------------------------------------------------------------
-- | Desc: 常用功能函数
-- -----------------------------------------------------------------------------

function backBtn(sprite)
    Scene:back()
end

function goHome(sprite)
    Scene:go(Alias.home)
end

function goMail(sprite)
    Scene:setReturn(Scene:getNameByHandle(Sprite:getCurScene()), Alias.mailList)
    Scene:go(Alias.mailList)
end

function goDangZhen(sprite)
    Scene:setReturn(Scene:getNameByHandle(Sprite:getCurScene()), Alias.dangzhengxinxiList)
    Scene:go(Alias.dangzhengxinxiList)
end

function goGongWen(sprite)
    Scene:setReturn(Scene:getNameByHandle(Sprite:getCurScene()), Alias.gongwen)
    Scene:go(Alias.gongwen)
end

function setAllShoworHide(sprite, isShow)
    Sprite:setVisible(sprite, isShow)
    Sprite:setActive(sprite, isShow)
    Sprite:setEnable(sprite, isShow)
end
-- 根据ip地址和端口号组装一个url地址
function getUrl()
    local port = Config:get('server_port')
    if not port or port == '' then
        Config:set('server_port', '8090')
        port = '8090'
    end
    port = ":" .. port
    local server_url = Config:get('server_url')
    if not server_url or server_url == '' then
        Config:set('server_url', '120.209.131.143')
        server_url = '120.209.131.143'
    end
    return 'http://' .. server_url .. port .. '/'
end

-- 获得请求url地址
function getWholeUrl(urlContent, params)
    local url = getUrl()
    local lines = Config:get('lines')
    if not lines and lines ~= '' then
        lines = '10'
    end
    if isPost then
        return url .. urlContent
    end
    if params and params ~= '' then
        return url .. urlContent .. '?' .. 'usercode=' .. Config:get('username') .. '&pagesize=' .. lines.. '&' .. params
    else
        return url .. urlContent .. '?' .. 'usercode=' .. Config:get('username') .. '&pagesize=' .. lines
    end
end
function getGongwenUrl(cmd, searchStr, pageindex, pagesize)
    local url = Config:get('server_url') .. 'Document/DocumentManager.ashx?cmd=' .. cmd .. '&usercode=' .. Config:get('username')
    if cmd == 'yibanlist' or cmd == 'yiyuelist' then
        return url .. '&orgid=' .. Config:get('orgId') .. '&pageindex='..(pageindex or '')..'&pagesize='..(pagesize or '')..'&searchValue=' .. (searchStr or '')
    end
    return url
end

function getDangzhenUrl(cmd, searchStr, pageindex, pagesize)
    local url = Config:get('server_url') .. 'Party/PartyManagerService.ashx?cmd=' .. cmd .. '&usercode=' .. Config:get('username')
    if cmd == 'newsdetail' then
        return url
    end
    return url .. '&pageindex='..(pageindex or '')..'&pagesize='..(pagesize or '')..'&searchValue=' .. (searchStr or '')
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
function getMailUrl(oper,pagesize,curpage)
    local mailreceServer = Config:get('mailreceServer')
    local mailsendServer = Config:get('mailsendServer')
    local mailUser = Config:get('mailuser')
    local mailpwd = Config:get('mailpwd')
    local ip = 'http://120.209.131.146/webcloud/'
    local param = '&mailUser=' .. mailUser .. '&mailPassword=' .. mailpwd
    if oper == 'detail' then
        return ip .. 'client/mail/getMailDetail.html?'..'mailServer=' .. mailreceServer .. param
    elseif oper == 'send' then
        return ip .. 'client/mail/sendMail.html?' ..'mailServer=' .. mailsendServer .. param
    elseif oper == 'upload' then
        return ip .. 'upload/UGC_GetUploadUrl.html?'
    else
        return ip .. 'client/mail/getMailList.html?' ..'mailServer=' .. mailreceServer.. param..'&pageSize='..(pagesize or '')..'&currentPage='..(curpage or '')
    end
end

function getFileNameAndExt(filePath)
    local reversStr = string.reverse(filePath)
    local line = string.find(reversStr, '%/')
    local point = string.find(reversStr, '%.')
    if point and point ~= 1 then
        local fileName
        if line then
            fileName = string.sub(filePath, -line+1, -point-1)
        else
            fileName = string.sub(filePath, 1, -point-1)
        end
        local fileExt = string.sub(filePath, -point+1)
        return fileName, fileExt
    end
    return nil
end

function handleF2Key(sprite)
    if Loading:isShow() then
        Loading:close()
        return
    elseif Dialog:isShow() then
        Dialog:close()
        return
    end
    backBtn()
end

function setSpriteVisible(sprite, visible)
    Sprite:setVisible(sprite, visible)
    Sprite:setEnable(sprite, visible)
end

function doExit()
    IO:dirRemove('WONDER:\\temp', 1)
    IO:dirRemove('CACHE:\\com_wondertek_hsoa', 1)
    Scene:exit()
end

function getCurDateAndTime()
    local year = os.date("*t")["year"]
    local month = os.date("*t")["month"]
    local day = os.date("*t")["day"]
    local hour = os.date("*t")["hour"]
    local minute = os.date("*t")["min"]
    local sec = os.date("*t")["sec"]
    return string.format('%04s-%02s-%02s %02s:%02s:%02s', year, month, day, hour, minute, sec)
end
function Util:stringToTable(s)
    local returnTable = loadstring("return "..s)()
    if type(returnTable) == 'table' then
        return returnTable
    else
        return {}
    end
end
function Util:tableToString(t)
    if type(t) == 'table' then
        return Util:tostring(t, '', '')
    else
        return nil
    end
end
function deleApeopleFromTable(table)
    local tableLenth = #table
    for i=tableLenth,1, -1 do
        local timeLine = Util:getTimeRegionPosition(remindsTable[i].startTime,remindsTable[i].endTime)
        if timeLine == 'current' or timeLine == 'before' then
            table.remove(remindsTable,i)
        end
    end
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
