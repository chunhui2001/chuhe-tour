package com.shenmao.chuhe.passport;

import com.shenmao.chuhe.Application;
import io.vertx.rxjava.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.auth.AuthProvider;
import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.rxjava.ext.auth.jdbc.JDBCAuth;
import io.vertx.rxjava.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.auth.shiro.ShiroAuthOptions;
import io.vertx.ext.auth.shiro.ShiroAuthRealmType;
import io.vertx.rxjava.ext.jdbc.JDBCClient;
import io.vertx.rxjava.ext.web.handler.*;
import scala.Int;

import java.util.Objects;

public class AuthHandlerImpl {

  private static AuthProvider _authProvider;
  private static JWTAuth _jwtAuthProvider;
  private static JWTAuthOptions jwtAuthOptions = new JWTAuthOptions()
    .setKeyStore(new KeyStoreOptions()
      .setPath("jceks/keystore.jceks")
//        .setPath("ssh_keys/server-keystore.jks")
      .setType("jceks")
      .setPassword("secret"));

  public static synchronized AuthHandler create(Vertx vertx) {

    ShiroAuthOptions shiroAuthOptions = new ShiroAuthOptions().setType(ShiroAuthRealmType.PROPERTIES).setConfig(new JsonObject().put("properties_path", "classpath:properties/app-users.properties"));
    AuthProvider shiroAuthProvider = ShiroAuthProviderImpl.create(vertx, shiroAuthOptions);

    // http://vertx.io/docs/vertx-auth-jdbc/java/
    // http://vertx.io/docs/vertx-jdbc-client/java/

    String dbHost = Application.getConfig().getString("mysql_host");
    Integer dbPort = Application.getConfig().getInteger("mysql_port");
    String dbName = Application.getConfig().getString("mysql_database_name");
    String dbUname = Application.getConfig().getString("mysql_uname");
    String dbPasswd = Application.getConfig().getString("mysql_password");

    Objects.requireNonNull(dbName);

    JDBCClient client = JDBCClient.createNonShared(vertx, new JsonObject()
            .put("url", "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName)
            .put("driver_class", "com.mysql.jdbc.Driver")
            .put("user", dbUname)
            .put("password", dbPasswd)
            .put("max_pool_size", 30));
    // JDBCClient client = JDBCClient.createShared(vertx.getDelegate(), mySQLClientConfig, dbName);
    JDBCAuth jdbcAuthProvider = JDBCAuth.create(vertx, client);
//    jdbcAuthProvider.setAuthenticationQuery("");
//    jdbcAuthProvider.setPermissionsQuery("");
//    jdbcAuthProvider.setRolesQuery("");
//    jdbcAuthProvider.setRolePrefix("");



    // ****



    JWTAuth jwtAuthProvider = JWTAuth.create(vertx, jwtAuthOptions);

    AuthHandler jwtAuthHandler = JWTAuthHandler.create(jwtAuthProvider);
     AuthHandler basicAuthHandler = BasicAuthHandler.create(shiroAuthProvider);
//    AuthHandler basicAuthHandler = BasicAuthHandler.create(jdbcAuthProvider);
     AuthHandler redirectAuthHandler = RedirectAuthHandler.create(shiroAuthProvider, "/login");
//    AuthHandler redirectAuthHandler = RedirectAuthHandler.create(jdbcAuthProvider, "/login");

    ChainAuthHandler chain = ChainAuthHandler.create();

    chain.append(jwtAuthHandler);
    chain.append(basicAuthHandler);

    chain.append(redirectAuthHandler);

     _authProvider = shiroAuthProvider;
//    _authProvider = jdbcAuthProvider;
    _jwtAuthProvider = jwtAuthProvider;

    // chain.addAuthority("role:admin");

    return chain;
    // return redirectAuthHandler;
    // return basicAuthHandler;

  }

  public static AuthProvider getAuthProvider() {
    return _authProvider;
  }

  public static JWTAuth getJWTAuthProvider() {
    return _jwtAuthProvider;
  }

}
