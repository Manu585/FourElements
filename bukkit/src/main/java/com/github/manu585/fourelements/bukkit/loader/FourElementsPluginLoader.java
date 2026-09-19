package com.github.manu585.fourelements.bukkit.loader;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FourElementsPluginLoader implements PluginLoader {

  private static final String LIBRARIES_RESOURCE = "paper-libraries.txt";


  @Override
  public void classloader(PluginClasspathBuilder classpathBuilder) {
    MavenLibraryResolver resolver = new MavenLibraryResolver();

    resolver.addRepository(new RemoteRepository.Builder("central", "default", MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR).build());

    for (String coordinates : readLibraries()) {
      resolver.addDependency(new Dependency(new DefaultArtifact(coordinates), null));
    }

    classpathBuilder.addLibrary(resolver);
  }

  private static List<String> readLibraries() {
    try (InputStream in = FourElementsPluginLoader.class.getClassLoader()
        .getResourceAsStream(LIBRARIES_RESOURCE)) {
      if (in == null) {
        throw new IllegalStateException(LIBRARIES_RESOURCE + " fehlt in der Plugin-JAR");
      }
      return new String(in.readAllBytes(), StandardCharsets.UTF_8)
          .lines()
          .map(String::strip)
          .filter(line -> !line.isEmpty())
          .toList();
    } catch (IOException e) {
      throw new UncheckedIOException("Konnte " + LIBRARIES_RESOURCE + " nicht lesen", e);
    }
  }

}
