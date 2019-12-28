package com.xqw.common;

import com.xqw.utils.SysClipboardUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.util.function.Consumer;

/**
 * @author hlkj
 */
public class SysClipboardMonitor implements ClipboardOwner {
    Consumer<Transferable> consumer;
    private static final Logger logger = LoggerFactory.getLogger(SysClipboardMonitor.class);

    public SysClipboardMonitor(Consumer<Transferable> consumer) {
        this.consumer = consumer;
        SysClipboardUtil.getSysClip().setContents(SysClipboardUtil.getSysClip().getContents(null), this);
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        for (int i = 1; i < 10; i++) {
            try {
                Thread.sleep(1);
                Transferable trans = clipboard.getContents(null);
                consumer.accept(trans);
                clipboard.setContents(trans, this);
                return;
            } catch (Exception e) {
                logger.error("lostOwnership error, try times:{}, cause：{}" , i, e.getMessage());
            }
        }
    }
}
