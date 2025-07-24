-- stock-decrease.lua
-- KEYS[1]: stock key (e.g., stock:123)
-- ARGV[1]: 감소할 수량

local quantity = tonumber(ARGV[1])
local stock = tonumber(redis.call('GET', KEYS[1]))

if not stock then
    return -2 -- 캐시에 없음
end

if stock < quantity then
    return -1 -- 재고 부족
end

return redis.call('DECRBY', KEYS[1], quantity)