-- -----------------------------------------------------------------------------
-- | WonderTek [ 网络无处不在，沟通及时到达 ]
-- -----------------------------------------------------------------------------
-- | Copyright (c) 2012, WonderTek, Inc. All Rights Reserved.
-- -----------------------------------------------------------------------------
-- | Author: xxxx <xxxx@xxxx.com>
-- -----------------------------------------------------------------------------
-- | Desc: 常用功能函数
-- -----------------------------------------------------------------------------


function setAllShoworHide(sprite, isShow)
    Sprite:setVisible(sprite, isShow)
    Sprite:setActive(sprite, isShow)
    Sprite:setEnable(sprite, isShow)
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

function getCurDateAndTime1()
    local year = os.date("*t")["year"]
    local month = os.date("*t")["month"]
    local day = os.date("*t")["day"]
    local hour = os.date("*t")["hour"]
    local minute = os.date("*t")["min"]
    local sec = os.date("*t")["sec"]
    return string.format('%04s-%02s-%02s %02s:%02s:%02s', year, month, day, hour, minute, sec)
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

function replaceStr(str,orign,target)
    local i=string.find(str,orign,1);
    if i==nil then
        return str
    else
        return string.sub(str,1,i)..target..string.sub(str,i+string.len(orign),string.len(str))
    end
end

function utfstrlen(str)
	local len = #str;
	local left = len;
	local cnt = 0;
	local arr={0,0xc0,0xe0,0xf0,0xf8,0xfc};
	while left ~= 0 do
	local tmp=string.byte(str,-left);
	local i=#arr;
	while arr[i] do
	if tmp>=arr[i] then left=left-i;break;end
	i=i-1;
	end
	cnt=cnt+1;
	end
	return cnt;
end

require 'com_xsgj.common.webbrowser'

-- 显示webView
function showWebView(x, y, w, h)
    local xscal = SCREEN_WIDTH / 480
    local yscal = SCREEN_HEIGHT / 800
    Log:write('the width is ',x*xscal)
    Log:write('the height is ',h*yscal)
    WebBrowser:create(x, y*yscal, w*xscal, h*yscal)
    WebBrowser:showWebBrowser(1)
end

--根据分辨率显示WebBrowser
-- 显示webView
function showWebViewByUrl(url,x, y, w, h)
    local xscal = SCREEN_WIDTH / 480
    local yscal = SCREEN_HEIGHT / 800
    Log:write('the width is ',x*xscal)
    Log:write('the height is ',h*yscal)
    WebBrowser:create(x, y*yscal, w*xscal, h*yscal)
    WebBrowser:showWebBrowser(1)
    WebBrowser:openUrl(url)
end

-- 关闭报表显示
-- 关闭报表显示
function closeWebView()
    Log:write('closeWebViewcloseWebViewcloseWebView')
       WebBrowser:showWebBrowser(0)
       WebBrowser:release()
end

function showHighCharts(title, labels, values, seriesType, showType)
    -- 打印日志
    local param = string.format('showHighCharts(title=%s, labels=%s, values=%s, seriesType = %s, showType = %s)', 
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
        --   Log:write('showHighCharts：无法显示报表，浏览器未打开！')
        return
    end

    -- 构造请求地址
    local requestUrl = string.format('http://%s/webcloud/client/chart/hc_show.action?'..
        'title=%s&labels=%s&values=%s&seriesType=%s&showType=%s', 
        webcloud, title, labels, values, seriesType, showType)
    Log:write('showHighCharts: requestUrl', requestUrl)
    WebBrowser:openUrl(requestUrl)
end

--table2String
--@by zhouyu
function sz_T2S(_t)  
    local szRet = "{"  
    function doT2S(_i, _v)  
        if "number" == type(_i) then  
            szRet = szRet .. "[" .. _i .. "] = "  
            if "number" == type(_v) then  
                szRet = szRet .. _v .. ","  
            elseif "string" == type(_v) then  
                szRet = szRet .. '"' .. _v .. '"' .. ","  
            elseif "table" == type(_v) then  
                szRet = szRet .. sz_T2S(_v) .. ","  
            else  
                szRet = szRet .. "nil,"  
            end  
        elseif "string" == type(_i) then  
            szRet = szRet .. '["' .. _i .. '"] = '  
            if "number" == type(_v) then  
                szRet = szRet .. _v .. ","  
            elseif "string" == type(_v) then  
                szRet = szRet .. '"' .. _v .. '"' .. ","  
            elseif "table" == type(_v) then  
                szRet = szRet .. sz_T2S(_v) .. ","  
            else  
                szRet = szRet .. "nil,"  
            end  
        end  
    end  
    table.foreach(_t, doT2S)  
    szRet = szRet .. "}"  
    return szRet  
end

--字符串转table(反序列化,异常数据直接返回nil)  
function t_S2T(_szText)  
   --栈  
   function stack_newStack()  
       local first = 1  
       local last = 0  
       local stack = {}  
       local m_public = {}  
       function m_public.pushBack(_tempObj)  
           last = last + 1  
           stack[last] = _tempObj  
       end  
       function m_public.temp_getBack()  
           if m_public.bool_isEmpty() then  
               return nil  
           else  
               local val = stack[last]  
               return val  
           end  
       end  
       function m_public.popBack()  
           stack[last] = nil  
           last = last - 1  
       end  
       function m_public.bool_isEmpty()  
           if first > last then  
               first = 1  
               last = 0  
               return true  
           else  
               return false  
           end  
       end  
       function m_public.clear()  
           while false == m_public.bool_isEmpty() do  
               stack.popFront()  
           end  
       end  
       return m_public  
   end  
   function getVal(_szVal)  
       local s, e = string.find(_szVal,'"',1,string.len(_szVal))  
       if nil ~= s and nil ~= e then  
           --return _szVal  
            return string.sub(_szVal,2,string.len(_szVal)-1)  
        else  
            return tonumber(_szVal)  
        end  
    end  
  
    local m_szText = _szText  
    local charTemp = string.sub(m_szText,1,1)  
    if "{" == charTemp then  
        m_szText = string.sub(m_szText,2,string.len(m_szText))  
    end  
    function doS2T()  
        local tRet = {}  
        local tTemp = nil  
        local stackOperator = stack_newStack()  
        local stackItem = stack_newStack()  
        local val = ""  
        while true do  
            local dLen = string.len(m_szText)  
            if dLen <= 0 then  
                break  
            end  
  
            charTemp = string.sub(m_szText,1,1)  
            if "[" == charTemp or "=" == charTemp then  
                stackOperator.pushBack(charTemp)  
                m_szText = string.sub(m_szText,2,dLen)  
            elseif '"' == charTemp then  
                local s, e = string.find(m_szText, '"', 2, dLen)  
                if nil ~= s and nil ~= e then  
                    val = val .. string.sub(m_szText,1,s)  
                    m_szText = string.sub(m_szText,s+1,dLen)  
                else  
                    return nil  
                end  
            elseif "]" == charTemp then  
                if "[" == stackOperator.temp_getBack() then  
                    stackOperator.popBack()  
                    stackItem.pushBack(val)  
                    val = ""  
                    m_szText = string.sub(m_szText,2,dLen)  
                else  
                    return nil  
                end  
            elseif "," == charTemp then  
                if "=" == stackOperator.temp_getBack() then  
                    stackOperator.popBack()  
                    local Item = stackItem.temp_getBack()  
                    Item = getVal(Item)  
                    stackItem.popBack()  
                    if nil ~= tTemp then  
                        tRet[Item] = tTemp  
                        tTemp = nil  
                    else  
                        tRet[Item] = getVal(val)  
                    end  
                    val = ""  
                    m_szText = string.sub(m_szText,2,dLen)  
                else  
                    return nil  
                end  
            elseif "{" == charTemp then  
                m_szText = string.sub(m_szText,2,string.len(m_szText))  
                local t = doS2T()  
                if nil ~= t then  
                    szText = sz_T2S(t)  
                    tTemp = t  
                    --val = val .. szText  
                else  
                    return nil  
                end  
            elseif "}" == charTemp then  
                m_szText = string.sub(m_szText,2,string.len(m_szText))  
                return tRet  
            elseif " " ~= charTemp then  
                val = val .. charTemp  
                m_szText = string.sub(m_szText,2,dLen)  
            else  
                m_szText = string.sub(m_szText,2,dLen)  
            end  
        end  
        return tRet  
    end  
    local t = doS2T()  
    return t  
end

-----------------------------获取sdcard的路径----------------------
--获取SD卡上的Download目录
function getDownloadTxtPath()
        local txtPath = ''
        local downloadPath = System:getFlashCardName(1) 
        Log:write('内部存储卡地址为：'..downloadPath)
        if downloadPath == nil or downloadPath == "" then 
        Log:write("SD卡不存在,使用内部存储！")
        downloadPath = System:getFlashCardName(0)
        Log:write('内部存储卡地址为：',downloadPath)
        --在SD卡不存在的话，如果是htc_t327t_android_A001，则使用缓存
        if(System:getUserAgent() == 'htc_t327t_android_A001') then
            downloadPath = "MODULE:\\com_xsgj\\"
        end
    end
         txtPath = downloadPath.."download"
        Log:write("getDownloadTxtPath: localDir="..txtPath)
        -- 如果路径不存在，创建下载目录
        if  IO:dirExist(txtPath) == false then 
            IO:dirCreate(txtPath)
        end
          return txtPath
end
    --------------------------------保存注册信息--------------------
function savebtn_onselected(configString)
    --保存至xml中
    local path = getDownloadTxtPath() .. '/config.json'
    if IO:fileExist(path) == false then
        IO:fileCreate(path)
    end
    IO:fileWrite(path,configString)
end
function readDownloadTxtConfig()
    local configTable =''
    local path = getDownloadTxtPath() .. '/config.json'
       Log:write("readDownloadTxtConfig: localDir=",path)
     if IO:fileExist(path) then
         Log:write("fileExist: fileExist="..path)
         configTable = IO:fileReadToTable(path)
     else
          IO:fileCreate(path) --创建一个文件
     end
    return  configTable
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
    local reg = Reg:create(Reg.com_wondertek_samples.wlan)
    Reg:setString(reg, 'curSSID', ssid)
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
    local reg = Reg:create(Reg.com_wondertek_samples.wlan)
    return Reg:getString(reg, 'curSSID')
end
