package com.xqw.common;

import com.xqw.utils.SysClipboardUtil;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.util.function.Consumer;

/**
 * @author hlkj
 */
public class SysClipboardMonitor implements ClipboardOwner {
    Consumer<Transferable> consumer;

    public SysClipboardMonitor(Consumer<Transferable> consumer) {
        this.consumer = consumer;
        SysClipboardUtil.getSysClip().setContents(SysClipboardUtil.getSysClip().getContents(null), this);
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        Transferable trans = clipboard.getContents(null);
        consumer.accept(trans);
        clipboard.setContents(trans, this);
    }
}
