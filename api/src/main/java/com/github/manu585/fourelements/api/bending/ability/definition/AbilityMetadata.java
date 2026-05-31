package com.github.manu585.fourelements.api.bending.ability.definition;

import com.github.manu585.fourelements.api.bending.registry.Element;

public record AbilityMetadata(String name, String description, String author, Element element) {}