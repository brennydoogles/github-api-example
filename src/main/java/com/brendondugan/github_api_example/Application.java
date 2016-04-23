package com.brendondugan.github_api_example;

import com.brendondugan.github_api_example.github.RepositoryValidityValidator;
import org.eclipse.egit.github.core.Repository;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

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
        List<String> branches = null;
        List<String> tags = null;
        List<String> branchesWithMissingFile = null;
        List<String> tagsWithMissingFile = null;
        try {
            properties = Application.loadProperties();
        }
        catch (IOException e){
            System.out.println(e.getMessage());
            System.exit(1);
        }
        validator = Application.getValidator(properties);
        String file_to_check = properties.getProperty("file_to_check");
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        Callable<List<String>> branchThread = ()-> validator.getBranches();
        Future<List<String>> branchFuture = executorService.submit(branchThread);
        Callable<List<String>> tagThread = ()-> validator.getBranches();
        Future<List<String>> tagFuture = executorService.submit(tagThread);

        Callable<List<String>> branchFileThread = ()-> validator.checkBranchesForMissingFile(file_to_check);
        Future<List<String>> branchFileFuture = executorService.submit(branchFileThread);

        Callable<List<String>> tagFileThread = ()-> validator.checkTagsForMissingFile(file_to_check);
        Future<List<String>> tagFileFuture = executorService.submit(tagFileThread);
        try {
            System.out.print("Waiting for data retrieval (This takes a while)");
            while(!branchFuture.isDone() || !tagFuture.isDone() || !branchFileFuture.isDone() || !tagFileFuture.isDone()){
                System.out.print(".");
                Thread.sleep(1000);
            }
            branches = branchFuture.get();
            tags = tagFuture.get();
            branchesWithMissingFile = branchFileFuture.get();
            tagsWithMissingFile = tagFileFuture.get();
            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println();
        System.out.println("Branches:");
        for(String branch : branches){
            System.out.println("\t" + branch);
        }
        System.out.println("Tags:");
        for (String tag : tags){
            System.out.println("\t" + tag);
        }
        System.out.println(String.format("Branches without %s:", file_to_check));
        for(String branch : branchesWithMissingFile){
            System.out.println("\t" + branch);
        }
        System.out.println(String.format("Tags without %s:", file_to_check));
        for(String tag : tagsWithMissingFile){
            System.out.println("\t" + tag);
        }
    }
}
