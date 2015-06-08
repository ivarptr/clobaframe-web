package org.archboy.clobaframe.web.page.revision;

import java.util.Collection;
import org.archboy.clobaframe.web.page.PageKey;
import org.archboy.clobaframe.web.page.PageProvider;

/**
 *
 * @author yang
 */
public interface RevisionPageProvider extends PageProvider {
	
	/**
	 * Get the specify page by the name and locale.
	 * To get the active revision, use the {@link PageManager#get(PageKey)} method.
	 * 
	 * @param pageKey
	 * @param revision
	 * @return NULL when it does not found
	 */
	RevisionPageInfo get(PageKey pageKey, int revision);
	
	/**
	 * 
	 * @param pageKey
	 * @return 
	 */
	Collection<RevisionPageInfo> listRevision(PageKey pageKey);
	
}
