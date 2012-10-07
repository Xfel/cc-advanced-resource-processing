-- version @arp.version@

local tArgs = {...}

-- enshure that the apis are loaded
if not inventory and not os.loadAPI("/rom/apis/inventory") then
	print("No inventory reader found.")
	return false
end

if not db and not os.loadAPI("/rom/apis/db") then
	print("No inventory reader found.")
	return false
end

-- locate the inventory reader
local side,key
if #tArgs >= 1 then
	-- use arg value
	side = tArgs[1]
	if not inventory.isPresent(side) then
		print("Invalid side specified.")
		return
	end
	
	if #tArgs>=2 then
		key = tArgs[2]
		if not inventory.isPresent(side,key) then
			print("Invalid side specified.")
		return
	end
	end
else
	-- do full search
	local sides = redstone.getSides()
	
	if peripheral.getSides then
		sides = peripheral.getSides()
	end
	
	for _,s in ipairs(sides) do
		if inventory.isPresent(s) then
			side = s
			break
		end
	end
	if not side then
		print("No inventory reader found.")
		return
	end
end

-- open the inventory
local inv = inventory.get(side, key)

-- print generic information
print(inv.name)
print("Size: ", inv.size)
print("Stack size limit: ", inv.stackLimit)
print()
print("Contents:")

-- collect items
local itemMap = {}
local totalDamage = {}
for _,stack in inv:list() do
	local oldCount = itemMap[stack.item.name] or 0
	itemMap[stack.item.name] = oldCount + stack.count
	if stack.damage and stack.item.damageable then
		local maxd = stack.item.maxDamage
		local olddmg = (totalDamage[stack.item.name] or 0)
		if maxd == 0 then
			totalDamage[stack.item.name] = olddmg + 100
		else
			totalDamage[stack.item.name] = olddmg + stack.damage * 100 / maxd
		end
	end
end

print("Found items...")

-- print items
local nLinesPrinted = 5
local w,h = term.getSize()

for item, count in pairs(itemMap) do
	-- enshure a long item list won't take off. Copied from help.
    if nLinesPrinted >= h - 2 then
   		term.write("Press any key to continue")
   		os.pullEvent("key")
   		term.clearLine()
   		term.setCursorPos(1, h)
   	end
	local printDamage
	if totalDamage[item] then
		printDamage = string.format(" (%3.1d%% OK)", 100-totalDamage[item] / count)
	end
	nLinesPrinted = nLinesPrinted + print(string.format(" %4d of %s", count, item), printDamage)
end

