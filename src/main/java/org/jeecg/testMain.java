package org.jeecg;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import io.lettuce.core.ScriptOutputType;
import org.jeecg.common.util.IPUtils;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class testMain {



    public static void main(String[] args) {
        int i = DateUtil.dayOfWeek(DateUtil.offsetDay(new Date(),6));
        System.out.println(i);

        String aa = ResourceUtils.CLASSPATH_URL_PREFIX+"/static";
        System.out.println(aa);

    }
}
