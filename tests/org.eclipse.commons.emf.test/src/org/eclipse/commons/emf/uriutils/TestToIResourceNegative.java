package org.eclipse.commons.emf.uriutils;

import static org.junit.Assert.assertNull;

import org.eclipse.commons.emf.UriUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.common.util.URI;
import org.junit.Test;
import org.osgi.framework.FrameworkUtil;

/**
 * Test cases for invalid uses of {@link UriUtils#toIResource(URI)}.
 * 
 * @author Niko Stotz
 *
 */
public class TestToIResourceNegative extends ATestWorkspace {

	@Test(expected = NullPointerException.class)
	public void uriNull() throws Exception {
		UriUtils.toIResource(null);
	}

	@Test
	public void uriPlugin() throws Exception {
		Class<? extends TestToIResourceNegative> self = this.getClass();
		String pathName = FrameworkUtil.getBundle(self).getSymbolicName() + "/"
				+ self.getPackageName().replace('.', '/') + "/" + self.getSimpleName() + ".class";
		URI uri = URI.createPlatformPluginURI(pathName, true);
		IResource iResource = UriUtils.toIResource(uri);

		assertNull(iResource);
	}

	@Test
	public void uriOther() throws Exception {
		URI uri = URI.createURI("https://example.com/MyFile.ext");
		IResource iResource = UriUtils.toIResource(uri);

		assertNull(iResource);
	}

	@Test
	public void uriBroken() throws Exception {
		URI uri = URI.createURI("fasfasdf");
		IResource iResource = UriUtils.toIResource(uri);

		assertNull(iResource);
	}

	@Test(expected = IllegalArgumentException.class)
	public void uriPlatformResourceBroken() throws Exception {
		URI uri = URI.createURI("platform:/resource/...////");
		UriUtils.toIResource(uri);
	}

}
