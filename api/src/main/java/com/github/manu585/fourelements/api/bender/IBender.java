package com.github.manu585.fourelements.api.bender;

import com.github.manu585.fourelements.api.bending.element.Element;
import com.github.manu585.fourelements.api.bending.element.Elements;
import java.util.Set;
import java.util.UUID;

public interface IBender {

  UUID uuid();

  Set<Elements> elements();

}
