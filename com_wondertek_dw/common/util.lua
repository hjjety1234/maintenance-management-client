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
        return '参数错误，请与管理员联系'
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
-- 在webview中显示统计报表
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

-- 关闭报表显示
function closeWebView()
       WebBrowser:showWebBrowser(0)
       WebBrowser:release()
end

-- 显示系统网络设置
function showSysSetting()
    ShowNetSetting()
end
