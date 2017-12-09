package org.eclipse.commons.emf;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Utilities for handling {@linkplain org.eclipse.emf.common.util.URI Ecore
 * URIs}.
 * 
 * @author Niko Stotz
 * 
 * @since 0.0.1
 *
 */
public class UriUtils {

	/**
	 * Returns the equivalent {@linkplain org.eclipse.core.resources.IResource
	 * Eclipse IResource} for an {@linkplain org.eclipse.emf.common.util.URI Ecore
	 * URI}, if available.
	 * 
	 * <p>
	 * {@code uri} can be represented as IResource if {@code uri} is an
	 * {@linkplain URI#isPlatformResource() platform resource} (i.e. {@code uri}
	 * starts with {@code platform:/resource/}). Otherwise, this method returns
	 * {@code null}.
	 * </p>
	 * 
	 * <p>
	 * This method ignores any {@linkplain URI#fragment() fragment} or
	 * {@linkplain URI#query() query} of {@code uri}.
	 * </p>
	 * 
	 * <p>
	 * If the resulting IResource exists, this method returns the existing kind of
	 * IResource ({@linkplain org.eclipse.core.resources.IWorkspaceRoot
	 * IWorkspaceRoot}, {@linkplain org.eclipse.core.resources.IProject IProject},
	 * {@linkplain org.eclipse.core.resources.IFolder IFolder}, or
	 * {@linkplain org.eclipse.core.resources.IFile IFile}).
	 * </p>
	 * 
	 * <p>
	 * If the resulting IResource does not exist, this method returns an IFile
	 * pointing to the place equivalent to {@code uri}.
	 * </p>
	 * 
	 * <p>
	 * This method handles excess slashes (behind the platform resource identifiers)
	 * gracefully (i.e. ignores the slashes).<br/>
	 * Example: An URI of
	 * {@code platform:/resource/////MyProject///folder///deep/myFile.ext//} leads
	 * to an IFile for path {@code /MyProject/folder/deep/myFile.ext}.
	 * </p>
	 * 
	 * <p>
	 * <b>Note:</b> This method treats {@code uri} as case-sensitive (on <i>all</i>
	 * platforms, including Windows). Therefore, if the workspace contained a file
	 * at {@code /MyProject/myFolder/myFile.ext} and we passed the URI
	 * {@code platform:/resource/MyProject/myFolder/mYfILE.ext} to this method, the
	 * result is an IFile for path {@code /MyProject/myFolder/mYfILE.ext}.
	 * {@link IResource#exists() result.exists()} will return {@code false}.
	 * </p>
	 * 
	 * @param uri
	 *            The Ecore URI to return as Eclipse IResource.
	 * @return {@code uri} as Eclipse IResource, if available; {@code null}
	 *         otherwise.
	 * 
	 * @throws IllegalArgumentException
	 *             If {@code uri} is seriously ill-formatted.
	 * 
	 * @since 0.0.1
	 */
	public static @Nullable IResource toIResource(final @NonNull URI uri) {
		if (uri.isPlatformResource()) {
			String platformString = uri.toPlatformString(true);
			IPath path = Path.fromOSString(platformString);
			IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
			if (workspaceRoot.exists(path)) {
				return workspaceRoot.findMember(path);
			} else {
				return workspaceRoot.getFile(path);
			}
		}

		return null;
	}

}
