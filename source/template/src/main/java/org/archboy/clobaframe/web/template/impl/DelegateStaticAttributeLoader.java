package org.archboy.clobaframe.web.template.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.archboy.clobaframe.web.template.StaticAttributeLoader;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 *
 * @author yang
 */
public class DelegateStaticAttributeLoader implements StaticAttributeLoader {
	
	public static final String SETTING_KEY_STATIC_ATTRIBUTES_DEFINE_FILE_NAME = "clobaframe.web.template.viewAttributeDefineFileName";
	public static final String DEFAULT_STATIC_ATTRIBUTES_DEFINE_FILE_NAME = ""; //"classpath:staticViewAttribute.json";
	
	@Value("${" + SETTING_KEY_STATIC_ATTRIBUTES_DEFINE_FILE_NAME + ":" + DEFAULT_STATIC_ATTRIBUTES_DEFINE_FILE_NAME + "}")
	private String defineFileName; // = DEFAULT_STATIC_ATTRIBUTES_DEFINE_FILE_NAME;

	@Inject
	private ResourceLoader resourceLoader;
	
	@Inject
	private BeanFactory beanFactory;

	TypeReference<Collection<Map<String, String>>> typeReference = 
					new TypeReference<Collection<Map<String, String>>>() {};
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private Map<String, Object> staticAttributes = new HashMap<>();

	public void setDefineFileName(String defineFileName) {
		this.defineFileName = defineFileName;
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@PostConstruct
	public void init() throws Exception {
		if (StringUtils.isEmpty(defineFileName)) {
			return;
		}
		
		Resource resource = resourceLoader.getResource(defineFileName);
		if (!resource.exists()) {
			throw new FileNotFoundException(
					String.format("Can not found the view static attribute define file [%s].", 
							defineFileName));
		}
		
		InputStream in = null;
		try{
			in = resource.getInputStream();
			Collection<Map<String, String>> items = objectMapper.readValue(in, typeReference);
			
			for (Map<String, String> item : items) {
				//for(Map.Entry<String, String> entry : map.entrySet()) {
					String key = item.get("key");
					String bean = item.get("bean");
					Object object = beanFactory.getBean(bean);

//					if (object == null) {
//						throw new IllegalArgumentException(
//								String.format("Can not found the bean [id=%s]", bean));
//					}

					staticAttributes.put(key, object);
				//}
			}
		}finally{
			IOUtils.closeQuietly(in);
		}
	}
	
	@Override
	public Map<String, Object> list() {
		return staticAttributes;
	}
}
