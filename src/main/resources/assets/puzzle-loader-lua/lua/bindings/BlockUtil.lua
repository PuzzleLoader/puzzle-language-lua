---
--- Created by Mr Zombii:
--- DateTime: 7/31/2024 7:33 PM
---

local BlockUtil = {}

-- id: String,
-- returns Block
function BlockUtil:getBlock(id)
    return LBlockUtil:getBlock(id)
end

-- id: String,
-- returns BlockState
function BlockUtil:getBlockState(id)
    return LBlockUtil:getBlockState(id)
end

-- zone: Zone,
-- block: BlockState,
-- x: Integer,
-- y: Integer,
-- z: Integer
function BlockUtil:setBlockState(zone, block, x, y, z)
    LBlockUtil:setBlockState(zone, block, x, y, z)
end

-- name: String
-- returns DataModBlock
function BlockUtil:newBlockFromName(name) -- uses block at /assets/blocks/{name}.json
    return LBlockUtil:newBlockFromName(name)
end

-- name: String
-- json: String
-- returns DataModBlock
function BlockUtil:newBlockFromJson(name, json)
    return LBlockUtil:newBlockFromJson(name, json)
end

-- name: String
-- location: ResourceLocation
-- returns DataModBlock
function BlockUtil:newBlockFromLocation(name, location)
    return LBlockUtil:newBlockFromLocation(name, location)
end

-- namespace: String
-- path: String
-- returns ResourceLocation
function BlockUtil:makeLocation(namespace, path)
    return LBlockUtil:makeLocation(namespace, path)
end

-- namespace: String
-- name: String
-- returns Identifier
function BlockUtil:makeId(namespace, name)
    return LBlockUtil:makeId(namespace, name)
end

return BlockUtil