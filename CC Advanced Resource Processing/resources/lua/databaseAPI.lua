-- version @arp.version@

-- dirty fix to allow true lua-style api files
local db = getfenv()

local function callNative(name, ...)
	local sides = redstone.getSides()

	if peripheral.getSides then
		sides = peripheral.getSides()
	end

	local side
	for _, v in ipairs(sides) do
		if peripheral.getType(v) == "database" or peripheral.getType(v) == "inventory" or peripheral.getType(v) == "workbench" or peripheral.getType(v) == "allocator" then
			side = v
			break
		end
	end

	if not side then
		error("No database attached!")
	end

	return peripheral.call(side, name, ...)
end

function db.isBlock(_item)
	return callNative("checkBlock", _item.name)
end
function db.isTool(_item)
	return callNative("checkTool", _item.name)
end

-- hold items in a weak cache, so they can be gced
local itemCache = setmetatable({}, {
	__mode = "v"
}) -- weak values

local stack_mt = {
	__eq = function(a,b)
		return a.item == b.item and a.damage == b.damage and a.count == b.count
	end,
	__lt = function(a,b)
		return a.item == b.item and a.damage == b.damage and a.count < b.count
	end,
	__le = function(a,b)
		return a.item == b.item and a.damage == b.damage and a.count <= b.count
	end,
	__tostring = function(t)
		local res = tostring(t.count) .. " of " .. tostring(t.item)

		if t.damage and t.item.damageable then
			local maxd = t.item.maxDamage
			if maxd == 0 then
				res = res .. " (100% damaged)"
			else
				res = res .. " (" .. tostring(t.damage / maxd) .. "% damaged)"
			end
		end

		return res
	end,
	__metatable = "stack"
}

local item_mt = {
	-- functions
	isTool = isTool,
	isBlock = isBlock,
	-- metafunctions
	__eq = function(a,b)
		return string.lower(a.name) == string.lower(b.name)
	end,
	__newindex = function()
		error("Can't change item properties")
	end,
	__tostring = function(t)
		return t.name
	end,
	__metatable = "item"
}
-- seperate indexer
item_mt.__index = function(t, prop)
	-- check for global functions etc.
	local result = item_mt[prop]
	if result then
		return result
	end

	-- check for cached values
	--[[result = rawget(t, prop)
	if result then
		return result
	end]]

	-- load property data
	result = callNative("getProperty", t.name, prop)
	rawset(t, prop, result)
	return result
end

function db.stack(item,count,dmg)
	if type(item) == "string" then
		item = db.getItem(item)
	end

	if not item then
		return nil
	end

	local st = { item = item, count = count or 1, damage = dmg }

	return setmetatable(st, stack_mt)
end

function db.getItem(_name)
	local item = itemCache[_name]
	if item then
		return item
	end

	if not callNative("checkItem", _name) then
		return nil
	end

	item = { name = _name }

	itemCache[_name] = item

	return setmetatable(item, item_mt)
end

function db.getRecipeResult(recipe, rtype)
	if not recipe then
		return nil
	end

	local rrtype = recipe.type or rtype

	if not rrtype then
		error("no valid recipe type specified")
	end

	local limit
	if recipe.width and recipe.height then
		limit = recipe.width*recipe.height
	else
		-- default limit.
		limit = 9
	end

	local processed = {}

	for i = 1,9 do
		if recipe[i] then
			processed[i] = recipe[i].name
		else
			processed[i] = "#EMPTY#"
		end
	end

	local item, count, dmg = callNative("getRecipeResult", rrtype, unpack(processed))

	return stack(item, count, dmg)
end

local function unserialize( s )
	local func, e = loadstring( s, "serialize" )
	if not func then
		return nil
	else
		setfenv( func, setmetatable({}, {__index = _G} ))
		return func()
	end
end

function db.getRecipesFor(item, rtype)
	if not item then
		return {}
	end

	local result = callNative("getRecipes", item.name, rtype)

	if not result then
		return {}
	end

	return unserialize(result)
end

function db.getSmeltingResult(input)
	local item, count, dmg = callNative("getRecipeResult", "furnace", input.name)

	return stack(item, count, dmg)
end

function db.getSmeltingSource(output)
	if not item then
		return {}
	end

	local result = callNative("getRecipes", item.name, "furnace")

	if not result then
		return {}
	end

	local table = unserialize(result)
	
	if not table or not table[1] then
		return nil
	end
	
	return table[1][1]
	--[[
	local item, count, dmg = callNative("getSmeltingSource", output.name)

	return stack(item, count, dmg)
	]]
end

return db
