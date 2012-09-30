
function isPresent(_side)
	return peripheral.getType(_side) == "allocator"
end

function addFilter(_side, ...)
	if peripheral.getType(_side) ~= "allocator" then
		error("unsupported side")
	end
	
	local tArgs = {...}
	
	if #tArgs == 0 then
		return
	end
	
	local filter = {peripheral.call(_side, "getFilter")}
	
	for i,item in ipairs(tArgs) do
		table.insert(filter, item.name)
	end
	
	peripheral.call(_side, "setFilter", unpack(filter))
end

function removeFilter(_side, ...)
	if peripheral.getType(_side) ~= "allocator" then
		error("unsupported side")
	end
	
	local tArgs = {...}
	
	if #tArgs == 0 then
		return
	end
	
	local filter = {peripheral.call(_side, "getFilter")}
	
	for _,item in ipairs(tArgs) do
		for i, fitem in ipairs(filter) do
			if fitem == item.name then
				table.remove(filter, i)
				break
			end
		end
	end
	
	peripheral.call(_side, "setFilter", unpack(filter))
end

function clearFilter(_side)
	if peripheral.getType(_side) ~= "allocator" then
		error("unsupported side")
	end
	
	peripheral.call(_side, "setFilter")
end

function getFilter(_side)
	if peripheral.getType(_side) ~= "allocator" then
		error("unsupported side")
	end
	
	local filter = {peripheral.call(_side, "getFilter")}
	local result = {}
	
	for i, part in ipairs(filter) do
		result[i] = db.getItem(part)
	end
	
	return result
end

function isActivated(_side)
	if peripheral.getType(_side) ~= "allocator" then
		error("unsupported side")
	end
	
	return peripheral.call(_side, "isActivated")
end

function activate(_side,activated)
	if peripheral.getType(_side) ~= "allocator" then
		error("unsupported side")
	end
	
	return peripheral.call(_side, "setActivated", activated)
end

function getFilterMode(_side)
	if peripheral.getType(_side) ~= "allocator" then
		error("unsupported side")
	end
	
	return peripheral.call(_side, "getFilterMode")
end

function setFilterMode(_side,mode)
	if peripheral.getType(_side) ~= "allocator" then
		error("unsupported side")
	end
	
	peripheral.call(_side, "setFilterMode", mode)
end

function getSpecialSlots(_side)
	if peripheral.getType(_side) ~= "allocator" then
		error("unsupported side")
	end
	
	return peripheral.call(_side, "getSpecialSlots")
end

function setSpecialSlots(_side, nSourceSlot, nTargetSlot)
	if peripheral.getType(_side) ~= "allocator" then
		error("unsupported side")
	end
	
	peripheral.call(_side, "setSpecialSlots", nSourceSlot, nTargetSlot)
end

function transmit(_side,_count,_type)
	
	local oldFilter
	local oldMode
	if _type then
		oldFilter = { peripheral.call(_side, "getFilter") }
		peripheral.call(_side, "setFilter", tostring(_type))
		oldMode = peripheral.call(_side, "getFilterMode")
		peripheral.call(_side, "setFilterMode", "")
	end
	
	local i = 0
	activate(_side,true)
	
	while i < _count do
		local event, eside, item, count = os.pullEventRaw()
		if _side == eside then
			if event == "allocated" then
				i = i + count
			elseif event == "terminate" then
				if _type then
					peripheral.call(_side, "setFilter", unpack(oldFilter))
				end
				activate(_side, false)
				print( "Terminated" )
				error()
			end
		end
	end
	
	activate(_side, false)
	
	if _type then
		peripheral.call(_side, "setFilter", unpack(oldFilter))
		peripheral.call(_side, "setFilterMode", oldMode)
	end
	
	return true
end