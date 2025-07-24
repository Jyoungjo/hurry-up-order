-- stock-increase.lua
-- KEYS[1]: stock key
-- ARGV[1]: 증가할 수량

local stock = redis.call('GET', KEYS[1])
if not stock then
    redis.call('SET', KEYS[1], 0)
end
return redis.call('INCRBY', KEYS[1], tonumber(ARGV[1]))