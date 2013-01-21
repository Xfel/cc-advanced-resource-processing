
local function listIterSkip(_inv, _idx)
	local nextobj
	repeat
		_idx = _idx + 1

		if _idx > _inv.size then
			-- break the loop
			return nil,nil
		end

		nextobj = _inv[_idx]
	until nextobj

	return _idx, nextobj
end

local function listIter(_inv, _idx)
	_idx = _idx + 1

	if _idx > _inv.size then
		return nil,nil
	end

	local nextobj = _inv[_idx]

	return _idx, nextobj
end

function listContents(_inv, includeall)
	local func
	if includeall then
		func = listIter
	else
		func = listIterSkip
	end

	return func, _inv, 0
end

function getStoredAmount(_inv, _item)
	if type(_item) == "string" then
		_item = db.getItem(_item)
	end

	if not _item then
		error("Invalid input")
	end

	local count = 0

	for i,c in inventory.listContents(_inv) do
		if c.item == _item then
			count = count + c.count
		end
	end

	return count
end

function contains(_inv, _input, _amout)
	local item, minCount
	if type(_input) == "string" then
		item = db.getItem(_input)
		minCount = _amount or 1
	elseif getmetatable(_input) == "stack" then
		item = _input.item
		minCount = _input.count
	else
		item = _input
		minCount = _amount or 1
	end

	if not item then
		error("Invalid input")
	end

	for i,c in inventory.listContents(_inv) do
		if c.item == item then
			if c.count >= minCount then
				return true
			else
				minCount = minCount - c.count
			end
		end
	end

	return false
end

function swap(_inv1, _slot1, _inv2, _slot2)
	assert(_inv1:isValid(), "arg 1: invalid inventory")
	if not _slot2 then
		_slot2 = _inv2
		_inv2 = _inv1
	else
		assert(_inv2:isValid(), "arg 3: invalid inventory")
		assert(_inv1.side == _inv2.side, "Can't modify inventories from different peripherals")
	end

	local taskId = peripheral.call(_inv1.side, "swapStacks", _inv1.key, _slot1, _inv2.key, _slot2)
	if taskId~=-1 then

		local evt, id, success, param
		while id ~= taskId do
			evt, id, success, param = os.pullEvent("task_result")
		end

		if success then
			if param then
				return unpack(param)
			end
		else
			error(param)
		end
	end
end

function move(amount, _inv1, _slot1, _inv2, _slot2)
	assert(_inv1:isValid(), "arg 1: invalid inventory")
	if not _slot2 then
		_slot2 = _inv2
		_inv2 = _inv1
	else
		assert(_inv2:isValid(), "arg 3: invalid inventory")
		assert(_inv1.side == _inv2.side, "Can't modify inventories from different peripherals")
	end

	local taskId = peripheral.call(_inv1.side, "splitStack", _inv1.key, _slot1, _inv2.key, _slot2, amount)

	if taskId~=-1 then

		local evt, id, success, param
		while id ~= taskId do
			evt, id, success, param = os.pullEvent("task_result")
		end

		if success then
			if param then
				return unpack(param)
			end
		else
			error(param)
		end
	end
end

function isPresent(_side, _key)
	return (peripheral.getType(_side) == "inventory" or peripheral.getType(_side) == "arpturtle") and peripheral.call(_side, "isInventoryValid", _key)
end

local inv_mt = {
	-- Function table
	list = listContents,
	isValid = function(_inv)
		return inventory.isPresent(_inv.side, _inv.key)
	end,
	contains = contains,
	getStoredAmount = getStoredAmount,
	swap=swap,
	move=move,

	--metatags
	__newindex = function()
		error("Can't change inventory properties")
	end,
	__tostring = function(t)
		assert(inventory.isPresent(t.side), "Invalid Inventory")
		return peripheral.call(t.side, "getInventoryName", t.key)
	end,
	__eq = function(inv1, inv2)
		return inv1.side==inv2.side and inv1.key==inv2.key
	end,

	-- lua 5.2-metatags
	__ipairs = listContents,
	__pairs = listContents,
	__len = function(t)
		assert(inventory.isPresent(t.side), "Invalid Inventory")
		return peripheral.call(t.side, "getInventorySize", t.key)
	end,

	-- protected metatable
	__metatable = "inventory"
}

inv_mt.__index = function(t, i)
	assert(inventory.isPresent(t.side), "Invalid Inventory")
	if type(i) == "number" then
		local stack = peripheral.call(t.side, "getInventorySlot", t.key, i)

		if stack then
			stack.inventory = t;
			stack.slot = i
			stack.item = db.getItem(stack.item)
			stack = db.setStackMT(stack)
		end
		return stack
	end

	if i == "size" or i == "n" then
		return peripheral.call(t.side, "getInventorySize", t.key)
	end

	if i == "stackLimit" then
		return peripheral.call(t.side, "getInventoryStackLimit", t.key)
	end

	if i == "name" then
		return peripheral.call(t.side, "getInventoryName", t.key)
	end

	return inv_mt[i]
end

function get(_side, _key)

	if peripheral.getType(_side) ~= "inventory" then
		error("unsupported side")
	elseif not peripheral.call(_side, "isInventoryValid", _key) then
		return nil
	end

	local inv = {side = _side, key = _key}

	setmetatable(inv, inv_mt)

	return inv
end