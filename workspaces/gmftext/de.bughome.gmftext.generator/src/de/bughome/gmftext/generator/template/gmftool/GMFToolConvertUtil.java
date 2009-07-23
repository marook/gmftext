/*
 * Copyright 2009 Markus Pielmeier
 *
 * This file is part of gmftext.
 *
 * gmftext is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * gmftext is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with gmftext.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.bughome.gmftext.generator.template.gmftool;

import org.eclipse.gmf.tooldef.ToolRegistry;
import org.eclipse.xtend.XtendFacade;

import de.bughome.gmfText.Model;

public final class GMFToolConvertUtil {
	
	private GMFToolConvertUtil(){
		throw new IllegalStateException();
	}
	
	public static ToolRegistry transformModelToGmfTool(final Model model) {
		final XtendFacade f =
			XtendFacade.create("de::bughome::gmftext::generator::template::gmftool::GMFToolConvert");
		return (ToolRegistry) f.call("transformModelToGmfTool", model);
	}

}
