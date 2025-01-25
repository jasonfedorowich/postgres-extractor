package com.extractor.postgres.objects.constants;

import lombok.Getter;

@Getter
public enum PGOptions {

    OUTPUT_PLUGIN("output_plugin");

    private final String optionName;

    PGOptions(String optionName) {
        this.optionName = optionName;
    }
}
