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

-- 定义巡检暂存文件路径
Patrol.filePath = "CACHE:\\Patrol\\patrol.json"

-- 暂存数据，从下表1开始
-- 注意序列化函数从下表0开始，需要进行格式化
Patrol.data = {}

-- 反序列化巡检暂存
function Patrol:deserialize()
	if IO:fileExist(Patrol.filePath) == true then 
		local fileContent = IO:fileRead(Patrol.filePath)
		Patrol.data = Json:loadString2Table(fileContent) 
		if Patrol.data == nil then 
			Log:write("反序列化巡检暂存失败，数据文件格式非法或为空!")
			Patrol.data = {}
			return false
		else
			Log:write("反序列化巡检暂存成功!")
			return true
		end
	else
		Log:write("反序列化巡检暂存失败，数据文件不存在!")
		return false
	end
end

-- 序列化巡检暂存
function Patrol:serialize()
	if Patrol.data == nil then 
		Log:write("序列化巡检暂存失败，无暂存数据!")
		return
	end
	writeTable2File(Patrol.data, Patrol.filePath)
	Log:write("序列化巡检暂存成功!")
end

-- 获取巡检暂存数据
function Patrol:getUserInput( planId, stationId )
    if Patrol.data == nil or Patrol.data[planId] == nil or 
        Patrol.data[planId][stationId] == nil then 
        return nil
    end
    -- 格式化暂存数据，从下表1开始
    if Patrol.data[planId][stationId][0] ~= nil then 
	    table.insert(Patrol.data[planId][stationId], 1, Patrol.data[planId][stationId][0])
	    Patrol.data[planId][stationId][0] = nil
	end 
	for i=1,#Patrol.data[planId][stationId] do 
		table.insert(Patrol.data[planId][stationId][i].subitems, 1, 
			Patrol.data[planId][stationId][i].subitems[0])
		Patrol.data[planId][stationId][i].subitems[0] = nil
	end 
	Log:write("获取暂存的用户输入:", Patrol.data)
    return Patrol.data[planId][stationId]
end

-- 保存暂存数据
function Patrol:save(planId, stationId, userinput)
	-- 保存巡检计划ID
	if Patrol.data[planId] == nil then 
		Patrol.data[planId] = {}
	end
	-- 保存站点ID
	if Patrol.data[planId][stationId] == nil then 
		Patrol.data[planId][stationId] = {}
	end
	-- 保存巡检结果ID
	Patrol.data[planId][stationId] = userinput
end

-- 删除暂存数据
function Patrol:delete(planId, stationId)
	-- 查找是否有指定的暂存数据,如果找到则删除之，否则直接返回
	if Patrol.data[planId] ~= nil and 
		Patrol.data[planId][stationId] ~= nil then 
		Patrol.data[planId][stationId] = nil
	else
		return
	end

	-- 检查是否有空的计划ID
	if  isTableEmpty(Patrol.data[planId]) == true then 
		Patrol.data[planId] = nil
	end
end


function isTableEmpty (tbl)
	if tbl == nil then return true end 
    for _, _ in pairs(tbl) do
        return false
    end
    return true
end