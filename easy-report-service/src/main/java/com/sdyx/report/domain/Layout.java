package com.sdyx.report.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class Layout implements Serializable {
    /**
     * 栅格中元素的ID
     */
    private String i;
    /**
     * 标识栅格元素位于第几列，需为自然数
     */
    private int x;
    /**
     * 标识栅格元素位于第几行，需为自然数
     */
    private int y;
    /**
     * 标识栅格元素的初始宽度，值为colWidth的倍数
     */
    private int w;
    /**
     * 标识栅格元素的初始高度，值为rowHeight的倍数
     */
    private int h;
    /**
     * 栅格元素的最小宽度，值为colWidth的倍数。
     * 如果w小于minW，则minW的值会被w覆盖
     */
    private int minW;
    /**
     * 栅格元素的最小高度，值为rowHeight的倍数
     * 如果h小于minH，则minH的值会被h覆盖
     */
    private int minH;
    /**
     * 栅格元素的最大宽度，值为colWidth的倍数
     * 如果w大于maxW，则maxW的值会被w覆盖
     */
    private int maxW;
    /**
     * 栅格元素的最大高度，值为rowHeight的倍数
     * 如果h大于maxH，则maxH的值会被h覆盖
     */
    private int maxH;

    public Layout(String i, int x, int y, int w, int h, int minW) {
        this.i = i;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.minW = minW;
    }
}