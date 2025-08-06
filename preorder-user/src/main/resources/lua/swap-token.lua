-- KEYS[1] = emailKey
-- KEYS[2] = refreshTokenKey
-- ARGV[1] = expirationSeconds

local emailKey       = KEYS[1]
local refreshKey     = KEYS[2]
local exp            = tonumber(ARGV[1])

-- 1) 이메일 키에 저장된 값(리프레시 토큰) 조회
local storedToken = redis.call("GET", emailKey)
-- 2) 값이 동일해야만
if storedToken and storedToken == refreshKey then
  -- a) 이메일 키 삭제
  redis.call("DEL", emailKey)
  -- b) 리프레시 토큰 키 → 이메일 값으로 세팅, 만료시간(exp) 적용
  redis.call("SET", refreshKey, emailKey, "EX", exp)
  return true
else
  return false
end
