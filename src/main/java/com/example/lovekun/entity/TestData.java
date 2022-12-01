package com.example.lovekun.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import java.util.Map;

@Data
public class TestData extends ModelAndView {

    private String da;

    @ApiModelProperty(hidden = true)
    @Override
    public Map<String, Object> getModel() {
        return super.getModel();
    }
    @ApiModelProperty(hidden = true)
    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    @ApiModelProperty(hidden = true)
    @Override
    public ModelMap getModelMap() {
        return super.getModelMap();
    }
    @ApiModelProperty(hidden = true)
    @Override
    public boolean isReference() {
        return super.isReference();
    }
    @ApiModelProperty(hidden = true)
    @Override
    public HttpStatus getStatus() {
        return super.getStatus();
    }
    @ApiModelProperty(hidden = true)
    @Override
    public View getView() {
        return super.getView();
    }
    @ApiModelProperty(hidden = true)
    @Override
    public String getViewName() {
        return super.getViewName();
    }


}
