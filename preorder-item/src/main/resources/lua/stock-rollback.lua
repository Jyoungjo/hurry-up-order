-- ARGV: itemId1, qty1, itemId2, qty2, ...
for i = 1, #ARGV, 2 do
    local itemId = ARGV[i]
    local qty = tonumber(ARGV[i + 1])
    redis.call("INCRBY", "stock:item:" .. itemId, qty)
end
return true
