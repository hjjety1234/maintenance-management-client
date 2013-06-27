-- -----------------------------------------------------------------------------
-- | WonderTek [ 网络无处不在，沟通及时到达 ]
-- -----------------------------------------------------------------------------
-- | Copyright (c) 2012, WonderTek, Inc. All Rights Reserved.
-- -----------------------------------------------------------------------------
-- | Author: xxxx <xxxx@xxxx.com>
-- -----------------------------------------------------------------------------
-- | Desc: 常用功能函数
-- -----------------------------------------------------------------------------

function Util:getServer()
    return Config:get('server')
end

function Cb_doExit()
    Scene:exit()
end

function Util:getRequestTail()
    return '&WD_UUID='..Config:get('wduuid')..'&WD_CLIENT_TYPE='..Config:get('wdclienttype')..'&WD_UA='..Config:get('wduseragent')..'&WD_VERSION='..Config:get('app_version')..'&WD_CHANNEL='..Config:get('wd_channel')
end

-- @brief 去除字符创头部和尾部的空白字符
function trim(s)
  return (string.gsub(s,"^%s*(.-)%s*$", "%1"))
end

-- @brief 对各个页面OnSpriteEvent统一处理
function Util:onSpriteEvent(msg, param)
    if msg == MSG_SMS then                    -- 短消息
        Util:checkSmsrecommendFile()
    elseif msg == MSG_NETWORK then            --网络消息新写法
        Log:write('MSG_NETWORK ===111= ')
        if Loading:isShow() then Loading:close() end
        local paramStatus = param.networkstatus
        Log:write('paramStatus ===111= ', paramStatus)
        local paramFlag = param.flag
        Log:write('paramFlag ===111= ',paramFlag)
        if paramStatus == NETWORK_CONNECTED then
		         if Util:isIOSPlatform() then
		             if paramFlag == 2 then
		                 local reg = Reg:create(Util:getCurAppId())
                     Reg:setString(reg, 'curConnect', 'WLAN')
		             elseif paramFlag == 1 then
		                 local reg = Reg:create(Util:getCurAppId())
                     Reg:setString(reg, 'curConnect', 'CMWAP')
		             end
		          end
        elseif paramStatus == NETWORK_ERROR then
            Tips:show('当前网络中断！')
            if Http:getCurConnect() == 'WLAN' then
                Log:write('WLAN ===111= ')
		            if param.errno == NETERR_TRANS_FAIL or param.errno == NETERR_TRANS_LOGIN then
		                Log:write('WLAN ===222= ')
		                Timer:set(1,10000,'netConnect')
		            end
		        else
		            if param.errno == NETERR_TRANS_FAIL then
		                Log:write('WLAN ===333= ')
		                Timer:set(1,10000,'netConnect')
		            elseif param.errno == NETERR_TRANS_INVALIDAPN then
		                Log:write('WLAN ===444= ')
		                if Loading:isShow() then Loading:close() end
		                Tips:show('当前网络中断！')
		            end
		        end
        end
    elseif msg > MSG_NETWORK_ERROR then
        Log:write('msg > MSG_NETWORK_ERROR==111== ')
        if Loading:isShow() then Loading:close() end
        Tips:show('网络超时！')
    end
end

-- @brief 对各个页面OnPluginEvent统一处理
function Util:onPluginEvent(msg, param)
    Log:write('Util:onPluginEvent msg ==== ', msg)
    searchDialogOnPluginEvent(msg, param)
    if msg == MSG_NETWORK_ERROR then
        if Http:getCurConnect() == 'WLAN' then
            if param.errno == NETERR_TRANS_FAIL or param.errno == NETERR_TRANS_LOGIN then
                netConnect()
            end
        else
            if param.errno == NETERR_TRANS_FAIL then
                netConnect()
            elseif param.errno == NETERR_TRANS_INVALIDAPN then
                if Loading:isShow() then Loading:close() end
                Tips:show('当前网络中断！')
            end
        end
    elseif msg > MSG_NETWORK_ERROR then
        Log:write('msg > MSG_NETWORK_ERROR==== ')
        if Loading:isShow() then Loading:close() end
        Tips:show('网络超时！')
    end
end

function Util:checkSmsrecommendFile()
    Log:write('======Util:checkSmsrecommendFile===========')
    -- 判断是否存在消息推送文件，如存在则分析其中数据
    local msgFilePath = Util:getDefaultFolder(WDFIDL_MMS) .. 'msgpush.txt'
    Log:write('msgFilePath===========',msgFilePath)
    if IO:fileExist(msgFilePath) then
        local msgTable = IO:fileRead(msgFilePath)
        Log:write('msgTable===========',msgTable)
        local reg = Reg:create(Reg.com_wondertek_cnlive2.play)
        Reg:setString(reg, 'isLive', '1')
        Reg:setNumber(reg, 'currTime', 0)
        Reg:setString(reg, 'playopenUrl', msgTable)
        local aaa = Scene:getNameByHandle(Sprite:getCurScene())
        Log:write('getNameByHandle===========',aaa)
        if Scene:getNameByHandle(Sprite:getCurScene()) == Alias.play then
            Log:write('Scene:getNameByHandle(Sprite:getCurScene())===========',aaa)
            Log:write('getNameByHandle==Alias.play=====88888888===msgTable=',msgTable)
            if msgTable ~='' then
                Log:write('请求前================')
                Http:request('b01LeftListviewData', msgTable, 121, {useCache = false, method = 'post', postData = Util:getRequestTail()})
                Log:write('请求后================')
            end
        else
            Scene:setReturn(Alias.home, Alias.play)
            Scene:go(Alias.play,true)
        end
        IO:fileRemove(msgFilePath)
    end
end

-- @brief 通过配置和判断wlan状态连接网络
function netConnect()
    local wlanSuport = Config:get('wlan_support')
    Log:write('wlanSuport!!!!!!!!!!!!!!!!!!!!!!!!', wlanSuport)
    if wlanSuport == '1' then
        local isSwitchOn = WLAN:isSwitchOn()
        Log:write('WLANIsSwitchOn!!!!!!!!!!!!!!!!!!!!!!!!', isSwitchOn)
        if isSwitchOn then -- WLAN开关是否打开
            attachedWLAN = WLAN:isAttach()
            Log:write('attachedWLAN!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!', attachedWLAN)
            if attachedWLAN then -- 判断是否已附着到某个WLAN网络
                Util:setCurSSID(attachedWLAN.ssid)
                Http:connectWLAN(attachedWLAN.ssid)
            else
                connectCellularNetwork()
            end
        else
            Log:write('WLANIsNotSwitchOn!!!!!!!!!!!!!!!!!!!!!!!!', isSwitchOn)
            connectCellularNetwork()
        end
    else
        Log:write('wlanSuportIsNotSwitchOn!!!!!!!!!!!!!!!!!!!!!!!!', wlanSuport)
        connectCellularNetwork()
    end
end

function connectCellularNetwork()
    local APNtype = Http:getCurrentAPNType()
    if APNtype == 1 then                     -- Net网
        Http:setProxy('')
    elseif APNtype == 2 then                 --移动wap
        Http:setProxy('http://10.0.0.172:80/')
    elseif APNtype == 3 then                 --电信wap
        Http:setProxy('http://10.0.0.200:80/')
    elseif APNtype == 4 then                 --联通wap
        Http:setProxy('http://10.0.0.172:80/')
    else
        Http:setProxy('')
    end
    Http:connectCMWAP()
end

--[[
 ------------------------------------------------------------
 -- @function Util:getCurSSID()
 ------------------------------------------------------------
 -- @brief 获取当前wlan链接ssid
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param 无
 ------------------------------------------------------------
 -- @return string
 ------------------------------------------------------------
 --]]
function Util:getCurSSID()
    local reg = Reg:create(Reg.com_wondertek_cnlive2.wlan)
    return Reg:getString(reg, 'curSSID')
end

--[[
 ------------------------------------------------------------
 -- @function Util:setCurSSID(ssid)
 ------------------------------------------------------------
 -- @brief 保存当前wlan链接ssid
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param string: ssid
 ------------------------------------------------------------
 -- @return nil
 ------------------------------------------------------------
 --]]
function Util:setCurSSID(ssid)
    local reg = Reg:create(Reg.com_wondertek_cnlive2.wlan)
    Reg:setString(reg, 'curSSID', ssid)
end

function Util:isIOSPlatform()
    local platform = Config:get('platform')
    if platform == 'iOS' then
        return true
    else
        return false
    end
end

-- -----------------------------------------------------------------------------
-- @class Tips
-- @note  文字提示框
-- -----------------------------------------------------------------------------

Tips = {}

Tips.layout = [[
<?xml version="1.0" encoding="utf-8"?>
<root>
    <header/>
    <body>
        <image name="tipsBgImg" rect="100,600,440,140" src="file://image/tips_bg.png" style="sudoku-auto" sudoku="7,7,7,7" extendstyle="1010" />
        <textarea name="tipsText" rect="100,600,440,140" text="" color="#ffffff" font-size="22" h-align="center" v-center="true" autoextend="true" extendstyle="1010"  />
        <label name="tipsLbl" rect="100,600,440,140" text="" color="#ffffff" font-size="22" h-align="center" v-align="center" extendstyle="1010"  />
    </body>
</root>
]]

function Tips:show(text)
    local rootSprite = Sprite:getCurScene()
    local tipsNode = Sprite:findChild(rootSprite, 'tipsNode')
    if tipsNode ~= 0 then
        Sprite:setVisible(tipsNode, 1)
        Sprite:setEnable(tipsNode, 1)
        Sprite:setActive(tipsNode, 1)
    else
        tipsNode = Sprite:create('node', Sprite:findChild(rootSprite, 'mainNode'))
        Sprite:setProperty(tipsNode, 'name', 'tipsNode')
        --if not Menu:isShow(Sprite:getCurScene()) then
        --    Sprite:setProperty(tipsNode, 'padding', '0,0,0,0')
        --else
        --    Sprite:setProperty(tipsNode, 'padding', '0,0,48,0')
        --end
        Sprite:setProperty(tipsNode, 'extendstyle', '0010')
        Sprite:setRect(tipsNode, 0,600,640,140)
        Sprite:loadFromString(tipsNode, Tips.layout)
    end
    local _, _, tipsNodeW, tipsNodeH = Sprite:getRect(tipsNode)
    Log:write('tipsNodeH', tipsNodeH)
    local textarea = Sprite:findChild(tipsNode, 'tipsText')
    local tipsBgImg = Sprite:findChild(tipsNode, 'tipsBgImg')
    Sprite:setProperty(textarea, 'text', text)
    local tX, tY, tW, tH = Sprite:getRect(textarea)
    if tH > 26 then
        local tipsLbl = Sprite:findChild(tipsNode, 'tipsLbl')
        Sprite:setEnable(tipsLbl, 0)
        Sprite:setVisible(tipsLbl, 0)
        textarea = Sprite:findChild(tipsNode, 'tipsText')
        Sprite:setProperty(textarea, 'text', text)
        Sprite:setEnable(textarea, 1)
        Sprite:setVisible(textarea, 1)
        tX, tY, tW, tH = Sprite:getRect(textarea)
        Log:write('tX1', tX)
        Sprite:setRect(textarea, tX, tipsNodeH - tH - 10, tW, tH)
    else
        Sprite:setEnable(textarea, 0)
        Sprite:setVisible(textarea, 0)
        textarea = Sprite:findChild(tipsNode, 'tipsLbl')
        Sprite:setProperty(textarea, 'text', text)
        Sprite:setEnable(textarea, 1)
        Sprite:setVisible(textarea, 1)
        tX, tY, tW, tH = Sprite:getRect(textarea)
        Sprite:setRect(textarea, (tipsNodeW - tW) / 2, tipsNodeH - tH - 10, tW, tH)
        tX, tY, tW, tH = Sprite:getRect(textarea)
    end
    Sprite:setRect(tipsBgImg, tX - 15, tipsNodeH - tH - 20, tW + 30, tH + 20)
    Log:write('tX2', tX)
    Timer:set(1, 2200, '_closeTipsOnTimer')
end

function Tips:close()
    local tipsNode = Sprite:findChild(Sprite:getCurScene(), 'tipsNode')
    Sprite:setVisible(tipsNode, 0)
    Sprite:setEnable(tipsNode, 0)
    Sprite:setActive(tipsNode, 0)
end

function _closeTipsOnTimer()
    Tips:close()
end

--[[
 ------------------------------------------------------------
 -- @function savePlayHistoryToLocal(contparam, nodeName, titleName, breakPoint, objType)
 ------------------------------------------------------------
 -- @brief 本地存储播放历史记录信息
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param string: titleName
 -- @param integer: breakPoint
 -- @param integer: isFinish
 ------------------------------------------------------------
 -- @return nil
 ------------------------------------------------------------
 --]]
--[[  播放历史记录本地存储  ]]--
function saveHistoryToLocal(contId, nodeId,contName, curTime,totalTime,isFinish,isLive)
    local ct = os.time()*1000 --Util:getServerTime()
    Log:write("ct",ct)
    local d = os.date('*t', math.floor(ct / 1000))
    local date = d.year .. '-' .. timeFormat1(d.month) .. '-' .. timeFormat1(d.day) ..' '.. timeFormat1(d.hour) .. ':' .. timeFormat1(d.min) .. ':' .. timeFormat1(d.sec)
    local reg = Reg:create(Reg.com_wondertek_cnlive2.history)
    local historyFilePath = 'USERDATA:\\history1.xml'
    Reg:load(reg, historyFilePath)
    local hisString = Reg:getString(reg, contId)
    local t = Util:stringToTable(hisString)
    if t then --有历史记录
        t['date']=date
        t['contId']=contId
        t['nodeId']=nodeId
        t['contName']=contName
        t['currTime']=curTime
        t['totalTime']=totalTime
        t['isFinish']=isFinish and isFinish or 0
        t['live']=isLive
    else
        t = {['date']=date,['contId']=contId,['nodeId']=nodeId,['contName']=contName,['currTime']=curTime,['totalTime']=totalTime,['isFinish']=isFinish and isFinish or 0,['live']=isLive}
    end
    Log:write("hisTable",t)
    Reg:setString(reg, contId, Util:tableToString(t))
    Log:write('7777777777777',Util:tableToString(t))
    local str = Reg:getString(reg, 'historyContent')
    Reg:setString(reg, 'historyContent', contId .. ','..string.gsub(str,contId .. ',','') )
    Reg:save(reg, historyFilePath)
    Reg:release(Reg.com_wondertek_cnlive2.history)
end

function timeFormat1(time)
    if string.len(time) == 1 then
        return '0'..time
    else
        return time
    end
end

--[[
 ------------------------------------------------------------
 -- @function saveHistoryToServer(cid, nid, endTime, totalTime)
 ------------------------------------------------------------
 -- @brief 播放历史记录上传服务器
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param string: cid  内容ID
 -- @param string: nid  栏目ID
 -- @param integer: endTime  总时长
 -- @param integer: totalTime  观看时长
 ------------------------------------------------------------
 -- @return nil
 ------------------------------------------------------------
 --]]
--[[  播放历史记录上传服务器  ]]--
function saveHistoryToServer(cid, nid, endTime, totalTime)
    local reg_authority = Reg:create('authority')
    local userStatus = Reg:getInteger(reg_authority, 'userStatus')
    if userStatus == 1 then
        local loginName = Reg:getString(reg_authority, 'loginName')
        local uploadUrl = Util:getServer() .. 'clt/playHistory.html'
        local postData = 'loginName=' .. loginName .. '&c=' .. cid .. '&n=' .. nid .. '&totalTime=' .. totalTime .. '&endTime=' .. endTime
        Http:request('history_upload_data', uploadUrl, 2003, {useCache=false, method='post', postData=postData})
    else
        Tips:show('登录可上传历史记录至服务器')
    end
end

--[[
 ------------------------------------------------------------
 -- @function getHistoryFromServer()
 ------------------------------------------------------------
 -- @brief 从服务器获取播放历史记录
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @return nil
 ------------------------------------------------------------
 --]]
--[[  从服务器获取播放历史记录  ]]--
function getHistoryFromServer()
    local reg_authority = Reg:create('authority')
    local userStatus = Reg:getInteger(reg_authority, 'userStatus')
    if userStatus == 1 then
        local loginName = Reg:getString(reg_authority, 'loginName')
        local histroyUrl = Util:getServer() .. 'clt/myHistory.msp'
        local postData = 'loginName=' .. loginName
        Http:request('history_upload_data', histroyUrl, 2004, {useCache=false, method='post', postData=postData})
    else
        Tips:show('登录后可从服务器获取播放历史记录')
    end
end

--[[
 ------------------------------------------------------------
 -- @function saveCommentToLocal(commentText, videoName)
 ------------------------------------------------------------
 -- @brief 本地存储我的评论信息
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param string: commentText
 -- @param string: videoName
 ------------------------------------------------------------
 -- @return nil
 ------------------------------------------------------------
 --]]
--[[  我的评论本地存储  ]]--
function saveCommentToLocal(commentText, videoName)
    local ct = os.time()*1000 --Util:getServerTime()
    Log:write("ct",ct)
    local d = os.date('*t', math.floor(ct / 1000))
    local date = d.year .. '-' .. d.month .. '-' .. d.day
    local reg = Reg:create(Reg.com_wondertek_cnlive2.comment)
    local commentFilePath = 'USERDATA:\\comment.xml'
    Reg:load(reg, commentFilePath)
    local cmtString = Reg:getString(reg, commentText)
    local t = Util:stringToTable(cmtString)
    if t then --有历史记录
        t['date']=date
        t['commentText']=commentText
        t['videoName'] = videoName
    else
        t = {['date']=date,['commentText']=commentText,['videoName']=videoName}
    end
    Log:write("commentTable",t)
    Reg:setString(reg, commentText, Util:tableToString(t))
    local str = Reg:getString(reg, 'commentContent')
    Reg:setString(reg, 'commentContent', commentText .. ',' .. string.gsub(str, commentText .. ',', ''))
    Reg:save(reg, commentFilePath)
    Reg:release(Reg.com_wondertek_cnlive2.comment)
end

--[[
 ------------------------------------------------------------
 -- @function saveBookingToLocal(videoName, viewTime)
 ------------------------------------------------------------
 -- @brief 本地存储我的预约信息
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param string: videoName
 -- @param string: viewTime
 ------------------------------------------------------------
 -- @return nil
 ------------------------------------------------------------
 --]]
--[[  我的预约本地存储  ]]--
function saveBookingToLocal(videoId,videoName, viewTime)
    --local ct = os.time()*1000 --Util:getServerTime()
    --Log:write("ct",ct)
    --local d = os.date('*t', math.floor(ct / 1000))
    --local date = d.year .. '-' .. d.month .. '-' .. d.day
    local reg = Reg:create(Reg.com_wondertek_cnlive2.booking)
    local bookFilePath = 'USERDATA:\\booking2.xml'
    Reg:load(reg, bookFilePath)
    local bookString = Reg:getString(reg, videoId)
    local t = Util:stringToTable(bookString)
    if t then --有历史记录
        t['videoId']=videoId
        t['viewTime']=viewTime
        t['videoName'] = videoName
    else
        t = {['videoId']=videoId,['viewTime']=viewTime,['videoName']=videoName}
    end
    Log:write("bookingTable",t)
    local times = Reg:getString(reg, 'bookingTime')
    local index = getBookIndex(times,timeToNum(viewTime))
    Reg:setString(reg, 'bookingTime', times..timeToNum(viewTime)..',')
    Reg:setString(reg, videoId, Util:tableToString(t))
    local str = Reg:getString(reg, 'bookingContent')
    Reg:setString(reg, 'bookingContent', insertVideoId(str,videoId,index))
    Reg:save(reg, bookFilePath)
    Reg:release(Reg.com_wondertek_cnlive2.booking)
end

function hasBookingToLocal(videoId)
    local reg = Reg:create(Reg.com_wondertek_cnlive2.booking)
    local fileName = 'USERDATA:\\booking2.xml'
    Reg:load(reg, fileName)
    local bookingContent = Reg:getString(reg, 'bookingContent')
    local i,j = string.find(bookingContent, videoId)
    if i then --有历史记录
        return '1';
    else
        return '0';
    end
end

function timeToNum(time) -- 时间转换为可比较字符串
    Log:write('44444444444444',time)
    local time1 = string.gsub(time,'-','')
    local time2 = string.gsub(time1,':','')
    local time3 = string.gsub(time2,' ','')
    return time3
end

function getBookIndex(bookingTime,viewTime) -- 获取一个数字字符串在一组数字字符串的位置
    Log:write('bookingTimes======',bookingTime,viewTime)
    local bookingTimes = Util:split(bookingTime,',')
    Log:write('bookingTimes======',bookingTimes)
    local index = 0
    for i=1,#bookingTimes-1 do
        if compare(bookingTimes[i],viewTime) == 0 then
            index = index + 1
        end
    end
    Log:write('index==============',index)
    return index
end

function compare(str1,str2) -- 定义两个数字字符串的比较规则
    for i=1,string.len(str2) do
        if tonumber(string.sub(str1,i,i+1)) > tonumber(string.sub(str2,i,i+1)) then
            return 0
        elseif tonumber(string.sub(str1,i,i+1)) < tonumber(string.sub(str2,i,i+1)) then
            return 1
        end
    end
end

function insertVideoId(bookingContent,viewId,index) -- 在index的位置插入一个数字字符串
    local bookingContents = Util:split(bookingContent,',')
    Log:write('bookingContents===============',index,bookingContents)
    local bookingContents1 = ''
    for i=1,#bookingContents do
        if i == index+1 then
            bookingContents1 = bookingContents1..viewId..','
        end
        if bookingContents[i] ~= nil and bookingContents[i] ~= '' then
            bookingContents1 = bookingContents1..bookingContents[i]..','
        end
    end
    return bookingContents1
end

 -- @brief 删除本地存储我的预约信息
function delMyBooking(videoId)
    local reg = Reg:create(Reg.com_wondertek_cnlive2.booking)
    local fileName = 'USERDATA:\\booking2.xml'
    Reg:load(reg, fileName)
    local bookingTime = Reg:getString(reg, 'bookingTime')
    local bookString = Reg:getString(reg,videoId)
    local bookTable = Util:stringToTable(bookString)
    Reg:setString(reg, 'bookingTime', string.gsub(bookingTime, timeToNum(bookTable.viewTime) .. ',', ''))
    local bookingContent = Reg:getString(reg, 'bookingContent')
    Reg:setString(reg, 'bookingContent', string.gsub(bookingContent, videoId .. ',', ''))
    Reg:remove(reg, videoId)
    Reg:save(reg, fileName)
    Reg:release(Reg.com_wondertek_cnlive2.booking)
end

 -- @brief 本地存储我的登录信息
function getAccountAndPasswordFromLocal()
    local reg = Reg:create(Reg.com_wondertek_cnlive2.accountmgr)
    local userAccountFilePath = 'USERDATA:\\userAccountServer.xml'
    Reg:load(reg, userAccountFilePath)
    local account = Reg:getString(reg, 'account')
    local password = Reg:getString(reg, 'password')
    Reg:release(Reg.com_wondertek_cnlive2.accountmgr)
    return account, password
end



function saveUserAccountAndPassword(account, password)
    local reg = Reg:create(Reg.com_wondertek_cnlive2.accountmgr)
    local userAccountFilePath = 'USERDATA:\\userAccountServer.xml'
    Reg:load(reg, userAccountFilePath)
    Reg:setString(reg, 'account', account)
    Reg:setString(reg, 'password', password)
    Reg:save(reg, userAccountFilePath)
    Reg:release(Reg.com_wondertek_cnlive2.accountmgr)
end

function delAccount(account,password)
    local reg = Reg:create(Reg.com_wondertek_cnlive2.accountmgr)
    local userAccountFilePath = 'USERDATA:\\userAccountServer.xml'
    Reg:load(reg, userAccountFilePath)
    Reg:remove(reg, account)
    Reg:remove(reg, password)
    Reg:save(reg, userAccountFilePath)
end

--[[
 ------------------------------------------------------------
 -- @function saveMyFavToLocal(videoName, videoType)
 ------------------------------------------------------------
 -- @brief 本地存储我的收藏信息
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param string: contentName
 -- @param integer: contentType 1-直播电视台 2-栏目 3-电影 4-电视剧 5-综艺 6-动漫 7-原创 8-栏目
 ------------------------------------------------------------
 -- @return nil
 ------------------------------------------------------------
 --]]
--[[  我的收藏本地存储  ]]--
function saveMyFavToLocal(contentName, contentType)
    local reg = Reg:create(Reg.com_wondertek_cnlive2.favorite)
    local favoriteFilePath = 'USERDATA:\\favorite2.xml'
    Reg:load(reg, favoriteFilePath)
    local favString = Reg:getString(reg, contentName)
    local t = Util:stringToTable(favString)
    if t then
        t['contentType']=contentType
        t['contentName'] = contentName
    else
        t = {['contentType']=contentType,['contentName']=contentName}
    end
    Log:write("favoriteTable",t)
    Reg:setString(reg, contentName, Util:tableToString(t))
    local str = Reg:getString(reg, 'favoriteContent'..contentType)
    Reg:setString(reg, 'favoriteContent'..contentType, contentName .. ',' .. string.gsub(str, contentName .. ',', ''))
    Reg:save(reg, favoriteFilePath)
    Reg:release(Reg.com_wondertek_cnlive2.favorite)
end

function delMyFavorite(contentName, contentType)
    local reg = Reg:create(Reg.com_wondertek_cnlive2.favorite)
    local fileName = 'USERDATA:\\favorite2.xml'
    Reg:load(reg, fileName)
    local favoriteContent = Reg:getString(reg, 'favoriteContent'..contentType)
    Log:write('favoriteContent', favoriteContent)
    Reg:setString(reg, 'favoriteContent'..contentType, string.gsub(favoriteContent, contentName .. ',', ''))
    Reg:remove(reg, contentName)
    Reg:save(reg, fileName)
end

function loadLocalDataFromXml(regName, xmlPath, keyName)
    local reg = Reg:create(regName)
    Reg:load(reg, xmlPath)
    local keyContent = Reg:getString(reg, keyName)
    local contentIdList = Util:split(keyContent, ',')
    local tmpTable = {}
    if contentIdList[1] ~= '' then
        table.remove(contentIdList)
        for i = 1, #contentIdList do
            local strData = Reg:getString(reg, contentIdList[i])
            table.insert(tmpTable, Util:stringToTable(strData) )
        end
    end
    Reg:release(regName)
    Log:write('TmpTable', tmpTable)
    return tmpTable
end

-----本地存储微博绑定信息
function loadWeiboDataFromXml(keyName)
    local reg = Reg:create('Reg.com_wondertek_cnlive2.weibo')
    Reg:load(reg, 'USERDATA:\\weibo1.xml')
    local keyContent = Reg:getString(reg, keyName)
    return keyContent
end

function saveWeiboToLocal(key, value)
    local reg = Reg:create(Reg.com_wondertek_cnlive2.weibo)
    local weiboFilePath = 'USERDATA:\\weibo1.xml'
    Reg:load(reg, weiboFilePath)
    Reg:setString(reg, key, value)
    Reg:save(reg, weiboFilePath)
    Reg:release(Reg.com_wondertek_cnlive2.weibo)
end

function delWeibo(key)
    local reg = Reg:create(Reg.com_wondertek_cnlive2.weibo)
    local fileName = 'USERDATA:\\weibo1.xml'
    Reg:load(reg, fileName)
    Reg:remove(reg, key)
    Reg:save(reg, 'USERDATA:\\weibo1.xml')
end

--[[
 ------------------------------------------------------------
 -- @function Util:tableToString()
 ------------------------------------------------------------
 -- @brief table对象转换为字符串
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param talbe t table对象
 ------------------------------------------------------------
 -- @return integer
 ------------------------------------------------------------
 --]]
function Util:tableToString(t)
    if type(t) == 'table' then
        return Util:tostring(t, '', '')
    else
        return nil
    end
end

function Util:stringToTable(s)
    local returnTable = loadstring("return "..s)()
    if type(returnTable) == 'table' then
        return returnTable
    else
        return nil
    end
end

function convertToLimited(reg,serialStr,limitCount)
    local newFavTable = Util:split(serialStr,",")
    local newStr = ''
    if newFavTable and #newFavTable >= limitCount then
        for i = #newFavTable, 1, -1 do
            if i <= limitCount then
                newStr = newFavTable[i]..','..newStr
            else
                Reg:remove(reg, newFavTable[i])
            end
        end
    else
        newStr = serialStr
    end
    return newStr
end

 --[[
 ------------------------------------------------------------
 -- @function Util:urlencode(str)
 ------------------------------------------------------------
 -- @brief 返回字符串，此字符串中除了 -_. 之外的所有非字母数字字符都将被替换成百分号（%）后跟两位十六进制数，空格则编码为加号（+）。
 -- @brief 此编码与 WWW 表单 POST 数据的编码方式是一样的，同时与 application/x-www-form-urlencoded 的媒体类型编码方式一样。由于历史原因，
 -- @brief 此编码在将空格编码为加号（+）方面与 RFC1738 编码不同。此函数便于将字符串编码并将其用于 URL 的请求部分，同时它还便于将变量传递给下一页：
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param str: string, 要转换的字符串
 ------------------------------------------------------------
 -- @return string
 ------------------------------------------------------------
 --]]
function Util:urlencode(str)
    local rs = {}
    for i = 1, #str do
        local ch = string.sub(str, i, i)
        if ch == ' ' then
            table.insert(rs, '+')
        elseif not string.match(ch, '[a-zA-Z0-9_%-%.]') then
            table.insert(rs, string.upper('%' .. self:dechex(string.byte(ch))))
        else
            table.insert(rs, ch)
        end
    end

    return table.concat(rs)
end

--[[
 ------------------------------------------------------------
 -- @function Util:dechex(n)
 ------------------------------------------------------------
 -- @brief 返回一字符串，包含有给定 number 参数的十六进制表示。
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param n: number, 要转换的数字
 ------------------------------------------------------------
 -- @return string
 ------------------------------------------------------------
 --]]
function Util:dechex(n)
	local dexTable = {
		[0] = '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'a', 'b', 'c', 'd', 'e', 'f',
	}
	if math.floor(n / 16) ~= 0 then
		return self:dechex(math.floor(n / 16)) .. dexTable[n % 16]
	else
		return dexTable[n % 16]
	end
end
