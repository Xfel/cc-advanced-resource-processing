
function getInventory(_key)
	return inventory.get("right",_key)
end

function detect()
	local tTable = {peripheral.call("right","detect")}
	
	local tResult = {}
	
	local i = 1
	while i <= #tTable do
		local tStack = db.stack(tTable[i],tTable[i+1],tTable[i+2])
		
		i = i + 3
		
		table.insert(tResult,tStack)
	end
	
	return tResult
end

function detectUp()
	local tTable = {peripheral.call("right","detectUp")}
	
	local tResult = {}
	
	local i = 1
	while i <= #tTable do
		local tStack = db.stack(tTable[i],tTable[i+1],tTable[i+2])
		
		i = i + 3
		
		table.insert(tResult,tStack)
	end
	
	return tResult
end

function detectDown()
	local tTable = {peripheral.call("right","detectDown")}
	
	local tResult = {}
	
	local i = 1
	while i <= #tTable do
		local tStack = db.stack(tTable[i],tTable[i+1],tTable[i+2])
		
		i = i + 3
		
		table.insert(tResult,tStack)
	end
	
	return tResult
end
