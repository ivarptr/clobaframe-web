package org.archboy.clobaframe.web.page.revision.local.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.io.MimeTypeDetector;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.file.FileBaseResourceInfo;
import org.archboy.clobaframe.io.file.local.DefaultLocalResourceScanner;
import org.archboy.clobaframe.io.file.local.LocalResourceScanner;
import org.archboy.clobaframe.web.page.PageInfo;
import org.archboy.clobaframe.web.page.PageKey;
import org.archboy.clobaframe.web.page.revision.RevisionPageInfo;
import org.archboy.clobaframe.web.page.revision.impl.AbstractPreloadRevisionPageProvider;
import org.archboy.clobaframe.web.page.revision.local.LocalRevisionPageInfo;
import org.archboy.clobaframe.web.page.revision.local.LocalRevisionPageResourceInfo;
import org.archboy.clobaframe.web.page.revision.local.LocalRevisionPageResourceNameStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 *
 * @author yang
 */
@Named
public class LocalRevisionPageProvider extends AbstractPreloadRevisionPageProvider {
	//implements ResourceLoaderAware, InitializingBean {

	@Inject
	private ResourceLoader resourceLoader;

	@Inject
	private MimeTypeDetector mimeTypeDetector;
	
//	@Inject
//	private LocalResourceScanner resourceScanner;
	
	// local resource path, usually relative to the 'src/main/webapp' folder.
	// to using this repository, the web application war package must be expended when running.
	public static final String DEFAULT_PAGE_RESOURCE_PATH = "" ; //"resources/page";
	
	public static final String SETTING_KEY_PAGE_RESOURCE_PATH = "clobaframe.web.page.resource.path";
	
	@Value("${" + SETTING_KEY_PAGE_RESOURCE_PATH + ":" + DEFAULT_PAGE_RESOURCE_PATH + "}")
	private String pageResourcePath;
	
	/**
	 * The resource name can specify some properties, includes language code,
	 * revision number, rendering template name.
	 * 
	 * Note:
	 * Resources with the same name must keep the same URL name and template name also.
	 * 
	 * Example:
	 * 
	 * terms.md // default English language
	 * terms_ja.md // for Japanese language
	 * terms_zh_CN.md // for Simplified Chinese language
	 * terms.r23.md // the revision 23
	 * terms@path#name.md // using the specify URL "path/name" to override the default page URL.
	 *						// the slash mark is replaced with the hash mask.
	 * terms[templatepath#name].md // using the template "templatepath/name" for rendering, 
	 *								//the slash mark is replaced with the hash mark.
	 * A full name example:
	 * terms@developers#api[template#dev]_zh-CN.r23.md
	 * 
	 * Regex match groups:
	 * 1 - name
	 * 3 - custom URL name (optional)
	 * 5 - template name (optional)
	 * 7 - lang code (optional)
	 * 10 - country code (optional)
	 * 12 - revision (optional)
	 
	 * 
	 */
	private static final String resourceNameRegex = 
			//"^([a-zA-Z0-9-]+)(@([a-zA-Z0-9#-]+))?(\\[([a-zA-Z0-9#-]+)\\])?(_([a-z]{2})((_)([A-Z]{2}))?)?(.r(\\d+))?\\.md$";
			"^(?<name>[a-zA-Z0-9-]+)" +
			"(@(?<url>[a-zA-Z0-9#-]+))?" +
			"(\\[(?<template>[a-zA-Z0-9#-]+)\\])?" +
			"(_(?<locale>[a-z]{2}(-[a-zA-Z]{2,4})?))?" +
			"(.r(?<revision>\\d+))?" +
			"\\.md$";
	
	private Pattern resourceNamePattern = Pattern.compile(resourceNameRegex);
	
	public static final String DEFAULT_LOCALE = "en";
	
	public static final String SETTING_KEY_DEFAULT_LOCALE = "clobaframe.web.page.defaultLocale";
	
	@Value("${" + SETTING_KEY_DEFAULT_LOCALE + ":" + DEFAULT_LOCALE + "}")
	private Locale defaultLocale = Locale.ENGLISH;

	// content header
	private static final String regexHeader1a = "^(.+)\\n(={1,})(\\n|$)";
	private static final String regexHeader1b = "^(#.+#?)(\\n|$)";
	
	private static final Pattern patternHeader1a = Pattern.compile(regexHeader1a);
	private static final Pattern patternHeader1b = Pattern.compile(regexHeader1b);

	private final Logger logger = LoggerFactory.getLogger(LocalRevisionPageProvider.class);

	//@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
	
	public void setMimeTypeDetector(MimeTypeDetector mimeTypeDetector) {
		this.mimeTypeDetector = mimeTypeDetector;
	}

	public void setPageResourcePath(String pageResourcePath) {
		this.pageResourcePath = pageResourcePath;
	}

	public void setDefaultLocale(Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	@PostConstruct
	//@Override
	public void init() throws Exception {
		for(PageInfo pageInfo : listLocal()){
			super.add((RevisionPageInfo)pageInfo);
		}
	}
	
	private Collection<PageInfo> listLocal() {
		Resource resource = resourceLoader.getResource(pageResourcePath);
		
		try{
			File baseDir = resource.getFile();
			
			// Do not throws exception because the web application maybe running in the
			// WAR package.
			if (!baseDir.exists()){
				logger.error("Can not find the local page resource folder [{}], please ensure " +
						"unpackage the WAR if you are running web application.", pageResourcePath);
				return new ArrayList<PageInfo>();
			}
			
			return listLocal(baseDir);
			
		}catch(IOException e){
			logger.error("Load local page resource repository error, {}", e.getMessage());
		}
		
		return new ArrayList<PageInfo>();
	}
	
	private Collection<PageInfo> listLocal(File baseDir) {
		
		LocalRevisionPageResourceNameStrategy localPageResourceNameStrategy = new DefaultLocalRevisionPageResourceNameStrategy(baseDir);
		LocalRevisionPageResourceInfoFactory localPageResourceInfoFactory = new LocalRevisionPageResourceInfoFactory(mimeTypeDetector, localPageResourceNameStrategy);
		LocalResourceScanner localResourceScanner = new DefaultLocalResourceScanner();
				
		List<PageInfo> pages = new ArrayList<PageInfo>();
		
		
		Collection<FileBaseResourceInfo> fileBaseResourceInfos = localResourceScanner.list(baseDir, localPageResourceInfoFactory);
		for(FileBaseResourceInfo fileBaseResourceInfo : fileBaseResourceInfos) {
			LocalRevisionPageResourceInfo localPageResourceInfo = (LocalRevisionPageResourceInfo)fileBaseResourceInfo;
			
			String fullname = localPageResourceInfo.getName();
			String path = null;
			String filename = fullname;
			
			int pos = fullname.lastIndexOf('/');
			if (pos > 0) {
				path = fullname.substring(0, pos);
				filename = fullname.substring(pos + 1);
			}
			
			Matcher matcher = resourceNamePattern.matcher(filename);
			if (matcher.find()){
				String name = matcher.group("name");
				String urlName = matcher.group("url");
				String templateName = matcher.group("template");
				//String langCode = matcher.group(7);
				//String countryCode = matcher.group(10);
				String localeString = matcher.group("locale");
				String revisionString = matcher.group("revision");
				
				// build page name
				String pageName = (path != null) ? path + "/" + name : name;
				
				// get the locale value
				Locale locale = (localeString == null) ? defaultLocale : Locale.forLanguageTag(localeString);

//				if (langCode != null && countryCode != null){
//					locale = new Locale(langCode, countryCode);
//				}else if (langCode != null){
//					locale = new Locale(langCode);
//				}else{
//					locale = defaultLocale;
//				}

				// get the revision number.
				// default is "0" (while no specified).
				int revision = 0;
				if (revisionString != null) {
					revision = Integer.parseInt(revisionString);
				}
				
				// convert the hash mark
				if (urlName != null) {
					urlName = urlName.replaceAll("#", "/");
				}
				
				// convert the hash mark
				if (templateName != null) {
					templateName = templateName.replaceAll("#", "/");
				}
				
				try{
					RevisionPageInfo revisionDoc = convertResourceInfo(
								fileBaseResourceInfo, 
								pageName, locale, revision, 
								urlName, templateName);
				
					pages.add(revisionDoc);
				}catch(IOException e){
					// ignore
				}
			}
		}
		
		return pages;
	}
	
	private RevisionPageInfo convertResourceInfo(ResourceInfo resourceInfo, 
			String name, Locale locale, int revision,
			String urlName, String templateName) throws IOException{
		
		InputStream in = null;
		try{
			in = resourceInfo.getContent();
			String source = IOUtils.toString(in, "UTF-8");
			TitleAndContent titleAndContent = extractTitle(source);
			
			LocalRevisionPageInfo page = new LocalRevisionPageInfo();
			page.setContent(titleAndContent.getContent());
			page.setLastModified(resourceInfo.getLastModified());
			page.setPageKey(new PageKey(name, locale));
			page.setReadonly(true);
			page.setRevision(revision);
			page.setTemplateName(templateName);
			page.setTitle(titleAndContent.getTitle());
			page.setUrlName(urlName);
			
			return page;
			
		}finally{
			IOUtils.closeQuietly(in);
		}
	}
	
	/**
	 * Get the first line as page title.
	 * @param text
	 * @return 
	 */
	private TitleAndContent extractTitle(String text) {
		/**
		 * #TITLE#\n
		 * CONTENT
		 * 
		 * - OR -
		 * 
		 * TITLE\n
		 * ===\n
		 * CONTENT
		 * 
		 * */
		Matcher matcher1a = patternHeader1a.matcher(text);
		if (matcher1a.find()){
			String title = matcher1a.group(1);
			String content = null;
			int start = matcher1a.end();
			if (start < text.length()) {
				content = trimContent(text.substring(start));
			}
			return new TitleAndContent(title, content);
		}
		
		Matcher matcher1b = patternHeader1b.matcher(text);
		if (matcher1b.find()){
			String title = trimAtxHeaderHash(matcher1b.group(1));
			String content = null;
			int start = matcher1b.end();
			if (start < text.length()) {
				content = text.substring(start).trim();
			}
			return new TitleAndContent(title, content);
		}

		throw new IllegalArgumentException("Can not find the Markdown document title.");
	}

	/**
	 * Remove the prefix and suffix hash marks.
	 * @param title
	 * @return 
	 */
	private String trimAtxHeaderHash(String title) {
		int start = 0;
		int end = title.length() - 1;
		for (; start < end; start ++) {
			if (title.charAt(start) != '#') break;
		}
		
		for (; end > start; end--){
			if (title.charAt(end) != '#') break;
		}
		
		return title.substring(start, end +1).trim();
	}	

	private String trimContent(String content) {
		return content.trim();
	}
	
	@Override
	public int getOrder() {
		return PRIORITY_LOWER;
	}
	
	private static class TitleAndContent {
		private String title;
		private String content;

		public TitleAndContent(String title, String content) {
			this.title = title;
			this.content = content;
		}

		public String getTitle() {
			return title;
		}

		public String getContent() {
			return content;
		}
	}
}
