package org.metaborg.core;

import org.apache.commons.vfs2.FileSystemManager;
import org.metaborg.core.analysis.AnalysisService;
import org.metaborg.core.analysis.IAnalysisService;
import org.metaborg.core.build.Builder;
import org.metaborg.core.build.IBuilder;
import org.metaborg.core.build.dependency.DefaultDependencyService;
// import org.metaborg.core.build.dependency.DependencyService;
// import org.metaborg.core.build.dependency.IDependencyService;
import org.metaborg.core.build.dependency.IDependencyService;
import org.metaborg.core.build.paths.DependencyPathProvider;
import org.metaborg.core.build.paths.ILanguagePathProvider;
import org.metaborg.core.build.paths.ILanguagePathService;
import org.metaborg.core.build.paths.LanguagePathService;
import org.metaborg.core.config.AConfigurationReaderWriter;
import org.metaborg.core.config.ILanguageComponentConfigBuilder;
import org.metaborg.core.config.ILanguageComponentConfigService;
import org.metaborg.core.config.ILanguageComponentConfigWriter;
import org.metaborg.core.config.IProjectConfigBuilder;
import org.metaborg.core.config.IProjectConfigService;
import org.metaborg.core.config.LanguageComponentConfigBuilder;
import org.metaborg.core.config.LanguageComponentConfigService;
import org.metaborg.core.config.ProjectConfigBuilder;
import org.metaborg.core.config.ProjectConfigService;
import org.metaborg.core.config.YamlConfigurationReaderWriter;
import org.metaborg.core.context.ContextService;
import org.metaborg.core.context.IContextFactory;
import org.metaborg.core.context.IContextProcessor;
import org.metaborg.core.context.IContextService;
import org.metaborg.core.context.IContextStrategy;
import org.metaborg.core.context.ProjectContextStrategy;
import org.metaborg.core.context.ResourceContextStrategy;
import org.metaborg.core.editor.DummyEditorRegistry;
import org.metaborg.core.editor.IEditorRegistry;
import org.metaborg.core.language.ILanguageCache;
import org.metaborg.core.language.ILanguageIdentifierService;
import org.metaborg.core.language.ILanguageService;
import org.metaborg.core.language.LanguageIdentifierService;
import org.metaborg.core.language.LanguageService;
import org.metaborg.core.processing.BlockingProcessor;
import org.metaborg.core.processing.ILanguageChangeProcessor;
import org.metaborg.core.processing.IProcessor;
import org.metaborg.core.processing.IProcessorRunner;
import org.metaborg.core.processing.LanguageChangeProcessor;
import org.metaborg.core.processing.ProcessorRunner;
import org.metaborg.core.processing.analyze.AnalysisResultProcessor;
import org.metaborg.core.processing.analyze.IAnalysisResultProcessor;
import org.metaborg.core.processing.analyze.IAnalysisResultRequester;
import org.metaborg.core.processing.analyze.IAnalysisResultUpdater;
import org.metaborg.core.processing.parse.IParseResultProcessor;
import org.metaborg.core.processing.parse.IParseResultRequester;
import org.metaborg.core.processing.parse.IParseResultUpdater;
import org.metaborg.core.processing.parse.ParseResultProcessor;
import org.metaborg.core.project.DummyProjectService;
import org.metaborg.core.project.IProjectService;
import org.metaborg.core.resource.DefaultFileSystemManagerProvider;
import org.metaborg.core.resource.IResourceService;
import org.metaborg.core.resource.ResourceService;
import org.metaborg.core.source.ISourceTextService;
import org.metaborg.core.source.SourceTextService;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

/**
 * Guice module that specifies which implementations to use for services and factories.
 */
public class MetaborgModule extends AbstractModule {
    private final ClassLoader resourceClassLoader;

    protected Multibinder<ILanguageCache> languageCacheBinder;


    public MetaborgModule() {
        this(MetaborgModule.class.getClassLoader());
    }

    public MetaborgModule(ClassLoader resourceClassLoader) {
        this.resourceClassLoader = resourceClassLoader;
    }


    @Override protected void configure() {
        languageCacheBinder = Multibinder.newSetBinder(binder(), ILanguageCache.class);

        bindResource();
        bindLanguage();
        bindContext();
        bindContextFactories(MapBinder.newMapBinder(binder(), String.class, IContextFactory.class));
        bindContextStrategies(MapBinder.newMapBinder(binder(), String.class, IContextStrategy.class));
        bindProject();
        bindConfigMisc();
        bindProjectConfig();
        bindLanguageComponentConfig();
        bindLanguagePath();
        bindLanguagePathProviders(Multibinder.newSetBinder(binder(), ILanguagePathProvider.class));
        bindDependency();
        bindSourceText();
        bindAnalysis();
        bindBuilder();
        bindProcessor();
        bindProcessorRunner();
        bindLanguageChangeProcessing();
        bindEditor();

        bind(ClassLoader.class).annotatedWith(Names.named("ResourceClassLoader")).toInstance(resourceClassLoader);
    }

    protected void bindResource() {
        bind(IResourceService.class).to(ResourceService.class).in(Singleton.class);
        bind(FileSystemManager.class).toProvider(DefaultFileSystemManagerProvider.class).in(Singleton.class);
    }

    protected void bindLanguage() {
        bind(ILanguageService.class).to(LanguageService.class).in(Singleton.class);
        bind(ILanguageIdentifierService.class).to(LanguageIdentifierService.class).in(Singleton.class);
    }

    protected void bindProject() {
        bind(IProjectService.class).to(DummyProjectService.class).in(Singleton.class);
    }

    protected void bindContext() {
        bind(ContextService.class).in(Singleton.class);
        bind(IContextService.class).to(ContextService.class);
        bind(IContextProcessor.class).to(ContextService.class);
    }

    protected void bindContextFactories(@SuppressWarnings("unused") MapBinder<String, IContextFactory> binder) {
    }

    protected void bindContextStrategies(MapBinder<String, IContextStrategy> binder) {
        binder.addBinding(ResourceContextStrategy.name).to(ResourceContextStrategy.class).in(Singleton.class);
        binder.addBinding(ProjectContextStrategy.name).to(ProjectContextStrategy.class).in(Singleton.class);
    }

    protected void bindConfigMisc() {
        bind(AConfigurationReaderWriter.class).to(YamlConfigurationReaderWriter.class).in(Singleton.class);
    }

    protected void bindProjectConfig() {
        bind(ProjectConfigService.class).in(Singleton.class);
        bind(IProjectConfigService.class).to(ProjectConfigService.class);

        bind(ProjectConfigBuilder.class);
        bind(IProjectConfigBuilder.class).to(ProjectConfigBuilder.class);
    }

    protected void bindLanguageComponentConfig() {
        bind(LanguageComponentConfigService.class).in(Singleton.class);
        bind(ILanguageComponentConfigWriter.class).to(LanguageComponentConfigService.class);
        bind(ILanguageComponentConfigService.class).to(LanguageComponentConfigService.class);

        bind(LanguageComponentConfigBuilder.class);
        bind(ILanguageComponentConfigBuilder.class).to(LanguageComponentConfigBuilder.class);
    }

    protected void bindLanguagePath() {
        bind(ILanguagePathService.class).to(LanguagePathService.class).in(Singleton.class);
    }

    protected void bindLanguagePathProviders(Multibinder<ILanguagePathProvider> binder) {
        binder.addBinding().to(DependencyPathProvider.class);
    }

    protected void bindDependency() {
        bind(IDependencyService.class).to(DefaultDependencyService.class).in(Singleton.class);
    }

    protected void bindSourceText() {
        bind(ISourceTextService.class).to(SourceTextService.class).in(Singleton.class);
    }

    protected void bindAnalysis() {
        bind(IAnalysisService.class).to(AnalysisService.class).in(Singleton.class);
    }

    protected void bindBuilder() {
        bind(ParseResultProcessor.class).in(Singleton.class);
        bind(IParseResultRequester.class).to(ParseResultProcessor.class);
        bind(IParseResultUpdater.class).to(ParseResultProcessor.class);
        bind(IParseResultProcessor.class).to(ParseResultProcessor.class);

        bind(AnalysisResultProcessor.class).in(Singleton.class);
        bind(IAnalysisResultRequester.class).to(AnalysisResultProcessor.class);
        bind(IAnalysisResultUpdater.class).to(AnalysisResultProcessor.class);
        bind(IAnalysisResultProcessor.class).to(AnalysisResultProcessor.class);

        bind(IBuilder.class).to(Builder.class).in(Singleton.class);
    }

    protected void bindProcessorRunner() {
        bind(IProcessorRunner.class).to(ProcessorRunner.class).in(Singleton.class);
    }

    protected void bindProcessor() {
        bind(IProcessor.class).to(BlockingProcessor.class).in(Singleton.class);
    }

    protected void bindLanguageChangeProcessing() {
        bind(ILanguageChangeProcessor.class).to(LanguageChangeProcessor.class).in(Singleton.class);
    }

    protected void bindEditor() {
        bind(IEditorRegistry.class).to(DummyEditorRegistry.class).in(Singleton.class);
    }
}
