package com.xqw.common;

import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.Transferable;
import java.util.function.Consumer;

/**
 * 自定义监听
 * @author xqw
 */
public class MyClipListener implements FlavorListener {
    private Consumer<FlavorEvent> consumer;
    private Transferable syncContent;

    public MyClipListener(Consumer<FlavorEvent> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void flavorsChanged(FlavorEvent flavorEvent) {
        consumer.accept(flavorEvent);
    }

    public Transferable getSyncContent() {
        return syncContent;
    }

    public void setSyncContent(Transferable syncContent) {
        this.syncContent = syncContent;
    }
}
