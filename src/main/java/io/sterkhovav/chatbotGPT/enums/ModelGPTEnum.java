package io.sterkhovav.chatbotGPT.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static io.sterkhovav.chatbotGPT.utils.Constants.GPT_4_O_DESCRIPTION;
import static io.sterkhovav.chatbotGPT.utils.Constants.GPT_4_TURBO_DESCRIPTION;

@Getter
@AllArgsConstructor
public enum ModelGPTEnum {
    GPT_4_TURBO("GPT-4 Turbo", GPT_4_TURBO_DESCRIPTION),
    GPT_4_O("GPT-4o", GPT_4_O_DESCRIPTION),
    ;

    private final String name;
    private final String description;

    public static boolean existsByName(String name) {
        for (ModelGPTEnum model : ModelGPTEnum.values()) {
            if (model.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public static ModelGPTEnum getByName(String name) {
        for (ModelGPTEnum model : ModelGPTEnum.values()) {
            if (model.getName().equalsIgnoreCase(name)) {
                return model;
            }
        }
        return null;
    }
}
