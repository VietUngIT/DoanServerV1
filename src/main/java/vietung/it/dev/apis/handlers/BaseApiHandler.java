package vietung.it.dev.apis.handlers;

import com.google.gson.JsonArray;
import io.vertx.core.http.HttpServerRequest;
import lombok.Data;
import vietung.it.dev.apis.responses.BaseResponse;
import vietung.it.dev.core.dao.Roles;

/**
 * Created by ThinhNK on 1/25/2018.
 */
@Data
public abstract class BaseApiHandler {
    protected String path = "";
    protected boolean isPublic = false;
    protected String method;
    protected int[] roles = {Roles.SUPER_ADMIN};

    public abstract BaseResponse handle(HttpServerRequest request) throws Exception;

    public  boolean filterSecutiry(String nickname, String accessToken){

        return true;
    }

    private boolean checkRole(int role){
        for(int i = 0; i < roles.length; i++){
            if(roles[i] == role){
                return true;
            }
        }
        return (roles.length == 0);
    }

    public void initRoles(JsonArray roleArray) {
        roles = new int[roleArray.size()];
        for(int i = 0; i < roles.length; i++){
            roles[i] = roleArray.get(i).getAsInt();
        }
    }
}
