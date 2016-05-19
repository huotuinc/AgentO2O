/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.agent.service.impl;

import com.huotu.agento2o.agent.service.StaticResourceService;
import com.huotu.agento2o.agent.service.VFSHelper;
import com.huotu.agento2o.common.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author CJ
 */
public abstract class AbstractStaticResourceService implements StaticResourceService {

    private static final Log log = LogFactory.getLog(AbstractStaticResourceService.class);

    protected URI uriPrefix;
    protected URI fileHome;
    @Autowired
    private VFSHelper vfsHelper;

    @Override
    public void deleteResource(String path) throws IOException {
        if (path == null)
            return;
        StringBuilder stringBuilder = new StringBuilder(fileHome.toString());
        if (!stringBuilder.toString().endsWith("/") && !path.startsWith("/"))
            stringBuilder.append("/");
        stringBuilder.append(path);

        vfsHelper.handle(stringBuilder.toString(), FileObject::delete);
    }

    @Override
    public URI uploadResource(String path, InputStream data) throws IOException, IllegalStateException, URISyntaxException {
        StringBuilder stringBuilder = new StringBuilder(fileHome.toString());
        if (!stringBuilder.toString().endsWith("/") && !path.startsWith("/"))
            stringBuilder.append("/");
        stringBuilder.append(path);

        vfsHelper.handle(stringBuilder.toString(), file -> {
            if (file.exists())
                throw new IllegalStateException("" + file.toString() + " already existing");
            OutputStream out = file.getContent().getOutputStream();
            try {
                StreamUtils.copy(data, out);
            } catch (IOException e) {
                throw new FileSystemException(e);
            } finally {
                try {
                    data.close();
                    out.close();
                } catch (IOException e) {
                    log.info("Exception on close stream." + e);
                }
            }
        });
        return getResource(path);
    }

    @Override
    public URI getResource(String path) throws URISyntaxException {
        StringBuilder stringBuilder = new StringBuilder(uriPrefix.toString());
        if (!stringBuilder.toString().endsWith("/") && !path.startsWith("/"))
            stringBuilder.append("/");
        stringBuilder.append(path);
        return new URI(stringBuilder.toString());
    }

    @Override
    public void setListUri(List targetList, String sourceColumnName, String targetColumnName) {
        if (targetList != null) {
            targetList.forEach(p -> {
                try {
                    if (p.getClass().getDeclaredField(targetColumnName).getType() != URI.class) {
                        return;
                    }
                    Field sourceColumn = p.getClass().getDeclaredField(sourceColumnName);
                    sourceColumn.setAccessible(true);
                    String resourceStr = String.valueOf(sourceColumn.get(p));
                    if (!StringUtil.isEmptyStr(resourceStr)) {
                        URI picUri = getResource(resourceStr);
                        Field targetColumn = p.getClass().getDeclaredField(targetColumnName);
                        targetColumn.setAccessible(true);
                        targetColumn.set(p, picUri);
                        targetColumn.setAccessible(false);
                    }
                    sourceColumn.setAccessible(false);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void deleteResource(URI uri) throws IOException {
        if (!uri.toString().startsWith(uriPrefix.toString())) {
            log.warn("can not resolve " + uri);
            return;
        }
        String path = uri.toString().substring(uriPrefix.toString().length());
        deleteResource(path);
    }
}
