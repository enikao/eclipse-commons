package org.eclipse.commons.emf.uriutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;

import org.apache.commons.io.input.NullInputStream;
import org.eclipse.commons.emf.UriUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.emf.common.util.URI;
import org.junit.Test;

/**
 * Test cases for valid uses of {@link UriUtils#toIResource(URI)}.
 * 
 * @author Niko Stotz
 *
 */
public class TestToIResourcePositive extends ATestWorkspace {
	@Test
	public void file() throws Exception {
		waitForWorkspaceChanges(() -> {
			IFile file = project.getFile("myFile.ext");
			file.create(new NullInputStream(0), true, null);
		});

		URI uri = URI.createPlatformResourceURI("/myProject/myFile.ext", true);
		IResource iResource = UriUtils.toIResource(uri);

		assertTrue(iResource instanceof IFile);
		assertTrue(iResource.exists());
		assertEquals("/myProject/myFile.ext", iResource.getFullPath().toString());
	}

	@Test
	public void fileMissing() throws Exception {
		URI uri = URI.createPlatformResourceURI("/myProject/myFile.ext", true);
		IResource iResource = UriUtils.toIResource(uri);

		assertTrue(iResource instanceof IFile);
		assertFalse(iResource.exists());
		assertEquals("/myProject/myFile.ext", iResource.getFullPath().toString());
	}

	@Test
	public void fileNested() throws Exception {
		waitForWorkspaceChanges(() -> {
			project.getFolder("/folder").create(true, true, null);
			project.getFolder("/folder/deep/").create(true, true, null);
			IFile file = project.getFile("/folder/deep/myFile.ext");
			file.create(new NullInputStream(0), true, null);
		});

		URI uri = URI.createPlatformResourceURI("/myProject/folder/deep/myFile.ext", true);
		IResource iResource = UriUtils.toIResource(uri);

		assertTrue(iResource instanceof IFile);
		assertTrue(iResource.exists());
		assertEquals("/myProject/folder/deep/myFile.ext", iResource.getFullPath().toString());
	}

	@Test
	public void fileSlashesExcess() throws Exception {
		waitForWorkspaceChanges(() -> {
			project.getFolder("/folder").create(true, true, null);
			project.getFolder("/folder/deep/").create(true, true, null);
			IFile file = project.getFile("/folder/deep/myFile.ext");
			file.create(new NullInputStream(0), true, null);
		});

		URI uri = URI.createPlatformResourceURI("////myProject///folder///deep/myFile.ext//", true);
		IResource iResource = UriUtils.toIResource(uri);

		assertTrue(iResource instanceof IFile);
		assertTrue(iResource.exists());
		assertEquals("/myProject/folder/deep/myFile.ext", iResource.getFullPath().toString());
	}

	@Test
	public void fileSlash() throws Exception {
		waitForWorkspaceChanges(() -> {
			IFile file = project.getFile("myFile.ext");
			file.create(new NullInputStream(0), true, null);
		});

		URI uri = URI.createPlatformResourceURI("/myProject/myFile.ext/", true);
		IResource iResource = UriUtils.toIResource(uri);

		assertTrue(iResource instanceof IFile);
		assertTrue(iResource.exists());
		assertEquals("/myProject/myFile.ext", iResource.getFullPath().toString());
	}

	@Test
	public void fileDifferentCase() throws Exception {
		waitForWorkspaceChanges(() -> {
			IFile file = project.getFile("myFile.ext");
			file.create(new NullInputStream(0), true, null);
		});

		URI uri = URI.createPlatformResourceURI("/myProject/MYfILE.ext", true);
		IResource iResource = UriUtils.toIResource(uri);

		assertTrue(iResource instanceof IFile);
		assertFalse(iResource.exists());
		assertEquals("/myProject/MYfILE.ext", iResource.getFullPath().toString());
	}

	@Test
	public void fileWorkspaceLinkExists() throws Exception {
		waitForWorkspaceChanges(() -> {
			IFile file = project.getFile("myFile.ext");
			file.create(new NullInputStream(0), true, null);
			java.net.URI uri = project.getPathVariableManager()
					.resolveURI(new java.net.URI("WORKSPACE_LOC/myProject/myFile.ext"));
			project.getFile("myLink.otherext").createLink(uri, IResource.NONE, null);
		});

		URI uri = URI.createPlatformResourceURI("/myProject/myLink.otherext", true);
		IResource iResource = UriUtils.toIResource(uri);

		assertTrue(iResource instanceof IFile);
		assertTrue(iResource.exists());
		assertEquals("/myProject/myLink.otherext", iResource.getFullPath().toString());
	}

	@Test
	public void fileWorkspaceLinkMissing() throws Exception {
		waitForWorkspaceChanges(() -> {
			java.net.URI uri = project.getPathVariableManager()
					.resolveURI(new java.net.URI("WORKSPACE_LOC/myProject/myFile.ext"));
			project.getFile("myLink.otherext").createLink(uri, IResource.ALLOW_MISSING_LOCAL, null);
		});

		URI uri = URI.createPlatformResourceURI("/myProject/myLink.otherext", true);
		IResource iResource = UriUtils.toIResource(uri);

		assertTrue(iResource instanceof IFile);
		assertTrue(iResource.exists());
		assertEquals("/myProject/myLink.otherext", iResource.getFullPath().toString());
	}

	@Test
	public void fileExternalLinkExists() throws Exception {
		waitForWorkspaceChanges(() -> {
			File tempFile = File.createTempFile(this.getClass().getSimpleName(), "");
			tempFile.deleteOnExit();
			java.net.URI uri = tempFile.toURI();
			project.getFile("myLink.otherext").createLink(uri, IResource.NONE, null);
		});

		URI uri = URI.createPlatformResourceURI("/myProject/myLink.otherext", true);
		IResource iResource = UriUtils.toIResource(uri);

		assertTrue(iResource instanceof IFile);
		assertTrue(iResource.exists());
		assertEquals("/myProject/myLink.otherext", iResource.getFullPath().toString());
	}

	@Test
	public void fileExternalLinkMissing() throws Exception {
		waitForWorkspaceChanges(() -> {
			File tempFile = File.createTempFile(this.getClass().getSimpleName(), "");
			tempFile.delete();
			java.net.URI uri = tempFile.toURI();
			project.getFile("myLink.otherext").createLink(uri, IResource.ALLOW_MISSING_LOCAL, null);
		});

		URI uri = URI.createPlatformResourceURI("/myProject/myLink.otherext", true);
		IResource iResource = UriUtils.toIResource(uri);

		assertTrue(iResource instanceof IFile);
		assertTrue(iResource.exists());
		assertEquals("/myProject/myLink.otherext", iResource.getFullPath().toString());
	}

	@Test
	public void folderWorkspaceLinkExists() throws Exception {
		waitForWorkspaceChanges(() -> {
			IFolder folder = project.getFolder("myFolder");
			folder.create(true, true, null);
			java.net.URI uri = project.getPathVariableManager()
					.resolveURI(new java.net.URI("WORKSPACE_LOC/myProject/myFolder"));
			project.getFolder("myLink.otherext").createLink(uri, IResource.NONE, null);
		});

		URI uri = URI.createPlatformResourceURI("/myProject/myLink.otherext", true);
		IResource iResource = UriUtils.toIResource(uri);

		assertTrue(iResource instanceof IFolder);
		assertTrue(iResource.exists());
		assertEquals("/myProject/myLink.otherext", iResource.getFullPath().toString());
	}

	@Test
	public void folderWorkspaceLinkMissing() throws Exception {
		waitForWorkspaceChanges(() -> {
			java.net.URI uri = project.getPathVariableManager()
					.resolveURI(new java.net.URI("WORKSPACE_LOC/myProject/myFolder"));
			project.getFolder("myLink.otherext").createLink(uri, IResource.ALLOW_MISSING_LOCAL, null);
		});

		URI uri = URI.createPlatformResourceURI("/myProject/myLink.otherext", true);
		IResource iResource = UriUtils.toIResource(uri);

		assertTrue(iResource instanceof IFolder);
		assertTrue(iResource.exists());
		assertEquals("/myProject/myLink.otherext", iResource.getFullPath().toString());
	}

	@Test
	public void folderExternalLinkExists() throws Exception {
		waitForWorkspaceChanges(() -> {
			File tempDir = Files.createTempDirectory(this.getClass().getSimpleName()).toFile();
			tempDir.deleteOnExit();
			java.net.URI uri = tempDir.toURI();
			project.getFolder("myLink.otherext").createLink(uri, IResource.NONE, null);
		});

		URI uri = URI.createPlatformResourceURI("/myProject/myLink.otherext", true);
		IResource iResource = UriUtils.toIResource(uri);

		assertTrue(iResource instanceof IFolder);
		assertTrue(iResource.exists());
		assertEquals("/myProject/myLink.otherext", iResource.getFullPath().toString());
	}

	@Test
	public void folderExternalLinkMissing() throws Exception {
		waitForWorkspaceChanges(() -> {
			File tempDir = Files.createTempDirectory(this.getClass().getSimpleName()).toFile();
			tempDir.delete();
			java.net.URI uri = tempDir.toURI();
			project.getFolder("myLink.otherext").createLink(uri, IResource.ALLOW_MISSING_LOCAL, null);
		});

		URI uri = URI.createPlatformResourceURI("/myProject/myLink.otherext", true);
		IResource iResource = UriUtils.toIResource(uri);

		assertTrue(iResource instanceof IFolder);
		assertTrue(iResource.exists());
		assertEquals("/myProject/myLink.otherext", iResource.getFullPath().toString());
	}

	@Test
	public void folder() throws Exception {
		waitForWorkspaceChanges(() -> {
			IFolder folder = project.getFolder("myFolder");
			folder.create(true, true, null);
		});

		URI uri = URI.createPlatformResourceURI("/myProject/myFolder", true);
		IResource iResource = UriUtils.toIResource(uri);

		assertTrue(iResource instanceof IFolder);
		assertTrue(iResource.exists());
		assertEquals("/myProject/myFolder", iResource.getFullPath().toString());
	}

	@Test
	public void folderSlash() throws Exception {
		waitForWorkspaceChanges(() -> {
			IFolder folder = project.getFolder("myFolder");
			folder.create(true, true, null);
		});

		URI uri = URI.createPlatformResourceURI("/myProject/myFolder/", true);
		IResource iResource = UriUtils.toIResource(uri);

		assertTrue(iResource instanceof IFolder);
		assertTrue(iResource.exists());
		assertEquals("/myProject/myFolder", iResource.getFullPath().toString());
	}

	@Test
	public void folderSlashes() throws Exception {
		waitForWorkspaceChanges(() -> {
			IFolder folder = project.getFolder("myFolder");
			folder.create(true, true, null);
		});

		URI uri = URI.createPlatformResourceURI("/myProject/myFolder///", true);
		IResource iResource = UriUtils.toIResource(uri);

		assertTrue(iResource instanceof IFolder);
		assertTrue(iResource.exists());
		assertEquals("/myProject/myFolder", iResource.getFullPath().toString());
	}

	@Test
	public void folderSlashesInbetween() throws Exception {
		waitForWorkspaceChanges(() -> {
			IFolder folder = project.getFolder("myFolder");
			folder.create(true, true, null);
		});

		URI uri = URI.createPlatformResourceURI("/myProject///myFolder", true);
		IResource iResource = UriUtils.toIResource(uri);

		assertTrue(iResource instanceof IFolder);
		assertTrue(iResource.exists());
		assertEquals("/myProject/myFolder", iResource.getFullPath().toString());
	}

	@Test
	public void fragment() throws Exception {
		waitForWorkspaceChanges(() -> {
			IFile file = project.getFile("myFile.ext");
			file.create(new NullInputStream(0), true, null);
		});

		URI uri = URI.createPlatformResourceURI("/myProject/myFile.ext", true).appendFragment("fragment");
		IResource iResource = UriUtils.toIResource(uri);

		assertTrue(iResource instanceof IFile);
		assertTrue(iResource.exists());
		assertEquals("/myProject/myFile.ext", iResource.getFullPath().toString());
	}

	@Test
	public void query() throws Exception {
		waitForWorkspaceChanges(() -> {
			IFile file = project.getFile("myFile.ext");
			file.create(new NullInputStream(0), true, null);
		});

		URI uri = URI.createPlatformResourceURI("/myProject/myFile.ext", true).appendQuery("query");
		IResource iResource = UriUtils.toIResource(uri);

		assertTrue(iResource instanceof IFile);
		assertTrue(iResource.exists());
		assertEquals("/myProject/myFile.ext", iResource.getFullPath().toString());
	}

	@Test
	public void project() throws Exception {
		URI uri = URI.createPlatformResourceURI("/myProject", true);
		IResource iResource = UriUtils.toIResource(uri);

		assertTrue(iResource instanceof IProject);
		assertTrue(iResource.exists());
		assertEquals("/myProject", iResource.getFullPath().toString());
	}

	@Test
	public void projectSlash() throws Exception {
		URI uri = URI.createPlatformResourceURI("/myProject/", true);
		IResource iResource = UriUtils.toIResource(uri);

		assertTrue(iResource instanceof IProject);
		assertTrue(iResource.exists());
		assertEquals("/myProject", iResource.getFullPath().toString());
	}

	@Test
	public void projectSlashes() throws Exception {
		URI uri = URI.createPlatformResourceURI("/myProject///", true);
		IResource iResource = UriUtils.toIResource(uri);

		assertTrue(iResource instanceof IProject);
		assertTrue(iResource.exists());
		assertEquals("/myProject", iResource.getFullPath().toString());
	}

	@Test
	public void workspaceRoot() throws Exception {
		URI uri = URI.createPlatformResourceURI("", true);
		IResource iResource = UriUtils.toIResource(uri);

		assertTrue(iResource instanceof IWorkspaceRoot);
		assertTrue(iResource.exists());
		assertEquals("/", iResource.getFullPath().toString());
	}

	@Test
	public void workspaceRootSlash() throws Exception {
		URI uri = URI.createPlatformResourceURI("/", true);
		IResource iResource = UriUtils.toIResource(uri);

		assertTrue(iResource instanceof IWorkspaceRoot);
		assertTrue(iResource.exists());
		assertEquals("/", iResource.getFullPath().toString());
	}

	@Test
	public void workspaceRootSlashes() throws Exception {
		URI uri = URI.createPlatformResourceURI("///", true);
		IResource iResource = UriUtils.toIResource(uri);

		assertTrue(iResource instanceof IWorkspaceRoot);
		assertTrue(iResource.exists());
		assertEquals("/", iResource.getFullPath().toString());
	}

}
