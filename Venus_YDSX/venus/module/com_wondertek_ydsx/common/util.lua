-- -----------------------------------------------------------------------------
-- | WonderTek [ 网络无处不在，沟通及时到达 ]
-- -----------------------------------------------------------------------------
-- | Copyright (c) 2013, WonderTek, Inc. All Rights Reserved.
-- -----------------------------------------------------------------------------
-- | Author: hewu <hewu2008@gmail.com>
-- -----------------------------------------------------------------------------
-- | Desc: 常用功能函数
-- -----------------------------------------------------------------------------
require 'framework.sqlite'
require 'framework.appmanager'
require 'framework.webbrowser'

-- @brief 获取本地数据库目录
-- @author hewu 
function getLocalDiskPath()
    local path = System:getFlashCardName(1)
    if isEmpty(path) then
        Log:write('SD卡不存在，尝试使用手机存储')
        path = System:getFlashCardName(0)
        if isEmpty(path) then
            Log:write('手机内部存储无法获取，使用应用路径')
            path = 'MODULE:\\com_wondertek_ydsx\\'
        end
    end
    path = path..'database'
    if IO:dirExist(path) == false then
        IO:dirCreate(path)
    end
    Log:write('最终获得的磁盘路径为',path)
    return path
end

-- @brief 判断字符串是否为nil和空
-- @author hewu 
function isEmpty(str)
    if str == nil or str == '' then
        return true
    else
        return false
    end
end

-- @brief 由姓名查找联系人信息
-- @author hewu
function query_employee(db_path, emp_name)
	-- 打开数据库
	local bRet, errMsg = Sqlite:open(db_path) 
	Log:write("打开数据库：bRet = "..bRet.." errMsg ="..errMsg)
	
	-- 查询用户表
	local sql = string.format("select user_company, department_id, headship_name, mobile"..
		" from tb_c_employee where employee_name='%s';", emp_name)
	Log:write("查询用户表sql语句为: "..sql)
	local bRet, retCountTable, errMsg = Sqlite:query(db_path, sql)
    Log:write("查询用户表返回：", retCountTable)
    
    -- 获取部门全名称
    if retCountTable ~= nil and retCountTable[1] ~= nil then 
    	retCountTable[1][2] = query_full_department_name(db_path, retCountTable[1][2])
    end 

    -- 返回联系人信息
    Log:write("联系人信息为：", retCountTable)
    return retCountTable
end

-- @brief 由部门ID号查询部门信息
-- @author hewu 
function query_department(db_path, department_id)
	if department_id == nil or department_id == "0" then 
		return "" 
	end 
	local sql = string.format("select department_name, parent_department_id"..
		" from tb_c_department where department_id = '%s';", 
		department_id)
	Log:write("查询部门表语句: "..sql)
	local bRet, retCountTable, errMsg = Sqlite:query(db_path, sql)
	return retCountTable
end

-- @brief 由部门ID号查询部门全名称
-- @author hewu 
function query_full_department_name(db_path, department_id)
	if department_id == nil or department_id == "0" then 
		return "" 
	end 
	local full_department_name = ""
	while true do 
		local department = query_department(db_path, department_id) 
		if department == nil or department[1] == nil or 
			department[1][1] == nil or department[1][1] == "" then 
			return full_department_name
		else
			if full_department_name == "" then 
				full_department_name = department[1][1]
			else
				full_department_name = department[1][1].."-"..full_department_name
			end
			department_id = department[1][2]
		end
	end  
end


--搜索记录存入数据库
function addSearchLog(type,title,other_log,db_path)
	if other_log == nil then other = 'no' end
    local createSql = "CREATE TABLE IF NOT EXISTS tb_c_search_logs (id INTEGER PRIMARY KEY,search_key VARCHAR(80),type VARCHAR(2),search_count INTEGER,logtime datetime,mark1 varchar(200),mark2 varchar(200));"
    Log:write('createSql is ',createSql)
    local bRet, retCountTable, errMsg = Sqlite:query(db_path, createSql)
    Log:write('create table errMsg is ',errMsg)
    local sql = "SELECT id,search_key,search_count FROM tb_c_search_logs WHERE search_key='"..title.."' AND type='"..type.."';"
    bRet,retCountTable,errMsg = Sqlite:query(db_path,sql)
    if #retCountTable > 0 then
    	Log:write('更新')
    	sql = "UPDATE tb_c_search_logs SET search_count=search_count+1,mark1='"..other_log.."',logtime=datetime(CURRENT_TIMESTAMP,'localtime') WHERE id='"..retCountTable[1][1].."'"
    	bRet,retCountTable,errMsg = Sqlite:query(db_path,sql)
    else
    	Log:write('新增')
    	sql = string.format("INSERT INTO tb_c_search_logs (type,search_key,search_count,mark1,logtime) VALUES ('%s','%s',1,'%s',datetime(CURRENT_TIMESTAMP,'localtime'));",type,title,other_log)
    	bRet,retCountTable,errMsg = Sqlite:query(db_path,sql)
    end
    Log:write('set log errMsg is ',errMsg)
    if errMsg ~= nil and errMsg ~= '' then
    	sql = "DROP TABLE tb_c_search_logs;"
    	bRet,retCountTable,errMsg = Sqlite:query(db_path,sql)
    end
end

-- 设置对象隐藏或显示
function setAllShoworHide(sprite, isShow)
    Sprite:setVisible(sprite, isShow)
    Sprite:setActive(sprite, isShow)
    Sprite:setEnable(sprite, isShow)
end

-- @brief 应用详细信息
local g_app_infos = {
	{
		app_name = "集团通讯录", 
		app_id = "com.wondertek.jttxl",
		download_url = "http://120.209.138.173:8080/resources/wap" 
	},{
		app_name = "计步器", 
		app_id = "com.cmcc.pedometer",
		download_url = "http://120.209.131.146/download/pedometer.apk"
	},{
		app_name = "微信", 
		app_id = "com.tencent.mm",
		download_url = "http://u.androidgame-store.com/android/new/game1/13/107613/wx_1369542545090.apk?f=baidu_1"
	},{
		app_name = "飞信", 
		app_id = "cn.com.fetion",
		download_url = "http://cdn.market.hiapk.com/data/upload/2013/06_07/15/cn.com.fetion_152359.apk"
	},{
		app_name = "QQ", 
		app_id = "com.tencent.mobileqq",
		download_url = "http://gdown.baidu.com/data/wisegame/ef58dc6eed2ad12a/QQ.apk"
	},{
		app_name = "微博", 
		app_id = "com.sina.weibo",
		download_url = "http://gdown.baidu.com/data/wisegame/dca1df31413bac05/weibo.apk"
	}
}

-- @brief 语音搜索app，匹配返回app详细信息，否则返回nil 
-- @author hewu
function voice_query_app(voice_text)
	if voice_text == nil or voice_text == "" then 
		return  nil
	end
	local str_prefix = "打开"
	if string.starts(voice_text, str_prefix) == false then 
		Log:write(voice_text.."不是以“打开”开头")
		return nil
	else
		local app_name = string.sub(voice_text, string.len(str_prefix) + 1)
		Log:write("查询的应用名称为：", app_name)
		return query_app(app_name)
	end
end

-- @brief 检查是字符串是否有特定的起始符
-- @author hewu
function string.starts(String, Start)
   return string.sub(String, 1, string.len(Start)) == Start
end

-- @brief 语音调用app
-- @author hewu
function voice_invoke_app(app_name)
	local app_info  = query_app(app_name)
	if app_info ~= nil then 
		Log:write("应用详细信息为：", app_info)
		if AppManager:getInfoById(app_info.app_id) ~= "" then 
			run_app(app_info.app_id)
		else
			download_app(app_info.download_url)
		end
	else
		app_name = app_name or "" 
		Log:write("找不到应用"..app_name.."的详细信息！")
	end
end

-- @brief 由应用名称查询其详细信息
-- @author hewu
function query_app(app_name)
	if app_name == nil or app_name == "" then 
		return nil
	end
	local i = 1
	for i=1, #g_app_infos do 
		if g_app_infos[i].app_name == app_name then 
			return g_app_infos[i]
		end
	end
	return nil 
end

-- @brief 根据appId启动应用
-- @author hewu
function run_app(app_id)
	local app_info = AppManager:getInfoById(app_id)
	Log:write("app_info", app_info)
	if app_info ~= nil then 
		Log:write("启动应用"..app_id)
		AppManager:runApp(app_id) 
	else
		Log:write("调用应用程序失败，应用未安装！")
	end
end

-- @brief 下载应用
-- @author hewu
function download_app(url)
	if url ~= nil and url ~= "" then 
		Log:write("打开地址"..url)
		Util:openURL(url)
	else
		Log:write("url地址非法")
	end
end

-- @brief 语音命令看新闻
function voice_view_news(news_url)
	if news_url == nil or news_url == "" then 
		Log:write("news_url", news_url)
		return
	end
	Util:openURL(news_url)
end