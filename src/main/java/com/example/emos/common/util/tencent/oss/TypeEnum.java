package com.example.emos.common.util.tencent.oss;

/**
 * 业务类型枚举类
 */
public enum TypeEnum {

    ARCHIVE("archive");

    private final String key;

    TypeEnum(String key) {
        this.key = key;
    }

    private String getKey(){
        return key;
    }

    public static TypeEnum findByKey(String key) {
        if (key != null) {
            for (TypeEnum type : TypeEnum.values()) {
                if (key.equals(type.getKey())) {
                    return type;
                }
            }
        }
        return null;
    }
}
