package com.brendondugan.github_api_example;

import com.brendondugan.github_api_example.github.RepositoryValidityValidator;
import org.eclipse.egit.github.core.Repository;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by brendon on 4/22/2016.
 */
public class Application {

    private static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        InputStream githubPropertiesStream = null;
        githubPropertiesStream = Application.class.getClassLoader().getResourceAsStream("github.properties");
        properties.load(githubPropertiesStream);
        return properties;
    }

    private static RepositoryValidityValidator getValidator(Properties properties){
        RepositoryValidityValidator validator = null;
        String repository_owner = properties.getProperty("repository_owner");
        String repository_name = properties.getProperty("repository_name");
        String auth_type = properties.getProperty("auth_type");
        if("token".equalsIgnoreCase(auth_type)){
            String access_token = properties.getProperty("access_token");
            validator = new RepositoryValidityValidator(repository_owner, repository_name, access_token);
        }
        else {
            String username = properties.getProperty("username");
            String password = properties.getProperty("password");
            validator = new RepositoryValidityValidator(repository_owner, repository_name, username, password);
        }
        return validator;
    }

    public static void main(String[] args) {
        Properties properties = null;
        RepositoryValidityValidator validator;
        try {
            properties = Application.loadProperties();
        }
        catch (IOException e){
            System.out.println(e.getMessage());
            System.exit(1);
        }
        validator = Application.getValidator(properties);
        try {
            for(String branch : validator.getBranches()){
                System.out.println(branch);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }

    }
}
