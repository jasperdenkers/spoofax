package org.metaborg.spoofax.core.project;

import javax.annotation.Nullable;

import org.apache.maven.project.MavenProject;
import org.metaborg.core.project.IProject;

/**
 * Maven project service that always returns null.
 */
@Deprecated
public class NullLegacyMavenProjectService implements ILegacyMavenProjectService {
    @Override public @Nullable MavenProject get(IProject project) {
        return null;
    }
}