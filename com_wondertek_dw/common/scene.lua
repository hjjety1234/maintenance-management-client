-- -----------------------------------------------------------------------------
-- | WonderTek [ 网络无处不在，沟通及时到达 ]
-- -----------------------------------------------------------------------------
-- | Copyright (c) 2012, WonderTek, Inc. All Rights Reserved.
-- -----------------------------------------------------------------------------
-- | Author: xxxx <xxxx@xxxx.com>
-- -----------------------------------------------------------------------------
-- | Desc: 场景管理、操作
-- -----------------------------------------------------------------------------

--[[ ------------------------------------------------------------
 -- @function Scene:go(destSceneName, bFreeDestScene, bHiddeMenu, menuIndex)
 ------------------------------------------------------------
 -- @brief 场景跳转
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @param string destSceneName 要跳转到的场景名
 -- @param string bFreeDestScene 是否先释放目标场景（目标场景之前存在，要重新加载数据时）
 -- @param bool bHiddeMenu 是否隐藏menu
 -- @param bool menuIndex 菜单焦点项
 ------------------------------------------------------------
 -- @return nil
 ------------------------------------------------------------
 --]] 
Scene._go = Scene.go
function Scene:go(destSceneName, bFreeDestScene, bHiddeMenu, menuIndex)
    if destSceneName == Scene:getNameByHandle(Sprite:getCurScene()) then return end -- 不让场景自己跳自己
    Scene:_go(destSceneName, bFreeDestScene, 0)
    local destSceneHandle = Scene:getHandleByName(destSceneName)
    local sprite = Sprite:findChild(destSceneHandle, 'mainNode')
    if not bHiddeMenu then -- 加载menu
        --Menu:load(sprite, menuIndex)
    end
end

Scene._back = Scene.back
function Scene:back()
    Log:write('action in Scene:back free scene', Scene:getNameByHandle(Sprite:getCurScene()))
    Scene:freeByHandle(Sprite:getCurScene())
    Scene:_back()
end
