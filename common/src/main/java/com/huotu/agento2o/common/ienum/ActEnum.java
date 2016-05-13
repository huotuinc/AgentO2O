package com.huotu.agento2o.common.ienum;

/**
 * Created by allan on 5/4/16.
 */
public interface ActEnum {
    enum OrderPintuanStatusOption implements ICommonEnum {
        WAITING(0, "待开团"),
        GROUPING(1, "组团中"),
        GROUPED(2, "已成团"),
        FAILED(3, "成团失败");
        private Integer code;
        private String value;

        OrderPintuanStatusOption(Integer code, String value) {
            this.code = code;
            this.value = value;
        }

        @Override
        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        @Override
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
