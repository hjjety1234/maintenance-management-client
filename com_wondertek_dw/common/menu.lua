-- -----------------------------------------------------------------------------
-- | WonderTek [ 网络无处不在，沟通及时到达 ]
-- -----------------------------------------------------------------------------
-- | Copyright (c) 2012, WonderTek, Inc. All Rights Reserved.
-- -----------------------------------------------------------------------------
-- | Author: xxxx <xxxx@xxxx.com>
-- -----------------------------------------------------------------------------
-- | Desc: 菜单等功能部分
-- -----------------------------------------------------------------------------

Menu = {}

-- @brief Menu 模板布局
Menu.layout = [[
<?xml version="1.0" encoding="utf-8"?>
<root>
    <header/>
    <body>
        <!-- 顶部状态栏 -->
        <node rect="0,0,480,41" extendstyle="1111">
            <battery-signal rect="0,0,480,41" image-battery="WONDER:\\framework\\image\\battery.png" image-signal="WONDER:\\framework\\image\\signal.png" image-degree="WONDER:\\framework\\image\\degree.png" number-degree="4" position-battery="310,10" position-signal="350,10" position-time="405,9" size-battery="16,21" size-signal="24,21" size-degree="21,21" extendstyle="1111" />
        </node>
        <!-- 底部菜单栏 -->
        <node rect="0,715,480,85" extendstyle="1111">
            <!-- 底部所有menu button -->
            <button name="menu1" rect="0,0,120,85" text="home" src="WONDER:\\framework\\image\\button.png" style="sudoku-auto" sudoku="4,4,4,4" color="#ffffff" OnSelect="menu1OnSelect" extendstyle="1111" />
            <button name="menu2" text="index" rect="120,0,120,85" src="WONDER:\\framework\\image\\button.png" style="sudoku-auto" sudoku="4,4,4,4" color="#ffffff" OnSelect="menu2OnSelect" extendstyle="1111" />
            <button name="menu3" text="help" rect="240,0,120,85" src="WONDER:\\framework\\image\\button.png" style="sudoku-auto" sudoku="4,4,4,4" color="#ffffff" OnSelect="menu3OnSelect" extendstyle="1111" />
            <button name="menu4" text="exit" rect="360,0,120,85" src="WONDER:\\framework\\image\\button.png" style="sudoku-auto" sudoku="4,4,4,4" color="#ffffff" OnSelect="closeBtnOnSelect" extendstyle="1111" />

            <!-- 用于标识选中项的图片 -->
            <image name="menuFocus" rect="" src="WONDER:\\framework\\image\\button_f.png" style="sudoku-auto" sudoku="4,4,4,4" extendstyle="1111" />
        </node>
    </body>
</root>
]]
-- @brief 加载菜单
function Menu:load(sprite, menuIndex)
    local spriteMenu = Sprite:findChild(sprite, 'spriteMenu')
    if spriteMenu == 0 then -- 未挂载menu
--        Log:write('menu load')
        spriteMenu = Sprite:create('node', sprite)
        Sprite:setProperty(spriteMenu, 'extendstyle', '1111')
        Sprite:setProperty(spriteMenu, 'name', 'spriteMenu')
        Sprite:loadFromString(spriteMenu, Menu.layout)
    end
    -- 设置选中焦点
    local menuFocusRect = {
        {0,0,120,85},
        {120,0,120,85},
        {240,0,120,85},
        {360,0,120,85},
    }
    if menuIndex then
        Sprite:setRect(Sprite:findChild(spriteMenu, 'menuFocus'), menuFocusRect[menuIndex][1], menuFocusRect[menuIndex][2],
         menuFocusRect[menuIndex][3], menuFocusRect[menuIndex][4])
    end
end

function menu1OnSelect(sprite)
    Scene:go(Alias.home, nil, nil, 1)
end

function menu2OnSelect(sprite)
    Scene:go(Alias.index, nil, nil, 2)
end
function menu3OnSelect(sprite)
    Scene:go(Alias.help, nil, nil, 3)
end

-- @brief 退出
function closeBtnOnSelect(sprite)
    Dialog:show('', '是否确定退出？', 'ok_cancel', 'doExit')
end

function doExit()
    Scene:exit()
end

