package com.meteor.bukkitioc.io;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PropertyExpr {
    private String key;
    private String defaultValue;
}
