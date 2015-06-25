package org.archboy.clobaframe.web.service;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.inject.Named;
import static org.archboy.clobaframe.setting.SettingProvider.PRIORITY_HIGH;
import org.archboy.clobaframe.setting.global.GlobalSettingProvider;
import org.archboy.clobaframe.setting.global.GlobalSettingRepository;
import org.archboy.clobaframe.setting.support.Utils;

/**
 *
 * @author yang
 */
@Named
public class InMemoryGlobalSettingRepository implements GlobalSettingProvider, GlobalSettingRepository {
	
	private Map<String, Object> setting = new LinkedHashMap<String, Object>();
		
	@Override
	public int getOrder() {
		return PRIORITY_NORMAL;
	}

	@Override
	public Map<String, Object> getAll() {
		return setting;
	}

	@Override
	public void update(Map<String, Object> item) {
		setting = Utils.merge(setting, item);
	}

	@Override
	public void update(String key, Object value) {
		setting = Utils.merge(setting, key, value);
	}
}