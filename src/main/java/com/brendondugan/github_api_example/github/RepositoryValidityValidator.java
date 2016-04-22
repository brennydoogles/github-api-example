package com.brendondugan.github_api_example.github;

import com.google.common.base.Strings;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;

import javax.naming.AuthenticationException;
import java.io.IOException;

/**
 * Created by brendon on 4/22/2016.
 */
public class RepositoryValidityValidator {
    private String repositoryOwner;
    private String repositoryName;
    private String username;
    private String password;
    private String accessToken;
    private GithubAuthenticationType authenticationType;
    private GitHubClient gitHubClient = null;
    private RepositoryService repositoryService = null;

    public RepositoryValidityValidator(String repositoryOwner, String repositoryName, String accessToken) {
        this.repositoryOwner = repositoryOwner;
        this.repositoryName = repositoryName;
        this.accessToken = accessToken;
        this.authenticationType = GithubAuthenticationType.TOKEN;
    }

    public RepositoryValidityValidator(String repositoryOwner, String repositoryName, String username, String password) {
        this.repositoryOwner = repositoryOwner;
        this.repositoryName = repositoryName;
        this.username = username;
        this.password = password;
        this.authenticationType = GithubAuthenticationType.CREDENTIALS;
    }

    public Repository checkConnection() throws AuthenticationException, IOException {
        if(this.gitHubClient == null || this. repositoryService == null){
            if(
                    this.authenticationType == null ||
                    (
                            Strings.isNullOrEmpty(this.accessToken) &&
                            (
                                    Strings.isNullOrEmpty(this.username) ||
                                    Strings.isNullOrEmpty(this.password)
                            )
                    )
            ){
                throw new AuthenticationException("You must provide either an access token or username/password");
            }
            else{
                switch (this.authenticationType){
                    case TOKEN:
                        this.gitHubClient = new GitHubClient();
                        this.gitHubClient.setOAuth2Token(this.accessToken);
                        break;
                    case CREDENTIALS:
                        this.gitHubClient = new GitHubClient();
                        this.gitHubClient.setCredentials(this.username, this.password);
                }
                this.repositoryService = new RepositoryService(this.gitHubClient);
            }
        }
        Repository repository = this.repositoryService.getRepository(this.repositoryOwner, this.repositoryName);
        return repository;
    }
}
