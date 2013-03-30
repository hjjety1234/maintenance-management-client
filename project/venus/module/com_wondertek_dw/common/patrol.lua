-- -----------------------------------------------------------------------------
-- | WonderTek [ 网络无处不在，沟通及时到达 ]
-- -----------------------------------------------------------------------------
-- | Copyright (c) 2013, WonderTek, Inc. All Rights Reserved.
-- -----------------------------------------------------------------------------
-- | Author: hewu <hewu2008@gmail.com>
-- -----------------------------------------------------------------------------
-- | Desc: 巡检暂存相关操作
-- -----------------------------------------------------------------------------
require 'framework.common'
require 'com_wondertek_dw.common.util'

Patrol = {}

-- 获取巡检暂存数据
function Patrol:getUserInput( planId, stationId )
	local filePath = "CACHE:\\Patrol\\"..planId.."-"..stationId..".json"
	local data = nil
	if IO:fileExist(filePath) == true then 
		local fileContent = IO:fileRead(filePath)
		data = Json:loadString2Table(fileContent) 
		if data == nil then 
			Log:write("读取巡检暂存失败，数据文件格式非法或为空!")
			return nil
		else
			Log:write("读取巡检暂存成功!")
		end
	else
		Log:write("读取巡检暂存失败，数据文件不存在!")
		return nil
	end

	Log:write("暂存的用户输入:", data)

    -- 格式化巡检大项，从下表1开始
    if data[0] ~= nil then 
	    table.insert(data, 1, data[0])
	    data[0] = nil
	end 
	-- 格式化巡检子项，从下标1开始
	for i=1,#data do 
		table.insert(data[i].subitems, 1, data[i].subitems[0])
		data[i].subitems[0] = nil
		-- 格式化巡检子项的照片列表
		for j=1, #data[i].subitems do
			if data[i].subitems[j].imageArray ~= nil 
				and data[i].subitems[j].imageArray[0] ~= nil then 
				table.insert(data[i].subitems[j].imageArray, 1, 
					data[i].subitems[j].imageArray[0])
				data[i].subitems[j].imageArray[0] = nil
			end
		end 
	end 
    return data
end

-- 保存暂存数据
function Patrol:save(planId, stationId, userinput)
	local filePath = "CACHE:\\Patrol\\"..planId.."-"..stationId..".json"
	writeTable2File(userinput, filePath)
	Log:write("保存巡检暂存成功!")
end

-- 删除暂存数据
function Patrol:delete(planId, stationId)
	local filePath = "CACHE:\\Patrol\\"..planId.."-"..stationId..".json"
	if IO:fileExist(filePath) == true then 
        IO:fileRemove(filePath) 
    end
end


----------------------与资源有关的暂存-------------------------------------
-- 获取资源相关巡检暂存数据
function Patrol:getResUserInput( planId, stationId, resourceId )
	local filePath = "CACHE:\\Patrol\\"..planId.."-"..stationId.."-"..resourceId..".json"
	local data = nil
	if IO:fileExist(filePath) == true then 
		local fileContent = IO:fileRead(filePath)
		data = Json:loadString2Table(fileContent) 
		if data == nil then 
			Log:write("读取巡检暂存失败，数据文件格式非法或为空!")
			return nil
		else
			Log:write("读取巡检暂存成功!")
		end
	else
		Log:write("读取巡检暂存失败，数据文件不存在!")
		return nil
	end

	Log:write("暂存的用户输入:", data)

    -- 格式化巡检大项，从下表1开始
    if data[0] ~= nil then 
	    table.insert(data, 1, data[0])
	    data[0] = nil
	end 
	-- 格式化巡检子项，从下标1开始
	for i=1,#data do 
		table.insert(data[i].subitems, 1, data[i].subitems[0])
		data[i].subitems[0] = nil
		-- 格式化巡检子项的照片列表
		for j=1, #data[i].subitems do
			if data[i].subitems[j].imageArray ~= nil 
				and data[i].subitems[j].imageArray[0] ~= nil then 
				table.insert(data[i].subitems[j].imageArray, 1, 
					data[i].subitems[j].imageArray[0])
				data[i].subitems[j].imageArray[0] = nil
			end
		end 
	end 
    return data
end

-- 保存资源相关的暂存数据
function Patrol:resSave( planId, stationId, resourceId, userinput )
	local filePath = "CACHE:\\Patrol\\"..planId.."-"..stationId.."-"..resourceId..".json"
	writeTable2File(userinput, filePath)
	Log:write("保存巡检暂存成功!")
end

-- 删除资源相关的暂存数据
function Patrol:resDelete( planId, stationId, resourceId )
	local filePath = "CACHE:\\Patrol\\"..planId.."-"..stationId.."-"..resourceId..".json"
	if IO:fileExist(filePath) == true then 
        IO:fileRemove(filePath) 
    end
end

-- 判断Table是否为空
function isTableEmpty (tbl)
	if tbl == nil then return true end 
    for _, _ in pairs(tbl) do
        return false
    end
    return true
end