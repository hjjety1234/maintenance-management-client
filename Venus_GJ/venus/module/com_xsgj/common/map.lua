-- -----------------------------------------------------------------------------
-- | WonderTek [ 网络无处不在，沟通及时到达 ]
-- -----------------------------------------------------------------------------
-- | Copyright (c) 2012, WonderTek, Inc. All Rights Reserved.
-- -----------------------------------------------------------------------------
-- | Author: yuhongwei <yuhongwei@mantis.com>
-- -----------------------------------------------------------------------------
-- @class Map
-- @note  地图相关操作
-- -----------------------------------------------------------------------------

Map = {}

Reg.g_Map = 'g_Map'

--[[
 -------------------------------------------------------------------------------
 -- @function Map:_getHandle()
 -------------------------------------------------------------------------------
 -- @brief 获取Map插件句柄
 -------------------------------------------------------------------------------
 -- @access private
 -------------------------------------------------------------------------------
 -- @param 无
 -------------------------------------------------------------------------------
 -- @return number
 -------------------------------------------------------------------------------
 --]]
function Map:_getHandle()
    local reg = registerCreate(Reg.g_Map)
    local handle = registerGetInteger(reg, 'MapPlugin')
    if handle == 0 then
        handle = pluginCreate('MapView', 'MapView')
        registerSetInteger(reg, 'MapPlugin', handle)
    end
    return handle
end

--[[
  ------------------------------------------------------------------------------
  --@function Map:open(nLeft, nTop, nWidth, nHeight)
  ------------------------------------------------------------------------------
  --@breif 创建地图显示窗口
  ------------------------------------------------------------------------------
  --@access public
  ------------------------------------------------------------------------------
  --@param nLeft: number, 地图窗口左上角X坐标
  --@param nTop: number, 地图窗口左上角Y坐标
  --@param nWidth: number, 地图窗口宽度(像素)
  --@param nHeight: number, 地图窗口高度(像素)
  ------------------------------------------------------------------------------
  --@return nil
  ------------------------------------------------------------------------------
  --]]
function Map:open(nLeft, nTop, nWidth, nHeight)
    pluginInvoke(self:_getHandle(), 'Open', nLeft, nTop, nWidth, nHeight)
end

--[[
  ------------------------------------------------------------------------------
  --@function Map:close()
  ------------------------------------------------------------------------------
  --@breif 关闭地图显示窗口，引擎仅做了隐藏处理，可以使用Open来展现出来
  ------------------------------------------------------------------------------
  --@access public
  ------------------------------------------------------------------------------
  --@param 无
  ------------------------------------------------------------------------------
  --@return nil
  ------------------------------------------------------------------------------
  --]]
function Map:close()
    pluginInvoke(self:_getHandle(), 'Close')
end

--[[
  ------------------------------------------------------------------------------
  --@function Map:getCurPosition(observer, msgId)
  --------------------------------------------------------------------------------
  --@breif 获取当前位置坐标
  ------------------------------------------------------------------------------
  --@access public
  ------------------------------------------------------------------------------
  --@param observer: number, 场景监听器
  --@param msgId: number, 插件消息号,业务自定
  ------------------------------------------------------------------------------
  --@sample 参考用法如下：
  --@sample local observer = Plugin:getObserver()
  --@sample Map:getCurPosition(observer, 1000)
  --@sample 插件消息返回数据
  --@sample function bodyOnPluginEvent(msg, param)
  --@sample     if msg == 1000 then
  --@sample         local postData = Param:getString(param, 0)
  --@sample         -- postData为Json数据({"latitude":"31250614","longitude":"121600975"})
  --@sample     end
  --@sample end
  ------------------------------------------------------------------------------
  --@return nil
  ------------------------------------------------------------------------------
  --]]
function Map:getCurPosition(observer, msgId)
    pluginInvoke(self:_getHandle(), 'getCurrentPosition', observer, msgId)
end

--[[
  ------------------------------------------------------------------------------
  --@function Map:getLocation(observer, msgId, latitude, longitude)
  --------------------------------------------------------------------------------
  --@breif 获取地名
  ------------------------------------------------------------------------------
  --@access public
  ------------------------------------------------------------------------------
  --@param observer: number, 场景监听器
  --@param msgId: number, 插件消息号,业务自定
  --@param latitude: number, 经度
  --@param longitude: number, 纬度
  ------------------------------------------------------------------------------
  --@sample 参考用法如下：
  --@sample local observer = Plugin:getObserver()
  --@sample Map:getCurPosition(observer, 1001, latitude, longitude)
  --@sample 插件事件回调函数会返回数据
  --@sample function bodyOnPluginEvent(msg, param)
  --@sample     if msg == 1001 then
  --@sample         local postData = Param:getString(param, 0)
  --@sample         -- postData为Json数据({"address":"北京市西城区后红井胡同2号","city":"北京"})
  --@sample     end
  --@sample end
  ------------------------------------------------------------------------------
  --@return nil
  ------------------------------------------------------------------------------
  --]]
function Map:getLocation(observer, msgId, latitude, longitude)
    pluginInvoke(self:_getHandle(), 'reverse', observer, msgId, latitude, longitude)
end

--[[
  ------------------------------------------------------------------------------
  --@function Map:searchMapByKeys(observer, msgId, strKeys, latitude, longitude, radius)
  --------------------------------------------------------------------------------
  --@breif 在指定坐标半径内搜索地图关键词
  ------------------------------------------------------------------------------
  --@access public
  ------------------------------------------------------------------------------
  --@param observer: number, 场景监听器
  --@param msgId: number, 插件消息号,业务自定
  --@param strKeys: string, 搜索关键词，可多个, 以','(英文逗号)分隔
  --@param latitude: number, 经度
  --@param longitude: number, 纬度
  --@param radius: number, 搜索半径(单位:米)
  ------------------------------------------------------------------------------
  --@sample 参考用法如下：
  --@sample local observer = Plugin:getObserver()
  --@sample Map:getCurPosition(observer, 1002, '公园,商场', latitude, longitude, 1000)
  --@sample 插件事件回调函数会返回数据
  --@sample function bodyOnPluginEvent(msg, param)
  --@sample     if msg == 1002 then
  --@sample         local postData = Param:getString(param, 0)
  --@sample         -- postData为Json数据:data0为搜索"公园"关键词返回的接口，data1为搜索"商场"关键词返回的接口
  --@sample        {
  --@sample         "data0":
  --@sample          [
  --@sample            {"latitude":"39899559","longitude":"116390193","name":"后孙公园胡同","address":"北京市西城区","phoneNum":""},
  --@sample            {"latitude":"39898688","longitude":"116390219","name":"前孙公园东夹道","address":"北京市西城区","phoneNum":""},
  --@sample	         ],
  --@sample         "data1":
  --@sample          [
  --@sample            {"latitude":"39916613","longitude":"116413531","name":"时代音乐广场","address":"北京市","phoneNum":""},
  --@sample	           {"latitude":"39908111","longitude":"116415390","name":"首都月圆KTV","address":"崇文区前门东大街3号首都大酒店B座B1楼公交/驾车","phoneNum":"(010)65268748, (010)58159988-5218"},
  --@sample            {"latitude":"39898556","longitude":"116413135","name":"天王星时尚娱乐会所","address":"珠市口东大街12-12号","phoneNum":"(010)67078788"}
  --@sample	         ]
  --@sample        }
  --@sample end
  ------------------------------------------------------------------------------
  --@return nil
  ------------------------------------------------------------------------------
  --]]
function Map:searchMapByKeys(observer, msgId, strKeys, latitude, longitude, radius)
    pluginInvoke(self:_getHandle(), 'search', observer, msgId, strKeys, latitude, longitude, radius)
end

--[[
  ------------------------------------------------------------------------------
  --@function Map:searchMapByName(city, name)
  ------------------------------------------------------------------------------
  --@breif 以地点名字搜索地图
  ------------------------------------------------------------------------------
  --@access public
  ------------------------------------------------------------------------------
  --@param city: string, 搜索城市
  --@param name: string, 搜索地名
  ------------------------------------------------------------------------------
  --@sample 参考用法如下：
  --@sample Map:searchMapByName('上海', '公安局')
  --@sample 执行后会在地图列出所有上海的公安局
  --------------------------------------------------------------------------------
  --@return nil
  ------------------------------------------------------------------------------
  --]]
function Map:searchMapByName(city, name)
    pluginInvoke(self:_getHandle(), 'poiSearchInCity', city, name)
end

--[[
  ------------------------------------------------------------------------------
  --@function Map:searchRoute(routeType, startCityname, startAddress, startlatitude, startlongitude, endCityname, endAddress, endlatitude, endlongitude)
  --------------------------------------------------------------------------------
  --@breif 搜索路线(打开地图后执行,成功后会在地图上显示路线)
  ------------------------------------------------------------------------------
  --@access public
  ------------------------------------------------------------------------------
  --@param routeType: number, 路线方式：1.自驾。2.公车。3.步行
  --@param startCityname: string, 起点城市名(如:'上海')
  --@param startAddress: string, 起点地址（地址尽量清晰：如写“合肥左岸国际电影院”，不如写成“望江路与潜山路交口大唐国际购物中心”） 
  --@param startlatitude: number, 起点经度
  --@param startlongitude: number, 起点纬度
  --@param endCityname: string, 终点城市名
  --@param endAddress: string, 终点地址
  --@param endlatitude: number, 终点经度
  --@param endlongitude: number, 终点纬度
  --@param 提示：
  --@param 当设置起点/终点地址时，起点/终点的坐标可以为0（此时必须设置起点/终点城市名），反之，当设置起点/终点的坐标时
  --@param （城市名与地址名被忽略），起点/终点地址和起点/终点城市名可以为""（空字符串）
  ------------------------------------------------------------------------------
  --@sample 参考用法如下：
  --@sample Map:searchRoute(1,"合肥","市公安局",31874414,117288759,"合肥","左岸国际电影院",31838239,117239271) //城市名与地址名被忽略
  --@sample Map:searchRoute(1,"合肥","市公安局",0,0," ","",31838239,117239271) //支持，速度一般
  --@sample Map:searchRoute(1,"","",31874414,117288759," 合肥","左岸国际电影院",0,0) //支持，速度一般
  --@sample Map:searchRoute(1,"合肥","市公安局",0,0," 合肥","左岸国际电影院",0,0) //支持，速度慢
  --@sample Map:searchRoute(1,"","",31874414,117288759,"","",31838239,117239271) //支持,建议使用，速度快
  ------------------------------------------------------------------------------
  --@return nil
  ------------------------------------------------------------------------------
  --]]

function Map:searchRoute(routeType, startCityname, startAddress, startlatitude, startlongitude, endCityname, endAddress, endlatitude, endlongitude)
    pluginInvoke (self:_getHandle(), "searchRoute", routeType, startCityname, startAddress, startlatitude, startlongitude, endCityname, endAddress, endlatitude, endlongitude)
end

--[[
  ------------------------------------------------------------------------------
  --@function Map:moveTo(latitude, longitude, name, bRemoveAllOverley)
  ------------------------------------------------------------------------------
  --@breif 将某点移到地图中央并显示关键字符串
  ------------------------------------------------------------------------------
  --@access public
  ------------------------------------------------------------------------------
  --@param latitude: number, 某点经度
  --@param longitude: number, 某点纬度
  --@param name: string, 地图目标坐标点显示文字内容
  --@param bRemoveAllOverley: number, 1表示移除其它设置覆盖物，0表示不移除其它覆盖物,缺省为1
  --------------------------------------------------------------------------------
  --@return nil
  ------------------------------------------------------------------------------
  --]]
function Map:moveTo(latitude, longitude, name, bRemoveAllOverley)
    pluginInvoke(self:_getHandle(), 'showMap', latitude, longitude, name, bRemoveAllOverley or 1)
end


