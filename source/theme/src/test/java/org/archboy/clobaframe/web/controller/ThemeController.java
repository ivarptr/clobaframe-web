package org.archboy.clobaframe.web.controller;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.archboy.clobaframe.setting.global.GlobalSetting;
import org.archboy.clobaframe.web.theme.ThemeManager;
import org.archboy.clobaframe.web.theme.ThemePackage;
import org.archboy.clobaframe.web.theme.inject.ThemeResourcePageHeaderTool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author yang
 */
@Controller
public class ThemeController {
	
	@Inject
	private GlobalSetting globalSetting;

	@Inject
	private ThemeManager themeManager;
	
	@Inject
	private ThemeResourcePageHeaderTool themeResourcePageHeaderTool;
	
	@ResponseBody
	@RequestMapping("/settheme")
	public Collection<String> setTheme(
			@RequestParam(value="name", required = false, defaultValue = "") String name) 
			throws FileNotFoundException{
		
		Collection<String> headers = new ArrayList<>();
		
		if (StringUtils.isNotEmpty(name)) {
			ThemePackage themePackage = themeManager.get(ThemeManager.PACKAGE_CATALOG_LOCAL, name);
			if (themePackage == null) {
				throw new FileNotFoundException("No this theme:" + name);
			}
			headers = themeResourcePageHeaderTool.listFixedResourceHeaders(name);
		}
		
		globalSetting.set("theme", name);
		
		return headers;
	}
}
