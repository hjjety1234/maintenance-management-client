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
				full_department_name = full_department_name.."-"..department[1][1]
			end
			department_id = department[1][2]
		end
	end  
end

-- 设置对象隐藏或显示
function setAllShoworHide(sprite, isShow)
    Sprite:setVisible(sprite, isShow)
    Sprite:setActive(sprite, isShow)
    Sprite:setEnable(sprite, isShow)
end