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

-- 暂存数据
Patrol.data = {}

-- 反序列化巡检暂存
function Patrol:deserialize()
	if IO:fileExist(Patrol.filePath) == true then 
		Patrol.data = Json:loadFile2Table(Patrol.filePath) 
		if Patrol.data == nil then 
			Patrol.data = {}
			return false
		else
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
end

-- 获取巡检暂存数据
function Patrol:getUserInput( planId, stationId, resultId )
	if Patrol.data == nil or Patrol.data[planId] == nil or 
		Patrol.data[planId][stationId] == nil  or
		Patrol.data[planId][stationId][resultId] == nil then 
		return nil
	end
	return Patrol.data[planId][stationId][resultId]
end


-- 保存暂存数据
function Patrol:save(planId, stationId, resultId, userinput)
	-- 保存巡检计划ID
	if Patrol.data[planId] == nil then 
		Patrol.data[planId] = {}
	end
	-- 保存站点ID
	if Patrol.data[planId][stationId] == nil then 
		Patrol.data[planId][stationId] = {}
	end
	-- 保存巡检结果ID
	Patrol.data[planId][stationId][resultId] = userinput
end

-- 删除暂存数据
function Patrol:delete(planId, stationId, resultId)
	-- 查找是否有指定的暂存数据,如果找到则删除之，否则直接返回
	if Patrol.data[planId] ~= nil and 
		Patrol.data[planId][stationId] ~= nil  and
		Patrol.data[planId][stationId][resultId] ~= nil then 
		Patrol.data[planId][stationId][resultId] = nil
	else
		return
	end

	-- 检查是否有空的站点ID
	if Patrol.data[planId] ~= nil and 
		isTableEmpty(Patrol.data[planId][stationId]) == true then 
		Patrol.data[planId][stationId] = nil
	end

	-- 检查是否有空的计划ID
	if  isTableEmpty(Patrol.data[planId]) == true then 
		Patrol.data[planId] = nil
	end
end


function isTableEmpty (tbl)
	if tbl == nil return true end 
    for _, _ in pairs(tbl) do
        return false
    end
    return true
end