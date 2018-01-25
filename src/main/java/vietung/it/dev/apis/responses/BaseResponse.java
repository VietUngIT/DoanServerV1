package vietung.it.dev.apis.responses;

import lombok.Data;
import vietung.it.dev.config.ErrorCode;

/**
 * Created by ThinhNK on 1/25/2018.
 */
@Data
public abstract class BaseResponse {
    public int error = ErrorCode.SUCCESS;
    public boolean isSuccess(){
        return error == ErrorCode.SUCCESS;
    }
    public abstract String toJonString();
}
