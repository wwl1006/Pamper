package com.work.pamper.utils;

public class ResultUtil {
    // 成功的静态方法
    // 泛型方法，返回类型为Result<T>
    // 重载了三个success方法，分别接受不同的参数
    // 2025-11-17 这里是一个通用的结果返回类，可以用于API的统一响应格式
    // 用到了很多泛型，也许我应该收藏一下这一坨代码，以后可以直接拿来用

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "成功", data);
    }

    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(200, msg, data);
    }

    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }

    public static <T> Result<T> error(Integer code, String msg, T data) {
        return new Result<>(code, msg, data);
    }

    // 静态内部类Result
    public static class Result<T> {
        private Integer code;
        private String msg;
        private T data;

        public Result() {}

        public Result(Integer code, String msg, T data) {
            this.code = code;
            this.msg = msg;
            this.data = data;
        }

        // Getter和Setter方法
        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "code=" + code +
                    ", msg='" + msg + '\'' +
                    ", data=" + data +
                    '}';
        }
    }
}
