package vietung.it.dev.apis.responses;

/**
 * Created by ThinhNK on 1/25/2018.
 */
public class SimpleResponse extends BaseResponse {
    public SimpleResponse(){}
    public SimpleResponse(int error){
        setError(error);
    }
    @Override
    public String toJonString() {
        StringBuilder sb = new StringBuilder("{\"e\":");
        sb.append(getError()).append("}");
        return sb.toString();
    }
}
