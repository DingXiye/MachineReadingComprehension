package com.rengu.machinereadingcomprehension.Controller;

import com.rengu.machinereadingcomprehension.Entity.ResultEntity;
import com.rengu.machinereadingcomprehension.Service.FinalConfigService;
import com.rengu.machinereadingcomprehension.Service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(value = "/{userId}/commit-file")
    public ResultEntity commitFiles(@PathVariable(value = "userId") String userId, @RequestParam(value = "ref") MultipartFile ref, @RequestParam(value = "type") int type) throws Exception {
        switch (type) {
            case 1:
                return ResultService.resultBuilder(finalConfigService.commitFile_1(userId, ref));
            case 2:
                return ResultService.resultBuilder(finalConfigService.commitFile_2(userId, ref));
            case 3:
                return ResultService.resultBuilder(finalConfigService.commitFile_3(userId, ref));
            default:
                throw new RuntimeException("类型错误");
        }
    }

    @PatchMapping(value = "/order-user")
    public ResultEntity orderUser(@RequestParam(value = "type") int type) {
        return ResultService.resultBuilder(finalConfigService.orderUser(type));
    }
}
