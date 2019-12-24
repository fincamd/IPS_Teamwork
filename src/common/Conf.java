package common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Conf {
	private Properties props;

	private static Map<String, Conf> configurations = new HashMap<>();

	public Conf(String path) {
		this.props = new Properties();
		try {
			props.load(inputStreamFromPath(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("File properties cannot be loaded", e);
		}

	}

	private InputStream inputStreamFromPath(String path) throws IOException {
		return new FileInputStream(new File(path));
	}

	public static Conf getInstance(String path) {
		Conf config = configurations.get(path);
		if (config == null) {
			config = new Conf(path);
			configurations.put(path, config);
		}
		return config;
	}

	public String getProperty(String key) {
		String value = props.getProperty(key);
		if (value == null) {
			throw new RuntimeException("Property not found in config file");
		}
		return value;
	}

}
