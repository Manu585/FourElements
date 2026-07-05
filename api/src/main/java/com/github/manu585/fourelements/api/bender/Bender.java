package com.github.manu585.fourelements.api.bender;

import com.github.manu585.fourelements.api.bending.Element;
import java.util.List;
import java.util.UUID;

public interface Bender {

  UUID uuid();

  List<Element> elements();

}
