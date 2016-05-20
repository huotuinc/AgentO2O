/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.agent.service;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.springframework.stereotype.Service;

/**
 * @author CJ
 */
@Service
public class VFSHelper {

    private final FileSystemOptions options = new FileSystemOptions();
    private boolean passive;

    public VFSHelper() {
        super();
        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(options, false);
        SftpFileSystemConfigBuilder.getInstance().setTimeout(options, 30000);

        FtpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(options, false);
        FtpFileSystemConfigBuilder.getInstance().setDataTimeout(options, 30000);
        FtpFileSystemConfigBuilder.getInstance().setSoTimeout(options, 30000);
        FtpFileSystemConfigBuilder.getInstance().setPassiveMode(options, passive);
    }

    public void handle(String name, FileObjectConsumer consumer) throws FileSystemException {
        StandardFileSystemManager manager = new StandardFileSystemManager();
        manager.init();
        try {
            FileObject file = resolveFile(name, manager);
            try {
                consumer.accept(file);
            } catch (FileSystemException ex) {
                togglePassive();
                try {
                    consumer.accept(file);
                } catch (FileSystemException e) {
                    throw e;
                }
            }
        } finally {
            manager.close();
        }
    }

    public FileObject resolveFile(String name, FileSystemManager manager) throws FileSystemException {
        return manager.resolveFile(name, options);
    }

    public void togglePassive() {
        passive = !passive;
        FtpFileSystemConfigBuilder.getInstance().setPassiveMode(options, passive);
    }
}
