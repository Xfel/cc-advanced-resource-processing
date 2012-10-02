
local tuside

if peripheral.getType("right") == "arpturtle" then
	tuside = "right"
elseif peripheral.getType("left") == "arpturtle" then
	tuside = "left"
end

function isAvailable()
	return tuside ~= nil
end

function getInventory(_key)
	if not tuside then
		error("ARP Turtle upgrade not available")
	end
	
	return inventory.get(tuside,_key)
end

function detect()
	if not tuside then
		error("ARP Turtle upgrade not available")
	end
	
	local tStack = peripheral.call(tuside, "detect")
	
	return db.setStackMT(tStack)
end

function detectUp()
	if not tuside then
		error("ARP Turtle upgrade not available")
	end
	
	local tStack = peripheral.call(tuside, "detectUp")
	
	return db.setStackMT(tStack)
end

function detectDown()
	if not tuside then
		error("ARP Turtle upgrade not available")
	end
	
	local tStack = peripheral.call(tuside, "detectDown")
	
	return db.setStackMT(tStack)
end
