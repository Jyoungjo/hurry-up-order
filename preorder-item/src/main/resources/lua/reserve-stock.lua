local userId = ARGV[1]
local now = tonumber(ARGV[2])
local ttl = tonumber(ARGV[3])
local zsetKey = "reserved:ttl"

-- Validate ARGV length
if (#ARGV - 3) % 2 ~= 0 then
    return {0, "Invalid itemId-qty pair structure"}
end

local errorMessages = {}
local items = {}

-- Step 1: 가용 재고 확인
for i = 4, #ARGV, 2 do
    local itemId = ARGV[i]
    local qty = tonumber(ARGV[i+1])
    local stockKey = "stock:item:" .. itemId
    local reservedKey = "reserved:item:" .. itemId

    if itemId ~= nil and qty ~= nil then
        local stockRaw = redis.call("GET", stockKey)
        if stockRaw == false then
            table.insert(errorMessages, "Stock not initialized: " .. itemId)
        else
            local stock = tonumber(stockRaw)
            local userReserved = redis.call("HGETALL", reservedKey)
            local totalReserved = 0
            for j = 1, #userReserved, 2 do
                totalReserved = totalReserved + (tonumber(userReserved[j + 1]) or 0)
            end

            local available = stock - totalReserved
            if available < qty then
                return {0, "Insufficient stock for item: " .. itemId}
            end

            table.insert(items, {itemId = itemId, qty = qty})
        end
    else
        table.insert(errorMessages, "Invalid itemId or qty at index " .. i)
    end
end

-- 실패 메시지가 있으면 바로 리턴
if #errorMessages > 0 then
    return {0, unpack(errorMessages)}
end

-- Step 2: 예약 기록 + TTL 기록
local reservedItems = {}

for i = 1, #items do
    local itemId = items[i].itemId
    local qty = items[i].qty
    local reservedKey = "reserved:item:" .. itemId
    local member = itemId .. ":" .. userId
    local expireScore = now + ttl

    local currentQty = tonumber(redis.call("HGET", reservedKey, userId) or "0")
    redis.call("HSET", reservedKey, userId, currentQty + qty)
    redis.call("ZADD", zsetKey, expireScore, member)

    table.insert(reservedItems, itemId)
end

return {1, unpack(reservedItems)}
