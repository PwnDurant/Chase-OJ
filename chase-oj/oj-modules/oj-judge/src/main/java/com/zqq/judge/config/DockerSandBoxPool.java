package com.zqq.judge.config;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.zqq.common.core.constants.JudgeConstants;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Slf4j
public class DockerSandBoxPool {

    private final DockerClient dockerClient;

    private final String sandboxImage;

    private final String volumeDir;

    private final Long memoryLimit;

    private final Long memorySwapLimit;

    private final Long cpuLimit;

    private final int poolSize;

    private final String containerNamePrefix;

    private final BlockingQueue<String> containerQueue;

    private final Map<String, String> containerNameMap;

    public DockerSandBoxPool(DockerClient dockerClient,
                             String sandboxImage,
                             String volumeDir, Long memoryLimit,
                             Long memorySwapLimit, Long cpuLimit,
                             int poolSize, String containerNamePrefix) {
        this.dockerClient = dockerClient;
        this.sandboxImage = sandboxImage;
        this.volumeDir = volumeDir;
        this.memoryLimit = memoryLimit;
        this.memorySwapLimit = memorySwapLimit;
        this.cpuLimit = cpuLimit;
        this.poolSize = poolSize;
        this.containerQueue = new ArrayBlockingQueue<>(poolSize);
        this.containerNamePrefix = containerNamePrefix;
        this.containerNameMap = new HashMap<>();
    }

    /**
     * 初始化容器池
     */
    public void initDockerPool(){
        log.info("----- 创建容器开始 -----");
        for(int i=0;i<poolSize;i++){
            createContainer(containerNamePrefix+"-"+i);
        }
        log.info("------  创建容器结束  -----");
    }

    /**
     * 将一个容器Id放入容器队列中
     * @param containerId 容器Id
     */
    public void returnContainer(String containerId){
        containerQueue.add(containerId);
    }

    /**
     * 取出容器
     * @return 返回容器Id
     */
    public String getContainer(){
        try{
            return containerQueue.take();
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建一个容器
     * @param containerName 容器名称
     */
    private void createContainer(String containerName){
        List<Container> containerList=dockerClient.listContainersCmd().withShowAll(true).exec();
        if(!CollectionUtil.isEmpty(containerList)){
            String names= JudgeConstants.JAVA_CONTAINER_PREFIX+containerName;
            for(Container container:containerList){
//                docker本身允许一个容器被多个名字引用，所以直接与0号下标进行匹配就行
                String[] containerNames=container.getNames();
                if(containerNames!=null&&containerNames.length>0&&names.equals(containerNames[0])){
                    if("created".equals(container.getState())||"exited".equals(container.getState())){
//                        启动容器
                        dockerClient.startContainerCmd(container.getId()).exec();
                    }
                    containerQueue.add(container.getId());
                    containerNameMap.put(container.getId(),containerName);
                    return ;
                }
            }
        }

//        说明现在队列里面并没有空闲容器
//        拉取镜像
        pullJavaEnvImage();
//        创建容器，限制资源，控制权限（内存，CPU，挂载，网络）
        HostConfig hostConfig=getHostConfig(containerName);
        CreateContainerCmd containerCmd=dockerClient
                .createContainerCmd(JudgeConstants.JAVA_ENV_IMAGE)
                .withName(containerName);
        CreateContainerResponse createContainerResponse = containerCmd
                .withHostConfig(hostConfig)
                .withAttachStderr(true)
                .withAttachStdout(true)
                .withTty(true)
                .exec();
//      记录容器id
        String containerId = createContainerResponse.getId();
//      启动容器
        dockerClient.startContainerCmd(containerId).exec();
        containerQueue.add(containerId);
        containerNameMap.put(containerId, containerName);
    }

    /**
     * 拉取java容器镜像
     */
    private void pullJavaEnvImage() {
//        先获得镜像列表，判断所需镜像是否存在
        ListImagesCmd listImagesCmd=dockerClient.listImagesCmd();
        List<Image> imageList=listImagesCmd.exec();
        for(Image image:imageList){
            String[] repoTags=image.getRepoTags();
            if(repoTags!=null&&repoTags.length>0&&sandboxImage.equals(repoTags[0])){
                return;
            }
        }
//        不存在就进行拉取
        PullImageCmd pullImageCmd=dockerClient.pullImageCmd(sandboxImage);
        try{
            pullImageCmd.exec(new PullImageResultCallback()).awaitCompletion();
        }catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

//    资源配置
    private HostConfig getHostConfig(String containerName) {
        HostConfig hostConfig = new HostConfig();
//        设置挂载目录
        String userCodeDir=createContainerDir(containerName);
        hostConfig.setBinds(new Bind(userCodeDir,new Volume(volumeDir)));
//        限制docker容器使用资源
        hostConfig.withMemory(memoryLimit);
        hostConfig.withMemorySwap(memorySwapLimit);
        hostConfig.withCpuCount(cpuLimit);
        hostConfig.withNetworkMode("none"); //禁用网络
        hostConfig.withReadonlyRootfs(true);
        return hostConfig;
    }

//    为每一个容器，创建指定的挂载文件
    private String createContainerDir(String containerName) {
//        一级目录 存放所有容器的挂载目录
        String codeDir=System.getProperty("user.dir")+ File.separator+JudgeConstants.CODE_DIR_POOL;
        if(!FileUtil.exist(codeDir)){
            FileUtil.mkdir(codeDir);
        }
        return codeDir+File.separator+containerName;
    }

    public String getCodeDir(String containerId){
        String containerName=containerNameMap.get(containerId);
        log.info("containerName:{}",containerName);
        return System.getProperty("user.dir")+File.separator+JudgeConstants.CODE_DIR_POOL+File.separator+containerName;
    }


}
