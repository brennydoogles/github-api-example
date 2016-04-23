package com.brendondugan.github_api_example.github;

import com.google.common.base.Strings;
import org.eclipse.egit.github.core.*;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.ContentsService;
import org.eclipse.egit.github.core.service.DataService;
import org.eclipse.egit.github.core.service.RepositoryService;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private ContentsService contentsService = null;
    private DataService dataService = null;

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

    public void setGitHubClient(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    public void setContentsService(ContentsService contentsService) {
        this.contentsService = contentsService;
    }

    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }

    private Repository getRepository() throws AuthenticationException, IOException {
        if(this.gitHubClient == null || this.repositoryService == null || this.contentsService == null || this.dataService == null){
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
                this.contentsService = new ContentsService(this.gitHubClient);
                this.dataService = new DataService(this.gitHubClient);
            }
        }
        Repository repository = this.repositoryService.getRepository(this.repositoryOwner, this.repositoryName);
        return repository;
    }

    public List<String> getBranches() throws IOException, AuthenticationException {
        List<String> branches = new ArrayList<String>();
        Repository repository = this.getRepository();
        List<RepositoryBranch> repositoryBranches = repositoryService.getBranches(repository);
        for (RepositoryBranch branch : repositoryBranches){
            branches.add(branch.getName());
        }
        return branches;
    }

    public List<String> getTags() throws IOException, AuthenticationException {
        List<String> tags = new ArrayList<String>();
        Repository repository = this.getRepository();
        List<RepositoryTag> repositoryTags = repositoryService.getTags(repository);
        for(RepositoryTag tag : repositoryTags){
            tags.add(tag.getName());
        }
        return tags;
    }

    public List<String> checkBranchesForMissingFile(String filename) throws IOException, AuthenticationException {
        List<String> branches = new ArrayList<String>();
        Repository repository = this.getRepository();
        List<RepositoryBranch> repositoryBranches = repositoryService.getBranches(repository);
        for(RepositoryBranch branch : repositoryBranches){
            boolean fileFound = false;
            Reference dataServiceReference = dataService.getReference(repository, "heads/" + branch.getName());
            Commit commit = dataService.getCommit(repository, dataServiceReference.getObject().getSha());
            Tree tree = dataService.getTree(repository, commit.getSha());
            for (TreeEntry entry : tree.getTree()){
                if(filename.equals(entry.getPath())){
                    fileFound = true;
                }
            }
            if(!fileFound){
                branches.add(branch.getName());
            }
        }
        return branches;
    }

    public List<String> checkTagsForMissingFile(String filename) throws IOException, AuthenticationException {
        List<String> tags = new ArrayList<String>();
        Repository repository = this.getRepository();
        List<RepositoryTag> repositoryTags = repositoryService.getTags(repository);
        for(RepositoryTag tag : repositoryTags){
            boolean fileFound = false;
            Reference dataServiceReference = dataService.getReference(repository, "tags/" + tag.getName());
            Commit commit = dataService.getCommit(repository, dataServiceReference.getObject().getSha());
            Tree tree = dataService.getTree(repository, commit.getSha());
            for (TreeEntry entry : tree.getTree()){
                if(filename.equals(entry.getPath())){
                    fileFound = true;
                }
            }
            if(!fileFound){
                tags.add(tag.getName());
            }
        }
        return tags;
    }
}
