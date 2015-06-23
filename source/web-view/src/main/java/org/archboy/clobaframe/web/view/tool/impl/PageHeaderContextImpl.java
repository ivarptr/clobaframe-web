package org.archboy.clobaframe.web.view.tool.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.web.view.tool.PageHeaderContext;
import org.archboy.clobaframe.web.view.tool.PageHeaderProvider;
import org.archboy.clobaframe.web.view.tool.PageHeaderTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 *
 * @author yang
 */
@Named
public class PageHeaderContextImpl implements PageHeaderContext {

	@Inject
	private PageHeaderTool pageHeaderTool;
	
	@Autowired(required = false)
	private List<PageHeaderProvider> pageHeaderProviders; // = new ArrayList<PageHeaderProvider>();
	
	private static final String customHeaderRequestAttributeName = "clobaframe-web-view.customPageHeaders";
	
	@Override
	public List<String> getHeaders() {
		List<String> headers = new ArrayList<String>();
		
		if (pageHeaderProviders != null && !pageHeaderProviders.isEmpty()) {
			for(PageHeaderProvider pageHeaderProvider : pageHeaderProviders){
				headers.addAll(pageHeaderProvider.getHeaders());
			}
		}
		
		// get custom header from current HTTP request attributes (HTTP request scope).
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		
		@SuppressWarnings("unchecked")
		List<String> extraHeaders = (List<String>)requestAttributes.getAttribute(
				customHeaderRequestAttributeName, RequestAttributes.SCOPE_REQUEST);
		
		if (extraHeaders != null && !extraHeaders.isEmpty()){
			headers.addAll(extraHeaders);
		}
		
		return headers;
	}

	@Override
	public void addHeader(String tagName, Map<String, Object> attributes, boolean closeTag) {
		String header = pageHeaderTool.write(tagName, attributes, closeTag);
		
		// get custom header from current HTTP request attributes.
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		
		@SuppressWarnings("unchecked")
		List<String> extraHeaders = (List<String>)requestAttributes.getAttribute(
				customHeaderRequestAttributeName, RequestAttributes.SCOPE_REQUEST);
		
		if (extraHeaders == null) {
			extraHeaders = new ArrayList<String>();
		}
		
		extraHeaders.add(header);
		
		// set custom header to current HTTP request attributes.
		requestAttributes.setAttribute(customHeaderRequestAttributeName, 
				extraHeaders, 
				RequestAttributes.SCOPE_REQUEST);
		
	}
	
}
