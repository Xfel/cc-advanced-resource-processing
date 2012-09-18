package xfel.mods.arp.common.peripheral;

import java.util.Arrays;
import java.util.Map;

import dan200.computer.api.IPeripheral;

import xfel.mods.arp.api.ItemKey;
import xfel.mods.arp.base.peripheral.bind.AbstractAnnotatedPeripheral;
import xfel.mods.arp.base.peripheral.bind.PeripheralMethod;
import xfel.mods.arp.core.ResourceDatabase;

public class DatabasePeripheral extends AbstractAnnotatedPeripheral {
	
	protected final ResourceDatabase database;
	
	public DatabasePeripheral() {
		this("database");
	}

	protected DatabasePeripheral(String type) {
		super(type);
		database=ResourceDatabase.instance();
	}
	
	@PeripheralMethod
	public Map<?, ?> getItem(String nameOrId){
		ItemKey key=database.getItem(nameOrId);
		
		if(key==null)return null;
		
		return database.getItemProperties(key);
	}
	
	
}
