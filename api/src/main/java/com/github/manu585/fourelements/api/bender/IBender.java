package com.github.manu585.fourelements.api.bender;

import com.github.manu585.fourelements.api.bending.element.Element;
import java.util.List;
import java.util.UUID;

public interface IBender {

  UUID uuid();

  List<Element> elements();

}
