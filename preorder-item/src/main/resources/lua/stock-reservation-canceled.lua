-- ARGV[1]: userId
-- ARGV[2:] = itemIds
local userId = ARGV[1]
local zsetKey = "reserved:ttl"

for i = 2, #ARGV do
    local itemId = ARGV[i]
    local reservedKey = "reserved:item:" .. itemId
    local zsetMember = itemId .. ":" .. userId

    -- 예약 해제
    redis.call("HDEL", reservedKey, userId)

    -- TTL ZSET에서도 제거
    redis.call("ZREM", zsetKey, zsetMember)
end

return {1}
