-- -----------------------------------------------------------------------------
-- | WonderTek [ 网络无处不在，沟通及时到达 ]
-- -----------------------------------------------------------------------------
-- | Copyright (c) 2012, WonderTek, Inc. All Rights Reserved.
-- -----------------------------------------------------------------------------
-- | Author: xxxx <xxxx@xxxx.com>
-- -----------------------------------------------------------------------------
-- | Desc: 对话框
-- -----------------------------------------------------------------------------

Dialog.layout = 
[[<?xml version="1.0" encoding="utf-8"?>
<root>
    <header></header>
    <body>
        <shadow name="shadow" />
        <node name="extendable"/>
        <image name="bgImg" style="autosize"/>
        <image name="iconImg" style="autosize"/>
        <label name="noticeLabel" />
        <textarea name="messageText" color="#1A4A6C" />
        <button name="fullscreenBtn"/>
        <button name="okBtn"/>
        <button name="cancelBtn"/>
    </body>
</root>
]]
-- @brief dialog属性
Dialog.propTable = {
    shadow = {rect='0,0,640,960',alpha='128',color='#000000', extendstyle='1111'},
    extendable = {visible='0',rect='', extendstyle='1111'},
    bgImg = {rect='100,350,444,291', src='file://image/dialog_bg.png', style='sudoku-auto', sudoku='4,4,4,4', extendstyle='1111'},
    iconImg = {rect='158,230,48,48', src='', extendstyle='1111'},
    noticeLabel = {rect='228,235,148,48', color='#ffffff', text='', extendstyle='1111'}, -- 对话框标题
    messageText = {rect='120,430,410,75', ['h-align']='center', color='#000000', -- 对话框内容
        text='textarea 文本描述', ['font-size']='27',frame='true', ['line-height']='28', top='0', extendstyle='1111'},
    okBtn = {rect='32,407,203,65', OnSelect='_dialogClose', text='确定', color='#FFFFFF',
            normal = 'src:file://image/dialog_btn_n.png;style:sudoku-auto;sudoku:4,4,4,4',
            sel = 'src:file://image/dialog_btn_f.png;style:sudoku-auto;sudoku:4,4,4,4', extendstyle='1111'},
    cancelBtn = {rect='245 ,407,203,65', OnSelect='_dialogClose', text='取消', color='#FFFFFF',
            normal = 'src:file://image/dialog_btn_n.png;style:sudoku-auto;sudoku:4,4,4,4',
            sel = 'src:file://image/dialog_btn_f.png;style:sudoku-auto;sudoku:4,4,4,4', extendstyle='1111'},
}

--[[
 ------------------------------------------------------------
 -- @function Dialog:show(title, message, typeStr, okCallback, cancelCallback)
 ------------------------------------------------------------
 -- @brief 显示对话框
 ------------------------------------------------------------
 -- @param string title 对话框标题
 -- @param string message 对话框内容
 -- @param string typeStr 对话框类型，'ok'、'cancle'、'ok_cancel'
 -- @param string okCallback 确定回调函数名，可空
 -- @param string cancelCallback 取消回调函数名，可空
 ------------------------------------------------------------
 -- @return void
 ------------------------------------------------------------
 --]] 
Dialog._show = Dialog.show
function Dialog:show(title, message, typeStr, okCallback, cancelCallback)
    Dialog.propTable.noticeLabel.text = title
    Dialog.propTable.messageText.text = message
    if okCallback then
        _okCallback = okCallback
        Dialog.propTable.okBtn.OnSelect = "_okProc" --okCallback
    else
        Dialog.propTable.okBtn.OnSelect = "_dialogClose"
    end
    if cancelCallback then
        _cancelCallback = cancelCallback
        Dialog.propTable.cancelBtn.OnSelect = "_cancelProc" --cancelCallback
    else
        Dialog.propTable.cancelBtn.OnSelect = "_dialogClose"
    end
    if typeStr == "cancel" or typeStr == "ok" then
        Dialog.propTable.okBtn.rect = "218,550,203,65"
        --Dialog.propTable.iconImg.src = "WONDER:\\framework\\image\\dialog_information.png"
    else
        Dialog.propTable.okBtn.rect = "110,550,203,65"
        Dialog.propTable.cancelBtn.rect = "330,550,203,65"
        --Dialog.propTable.iconImg.src = "WONDER:\\framework\\image\\dialog_question.png"
    end
    Dialog:_show(typeStr)
end

--[[
 ------------------------------------------------------------
 -- @function Dialog:isShow()
 ------------------------------------------------------------
 -- @brief Dialog框是否显示
 ------------------------------------------------------------
 -- @access public
 ------------------------------------------------------------
 -- @return boolean
 ------------------------------------------------------------
 --]]
function Dialog:isShow()
    local rootSprite = Sprite:getCurScene()
    local dialogSprite = Sprite:findChild(rootSprite, 'dialogNode')
    if dialogSprite ~=0 and Sprite:isVisible(dialogSprite) == 1 then
        return true
    end
    return false
end

function _okProc()
    Dialog:close()
    if type(loadstring('return '.._okCallback)()) == 'function' then
        loadstring(_okCallback..'()')()
    end
end

function _cancelProc()
    Dialog:close()
    if type(loadstring('return '.._cancelCallback)()) == 'function' then
        loadstring(_cancelCallback..'()')()
    end
end

-- @brief SearchDialog 模板布局
SearchDialog = {}
SearchDialog.layout = 
[[<?xml version="1.0" encoding="utf-8"?>
<root>
    <header></header>
    <body>
        <button rect="0,0,640,960" extendstyle="1111" OnSelect="dialogDoNothingFunc" />
        <image rect="0,0,640,960" extendstyle="1111" style="autosize" src="file://image/background_nofont.png" />
        <image rect="0,0,640,70" style="tile" src="file://image/top_bg.png" extendstyle="1111" />
        <button rect="0,0,130,70" extendstyle="1111" OnSelect="SearchDialogCloseBtnOnSelect" >
                <image rect="23,10,84,50" extendstyle="1111" style="autosize" src="file://image/goback_bg.png" />
                <label rect="33,10,79,50" extendstyle="1111" text="取消" h-align="center" v-align="center" color="#ffffff" font-size="24" />
        </button>
        <label name="totaltitle" rect="100,0,440,70" text="搜索" color="#a40c0c" v-align="center" h-align="center" font-size="24" font-style="bold" extendstyle="1111" />
        <image rect="40,80,440,62" extendstyle="1111" style="autosize" src="file://image/search_dialog_edit.png" />
        <label name="chatTips" rect="90,100,360,30" extendstyle="1111" text="请输入关键字" color="#777777" font-size="20"/>

        <node name="tipAreaNode" rect="90,700,440,40" visible="false" enable="false">
            <shadow rect="0,0,440,40" style="autosize" color="#0" extendstyle="1111" />
            <label name="tipAreaNodeLab" rect="0,0,440,40" extendstyle="1111" text="请输入关键字" v-align="center" h-align="center" color="#ffffff" font-size="20" border="false" />
        </node>
        <edit name="keywordsInput" rect="90,100,340,30" extendstyle="1111" text="" OnTextChanged="chatDetailOnTextChanged" OnKeyboardSizeChanged="chatOnKeyboardSizeChanged" color="#777777" font-size="20" />
        <button normal="normal" sel="sel" rect="500,90,106,53" extendstyle="1111" OnSelect="startSearchBtnOnSelect" >
            <image name="sel" rect="0,0,106,53" extendstyle="1111" style="autosize" src="file://image/search_dialog_btn_f.png" />
            <image name="normal" rect="0,0,106,53" extendstyle="1111" style="autosize" src="file://image/search_dialog_btn.png" />
            <label rect="0,0,106,53" extendstyle="1111" text="搜索" h-align="center" v-align="center" color="#ffffff" font-size="24" />
        </button>
        <button rect="420,90,70,54" extendstyle="1111" OnSelect="clearText">
        	<image rect="17,4,35,35" extendstyle="1111" style="autosize" src="file://image/search_dialog_close.png" />
        </button>
        <node name="searchKeywordArea" >
            <label name="historyLabel" rect="50,170,590,30" extendstyle="1111" text="历史搜索" h-align="left" v-align="center" color="#000000" font-size="24" />
            <list name="historyKeyWordList" rect="50,210,510,70" line="5" col="3" extendstyle="1111" />
            <label name="hotLabel" rect="50,400,590,30" extendstyle="1111" text="热门搜索" h-align="left" v-align="center" color="#000000" font-size="24" />
            <list name="hotKeyWordList" rect="50,440,510,70" line="3" col="3" extendstyle="1111" />
            <node name="keywordItem" rect="0,0,170,60" extendstyle="1111" visible="false" enable="false" >
                <button rect="0,0,170,60" normal="keywordText" sel="keywordTextF" extendstyle="1111" OnSelect="keywordBtnOnSelect" >
                    <label name="keywordText" rect="0,0,170,40" extendstyle="1111" text="关键字" color="#000000" postfix="..." font-size="22" v-align="center" />
                    <label name="keywordTextF" rect="0,0,170,40" extendstyle="1111" text="关键字" color="#a40c0c" postfix="..." font-size="22" v-align="center" />
                </button>
            </node>
        </node>
        <label name="nullTips" rect="0,450,640,50" extendstyle="1111" text="对不起，没有搜到您想要的节目！" color="#0" font-size="30" h-align="center" v-align="center" visible="0"/>
        <node name="searchResultArea" visible="false" enable="false" >
            <image name="subtitleImage" visible="0" rect="0,160,640,70" extendstyle="1111" style="tile" src="file://image/subtitle_bg.png" />
            <listview name="searchTitleList" rect="0,160,640,70" extendstyle="1111" limit="true" direction="horizontal" />
            <node name="subTitleItem" rect="0,0,160,70" extendstyle="1111" visible="false" enable="false" >
                <button rect="0,0,160,70" disabled="disabled" normal="subTitleNameN" extendstyle="1111" OnSelect="searchSubTitleBtnOnSelect" >
                    <node name="disabled" >
                        <image rect="11,10,136,49" extendstyle="1111" style="autosize" src="file://image/subtitle_focus.png" />
                        <label name="subTitleNameD" rect="11,10,136,49" h-align="center" v-align="center" color="#ffffff" font-size="18" />
                    </node>
                    <label name="subTitleNameN" rect="11,10,136,49" h-align="center" v-align="center" color="#777777" font-size="18" />
                </button>
            </node>
            <listview name="searchContentListview" rect="0,230,640,755" extendstyle="1111" OnTrail="searchContentListviewOnTail">
                <list-item name="searchresultItem" rect="0,0,640,755" extendstyle="1111">
                    <list name="searchContentList" rect="10,15,615,0" extendstyle="1111" auto-adjust="true" offset="0,15" />
                </list-item>
            </listview>
            <node name="searchContentItem" rect="0,0,205,275" extendstyle="1111" visible="false" enable="false" >
                <button name="searchresultBtn" rect="0,0,197,197" extendstyle="1111" OnSelect="searchresultBtnOnSelect">
                    <image rect="0,0,197,197" extendstyle="1100" style="autosize" src="file://image/tv_content_bg.png" />
                    <image name="searchList_bg" rect="7,7,182,182" extendstyle="1100" style="autosize" src="" dftsrc="file://image/home_recommend2.png" />
                </button>
                <label name="contentText" rect="0,200,197,40" extendstyle="1111" color="#000000" postfix="..."  font-size="20" v-align="center" h-align="left" />
                <label rect="0,240,50,30" text="评分:" font-size="18" extendstyle="1111" v-align="center" h-align="left"/>
                <node name="star_area" >
                     <node name="star_grey_area" rect="60,248,100,16" extendstyle="1111" >
                        <image rect="0,0,16,16" extendstyle="1111" style="autosize" src="file://image/tv_star_grey.png" />
                        <image rect="21,0,16,16" extendstyle="1111" style="autosize" src="file://image/tv_star_grey.png" />
                        <image rect="42,0,16,16" extendstyle="1111" style="autosize" src="file://image/tv_star_grey.png" />
                        <image rect="63,0,16,16" extendstyle="1111" style="autosize" src="file://image/tv_star_grey.png" />
                        <image rect="84,0,16,16" extendstyle="1111" style="autosize" src="file://image/tv_star_grey.png" />
                    </node>
                    <node name="star_yellow_area" rect="60,248,100,16" extendstyle="1111" frame="true" >
                        <image rect="0,0,16,16" extendstyle="1111" style="autosize" src="file://image/tv_star_yellow.png" />
                        <image rect="21,0,16,16" extendstyle="1111" style="autosize" src="file://image/tv_star_yellow.png" />
                        <image rect="42,0,16,16" extendstyle="1111" style="autosize" src="file://image/tv_star_yellow.png" />
                        <image rect="63,0,16,16" extendstyle="1111" style="autosize" src="file://image/tv_star_yellow.png" />
                        <image rect="84,0,16,16" extendstyle="1111" style="autosize" src="file://image/tv_star_yellow.png" />
                    </node>
                </node>
                <shadow rect="0,273,640,2" extendstyle="1114" visible="0" color="#000000"/>
            </node>
        </node>
    </body>
</root>
]]

SearchDialog.hotkeywordCount = 9
SearchDialog.subTitleTable = {"电影", "电视剧","综艺","动漫","原创"}
SearchDialog.subTitleIndex = 1
SearchDialog.contentTable = {{"最佳现场", "中国梦想秀", "越跳越美丽", "娱乐现场" ,"影视风云榜" ,"音乐风云榜", "我爱记歌词", "天天向上", "全民大精彩", "今夜有戏", "婚姻保卫战", "TOP MODEL", "裸婚时代", "雪豹突击队", "诸神之战", "蜘蛛侠", "火影忍者OVA", "蝙蝠侠", "美国队长", "绿巨人"}, {"美国队长", "绿巨人"}, {"最佳现场", "中国梦想秀"},{"越跳越美丽", "娱乐现场" ,"影视风云榜" ,"音乐风云榜", "我爱记歌词", "天天向上", "全民大精彩", "今夜有戏"}, {"婚姻保卫战", "TOP MODEL", "裸婚时代", "雪豹突击队"},{"诸神之战", "蜘蛛侠", "火影忍者OVA", "蝙蝠侠"}}
SearchDialog.preSubTitleBtn = 0
SearchDialog.curArea = 0  -- 0-当前显示为搜索关键字区域 1-当前显示为搜索结果区域
SearchDialog.preText = '' -- 上一次搜索的关键字
SearchDialog.line = 0
SearchDialog.isMore = 0
count = 0
function SearchDialogShow()
    local mainNode = Sprite:findChild(Sprite:getCurScene() , 'mainNode')
    local SearchDialogNode = Sprite:create('node', mainNode)
    Sprite:setRect(SearchDialogNode, 0, 0, 640, 960)
    Sprite:setProperty(SearchDialogNode, 'extendstyle', '1111')
    Sprite:setProperty(SearchDialogNode, 'name', 'SearchDialogNode')
    Sprite:loadFromString(SearchDialogNode, SearchDialog.layout)
    hotKeyWordList = Sprite:findChild(SearchDialogNode , 'hotKeyWordList')
    historyKeyWordList = Sprite:findChild(SearchDialogNode , 'historyKeyWordList')
    local historyLabel = Sprite:findChild(SearchDialogNode , 'historyLabel')
    local hotKeyWordList = Sprite:findChild(SearchDialogNode , 'hotKeyWordList')
    local hotLabel = Sprite:findChild(SearchDialogNode , 'hotLabel')
    keywordItem = Sprite:findChild(SearchDialogNode , 'keywordItem')
    searchKeywordArea = Sprite:findChild(SearchDialogNode , 'searchKeywordArea')
    searchResultArea = Sprite:findChild(SearchDialogNode , 'searchResultArea')
    searchTitleList = Sprite:findChild(SearchDialogNode , 'searchTitleList')
    subTitleItem = Sprite:findChild(SearchDialogNode , 'subTitleItem')
    searchContentListview = Sprite:findChild(SearchDialogNode , 'searchContentListview')
    searchContentList = Sprite:findChild(SearchDialogNode , 'searchContentList')
    searchContentItem = Sprite:findChild(SearchDialogNode , 'searchContentItem')
    List:removeAllItems(hotKeyWordList, 1, 1)
    List:removeAllItems(historyKeyWordList, 1, 1) 
    historyKeyWordsTable = loadHistoryKeyWordData()
    local historyKeywordCount = (historyKeyWordsTable and historyKeyWordsTable[1] and historyKeyWordsTable[1]~='') and math.min(15, #historyKeyWordsTable) or 0
    local hisLineCount = math.ceil(historyKeywordCount/3)
    Sprite:setProperty(historyKeyWordList, 'line', hisLineCount)
    if historyKeyWordsTable and historyKeyWordsTable[1] and historyKeyWordsTable[1]~='' then
        List:loadItem(historyKeyWordList, keywordItem, math.min(15, #historyKeyWordsTable), 'historyKeyWordOnLoad')
    end
    if historyKeywordCount == 0 then
        SetSpriteVisible(historyLabel, 0)
        Sprite:setRect(hotLabel,50,170,590,30)
        Sprite:setRect(hotKeyWordList,50,210,510,70)
    else
        SetSpriteVisible(historyLabel, 1)
        Sprite:setRect(hotLabel,50,400,590,30)
        Sprite:setRect(hotKeyWordList,50,440,510,70)
    end
    local x_his, y_his, w_his, h_his = Sprite:getRect(historyKeyWordList)
    Sprite:setRect(historyKeyWordList, x_his, y_his, w_his, hisLineCount*40)
    List:adjust(historyKeyWordList) 
    Sprite:setCapture(SearchDialogNode) 
    SetSpriteEnable(SearchDialogNode, 1)
    SetSpriteVisible(SearchDialogNode, 1)
    SearchDialog.curArea = 0
    getHotKeywordFromServer()
end

function SearchDialogCloseBtnOnSelect(sprite)
    local mainNode = Sprite:findChild(Sprite:getCurScene() , 'mainNode')
    local SearchDialogNode = Sprite:findChild(mainNode, 'SearchDialogNode')
    local historyLabel = Sprite:findChild(SearchDialogNode , 'historyLabel')
    local hotKeyWordList = Sprite:findChild(SearchDialogNode , 'hotKeyWordList')
    local hotLabel = Sprite:findChild(SearchDialogNode , 'hotLabel')
    if SearchDialog.curArea == 1 then
        count = 0
        List:removeAllItems(historyKeyWordList, 1, 1) 
        List:removeAllItems(searchContentList,1,1)
        ListView:removeAllItems(searchTitleList, 1, 1) 
        local nullTips = Sprite:findChild(Sprite:getCurScene(),'nullTips')
        Sprite:setVisible(nullTips,0)
        local subtitleImage = Sprite:findChild(Sprite:getCurScene(),'subtitleImage')
        Sprite:setVisible(subtitleImage,0)
        Sprite:setVisible(searchContentList,0)
        local listimg = Sprite:findChild(Sprite:getCurScene(),'listViewBg')
        Sprite:setVisible(listimg,0)
        
        historyKeyWordsTable = loadHistoryKeyWordData()
        local historyKeywordCount = (historyKeyWordsTable and historyKeyWordsTable[1] and historyKeyWordsTable[1]~='') and math.min(15, #historyKeyWordsTable) or 0
        local hisLineCount = math.ceil(historyKeywordCount/3)
        Sprite:setProperty(historyKeyWordList, 'line', hisLineCount)
        if historyKeyWordsTable and historyKeyWordsTable[1] and historyKeyWordsTable[1]~='' then
            List:loadItem(historyKeyWordList, keywordItem, math.min(15, #historyKeyWordsTable), 'historyKeyWordOnLoad')
        end
        if historyKeywordCount == 0 then
            SetSpriteVisible(historyLabel, 0)
            Sprite:setRect(hotLabel,50,170,590,30)
            Sprite:setRect(hotKeyWordList,50,210,510,70)
        else
            SetSpriteVisible(historyLabel, 1)
            Sprite:setRect(hotLabel,50,400,590,30)
            Sprite:setRect(hotKeyWordList,50,440,510,70)
        end
        local x_his, y_his, w_his, h_his = Sprite:getRect(historyKeyWordList)
        Sprite:setRect(historyKeyWordList, x_his, y_his, w_his, hisLineCount*40)
        List:adjust(historyKeyWordList) 
        setReaultAreaVisible(0)
        SearchDialog.curArea = 0
    else
        Sprite:releaseCapture(SearchDialogNode)
        Sprite:removeChild(mainNode, SearchDialogNode, 1)
    end 
end

function historyKeyWordOnLoad(list, item, index)
    Sprite:setProperty(item,"extendstyle","1111")
    Sprite:setRect(item, 0, 0, 170, 40)
    Sprite:setProperty(Sprite:findChild(item, 'keywordText'), 'text', historyKeyWordsTable[index + 1])
    Sprite:setProperty(Sprite:findChild(item, 'keywordTextF'), 'text', historyKeyWordsTable[index + 1])
    Sprite:setProperty(Sprite:findChildByClass(item, 'button'), 'data', historyKeyWordsTable[index + 1])
end

function hotKeyWordOnLoad(list, item, index)
    Sprite:setProperty(item,"extendstyle","1111")
    Sprite:setRect(item, 0, 0, 170, 40)
    Sprite:setProperty(Sprite:findChild(item, 'keywordText'), 'text', hotKeywordTable.keywords[index].keyword)
    Sprite:setProperty(Sprite:findChild(item, 'keywordTextF'), 'text', hotKeywordTable.keywords[index].keyword)
    Sprite:setProperty(Sprite:findChildByClass(item, 'button'), 'data', hotKeywordTable.keywords[index].keyword)
end

function createHotKeywordList()
    if hotKeywordTable and hotKeywordTable.keywords and hotKeywordTable.keywords[0] then
        local count = #hotKeywordTable.keywords + 1
        List:loadItem(hotKeyWordList, keywordItem, count, 'hotKeyWordOnLoad')
        local hotLineCount = math.ceil(count/3)
        Sprite:setProperty(hotKeyWordList, 'line', hotLineCount)
        local x_hot, y_hot, w_hot, h_hot = Sprite:getRect(hotKeyWordList)
        local x_bg, y_bg, w_bg, h_bg = Sprite:getRect(dialogBg)
        local x_his, y_his, w_his, h_his = Sprite:getRect(historyKeyWordList)
        local x_l, y_l, w_l, h_l = Sprite:getRect(hotLabel)
        Sprite:setRect(hotLabel, x_l, y_his + h_his+10, w_l, h_l)
        Sprite:setRect(hotKeyWordList, x_hot, y_his + h_his + h_l+20, w_hot, hotLineCount*40)
        Sprite:setVisible(hotLabel,1)
        local totalHeight = 100 + h_his + h_l + hotLineCount*40
        Sprite:setRect(dialogBg, x_bg, y_bg, w_bg, totalHeight > 396 and totalHeight + 10 or 396 )
        List:adjust(hotKeyWordList)
    end

end

function keywordBtnOnSelect(sprite)
    Sprite:killFocus(sprite)
    Sprite:releaseCapture(sprite)
    local SearchDialogNode = Sprite:findChild(Sprite:getCurScene(), 'SearchDialogNode')
    local keywordsInput = Sprite:findChild(SearchDialogNode, 'keywordsInput')
    local chatTips= Sprite:findChild(SearchDialogNode, 'chatTips')
    local keyWord = Sprite:getData(sprite)
    if keyWord then
        saveMyHistoryKeyWords(keyWord)
        Sprite:setProperty(chatTips, 'text', keyWord)
        SearchDialog.preText = keyWord 
        Sprite:setVisible(chatTips,0)
        --Sprite:setProperty(keywordsInput,'text',keyWord)
    end
    
    setReaultAreaVisible(1)
    SearchDialog.curArea = 1
    getResultFromServer(keyWord)
end

function startSearchBtnOnSelect(sprite)
    Sprite:killFocus(sprite)
    Sprite:releaseCapture(sprite)
    local SearchDialogNode = Sprite:findChild(Sprite:getCurScene(), 'SearchDialogNode')
    local keywordsInput = Sprite:findChild(SearchDialogNode, 'keywordsInput')
    local chatTips = Sprite:findChild(SearchDialogNode, 'chatTips')
    local tipAreaNode = Sprite:findChild(SearchDialogNode, 'tipAreaNode')
    local tipAreaNodeLab = Sprite:findChild(tipAreaNode, 'tipAreaNodeLab')
    local text = Sprite:getText(keywordsInput)
    local i = string.find(text,"\"")
    local j = string.find(text,"|")
    Log:write('444444444444444',text)
    if '' == text and SearchDialog.preText == '' then
        Log:write('------------------------------')
        Sprite:setProperty(tipAreaNodeLab, 'text', '请输入搜索关键词！')
        Sprite:setVisible(tipAreaNode, 1)
        Sprite:setEnable(tipAreaNode, 1)
        Timer:set(1, 1200, 'closeTipAreaNode')

        --Tips:show('请输入搜索关键词！')
        return
    elseif (i and i ~= 0) or j then
        Sprite:setProperty(tipAreaNodeLab, 'text', '存在非法字符，请重新输入')
        Sprite:setVisible(tipAreaNode, 1)
        Sprite:setEnable(tipAreaNode, 1)
        Timer:set(1, 1200, 'closeTipAreaNode')
        --Tips:show('存在非法字符，请重新输入')
        return
    end
    if text == '' then
        text = SearchDialog.preText
    end
    saveMyHistoryKeyWords(text)
    SearchDialog.preText = text
    Sprite:setProperty(chatTips,'text',text)
    setReaultAreaVisible(1)
    SearchDialog.curArea = 1
    getResultFromServer(text)
end
function closeTipAreaNode()
    -- body
    local SearchDialogNode = Sprite:findChild(Sprite:getCurScene(), 'SearchDialogNode')
    local tipAreaNode = Sprite:findChild(SearchDialogNode, 'tipAreaNode')
    Sprite:setVisible(tipAreaNode, 0)
    Sprite:setEnable(tipAreaNode, 0)
end


function loadHistoryKeyWordData()
    local reg = Reg:create(Reg.com_wondertek_cnlive2.search)
    local keywordsFilePath = 'USERDATA:\\historykeywords.xml'
    Reg:load(reg, keywordsFilePath)
    local keywords = Reg:getString(reg, 'keywords')
    keywords = string.gsub(keywords, '||', '|')
    if string.sub(keywords, 1, 1) == '|' then
        keywords = string.sub(keywords, 2, -1)
    end
    if string.sub(keywords, -1, -1) == '|' then
        keywords = string.sub(keywords, 1, -2)
    end
    Reg:setString(reg, 'keywords', keywords )
    Reg:save(reg, keywordsFilePath)
    Reg:release(Reg.com_wondertek_cnlive2.search)
    return Util:split(keywords, '|')
end

function saveMyHistoryKeyWords(KeyWord)
    local reg = Reg:create(Reg.com_wondertek_cnlive2.search)
    local keywordsFilePath = 'USERDATA:\\historykeywords.xml'
    Reg:load(reg, keywordsFilePath)
    local oldKeyWords = Reg:getString(reg, 'keywords')
    local oldTempTable = Util:split(oldKeyWords, '|')
    local isExistKeyWord = 0
    local existKeyWordIndex = 0
    local addFlag = 0
    for i= 1, #oldTempTable do
        if oldTempTable[i] == KeyWord then
            isExistKeyWord = 1
            existKeyWordIndex = i
            break
        end
    end
    if isExistKeyWord == 1 then
        for i = existKeyWordIndex, 2, -1 do
            oldTempTable[i] = oldTempTable[i-1]
        end
    else
        if #oldTempTable > 14 then
            for i = 15,1,-1 do
                oldTempTable[i] = oldTempTable[i-1]
            end
        else
            addFlag = 1
        end
    end
    local tempKeyWords = ''
    if addFlag == 0 then
        for i = 2, #oldTempTable do
            if i == 2 then
                tempKeyWords = oldTempTable[i]
            else
                tempKeyWords = tempKeyWords..'|'..oldTempTable[i]
            end
        end
    else
        tempKeyWords = oldKeyWords
    end
    Reg:setString(reg, 'keywords',  (oldKeyWords == '') and KeyWord or KeyWord..'|'..tempKeyWords)
    Reg:save(reg, keywordsFilePath)
    Reg:release(Reg.com_wondertek_cnlive2.search)
end

function setReaultAreaVisible(visibleFlag)
    SetSpriteEnable(searchKeywordArea, visibleFlag == 1 and 0 or 1)
    SetSpriteVisible(searchKeywordArea, visibleFlag == 1 and 0 or 1)
    SetSpriteEnable(searchResultArea, visibleFlag)
    SetSpriteVisible(searchResultArea, visibleFlag)
end

function createSearchResultList()
    ListView:removeAllItems(searchTitleList, 1, 1) 
    ListView:loadItem(searchTitleList, subTitleItem, #resultTable.categoryList+2, 'onLoadSearchSubTitleItem')
    ListView:adjust(searchTitleList)
    createSearchContentList()
end

function onLoadSearchSubTitleItem(list, item, index)
    Sprite:setProperty(item, "extendstyle", "1111")
    Sprite:setRect(item, 0, 0, 160, 70)
    if index == 0 then
    	Sprite:setProperty(Sprite:findChild(item, 'subTitleNameD'), "text", '全部('..resultTable.count..')')
        Sprite:setProperty(Sprite:findChild(item, 'subTitleNameN'), "text", '全部('..resultTable.count..')')
        Sprite:setEnable(Sprite:findChildByClass(item, 'button'), 0)
        SearchDialog.preSubTitleBtn = Sprite:findChildByClass(item, 'button')
        SearchDialog.subTitleIndex = 1   
    else
    	Sprite:setProperty(Sprite:findChild(item, 'subTitleNameD'), "text", SearchDialog.subTitleTable[tonumber(resultTable.categoryList[index-1].type)].."("..resultTable.categoryList[index-1].count..')')
    	Sprite:setProperty(Sprite:findChild(item, 'subTitleNameN'), "text", SearchDialog.subTitleTable[tonumber(resultTable.categoryList[index-1].type)].."("..resultTable.categoryList[index-1].count..')')
    	Sprite:setProperty(Sprite:findChildByClass(item, 'button'), "data", resultTable.categoryList[index-1].searchUrl)
    end 
end

function searchContentListviewOnTail(sprite)	
	if resultTable.isLastPage == 1 then
        Tips:show('已是最后一页！')
    else
        SearchDialog.isMore = 1
		local url = resultTable.moreSearchUrl
        Http:request('search_result', url, 1013 , {useCache=false, method='post', postData=Util:getRequestTail()})
        Tips:show('正在加载下一页！')
    end
end

function createSearchContentList()
    local col = 3
    if SearchDialog.isMore == 0 then
    	count = #resultTable.searchResult+1
    else
    	count = count+(#resultTable.searchResult+1)
    end    
    local line = math.ceil(count/col)
    Sprite:setProperty(searchContentList, "line", line)
    Sprite:setProperty(searchContentList, "col", col)
    local x_list, y_list, w_list, h_list = Sprite:getRect(searchContentList)
    local listHeight = line*290
    Sprite:setRect(searchContentList, x_list, y_list, w_list, listHeight)
    Sprite:setRect(Sprite:getParent(searchContentList) , x_list, y_list, w_list,listHeight + 20)
    ListView:adjust(searchContentListview)
    if SearchDialog.isMore == 0 then
    	List:removeAllItems(searchContentList, 1, 1)
    end
    List:loadItem(searchContentList, searchContentItem, #resultTable.searchResult+1, 'onLoadSearchContentItem')
    List:adjust(searchContentList)
end

function onLoadSearchContentItem(list, item, index)
    local cutLine = Sprite:findChildByClass(item, 'shadow')
    local col = 3
	local starArea = Sprite:findChild(item, 'star_area')
    local count = (#resultTable.searchResult+1)
    local line = math.ceil(count/col)
    --if index%col == 0 and col*(line-1) ~= index then
        --Sprite:setVisible(cutLine, 1)
    --else
        --Sprite:setVisible(cutLine, 0)
    --end
    if index <= (line - 1) * col - 1 then
        Sprite:setVisible(cutLine, 1)
    else
        Sprite:setVisible(cutLine, 0)
    end
    Sprite:setProperty(item, "extendstyle", "1111")
    Sprite:setRect(item, 0, 0, 205, 275)
    Sprite:setProperty(Sprite:findChild(item, 'contentText'), "text", resultTable.searchResult[index].contentName)
    Sprite:setProperty(Sprite:findChild(item, 'searchList_bg'), "src", resultTable.searchResult[index].imgUrl)
    Sprite:setProperty(Sprite:findChildByClass(item, 'button'), "data", resultTable.searchResult[index].contentUrl)
    Sprite:setVisible(starArea, 1)
    local starCount = resultTable.searchResult[index].score and tonumber(resultTable.searchResult[index].score) ~= nil and tonumber(resultTable.searchResult[index].score) or 0
    local yellowStarNode = Sprite:findChild(item, 'star_yellow_area')
    local x_s, y_s, w_s, h_s = Sprite:getRect(yellowStarNode)
    Sprite:setRect(yellowStarNode, x_s, y_s, 20*starCount, h_s)
end

function searchSubTitleBtnOnSelect(sprite)
    SearchDialog.isMore = 0
	count = 0
    SearchDialog.subTitleIndex = ListView:getItemIndex(Sprite:getParent(sprite)) + 1
    Sprite:killFocus(sprite)
    Sprite:releaseCapture(sprite)
    Sprite:setEnable(sprite, 0)
    Sprite:setEnable(SearchDialog.preSubTitleBtn, 1)
    SearchDialog.preSubTitleBtn = sprite
    if SearchDialog.subTitleIndex == 1 then
    	getResultFromServer(SearchDialog.preText)
    else
	    local url = Sprite:getData(sprite)
	    Http:request('search_result', url, 1013, {useCache=false, method='post', postData=Util:getRequestTail()})
    end
end

function getHotKeywordFromServer()
    local url = Util:getServer() .. 'pages/clt/v1/keywords.jsp'
    Http:request('hotkeyword', url, Msg.hotKeyword, {useCache=false})
end

function searchDialogOnPluginEvent(msg, param)
    if msg == Msg.hotKeyword then
        hotKeywordTable = Http:jsonDecode('hotkeyword')
        Log:write('hotKeywordTable', hotKeywordTable)
        if hotKeywordTable and hotKeywordTable.keywords and hotKeywordTable.keywords[0] then
            List:loadItem(hotKeyWordList, keywordItem, #hotKeywordTable.keywords + 1, 'hotKeyWordOnLoad')
        end
    elseif msg == Msg.searchResult then
        resultTable = Http:jsonDecode('search_result')
        local nullTips = Sprite:findChild(Sprite:getCurScene(),'nullTips')
        local listimg = Sprite:findChild(Sprite:getCurScene(),'listViewBg')
        local subtitleImage = Sprite:findChild(Sprite:getCurScene(),'subtitleImage')
        local keywordsInput = Sprite:findChild(Sprite:getCurScene(), 'keywordsInput')
        local chatTips= Sprite:findChild(Sprite:getCurScene(), 'chatTips')
        
        Sprite:setProperty(keywordsInput,'text','')
        Sprite:setVisible(chatTips,1)
        if resultTable and resultTable.count and tonumber(resultTable.count)> 0 then
            Sprite:setVisible(searchContentList,1)
        	Sprite:setVisible(nullTips,0)
        	Sprite:setVisible(listimg,1)
        	Sprite:setVisible(subtitleImage,1)
        	Sprite:setVisible(searchTitleList,1)
        	createSearchResultList()    	
        else
        	List:removeAllItems(searchContentList,1,1)
        	Sprite:setVisible(nullTips,1)
        	Sprite:setVisible(listimg,0)
        	Sprite:setVisible(subtitleImage,0)
        	Sprite:setVisible(searchTitleList,0)
        	Sprite:setVisible(searchContentList,0)
        end
    elseif msg == 1013 then
    	resultTable = Http:jsonDecode('search_result')
        local nullTips = Sprite:findChild(Sprite:getCurScene(),'nullTips')
        local listimg = Sprite:findChild(Sprite:getCurScene(),'listViewBg')
        local subtitleImage = Sprite:findChild(Sprite:getCurScene(),'subtitleImage')
        local keywordsInput = Sprite:findChild(Sprite:getCurScene(), 'keywordsInput')
        local chatTips= Sprite:findChild(Sprite:getCurScene(), 'chatTips')
        
        Sprite:setProperty(keywordsInput,'text','')
        Sprite:setVisible(chatTips,1)
        if resultTable and resultTable.count and tonumber(resultTable.count)> 0 then
            Sprite:setVisible(searchContentList,1)
        	Sprite:setVisible(nullTips,0)
        	Sprite:setVisible(listimg,1)
        	Sprite:setVisible(subtitleImage,1)
        	Sprite:setVisible(searchTitleList,1)
        	createSearchContentList()    	
        else
        	List:removeAllItems(searchContentList,1,1)
        	Sprite:setVisible(nullTips,1)
        	Sprite:setVisible(listimg,0)
        	Sprite:setVisible(subtitleImage,0)
        	Sprite:setVisible(searchTitleList,0)
        	Sprite:setVisible(searchContentList,0)
        end
    end
    List:adjust(hotKeyWordList)
end

function getResultFromServer(k)
    local url = Util:getServer() .. 'clt/search.msp'
    local postData = 'k=' .. k..Util:getRequestTail()
    Http:request('search_result', url, Msg.searchResult , {useCache=false, method='post', postData=postData})
end

function contentOnSelect(sprite)
    local item = Sprite:getParent(sprite)
    local contentName = Sprite:getText(Sprite:findChild(item,'contentText'))
    local contUrl = Sprite:getData(sprite)
    local reg = Reg:create(Reg.com_wondertek_cnlive2.detail)
    Reg:setString(reg,'contUrl',contUrl)
    Reg:setString(reg,'contentName',contentName)
    
    Scene:setReturn(Scene:getNameByHandle(), Alias.detail)
    Scene:go(Alias.detail)
end

function dialogDoNothingFunc()
    
end

--  点击按钮跳转到detail页
function searchresultBtnOnSelect(sprite)
	Sprite:killFocus(sprite)
	Sprite:releaseCapture(sprite)
	local item = Sprite:getParent(sprite)
    local contentName = Sprite:getText(Sprite:findChild(item,'contentText'))
    local contUrl = Sprite:getData(sprite)
	local reg = Reg:create(Reg.com_wondertek_cnlive2.detail)
	Reg:setString(reg,'contUrl',contUrl)
    Reg:setString(reg,'contentName',contentName)
	Scene:setReturn(Scene:getNameByHandle(),Alias.detail)
	Scene:go(Alias.detail)
end

-- @brief 编辑框内容变化
function chatDetailOnTextChanged(sprite)
    --Log:write('chatDetailOnTextChanged')
    SearchDialog.preText = ''
    local chatTips = Sprite:findChild(Sprite:getCurScene(), 'chatTips')
    if Sprite:getText(sprite)=='' then
        Sprite:setVisible(chatTips, 1)
    	  Sprite:setEnable(chatTips, 1)
    else
        Sprite:setVisible(chatTips, 0)
    	  Sprite:setEnable(chatTips, 0)
    end
end

-- @brief 键盘高度变化
function chatOnKeyboardSizeChanged(sprite, nHeight)
    shurufaHeight = nHeight
    Log:write('shurufaHeight==111=',shurufaHeight)
    local chatTips = Sprite:findChild(Sprite:getCurScene(), 'chatTips')
    -------------出现光标，文字消失---------------
    if nHeight ~= 0 then	
    	Sprite:setVisible(chatTips, 0)
    	Sprite:setEnable(chatTips, 0)
    end
end

function clearText(sprite)
    SearchDialog.preText = ''
    local SearchDialogNode = Sprite:findChild(Sprite:getCurScene(), 'SearchDialogNode')
    local keywordsInput = Sprite:findChild(SearchDialogNode, 'keywordsInput')
    local chatTips = Sprite:findChild(SearchDialogNode, 'chatTips')
    Sprite:setProperty(keywordsInput,'text','')
    Sprite:setProperty(chatTips,'text','请输入关键字')
    Sprite:setVisible(chatTips,1)
end

-- @brief 非wifi提示
notWifiTipsDialogLayout = 
[[<?xml version="1.0" encoding="utf-8"?>
<root>
    <header></header>
    <body>
        <shadow name="shadow" rect="0,0,640,960" extendstyle="1111" />
        <node name="tipsNode" rect="119,305,402,349" extendstyle="1111" >
            <image name="dialogBg" rect="0,0,0,0" extendstyle="1177" style="autosize" src="file://image/3Gor2Gbg.png" />
            <label rect="0,10,0,35" extendstyle="1171" v-align="center" h-align="center" text="温馨提示" font-size="25"/>
            <textarea rect="25,70,352,50" extendstyle="1111" v-align="center" line-height="22" h-align="center" text="您正在使用2G/3G网络播放，可能会产生流量费用。" font-size="17"/>
            <button rect="100,120,202,45" color="#ffffff" extendstyle="1111" OnSelect="continue1select" >
                <image rect="0,0,0,0" extendstyle="1177" style="sudoku-auto" sudoku="7,7,7,7" src="file://image/about_btn_n.png" />
                <label rect="0,0,0,0" extendstyle="1177" v-align="center" h-align="center" text="继续，不再提示我" font-size="20" color="#ffffff"/>
            </button>
            <button rect="100,185,202,45" color="#ffffff" extendstyle="1111" OnSelect="continue2select" >
                <image rect="0,0,0,0" extendstyle="1177" style="sudoku-auto" sudoku="7,7,7,7" src="file://image/about_btn_n.png"/>
                <label rect="0,0,0,0" extendstyle="1177" v-align="center" h-align="center" text="继续，下次询问我" font-size="20" color="#ffffff"/>
            </button>
            <button rect="100,245,202,45" color="#ffffff" extendstyle="1111" OnSelect="cancelselect" >
                <image rect="0,0,0,0" extendstyle="1177" style="sudoku-auto" sudoku="7,7,7,7" src="file://image/about_btn_n.png" />
                <label rect="0,0,0,0" extendstyle="1177" v-align="center" h-align="center" text="取消" font-size="20" color="#ffffff"/>
            </button>
        </node>
    </body>
</root>
]]

function notWifiTipsDialogShow()
    local mainNode = Sprite:findChild(Sprite:getCurScene() , 'mainNode')
    notWifiTipsDialog = Sprite:findChild(mainNode, 'returnHomeDialog')
    if notWifiTipsDialog == 0 then
        notWifiTipsDialog = Sprite:create('node', mainNode)
        Sprite:setRect(notWifiTipsDialog, 0, 0, 640, 960)
        Sprite:setProperty(notWifiTipsDialog, 'extendstyle', '1111')
        Sprite:setProperty(notWifiTipsDialog, 'name', 'notWifiTipsDialog')
        Sprite:loadFromString(notWifiTipsDialog, notWifiTipsDialogLayout)
    end
    Sprite:setCapture(notWifiTipsDialog) 
    SetSpriteEnable(notWifiTipsDialog, 1)
    SetSpriteVisible(notWifiTipsDialog, 1)
end

function cancelselect(sprite)
    SetSpriteEnable(notWifiTipsDialog, 0)
    SetSpriteVisible(notWifiTipsDialog, 0)
end

function continue1select(sprite)
    local wifiTips_reg = Reg:create("wifiTips")
    Reg:setString(wifiTips_reg,'continueTips','0')
    startPlayOnSelect()
end

function continue2select(sprite)
    startPlayOnSelect()
end

function startPlayOnSelect()   
    Scene:setReturn(Scene:getNameByHandle(), Alias.play)
    Scene:go(Alias.play)
end