-- ARGV[1]: userId
-- ARGV[2:] = itemIds

local userId = ARGV[1]
local zsetKey = "reserved:ttl"
local result = {}

for i = 2, #ARGV do
    local itemId = ARGV[i]
    local reservedKey = "reserved:item:" .. itemId
    local zsetMember = itemId .. ":" .. userId

    local reservedQty = redis.call("HGET", reservedKey, userId)

    if reservedQty then
        redis.call("HDEL", reservedKey, userId)
        redis.call("ZREM", zsetKey, zsetMember)

        table.insert(result, itemId)
        table.insert(result, reservedQty)
    end
end

return result
