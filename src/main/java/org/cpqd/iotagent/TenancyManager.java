package org.cpqd.iotagent;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.io.UnsupportedEncodingException;
import java.util.Date;

public class TenancyManager {

    public static String GetJwtToken(String service) {
        String token = "";
        Integer[] group = new Integer[1];
        group[0] = 1;

        // TODO(jsiloto) Substitute mocked values with reasonable ones
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            token = JWT.create()
                    .withClaim("userid", 1)
                    .withClaim("name", "Admin (superuser)")
                    .withArrayClaim("groups", group)
                    .withIssuedAt(new Date(1517339633))
                    .withExpiresAt(new Date(1517340053))
                    .withClaim("email", "admin@noemail.com")
                    .withClaim("profile", "admin")
                    .withIssuer("eGfIBvOLxz5aQxA92lFk5OExZmBMZDDh")
                    .withClaim("service", service)
                    .withJWTId("7e3086317df2c299cef280932da856e5")
                    .withClaim("username", "admin")
                    .sign(algorithm);
        } catch (UnsupportedEncodingException exception) {
            //UTF-8 encoding not supported
        }
        return token;
    }

}
