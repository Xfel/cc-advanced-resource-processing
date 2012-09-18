-- version @arp.version@

function isPresent(_side)
	return peripheral.getType(_side) == "workbench"
end

function setRecipe(_side, recipe)
	if peripheral.getType(_side) ~= "workbench" then
		error("unsupported side")
	end
	
	if recipe.type and recipe.type~= "crafting" then
		error("the digital workbench can only handle crafting recipes.")
	end
	
	local processed = {}
	
	if recipe then
		for i = 1,9 do
			if recipe[i] then
				processed[i] = recipe[i].name
			else
				processed[i] = "#EMPTY#"
			end
		end
	end
	
	return peripheral.call(_side, "setRecipe", unpack(processed))
end

function getRecipe(_side)
	if peripheral.getType(_side) ~= "workbench" then
		error("unsupported side")
	end
	
	if not getResult(_side) then
		return nil
	end
	
	local recipe = {peripheral.call(_side, "getRecipe")}
	local result = {}
	
	for i, part in ipairs(recipe) do
		result[i] = db.getItem(part)
	end
	
	recipe.type = "crafting"
	recipe.width = 3
	recipe.height = 3
	
	return result
end

function getResult(_side)
	if peripheral.getType(_side) ~= "workbench" then
		error("unsupported side")
	end
	
	return db.stack(peripheral.call(_side, "getResult"))
end

function isRecipeValid(_side)
	if peripheral.getType(_side) ~= "workbench" then
		error("unsupported side")
	end
	
	return peripheral.call(_side, "getResult") ~= nil
end

function isActivated(_side)
	if peripheral.getType(_side) ~= "workbench" then
		error("unsupported side")
	end
	
	return peripheral.call(_side, "isActivated")
end

function isWorking(_side)
	if peripheral.getType(_side) ~= "workbench" then
		error("unsupported side")
	end
	
	return peripheral.call(_side, "isWorking")
end

function activate(_side,activated)
	if peripheral.getType(_side) ~= "workbench" then
		error("unsupported side")
	end
	
	return peripheral.call(_side, "setActivated", activated)
end

function craft(_side,count)
	count = tonumber(count)
	
	activate(_side,true)
	
	local i = 0
	
	while i < count do
		local event, eside, p1 = os.pullEventRaw()
		
		if event == "terminate" then
			activate(_side, false)
			print( "Terminated" )
			error()
		elseif _side == eside then
			if event == "crafted" then
				i = i + 1
			elseif event == "missing_resource" then
				activate(_side, false)
				return false, db.getItem(p1)
			end
		end
	end
	
	activate(_side, false)
	return true
end