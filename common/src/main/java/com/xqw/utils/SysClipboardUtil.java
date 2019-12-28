package com.xqw.utils;

import com.xqw.common.MyClipListener;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SysClipboardUtil {
    public static final Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
    public static MyClipListener clipListener;

    /**
     * 从剪切板获得文字。
     * @param trans
     */
    public static String getText(Transferable trans) {
        try {
            return (String) trans.getTransferData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("获取文字异常");
    }

    /**
     * 将字符串复制到剪切板。
     * @return
     */
    public static Transferable setSysClipboardText(String writeMe) {
        Transferable tText = new StringSelection(writeMe);
        sysClip.setContents(tText, null);
        return tText;
    }

    /**
     * 从剪切板获得图片。
     */
    public static BufferedImage getImage(Transferable trans) {
        try {
            return (BufferedImage) trans.getTransferData(DataFlavor.imageFlavor);
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("获取图片异常");
    }

    /**
     * 复制图片到剪切板。
     * @return
     */
    public static Transferable setClipboardImage(final Image image) {
        Transferable trans = new Transferable() {
            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[]{DataFlavor.imageFlavor};
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return DataFlavor.imageFlavor.equals(flavor);
            }

            @Override
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                if (isDataFlavorSupported(flavor)) return image;
                throw new UnsupportedFlavorException(flavor);
            }

        };
        sysClip.setContents(trans, null);
        return trans;
    }
}
