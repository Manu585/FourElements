package com.github.manu585.fourelements.core.bender;

import com.github.manu585.fourelements.api.bender.Bender;
import com.github.manu585.fourelements.api.bending.Element;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Persistent bender aggregate: the data loaded from and saved back to the database.
 *
 * <p>Elements are held in a {@link LinkedHashSet} so membership is unique while the
 * order in which they were learned is preserved (useful for picking a default element).
 */
public class BenderPlayer implements Bender {

  private final UUID uuid;
  private final Set<Element> elements;

  public BenderPlayer(UUID uuid) {
    this(uuid, List.of());
  }

  public BenderPlayer(UUID uuid, Collection<Element> elements) {
    this.uuid = uuid;
    this.elements = elements == null ? new LinkedHashSet<>() : new LinkedHashSet<>(elements);
  }

  @Override
  public UUID uuid() {
    return this.uuid;
  }

  @Override
  public List<Element> elements() {
    return List.copyOf(elements);
  }

  /**
   * Teach this bender a new element.
   *
   * @param element Element to add
   * @return <code>true</code> if it was not already known
   */
  public boolean addElement(Element element) {
    return elements.add(element);
  }

  /**
   * Remove an element from this bender.
   *
   * @param element Element to remove
   * @return <code>true</code> if it was known and removed
   */
  public boolean removeElement(Element element) {
    return elements.remove(element);
  }

  /**
   * Check whether this bender possesses the given element.
   *
   * @param element Element to check
   * @return <code>true</code> if known
   */
  public boolean hasElement(Element element) {
    return elements.contains(element);
  }

}
