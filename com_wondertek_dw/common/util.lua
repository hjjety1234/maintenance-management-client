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

-- 将table写入指定的文件中
require 'com_wondertek_dw.common.json'
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
-- 对table进行排序
-- @param tbl 待排序的table
-- @param index 下标或列名
-- @param bDesc true 降序, false 升序
function sortTable(tbl, index, bDesc)
    local sortFunc = nil
    local bExists = false
    if (tbl[0] ~= nil) then 
        table.insert(tbl, 1, tbl[0])
        tbl[0] = nil
        bExists =  true
    end
    if bDesc ==  true then 
        sortFunc = function(a, b)  return a[index] > b[index] end
    else
        sortFunc = function(a, b)  return a[index] < b[index] end
    end
    table.sort(tbl, sortFunc)

    if bExists == true then 
        local result = {}
        for i, v in ipairs(tbl) do 
            table.insert(result, i-1, v)
        end
        return result
    else
        return tbl
    end
end