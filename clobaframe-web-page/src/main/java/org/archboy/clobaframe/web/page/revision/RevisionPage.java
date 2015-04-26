package org.archboy.clobaframe.web.page.revision;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.archboy.clobaframe.web.page.Page;

/**
 *
 * @author yang
 */
public class RevisionPage extends Page {
	
	private int revision;
	
	public int getRevision() {
		return revision;
	}

	public void setRevision(int revision) {
		this.revision = revision;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(getName())
				.append(getLocale())
				.append(getRevision())
				.toHashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null){
			return false;
		}

		if (o == this){
			return true;
		}

		if(o.getClass() != getClass()){
			return false;
		}

		RevisionPage other = (RevisionPage)o;
		return new EqualsBuilder()
				.append(getName(), other.getName())
				.append(getLocale(), other.getLocale())
				.append(getRevision(), other.getRevision())
				.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("name", getName())
				.append("locale", getLocale())
				.append("revision", getRevision())
				.append("title", getTitle())
				.toString();
	}
}