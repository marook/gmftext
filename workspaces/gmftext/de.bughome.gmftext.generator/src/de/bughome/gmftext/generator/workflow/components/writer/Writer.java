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

package de.bughome.gmftext.generator.workflow.components.writer;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLHelperImpl;
import org.eclipse.emf.mwe.core.WorkflowContext;
import org.eclipse.emf.mwe.core.issues.Issues;
import org.eclipse.emf.mwe.core.lib.AbstractWorkflowComponent;
import org.eclipse.emf.mwe.core.monitor.ProgressMonitor;

public class Writer extends AbstractWorkflowComponent {
	
	private static Pattern MODEL_DEF_PATTERN =
		Pattern.compile("([\\w]*):(.*)");
	
	protected final Logger logger = Logger.getLogger(this.getClass());
	
	private final Collection<Model> models = new LinkedList<Model>();
	
	@Override
	public void checkConfiguration(final Issues issues) {
	}
	
	@Override
	public String getLogMessage() {
		return "Writing " + models.size() + " models.";
	}
	
	private ResourceSet createResourceSet(){
		final ResourceSet resourceSet = new ResourceSetImpl();
		
		resourceSet.setURIConverter(new ExtensibleURIConverterImpl());
		
		return resourceSet;
	}
	
	/**
	 * This method tries to convert any <code>URI</code> into a <code>URI</code>
	 * which can be handled by GMF.
	 * 
	 * <p>TODO This method is a total hack! Do not reuse it! There is no
	 * guarantee that this method will work as expected!</p>
	 * 
	 * @param uri This is the converted <code>URI</code>.
	 * @return Returns a <code>URI</code> which is more likely a
	 * <code>URI</code> which works with GMF than <code>uri</code>.
	 */
	private URI toGmfUri(final URI uri){
		final String scheme = uri.scheme();
		
		if(scheme == null){
			final String fileName = (new File(uri.path())).getAbsolutePath();
			
			/*
			 * TODO make the fileName.split("/") operation OS independent.
			 * right now this will not work with windows.
			 */
			return URI.createHierarchicalURI("file",
					null,
					null,
					fileName.split("/"),
					null,
					uri.fragment());
		}
		else if("file".equalsIgnoreCase(scheme)){
			/*
			 * TODO maybe we should check wether URI is absolute. when URI is
			 * not absolute we should convert it into an absolute one.
			 */
			
			return uri;
		}
		else if("platform".equalsIgnoreCase(scheme)){
			return uri;
		}
		
		logger.warn("Can't convert URI to GMF URI: " + uri);
		
		return uri;
	}
	
	private Resource createResource(final EObject model){
		final XMIResource r = new XMIResourceImpl(){
			@Override
			protected XMLHelper createXMLHelper() {
				return new XMLHelperImpl(this){
					@Override
					public URI deresolve(final URI uri) {
						final URI newUri = toGmfUri(uri);

						if(!uri.equals(newUri)){
							logger.debug("Converted " + uri + " to " + newUri);
						}
						
						return newUri;
					}
				};
			}
		};
		
		r.getContents().add(model);
		
		r.setEncoding("UTF-8");
		
	    return r;
	}
	
	@Override
	protected void invokeInternal(final WorkflowContext ctx,
			final ProgressMonitor monitor,
			final Issues issues) {
		final ResourceSet resourceSet = createResourceSet();
		
		for(final Model modelDef : models){
			final EObject model = (EObject) ctx.get(modelDef.slotName);
			
			if(model == null){
				throw new IllegalArgumentException("Slot " + modelDef.slotName 
						+ " was empty! Expected model / EObject.");
			}
			
			final URI modelUri = URI.createURI(modelDef.uri);
			
			final Resource res = createResource(model);
			res.setURI(modelUri);
			
			resourceSet.getResources().add(res);
		}
		
		// persist all resources
		for(final Resource res : resourceSet.getResources()){
			try {
				res.save(null);
			}
			catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public void addModel(final String modelDef){
		final Matcher m = MODEL_DEF_PATTERN.matcher(modelDef);
		
		if(!m.matches()){
			throw new IllegalArgumentException("Illegal model definition: "
					+ modelDef);
		}
		
		final String slotName = m.group(1);
		final String uri = m.group(2);
		
		models.add(new Model(slotName, uri));
	}
	
	private static class Model {
		
		private final String slotName;
		
		private final String uri;
		
		private Model(final String slotName,
				final String uri){
			this.slotName = slotName;
			this.uri = uri;
		}
		
	}

}
