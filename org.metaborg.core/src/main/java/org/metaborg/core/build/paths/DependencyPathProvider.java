package org.metaborg.core.build.paths;

import java.util.Collection;

import javax.annotation.Nullable;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.metaborg.core.MetaborgRuntimeException;
import org.metaborg.core.build.dependency.IDependencyService;
import org.metaborg.core.language.ILanguage;
import org.metaborg.core.language.LanguagePathFacet;
import org.metaborg.core.project.IProject;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class DependencyPathProvider implements ILanguagePathProvider {
    private final IDependencyService dependencyService;


    @Inject public DependencyPathProvider(IDependencyService dependencyService) {
        this.dependencyService = dependencyService;
    }


    @Override public Iterable<FileObject> sourcePaths(IProject project, String language) {
        final Iterable<ILanguage> dependencies = dependencyService.compileDependencies(project);
        final Collection<FileObject> sources = Lists.newArrayList();
        for(ILanguage dependency : dependencies) {
            final LanguagePathFacet facet = dependency.facet(LanguagePathFacet.class);
            if(facet != null) {
                final Collection<String> paths = facet.sources.get(language);
                if(paths != null) {
                    resolve(project.location(), paths, sources);
                }
            }
        }
        return sources;
    }

    @Override public Iterable<FileObject> includePaths(IProject project, String language) {
        final Iterable<ILanguage> dependencies = dependencyService.runtimeDependencies(project);
        final Collection<FileObject> includes = Lists.newArrayList();
        for(ILanguage dependency : dependencies) {
            final LanguagePathFacet facet = dependency.facet(LanguagePathFacet.class);
            if(facet != null) {
                final Collection<String> paths = facet.includes.get(language);
                if(paths != null) {
                    resolve(dependency.location(), paths, includes);
                }
            }
        }
        return includes;
    }


    private void resolve(FileObject basedir, @Nullable Collection<String> paths, Collection<FileObject> filesToAppend) {
        if(paths != null) {
            for(String path : paths) {
                try {
                    filesToAppend.add(basedir.resolveFile(path));
                } catch(FileSystemException ex) {
                    throw new MetaborgRuntimeException(ex);
                }
            }
        }
    }
}
