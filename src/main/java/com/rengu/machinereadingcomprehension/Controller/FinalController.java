package com.rengu.machinereadingcomprehension.Controller;

import com.rengu.machinereadingcomprehension.Entity.ResultEntity;
import com.rengu.machinereadingcomprehension.Service.FinalConfigService;
import com.rengu.machinereadingcomprehension.Service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: machine-reading-comprehension
 * @author: hanch
 * @create: 2018-10-11 13:23
 **/

@RestController
@RequestMapping(value = "/finals")
public class FinalController {

    private final FinalConfigService finalConfigService;

    @Autowired
    public FinalController(FinalConfigService finalConfigService) {
        this.finalConfigService = finalConfigService;
    }

    // 开始比赛
    @PatchMapping(value = "/start-final")
    public ResultEntity startFinal(@RequestParam(value = "type") int type) {
        return ResultService.resultBuilder(finalConfigService.startFinal(type));
    }

    // 清除比赛开始标记
    @PatchMapping(value = "/reset-final")
    public ResultEntity resetFinal(@RequestParam(value = "type") int type) {
        return ResultService.resultBuilder(finalConfigService.resetFinal(type));
    }
}
