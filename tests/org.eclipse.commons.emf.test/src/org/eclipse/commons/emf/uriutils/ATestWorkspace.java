package org.eclipse.commons.emf.uriutils;

import static org.eclipse.core.resources.IResourceChangeEvent.POST_BUILD;
import static org.eclipse.core.resources.IResourceChangeEvent.POST_CHANGE;
import static org.eclipse.core.resources.IResourceChangeEvent.PRE_BUILD;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.ISafeRunnable;
import org.junit.After;
import org.junit.Before;

/**
 * Base class for JUnit tests requiring an IProject.
 * 
 * @author Niko Stotz
 *
 * @since 0.1
 */
public abstract class ATestWorkspace {

	protected IProject project;

	@Before
	public void createProject() throws Exception {
		waitForWorkspaceChanges(() -> {
			project = ResourcesPlugin.getWorkspace().getRoot().getProject("myProject");
			project.create(null);
			project.open(null);
		});
	}

	@After
	public void destroyProject() throws Exception {
		if (project != null) {
			waitForWorkspaceChanges(() -> {
				project.delete(true, true, null);
			});
		}
	}

	protected void waitForWorkspaceChanges(ISafeRunnable work) throws Exception {
		AtomicBoolean finished = new AtomicBoolean(false);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(new IResourceChangeListener() {
			private boolean buildStarted = false;
			private boolean changed = false;

			private boolean isType(IResourceChangeEvent event, int typeFlag) {
				return (event.getType() & typeFlag) != 0;
			}

			@Override
			public void resourceChanged(IResourceChangeEvent event) {
				if (isType(event, PRE_BUILD)) {
					buildStarted = true;
				}
				if (isType(event, POST_BUILD)) {
					buildStarted = false;
				}
				if (isType(event, POST_CHANGE)) {
					changed = true;
				}

				if (!buildStarted && changed) {
					finished.set(true);
					ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
				}

			}
		}, IResourceChangeEvent.PRE_BUILD | IResourceChangeEvent.POST_CHANGE | IResourceChangeEvent.POST_BUILD);

		work.run();

		while (!finished.get()) {
			Thread.sleep(271);
		}
	}

}
