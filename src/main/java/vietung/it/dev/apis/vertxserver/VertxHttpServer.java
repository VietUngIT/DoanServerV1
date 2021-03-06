package vietung.it.dev.apis.vertxserver;

import io.netty.util.AsciiString;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import vietung.it.dev.apis.handlers.BaseApiHandler;
import vietung.it.dev.apis.responses.BaseResponse;
import vietung.it.dev.apis.responses.SimpleResponse;
import vietung.it.dev.config.APIConfig;
import vietung.it.dev.config.ErrorCode;

/**
 * Created by ThinhNK on 1/25/2018.
 */
public class VertxHttpServer extends AbstractVerticle implements Handler<HttpServerRequest> {
    static Logger logger = LoggerFactory.getLogger(VertxHttpServer.class.getName());
    private HttpServer server;
    private static final CharSequence RESPONSE_TYPE_JSON = new AsciiString("application/json");

    @Override
    public void start() {
        int port = APIConfig.PORT;
        server = vertx.createHttpServer();
        server.requestHandler(VertxHttpServer.this).listen(port);
        logger.debug("start on port {}", port);
    }

    @Override
    public void handle(HttpServerRequest request) {
        BaseResponse response = null;
        BaseApiHandler handler = APIConfig.getHandler(request.path());
        try {
            if (handler != null) {
                switch (handler.getMethod()){
                    case "GET":
                        handleGet(handler, request);
                        break;
                    case "POST":
                        handlePOST(handler, request);
                        break;
                }
            } else {
                response = new SimpleResponse(ErrorCode.HANDLER_NOT_FOUND);
                makeHttpResponse(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = new SimpleResponse(ErrorCode.SYSTEM_ERROR);
            makeHttpResponse(request, response);
        }
    }
    private void handleGet(BaseApiHandler handler, HttpServerRequest request) throws Exception {
        BaseResponse response = null;
        if (handler.isPublic()) {
            response = handler.handle(request);
        } else {
            String nickname = request.getParam("un");
            String accessToken = request.getParam("phone");
            response = handlePrivateRequest(handler, request, nickname, accessToken);
        }
        makeHttpResponse(request, response);
    }
    private void handlePOST(BaseApiHandler handler, HttpServerRequest request) throws Exception {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        request.setExpectMultipart(true);
        request.endHandler(req->{
            BaseResponse response = null;
            try {
                if (handler.isPublic()) {
                    response = handler.handle(request);
                } else {
                    String nickname = request.formAttributes().get("un");
                    String accessToken = request.formAttributes().get("phone");
                    response = handlePrivateRequest(handler, request, nickname, accessToken);
                }
            }catch (Exception e){
                e.printStackTrace();
                response = new SimpleResponse(ErrorCode.SYSTEM_ERROR);
            }
            makeHttpResponse(request, response);
        });
    }
    private BaseResponse handlePrivateRequest(BaseApiHandler handler, HttpServerRequest request, String nickname, String accessToken) throws Exception {
        BaseResponse response = null;
        if (nickname != null && accessToken != null) {
            boolean securityCheck = handler.filterSecutiry(nickname, accessToken);
            if (securityCheck) {
                response = handler.handle(request);
            } else {
                response = new SimpleResponse(ErrorCode.NOT_AUTHORISED);
            }
        } else {
            response = new SimpleResponse(ErrorCode.INVALID_PARAMS);
        }
        return response;
    }
    public void allowAccessControlOrigin(HttpServerRequest request){
        request.response().putHeader("Access-Control-Allow-Origin", "*");
        request.response().putHeader("Access-Control-Allow-Credentials", "true");
        request.response().putHeader("Access-Control-Allow-Methods", "POST, GET, HEAD, OPTIONS");
        request.response().putHeader("Access-Control-Allow-Headers",
                "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
    }

    private void makeHttpResponse(HttpServerRequest request, BaseResponse response){
        allowAccessControlOrigin(request);
        String content = response.toJonString();
        CharSequence contentLength = new AsciiString(String.valueOf(content.length()));
        Buffer contentBuffer = Buffer.buffer(content);
        request.response().putHeader("CONTENT_TYPE", RESPONSE_TYPE_JSON)
                .putHeader("CONTENT_LENGTH", contentLength).end(contentBuffer);
    }

    @Override
    public void stop(){
        if ( server != null ) server.close();
    }
}
