package org.metaborg.spoofax.eclipse.test;

import org.eclipse.core.runtime.jobs.Job;
import org.junit.AfterClass;
import org.junit.Before;
import org.metaborg.spoofax.core.language.ILanguageDiscoveryService;
import org.metaborg.spoofax.core.language.ILanguageService;
import org.metaborg.spoofax.core.resource.IResourceService;
import org.metaborg.spoofax.core.terms.ITermFactoryService;
import org.metaborg.spoofax.eclipse.SpoofaxEclipseModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class SpoofaxEclipseTest {
    protected Injector injector;

    protected IResourceService resourceService;
    protected ITermFactoryService termFactoryService;
    protected ILanguageService languageService;
    protected ILanguageDiscoveryService languageDiscoveryService;

    @Before public void beforeTest() {
        injector = Guice.createInjector(new SpoofaxEclipseModule(SpoofaxEclipseTest.class.getClassLoader()));

        resourceService = injector.getInstance(IResourceService.class);
        termFactoryService = injector.getInstance(ITermFactoryService.class);
        languageService = injector.getInstance(ILanguageService.class);
        languageDiscoveryService = injector.getInstance(ILanguageDiscoveryService.class);
    }

    @AfterClass public static void afterClass() {
        try {
            Job.getJobManager().join(null, null);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}