package org.archboy.clobaframe.web.tool;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.AbstractRequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 *
 * @author yang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml"})
public class PageHeaderToolTest {

	@Inject
	private PageHeaderTool pageHeaderTool;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testWrite()  {
		// test build header
		// expect: <meta charset="UTF-8">
		Map<String, Object> attr1 = new LinkedHashMap<String, Object>();
		attr1.put("charset", "UTF-8");
		
		String result1 = pageHeaderTool.write("meta", attr1, false);
		String text1 = "<meta charset=\"UTF-8\">";
		assertEquals(text1, result1);
		
		// expect: <link rel="icon" type="image/png" href="/favicon.png">
		Map<String, Object> attr2 = new LinkedHashMap<String, Object>();
		attr2.put("rel", "icon");
		attr2.put("type", "image/png");
		attr2.put("href", "/favicon.png");
		
		String result2 = pageHeaderTool.write("link", attr2, false);
		String text2 = "<link rel=\"icon\" type=\"image/png\" href=\"/favicon.png\">";
		assertEquals(text2, result2);
		
		// expect: <script type="text/javascript" src="script.js"></script>
		Map<String, Object> attr3 = new LinkedHashMap<String, Object>();
		attr3.put("type", "text/javascript");
		attr3.put("src", "script.js");
		
		String result3 = pageHeaderTool.write("script", attr3, true);
		String text3 = "<script type=\"text/javascript\" src=\"script.js\"></script>";
		assertEquals(text3, result3);
		
		// test write resource
		String res1 = pageHeaderTool.writeResource("css/index.css");
		assertEquals("<link href=\"/resource/css/index.css?v0999d52a\" rel=\"stylesheet\">", res1);
		
		String res2 = pageHeaderTool.writeResource("js/jquery-1.11.1.js");
		assertEquals("<script src=\"/resource/js/jquery-1.11.1.js?v3029834a\"></script>", res2);
		
		assertEquals(StringUtils.EMPTY, 
				pageHeaderTool.writeResource("css/none-exists.css"));
		
		// test write resource with custom attr
		Map<String, Object> attr4 = new LinkedHashMap<String, Object>();
		attr4.put("type", "text/jsx");
		String res3 = pageHeaderTool.writeResource("js/index.js", "script", "src", attr4, true);
		assertEquals("<script src=\"/resource/js/index.js?v4a6ae5f4\" type=\"text/jsx\"></script>", res3);
		
		assertEquals(StringUtils.EMPTY, 
				pageHeaderTool.writeResource("css/none-exists.css", "link", "src", attr4, false));
		
		// test get resources
		String ress1 = pageHeaderTool.writeResources(Arrays.asList("css/index.css", "js/jquery-1.11.1.js"));
		assertEquals(res1 + res2, ress1);
		
		String ress2 = pageHeaderTool.writeResources(Arrays.asList("css/index.css", "js/none-exists.js"));
		assertEquals(res1, ress2);
		
		String ress3 = pageHeaderTool.writeResources(Arrays.asList("css/none-exists.css", "js/none-exists.js"));
		assertEquals(StringUtils.EMPTY, ress3);
		
		String ress4 = pageHeaderTool.writeResources(Arrays.asList("css/index.css", "js/jquery-1.11.1.js"), "\n");
		assertEquals(res1 + "\n" + res2, ress4);
		
		
	}
}
