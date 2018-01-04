package be.zoomit.auth;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import javax.security.auth.message.callback.GroupPrincipalCallback;
import javax.security.auth.message.module.ServerAuthModule;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class ZoomitAuthModule implements ServerAuthModule {

    private CallbackHandler handler;

    @Override
    public void initialize(MessagePolicy requestPolicy, MessagePolicy responsePolicy, CallbackHandler handler, Map options) throws AuthException {
        this.handler = handler;
    }

    @Override
    public Class[] getSupportedMessageTypes() {
        return new Class[]{HttpServletRequest.class, HttpServletResponse.class};
    }

    @Override
    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject) throws AuthException {
        HttpServletRequest request = (HttpServletRequest) messageInfo.getRequestMessage();
        String user = request.getParameter("user");
        String groups[] = request.getParameterValues("group");

        if (user == null || groups == null) {
            return AuthStatus.FAILURE;
        }
        try {
            // callback used to set the user Principal
            Callback userCallback = new CallerPrincipalCallback(clientSubject, user);
            Callback groupsCallback = new GroupPrincipalCallback(clientSubject, groups);
            Callback[] callbackArray = {userCallback, groupsCallback};
            handler.handle(callbackArray);
        } catch (Exception exception) {
            String message = exception.getMessage();
            AuthException authException = new AuthException(message);
            authException.initCause(exception);
            throw authException;
        }
        return AuthStatus.SUCCESS;
    }

    @Override
    public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) throws AuthException {
        return AuthStatus.SEND_SUCCESS;
    }

    @Override
    public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {
        // do nothing
    }
}
