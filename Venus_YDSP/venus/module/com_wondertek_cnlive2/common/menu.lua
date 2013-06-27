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
        <!-- 顶部菜单栏 -->
        <node rect="0,0,640,70" extendstyle="1110">
            <!-- 顶部所有menu button -->
            <image rect="0,0,640,70" style="tile" src="file://image/top_bg.png" extendstyle="1111" />
            <button name="topReturnButton" sel="sel" normal="normal" visible="0" enable="0" rect="0,0,130,70" extendstyle="1111" OnSelect="returnOnSelect" >
                <image name="normal" rect="23,10,84,50" extendstyle="1111" style="autosize" src="file://image/goback_bg.png" />
                <image name="sel" rect="23,10,84,50" extendstyle="1111" style="autosize" src="file://image/goback_bg_f.png" />
                <label rect="33,10,79,50" extendstyle="1111" text="返回" h-align="center" v-align="center" color="#ffffff" font-size="24" />
            </button>
            <button name="cancelButton" sel="sel" normal="normal" visible="0" enable="0" rect="0,0,130,70" extendstyle="1111" OnSelect="cancelButtonOnSelect" >
                <image name="normal" rect="23,10,84,50" extendstyle="1111" style="autosize" src="file://image/goback_bg.png" />
                <image name="sel" rect="23,10,84,50" extendstyle="1111" style="autosize" src="file://image/goback_bg_f.png" />
                <label rect="33,10,79,50" extendstyle="1111" text="取消" h-align="center" v-align="center" color="#ffffff" font-size="24" />
            </button>
            <label name="totaltitle" rect="100,0,440,70" text="" color="#a40c0c" v-align="center" h-align="center" font-size="30" font-style="bold" extendstyle="1111" />
            <image name="logoImage" visible="0" rect="10,20,194,29" extendstyle="1100" style="autosize" src="file://image/logo.png" />
            <button name="searchButton" visible="0" enable="0" rect="460,0,80,70" extendstyle="1111" OnSelect="searchBtnOnSelect" >
                <image rect="20,15,35,35" extendstyle="1100" style="autosize" src="file://image/search_icon.png" />
            </button>
            <button name="personCenterButton" visible="0" enable="0" rect="560,0,80,70" extendstyle="1111" OnSelect="personalPanelBtnOnSelect" >
                <image rect="20,15,35,35" extendstyle="1100" style="autosize" src="file://image/setting_icon.png" />
            </button>
            <button name="editButton" visible="0" enable="0" rect="520,8,106,53" extendstyle="1111" OnSelect="editButtonOnSelect" >
                <image rect="0,0,106,53" extendstyle="1111" style="autosize" src="file://image/top_editbtn_bg.png" />
                <label  rect="0,0,106,53" text="编辑" color="#ffffff" v-align="center" h-align="center" font-size="24" extendstyle="1111" />
            </button>
            <button name="finishButton" visible="0" enable="0" rect="520,8,106,53" extendstyle="1111" OnSelect="finishButtonOnSelect" >
                <image rect="0,0,106,53" extendstyle="1111" style="autosize" src="file://image/top_editbtn_bg.png" />
                <label  rect="0,0,106,53" text="完成" color="#ffffff" v-align="center" h-align="center" font-size="24" extendstyle="1111" />
            </button>
        </node>
        <!-- 底部菜单栏 -->
        <node name="bottomMenu" rect="0,891,640,69" extendstyle="0510">
            <!-- 底部所有menu button -->
            <image rect="0,0,640,69" style="tile" src="file://image/bottom_bg.png" extendstyle="1010" />
            <list rect="20,0,600,0" extendstyle="1017" auto-adjust="true" line="1" col="8">
                <list-item rect="0,0,120,0" extendstyle="0017">
                    <button text="$(txt)" rect="0,0,0,0" font-size="24"
                    		normal="src:file://image/blank.png;color:#0"
                        disabled="src:file://image/bottom_focus.png;color:#ffffff"
                        sel="src:file://image/bottom_focus.png;color:#ffffff"
                        style="sudoku-auto" sudoku="4,4,4,4" OnSelect="_menuItemOnSelect" extendstyle="0077" />
                </list-item>
                <dataset>
                    <set txt="首页"/>
                    <set txt="直播"/>
                    <set txt="频道"/>
                    <set txt="排行榜"/>
                    <set txt="我的电视"/>
                </dataset>
            </list>
        </node>
    </body>
</root>
]]

Menu.TYPE = {
    home = 1,
    live = 2,
    channel = 3,
    ranking = 4,
    mytvstations = 5,
}

-- @brief 加载菜单
function Menu:load(sprite, menuType)
    local mainNode = Sprite:findChild(sprite, 'mainNode')
    local spriteMenu = Sprite:findChild(mainNode, 'spriteMenu')
    if spriteMenu == 0 then -- 未挂载menu
        spriteMenu = Sprite:create('node', mainNode)
        Sprite:setRect(spriteMenu, 0, 0, 640, 960)
        Sprite:setProperty(spriteMenu, 'extendstyle', '1111')
        Sprite:setProperty(spriteMenu, 'name', 'spriteMenu')
        Sprite:loadFromString(spriteMenu, Menu.layout)
    end
    if menuType and menuType >= 1 and menuType <= 5 then
        local list = Sprite:findChildByClass(spriteMenu, 'list')
        local count = List:getItemCount(list)
        for i=0,count-1 do
           Sprite:setEnable(Sprite:findChildByClass(List:getItem(list, i), 'button'), 1)
        end
        Sprite:setEnable(Sprite:findChildByClass(List:getItem(list, menuType - 1), 'button'), 0)
    end
end

function Menu:isShow(sprite)
    local spriteMenu = Sprite:findChild(sprite, 'spriteMenu')
    if spriteMenu ~= 0 and Sprite:isVisible(spriteMenu) == 1 then
        return true
    else
        return false
    end
end

function _menuItemOnSelect(sprite)
    local item = Sprite:getParent(sprite)
    local index = List:getItemIndex(item) + 1
    if index == Menu.TYPE.home then
        Scene:go(Alias.home,true)
    elseif index == Menu.TYPE.live then
        Scene:go(Alias.live)
    elseif index == Menu.TYPE.channel then
        Scene:go(Alias.channel,true)
    elseif index == Menu.TYPE.ranking then
        Scene:go(Alias.ranking)
    elseif index == Menu.TYPE.mytvstations then
        Scene:go(Alias.mytvstations,true)
    end
end

function setTopMenuStatus(nodeNameTable, bEnable,titleText)
    local spriteMenu = Sprite:findChild(Sprite:getCurScene(), 'spriteMenu')
    local title = Sprite:findChild(spriteMenu, 'totaltitle')
    for i = 1, #nodeNameTable do
        local tempNode = Sprite:findChild(spriteMenu, nodeNameTable[i])
        Sprite:setEnable(tempNode, bEnable)
        Sprite:setVisible(tempNode, bEnable)
    end
    if titleText then
        Sprite:setProperty(title, 'text', titleText)
    end
end

-- @brief 点击标题栏“搜索”按钮公用方法
function searchBtnOnSelect(sprite)
    Sprite:killFocus(sprite)
    Sprite:releaseCapture(sprite)
    SearchDialogShow()
end

function personalPanelBtnOnSelect(sprite)
    Sprite:killFocus(sprite)
    Sprite:releaseCapture(sprite)
    local reg = Reg:create(Reg.com_wondertek_cnlive2.personalcenter)
    Reg:setNumber(reg,'myAccountflag','0')
    Scene:setReturn(Alias.home, Alias.personalcenter)
    Scene:go(Alias.personalcenter)
end

-- @brief 返回
function returnOnSelect(sprite)
    Sprite:killFocus(sprite)
    Sprite:releaseCapture(sprite)
    Scene:back()
end

