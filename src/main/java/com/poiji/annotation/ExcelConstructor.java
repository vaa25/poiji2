package com.poiji.annotation;

import java.lang.annotation.*;

/**
 * Marks constructor what should be used by Poiji.
 *
 * Created by vaa25 on 16.03.24.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
@Documented
public @interface ExcelConstructor {

}
