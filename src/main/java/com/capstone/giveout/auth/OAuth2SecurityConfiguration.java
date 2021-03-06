package com.capstone.giveout.auth;

import com.capstone.giveout.Application;
import com.capstone.giveout.Routes;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import java.io.File;

/**
*	Configure this web application to use OAuth 2.0.
*
* 	The resource server is located at "/video", and can be accessed only by retrieving a token from "/oauth/token"
*  using the Password Grant Flow as specified by OAuth 2.0.
*
*  Most of this code can be reused in other applications. The key methods that would definitely need to
*  be changed are:
*
*  ResourceServer.configure(...) - update this method to apply the appropriate
*  set of scope requirements on client requests
*
*  OAuth2Config constructor - update this constructor to create a "real" (not hard-coded) UserDetailsService
*  and ClientDetailsService for authentication. The current implementation should never be used in any
*  type of production environment as these hard-coded credentials are highly insecure.
*
*  OAuth2SecurityConfiguration.containerCustomizer(...) - update this method to use a real keystore
*  and certificate signed by a CA. This current version is highly insecure.
*
*/
@Configuration
public class OAuth2SecurityConfiguration {

	// This first section of the configuration just makes sure that Spring Security picks
	// up the UserDetailsService that we create below.
	@Configuration
	@EnableWebSecurity
	protected static class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

		@Autowired
		private UserDetailsService userDetailsService;

		@Autowired
		protected void registerAuthentication(final AuthenticationManagerBuilder auth) throws Exception {
			auth.userDetailsService(userDetailsService);
		}

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring()
                .antMatchers(HttpMethod.GET,
                    "/gifts/*/image/*",
                    "/users/*/image/*",
                    Routes.GIFTS_PATH,
                    Routes.GIFTS_CHAIN_PATH,
                    Routes.TOP_GIVERS_PATH
                ).antMatchers(HttpMethod.POST,
                    Routes.USERS_PATH
            );
        }


    }

	/**
	 *	This method is used to configure who is allowed to access which parts of our
	 *	resource server
	 */
	@Configuration
	@EnableResourceServer
	protected static class ResourceServer extends ResourceServerConfigurerAdapter {

		// This method configures the OAuth scopes required by clients to access
		// all of the paths
		@Override
		public void configure(HttpSecurity http) throws Exception {

			http.csrf().disable();

			http.authorizeRequests()
				.antMatchers("/oauth/token").anonymous();

			// Require all GET requests to have client "read" scope
			http.authorizeRequests()
				.antMatchers(HttpMethod.GET, "/**")
				.access("#oauth2.hasScope('read')");

			// Require all other requests to have "write" scope
			http.authorizeRequests()
				.antMatchers("/**")
				.access("#oauth2.hasScope('write')");
		}

	}

	/**
	 * This class is used to configure how our authorization server (the "/oauth/token" endpoint)
	 * validates client credentials.
	 */
	@Configuration
	@EnableAuthorizationServer
	@Order(Ordered.LOWEST_PRECEDENCE - 100)
	protected static class OAuth2Config extends AuthorizationServerConfigurerAdapter {

		// Delegate the processing of Authentication requests to the framework
		@Autowired
		private AuthenticationManager authenticationManager;

		// A data structure used to store both a ClientDetailsService and a UserDetailsService
		private ClientAndUserDetailsService combinedService_;

        private UserDetailsManager userDetailsManager;

		/**
		 *
		 * This constructor is used to setup the clients and users that will be able to login to the
		 * system. This is a VERY insecure setup that is using hard-coded lists of clients / users /
		 * passwords and should never be used for anything other than local testing
		 * on a machine that is not accessible via the Internet. Even if you use
		 * this code for testing, at the bare minimum, you should consider changing the
		 * passwords listed below and updating the VideoSvcClientApiTest.
		 *
		 * @throws Exception
		 */
		public OAuth2Config() throws Exception {
			// Create a service that has the credentials for all our clients
			ClientDetailsService csvc = new InMemoryClientDetailsServiceBuilder()
					// Create a client that has "read" and "write" access to the
			        // gift service
					.withClient("mobile").authorizedGrantTypes("password", "refresh_token")
                                         .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
                                         .scopes("read","write")
                                         .resourceIds("gift")
					.and().build();


            JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager();
            jdbcUserDetailsManager.setDataSource(Application.dataSource());
            userDetailsManager = jdbcUserDetailsManager;

            // Remember to have this in the database:
            /*
            create table authorities (
                    username varchar(50) not null,
                    authority varchar(50) not null,
                    constraint fk_authorities_users foreign key(username) references users(username));
            create unique index ix_auth_username on authorities (username,authority);
            */
			combinedService_ = new ClientAndUserDetailsService(csvc, userDetailsManager);
		}

		/**
		 * Return the list of trusted client information to anyone who asks for it.
		 */
		@Bean
		public ClientDetailsService clientDetailsService() throws Exception {
			return combinedService_;
		}

		/**
		 * Return an instance of the user management to anyone in the framework who requests it.
         * Usefull to create users
		 */
		@Bean
		public UserDetailsManager userDetailsManager() {
			return userDetailsManager;
		}

		/**
		 * This method tells our AuthorizationServerConfigurerAdapter to use the delegated AuthenticationManager
		 * to process authentication requests.
		 */
		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			endpoints.authenticationManager(authenticationManager);
		}

		/**
		 * This method tells the AuthorizationServerConfigurerAdapter to use our self-defined client details service to
		 * authenticate clients with.
		 */
		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			clients.withClientDetails(clientDetailsService());
		}
    }


    // This version uses the Tomcat web container and configures it to
	// support HTTPS. The code below performs the configuration of Tomcat
	// for HTTPS. Each web container has a different API for configuring
	// HTTPS.
	//
	// The app now requires that you pass the location of the keystore and
	// the password for your private key that you would like to setup HTTPS
	// with. In Eclipse, you can set these options by going to:
	//    1. Run->Run Configurations
	//    2. Under Java Applications, select your run configuration for this app
	//    3. Open the Arguments tab
	//    4. In VM Arguments, provide the following information to use the
	//       default keystore provided with the sample code:
	//
	//       -Dkeystore.file=src/main/resources/private/keystore -Dkeystore.pass=changeit
	//
	//    5. Note, this keystore is highly insecure! If you want more securtiy, you
	//       should obtain a real SSL certificate:
	//
	//       http://tomcat.apache.org/tomcat-7.0-doc/ssl-howto.html
	//
    @Bean
    EmbeddedServletContainerCustomizer containerCustomizer(
            @Value("${keystore.file:src/main/resources/private/keystore}") String keystoreFile,
            @Value("${keystore.pass:changeit}") final String keystorePass) throws Exception {

		// If you were going to reuse this class in another
		// application, this is one of the key sections that you
		// would want to change

        final String absoluteKeystoreFile = new File(keystoreFile).getAbsolutePath();

        return new EmbeddedServletContainerCustomizer () {

			@Override
			public void customize(ConfigurableEmbeddedServletContainer container) {
		            TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory) container;
		            tomcat.addConnectorCustomizers(
		                    new TomcatConnectorCustomizer() {
								@Override
								public void customize(Connector connector) {
									connector.setPort(8443);
			                        connector.setSecure(true);
			                        connector.setScheme("https");

			                        Http11NioProtocol proto = (Http11NioProtocol) connector.getProtocolHandler();
			                        proto.setSSLEnabled(true);
			                        proto.setKeystoreFile(absoluteKeystoreFile);
			                        proto.setKeystorePass(keystorePass);
			                        proto.setKeystoreType("JKS");
			                        proto.setKeyAlias("tomcat");
								}
		                    });

			}
        };
    }


}
