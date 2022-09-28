package com.corpize.sdk.ivoice.bean.response;

import java.io.Serializable;

/**
 * author : xpSun
 * date : 7/15/21
 * description :
 */
public class RenderingConfigBean implements Serializable {
    private int    stop_playing_mode;
    private int    rendering_type;
    private Object rendering_template;

    public int getStop_playing_mode () {
        return stop_playing_mode;
    }

    public void setStop_playing_mode (int stop_playing_mode) {
        this.stop_playing_mode = stop_playing_mode;
    }

    public int getRendering_type () {
        return rendering_type;
    }

    public void setRendering_type (int rendering_type) {
        this.rendering_type = rendering_type;
    }

    public Object getRendering_template () {
        return rendering_template;
    }

    public void setRendering_template (Object rendering_template) {
        this.rendering_template = rendering_template;
    }
}
