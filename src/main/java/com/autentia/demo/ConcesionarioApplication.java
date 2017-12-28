package com.autentia.demo;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.eclipse.jgit.api.Git;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@EnableAutoConfiguration
@SpringBootApplication
//@ComponentScan("com.autentia.demo")
public class ConcesionarioApplication {

	@RequestMapping( "/" )
	public String hello(){

	    System.out.print("sssss");
		return "Hello SpringBoot" ;
	}
	public static void main(String[] args) {
		SpringApplication. run(ConcesionarioApplication. class, args );
	}


    public static String localRepoPath = "/src/main/resources";
    public static String localRepoGitConfig = "D:/repo/.git";
    public static String remoteRepoURI = "git@gitlab.com:wilson/test.git";
    public static String localCodeDir = "D:/platplat";
    public static String newBranch(String branchName){
        String newBranchIndex = "refs/heads/"+branchName;
        String gitPathURI = "";
        Git git = null;
        try {

            //检查新建的分支是否已经存在，如果存在则将已存在的分支强制删除并新建一个分支
            List<Ref> refs = git.branchList().call();
            for (Ref ref : refs) {
                if (ref.getName().equals(newBranchIndex)) {
                    System.out.println("Removing branch before");
                    git.branchDelete().setBranchNames(branchName).setForce(true)
                            .call();
                    break;
                }
            }
            //新建分支
            Ref ref = git.branchCreate().setName(branchName).call();
            //推送到远程
            git.push().add(ref).call();
            gitPathURI = remoteRepoURI + " " + "feature/" + branchName;
        } catch (GitAPIException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return gitPathURI;
    }

    public static void commitFiles() throws IOException, GitAPIException{
        String filePath = "";
        Git git = Git.open( new File(localRepoGitConfig) );
        //创建用户文件的过程
        File myfile = new File(filePath);
        myfile.createNewFile();
        git.add().addFilepattern("pets").call();
        //提交
        git.commit().setMessage("Added pets").call();
        //推送到远程
        git.push().call();
    }

    public static boolean pullBranchToLocal(String cloneURL){
        boolean resultFlag = false;
        String[] splitURL = cloneURL.split(" ");
        String branchName = splitURL[1];
        String fileDir = localCodeDir+"/"+branchName;
        //检查目标文件夹是否存在
        File file = new File(fileDir);
        if(file.exists()){
            deleteFolder(file);
        }
        Git git;
        try {
            git = Git.open( new File(localRepoGitConfig) );
            git.cloneRepository().setURI(cloneURL).setDirectory(file).call();
            resultFlag = true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (GitAPIException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return resultFlag;
    }

    public static void deleteFolder(File file){
        if(file.isFile() || file.list().length==0){
            file.delete();
        }else{
            File[] files = file.listFiles();
            for(int i=0;i<files.length;i++){
                deleteFolder(files[i]);
                files[i].delete();
            }
        }
    }

    public static void setupRepo() throws GitAPIException{
        //建立与远程仓库的联系，仅需要执行一次
        Git git = Git.cloneRepository().setURI(remoteRepoURI).setDirectory(new File(localRepoPath)).call();
    }

}
