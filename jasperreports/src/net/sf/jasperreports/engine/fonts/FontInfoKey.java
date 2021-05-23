package net.sf.jasperreports.engine.fonts;

import java.io.Serializable;
import java.util.Locale;

/**
 * Caching key.
 * 
 * @author Markus Pscheidt
 *
 */
public class FontInfoKey implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private Locale locale;
	
	public FontInfoKey(String name, Locale locale) {
		this.name = name;
		this.locale = locale;
	}

	public String getName() {
		return name;
	}

	public Locale getLocale() {
		return locale;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((locale == null) ? 0 : locale.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FontInfoKey other = (FontInfoKey) obj;
		if (locale == null) {
			if (other.locale != null)
				return false;
		} else if (!locale.equals(other.locale))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FontInfoKey [name=" + name + ", locale=" + locale + "]";
	}

}
