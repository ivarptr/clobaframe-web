package org.archboy.clobaframe.web.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Locale;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.archboy.clobaframe.query.simplequery.SimpleQuery;
import org.archboy.clobaframe.web.page.PageInfo;
import org.archboy.clobaframe.web.page.PageKey;
import org.archboy.clobaframe.web.page.revision.RevisionPageInfo;
import org.archboy.clobaframe.web.page.revision.RevisionPageManager;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author yang
 */
@Controller
public class PageController implements Ordered {

	private final int pagePathPrefixNameLength = "/page/".length();
	private final int pagePrefixUrlNameLength = "/".length();
	
	@Inject
	private RevisionPageManager revisionPageManager;

	public void setRevisionPageManager(RevisionPageManager revisionPageManager) {
		this.revisionPageManager = revisionPageManager;
	}

	@Override
	public int getOrder() {
		// lower priority because of the all page url matcher "^/.*$" in this controller.
		return 100; 
	}
	
	@RequestMapping("^/page/.*$")
	public String sendPage(
			HttpServletRequest request,
			Locale locale,
			@RequestParam(value = "locale", required = false) Locale preferLocale,
			@RequestParam(value = "revision", required = false, defaultValue = "-1") int revision,
			Model model) throws IOException {
		
		String path = request.getRequestURI();
		String pageName = path.substring(pagePathPrefixNameLength);
		
		PageInfo page = null;
		if (revision >= 0) {
			Assert.notNull(preferLocale);
			page = revisionPageManager.get(new PageKey(pageName, preferLocale), revision);
		}else if (preferLocale != null) {
			page = revisionPageManager.get(new PageKey(pageName, preferLocale));
		}else {
			page = getCompatibleLocalePage(pageName, locale);
		}
		
		if (page == null){
			throw new FileNotFoundException("No this page: " + pageName);
		}
		
		Collection<RevisionPageInfo> revisions = revisionPageManager.listRevision(page.getPageKey());
		Collection<Locale> locales = revisionPageManager.listLocale(page.getPageKey().getName());
		
		revisions = SimpleQuery.from(revisions).orderBy("revision").list();
		
		model.addAttribute("page", page);
		model.addAttribute("revisions", revisions);
		model.addAttribute("locales", locales);
		
		String templateName = StringUtils.isEmpty(page.getTemplateName()) ? 
				revisionPageManager.getDefaultTemplateName(): 
				page.getTemplateName();
		
		return templateName;
	}
	
	@RequestMapping("^/.*$")
	public String sendUrlPage(
			HttpServletRequest request,
			Locale locale,
			@RequestParam(value = "locale", required = false) Locale preferLocale,
			@RequestParam(value = "revision", required = false, defaultValue = "-1") int revision,
			Model model) throws IOException {
		
		String path = request.getRequestURI();
		String urlName = path.substring(pagePrefixUrlNameLength); // exclude the '/'.
		String pageName = revisionPageManager.getByUrlName(urlName);
		
		if (pageName == null) {
			throw new FileNotFoundException("Page not found: " + urlName);
		}
		
		PageInfo page = null;
		if (revision >= 0) {
			Assert.notNull(preferLocale);
			page = revisionPageManager.get(new PageKey(pageName, preferLocale), revision);
		}else if (preferLocale != null) {
			page = revisionPageManager.get(new PageKey(pageName, preferLocale));
		}else {
			page = getCompatibleLocalePage(pageName, locale);
		}
		
		if (page == null){
			throw new FileNotFoundException("No this page: " + pageName);
		}
		
		Collection<RevisionPageInfo> revisions = revisionPageManager.listRevision(page.getPageKey());
		Collection<Locale> locales = revisionPageManager.listLocale(page.getPageKey().getName());
		
		revisions = SimpleQuery.from(revisions).orderBy("revision").list();
		
		model.addAttribute("page", page);
		model.addAttribute("revisions", revisions);
		model.addAttribute("locales", locales);
		
		String templateName = StringUtils.isEmpty(page.getTemplateName()) ? 
				revisionPageManager.getDefaultTemplateName(): 
				page.getTemplateName();
		
		return templateName;
	}

	/**
	 * try to find the compatible language page.
	 * 
	 * @param pageName
	 * @param locale
	 * @return 
	 */
	private PageInfo getCompatibleLocalePage(String pageName, Locale locale) {
		
		PageKey pageKey = new PageKey(pageName, locale);
		PageInfo page = revisionPageManager.get(pageKey);
		
		if (page != null) {
			return page;
		}
		
		if (StringUtils.isNotEmpty(locale.getLanguage())){
			
			// try to get the page without country code
			if (StringUtils.isNotEmpty(locale.getCountry())){
			
				locale = new Locale(locale.getLanguage());
				pageKey = new PageKey(pageName, locale);
				page = revisionPageManager.get(pageKey);

				if (page != null) {
					return page;
				}
			}
			
			// try to get the page with default locale
			pageKey = new PageKey(pageName, revisionPageManager.getDefaultLocale());
			page = revisionPageManager.get(pageKey);
		}
		
		return page;
	}
		
}
