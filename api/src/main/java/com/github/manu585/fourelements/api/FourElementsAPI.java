package com.github.manu585.fourelements.api;

/**
 * Static accessor for the FourElements API.
 *
 * <p>The provider is set once by the plugin implementation during
 * startup and must not be replaced afterward.</p>
 */
public final class FourElementsAPI {

  private static FourElementsProvider provider;

  private FourElementsAPI() {
    throw new AssertionError("No instances.");
  }

  /**
   * Returns the current {@link FourElementsProvider}.
   *
   * @return the provider instance
   * @throws IllegalStateException if the API has not been initialized yet
   */
  public static FourElementsProvider get() {
    if (provider == null) {
      throw new IllegalStateException("FourElements API not initialized");
    }
    return provider;
  }

  /**
   * Sets the backing provider. May only be called once.
   *
   * @param provider the provider implementation
   * @throws IllegalStateException if a provider has already been registered
   */
  public static void setProvider(FourElementsProvider provider) {
    if (FourElementsAPI.provider != null) {
      throw new IllegalStateException("Provider already set");
    }
    FourElementsAPI.provider = provider;
  }

  /**
   * Checks whether the API is ready for use.
   *
   * @return {@code true} if a provider has been registered
   */
  public static boolean isAvailable() {
    return provider != null;
  }

}
