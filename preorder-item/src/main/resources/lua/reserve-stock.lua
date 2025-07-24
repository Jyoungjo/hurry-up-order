-- optimized_reserve.lua
local call    = redis.call
local insert  = table.insert
-- Lua 5.1 환경에서 unpack을 가져오는 안전한 선언
local unpack = table.unpack or unpack

local userId  = ARGV[1]
local now     = tonumber(ARGV[2])
local ttl     = tonumber(ARGV[3])
local zsetKey = "reserved:ttl"

-- 1) ARGV 구조 유효성 검사
if ( (#ARGV - 3) % 2 ~= 0 ) then
    return {0, "Invalid itemId-qty pair structure"}
end

local errors = {}
local items  = {}

-- 2) 가용 재고 검사
for i = 4, #ARGV, 2 do
    local itemId = ARGV[i]
    local qty    = tonumber(ARGV[i+1])
    local stockKey    = "stock:item:" .. itemId
    local reservedKey = "reserved:item:" .. itemId

    if not itemId or not qty then
        insert(errors, "Invalid itemId or qty at index " .. i)
    else
        local stockRaw = call("GET", stockKey)
        if not stockRaw then
            insert(errors, "Stock not initialized: " .. itemId)
        else
            local stock = tonumber(stockRaw)
            -- HVALS로 값만 가져와 합산
            local vals = call("HVALS", reservedKey)
            local totalReserved = 0
            for _, v in ipairs(vals) do
                totalReserved = totalReserved + tonumber(v)
            end

            if stock - totalReserved < qty then
                return {0, "Insufficient stock for item: " .. itemId}
            end
            insert(items, {id = itemId, qty = qty})
        end
    end
end

-- 3) 에러가 있으면 즉시 리턴
if #errors > 0 then
    return {0, unpack(errors)}
end

-- 4) 실제 예약 처리
local expireScore = now + ttl
local zaddArgs = { zsetKey }

for _, it in ipairs(items) do
    local reservedKey = "reserved:item:" .. it.id
    -- HINCRBY로 원자적 증감
    call("HINCRBY", reservedKey, userId, it.qty)
    -- ZADD 인자 모아두기
    local member = it.id .. ":" .. userId
    insert(zaddArgs, expireScore)
    insert(zaddArgs, member)
end

-- 한 번에 ZADD 호출
call("ZADD", unpack(zaddArgs))

-- 5) 성공 리턴 (예약된 itemId 리스트)
local out = {1}
for _, it in ipairs(items) do
    insert(out, it.id)
end
return out
