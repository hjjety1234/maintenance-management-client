-- -----------------------------------------------------------------------------
-- | WonderTek [ 网络无处不在，沟通及时到达 ]
-- -----------------------------------------------------------------------------
-- | Copyright (c) 2012, WonderTek, Inc. All Rights Reserved.
-- -----------------------------------------------------------------------------
-- | Author: xxxx <xxxx@xxxx.com>
-- -----------------------------------------------------------------------------
-- | Desc: 常用功能函数
-- -----------------------------------------------------------------------------


function setAllShoworHide(sprite, isShow)
    Sprite:setVisible(sprite, isShow)
    Sprite:setActive(sprite, isShow)
    Sprite:setEnable(sprite, isShow)
end

function getCurDateAndTime()
    local year = os.date("*t")["year"]
    local month = os.date("*t")["month"]
    local day = os.date("*t")["day"]
    local hour = os.date("*t")["hour"]
    local minute = os.date("*t")["min"]
    local sec = os.date("*t")["sec"]
    return string.format('%04s%02s%02s%02s%02s%02s', year, month, day, hour, minute, sec)
end
function getCurDate()
    local year = os.date("*t")["year"]
    local month = os.date("*t")["month"]
    local day = os.date("*t")["day"]
    return string.format('%04s-%02s-%02s', year, month, day)
end

function SplitWithBlank(szFullString, szSeparator)
    local nFindStartIndex = 1
    local nSplitIndex = 1
    local nSplitArray = {}
    while true do
        local nFindLastIndex = string.find(szFullString, szSeparator, nFindStartIndex)
        if not nFindLastIndex then
            local stringData = string.sub(szFullString, nFindStartIndex, string.len(szFullString))
            
                nSplitArray[nSplitIndex] = stringData
            break
        end
        local stringData = string.sub(szFullString, nFindStartIndex, nFindLastIndex - 1)
            nSplitArray[nSplitIndex] = stringData
        nFindStartIndex = nFindLastIndex + string.len(szSeparator)
        nSplitIndex = nSplitIndex + 1
    end
    return nSplitArray
end

function Split(szFullString, szSeparator)
    local nFindStartIndex = 1
    local nSplitIndex = 1
    local nSplitArray = {}
    while true do
        local nFindLastIndex = string.find(szFullString, szSeparator, nFindStartIndex)
        if not nFindLastIndex then
            local stringData = string.sub(szFullString, nFindStartIndex, string.len(szFullString))
            
            if string.len(stringData) > 0 then
                nSplitArray[nSplitIndex] = stringData
            end
            break
        end
        local stringData = string.sub(szFullString, nFindStartIndex, nFindLastIndex - 1)
        if string.len(stringData) > 0 then
            nSplitArray[nSplitIndex] = stringData
        end
        nFindStartIndex = nFindLastIndex + string.len(szSeparator)
        nSplitIndex = nSplitIndex + 1
    end
    return nSplitArray
end