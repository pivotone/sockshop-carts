package works.weave.socks.cart.utils;


import works.weave.socks.cart.entities.Result;

public class ResultUtil {
    public static Result success(Object object){
        Result result = new Result();
        result.setCode("0000");
        result.setMessage("请求成功");
        result.setData(object);
        return result;
    }

    public static Result error(String code,String message){
        Result result = new Result();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static Result success(){
        Result result = new Result();
        result.setCode("0000");
        result.setMessage("请求成功");
        return result;
    }

    public static Result success(String message, Object object){
        Result result = new Result();
        result.setCode("0000");
        result.setMessage(message);
        result.setData(object);
        return result;
    }
}
