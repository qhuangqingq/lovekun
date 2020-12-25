package com.example.lovekun.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author huangqing
 * @since 2020-11-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Teacher extends BasePlus<Teacher> implements Serializable {

    private String name;


}
