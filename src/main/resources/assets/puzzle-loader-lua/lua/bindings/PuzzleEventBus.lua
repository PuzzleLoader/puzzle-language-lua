---
--- Created by Mr Zombii.
--- DateTime: 8/3/2024 10:44 PM
---

local PuzzleEventBus = {}

-- eventClass: String
-- func: LuaFunction/LuaClosure
function PuzzleEventBus:registerEvent(func, eventClass)
    LPuzzleEventBusUtil:registerEvent(func, eventClass)
end
return PuzzleEventBus
