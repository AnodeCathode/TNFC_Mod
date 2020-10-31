package mods.immibis.core.config;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import mods.immibis.core.api.porting.SidedProxy;

public class PropertyConfigReader implements IConfigReader {
	
	private Properties props;
	
	public PropertyConfigReader(String path) throws IOException {
		File f = new File(SidedProxy.instance.getMinecraftDir(), path);
		props = new Properties();
		FileInputStream f_in = new FileInputStream(f);
		try
		{
			props.load(f_in);
		}
		finally
		{
			f_in.close();
		}
	}

	@Override
	public String getConfigEntry(String path) {
		return props.getProperty(path);
	}

}
