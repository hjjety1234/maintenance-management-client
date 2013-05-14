-- -----------------------------------------------------------------------------
-- | WonderTek [ 网络无处不在，沟通及时到达 ]
-- -----------------------------------------------------------------------------
-- | Copyright (c) 2012, WonderTek, Inc. All Rights Reserved.
-- -----------------------------------------------------------------------------
-- | Author: xxxx <xxxx@xxxx.com>
-- -----------------------------------------------------------------------------
-- | Desc: 常用功能函数
-- -----------------------------------------------------------------------------
 -- 根据ip地址和端口号组装一个url地址
function getUrl()
    local port = Config:get('server_port')
    if not port or port == '' then
        Config:set('server_port', '7011')
        port = '7011'
    end
    port = ":" .. port
    local server_url = Config:get('server_url')
    if not server_url or server_url == '' then
        Config:set('server_url', '10.225.222.5')
        server_url = '10.225.222.5'
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
    return string.format('%04s-%02s-%02s %02s:%02s:%02s', year, month, day, hour, minute, sec)
end
--取系统日期时间，少一天的误差
function getCurDateAndTimeAddOneDay()
    local now = os.date("*t",os.time())
    local year = now['year']
    local month = now['month']
    local day = now['day']
    local hour = os.date("*t")["hour"]
    local minute = os.date("*t")["min"]
    local sec = os.date("*t")["sec"]
    return string.format('%04s-%02s-%02s %02s:%02s:%02s', year, month, day, hour, minute, sec)
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
        return '参数错误，请联系代维平台管理员'
        elseif code == '10' then
        return '用户不存在'
        elseif code == '11' then
        return '用户密码错误'
        elseif code == '12' then
        return '用户被禁用'
        elseif code == '13' then
        return '手机未注册，请在系统端注册该终端'
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

-- 显示或隐藏搜索提示
function editOnTextChanged(sprite,ncount)
    local hideLabel = Sprite:findChild(sprite, 'hideLabel')
    if ncount > 0 then
        setAllShoworHide(hideLabel, 0)
    else
        setAllShoworHide(hideLabel, 1)
    end
end

function Util:backgroundApp()
    BackgroundApp()
end

--------------------------------统计报表相关函数----------------------------
require 'framework.webbrowser'
-- 在webview中显示fusionCharts统计报表
-- @param dataUrl - string 数据源地址UTF-8编码
-- @param charType -- string 
--                    柱状图Column
--                    条形图Bar
--                    折线图line
--                    饼状图pie
--                    仪表盘 AngularGauge
-- @param x,y,w,h - int 坐标位置和长宽度
function showChartInWebView(dataUrl, chartType, x, y, w, h)
    -- 取得云平台服务器地址
    local webcloud = Config:get('webcloud')
    if webcloud == nil or webcloud == '' then 
        Log:write('webcloud server url is missing!')
        return
    end
    Log:write('webcloud:'..webcloud)
    
    -- 解析报表类型
    local swf = ''
    if chartType == 'Column' then
        swf = 'MSColumn3D.swf '
    elseif chartType == 'Bar' then
        swf = 'MSBar3D.swf'
    elseif chartType == 'Pie' then 
        swf = 'Pie3D.swf'
    elseif chartType == 'Line'  then
        swf = 'MSLine.swf'
    elseif chartType == 'AngularGauge'  then
        swf = 'WidgetsCharts/AngularGauge.swf'
    else
        Log:write('unknown chart type:'..chartType)
        return
    end
    
    -- 构造请求地址
    local requestUrl = string.format('http://%s/webcloud/client/chart/chart_show.action?'..
        'dataUrl=%s&swf=%s&renderer=javascript&dataFormat=jsonurl',
        webcloud, dataUrl, swf)
    Log:write('info: showChart() requestUrl', requestUrl)
    
    -- 
    local xscal = SCREEN_WIDTH / 480
    local yscal = SCREEN_HEIGHT / 800
    WebBrowser:create(x, y*yscal, w*xscal, h*yscal)
    WebBrowser:showWebBrowser(1)
    WebBrowser:openUrl(requestUrl)
end

-- 在webview中显示HighCharts统计报表
-- @param title - string 报表标题
-- @param labels - string 报表的底部名称，以逗号分隔
-- @param values - string 与报表底部名称对应的统计值,以逗号分隔，
--                 如果是MULTI类型的报表则以分号分隔,内部再以逗号分隔
-- @param seriesType - string 定义报表类型single/multi/pie
-- @param showType - string 图表的类型 line/spline/bar/column/pie/piedonut
function showHighCharts(title, labels, values, seriesType, showType)
    -- 打印日志
    local param = string.format('showHighCharts(title=%s, label=%s, values=%s, seriesType = %s, showType = %s)', 
        title, labels, values, seriesType, showType)
    Log:write(param)

    -- 取得云平台服务器地址
    local webcloud = Config:get('webcloud')
    if webcloud == nil or webcloud == '' then 
        Log:write('webcloud server url is missing!')
        return
    end
    Log:write('webcloud:'..webcloud)

    -- 检查webview是否打开
    if WebBrowser:isBrowserRun() == 0 then 
        Log:write('showHighCharts：无法显示报表，浏览器未打开！')
        return
    end

    -- 构造请求地址
    local requestUrl = string.format('http://%s/webcloud/client/chart/hc_show.action?'..
        'title=%s&labels=%s&values=%s&seriesType=%s&showType=%s', 
        webcloud, title, labels, values, seriesType, showType)
    Log:write('showHighCharts: requestUrl', requestUrl)
    WebBrowser:openUrl(requestUrl)
end

-- 显示webView
function showWebView(x, y, w, h)
    local xscal = SCREEN_WIDTH / 480
    local yscal = SCREEN_HEIGHT / 800
    WebBrowser:create(x, y*yscal, w*xscal, h*yscal)
    WebBrowser:showWebBrowser(1)
end

-- 关闭报表显示
function closeWebView()
       WebBrowser:showWebBrowser(0)
       WebBrowser:release()
end

-- 显示系统网络设置
function showSysSetting()
    ShowNetSetting()
end

-- 经纬度格式转换
require 'math'
function XY_TO_GPSCOORDINATE(lon, lat)
    -- 转换为数值类型
    lon = tonumber(lon)
    lat = tonumber(lat)
    if lon == nil or lat == nil then 
       Log:write("经纬度参数非法!")
       return
    end
    
    -- 取纬度的整数和小数部分
    ladu = math.floor(lat)
    lafen = (lat - ladu) * 60
    lafenstr = string.gsub(string.format("%013.10f", lafen), "%.", "")
    lafenstr = string.sub(lafenstr, 1, 6)
    ladustr = string.format("%02d", ladu)
    
    -- 取经度的整数和小数部分
    longdu = math.floor(lon)
    longfen = (lon - longdu) * 60
    longfenstr = string.gsub(string.format("%013.10f", longfen), "%.", "")
    longfenstr = string.sub(longfenstr, 1, 6)
    longdustr = string.format("%03d", longdu)
    
    -- 拼接返回字符串
    result = ladustr..lafenstr..longdustr..longfenstr
    Log:write('XY_TO_GPSCOORDINATE result is -->',result)
    return result
end

--读取本机磁盘的物理地址。如果无SD卡，则使用手机存储，如果存储路径找不到（某些修改过的手机系统），则保存在应用内部
function getLocalDiskPath()
    local path = System:getFlashCardName(1)
    if isEmpty(path) then
        Log:write('SD卡不存在，尝试使用手机存储')
        path = System:getFlashCardName(0)
        if isEmpty(path) then
            Log:write('手机内部存储无法获取，使用应用路径')
            path = 'MODULE:\\com_wondertek_tx\\'
        end
    end
    path = path..'sqlitedownload'
    if IO:dirExist(path) == false then
        IO:dirCreate(path)
    end
    Log:write('最终获得的磁盘路径为',path)
    return path
end
--判断是否为空
function isEmpty(str)
    if str == nil or str == '' then
        return true
    else
        return false
    end
end

-- 将table写入指定的文件中
require 'com_wondertek_tx.common.json'
function writeTable2File(tbl, filePath)
    if tbl == nil or type(tbl) ~= "table" then 
        Log:write("writeTable2File:", "待写入的table非法!")
        return
    end
    if filePath == nil then 
        Log:write("writeTable2File:", "文件路径不能为空!")
        return
    end
    local tblStr = encode(tbl)
    -- 获取待写入文件的目录位置
    local _,_,dirPath =  string.find(filePath, '(.+)[/\\].+$')
    if dirPath ~= nil and IO:dirExist(dirPath) == false then 
        IO:dirCreate(dirPath)
    end
    -- 如果文件不存在，需要重新创建
    if IO:fileExist(filePath) == false then 
        IO:fileCreate(filePath) 
    end
    -- 写入TABLE字符串
    IO:fileWrite(filePath, tblStr)
end
-- 格式化手机号码
function formatPhoneNum(phonenum)
    local noCountryCode = string.gsub(phonenum, "+86", "")
    local result = string.gsub(noCountryCode, "[^%d]", "")
    Log:write("格式化的手机号码为:", result)
    return result
end

-- @hewu 检查用户的imei,imsi和手机号是否三码合一
require('framework.sqlite')
function checkDeviceInfo()
    -- 获取用户的imei和imsi号
    Log:write("开始进行三码合一检查...")
    local imeicode = System:getMachineInfo(4) 
    local imsicode = System:getMachineInfo(5) 
    if imeicode == nil or imeicode == '' then
        imeicode = '0'
    end
    if imsicode == nil or imsicode == '' then
        imsicode = '0'
    end
    -- 获取用户手机号
    local mobile = "0"
    local bRet, errMsg, retCountTable
    local databaseName = getLocalDiskPath()..'/jttxlDatabase'
    local sql = "select mobile,tb_c_system.mark4 from tb_c_employee, tb_c_system where tb_c_system.employee_id = tb_c_employee.employee_id"
    bRet, errMsg = Sqlite:open(databaseName) 
    Log:write("打开数据库返回:bRet = "..bRet.." errMsg ="..errMsg)
    bRet, retCountTable, errMsg = Sqlite:query(databaseName, sql)
    Log:write("获取登录用户数据返回:bRet = "..bRet.." errMsg ="..errMsg)
    if tonumber(bRet) ~= 0 then
        Log:write("系统表查询结果: retCountTable =", retCountTable) 
        if retCountTable[1] ~= nil and retCountTable[1][1] ~= nil then 
            mobile = retCountTable[1][1] 
            if retCountTable[1] ~= nil and (retCountTable[1][2] == nil or retCountTable[1][2] == '') then
                local registerCode = updateDeviceInfoToDb(mobile)
                retCountTable[1][2] = registerCode
            end
        end
        if retCountTable[1] ~= nil and retCountTable[1][2] ~= nil and retCountTable[1][2] ~= '' then
            local registerCode = mobile..imeicode..imsicode
            if registerCode ~= retCountTable[1][2] then
                Dialog:show("", "设备已禁用，请重新注册!", "ok", "reRegister")
                return
            end
        end
    end
    Log:write("登录用户手机号为: ", mobile)
    -- 进行校验请求
    local url = Alias.url_server..'mobile/device/validate?userCode='..Config:get('employeeId')
    url = url .. '&imsi='..imsicode..'&imei='..imeicode.."&phone="..mobile
    Http:request('checkDevice', url, 1000, {useCache=false})
end

-- @hewu 三码合一校验返回处理
function checkDeviceRespProc()
    local checkDevice = Http:jsonDecode('checkDevice')
    Log:write('checkDevice', checkDevice)
    if checkDevice.msg ~= "SUCCESS" then 
        Dialog:show("", "设备已禁用，请重新注册!", "ok", "reRegister")
    else
        Config:set('forceUpdateDatabase', "0")
        Log:write("三码合一校验成功！")
    end
end

-- @hewu 强制退出应用并重新注册
function reRegister()
    Log:write("校验失败，即将退出应用并重新注册.")
    Config:set('forceUpdateDatabase', "1")
    doExit()
end

--@zhouyu 
function updateDeviceInfoToDb(phone)
    --write imei,imsi info
    local imeicode = System:getMachineInfo(4) 
    local imsicode = System:getMachineInfo(5)
    if imeicode == nil or imeicode == '' then
        imeicode = '0'
    end
    if imsicode == nil or imsicode == '' then
        imsicode = '0'
    end
    local registerCode = phone..imeicode..imsicode
    --write to database
    local databaseName = getLocalDiskPath()..'/jttxlDatabase'
    local bRet, errMsg = Sqlite:open(databaseName) 
    local sql = "update tb_c_system set mark4='"..registerCode.."'"
    bRet, errMsg = Sqlite:update(databaseName, sql)
    Log:write('the sql is ',sql)
    Log:write('sql execute return value is ',bRet)
    Log:write('sql execute error value is ',errMsg)
    return registerCode
end
